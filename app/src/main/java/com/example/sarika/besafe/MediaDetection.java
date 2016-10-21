package com.example.sarika.besafe;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.KeyEvent;
import android.widget.Toast;

public class MediaDetection extends Service {
    private final Handler handler = new Handler();
    public static boolean isIntentServiceRunning=false;
    private SoundMeter mSensor;
    private int threshold;

    public MediaDetection() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

            isIntentServiceRunning = true;
            Bundle passed_value = intent.getExtras();
            if (passed_value != null) {
                String thresh = passed_value.getString("MAIN_ACTIVITY");
                threshold = Integer.parseInt(thresh);
                Toast.makeText(this, "Threshold received : " + threshold, Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Problem loading number", Toast.LENGTH_SHORT).show();
            }

        //HeadSetReceiver myReceiver=new HeadSetReceiver();
        return START_STICKY;
    }



    @Override
    public void onCreate()
    {
        try {
            mSensor = new SoundMeter();
            mSensor.start();
            doToast();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        isIntentServiceRunning=false;
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }



   /* public void connected(double amp)
    {
        Toast.makeText(this,"HeadSet connected and music player running "+amp,Toast.LENGTH_SHORT).show();
    }

    public void connected1()
    {
        Toast.makeText(this,"Outside loop ",Toast.LENGTH_SHORT).show();
    }
    public void disconnected()
    {
        Toast.makeText(this,"Inside loop",Toast.LENGTH_SHORT).show();
    }*/

    public void doToast()
    {
        isIntentServiceRunning=true;
        handler.postDelayed(new Runnable(){

            @Override
            public void run() {
                // TODO Auto-g0enerated method stub
                AudioManager am1 = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                MediaPlayer mp = new MediaPlayer();
                if(isIntentServiceRunning==false)
                {
                    return;
                }
                if(am1.isWiredHeadsetOn() && am1.isMusicActive() || am1.isWiredHeadsetOn() && mp.isPlaying())
                {


                    double amp = mSensor.getAmplitude();
                    //connected(amp);
                    if ((amp > threshold))
                    {

                        am1.setStreamMute(AudioManager.STREAM_MUSIC, true);
                        // am1.adjustStreamVolume(STREAM_MUSIC,  ADJUST_MUTE);

                    }
                    else {
                        if(KeyEvent.KEYCODE_VOLUME_DOWN!=0) {
                            //connected1();
                            am1.setStreamMute(AudioManager.STREAM_MUSIC, false);
                        }
                        else if(KeyEvent.KEYCODE_VOLUME_UP!=0)
                        {
                            am1.setStreamMute(AudioManager.STREAM_MUSIC, false);
                        }
                        //am1.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                    }
                }
                handler.postDelayed(this, 3000);
            }

        }, 3000);

    }

}
