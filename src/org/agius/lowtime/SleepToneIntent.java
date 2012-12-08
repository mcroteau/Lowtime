package org.agius.lowtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.agius.lowtime.R;
import org.agius.lowtime.R.id;
import org.agius.lowtime.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SleepToneIntent extends Activity{

	private Map<Integer, Map<String, Object>> lookup = new HashMap<Integer, Map<String, Object>>();
	
	private LinearLayout layout;
	private List<CheckBox> radioButtons; 
	
	private static String TONE = "tone";
	private static String URI = "uri";

	private SharedPreferences settings;
	
	private static Ringtone ringtone;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sleeptone);

        settings = getSharedPreferences("lowtimeSettings", 0);
        
        RingtoneManager ringtoneMgr = new RingtoneManager(getApplicationContext());
    	ringtoneMgr.setType(RingtoneManager.TYPE_ALARM);
    	Cursor alarmsCursor = ringtoneMgr.getCursor();
    	
    	layout = (LinearLayout) findViewById(R.id.sleeptone);
        layout.setOrientation(LinearLayout.VERTICAL);

       
    	int count = 0;
    	radioButtons = new ArrayList<CheckBox>();
    	
    	while(!alarmsCursor.isAfterLast() && alarmsCursor.moveToNext()) {

    		int currentPosition = alarmsCursor.getPosition();
    		int elementsId = count + 2 + (count * 5);
    		
            LinearLayout row = new LinearLayout(getApplicationContext());
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    	    row.setOrientation(LinearLayout.HORIZONTAL);
    	    
    	    CheckBox setToneButton = new CheckBox(this);
            setToneButton.setId(elementsId);
            
            setToneButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                    	System.out.println("\n\nSleepToneIntent\n\n");
                    	processRadioButtonClick(buttonView);
                    	int id = buttonView.getId();
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);

                        String tone = lookup.get(id).get(TONE).toString();
                        Uri uri = (Uri) lookup.get(id).get(URI);

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("sleeptoneUri", uri.toString());
                        editor.putString("sleeptone", tone);
                        editor.commit();

                    	if(ringtone != null)ringtone.stop();
                        
                        startActivity(i);
                    }
                }   
            });
            radioButtons.add((CheckBox) setToneButton);
    	    row.addView(setToneButton, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    	    
            TextView title = new TextView(this);
    	    title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    	    title.setText(ringtoneMgr.getRingtone(currentPosition).getTitle(this));
    	    row.addView(title, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    	    
    	    Button preview = new Button(this);
            preview.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            preview.setText("Preview");
            preview.setId(elementsId);
            preview.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                	int id = v.getId();
                	if(ringtone != null)ringtone.stop();
        	    	ringtone = RingtoneManager.getRingtone(getApplicationContext(), (Uri) lookup.get(id).get(URI));
        	    	ringtone.play();
                }
            });
            
            row.addView(preview);
            lookup.put(elementsId, new HashMap<String, Object>());
            lookup.get(elementsId).put(TONE, ringtoneMgr.getRingtone(currentPosition).getTitle(this));
            lookup.get(elementsId).put(URI, ringtoneMgr.getRingtoneUri(currentPosition));

    	    count++;
    	    layout.addView(row);
    	    
    	}
    	
    	alarmsCursor.close();  
    	
    }

    
    
    private void processRadioButtonClick(CompoundButton checkboxView){
        for (CheckBox checkbox : radioButtons){
            if (checkbox != checkboxView ) checkbox.setChecked(false);
        }
    }
	
}
