package com.zorona.liverooms.reels.record.filters;

import android.opengl.GLES20;

import androidx.annotation.NonNull;

import com.otaliastudios.cameraview.filter.BaseFilter;
import com.otaliastudios.cameraview.filter.OneParameterFilter;
import com.otaliastudios.opengl.core.Egloo;

public class ExposureFilter extends BaseFilter implements OneParameterFilter {

    private static final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "varying vec2 " + DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME + ";\n" +
            "uniform samplerExternalOES sTexture;\n" +
            "uniform float exposure;\n" +
            "void main()\n" +
            "{\n" +
            "    vec4 textureColor = texture2D(sTexture, " + DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME + ");\n" +
            "    gl_FragColor = vec4(textureColor.rgb * pow(2.0, exposure), textureColor.w);\n" +
            "}";

    private float mExposure = 1f;
    private int mExposureLocation = -1;

    public float getExposure() {
        return getParameter1();
    }

    public void setExposure(float exposure) {
        setParameter1(exposure);
    }

    @NonNull
    @Override
    public String getFragmentShader() {
        return FRAGMENT_SHADER;
    }

    @Override
    public float getParameter1() {
        return mExposure;
    }

    @Override
    public void setParameter1(float value) {
        mExposure = value;
    }

    @Override
    public void onCreate(int programHandle) {
        super.onCreate(programHandle);
        mExposureLocation = GLES20.glGetUniformLocation(programHandle, "exposure");
        Egloo.checkGlProgramLocation(mExposureLocation, "exposure");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mExposureLocation = -1;
    }

    @Override
    protected void onPreDraw(long timestampUs, @NonNull float[] transformMatrix) {
        super.onPreDraw(timestampUs, transformMatrix);
        GLES20.glUniform1f(mExposureLocation, mExposure);
        Egloo.checkGlError("glUniform1f");
    }
}
