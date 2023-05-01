package com.zorona.liverooms.reels.record.filters;

import android.opengl.GLES20;

import androidx.annotation.NonNull;

import com.otaliastudios.cameraview.filter.BaseFilter;
import com.otaliastudios.cameraview.filter.TwoParameterFilter;
import com.otaliastudios.opengl.core.Egloo;

public class HazeFilter extends BaseFilter implements TwoParameterFilter {

    private static final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "varying vec2 " + DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME + ";\n" +
            "uniform samplerExternalOES sTexture;\n" +
            "uniform float distance;\n" +
            "uniform float slope;\n" +
            "void main()\n" +
            "{\n" +
            "    vec4 color = vec4(1.0);\n" +
            "    float d = vTextureCoord.y * slope  +  distance;\n" +
            "    vec4 c = texture2D(sTexture, " + DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME + ");\n" +
            "    c = (c - d * color) / (1.0 -d);\n" +
            "    gl_FragColor = c;\n" +
            "}";

    private float mDistance = 0.2f;
    private int mDistanceLocation = -1;
    private float mSlope = 0.0f;
    private int mSlopeLocation = -1;

    public float getDistance() {
        return getParameter1();
    }

    public void setDistance(final float distance) {
        setParameter1(distance);
    }

    @NonNull
    @Override
    public String getFragmentShader() {
        return FRAGMENT_SHADER;
    }

    public float getSlope() {
        return getParameter2();
    }

    public void setSlope(final float slope) {
        setParameter2(slope);
    }

    @Override
    public float getParameter1() {
        return mDistance;
    }

    @Override
    public void setParameter1(float value) {
        mDistance = value;
    }

    @Override
    public float getParameter2() {
        return mSlope;
    }

    @Override
    public void setParameter2(float value) {
        mSlope = value;
    }

    @Override
    public void onCreate(int programHandle) {
        super.onCreate(programHandle);
        mDistanceLocation = GLES20.glGetUniformLocation(programHandle, "distance");
        mSlopeLocation = GLES20.glGetUniformLocation(programHandle, "slope");
        Egloo.checkGlProgramLocation(mDistanceLocation, "distance");
        Egloo.checkGlProgramLocation(mSlopeLocation, "slope");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDistanceLocation = -1;
        mSlopeLocation = -1;
    }

    @Override
    protected void onPreDraw(long timestampUs, @NonNull float[] transformMatrix) {
        super.onPreDraw(timestampUs, transformMatrix);
        GLES20.glUniform1f(mDistanceLocation, mDistance);
        Egloo.checkGlError("glUniform1f");
        GLES20.glUniform1f(mSlopeLocation, mSlope);
        Egloo.checkGlError("glUniform1f");
    }
}
