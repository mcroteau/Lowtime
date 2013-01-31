package org.agius.lowtime;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

public class HomeActivity  extends Activity{

	private String waketone;
	private int minutes = 0;
	private int lowtimeHour = 0;
	private int lowtimeMinute = 0;
	private SharedPreferences settings;

	private TextView lowtime;
	private TextView lowtimeRange;
	private TextView lowtimeRangeMinutes;
	private RadioButton activatedStatus;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        lowtime = (TextView) findViewById(R.id.lowtime);
        lowtimeRange = (TextView) findViewById(R.id.lowtimeRange);
        lowtimeRangeMinutes = (TextView) findViewById(R.id.lowtimeRangeMinutes);
        activatedStatus = (RadioButton) findViewById(R.id.activatedStatus);
        
    	settings = getSharedPreferences("lowtimeSettings", 0);

        minutes = settings.getInt("minutes", 0);
        lowtimeHour = settings.getInt("lowtimeHour", 0);
        lowtimeMinute = settings.getInt("lowtimeMinute", 0);
        waketone = settings.getString("waketone", "");
        
        
        if(minutes > 0 &&
        		lowtimeHour > 0 && 
        		lowtimeMinute > 0 && 
        		!waketone.equals("")){
        	
	        Calendar lowtimeCalendar = Calendar.getInstance();
	        lowtimeCalendar.set(Calendar.HOUR_OF_DAY, lowtimeHour);
	        lowtimeCalendar.set(Calendar.MINUTE, lowtimeMinute);
	        
	        Calendar lowtimeBack = lowtimeCalendar;
	        lowtimeCalendar.add(Calendar.MINUTE, minutes);
	        
	        Date timeAfter = lowtimeCalendar.getTime();
	        
	        lowtimeCalendar.add(Calendar.MINUTE, (-minutes*2));
	        Date timeBefore = lowtimeCalendar.getTime();
	        
	        //lowtimeRange
	        //activatedStatus
	        //lowtime
	        //lowtimeRangeMinutes
	        
	        String range = timeBefore.toGMTString() + " to " + timeAfter.toGMTString();
	        lowtimeRange.setText(range);
	        lowtimeRangeMinutes.setText(minutes + " mins");
	        lowtime.setText(lowtimeBack.getTime().toGMTString());
	        
	        activatedStatus.setSelected(true);
	        
	        
	        Button editButton = (Button) findViewById(R.id.editButton);
	        editButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                Intent i = new Intent(HomeActivity.this, LowtimeSettingIntent.class);
	                startActivity(i);
	            }
	        });
	        
	        
        }else{

	        activatedStatus.setSelected(false);
	        Button editButton = (Button) findViewById(R.id.editButton);
	        editButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                Intent i = new Intent(HomeActivity.this, LowtimeSettingIntent.class);
	                startActivity(i);
	            }
	        });
        }
        
    }

}
