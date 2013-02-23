package org.agius.lowtime;

import static org.agius.lowtime.LowtimeConstants.LOWTIME_SETTINGS;

import org.agius.lowtime.R;
import org.agius.lowtime.domain.LowtimeSettings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;


public class SleepIntent extends Activity {

	static boolean active = false;
	private LowtimeSettings settings;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sleep);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
			               + WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
			               + WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
			               + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        
        stopService(new Intent(SleepIntent.this, TheService.class));        
        settings = new LowtimeSettings(getSharedPreferences(LOWTIME_SETTINGS, 0));
        
//        Button offButton = (Button) findViewById(R.id.turnoff);
//        offButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                stopService(new Intent(SleepIntent.this, TheService.class));
//            	finish();
//            }
//        });
        
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        for(int m = 0; m < 3; m++){
            v.vibrate(500);
        }

     
        Button snoozeButton = (Button) findViewById(R.id.snooze);
        snoozeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopService(new Intent(SleepIntent.this, TheService.class));
                startService(new Intent(SleepIntent.this, TheService.class));
            	finish();
            }
        });
        
    }
    
    @Override
    protected void onRestart() {
        super.onRestart();  
        settings.reinitialize(getSharedPreferences(LOWTIME_SETTINGS, 0));
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
