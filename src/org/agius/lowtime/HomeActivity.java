package org.agius.lowtime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
    }
    
    
    @Override
    protected void onRestart() {
        super.onRestart();  
    	settings.reinitialize(getSharedPreferences(LOWTIME_SETTINGS, 0));
        displayLowtimeSetView();
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
        activatedStatus.setChecked(true);
        
        lowtimeStatus.setText(LOWTIME_ACTIVE_LABEL);
    }
    
    private void displayLowtimeUnsetView(){

    	createRow.setVisibility(View.VISIBLE);
        editRow.setVisibility(View.GONE);
        lowtimeRow.setVisibility(View.GONE);
        rangeRow.setVisibility(View.GONE);
        activatedStatus.setChecked(false);
        
        lowtimeStatus.setText(LOWTIME_INACTIVE_LABEL);
    }
    
    
    private void resetValues(){
    	settings.resetValues();
    }
    
    private void reinitializeView(){
    	if(settings != null){
        	displayLowtimeSetView();
        }else{
        	displayLowtimeUnsetView();
        }
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
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, LowtimeSettingIntent.class);
                startActivity(i);
            }
        });
        
        hideButton = (Button) findViewById(R.id.hideButton);
        hideButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	moveTaskToBack(true);
            }
        });
        
        
        toggleButton = (Button) findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	updateStatus();
            }
        });	  
        
        createButton = (Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
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
