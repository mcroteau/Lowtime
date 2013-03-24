package org.agius.lowtime;

import org.agius.lowtime.domain.LowtimeSettings;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


import static org.agius.lowtime.LowtimeConstants.*;

public class WakeIntent extends Activity{

	static boolean active = false;	
	
	private MediaPlayer player;
	private LowtimeSettings settings;
	
    long count = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wake);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
	               + WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
	               + WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
	               + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);


        count = getInstanceCount();
        
        if(count == 0){
        	
        	if(serviceRunning())
            	System.out.println("running");
        	
            
            settings = new LowtimeSettings(getSharedPreferences(LOWTIME_SETTINGS, 0));
            
            TextView wakeText = (TextView)findViewById(R.id.wake);
            Typeface face= Typeface.createFromAsset(getAssets(), "fonts/Roboto-Black.ttf");

            wakeText.setTypeface(face);
            
            try {
            	
                Uri uri = Uri.parse(settings.getWaketoneUri());
            	player = new MediaPlayer();
            	player.setDataSource(this, uri);
            	
            	final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            	  
            	if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
            		 player.setAudioStreamType(AudioManager.STREAM_RING);
            		 player.setLooping(true);
            		 player.prepare();
            		 player.start();
            	}
            	
        	} catch(Exception e) {
        		
        	}        
            
    	    
            Button offButton = (Button) findViewById(R.id.turnoff);
            offButton.setOnClickListener(new View.OnClickListener() {
                @Override
				public void onClick(View v) {
                	while(serviceRunning()){
                        stopService(new Intent(WakeIntent.this, TheService.class));
                	}
                	settings.setActive(false);
                	settings.commit();
                	player.stop();
                	finish();
                }
            });
            
            
            Button backButton = (Button) findViewById(R.id.snooze);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
				public void onClick(View v) {
                    stopService(new Intent(WakeIntent.this, TheService.class));
                    startService(new Intent(WakeIntent.this, TheService.class));
                	player.stop();
                	finish();
                }
            });
        
        }else{
        	//dont start the activity
        }
        

        
    }
    
    /*
     * http://stackoverflow.com/questions/600207/android-check-if-a-service-is-running
     */
    private boolean serviceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TheService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    
//    @Override
//    protected void onRestart() {
//        super.onRestart();  
//        settings.reinitialize(getSharedPreferences(LOWTIME_SETTINGS, 0));
//    }
    
    @Override
    public void onStart(){
    	super.onStart();
    	active = true;
    }
    
    
    @Override
    public void onStop(){
    	super.onStop();
    	active = false;
    }
    
}
