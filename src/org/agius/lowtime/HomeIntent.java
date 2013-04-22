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
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import org.agius.lowtime.domain.LowtimeSettings;

import static org.agius.lowtime.LowtimeConstants.*;

@SuppressLint("SimpleDateFormat")
public class HomeIntent  extends Activity{

	private TextView lowtime;
	private TextView lowtimeTitle;
	private TextView lowtimeStatus;
	private TextView lowtimeRangeMinutes;
	
	private TableRow lowtimeAlarmRow;
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
    	
        lowtimeAlarmRow = (TableRow) findViewById(R.id.lowtimeAlarmRow);
        rangeRow = (TableRow) findViewById(R.id.rangeRow);
//        createRow = (TableRow) findViewById(R.id.createRow);
        editRow = (TableRow) findViewById(R.id.editRow);
        
        lowtime = (TextView) findViewById(R.id.lowtime);
        lowtimeTitle = (TextView) findViewById(R.id.lowtimeTitle);
        lowtimeStatus = (TextView) findViewById(R.id.lowtimeStatus);
        lowtimeRangeMinutes = (TextView) findViewById(R.id.lowtimeRangeMinutes);
        
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
        stopService(new Intent(HomeIntent.this, TheService.class));
        startService(new Intent(HomeIntent.this, TheService.class));
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
        
//        editRow.setVisibility(View.VISIBLE);
//        lowtimeAlarmRow.setVisibility(View.VISIBLE);
//        rangeRow.setVisibility(View.VISIBLE);
//    	createRow.setVisibility(View.GONE);
    	if(settings.isActive()){
    		setActiveViewState();
    	}else{
    		setInactiveViewState();
    	}
    }
    
    
    private void displayLowtimeUnsetView(){

//    	createRow.setVisibility(View.VISIBLE);
//        editRow.setVisibility(View.GONE);
//        lowtimeAlarmRow.setVisibility(View.GONE);
//        rangeRow.setVisibility(View.GONE);
        
        if(settings.isActive()){
    		setActiveViewState();
    	}else{
    		setInactiveViewState();
    	}
    }
    
    
    private void setActiveViewState(){
        lowtimeStatus.setText(LOWTIME_ACTIVE_LABEL);
        lowtimeStatus.setBackgroundColor(Color.parseColor(ACTIVE_COLOR));
        toggleButton.setText(LOWTIME_BUTTON_DISABLE);
    }
    
    private void setInactiveViewState(){
        lowtimeStatus.setText(LOWTIME_INACTIVE_LABEL);
        lowtimeStatus.setBackgroundColor(Color.parseColor(INACTIVE_COLOR));
        toggleButton.setText(LOWTIME_BUTTON_ENABLE);
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

        Calendar cal = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, settings.getHour());
        cal.set(Calendar.MINUTE, settings.getMinutes());
        
        if(cal.getTimeInMillis() < System.currentTimeMillis()){
            cal.add(Calendar.DATE, 1);  
        }
 
        Intent intent = new Intent(this, WakeIntent.class);
        intent.putExtra("onetime", Boolean.TRUE);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager =  (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        
        
    }
    
    private void updateStatus(){

    	if(serviceRunning())
    		stopService(new Intent(HomeIntent.this, TheService.class));
    	
    	String status = LOWTIME_ACTIVE_LABEL;
    	String buttonText = LOWTIME_BUTTON_DISABLE;
    	if(settings.isActive()){
    		settings.setActive(false);
    		status = LOWTIME_INACTIVE_LABEL;
    		lowtimeStatus.setBackgroundColor(Color.parseColor(ACTIVE_COLOR));
    		buttonText = LOWTIME_BUTTON_ENABLE;
        	stopService(new Intent(HomeIntent.this, TheService.class));
    	}else{
    		settings.setActive(true);
    		lowtimeStatus.setBackgroundColor(Color.parseColor(INACTIVE_COLOR));
        	startService(new Intent(HomeIntent.this, TheService.class));
    	}    	

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
                Intent i = new Intent(HomeIntent.this, LowtimeSettingIntent.class);
                startActivity(i);
            }
        });
        
//        hideButton = (Button) findViewById(R.id.hideButton);
//        hideButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//			public void onClick(View v) {
//            	moveTaskToBack(true);
//            }
//        });
        
        
        toggleButton = (Button) findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	updateStatus();
            }
        });	  
        
//        createButton = (Button) findViewById(R.id.createButton);
//        createButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//			public void onClick(View v) {
//                Intent i = new Intent(HomeIntent.this, LowtimeSettingIntent.class);
//                startActivity(i);
//            }
//        });
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    
    private void showSettings(){
        Intent i = new Intent(HomeIntent.this, LowtimeSettingIntent.class);
        startActivity(i);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                showSettings();
                return true;
        }
		return false;
    }
    
}
