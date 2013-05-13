package org.agius.lowtime;

import static org.agius.lowtime.LowtimeConstants.LOWTIME_SETTINGS;
import static org.agius.lowtime.LowtimeConstants.MAX_RANDOM;

import java.util.Calendar;
import java.util.Random;

import org.agius.lowtime.domain.LowtimeSettings;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import static org.agius.lowtime.LowtimeConstants.*;

public class LowtimeBase extends Activity{

	private LowtimeSettings settings;
	
   @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = new LowtimeSettings(getSharedPreferences(LOWTIME_SETTINGS, 0));    	
    }
	    
	public LowtimeSettings getSettings(){
		return this.settings;
	}
   
	public void clearAlarm(){
        Intent intent = new Intent(LowtimeBase.this, AlarmIntent.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(LowtimeBase.this, settings.getAlarmId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager =  (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        settings.setAlarmId(INACTIVE_ID);
        settings.setAlarmActive(false);
        settings.commit();
        
	}
	
	
	public void setAlarm(){

		if(!settings.isAlarmActive()){
			
	        Calendar cal = Calendar.getInstance();
	        
	        cal.setTimeInMillis(System.currentTimeMillis());
	        cal.set(Calendar.HOUR_OF_DAY, settings.getHour());
	        cal.set(Calendar.MINUTE, settings.getMinutes());
	        
	        if(cal.getTimeInMillis() < System.currentTimeMillis()){
	            cal.add(Calendar.DATE, 1);  
	        }
	 
	        Intent intent = new Intent(this, AlarmIntent.class);
	        
	        Random randomGenerator = new Random();
	        int alarmId = randomGenerator.nextInt(MAX_RANDOM);
	        PendingIntent pendingIntent = PendingIntent.getActivity(this, alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	        AlarmManager alarmManager =  (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
	        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
	        
	        settings.setAlarmId(alarmId);
	        settings.setAlarmActive(true);
	        settings.commit();
		
		}
		
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
