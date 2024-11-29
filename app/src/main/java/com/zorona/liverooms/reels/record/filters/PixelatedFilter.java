package com.app.liverooms.reels.record.filters;

import android.opengl.GLES20;

import androidx.annotation.NonNull;

import com.otaliastudios.cameraview.filter.BaseFilter;
import com.otaliastudios.cameraview.filter.TwoParameterFilter;
import com.otaliastudios.opengl.core.Egloo;

public class PixelatedFilter extends BaseFilter implements TwoParameterFilter {

    private static final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\n" +
            "precision highp float;\n" +
            "varying highp vec2 " + DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME + ";\n" +
            "uniform float imageSizeFactor;\n" +
            "uniform samplerExternalOES sTexture;\n" +
            "uniform float pixel;\n" +
            "void main()\n" +
            "{\n" +
            "    vec2 uv  = " + DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME + ".xy;\n" +
            "    float dx = pixel * imageSizeFactor;\n" +
            "    float dy = pixel * imageSizeFactor;\n" +
            "    vec2 coord = vec2(dx * floor(uv.x / dx), dy * floor(uv.y / dy));\n" +
            "    vec3 tc = texture2D(sTexture, coord).xyz;\n" +
            "    gl_FragColor = vec4(tc, 1.0);\n" +
            "}";

    private float mPixel = 1f;
    private int mPixelLocation = -1;
    private float mImageSizeFactor = 1f / 720;
    private int mImageSizeFactorLocation = -1;

    @NonNull
    @Override
    public String getFragmentShader() {
        return FRAGMENT_SHADER;
    }

    public float getImageSizeFactor() {
        return getParameter2();
    }

    public void setImageSizeFactor(float factor) {
        setParameter2(factor);
    }

    @Override
    public float getParameter1() {
        return mPixel;
    }

    @Override
    public void setParameter1(float value) {
        mPixel = value;
    }

    @Override
    public float getParameter2() {
        return mImageSizeFactor;
    }

    @Override
    public void setParameter2(float value) {
        mImageSizeFactor = value;
    }

    public float getPixel() {
        return getParameter1();
    }

    public void setPixel(float pixel) {
        setParameter1(pixel);
    }

    @Override
    public void onCreate(int programHandle) {
        super.onCreate(programHandle);
        mPixelLocation = GLES20.glGetUniformLocation(programHandle, "pixel");
        Egloo.checkGlProgramLocation(mPixelLocation, "pixel");
        mImageSizeFactorLocation = GLES20.glGetUniformLocation(programHandle, "imageSizeFactor");
        Egloo.checkGlProgramLocation(mImageSizeFactorLocation, "imageSizeFactor");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPixelLocation = -1;
        mImageSizeFactorLocation = -1;
    }

    @Override
    protected void onPreDraw(long timestampUs, @NonNull float[] transformMatrix) {
        super.onPreDraw(timestampUs, transformMatrix);
        GLES20.glUniform1f(mPixelLocation, mPixel);
        Egloo.checkGlError("glUniform1f");
        GLES20.glUniform1f(mImageSizeFactorLocation, mImageSizeFactor);
        Egloo.checkGlError("glUniform1f");
    }
}
