package org.agius.lowtime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TableRow;
import android.widget.TextView;

import org.agius.lowtime.domain.LowtimeSettings;




import static org.agius.lowtime.LowtimeConstants.*;

@SuppressLint("SimpleDateFormat")
public class HomeActivity  extends Activity{

	private TextView lowtime;
	private TextView lowtimeStatus;
	private TextView lowtimeRangeMinutes;
	private RadioButton activatedStatus;
	
	private TableRow lowtimeRow;
	private TableRow rangeRow;
	private TableRow createRow;
	private TableRow editRow;
	
	private Button hideButton;
	private Button toggleButton;
	private Button createButton;
	
	private LowtimeSettings settings;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        
    	if(serviceRunning())
        	System.out.println("running");
    	
        settings = new LowtimeSettings(getSharedPreferences(LOWTIME_SETTINGS, 0));
    	
        lowtimeRow = (TableRow) findViewById(R.id.lowtimeRow);
        rangeRow = (TableRow) findViewById(R.id.rangeRow);
        createRow = (TableRow) findViewById(R.id.createRow);
        editRow = (TableRow) findViewById(R.id.editRow);
        
        lowtime = (TextView) findViewById(R.id.lowtime);
        lowtimeStatus = (TextView) findViewById(R.id.lowtimeStatus);
        lowtimeRangeMinutes = (TextView) findViewById(R.id.lowtimeRangeMinutes);
        activatedStatus = (RadioButton) findViewById(R.id.activatedStatus);
        
        resetValues();
    	initializeLowtimeButtons();
    	reinitializeView();
    }
    
    
    @Override
    protected void onStart() {
        super.onStart();  
    	settings.reinitialize(getSharedPreferences(LOWTIME_SETTINGS, 0));
    	if(settings.settingsSet()){
    		if(settings.isActive()){
        		setActiveViewState();
    			restartService();
    		}else{
        		setInactiveViewState();
    		}
    	}else{
    		setInactiveViewState();
    	}
    }
    
    
    
    private void restartService(){
        stopService(new Intent(HomeActivity.this, TheService.class));
        startService(new Intent(HomeActivity.this, TheService.class));
    }
    
    
    private void displayLowtimeSetView(){
    	
        Calendar lowtimeCalendar = Calendar.getInstance();
        lowtimeCalendar.set(Calendar.HOUR_OF_DAY, settings.getHour());
        lowtimeCalendar.set(Calendar.MINUTE, settings.getMinutes());
        
        Calendar lowtimeBackCalendar = Calendar.getInstance();
        lowtimeBackCalendar.set(Calendar.HOUR_OF_DAY, settings.getHour());
        lowtimeBackCalendar.set(Calendar.MINUTE, settings.getMinutes());;
        lowtimeBackCalendar.add(Calendar.MINUTE, (-settings.getRange()));
        
        
        Date date = lowtimeCalendar.getTime();
        Date datePre = lowtimeBackCalendar.getTime();
        String formattedTime = new SimpleDateFormat("hh:mm a").format(date);	
        String formattedTimePre = new SimpleDateFormat("hh:mm a").format(datePre);     
        
        lowtime.setText(formattedTime);
        lowtimeRangeMinutes.setText(settings.getRange() + " mins    " + formattedTimePre + " - " + formattedTime);
        
        editRow.setVisibility(View.VISIBLE);
        lowtimeRow.setVisibility(View.VISIBLE);
        rangeRow.setVisibility(View.VISIBLE);
    	createRow.setVisibility(View.GONE);
    	if(settings.isActive()){
    		setActiveViewState();
    	}else{
    		setInactiveViewState();
    	}
    }
    
    
    private void displayLowtimeUnsetView(){

    	createRow.setVisibility(View.VISIBLE);
        editRow.setVisibility(View.GONE);
        lowtimeRow.setVisibility(View.GONE);
        rangeRow.setVisibility(View.GONE);
        
        if(settings.isActive()){
    		setActiveViewState();
    	}else{
    		setInactiveViewState();
    	}
    }
    
    
    private void setActiveViewState(){
        lowtimeStatus.setText(LOWTIME_ACTIVE_LABEL);
        toggleButton.setText(LOWTIME_BUTTON_DISABLE);
        activatedStatus.setChecked(true);
    }
    
    private void setInactiveViewState(){
        lowtimeStatus.setText(LOWTIME_INACTIVE_LABEL);
        toggleButton.setText(LOWTIME_BUTTON_ENABLE);
        activatedStatus.setChecked(false);
    }
    
    
    private void resetValues(){
    	settings.resetValues();
    }
    
    private void reinitializeView(){
    	if(settings.settingsSet()){
        	displayLowtimeSetView();
        	setAlarm();
        }else{
        	displayLowtimeUnsetView();
        }
    }
    
    private void setAlarm(){

        /************* NEW ALARM LOGIC ******************/
        
//    	AlarmManager alarm = (AlarmManager) getSystemService(HomeActivity.ALARM_SERVICE);
//
//    	Intent wakeIntent = new Intent(HomeActivity.this, AlarmReceiverActivity.class);
//    	PendingIntent wakeAlarmIntent = PendingIntent.getActivity(HomeActivity.this, 8675309, wakeIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//        Calendar calendar = Calendar.getInstance();
//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.SECOND, 10);
//        calendar.set(Calendar.SECOND, 10);
//        calendar.set(Calendar.HOUR, settings.getHour());
//        calendar.set(Calendar.MINUTE, settings.getMinutes());
//
//        System.out.println("\n\n*************  SET ALARM  *************** \n");
//        System.out.println(cal.getTimeInMillis() + " : " + calendar.getTimeInMillis());
//        System.out.println(calendar.get(Calendar.MINUTE) + " : "  + cal.get(Calendar.MINUTE));
//        System.out.println(calendar.get(Calendar.HOUR) + " : " + cal.get(Calendar.HOUR));
//        
//        alarm.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), wakeAlarmIntent);
        
        /************************************************/
        
        //Create an offset from the current time in which the alarm will go off.
        Calendar cal = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR, settings.getHour());
        cal.set(Calendar.MINUTE, settings.getMinutes());
        
        if(System.currentTimeMillis() < cal.getTimeInMillis()){
            System.out.println("\n\n*************  SET ALARM  *************** \n");
            System.out.println(cal.getTimeInMillis() + " : " + calendar.getTimeInMillis());
            System.out.println(cal.getTimeInMillis() - calendar.getTimeInMillis());
            System.out.println(calendar.get(Calendar.MINUTE) + " : "  + cal.get(Calendar.MINUTE));
            System.out.println(calendar.get(Calendar.HOUR) + " : " + cal.get(Calendar.HOUR));
            System.out.println(calendar.getTime() + " : " + cal.getTime());
            cal.set(Calendar.DATE, calendar.get(Calendar.DATE));
        }
 
        //Create a new PendingIntent and add it to the AlarmManager
        Intent intent = new Intent(this, WakeIntent.class);
        intent.putExtra("onetime", Boolean.TRUE);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
            12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = 
            (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                pendingIntent);
        
        
        TextView currentTime =  (TextView) findViewById(R.id.currentTime);
        currentTime.setText(String.valueOf(calendar.getTime()));
        
        TextView alarmTime =  (TextView) findViewById(R.id.alarmTime);
        alarmTime.setText(String.valueOf(cal.getTime()));
        
        
    }
    
    private void updateStatus(){

    	if(serviceRunning())
    		stopService(new Intent(HomeActivity.this, TheService.class));
    	
    	String status = LOWTIME_ACTIVE;
    	String buttonText = LOWTIME_BUTTON_DISABLE;
    	if(settings.isActive()){
    		settings.setActive(false);
    		status = LOWTIME_INACTIVE_LABEL;
    		buttonText = LOWTIME_BUTTON_ENABLE;
        	stopService(new Intent(HomeActivity.this, TheService.class));
    	}else{
    		settings.setActive(true);
        	startService(new Intent(HomeActivity.this, TheService.class));
    	}    	

        activatedStatus.setChecked(settings.isActive());
        lowtimeStatus.setText(status);
        toggleButton.setText(buttonText);
        
        //saves state
        settings.commit();
    }
    
    
    private void initializeLowtimeButtons(){
        Button editButton = (Button) findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, LowtimeSettingIntent.class);
                startActivity(i);
            }
        });
        
        hideButton = (Button) findViewById(R.id.hideButton);
        hideButton.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	moveTaskToBack(true);
            }
        });
        
        
        toggleButton = (Button) findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	updateStatus();
            }
        });	  
        
        createButton = (Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, LowtimeSettingIntent.class);
                startActivity(i);
            }
        });
    }
    
    /*
     * http://stackoverflow.com/questions/600207/android-check-if-a-service-is-running
     */
    private boolean serviceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TheService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
