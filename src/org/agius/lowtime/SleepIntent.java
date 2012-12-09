package org.agius.lowtime;

import org.agius.lowtime.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class SleepIntent extends Activity {

	static boolean active = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sleep);
        
        Button offButton = (Button) findViewById(R.id.turnoff);
        offButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopService(new Intent(SleepIntent.this, TheService.class));
            }
        });
        
        Button snoozeButton = (Button) findViewById(R.id.snooze);
        snoozeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
        
    }
    
    
    @Override
    public void onStart(){
    	super.onStart();
    	active = true;
    }
    
    
    @Override
    public void onStop(){
    	super.onStop();
    	active = false;
    }
    
    
}
