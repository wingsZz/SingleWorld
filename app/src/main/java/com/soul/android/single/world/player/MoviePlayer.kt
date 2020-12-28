package com.soul.android.single.world.player

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.view.Surface
import java.io.File
import java.lang.RuntimeException

/**
 * @author : yueqi.zhou
 * @date : 2020-10-11 19:36
 * describe :
 */
class MoviePlayer {
    private val bufferInfo = MediaCodec.BufferInfo()
    @Volatile
    private var isStopRequested = false

    private var sourceFile: File? = null
    private var outputSurface: Surface? = null
    private var frameCallback: FrameCallback? = null
    private var loop = false
    private var videoWidth = -1
        get() = field
    private var videoHeight = -1
        get() = field

    constructor(sourceFile: File, outputSurface: Surface, frameCallback: FrameCallback) {
        this.sourceFile = sourceFile
        this.outputSurface = outputSurface
        this.frameCallback = frameCallback

        var extractor: MediaExtractor? = null
        try {
            // 获取媒体资源信息
            extractor = MediaExtractor()
            extractor.setDataSource(sourceFile.toString())
            val selectTrack = selectTrack(extractor)
            if (selectTrack < 0) {
                throw RuntimeException("No video track found in $sourceFile")
            }
            extractor.selectTrack(selectTrack)

            val format = extractor.getTrackFormat(selectTrack)
            videoWidth = format.getInteger(MediaFormat.KEY_WIDTH)
            videoHeight = format.getInteger(MediaFormat.KEY_HEIGHT)
        } finally {
            extractor?.release()
        }
    }

    public fun requestStop() {
        isStopRequested = true
    }

    public fun play() {
        var extractor: MediaExtractor? = null
        var decoder: MediaCodec? = null

        if (sourceFile?.canRead() == false) {
            throw RuntimeException("Unable to read file $sourceFile")
        }

        try {
            extractor = MediaExtractor()
            extractor.setDataSource(sourceFile?.toString() ?: "")
            val trackIndex = selectTrack(extractor)
            if (trackIndex < 0) {
                throw RuntimeException("No video track found in $sourceFile")
            }

            extractor.selectTrack(trackIndex)

            // 视频解码
            val format = extractor.getTrackFormat(trackIndex)
            decoder = MediaCodec.createDecoderByType(format.getString(MediaFormat.KEY_MIME))
            decoder.configure(format, outputSurface, null, 0)
            decoder.start()

            doExtrack(extractor, trackIndex, decoder, frameCallback)
        } finally {
            if (decoder != null) {
                decoder.stop()
                decoder.release()
                decoder = null
            }

            if (extractor != null) {
                extractor.release()
                extractor = null
            }
        }
    }

    private fun selectTrack(extractor: MediaExtractor): Int {
        // 获取轨道数
        val tracks = extractor.trackCount
        for (i in 0..tracks) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime?.startsWith("video/") == true) {
                return i
            }
        }

        return -1
    }

    private fun doExtrack(
        extractor: MediaExtractor,
        trackIndex: Int,
        decoder: MediaCodec,
        frameCallback: FrameCallback?
    ) {

    }

    companion object {
        val TAG = MoviePlayer::class.java.simpleName
    }


    interface FrameCallback {
        fun preRender(presentationTimeUses: Long)

        fun postRender()

        fun loopReset()
    }
}