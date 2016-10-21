package com.example.sarika.besafe;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MonitorNoise extends ActionBarActivity {

    private static final int POLL_INTERVAL = 300;

    /** running state **/
    private boolean mRunning = false;

    /** config state **/
    private int mThreshold;

    private Handler mHandler = new Handler();

    /* References to view elements */
    private TextView mStatusView;
    private SoundLevelView mDisplay;

    /* sound data source */
    private SoundMeter mSensor;

    private Runnable mSleepTask = new Runnable() {
        public void run() {
            //Log.i("Noise", "runnable mSleepTask");

            start();
        }
    };

    // Create runnable thread to Monitor Voice
    private Runnable mPollTask = new Runnable() {
        public void run() {

            double amp = mSensor.getAmplitude();
            //Log.i("Noise", "runnable mPollTask");
            updateDisplay("Monitoring Voice...", amp);

            // Runnable(mPollTask) will again execute after POLL_INTERVAL
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_noise);

        mStatusView = (TextView) findViewById(R.id.status);

        // Used to record voice
        mSensor = new SoundMeter();
        mDisplay = (SoundLevelView) findViewById(R.id.volume);

    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.i("Noise", "==== onResume ===");

        initializeApplicationConstants();
        mDisplay.setLevel(0, mThreshold);

        if (!mRunning) {
            mRunning = true;
            start();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        // Log.i("Noise", "==== onStop ===");

        //Stop noise monitoring
        stop();

    }


    private void start() {
        //Log.i("Noise", "==== start ===");

        mSensor.start();

        //Noise monitoring start
        // Runnable(mPollTask) will execute after POLL_INTERVAL
        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
    }


    private void stop() {
        Log.i("Noise", "==== Stop Noise Monitoring===");

        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);
        mSensor.stop();
        mDisplay.setLevel(0,0);
        updateDisplay("stopped...", 0.0);
        mRunning = false;

    }

    private void initializeApplicationConstants() {
        // Set Noise Threshold
        mThreshold = 2;

    }


    private void updateDisplay(String status, double signalEMA) {
        mStatusView.setText(status);
        //
        mDisplay.setLevel((int)signalEMA, mThreshold);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_monitor_noise, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
