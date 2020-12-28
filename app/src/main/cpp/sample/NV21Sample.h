//
// Created by Zhouyueqi on 2020/11/24.
//

#ifndef SINGLEWORLD_NV21SAMPLE_H
#define SINGLEWORLD_NV21SAMPLE_H

#include "GLSampleBase.h"

class NV21Sample : public GLSampleBase {
public:
    NV21Sample() {
        mYTextureId = GL_NONE;
        mUVTextureId = GL_NONE;
        mYSamplerLoc = GL_NONE;
        mUVSamplerLoc = GL_NONE;
    }

    virtual ~NV21Sample() {
        NativeImageUtil::FreeNativeImage(&renderImage);
    }

    virtual void LoadImage(NativeImage *pImage);

    virtual void Init();

    virtual void Draw(int screenW, int screenH);

    virtual void Destroy();


private:
    GLuint mYTextureId;
    GLuint mUVTextureId;
    GLuint mYSamplerLoc;
    GLuint mUVSamplerLoc;

    NativeImage renderImage;
};


#endif //SINGLEWORLD_NV21SAMPLE_H
