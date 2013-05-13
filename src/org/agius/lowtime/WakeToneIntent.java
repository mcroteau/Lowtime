package org.agius.lowtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.agius.lowtime.R;
import org.agius.lowtime.domain.LowtimeSettings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import static org.agius.lowtime.LowtimeConstants.*;

public class WakeToneIntent extends Activity{

	@SuppressLint("UseSparseArrays")
	private Map<Integer, Map<String, Object>> lookup = new HashMap<Integer, Map<String, Object>>();
	
//	private LinearLayout layout;
	private TableLayout layout;
	
	private List<CheckBox> radioButtons; 

	private static Ringtone ringtone;
	private LowtimeSettings settings;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waketone);
        
        settings = new LowtimeSettings(getSharedPreferences(LOWTIME_SETTINGS, 0));
        
        RingtoneManager ringtoneMgr = new RingtoneManager(getApplicationContext());
    	ringtoneMgr.setType(RingtoneManager.TYPE_ALARM);
    	Cursor alarmsCursor = ringtoneMgr.getCursor();
    	
//    	layout = (LinearLayout) findViewById(R.id.waketone);
    	layout = (TableLayout) findViewById(R.id.waketone);
        layout.setOrientation(LinearLayout.VERTICAL);

    	int count = 0;
    	radioButtons = new ArrayList<CheckBox>();    	
    	
    	
    	while(!alarmsCursor.isAfterLast() && alarmsCursor.moveToNext()) {

    		int currentPosition = alarmsCursor.getPosition();
    		int elementsId = count + 2 + (count * 5);
    		
//          LinearLayout row = new LinearLayout(this);
            
    		TableRow trow = new TableRow(this); 
            trow.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            
//          row.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
//    	    row.setOrientation(LinearLayout.HORIZONTAL);
    	  
            
    	    String ringtoneTitle = ringtoneMgr.getRingtone(currentPosition).getTitle(this);
    	    
    	    
    	    CheckBox setToneButton = new CheckBox(this);
            setToneButton.setId(elementsId);
            
            if(settings.getWaketone().equals(ringtoneTitle)){
            	setToneButton.setChecked(true);
            }
            
            setToneButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                @Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {

                    	processRadioButtonClick(buttonView);
                    	int id = buttonView.getId();
                        Intent i = new Intent(getApplicationContext(), LowtimeSettingIntent.class);

                        String tone = lookup.get(id).get(TONE).toString();
                        Uri uri = (Uri) lookup.get(id).get(URI);

                        settings.setWaketoneUri(uri.toString());
                        settings.setWaketone(tone);
                        settings.commit();
                        if(ringtone != null && ringtone.isPlaying())ringtone.stop();
                    	
                        startActivity(i);
                    }
                }   
            });
            
            radioButtons.add(setToneButton);
//    	    row.addView(setToneButton, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            trow.addView(setToneButton, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	    
            TextView title = new TextView(this);
    	    title.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    	    title.setText(ringtoneTitle);
    	    
//    	    row.addView(title, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	    trow.addView(title, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	    
    	    
    	    Button preview = new Button(this);
            preview.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            
            preview.setText("Preview");
            preview.setId(elementsId);
            preview.setOnClickListener(new View.OnClickListener() {
                @Override
				public void onClick(View v) {
                	int id = v.getId();
                	if(ringtone != null && ringtone.isPlaying())ringtone.stop();
        	    	ringtone = RingtoneManager.getRingtone(getApplicationContext(), (Uri) lookup.get(id).get(URI));
        	    	ringtone.play();
                }
            });
            
//            row.addView(preview);
            trow.addView(preview);
            
            lookup.put(elementsId, new HashMap<String, Object>());
            lookup.get(elementsId).put(TONE, ringtoneMgr.getRingtone(currentPosition).getTitle(this));
            lookup.get(elementsId).put(URI, ringtoneMgr.getRingtoneUri(currentPosition));
            
    	    count++;
    	    layout.addView(trow);
    	    
        
    	}
    	
    	alarmsCursor.close();  
    	
    }

    
    private void processRadioButtonClick(CompoundButton checkboxView){
        for (CheckBox checkbox : radioButtons){
            if (checkbox != checkboxView ) checkbox.setChecked(false);
        }
    }
    
    @Override
    protected void onStart() {
        super.onStart();  
    	settings.reinitialize(getSharedPreferences(LOWTIME_SETTINGS, 0));
    }
    
    @Override
    protected void onRestart() {
        super.onRestart();  
        settings.reinitialize(getSharedPreferences(LOWTIME_SETTINGS, 0));
    }
	
}
