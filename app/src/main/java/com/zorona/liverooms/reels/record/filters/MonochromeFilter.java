package com.app.liverooms.reels.record.filters;

import android.opengl.GLES20;

import androidx.annotation.NonNull;

import com.otaliastudios.cameraview.filter.BaseFilter;
import com.otaliastudios.cameraview.filter.OneParameterFilter;
import com.otaliastudios.opengl.core.Egloo;

public class MonochromeFilter extends BaseFilter implements OneParameterFilter {

    private static final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\n" +
            "precision lowp float;\n" +
            "varying highp vec2 " + DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME + ";\n" +
            "uniform samplerExternalOES sTexture;\n" +
            "uniform float intensity;\n" +
            "const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +
            "void main()\n" +
            "{\n" +
            "    vec4 textureColor = texture2D(sTexture, " + DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME + ");\n" +
            "    float luminance = dot(textureColor.rgb, luminanceWeighting);\n" +
            "    vec4 desat = vec4(vec3(luminance), 1.0);\n" +
            "    vec4 outputColor = vec4(" +
            "        (desat.r < 0.5 ? (2.0 * desat.r * 0.6) : (1.0 - 2.0 * (1.0 - desat.r) * (1.0 - 0.6)))," +
            "        (desat.g < 0.5 ? (2.0 * desat.g * 0.45) : (1.0 - 2.0 * (1.0 - desat.g) * (1.0 - 0.45)))," +
            "        (desat.b < 0.5 ? (2.0 * desat.b * 0.3) : (1.0 - 2.0 * (1.0 - desat.b) * (1.0 - 0.3)))," +
            "        1.0" +
            "    );\n" +
            "    gl_FragColor = vec4(mix(textureColor.rgb, outputColor.rgb, intensity), textureColor.a);\n" +
            "}";

    private float mIntensity = 1.0f;
    private int mIntensityLocation = -1;

    @NonNull
    @Override
    public String getFragmentShader() {
        return FRAGMENT_SHADER;
    }

    public float getIntensity() {
        return getParameter1();
    }

    public void setIntensity(float intensity) {
        setParameter1(intensity);
    }

    @Override
    public float getParameter1() {
        return mIntensity;
    }

    @Override
    public void setParameter1(float value) {
        mIntensity = value;
    }

    @Override
    public void onCreate(int programHandle) {
        super.onCreate(programHandle);
        mIntensityLocation = GLES20.glGetUniformLocation(programHandle, "intensity");
        Egloo.checkGlProgramLocation(mIntensityLocation, "intensity");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIntensityLocation = -1;
    }

    @Override
    protected void onPreDraw(long timestampUs, @NonNull float[] transformMatrix) {
        super.onPreDraw(timestampUs, transformMatrix);
        GLES20.glUniform1f(mIntensityLocation, mIntensity);
        Egloo.checkGlError("glUniform1f");
    }
}
