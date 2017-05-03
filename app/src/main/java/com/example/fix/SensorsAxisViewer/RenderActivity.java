package com.example.fix.SensorsAxisViewer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RenderActivity extends Activity {

    private SensorManager mSensorManager;

    private GLSurfaceView mGLView;
    private MyGLRenderer mRenderer;

    public TextView azimuthView;
    public TextView pitchView;
    public TextView rollView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Check if the system supports OpenGL ES 2.0.
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2)
        {
            mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
            mRenderer = new MyGLRenderer(this, mSensorManager);
            // Request an OpenGL ES 2.0 compatible context.
            mGLView = new MyGLSurfaceView(this);
            mGLView.setId(0);
            mGLView.setEGLContextClientVersion(2);
            // Set the renderer to our demo renderer, defined below.
            mGLView.setRenderer(mRenderer);
        }
        else
        {
            // This is where you could create an OpenGL ES 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
            return;
        }

        RelativeLayout rl = new RelativeLayout(this);
        setContentView(rl);
        rl.addView(mGLView);

        TextView infosView = new TextView(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        infosView.setLayoutParams(lp);
        infosView.setId(8);
        infosView.setTextSize(20);
        infosView.setTextColor(Color.WHITE);
        //Quick and dirty text justification. This is ugly ;-)
        infosView.setText("                         ATTENTION!\n\n                   ABOVE VALUES ARE\n               DEVICE COORDINATES.\n\n" +
                "                 MOVING 3D ARROW\n         IS IN DEFAULT OPENGL SPACE.");
        rl.addView(infosView);

        //Add textViews for Yaw/Pitch/Roll values
        pitchView = new TextView(this); //Pitch is Device X Axis, values are of color Red
        lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        pitchView.setLayoutParams(lp);
        pitchView.setId(10);
        pitchView.setTextSize(18);
        pitchView.setTextColor(Color.RED);
        rl.addView(pitchView);

        rollView = new TextView(this); //Roll is Device Y axis, values are of color GREEN
        lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rollView.setLayoutParams(lp);
        rollView.setId(11);
        rollView.setTextSize(18);
        rollView.setTextColor(Color.GREEN);
        rl.addView(rollView);

        azimuthView = new TextView(this); //Azimuth is YAW on Device Z axis, values are of color BLUE
        lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        azimuthView.setLayoutParams(lp);
        azimuthView.setId(9);
        azimuthView.setTextSize(18);
        azimuthView.setTextColor(Color.BLUE);
        rl.addView(azimuthView);
    }

    @Override
    protected void onResume()
    {
        // The activity must call the GL surface view's onResume() on activity onResume().
        super.onResume();
        mRenderer.start();
        mGLView.onResume();
    }

    @Override
    protected void onPause()
    {
        // The activity must call the GL surface view's onPause() on activity onPause().
        super.onPause();
        mRenderer.stop();
        mGLView.onPause();
    }
}
