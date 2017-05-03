
package com.example.fix.SensorsAxisViewer;

import java.io.BufferedReader;

import android.content.Context;
import android.opengl.GLES20;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;


import java.io.InputStream;
import java.io.InputStreamReader;

//A small (future) utility class for Shaders.
public class  ShaderObj {
    int mShaderHandler;       //Handler sur un objet GLshader
    private int mShaderType;  //0=vertex shader; 1=fragment shader
    private String mShaderSource;  //raw shader text file

    public ShaderObj(Context ctx, int type, int resId) {
        mShaderType = type;
        mShaderSource = getFile(ctx, resId);
        Log.d("SOURCE", mShaderSource);
        mShaderHandler = GLES20.glCreateShader(mShaderType);
        compileShader(mShaderHandler, mShaderSource);
    }

    @NonNull
    private String getFile(Context ctx, int resId) {
        InputStream inputStream = ctx.getResources().openRawResource(resId);
        InputStreamReader inputReader = new InputStreamReader(inputStream);
        BufferedReader buffReader = new BufferedReader(inputReader);

        String line;
        StringBuilder text = new StringBuilder();

        try {
            while (( line = buffReader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        return text.toString();
    }

    @NonNull
    private boolean compileShader(int handler, String source) {
        if (handler != 0)
        {
            // Pass in the shader source.
            GLES20.glShaderSource(handler, source);

            // Compile the shader.
            GLES20.glCompileShader(handler);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(handler, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                GLES20.glDeleteShader(handler);
                handler = 0;
            }
        }
        if (handler == 0)
        {
            throw new RuntimeException("Error creating vertex shader.");
        }
        return true;
    }

    /*** getters ***/
    public String getShaderSource() {return mShaderSource;}//For future use ?
    public int getShaderHandler() { return mShaderHandler; }
    public int getShaderType() { return mShaderType; }//For future use ?
}
