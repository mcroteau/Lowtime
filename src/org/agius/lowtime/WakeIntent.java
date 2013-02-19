package org.agius.lowtime;

import org.agius.lowtime.domain.LowtimeSettings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import static org.agius.lowtime.LowtimeConstants.*;

public class WakeIntent extends Activity{

	static boolean active = false;	
	
	private MediaPlayer player;
	private LowtimeSettings settings;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wake);

        settings = new LowtimeSettings(getSharedPreferences(LOWTIME_SETTINGS, 0));
        
        TextView wakeText = (TextView)findViewById(R.id.wake);
        Typeface face= Typeface.createFromAsset(getAssets(), "fonts/Roboto-Black.ttf");

        wakeText.setTypeface(face);
        
        try {
        	
            Uri uri = Uri.parse(settings.getWaketoneUri());
        	player = new MediaPlayer();
        	player.setDataSource(this, uri);
        	
        	final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        	  
//        	if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
//        		 player.setAudioStreamType(AudioManager.STREAM_RING);
//        		 player.setLooping(true);
//        		 player.prepare();
//        		 player.start();
//        	}
        	
    	} catch(Exception e) {
    		
    	}        
        
	    
        Button offButton = (Button) findViewById(R.id.turnoff);
        offButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	player.stop();
                stopService(new Intent(WakeIntent.this, TheService.class));
            	finish();
            }
        });
        
        
        Button backButton = (Button) findViewById(R.id.snooze);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	player.stop();
            	finish();
            }
        });
        
    }
    

    
    @Override
    protected void onRestart() {
        super.onRestart();  
        settings.reinitialize(getSharedPreferences(LOWTIME_SETTINGS, 0));
    }
    
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
