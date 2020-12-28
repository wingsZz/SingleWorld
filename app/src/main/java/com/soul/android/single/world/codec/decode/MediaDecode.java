package com.soul.android.single.world.codec.decode;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;

import com.soul.android.single.world.codec.MediaClock;

import java.nio.ByteBuffer;

/**
 * @author : yueqi.zhou
 * @date : 2020-11-09 17:05
 * describe :
 */
public class MediaDecode {
    // 定义读取数据的超时时间
    private static final long TIMEOUT = 1000;

    private MediaCodec mediaCodec;
    /**
     * 获取音视频关键信息
     */
    private MediaExtractor extractor;

    private final MediaClock clock;

    private boolean isInit;

    private int trackIndex;
    private long mStartTimeForSync;

    public MediaDecode(MediaClock clock) {
        if (clock == null) {
            throw new IllegalArgumentException("MediaClock must not be null");
        }
        this.clock = clock;
    }

    public void start(String path, Surface surface) {
        if (!isInit) {
            init(path, surface);
        }
    }

    private void init(String path, Surface surface) {
        try {
            /**
             * 初始化信息提取器
             */
            extractor = new MediaExtractor();
            extractor.setDataSource(path);

            /**
             * 获取视频中的轨道数量，其中可能包含有：
             * 1.视频轨道
             * 2.音频轨道
             * 3.字幕轨道
             */
            final int trackCount = extractor.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                final MediaFormat format = extractor.getTrackFormat(i);
                final String mime = format.getString(MediaFormat.KEY_MIME);
                if (TextUtils.isEmpty(mime)) {
                    continue;
                }

                if (mime.startsWith("video")) {
                    trackIndex = i;
                    extractor.selectTrack(i);
                    mediaCodec = MediaCodec.createDecoderByType(mime);
                    mediaCodec.configure(format, surface, null, 0);
                }
            }

            if (mediaCodec != null) {
                mediaCodec.start();
                decodeData();
            }

            isInit = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                int inputBufferIndex = mediaCodec.dequeueInputBuffer(TIMEOUT);
                if (inputBufferIndex >= 0) {
                    // 有数据输入

                    if (firstInputTimeNesc == -1) {
                        // 获取拿到第一次input的时间
                        mStartTimeForSync = System.currentTimeMillis();
                        firstInputTimeNesc = System.nanoTime() / 1000;
                    }

                    ByteBuffer buffer = mediaCodec.getInputBuffer(inputBufferIndex);
                    int chunkSize = extractor.readSampleData(buffer, 0);
                    if (chunkSize < 0) {
                        // 获取不到数据块，意味着输入结束，告诉解码器完成解码
                        mediaCodec.queueInputBuffer(inputBufferIndex, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        inputDone = true;
                    } else {
                        if (extractor.getSampleTrackIndex() != trackIndex) {

                        }
                        long presentationTimeUs = extractor.getSampleTime() / 1000;
                        Log.e("MediaCodec", "Video PTS = " + presentationTimeUs);
                        mediaCodec.queueInputBuffer(inputBufferIndex, 0, chunkSize, presentationTimeUs*2, 0);
                        inputChunk++;
                        extractor.advance();
                    }
                } else {
                    //TODO no input data available
                }
            }

            //
            if (!outputDone) {
                int status = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT);
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
                        while (clock.currentPts < bufferInfo.presentationTimeUs) {
                            try {
                                clock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    boolean doRender = bufferInfo.size != 0;
                    mediaCodec.releaseOutputBuffer(status, doRender);


                    if (doLoop) {
                        extractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
                        inputDone = false;
                        mediaCodec.flush();
                    }
                }
            }
        }
    }


    private void sleepRender(MediaCodec.BufferInfo bufferInfo) {
        long passTime = System.currentTimeMillis() - mStartTimeForSync;
        Log.e("MediaCodec", "pass time = " + passTime);
        long curTime = getCurTimeStamp(bufferInfo);
        Log.e("MediaCodec", "currentTime = " + curTime);
        if (curTime > passTime) {
            try {
                Log.e("MediaCodec", "Video sleep time = " + (curTime - passTime));
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
