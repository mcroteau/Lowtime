package org.agius.lowtime;


import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.agius.lowtime.domain.LowtimeSettings;
import static org.agius.lowtime.LowtimeConstants.LOWTIME_SETTINGS;

public class LowtimeIntentOLD extends Activity implements SensorEventListener {
	
	private LowtimeSettings settings;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lowtime);
		
		try{
			
			settings = new LowtimeSettings(getSharedPreferences(LOWTIME_SETTINGS, 0));
	        
	        final Calendar calendar = Calendar.getInstance();
	        calendar.set(Calendar.HOUR_OF_DAY, settings.getHour());
	        calendar.set(Calendar.MINUTE, settings.getMinutes());
	        
	        Button backButton = (Button) findViewById(R.id.back);
	        backButton.setOnClickListener(new View.OnClickListener() {
	            @Override
				public void onClick(View v) {
//                	if(ringtone != null && ringtone.isPlaying())ringtone.stop();
	                Intent i = new Intent(getApplicationContext(), LowtimeSettingIntent.class);
	                startActivity(i);
	            }
	        });
	       
			
			
		}catch(Exception e){}
        
	}


	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
		
	}


}
