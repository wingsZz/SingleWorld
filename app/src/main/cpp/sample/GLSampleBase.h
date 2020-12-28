//
// Created by Zhouyueqi on 2020/11/24.
//

#ifndef SINGLEWORLD_GLSAMPLEBASE_H
#define SINGLEWORLD_GLSAMPLEBASE_H

#endif //SINGLEWORLD_GLSAMPLEBASE_H

#include <GLES3/gl3.h>
#include <ImageDef.h>

class GLSampleBase {
public:
    GLSampleBase() {
        mProgram = 0;
        mVerterxShader = 0;
        mFragmentShader = 0;
        surfaceHeight = 0;
        surfaceWidth = 0;
    }

    virtual ~GLSampleBase();

    virtual void LoadImage(NativeImage *pImage)
    {};

    virtual void LoadMultiImageWithIndex(int index, NativeImage *pImage)
    {};

    virtual void LoadShortArrData(short *const pShortArr, int arrSize)
    {}

    virtual void UpdateTransformMatrix(float rotateX, float rotateY, float scaleX, float scaleY)
    {}

    virtual void SetTouchLocation(float x, float y)
    {}

    virtual void SetGravityXY(float x, float y)
    {}

    virtual void Init() = 0;
    virtual void Draw(int screenW, int screenH) = 0;

    virtual void Destroy() = 0;

private:
    GLuint mProgram;
    GLuint mVerterxShader;
    GLuint mFragmentShader;

    int surfaceWidth;
    int surfaceHeight;
};