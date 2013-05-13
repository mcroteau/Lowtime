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

public class AlarmIntent extends LowtimeBase {

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
        stopService(new Intent(AlarmIntent.this, LowtimeService.class));
        
        Calendar cal = Calendar.getInstance();
        Date datePre = cal.getTime();
        String formattedTime = new SimpleDateFormat("hh:mm a").format(datePre);	
        
        currentTime = (RobotoTextView) findViewById(R.id.current_time);
        currentTime.setText(formattedTime);
        
        //set lowtime launched to prevent additional alarms to be initiated
    	settings.setLowtimeLaunched(true);
    	settings.commit();
        
    	
        try {
        	
            Uri uri = Uri.parse(settings.getWaketoneUri());
        	player = new MediaPlayer();
        	player.setDataSource(this, uri);
        	
        	final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        	//check to see if volume is normally set
        	Log.i("LOWTIME WAKE INTENT ", "check ringtone mode " + audioManager.getRingerMode() + " : " + (audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) );
            if ( audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL )
            	audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            
            
        	if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
        		 player.setAudioStreamType(AudioManager.STREAM_RING);
        		 player.setLooping(true);
        		 player.prepare();
        		 player.start();
        	}
        	
    	} catch(Exception e) {}        
        
	    
        RobotoButton offButton = (RobotoButton) findViewById(R.id.turnoff);
        offButton.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	while(serviceRunning()){
                    stopService(new Intent(AlarmIntent.this, LowtimeService.class));
            	}
                
            	clearAlarm();

            	settings.setActive(false);
            	settings.setLowtimeLaunched(false);
            	
            	settings.commit();
                
            	player.stop();
            	finish();
            }
        });
        
        
        RobotoButton snooze = (RobotoButton) findViewById(R.id.snooze);
        snooze.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
                stopService(new Intent(AlarmIntent.this, LowtimeService.class));
            	player.stop();
            	
            	
            	/* New logic to reset lowtime, increase by snooze duration, then restart service */
    	        Calendar currentCalendar = Calendar.getInstance();
            	int hour = currentCalendar.get(Calendar.HOUR_OF_DAY);
            	int min = currentCalendar.get(Calendar.MINUTE);
            	
            	//increase by snooze amount
            	int snoozed = min + settings.getSnoozeDuration();
   
            	if(snoozed >= 60){
            		snoozed = snoozed - 60;
            		if(hour == 24){
            			hour = 1;
            		}else{
            			hour++;
            		}
            	}

            	
            	clearAlarm();

            	settings.setLowtimeLaunched(false);
            	settings.setHour(hour);
            	settings.setMinutes(snoozed);
            	settings.commit();
            	
                startService(new Intent(AlarmIntent.this, LowtimeService.class));
                
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
    }

    @Override
    protected void onStop(){
    	super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }
    
    
}
