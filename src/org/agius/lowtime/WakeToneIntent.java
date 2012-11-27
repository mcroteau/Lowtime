package org.agius.lowtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
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

public class WakeToneIntent extends Activity{

	private Map<Integer, Uri> uriLookup = new HashMap<Integer, Uri>();
	
	private LinearLayout layout;
	private List<CheckBox> radioButtons; 
	
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waketone);
        
        RingtoneManager ringtoneMgr = new RingtoneManager(this);
    	ringtoneMgr.setType(RingtoneManager.TYPE_ALARM);
    	Cursor alarmsCursor = ringtoneMgr.getCursor();
    	
    	layout = (LinearLayout) findViewById(R.id.waketone);
        layout.setOrientation(LinearLayout.VERTICAL);

    	int count = 0;
    	
    	radioButtons = new ArrayList<CheckBox>();
    	
    	while(!alarmsCursor.isAfterLast() && alarmsCursor.moveToNext()) {

    		int currentPosition = alarmsCursor.getPosition();

    		int elementsId = count + 2 + (count * 5);
    		
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    	    row.setOrientation(LinearLayout.HORIZONTAL);
    	  
    	    CheckBox setToneButton = new CheckBox(this);
            setToneButton.setId(elementsId);
            
            setToneButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                    	processRadioButtonClick(buttonView);
                    	int id = buttonView.getId();
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        String uri = uriLookup.get(id).toString();
                        i.putExtra("waketone", uri);
                        startActivity(i);
                    }
                }   
            });
            radioButtons.add((CheckBox) setToneButton);
    	    row.addView(setToneButton, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	    
    	    
            TextView title = new TextView(this);
    	    title.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    	    title.setText(ringtoneMgr.getRingtone(currentPosition).getTitle(this));
    	    row.addView(title, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	    
    	    
    	    Button preview = new Button(this);
            preview.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            preview.setText("Preview");
            preview.setId(elementsId);
            preview.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                	int id = v.getId();
        	    	Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), uriLookup.get(id));
        	    	r.play();
                }
            });
            
            row.addView(preview);
    	    uriLookup.put(elementsId, ringtoneMgr.getRingtoneUri(currentPosition));
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
