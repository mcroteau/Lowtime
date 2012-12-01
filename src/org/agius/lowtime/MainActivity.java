package org.agius.lowtime;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity{

	private String sleeptoneUri;
	private String waketoneUri;
	
	private String sleeptone;
	private String waketone;
	
	private SharedPreferences settings;
	
	
	private String t;
	private int lowHour = 22;
	private boolean d = false;
    private SensorManager sensorManager;
    private PowerManager powerManager;
    private WindowManager windowManager;
    private Display display;
    
    
	private MediaPlayer mediaPlayer, 
						sleepPlayer, 
						wakePlayer;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
        	
	        settings = getSharedPreferences("lowtimeSettings", 0);
	        
	        Button sleeptoneButton = (Button) findViewById(R.id.sleeptone_button);
	        sleeptoneButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                Intent i = new Intent(MainActivity.this, SleepToneIntent.class);
	                startActivity(i);
	            }
	        });
	        
	        Button waketoneButton = (Button) findViewById(R.id.waketone_button);
	        waketoneButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                Intent i = new Intent(MainActivity.this, WakeToneIntent.class);
	                startActivity(i);
	            }
	        });
	        
	        sleeptone = settings.getString("sleeptone", "");
	        TextView sleetoneView = (TextView) findViewById(R.id.sleeptone);
	        sleetoneView.setText(sleeptone);
	        
	        waketone = settings.getString("waketone", "");
	        TextView waketoneView = (TextView) findViewById(R.id.waketone);
	        waketoneView.setText(waketone);
	        
	        sleeptoneUri = settings.getString("sleeptoneUri","");
	        waketoneUri = settings.getString("waketoneUri", "");
	        
	        
	        wakePlayer = new MediaPlayer();
	        wakePlayer.setDataSource(waketoneUri);
	        sleepPlayer.prepare();
	        
	        
	        sleepPlayer = new MediaPlayer();
	        sleepPlayer.setDataSource(sleeptoneUri);
	        sleepPlayer.prepare();
	        
	        t = "";
	        
	        System.out.println("sleeptone : " + sleeptone + " waketone : " + waketone);
        
	        
	        // Get an instance of the SensorManager
	        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

	        // Get an instance of the PowerManager
	        powerManager = (PowerManager) getSystemService(POWER_SERVICE);

	        // Get an instance of the WindowManager
	        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
	        display = windowManager.getDefaultDisplay();

	        // Create a bright wake lock
//	        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, getClass().getName());

	        // instantiate our simulation view and set it as the activity's content
//	        simulationView = new SimulationView(this);
//	        setContentView(simulationView);
	        
	        startService(new Intent(this, LowtimeService.class));
        } catch(Exception e) {
        	e.printStackTrace();
        }
        
    }

    
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
      super.onSaveInstanceState(savedInstanceState);
      // Save UI state changes to the savedInstanceState.
      // This bundle will be passed to onCreate if the process is
      // killed and restarted.
      savedInstanceState.putString("waketone", waketone);
      savedInstanceState.putString("sleeptone", sleeptone);
    }    
    
    
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
      super.onRestoreInstanceState(savedInstanceState);
      // Restore UI state from the savedInstanceState.
      // This bundle has also been passed to onCreate.
      waketone = savedInstanceState.getString("waketone");
      sleeptone = savedInstanceState.getString("sleeptone");     
    }
    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    

}
