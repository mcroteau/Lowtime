package org.agius.lowtime;

import java.util.Calendar;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager.WakeLock;
import android.os.PowerManager;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

public class TheService extends Service implements SensorEventListener {
    
	private static String TAG = "Lowtime";
	
    public static final int SCREEN_OFF_RECEIVER_DELAY = 500;

    private SensorManager mSensorManager = null;
    private WakeLock mWakeLock = null;
	

	private boolean mInitialized;

	private Sensor mAccelerometer;
	private final float NOISE = (float) 2.0;

	private SharedPreferences settings;
	
	private int minutes,
				lowtimeHour,
				lowtimeMinute;
	
	
	private static final int FORCE_THRESHOLD = 150;
	private static final int TIME_THRESHOLD = 100;
	private static final int SHAKE_TIMEOUT = 500;
	private static final int SHAKE_DURATION = 1000;
	private static final int SHAKE_COUNT = 3;
	
	private float mLastX, mLastY, mLastZ;
	private long mLastTime;
	private int mShakeCount = 0;
	private long mLastShake;
	private long mLastForce;

	
	
    /*
     * Register this as a sensor event listener.
     */
    private void registerListener() {
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

		Toast.makeText(getApplicationContext(), 
				"registerListener",
				Toast.LENGTH_SHORT).show();
		
    }

    /*
     * Un-register this as a sensor event listener.
     */
    private void unregisterListener() {
		Toast.makeText(getApplicationContext(), 
				"unregisterListener",
				Toast.LENGTH_SHORT).show();
        mSensorManager.unregisterListener(this);
    }

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive("+intent+")");
            
    		Toast.makeText(getApplicationContext(), 
    				"BroadcastReceiver",
    				Toast.LENGTH_SHORT).show();

            if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                return;
            }
             
            Runnable runnable = new Runnable() {
                public void run() {
                    Log.i(TAG, "Runnable executing.");
                    
            		Toast.makeText(getApplicationContext(), 
            				"Runnable",
            				Toast.LENGTH_SHORT).show();
            		
                    unregisterListener();
                    registerListener();
                }
            };

            new Handler().postDelayed(runnable, SCREEN_OFF_RECEIVER_DELAY);
        }
    };

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(TAG, "onAccuracyChanged().");
        Toast.makeText(getApplicationContext(), 
				"onAccuracyChanged",
				Toast.LENGTH_SHORT).show();
    }

    
    @Override
    public void onSensorChanged(SensorEvent event){
    	
        Calendar currentCalendar = Calendar.getInstance();
        Calendar lowtimeCalendar = Calendar.getInstance();
        lowtimeCalendar.set(Calendar.HOUR_OF_DAY, lowtimeHour);
        lowtimeCalendar.set(Calendar.MINUTE, lowtimeMinute);
        
        long currentMillis = currentCalendar.getTimeInMillis();
        long lowtimeMillis = lowtimeCalendar.getTimeInMillis();
        
        long diffMillis = currentMillis - lowtimeMillis;
        
        long diffMinutes = ( diffMillis/1000 ) / 60;
        
        
        try{
				
	        Intent intent;
		    if (diffMinutes <= minutes){
                intent = new Intent(getApplicationContext(), WakeIntent.class);
		    } else {
                intent = new Intent(getApplicationContext(), SleepIntent.class);
		    }
    
        
        
			long now = System.currentTimeMillis();
		    if ((now - mLastForce) > SHAKE_TIMEOUT) {
		        mShakeCount = 0;
		    }
	
		    if ((now - mLastTime) > TIME_THRESHOLD) {
		    	  
		    	long diff = now - mLastTime;
		        
		    	float sum = event.values[SensorManager.DATA_X] + event.values[SensorManager.DATA_Y] + event.values[SensorManager.DATA_Z];
		    	float speed = Math.abs(sum - mLastX - mLastY - mLastZ) / diff * 10000;
		        
		    	if (speed > FORCE_THRESHOLD) {
		    		if ((++mShakeCount >= SHAKE_COUNT) && (now - mLastShake > SHAKE_DURATION)) {
		        		mLastShake = now;
		        		mShakeCount = 0; 
				        Log.i(TAG, "start activity");
				        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				        if(!SleepIntent.active && !WakeIntent.active)
				        	getApplication().startActivity(intent);
				        
		        	}
		        	mLastForce = now;
		        }
		        mLastTime = now;
		        mLastX = event.values[SensorManager.DATA_X];
		        mLastY = event.values[SensorManager.DATA_Y];
		        mLastZ = event.values[SensorManager.DATA_Z];
		    }  	
		    
		}catch(Exception e){
			
		}
			
    }
    
    
    
    
    
    /**
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

			        Toast.makeText(getApplicationContext(), 
							"difference less than",
							Toast.LENGTH_SHORT).show();
			        
	                intent = new Intent(getApplicationContext(), WakeIntent.class);
			    } else {

			        Toast.makeText(getApplicationContext(), 
							"difference greater than",
							Toast.LENGTH_SHORT).show();
			        
	                intent = new Intent(getApplicationContext(), SleepIntent.class);
			    }

		    	
			    if ( (z > -9.5 && z < -7.0) || 
			    		( z > 7.0 && z < 9.5 ) ) {
			    	
			        Log.i(TAG, "start activity");
			        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			        if(!SleepIntent.active && !WakeIntent.active)
			        	getApplication().startActivity(intent);
			        
			    } 
			    
			}catch(Exception e){
				
			}
			
			mLastX = x;
			mLastY = y;
			mLastZ = z;
		
		}
		
    }
    **/
    

    @Override
    public void onCreate() {
        super.onCreate();

		settings = getSharedPreferences("lowtimeSettings", 0);

        minutes = settings.getInt("minutes", 0);
        lowtimeHour = settings.getInt("lowtimeHour", 0);
        lowtimeMinute = settings.getInt("lowtimeMinute", 0);
		
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        PowerManager manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);

        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        
        Toast.makeText(getApplicationContext(), "onCreate", Toast.LENGTH_SHORT).show();
        
    }

    
    @Override
    public void onDestroy() {
        
    	Toast.makeText(getApplicationContext(), 
				"onDestroy",
				Toast.LENGTH_SHORT).show();
        
        unregisterReceiver(mReceiver);
        unregisterListener();
        mWakeLock.release();
        stopForeground(true);
    }

    
    @Override
    public IBinder onBind(Intent intent) {
    	
    	Toast.makeText(getApplicationContext(), 
				"onBind",
				Toast.LENGTH_SHORT).show();
    	
        return null;
    }

    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	
    	Toast.makeText(getApplicationContext(), 
				"onStartCommand",
				Toast.LENGTH_SHORT).show();
    	
        super.onStartCommand(intent, flags, startId);
        startForeground(Process.myPid(), new Notification());
        registerListener();
        mWakeLock.acquire();
        return START_STICKY;
        
    }
    
}
