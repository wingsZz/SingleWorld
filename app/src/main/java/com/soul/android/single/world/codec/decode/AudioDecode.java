package com.soul.android.single.world.codec.decode;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.util.Log;

import com.soul.android.single.world.codec.MediaClock;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author : yueqi.zhou
 * @date : 2020-11-14 13:56
 * describe :
 */
class AudioDecode {
    private static final long TIMEOUT = 1000;

    private boolean isInit;
    private MediaExtractor extractor;
    private MediaCodec audioCodec;
    private AudioTrack audioTrack;

    private short[] mAudioOutTempBuf;
    private long mStartTimeForSync;

    private final MediaClock clock;

    public AudioDecode(MediaClock clock) {
        if (clock == null) {
            throw new IllegalArgumentException("MediaClock must not be null");
        }
        this.clock = clock;
    }

    public void start(String path) {
        if (!isInit) {
            init(path);
        }

        if (isInit) {
            audioCodec.start();
            audioTrack.play();
            decodeData();

        }
    }

    private void init(String path) {
        extractor = new MediaExtractor();
        try {
            extractor.setDataSource(path);
            for (int i = 0; i < extractor.getTrackCount(); i++) {
                MediaFormat format = extractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("audio")) {
                    extractor.selectTrack(i);
                    audioCodec = MediaCodec.createDecoderByType(mime);

                    initAudioTack(format);

                    audioCodec.configure(format, null, null, 0);
                }
            }
            isInit = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initAudioTack(MediaFormat format) {
        int streamType = AudioManager.STREAM_MUSIC;
        int sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        int channel;
        if (format.getInteger(MediaFormat.KEY_CHANNEL_COUNT) == 1) {
            channel = AudioFormat.CHANNEL_OUT_MONO;
        } else {
            channel = AudioFormat.CHANNEL_OUT_STEREO;
        }
        int audioFormat;
        if (format.containsKey(MediaFormat.KEY_PCM_ENCODING)) {
            audioFormat = format.getInteger(MediaFormat.KEY_PCM_ENCODING);
        } else {
            audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        }
        int bufferSize = AudioTrack.getMinBufferSize(sampleRate, channel, audioFormat);
        audioTrack = new AudioTrack(streamType, sampleRate, channel, audioFormat, bufferSize, AudioTrack.MODE_STREAM);
    }

    private void decodeData() {
        int inputChunk = 0;
        long firstInputTimeNesc = -1;

        boolean outputDone = false;
        boolean inputDone = false;
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

        while (!outputDone) {

            // 读取数据的过程
            // 将数据写入input buffer
            if (!inputDone) {
                int inputBufferIndex = audioCodec.dequeueInputBuffer(TIMEOUT);
                if (inputBufferIndex >= 0) {
                    // 有数据输入

                    if (firstInputTimeNesc == -1) {
                        // 获取拿到第一次input的时间
                        mStartTimeForSync = System.currentTimeMillis();
                        firstInputTimeNesc = System.nanoTime();
                    }

                    ByteBuffer buffer = audioCodec.getInputBuffer(inputBufferIndex);
                    int chunkSize = extractor.readSampleData(buffer, 0);
                    if (chunkSize < 0) {
                        // 获取不到数据块，意味着输入结束，告诉解码器完成解码
                        audioCodec.queueInputBuffer(inputBufferIndex, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        inputDone = true;
                    } else {
                        long presentationTimeUs = extractor.getSampleTime() / 1000;
                        clock.currentPts = presentationTimeUs * 2;
                        Log.e("MediaCodec", "Audio PTS = " + presentationTimeUs);
                        audioCodec.queueInputBuffer(inputBufferIndex, 0, chunkSize, presentationTimeUs * 2, 0);
                        inputChunk++;
                        extractor.advance();
                    }
                } else {
                    //TODO no input data available
                }
            }

            //
            if (!outputDone) {
                int status = audioCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT);
                if (status == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    //TODO no output data available
                } else if (status == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {

                } else if (status == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {

                } else if (status < 0) {

                } else {
                    boolean doLoop = false;
                    if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        doLoop = false;
                    }

                    synchronized (clock) {
//                        while (clock.currentPts < bufferInfo.presentationTimeUs) {
//                            try {
//                                clock.wait(bufferInfo.presentationTimeUs - clock.currentPts);
//                                clock.currentPts = bufferInfo.presentationTimeUs;
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
                        clock.notify();
                    }

                    playAudio(audioCodec.getOutputBuffer(status), bufferInfo);
                    boolean doRender = bufferInfo.size != 0;
                    audioCodec.releaseOutputBuffer(status, doRender);

                    if (doLoop) {
                        extractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
                        inputDone = false;
                        audioCodec.flush();
                    }
                }
            }
        }
    }

    private void playAudio(ByteBuffer outputBuffer, MediaCodec.BufferInfo bufferInfo) {
        if (mAudioOutTempBuf == null || mAudioOutTempBuf.length < bufferInfo.size / 2) {
            mAudioOutTempBuf = new short[bufferInfo.size / 2];
        }
        outputBuffer.position(0);
        outputBuffer.asShortBuffer().get(mAudioOutTempBuf, 0, bufferInfo.size / 2);
        audioTrack.write(mAudioOutTempBuf, 0, bufferInfo.size / 2);
    }

    private void sleepRender(MediaCodec.BufferInfo bufferInfo) {
        long passTime = System.currentTimeMillis() - mStartTimeForSync;
        long curTime = getCurTimeStamp(bufferInfo);
        if (curTime > passTime) {
            try {
                Log.e("MediaCodec", "Audio sleep time = " + (curTime - passTime));
                Thread.sleep(curTime - passTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private long getCurTimeStamp(MediaCodec.BufferInfo bufferInfo) {
        return bufferInfo.presentationTimeUs;
    }
}
