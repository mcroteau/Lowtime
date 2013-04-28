package org.agius.lowtime;

import java.util.Calendar;
import java.util.Date;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.util.Log;

import org.agius.lowtime.domain.LowtimeSettings;
import static org.agius.lowtime.LowtimeConstants.*;

public class LowtimeService extends Service implements SensorEventListener {
    
	private float mLastX, mLastY, mLastZ;
	private long mLastTime;
	private int mShakeCount = 0;
	private long mLastShake;
	private long mLastForce;
	
    private WakeLock mWakeLock = null;
    private SensorManager mSensorManager = null;

    private LowtimeSettings settings;
	private boolean intentLaunched = false;

	
    @Override
    public void onCreate() {
        super.onCreate();

        intentLaunched = false;
        settings = new LowtimeSettings(getSharedPreferences(LOWTIME_SETTINGS, 0));
        
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        PowerManager manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);

        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        
    }


    
    @Override
    public void onSensorChanged(SensorEvent event){
        
        try{

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
		    			
		    	        Calendar currentCalendar = Calendar.getInstance();
		    	        Calendar lowtimeCalendar = Calendar.getInstance();
		    	        lowtimeCalendar.set(Calendar.HOUR_OF_DAY, settings.getHour());
		    	        lowtimeCalendar.set(Calendar.MINUTE, settings.getMinutes());
		    	        
		    	        Date date = currentCalendar.getTime();
		    	        Date lowtimeDate = lowtimeCalendar.getTime();
		    	        
		    	        long mills = date.getTime();
		    	        long lowmills = lowtimeDate.getTime();
		    	        long dif = lowmills - mills;
		    	        long mins = (dif/1000) / 60;
		    	        
		    	        long currentMillis = currentCalendar.getTimeInMillis();
		    	        long lowtimeMillis = lowtimeCalendar.getTimeInMillis();
		    	        
		    	        long diffMillis = lowtimeMillis - currentMillis;
		    	        long diffMinutes = ( diffMillis/1000 ) / 60;
		    	        
		    	        Intent intent;
		    	        Context context = getApplicationContext();
		    		    if ((diffMinutes <= settings.getRange() || diffMinutes <= 0) && diffMinutes < MAX_MINUTES && diffMinutes > -MAX_MINUTES){
		    		    	intent = new Intent(context, WakeIntent.class);
		    		    } else {
		    		    	intent = new Intent(context, SleepIntent.class);
		    		    }
		    		    
		        		mLastShake = now;
		        		mShakeCount = 0; 
		        		
				        Log.i(TAG, "check to start activity " + settings.isLowtimeLaunched());
				        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				        if(!settings.isLowtimeLaunched() && !WakeIntent.active && !SleepIntent.active){
				        	Log.i(TAG, "START ACTIVITY");
				        	settings.setLowtimeLaunched(true);
				        	getApplicationContext().startActivity(intent);
				        	stopSelf();
				        }
				        
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


    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        unregisterListener();
        mWakeLock.release();
        stopForeground(true);
        super.onDestroy();  
    }

    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startForeground(Process.myPid(), new Notification());
        registerListener();
        mWakeLock.acquire();
        intentLaunched = false;
        return START_STICKY;
    }
    

    private void registerListener() {
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);		
    }


    private void unregisterListener() {
        mSensorManager.unregisterListener(this);
    }

    
    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive("+intent+")");
            if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                return;
            }
             
            Runnable runnable = new Runnable() {
                @Override
				public void run() {
                    Log.i(TAG, "Runnable executing.");
                    unregisterListener();
                    registerListener();
                }
            };

            new Handler().postDelayed(runnable, SCREEN_OFF_RECEIVER_DELAY);
        }
    };

    
    
    @Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(TAG, "onAccuracyChanged().");
    }

    
}
