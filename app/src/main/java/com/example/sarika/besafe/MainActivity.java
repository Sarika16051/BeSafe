package com.example.sarika.besafe;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    Toolbar toolbar;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;

    private static String TAG="MAIN_ACTIVITY";
    private boolean running=false;
    private Button startbutton;
    private Button monitorbutton;
    private Button brightButton;
    private ImageView im3;
    private SeekBar volumeLevel;
    private TextView displayVolume;
    private String Label;
    String Name="namekey";
    SharedPreferences sp;
    SharedPreferences volume;
    String mypref="mypref";
    String myvol = "myvol";
    String Flag="flagkey";
    String Level="levelkey";
    private int threshold=5;
    private int curBrightnessValue=0;




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
        setContentView(R.layout.activity_main);

        /*
        * Code for navigation drawer
        * */
        mTitle = mDrawerTitle = getTitle();
        mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        setupToolbar();

        DataModel[] drawerItem = new DataModel[3];

        drawerItem[0] = new DataModel(R.drawable.info, "About Us");
        drawerItem[1] = new DataModel(R.drawable.reset, "Restore defaults");
        drawerItem[2] = new DataModel(R.drawable.faq, "FAQs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.list_view_item_row, drawerItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        setupDrawerToggle();


        /*
        * Code for the actual app
        *
        * */

        startbutton=(Button)findViewById(R.id.startbutton);
       // monitorbutton=(Button)findViewById(R.id.monitorbutton);
        brightButton=(Button)findViewById(R.id.brightbutton);
        im3 = (ImageView)findViewById(R.id.img3);
        volumeLevel=(SeekBar)findViewById(R.id.volumeLevel);
        displayVolume=(TextView)findViewById(R.id.displayVolume);
        sp=getSharedPreferences(mypref, Context.MODE_PRIVATE);
        volume = getSharedPreferences(myvol,Context.MODE_PRIVATE);
        if(volume.contains(Level))
        {
            threshold=volume.getInt(Level,5);
            volumeLevel.setProgress(threshold*10);
            displayVolume.setText(threshold+"");
        }
        else
        {
            volumeLevel.setProgress(threshold*10);
            displayVolume.setText(threshold+"");
        }
        if(sp.contains(Name)){
            startbutton.setText(sp.getString(Name, ""));
        }

        if(sp.contains(Flag))
        {
            int f = sp.getInt(Flag,0);
            if(f == 0)
            {
                im3.setImageResource(R.drawable.rd1);
            }
            else
            {
                im3.setImageResource(R.drawable.gr1);
            }
        }





        // Used to record voice
        mSensor = new SoundMeter();
        mDisplay = (SoundLevelView) findViewById(R.id.volumeShowing);



        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running=true;
                int flag =0;
                Label=startbutton.getText().toString();
                if(Label.equalsIgnoreCase("Start")) {
                    startbutton.setText("Stop");
                    Label="Stop";
                    flag=0;

                        Intent StartService = new Intent(getBaseContext(), MediaDetection.class);
                        StartService.putExtra(TAG, threshold + "");
                        startService(StartService);

                }
                else{
                    startbutton.setText("Start");
                    Label="Start";
                    flag=1;
                    stopService(new Intent(getBaseContext(), MediaDetection.class));
                }
                SharedPreferences.Editor editor= sp.edit();
                editor.putString(Name, Label);
                editor.putInt(Flag, flag);
                editor.commit();
                startbutton.setText(Label);
                if(flag == 0)
                {
                    im3.setImageResource(R.drawable.rd1);
                }
                else
                {
                    im3.setImageResource(R.drawable.gr1);
                }
            }
        });

       /* monitorbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(getBaseContext(),MonitorNoise.class);
                startActivity(in);
            }
        });

*/

        brightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    curBrightnessValue=android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
                    if(curBrightnessValue>151){

                        brightButton.setBackgroundResource(R.drawable.border_radius_red);
                    }
                    else{


                        brightButton.setBackgroundResource(R.drawable.border_radius_green);
                    }

                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
                finally {
                    int DELAY = 1000;
                    //code for directly returning to main activity after completion of work
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            brightButton.setBackgroundResource(R.drawable.buttonstyleview);
                        }
                    }, DELAY);
                }

            }

        });
        volumeLevel.setOnSeekBarChangeListener(this);

    }
    public void checkcheck(View view)
    {
        Intent in=new Intent(MainActivity.this,Check.class);
        startActivity(in);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        threshold=progress/10;
        displayVolume.setText(threshold+"");
        SharedPreferences.Editor editorVolume= volume.edit();
        editorVolume.putInt(Level, threshold);
        editorVolume.commit();

        if(startbutton.getText().toString().equalsIgnoreCase("Stop")) {
            Intent StartServiceAgain = new Intent(getBaseContext(), MediaDetection.class);
            StartServiceAgain.putExtra(TAG, threshold + "");
            startService(StartServiceAgain);
        }
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
        //
        mDisplay.setLevel((int) signalEMA, mThreshold);
    }



    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

    }

    private void selectItem(int position) {

        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new ConnectFragment();
                break;
            case 1:
                fragment = new FixturesFragment();
                break;
            case 2:
                fragment = new TableFragment();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(mNavigationDrawerItemTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    void setupToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    void setupDrawerToggle(){
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle.syncState();
    }

}
