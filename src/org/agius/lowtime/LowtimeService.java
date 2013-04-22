package org.agius.lowtime;

import java.util.Calendar;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LowtimeService extends Service implements SensorEventListener {

	private static String TAG = "Lowtime";
	
	private float mLastX, mLastY, mLastZ;
	private boolean mInitialized;

	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private final float NOISE = (float) 2.0;

	private SharedPreferences settings;
	
	private int minutes,
				lowtimeHour,
				lowtimeMinute;
	
	
	@Override
	public void onCreate(){

        Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
        
		settings = getSharedPreferences("lowtimeSettings", 0);
    	mInitialized = false;
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		
	}

	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}


	
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "LowtimeService started");
    }
    
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "LowtimeService destroyed");
    }
    

	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}
		
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		
		if (!mInitialized) {
			
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			mInitialized = true;
			
		} else {
			
	        Calendar currentCalendar = Calendar.getInstance();
	        Calendar lowtimeCalendar = Calendar.getInstance();
	        lowtimeCalendar.set(Calendar.HOUR_OF_DAY, lowtimeHour);
	        lowtimeCalendar.set(Calendar.MINUTE, lowtimeMinute);
	        
	        long currentMillis = currentCalendar.getTimeInMillis();
	        long lowtimeMillis = lowtimeCalendar.getTimeInMillis();
	        
	        long diff = currentMillis - lowtimeMillis;
	        
	        long diffMinutes = ( diff/1000 ) / 60;
	        
			try{
				
		        Intent intent;
			    if (diffMinutes <= minutes){
	                intent = new Intent(getApplicationContext(), WakeIntent.class);
			    } else {
	                intent = new Intent(getApplicationContext(), SleepIntent.class);
			    }

		    	
			    if ( (z > -9.5 && z < -7.0) || 
			    		( z > 7.0 && z < 9.5 ) ) {
			    	
	                startActivity(intent);
	                
			    } 
			    
			}catch(Exception e){
				
			}
			
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			
		}
				
	}   
    
}
