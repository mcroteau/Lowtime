package org.agius.lowtime;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WakeIntent extends Activity{

	private String waketoneUri;
	private SharedPreferences settings;
	private MediaPlayer player;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wake);

		settings = getSharedPreferences("lowtimeSettings", 0);
        waketoneUri = settings.getString("waketoneUri", "");    
        
        try {
        	
        	Uri alert =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            Uri uri = Uri.parse(waketoneUri);
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
        
	    
        Button backButton = (Button) findViewById(R.id.turnoff);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	player.stop();
                Intent i = new Intent(getApplicationContext(), LowtimeIntent.class);
                startActivity(i);
            }
        });
        
        Button backButton = (Button) findViewById(R.id.turnoff);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	player.stop();
                Intent i = new Intent(getApplicationContext(), LowtimeIntent.class);
                startActivity(i);
            }
        });
        
    }
    
}
