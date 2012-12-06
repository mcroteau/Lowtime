package org.agius.lowtime;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class LowtimeIntent extends Activity implements SensorEventListener {
	
	private float mLastX, mLastY, mLastZ;
	private boolean mInitialized;

	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private final float NOISE = (float) 2.0;

	int lowHour = 1;

	private float zSensor;
	private MediaPlayer mediaPlayer, 
						sleepPlayer, 
						wakePlayer;	

	private static Ringtone ringtone;
	
	private String sleeptoneUri;
	private String waketoneUri;
	private SharedPreferences settings;
	
	private int minutes,
				lowtimeHour,
				lowtimeMinute;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lowtime);
    	
		try{
			
			settings = getSharedPreferences("lowtimeSettings", 0);
	    	
			mInitialized = false;
			
			mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	        sleeptoneUri = settings.getString("sleeptoneUri","");
	        waketoneUri = settings.getString("waketoneUri", "");
	        
	        
//	        lowtimeHour = Integer.parseInt(settings.getString("lowtimeHour", ""));
//	        minutes = Integer.parseInt(settings.getString("minutes", ""));
//	        lowtimeMinute = Integer.parseInt(settings.getString("lowtimeMinute", ""));
	        
	
	        Button backButton = (Button) findViewById(R.id.back);
	        backButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
                	if(ringtone != null && ringtone.isPlaying())ringtone.stop();
	                Intent i = new Intent(getApplicationContext(), MainActivity.class);
	                startActivity(i);
	            }
	        });
	        
	        
		}catch(Exception e){}
        
	}

	
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}
		
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}
		
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}
		
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		
		TextView tvX= (TextView)findViewById(R.id.x_axis);
		TextView tvY= (TextView)findViewById(R.id.y_axis);
		TextView tvZ= (TextView)findViewById(R.id.z_axis);
		TextView zvalue = (TextView)findViewById(R.id.zvalue);
		ImageView iv = (ImageView)findViewById(R.id.image);
		
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		
		if (!mInitialized) {
			
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			tvX.setText("0.0");
			tvY.setText("0.0");
			tvZ.setText("0.0");
			mInitialized = true;
			
		} else {
			
			int hour = new java.sql.Time(System.currentTimeMillis()).getHours();

			try{
				
		        Uri uri;
			    if (hour < lowHour){
			    	uri = Uri.parse(sleeptoneUri);
			    } else {
			    	uri = Uri.parse(waketoneUri);
			    }
	
			    ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
		    	
		    	System.out.println("z : " + Float.toString(z));
		    	zvalue.setText(Float.toString(z));
		    	
			    if ( (z > -9.5 && z < -7.0) || 
			    		( z > 7.0 && z < 9.5 ) ) {
			    	
			    	zvalue.setText("PLAY SOUND");

                	if(ringtone != null && ringtone.isPlaying())ringtone.stop();
                	ringtone.play();
                	
			    } 
			    
			}catch(Exception e){
				
			}
			
			float deltaX = Math.abs(mLastX - x);
			float deltaY = Math.abs(mLastY - y);
			float deltaZ = Math.abs(mLastZ - z);
			
			if (deltaX < NOISE) deltaX = (float)0.0;
			if (deltaY < NOISE) deltaY = (float)0.0;
			if (deltaZ < NOISE) deltaZ = (float)0.0;
			
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			
			tvX.setText(Float.toString(deltaX));
			tvY.setText(Float.toString(deltaY));
			tvZ.setText(Float.toString(deltaZ));
			iv.setVisibility(View.VISIBLE);
			
			if (deltaX > deltaY) {
				iv.setImageResource(R.drawable.horizontal);
			} else if (deltaY > deltaX) {
				iv.setImageResource(R.drawable.vertical);
			} else {
				iv.setVisibility(View.INVISIBLE);
			}
			
		}
		

		
	}
	
}
