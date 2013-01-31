package org.agius.lowtime;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class LowtimeSettingIntent extends Activity {

//	private String sleeptoneUri;
	private String waketoneUri;
	
//	private String sleeptone;
	private String waketone;
	
	private SharedPreferences settings;
	private Spinner minutesSpinner;
    
	private String hour;
	private String minute;
	
	TimePicker timePicker;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        try {
        
        	settings = getSharedPreferences("lowtimeSettings", 0);
        
//	        Button sleeptoneButton = (Button) findViewById(R.id.sleeptone_button);
//	        sleeptoneButton.setOnClickListener(new View.OnClickListener() {
//	            public void onClick(View v) {
//	                Intent i = new Intent(MainActivity.this, SleepToneIntent.class);
//	                startActivity(i);
//	            }
//	        });
	        
            timePicker = (TimePicker) findViewById(R.id.lowtimetime);
            
	        Button waketoneButton = (Button) findViewById(R.id.waketone_button);
	        waketoneButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                Intent i = new Intent(LowtimeSettingIntent.this, WakeToneIntent.class);
	                startActivity(i);
	            }
	        });
	        
	        Button setLowtimeButton = (Button) findViewById(R.id.save);
	        setLowtimeButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                SharedPreferences.Editor editor = settings.edit();
                    
                    int lowtimeHour = timePicker.getCurrentHour();
                    int lowtimeMinute = timePicker.getCurrentMinute();
                    
                    editor.putInt("lowtimeHour", lowtimeHour);
                    editor.putInt("lowtimeMinute", lowtimeMinute);
                    
                    editor.commit();
                    
                    startService(new Intent(LowtimeSettingIntent.this, TheService.class));
                    moveTaskToBack(true);
                    
	                //Intent i = new Intent(MainActivity.this, LowtimeIntent.class);
	                //startActivity(i);
	            }
	        });

            minutesSpinner = (Spinner) findViewById(R.id.minutes);
            hour = settings.getString("hour", "");
            minute = settings.getString("minute", "");
            
            if(!hour.equals("") && !minute.equals("")){
            	timePicker.setCurrentHour(Integer.parseInt(hour));
            	timePicker.setCurrentMinute(Integer.parseInt(minute));
            }
            
//          sleeptone = settings.getString("sleeptone", "");
//	        TextView sleetoneView = (TextView) findViewById(R.id.sleeptone);
//	        sleetoneView.setText(sleeptone);
	        
	        waketone = settings.getString("waketone", "");
	        TextView waketoneView = (TextView) findViewById(R.id.waketone);
	        waketoneView.setText(waketone);
	
//	        sleeptoneUri = settings.getString("sleeptoneUri","");
//	        waketoneUri = settings.getString("waketoneUri", "");
	
	        addListenerOnSpinnerItemSelection();
	        
	    }catch (Exception e){
	    	e.printStackTrace();
	    }
        
    }

    
    public void addListenerOnSpinnerItemSelection() {
    	minutesSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener(settings));
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
}
