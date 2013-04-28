package org.agius.lowtime;

import static org.agius.lowtime.LowtimeConstants.LOWTIME_SETTINGS;
import static org.agius.lowtime.LowtimeConstants.TONE;
import static org.agius.lowtime.LowtimeConstants.URI;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.agius.lowtime.domain.LowtimeSettings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

@SuppressLint("UseSparseArrays")
public class LowtimeSettingIntent extends Activity {

	private TextView waketoneText; 
	private TimePicker timePicker;
	
	private Spinner rangeSpinner;
	private TextView rangeText;
	
	private Spinner snoozeDurationSpinner;
	private TextView snoozeDurationText;
	
	private Map<Integer, Integer> minuteOptionsLookup;
    
	private LowtimeSettings settings;
	private static Ringtone ringtone;
	
	private ArrayList<CheckBox> checkBoxes;
	
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Map<String, Object>> lookup = new HashMap<Integer, Map<String, Object>>();
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lowtime_setting);
        
        try {
	    	
        	setupOptionsLookup();
	       
            settings = new LowtimeSettings(getSharedPreferences(LOWTIME_SETTINGS, 0));
        	timePicker = (TimePicker) findViewById(R.id.lowtimetime);
	        waketoneText = (TextView) findViewById(R.id.waketone_value);
	        rangeText = (TextView) findViewById(R.id.range_value);
	        snoozeDurationText = (TextView) findViewById(R.id.snooze_duration_value); 
	        
	        final Dialog waketoneDialog = createWaketoneDialog();
	        Button waketoneButton = (Button) findViewById(R.id.set_waketone_button);
	        waketoneButton.setOnClickListener(new View.OnClickListener() {
	            @Override
				public void onClick(View v) {
	            	waketoneDialog.show();	 
	            }
	        });
	        
	        
	        
	        Button rangeButton = (Button) findViewById(R.id.set_range_button);
	        rangeButton.setOnClickListener(new View.OnClickListener() {
	            @Override
				public void onClick(View v) {
	            	
	    			final Dialog dialog = new Dialog(LowtimeSettingIntent.this);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
	    			dialog.setContentView(R.layout.range_select_dialog);
	     	     
	    			Button dialogButton = (Button) dialog.findViewById(R.id.set_range);
	    	        rangeSpinner = (Spinner) dialog.findViewById(R.id.range_spinner);
	    	        if(settings.getRange() > 0)
	    	        	rangeSpinner.setSelection(minuteOptionsLookup.get(settings.getRange()));
	    	        
	    			dialogButton.setOnClickListener(new View.OnClickListener() {
	    				@Override
	    				public void onClick(View v) {
	    					int range = Integer.parseInt(rangeSpinner.getSelectedItem().toString());
		                    settings.setRange(range);
		                    settings.commit();
		                    rangeText.setText(rangeSpinner.getSelectedItem().toString() + " Minutes");
		                    rangeText.setBackgroundColor(Color.TRANSPARENT);
	    					dialog.dismiss();
	    				}
	    			});
	     
	    			dialog.show();
	            }
	        });
	        
	        

	        Button snoozeButton = (Button) findViewById(R.id.set_snooze_duration_button);
	        snoozeButton.setOnClickListener(new View.OnClickListener() {
	            @Override
				public void onClick(View v) {
	            	
	    			final Dialog dialog = new Dialog(LowtimeSettingIntent.this);
	    			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
	    			dialog.setContentView(R.layout.snooze_select_dialog);
	     	     
	    			Button dialogButton = (Button) dialog.findViewById(R.id.set_snooze_duration);
	    			snoozeDurationSpinner = (Spinner) dialog.findViewById(R.id.snooze_duration_spinner);
	    			if(settings.getSnoozeDuration() > 0)
	    				snoozeDurationSpinner.setSelection(minuteOptionsLookup.get(settings.getSnoozeDuration()));
	    	        
	    			
	    			dialogButton.setOnClickListener(new View.OnClickListener() {
	    				@Override
	    				public void onClick(View v) {
	    					int snoozeDuration = Integer.parseInt(snoozeDurationSpinner.getSelectedItem().toString());
		                    settings.setSnoozeDuration(snoozeDuration);
		                    settings.commit();
		                    snoozeDurationText.setText(snoozeDurationSpinner.getSelectedItem().toString() + " Minutes");
		                    snoozeDurationText.setBackgroundColor(Color.TRANSPARENT);
		                    
	    					dialog.dismiss();
	    				}
	    			});
	    			dialog.show();
	            }
	        });
	        
	        
	        
	        Button setLowtimeButton = (Button) findViewById(R.id.save);
	        setLowtimeButton.setOnClickListener(new View.OnClickListener() {
	            @Override
				public void onClick(View v) {
	            	
	            	Integer range = settings.getRange();
	            	Integer snooze = settings.getSnoozeDuration();
	            	
	            	if(
//	            			!settings.getWaketone().equals("") 
//	            			&& 
	            			range != null && snooze != null){
	            			

	                    int lowtimeHour = timePicker.getCurrentHour();
	                    int lowtimeMinute = timePicker.getCurrentMinute();
	                    
	                    settings.setHour(lowtimeHour);
	                    settings.setMinutes(lowtimeMinute);
	                    
	                    settings.setActive(true);
	                    settings.commit();
	                    
		                Intent i = new Intent(LowtimeSettingIntent.this, HomeIntent.class);
		                startActivity(i);
		                
	            	}else{

	            		AlertDialog.Builder builder = new AlertDialog.Builder(LowtimeSettingIntent.this);

	            		builder.setMessage("Make sure that \"Wakeup Tone\", \"Range\" & \"Snooze Duration\" are set")
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
	            @Override
				public void onClick(View v) {
	                Intent i = new Intent(LowtimeSettingIntent.this, HomeIntent.class);
	                startActivity(i);
	            }
	        });
	        

	        
	        reinitializeView();
            
	
	        addListenerOnSpinnerItemSelection();
	        
	    }catch (Exception e){
	    	e.printStackTrace();
	    }        
    }

    
    
    private Dialog createWaketoneDialog(){
    	
    	final Dialog dialog = new Dialog(LowtimeSettingIntent.this);
		dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE); 
		dialog.setContentView(R.layout.waketone_select_dialog);
	     
		final Button dialogButton = (Button) dialog.findViewById(R.id.set_waketone);
    	
    	int ringtoneCount = 0; 	
    	
    	TableLayout tableLayout = (TableLayout) dialog.findViewById(R.id.waketone_select_table);
    	checkBoxes = new ArrayList<CheckBox>();    

        RingtoneManager ringtoneMgr = new RingtoneManager(getApplicationContext());
    	ringtoneMgr.setType(RingtoneManager.TYPE_ALARM);
    	Cursor alarmsCursor = ringtoneMgr.getCursor();
    	
    	
    	while(!alarmsCursor.isAfterLast() && alarmsCursor.moveToNext()) {

    		Log.i("LOWTIME -->> ", "ringtoneCount : " + ringtoneCount);
    		
    		int currentPosition = alarmsCursor.getPosition();
    		int elementsId = ringtoneCount + 2 + (ringtoneCount * 5);
    		
    		TableRow row = new TableRow(LowtimeSettingIntent.this); 
    		row.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            
    	    String ringtoneTitle = ringtoneMgr.getRingtone(currentPosition).getTitle(LowtimeSettingIntent.this);
    	    
    	    CheckBox toneCheckbox = new CheckBox(LowtimeSettingIntent.this);
    	    toneCheckbox.setId(elementsId);
            
            if(settings.getWaketone().equals(ringtoneTitle)){
            	toneCheckbox.setChecked(true);
            }
            
            toneCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                @Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                    	processCheckBoxClick(buttonView);
                        if(ringtone != null && ringtone.isPlaying())ringtone.stop();
                    }
                }   
            });

            checkBoxes.add(toneCheckbox);
            row.addView(toneCheckbox, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	    
            TextView title = new TextView(LowtimeSettingIntent.this);
    	    title.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    	    title.setText(ringtoneTitle);
    	    
    	    
    	    row.addView(title, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	    
    	    Button preview = new Button(LowtimeSettingIntent.this);
            preview.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            
            
            preview.setText("Preview");
            preview.setId(elementsId);
            preview.setOnClickListener(new View.OnClickListener() {
                @Override
				public void onClick(View v) {
                 	int id = v.getId();
                	if(ringtone != null && ringtone.isPlaying())ringtone.stop();
        	    	ringtone = RingtoneManager.getRingtone(LowtimeSettingIntent.this, (Uri) lookup.get(id).get(URI));
        	    	ringtone.play();
                }
            });
            
            row.addView(preview);
            
            lookup.put(elementsId, new HashMap<String, Object>());
            lookup.get(elementsId).put(TONE, ringtoneMgr.getRingtone(currentPosition).getTitle(LowtimeSettingIntent.this));
            lookup.get(elementsId).put(URI, ringtoneMgr.getRingtoneUri(currentPosition));
            
    	    ringtoneCount++;
    	    tableLayout.addView(row);
    	
    	}
    	
    	alarmsCursor.close();  
    	
    	dialogButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				CheckBox checkbox = getCheckedCheckBox();
				if(checkbox == null){
					
            		AlertDialog.Builder builder = new AlertDialog.Builder(LowtimeSettingIntent.this);

            		builder.setMessage("Please select a \"Wakeup Tone\"")
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
					
				}else{
	            	int id = checkbox.getId();
	                String tone = lookup.get(id).get(TONE).toString();
	                Uri uri = (Uri) lookup.get(id).get(URI);

	                waketoneText.setText(tone);

	                settings.setWaketoneUri(uri.toString());
	                settings.setWaketone(tone);
	                settings.commit();
	                
	                if(ringtone != null && ringtone.isPlaying())ringtone.stop();
	            	
					dialog.dismiss();
					
				}

                                    
			}
		});
    	
    	return dialog;
    	
    }
    
    
    private void processCheckBoxClick(CompoundButton checkboxView){
        for (CheckBox checkbox : checkBoxes){
            if (checkbox != checkboxView ) checkbox.setChecked(false);
        }
    }
    
    
    private CheckBox getCheckedCheckBox(){
    	CheckBox checkedCheckBox = null;
        for (CheckBox checkbox : checkBoxes){
            if (checkbox.isChecked()){
            	checkedCheckBox = checkbox;
            }
        }	
        return checkedCheckBox;
    }
    
    
    private void setupOptionsLookup(){
    	minuteOptionsLookup = new HashMap<Integer, Integer>();
    	minuteOptionsLookup.put(5, 0);
    	minuteOptionsLookup.put(10, 1);
    	minuteOptionsLookup.put(15, 2);
    	minuteOptionsLookup.put(20, 3);
    	minuteOptionsLookup.put(25, 4);
    	minuteOptionsLookup.put(30, 5);
    	minuteOptionsLookup.put(35, 6);
    	minuteOptionsLookup.put(40, 7);
    	minuteOptionsLookup.put(45, 8);
    	minuteOptionsLookup.put(50, 9);
    	minuteOptionsLookup.put(60, 10);
    	minuteOptionsLookup.put(90, 11);
    }
    
    
    public void addListenerOnSpinnerItemSelection() {
    	rangeSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener(settings.getSettings()));
    }
    

    
    
    @Override
    protected void onStart() {
        super.onStart();  
    	settings.reinitialize(getSharedPreferences(LOWTIME_SETTINGS, 0));
        reinitializeView();
    }

    private void reinitializeView(){
    	
        if(settings.settingsSet()){
        	timePicker.setCurrentHour(settings.getHour());
        	timePicker.setCurrentMinute(settings.getMinutes());
        	System.out.println("SETTINGS SETUP -> SET SPINNER " + settings.getRange() + "  :  " + minuteOptionsLookup.get(settings.getRange()));
        }
        
        if(!settings.getWaketone().equals("")){
        	waketoneText.setText(settings.getWaketone());
        	waketoneText.setBackgroundColor(Color.TRANSPARENT);
        }
        
        
        Integer range = settings.getRange();
        if(range != null && rangeText != null && range > 0){
        	rangeText.setText(range.toString() + " Minutes");
        	rangeText.setBackgroundColor(Color.TRANSPARENT);
        }
        

        Integer snooze = settings.getSnoozeDuration();
        if(snooze != null && snoozeDurationText != null && snooze > 0){
        	snoozeDurationText.setText(snooze.toString() + " Minutes");
        	snoozeDurationText.setBackgroundColor(Color.TRANSPARENT);
        }
        
    }
    
    
    @Override
    protected void onStop() {
        super.onStop();  
    }  
    

    private void showSettings(){
        Intent i = new Intent(LowtimeSettingIntent.this, LowtimeSettingIntent.class);
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
