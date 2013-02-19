package org.agius.lowtime;

import static org.agius.lowtime.LowtimeConstants.LOWTIME_SETTINGS;

import java.util.HashMap;
import java.util.Map;

import org.agius.lowtime.domain.LowtimeSettings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

@SuppressLint("UseSparseArrays")
public class LowtimeSettingIntent extends Activity {

	TextView waketoneView; 
	TimePicker timePicker;
	private Spinner minutesSpinner;
	Map<Integer, Integer> minuteOptionsLookup;
    
	private LowtimeSettings settings;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lowtime_setting);
        
        try {

            settings = new LowtimeSettings(getSharedPreferences(LOWTIME_SETTINGS, 0));
            
        	setupOptionsLookup();
	        
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
	            	
	            	if(!settings.getWaketone().equals("")){
	                    
	                    int lowtimeHour = timePicker.getCurrentHour();
	                    int lowtimeMinute = timePicker.getCurrentMinute();
	                    int lowtimeMinuteDifference = Integer.parseInt(minutesSpinner.getSelectedItem().toString());
	                    
	                    settings.setHour(lowtimeHour);
	                    settings.setMinutes(lowtimeMinute);
	                    settings.setRange(lowtimeMinuteDifference);
	                    settings.setActive(true);
	                    settings.commit();
	                    
		                Intent i = new Intent(LowtimeSettingIntent.this, HomeActivity.class);
		                startActivity(i);
		                
	            	}else{

	            		AlertDialog.Builder builder = new AlertDialog.Builder(LowtimeSettingIntent.this);

	            		builder.setMessage("Your \"Wakeup Tone\" needs to be set")
	            		       .setTitle("")
	            		       .setCancelable(true)
	            		       .setPositiveButton("OK", new OnClickListener(){
									@Override
									public void onClick(DialogInterface dialogInterface, int arg1) {
										dialogInterface.dismiss();
									}
	            		       });

	            		AlertDialog dialog = builder.create();
	            		dialog.show();
	            	}
	            	
	            }
	        });
	        
	        
	 
	        Button cancelButton = (Button) findViewById(R.id.cancel);
	        cancelButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                Intent i = new Intent(LowtimeSettingIntent.this, HomeActivity.class);
	                startActivity(i);
	            }
	        });
	        

	        minutesSpinner = (Spinner) findViewById(R.id.minutes);
	        waketoneView = (TextView) findViewById(R.id.waketone);
	        
	        reinitializeView();
            
            
//          sleeptone = settings.getString("sleeptone", "");
//	        TextView sleetoneView = (TextView) findViewById(R.id.sleeptone);
//	        sleetoneView.setText(sleeptone);
//	        sleeptoneUri = settings.getString("sleeptoneUri","");
	
	        addListenerOnSpinnerItemSelection();
	        
	    }catch (Exception e){
	    	e.printStackTrace();
	    }
        
    }

    
    private void setupOptionsLookup(){

    	minuteOptionsLookup = new HashMap<Integer, Integer>();
    	minuteOptionsLookup.put(5, 0);
    	minuteOptionsLookup.put(10, 0);
    	minuteOptionsLookup.put(15, 0);
    	minuteOptionsLookup.put(20, 0);
    	minuteOptionsLookup.put(25, 0);
    	minuteOptionsLookup.put(30, 0);
    	minuteOptionsLookup.put(35, 0);
    	minuteOptionsLookup.put(40, 0);
    	minuteOptionsLookup.put(45, 0);
    	minuteOptionsLookup.put(50, 0);
    	minuteOptionsLookup.put(60, 0);
    	minuteOptionsLookup.put(90, 0);
    	
    }
    
    
    public void addListenerOnSpinnerItemSelection() {
    	minutesSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener(settings.getSettings()));
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    
    @Override
    protected void onStart() {
        super.onStart();  
        reinitializeView();
    }

    private void reinitializeView(){
    	
        if(settings.settingsSet()){
        	timePicker.setCurrentHour(settings.getHour());
        	timePicker.setCurrentMinute(settings.getMinutes());
        	minutesSpinner.setSelection(minuteOptionsLookup.get(settings.getRange()));
        }
        
        if(!settings.getWaketone().equals("")){
            waketoneView.setText(settings.getWaketone());
        }
    }
    
    
    @Override
    protected void onStop() {
        super.onStop();  
    }  

    @Override
    protected void onRestart() {
        super.onRestart();  
    	settings.reinitialize(getSharedPreferences(LOWTIME_SETTINGS, 0));
    }
    
}
