package com.app.liverooms.reels.record.filters;

import android.opengl.GLES20;

import androidx.annotation.NonNull;

import com.otaliastudios.cameraview.filter.BaseFilter;
import com.otaliastudios.cameraview.filter.OneParameterFilter;
import com.otaliastudios.opengl.core.Egloo;

public class SolarizeFilter extends BaseFilter implements OneParameterFilter {

    private static final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "varying vec2 " + DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME + ";\n" +
            "uniform samplerExternalOES sTexture;\n" +
            "uniform float threshold;\n" +
            "const vec3 W = vec3(0.2125, 0.7154, 0.0721);\n" +
            "void main()\n" +
            "{\n" +
            "    vec4 textureColor = texture2D(sTexture, " + DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME + ");\n" +
            "    float luminance = dot(textureColor.rgb, W);\n" +
            "    float thresholdResult = step(luminance, threshold);\n" +
            "    vec3 finalColor = abs(thresholdResult - textureColor.rgb);\n" +
            "    gl_FragColor = vec4(finalColor, textureColor.w);\n" +
            "}";

    private float mThreshold = 0.5f;
    private int mThresholdLocation = -1;

    @NonNull
    @Override
    public String getFragmentShader() {
        return FRAGMENT_SHADER;
    }

    @Override
    public float getParameter1() {
        return mThreshold;
    }

    @Override
    public void setParameter1(float value) {
        mThreshold = value;
    }

    public float getThreshold() {
        return getParameter1();
    }

    public void setThreshold(float threshold) {
        setParameter1(threshold);
    }

    @Override
    public void onCreate(int programHandle) {
        super.onCreate(programHandle);
        mThresholdLocation = GLES20.glGetUniformLocation(programHandle, "threshold");
        Egloo.checkGlProgramLocation(mThresholdLocation, "threshold");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThresholdLocation = -1;
    }

    @Override
    protected void onPreDraw(long timestampUs, @NonNull float[] transformMatrix) {
        super.onPreDraw(timestampUs, transformMatrix);
        GLES20.glUniform1f(mThresholdLocation, mThreshold);
        Egloo.checkGlError("glUniform1f");
    }
}
