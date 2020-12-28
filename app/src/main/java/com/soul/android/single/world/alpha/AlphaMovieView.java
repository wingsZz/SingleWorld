/*
 * Copyright 2017 Pavel Semak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.soul.android.single.world.alpha;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;

import java.io.FileDescriptor;
import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

//import android.media.MediaPlayer;

/**
 * 通过这个textureView进行mp4播放
 * from https://github.com/pavelsemak/alpha-movie
 */
@SuppressLint("ViewConstructor")
public class AlphaMovieView extends GLTextureView {

    private static final int GL_CONTEXT_VERSION = 2;

    private static final String TAG = "VideoSurfaceView";

    EPlayerRenderer renderer;
    private IjkMediaPlayer mediaPlayer;

    private OnVideoStartedListener onVideoStartedListener;
    private OnVideoEndedListener onVideoEndedListener;

    private boolean isSurfaceCreated;
    private boolean isDataSourceSet;

    private PlayerState state = PlayerState.NOT_PREPARED;

    public AlphaMovieView(Context context) {
        super(context);
        if (!isInEditMode()) {
            init();
        }
    }

    public AlphaMovieView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            init();
        }
    }

    private void init() {
        setEGLContextClientVersion(GL_CONTEXT_VERSION);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        initMediaPlayer();

        renderer = new EPlayerRenderer(this);

        setRenderer(renderer);

        bringToFront();
        setPreserveEGLContextOnPause(true);
        setOpaque(false);
    }

    private void initMediaPlayer() {
        mediaPlayer = new IjkMediaPlayer();

        //开启硬解码
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        //自动旋转
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
        //支持seekto
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
        setScreenOnWhilePlaying(true);

        mediaPlayer.setOnCompletionListener(mp -> {
            state = PlayerState.PAUSED;
            if (onVideoEndedListener != null) {
                onVideoEndedListener.onVideoEnded();
            }
        });
    }

    public void setSurface(Surface surface) {
        renderer.setGlFilter(new AlphaFrameFilter());
        isSurfaceCreated = true;
        try {
            mediaPlayer.setSurface(surface);
            surface.release();
            if (isDataSourceSet) {
                prepareAndStartMediaPlayer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareAndStartMediaPlayer() {
        prepareAsync(mp -> start());
    }

    private void calculateVideoAspectRatio(int videoWidth, int videoHeight) {
        requestLayout();
        invalidate();
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int size = upperRound16(ScreenUtils.getScreenWidth());
//        int height = size * 1920 / 1080;
//        setMeasuredDimension(size, height);
//    }

//    private int upperRound16(int num) {
//        if (num < 0) {
//            return 0;
//        }
//        int temp = num % 16;
//        if (temp == 0) {
//            return num;
//        }
//        return num + 16 - temp;
//    }

    private void onDataSourceSet(MediaMetadataRetriever retriever) {
        int videoWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int videoHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));

        calculateVideoAspectRatio(videoWidth, videoHeight);
        isDataSourceSet = true;

        if (isSurfaceCreated) {
            prepareAndStartMediaPlayer();
        }
    }
//
    public void setVideoFromAssets(String assetsFileName) {
        reset();

        try {
            AssetFileDescriptor assetFileDescriptor = getContext().getAssets().openFd(assetsFileName);
            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor());

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());

            onDataSourceSet(retriever);

        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
//
//    public void setVideoByUrl(String url) {
//        reset();
//        try {
//            mediaPlayer.setDataSource(url);
//            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//            RxUtils.runThread(c -> {
//                try {
//                    retriever.setDataSource(url, new HashMap<String, String>());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    if (onVideoEndedListener != null) {
//                        RxUtils.runOnUiThread(() -> onVideoEndedListener.onVideoEnded());
//                    }
//                    return;
//                }
//                RxUtils.runOnUiThread(() -> {
//                    onDataSourceSet(retriever);
//                });
//            });
//        } catch (IOException e) {
//            Log.e(TAG, e.getMessage(), e);
//        }
//
//
//    }

    public void setVideoByFile(String path) {
        reset();

        try {
            mediaPlayer.setDataSource(path);

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(path);
            onDataSourceSet(retriever);

        } catch (IOException | UnsatisfiedLinkError e) {
           e.printStackTrace();
        }
    }

    public void setVideoFromFile(FileDescriptor fileDescriptor) {
        reset();

        try {
            mediaPlayer.setDataSource(fileDescriptor);

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(fileDescriptor);

            onDataSourceSet(retriever);

        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

//    public void setVideoFromFile(FileDescriptor fileDescriptor, int startOffset, int endOffset) {
//        reset();
//
//        try {
//            mediaPlayer.setDataSource(fileDescriptor, startOffset, endOffset);
//
//            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//            retriever.setDataSource(fileDescriptor, startOffset, endOffset);
//
//            onDataSourceSet(retriever);
//
//        } catch (IOException e) {
//            Log.e(TAG, e.getMessage(), e);
//        }
//    }
//
//    @TargetApi(23)
//    public void setVideoFromMediaDataSource(MediaDataSource mediaDataSource) {
//        reset();
//
//        mediaPlayer.setDataSource(mediaDataSource);
//
//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource(mediaDataSource);
//
//        onDataSourceSet(retriever);
//    }

    public void setVideoFromUri(Context context, Uri uri) {
        reset();

        try {
            mediaPlayer.setDataSource(context, uri);

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, uri);

            onDataSourceSet(retriever);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        start();
    }

    @Override
    public void onPause() {
        super.onPause();
        pause();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        release();
        renderer.release();
    }

    private void prepareAsync(final IjkMediaPlayer.OnPreparedListener onPreparedListener) {
        if (mediaPlayer != null && state == PlayerState.NOT_PREPARED
                || state == PlayerState.STOPPED) {
            mediaPlayer.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                    Log.e(TAG,"onError code = " + i +"," + i1);
                    return false;
                }
            });
            mediaPlayer.setOnPreparedListener(mp -> {
                state = PlayerState.PREPARED;
                onPreparedListener.onPrepared(mp);
            });

            try {
                mediaPlayer.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        if (mediaPlayer != null) {
            switch (state) {
                case PREPARED:
                    mediaPlayer.start();
                    state = PlayerState.STARTED;
                    if (onVideoStartedListener != null) {
                        onVideoStartedListener.onVideoStarted();
                    }
                    break;
                case PAUSED:
                    mediaPlayer.start();
                    state = PlayerState.STARTED;
                    break;
                case STOPPED:
                    prepareAsync(mp -> {
                        mediaPlayer.start();
                        state = PlayerState.STARTED;
                        if (onVideoStartedListener != null) {
                            onVideoStartedListener.onVideoStarted();
                        }
                    });
                    break;
            }
        }
    }

    public void pause() {
        if (mediaPlayer != null && state == PlayerState.STARTED) {
            mediaPlayer.pause();
            state = PlayerState.PAUSED;
        }
    }

    public void stop() {
        if (mediaPlayer != null && (state == PlayerState.STARTED || state == PlayerState.PAUSED)) {
            mediaPlayer.stop();
            state = PlayerState.STOPPED;
        }
    }

    public void reset() {
        if (mediaPlayer != null && (state == PlayerState.STARTED || state == PlayerState.PAUSED ||
                state == PlayerState.STOPPED)) {
            mediaPlayer.reset();
            state = PlayerState.NOT_PREPARED;
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            state = PlayerState.RELEASE;
        }
    }

    public PlayerState getState() {
        return state;
    }

    public boolean isPlaying() {
        return state == PlayerState.STARTED;
    }

    public boolean isPaused() {
        return state == PlayerState.PAUSED;
    }

    public boolean isStopped() {
        return state == PlayerState.STOPPED;
    }

    public boolean isReleased() {
        return state == PlayerState.RELEASE;
    }

    public void seekTo(int msec) {
        mediaPlayer.seekTo(msec);
    }

    public void setLooping(boolean looping) {
        mediaPlayer.setLooping(looping);
    }

    public int getCurrentPosition() {
        return (int) mediaPlayer.getCurrentPosition();
    }

    public void setScreenOnWhilePlaying(boolean screenOn) {
        mediaPlayer.setScreenOnWhilePlaying(screenOn);
    }

    public void setOnErrorListener(IjkMediaPlayer.OnErrorListener onErrorListener) {
        mediaPlayer.setOnErrorListener(onErrorListener);
    }

    public void setOnVideoStartedListener(OnVideoStartedListener onVideoStartedListener) {
        this.onVideoStartedListener = onVideoStartedListener;
    }

    public void setOnVideoEndedListener(OnVideoEndedListener onVideoEndedListener) {
        this.onVideoEndedListener = onVideoEndedListener;
    }

    public void setOnSeekCompleteListener(IjkMediaPlayer.OnSeekCompleteListener onSeekCompleteListener) {
        mediaPlayer.setOnSeekCompleteListener(onSeekCompleteListener);
    }

    public IjkMediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public interface OnVideoStartedListener {
        void onVideoStarted();
    }

    public interface OnVideoEndedListener {
        void onVideoEnded();
    }

    private enum PlayerState {
        NOT_PREPARED, PREPARED, STARTED, PAUSED, STOPPED, RELEASE
    }
}