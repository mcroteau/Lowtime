package org.agius.lowtime;

import java.util.Calendar;

import org.agius.lowtime.R;
import org.agius.lowtime.R.drawable;
import org.agius.lowtime.R.id;
import org.agius.lowtime.R.layout;

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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class LowtimeIntent extends Activity implements SensorEventListener {
	
	//private float mLastX, mLastY, mLastZ;
	private boolean mInitialized;

	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private final float NOISE = (float) 2.0;

//	private static Ringtone ringtone;
//	private String sleeptoneUri;
//	private String waketoneUri;
	private SharedPreferences settings;
	
	private int back;
	private int forth;
	private boolean backSet;
	private boolean forthSet;
	private boolean lowtime;
	
	private static final int SHAKE_THRESHOLD = 800;
	
	private int minutes,
				lowtimeHour,
				lowtimeMinute;
	
	
	private static final int FORCE_THRESHOLD = 150;
	private static final int TIME_THRESHOLD = 100;
	private static final int SHAKE_TIMEOUT = 500;
	private static final int SHAKE_DURATION = 1000;
	private static final int SHAKE_COUNT = 3;
	
	
	private SensorManager mSensorMgr;
	private float mLastX, mLastY, mLastZ;
	private long mLastTime;
	private int mShakeCount = 0;
	private long mLastShake;
	private long mLastForce;
	
	
	
		  
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lowtime);
		
		back = 0;
    	forth = 0;
    	backSet = false;
    	forthSet = false;
    	lowtime = false;
    	
		try{
			
			settings = getSharedPreferences("lowtimeSettings", 0);
	    	
			mInitialized = false;
			
			mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
			
//	        sleeptoneUri = settings.getString("sleeptoneUri","");
//	        waketoneUri = settings.getString("waketoneUri", "");

	        minutes = settings.getInt("minutes", 0);
	        lowtimeHour = settings.getInt("lowtimeHour", 0);
	        lowtimeMinute = settings.getInt("lowtimeMinute", 0);
	        
	        final Calendar calendar = Calendar.getInstance();
	        calendar.set(Calendar.HOUR_OF_DAY, lowtimeHour);
	        calendar.set(Calendar.MINUTE, lowtimeMinute);
	        
	        //TextView lowtimeText = (TextView) findViewById(R.id.lowtime);
	        //String time = calendar.getTime().toLocaleString();
	        //lowtimeText.setText(time);
	        
	        Button backButton = (Button) findViewById(R.id.back);
	        backButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
//                	if(ringtone != null && ringtone.isPlaying())ringtone.stop();
	                Intent i = new Intent(getApplicationContext(), LowtimeSettingIntent.class);
	                startActivity(i);
	            }
	        });
	       
			
			
		}catch(Exception e){}
        
	}


	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}
		
	
	
	@Override
	public void onSensorChanged(SensorEvent event){

		TextView tvX= (TextView)findViewById(R.id.x_axis);
		TextView tvY= (TextView)findViewById(R.id.y_axis);
		TextView tvZ= (TextView)findViewById(R.id.z_axis);
		TextView zvalue = (TextView)findViewById(R.id.zvalue);
		TextView backValue = (TextView)findViewById(R.id.back);
		TextView forthValue = (TextView)findViewById(R.id.forth);
		
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
	        		zvalue.setText("LOWTIME");
	        	}
	        	mLastForce = now;
	        }
	        mLastTime = now;
	        mLastX = event.values[SensorManager.DATA_X];
	        mLastY = event.values[SensorManager.DATA_Y];
	        mLastZ = event.values[SensorManager.DATA_Z];
	    }
	    
	}
	
	
	
	/**
	@Override
	public void onSensorChanged(SensorEvent event){
		float x = 0;
		float y = 0;
		float z = 0;
		float lastX = 0;
		float lastY = 0;
		float lastZ = 0;
		long lastUpdate = 0;


		TextView tvX= (TextView)findViewById(R.id.x_axis);
		TextView tvY= (TextView)findViewById(R.id.y_axis);
		TextView tvZ= (TextView)findViewById(R.id.z_axis);
		TextView zvalue = (TextView)findViewById(R.id.zvalue);
		TextView backValue = (TextView)findViewById(R.id.back);
		TextView forthValue = (TextView)findViewById(R.id.forth);
		
	    long curTime = System.currentTimeMillis();
	    
	    // only allow one update every 100ms.
	    if ((curTime - lastUpdate) > 100) {
			long diffTime = (curTime - lastUpdate);
			lastUpdate = curTime;
			
			x = event.values[SensorManager.DATA_X];
			y = event.values[SensorManager.DATA_Y];
			z = event.values[SensorManager.DATA_Z];
			  
			if(x > 0 || y > 0 || z > 0){
				tvX.setText(Float.toString(x));
				tvY.setText(Float.toString(y));
				tvZ.setText(Float.toString(z));
		    	zvalue.setText(Float.toString(z));
			}
	    	
			float diff = lastX - lastY - lastZ;
			float sum = x + y + z; 
			  
			float speed = Math.abs(sum - diff) / diffTime * 10000;
			
			if (speed > SHAKE_THRESHOLD) {
			    Log.d("sensor", "shake detected w/ speed: " + speed);
			    Toast.makeText(this, "shake detected w/ speed: " + speed, Toast.LENGTH_SHORT).show();
			}
			
			lastX = x;
			lastY = y;
			lastZ = z;
			
	    }
	    
	}
	**/
	
	
	
	
	/*
	@Override
	public void onSensorChanged(SensorEvent event) {
		
		TextView tvX= (TextView)findViewById(R.id.x_axis);
		TextView tvY= (TextView)findViewById(R.id.y_axis);
		TextView tvZ= (TextView)findViewById(R.id.z_axis);
		TextView zvalue = (TextView)findViewById(R.id.zvalue);
		TextView backValue = (TextView)findViewById(R.id.back);
		TextView forthValue = (TextView)findViewById(R.id.forth);
		
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
			

			tvX.setText(Float.toString(x));
			tvY.setText(Float.toString(y));
			tvZ.setText(Float.toString(z));
	    	zvalue.setText(Float.toString(z));
			

		    if ( ((z > -9.5 && z < -7.0) || 
		    		( z > 7.0 && z < 9.5 ) ) && !backSet && !lowtime) {
		    	back++;
		    	backValue.setText("b:" + Integer.toString(back));
		    	forthSet = false;
		    	backSet = true;
		    }

		    
		    if( ((z < 5 && z > 0) || (z > -5 && z < 0)) && !forthSet && !lowtime){
		    	forth++;
		    	forthValue.setText("f:" + Integer.toString(forth));
		    	forthSet = true;
		    	backSet = false;
		    }
		    
		    
	    	if(back >= 5 && forth >=5){
	    		back = 0;
	    		forth = 0;
	    		backSet = false;
	    		forthSet = false;
	    		lowtime = true;
	    		backValue.setText("LOW");
	    		forthValue.setText("TIME");
	    	}
	    	
	    	*/
	
	    	
			/*
	        Calendar currentCalendar = Calendar.getInstance();
	        Calendar lowtimeCalendar = Calendar.getInstance();
	        lowtimeCalendar.set(Calendar.HOUR_OF_DAY, lowtimeHour);
	        lowtimeCalendar.set(Calendar.MINUTE, lowtimeMinute);
	        
	        long currentMillis = currentCalendar.getTimeInMillis();
	        long lowtimeMillis = lowtimeCalendar.getTimeInMillis();
	        
	        long diff = currentMillis - lowtimeMillis;
	        
	        long diffMinutes = ( diff/1000 ) / 60;
	        
	        TextView differenceView = (TextView) findViewById(R.id.difference);
	        differenceView.setText(Long.toString(diffMinutes));
	        
			try{
				
		        Intent intent;
			    if (diffMinutes <= minutes){
	                intent = new Intent(getApplicationContext(), WakeIntent.class);
			    } else {
	                intent = new Intent(getApplicationContext(), SleepIntent.class);
			    }
	
		    	
		    	zvalue.setText(Float.toString(z));
		    	
			    if ( (z > -9.5 && z < -7.0) || 
			    		( z > 7.0 && z < 9.5 ) ) {
			    	
//			    	zvalue.setText("PLAY SOUND");
	                //startActivity(intent);
	                
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
			
			*/
			/*
		}
			
	}
		
	*/
	
}
