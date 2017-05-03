package com.example.fix.SensorsAxisViewer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class MyGLRenderer implements GLSurfaceView.Renderer, SensorEventListener
{
    //Tag for Debugger message
    private static final String TAG = "MiniShootRenderer";

    /** 4x4 Matrices **/
    private float [] mModelMatrix = new float[16];
    private float [] mViewMatrix = new float[16];
    private float [] mProjectionMatrix = new float [16];

    private float [] mRotXMatrix = new float[16];
    private float [] mRotYMatrix = new float[16];
    private float [] mRotZMatrix = new float[16];

    private float [] mTranslateMatrix = new float[16];
    private float [] mTempMatrix = new float[16];

    private float [] mMVPMatrix = new float[16];

    /** Sensor related matrices **/
    private final float[] mSensorRotationMatrix = new float[16];
    float[] orientation = new float[3];

    /** Store our model data in a float buffer. */
    private final FloatBuffer mCube;

    /** This will be used to pass in the transformation matrix. */
    private int mMVPMatrixHandle;

    /** This will be used to pass in model position information. */
    private int mPositionHandle;

    /** This will be used to pass in model color information. */
    private int mColorHandle;

    /** How many bytes per float. */
    private final int mBytesPerFloat = 4;

    /** How many elements per vertex. */
    private final int mStrideBytes = 7 * mBytesPerFloat;

    /** Offset of the position data. */
    private final int mPositionOffset = 0;

    /** Size of the position data in elements. */
    private final int mPositionDataSize = 3;

    /** Offset of the color data. */
    private final int mColorOffset = 3;

    /** Size of the color data in elements. */
    private final int mColorDataSize = 4;


    // Get an instance of the SensorManager
    private SensorManager sManager;
    private Sensor mRotationVectorSensor;

    //Context of the related activity
    // We need the context to retreive shaders' text files as raw resources
    Context activityCtx;

    public MyGLRenderer(Context context, SensorManager SensorManager)
    {
        activityCtx = context;

        sManager = SensorManager;
        mRotationVectorSensor = sManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // Initialize the buffers.
        XyzAsRgb xyzAsRgb = new XyzAsRgb();
        mCube = ByteBuffer.allocateDirect(xyzAsRgb.getGeometry().length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCube.put(xyzAsRgb.getGeometry()).position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        // Set the background clear color to gray.
        GLES20.glClearColor(0.4f, 0.4f, 0.4f, 1.0f);

        //Create shaders from raw text files
        int vShaderSource = R.raw.basic_vshader;
        int fShaderSource = R.raw.basic_fshader;
        ShaderObj vertexShader = new ShaderObj(activityCtx, GLES20.GL_VERTEX_SHADER, vShaderSource);
        ShaderObj fragmentShader = new ShaderObj(activityCtx, GLES20.GL_FRAGMENT_SHADER, fShaderSource);

        //Create the program
        GLProgramObj program = new GLProgramObj(vertexShader, fragmentShader);
        int programHandle = program.create(vertexShader, fragmentShader);

        // Set program handles. These will later be used to pass in values to the program.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");

        // Tell OpenGL to use this program when rendering.
        GLES20.glUseProgram(programHandle);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);
        // enable face culling feature
        GLES20.glEnable(GL10.GL_DEPTH_TEST);
        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        Matrix.perspectiveM(mProjectionMatrix, 0, 45.0f, ratio, 0.02f, 10.0f);
    }

    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        Matrix.setIdentityM(mTempMatrix, 0);//Temp matrix for combining them

        //Create a rotation matrix for x, y, z axis (orientation values are in Radians)
        Matrix.setRotateM(mRotXMatrix, 0, orientation[1]*57, 1.0f, 0.0f, 0.0f);//Rotate the model: Pitch (X axis)
        Matrix.setRotateM(mRotYMatrix, 0, orientation[0]*57, 0.0f, 1.0f, 0.0f);//Rotate the model: Yawl (Y axis)
        Matrix.setRotateM(mRotZMatrix, 0, orientation[2]*57,0.0f, 0.0f, 1.0f);//Rotate the model: Roll (Z axis)

        //Combine rotations
        Matrix.multiplyMM(mModelMatrix, 0, mRotYMatrix, 0, mRotXMatrix, 0);//multiply X by Y rotation
        mTempMatrix= mModelMatrix.clone();// We should avoid using same matrix for source and destination
        Matrix.multiplyMM(mModelMatrix, 0, mRotZMatrix, 0, mTempMatrix, 0);//multiply the result by Z rotation
        mTempMatrix = mModelMatrix.clone();//Save last rotation combining

        //Set camera like the default position, with 2.0f for Z instead of 1.0f
        Matrix.setLookAtM(mViewMatrix, 0, 0.0f, 0.0f, 2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

        //Combine with View matrix
        Matrix.multiplyMM(mTempMatrix, 0, mViewMatrix, 0, mModelMatrix, 0 );

        //Pass through projection matrix for final MVP combines matrix
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mTempMatrix, 0 );

        //Draw with the previous transforms
        draw(mCube);
    }

    /**
     * Draws a triangle from the given vertex data.
     *
     * @param aTriangleBuffer The buffer containing the vertex data.
     */
    private void draw(final FloatBuffer aTriangleBuffer)
    {
        // Pass in the position information
        aTriangleBuffer.position(mPositionOffset);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                mStrideBytes, aTriangleBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Pass in the color information
        aTriangleBuffer.position(mColorOffset);
        GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
                mStrideBytes, aTriangleBuffer);
        GLES20.glEnableVertexAttribArray(mColorHandle);

        //Pass in the MVP matrix
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 12);
    }

    //Sensor related functions
    public void start() {
        // enable our sensor when the activity is resumed, ask for
        // 10 ms updates.
        sManager.registerListener(this, mRotationVectorSensor, 10000);
    }

    public void stop() {
        // make sure to turn our sensor off when the activity is paused
        sManager.unregisterListener(this);
    }

    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // convert the rotation-vector to a 4x4 matrix. the matrix
            // is interpreted by Open GL as the inverse of the
            // rotation-vector, which is what we want.
            SensorManager.getRotationMatrixFromVector(mSensorRotationMatrix, event.values);//In radians

            SensorManager.getOrientation(mSensorRotationMatrix, orientation);//Get yaw/pitch/roll from matrix

            float pitch = orientation[1];
            float roll = orientation[2];
            float azimuth = orientation[0];

            ///update UI with YAw/Pitch/Roll values
            TextView azimuthView = (TextView) ((Activity)activityCtx).findViewById(9);
            String yawField = "Z axis/Yaw " + String.format("%.2f", azimuth);
            azimuthView.setText(yawField);

            TextView pitchView = (TextView) ((Activity)activityCtx).findViewById(10);
            String pitchField = "X axis/Pitch " + String.format("%.2f", pitch);
            pitchView.setText(pitchField);

            TextView rollView = (TextView) ((Activity)activityCtx).findViewById(11);
            String rollField = "Y axis/Roll " + String.format("%.2f", roll);
            rollView.setText(rollField);

            //Console output
            Log.d("ROT", "YAW (Azimuth)(radians): " + azimuthView );
            Log.d("ROT", "PITCH (radians): " + pitchView );
            Log.d("ROT", "ROLL (radians): " + rollView );
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /*
    * @param glOperation - Name of the OpenGL call to check.
    */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }
}
