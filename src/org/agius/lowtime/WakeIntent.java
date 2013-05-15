package org.agius.lowtime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.agius.lowtime.custom.RobotoButton;
import org.agius.lowtime.custom.RobotoTextView;
import org.agius.lowtime.domain.LowtimeSettings;

import com.google.analytics.tracking.android.EasyTracker;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;


public class WakeIntent extends LowtimeBase {

	static boolean active = false;	
	
	private RobotoTextView currentTime;
	private MediaPlayer player;
	private LowtimeSettings settings;
	
    long count = 0;
	
    @SuppressLint("SimpleDateFormat")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wake);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
	               + WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
	               + WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
	               + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        settings = getSettings();
        
        Calendar cal = Calendar.getInstance();
        Date datePre = cal.getTime();
        String formattedTime = new SimpleDateFormat("hh:mm a").format(datePre);	
        
        currentTime = (RobotoTextView) findViewById(R.id.current_time);
        currentTime.setText(formattedTime);
        
        try {
        	
            Uri uri = Uri.parse(settings.getWaketoneUri());
        	player = new MediaPlayer();
        	player.setDataSource(this, uri);
        	
        	final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        	if ( audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL )
            	audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            
            
        	if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
        		 player.setAudioStreamType(AudioManager.STREAM_RING);
        		 player.setLooping(true);
        		 player.prepare();
        		 player.start();
        	}
        	
    	} catch(Exception e) {
    		
    	}        
        
	    
        RobotoButton offButton = (RobotoButton) findViewById(R.id.turnoff);
        offButton.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	while(serviceRunning()){
                    stopService(new Intent(WakeIntent.this, LowtimeService.class));
            	}
            	settings.setActive(false);
            	settings.setLowtimeLaunched(false);
            	settings.commit();

            	clearAlarm();
                
            	player.stop();
            	finish();
            }
        });
        
        
        RobotoButton backButton = (RobotoButton) findViewById(R.id.snooze);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
                stopService(new Intent(WakeIntent.this, LowtimeService.class));
                startService(new Intent(WakeIntent.this, LowtimeService.class));
             	settings.setLowtimeLaunched(false);
            	settings.commit();
            	player.stop();
            	finish();
            }
        });
        
    }
    
    
    
    /*
     * http://stackoverflow.com/questions/600207/android-check-if-a-service-is-running
     */
    private boolean serviceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (LowtimeService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    
    
    
    @Override
    protected void onStart() {
        super.onStart();  
        EasyTracker.getInstance().activityStart(this);
//    	active = true;
    }


    @Override
    protected void onStop(){
    	super.onStop();
        EasyTracker.getInstance().activityStop(this);
//    	active = false;
    }
    
}
