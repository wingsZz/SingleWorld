//
// Created by Zhouyueqi on 2020/11/24.
//
#include <string.h>
#include "stdint.h"
#include "malloc.h"
#include "LogUtil.h"

#ifndef SINGLEWORLD_IMAGEDEF_H
#define SINGLEWORLD_IMAGEDEF_H

#endif //SINGLEWORLD_IMAGEDEF_H

#define IMAGE_FORMAT_RGBA 0x01
#define IMAGE_FORMAT_NV21 0x02
#define IMAGE_FORMAT_NV12 0x03
#define IMAGE_FORMAT_I420 0x04

#define IMAGE_FORMAT_RGBA_EXT "RGB32"
#define IMAGE_FORMAT_NV21_EXT "NV21"
#define IMAGE_FORMAT_NV12_EXT "NV12"
#define IMAGE_FORMAT_I420_EXT "I420"

// typedef struct 相当于别名
typedef struct _tag_NativeRectF {
    float left;
    float top;
    float right;
    float bottom;

    _tag_NativeRectF() {
        left = top = bottom = right = 0.0f;
    }
} RectF;

typedef struct _tag_NativeImage {
    int width;
    int height;
    int format;
    uint8_t *ppPlane[3];

    _tag_NativeImage() {
        width = 0;
        height = 0;
        format = 0;
        ppPlane[0] = nullptr;
        ppPlane[1] = nullptr;
        ppPlane[2] = nullptr;
    }
} NativeImage;

class NativeImageUtil {
public:
    static void AllocNativeImage(NativeImage *pImage) {
        if (pImage->height == 0 || pImage->width == 0) {
            return;
        }

        switch (pImage->format) {
            case IMAGE_FORMAT_RGBA:
                pImage->ppPlane[0] = static_cast<uint8_t *>(malloc(
                        pImage->width * pImage->height * 4));
                break;
            case IMAGE_FORMAT_NV12:
            case IMAGE_FORMAT_NV21:
                pImage->ppPlane[0] = static_cast<uint8_t *>(malloc(
                        pImage->width * pImage->height * 1.5));
                pImage->ppPlane[1] = pImage->ppPlane[0] + pImage->width * pImage->height;
                break;
            case IMAGE_FORMAT_I420:
                pImage->ppPlane[0] = static_cast<uint8_t *>(malloc(
                        pImage->width * pImage->height * 1.5));
                pImage->ppPlane[1] = pImage->ppPlane[0] + pImage->width * pImage->height;
                pImage->ppPlane[2] = pImage->ppPlane[1] + pImage->width * (pImage->height >> 2);
                break;
            default:

                break;
        }
    }

    static void FreeNativeImage(NativeImage *pImage) {
        if (pImage == nullptr || pImage->ppPlane[0] == nullptr) {
            return;
        }

        free(pImage->ppPlane[0]);
        pImage->ppPlane[0] = nullptr;
        pImage->ppPlane[1] = nullptr;
        pImage->ppPlane[2] = nullptr;
    }

    static void CopyNativeImage(NativeImage *pSrcImage, NativeImage *pDstImage) {
        if (pSrcImage == nullptr || pSrcImage->ppPlane[0] == nullptr) {
            return;
        }

        if (pSrcImage->width != pDstImage->width || pSrcImage->height != pDstImage->height ||
            pSrcImage->format != pDstImage->format) {
            return;
        }

        if (pDstImage->ppPlane[0] == nullptr) {
            AllocNativeImage(pSrcImage);
        }

        switch (pSrcImage->format) {
            case IMAGE_FORMAT_I420:
            case IMAGE_FORMAT_NV21:
            case IMAGE_FORMAT_NV12:
                memcpy(pDstImage->ppPlane[0], pSrcImage->ppPlane[0],
                       pSrcImage->width * pSrcImage->height * 1.5);
                break;
            case IMAGE_FORMAT_RGBA:
                memcpy(pDstImage->ppPlane[0], pSrcImage->ppPlane[0],
                       pSrcImage->width * pSrcImage->height * 4);
                break;
            default:
                LOGCATE("NativeImageUtil::CopyNativeImage do not support the format. Format = %d",
                        pSrcImage->format);
                break;
        }
    }
};




