package org.agius.lowtime;

import static org.agius.lowtime.LowtimeConstants.LOWTIME_SETTINGS;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.agius.lowtime.R;
import org.agius.lowtime.domain.LowtimeSettings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import org.agius.lowtime.custom.RobotoTextView;
import org.agius.lowtime.custom.RobotoButton;

import com.google.analytics.tracking.android.EasyTracker;


public class SleepIntent extends Activity {

	static boolean active = false;
	private LowtimeSettings settings;
	private RobotoTextView currentTime;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sleep);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
			               + WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
			               + WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
			               + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        
        stopService(new Intent(SleepIntent.this, LowtimeService.class));        
        settings = new LowtimeSettings(getSharedPreferences(LOWTIME_SETTINGS, 0));
        
        Calendar cal = Calendar.getInstance();
        Date datePre = cal.getTime();
        String formattedTime = new SimpleDateFormat("hh:mm a").format(datePre);	
        
        currentTime = (RobotoTextView) findViewById(R.id.current_time);
        currentTime.setText(formattedTime);
        
        
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        for(int m = 0; m < 3; m++){
            v.vibrate(500);
        }

     
        RobotoButton snoozeButton = (RobotoButton) findViewById(R.id.snooze);
        snoozeButton.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
                stopService(new Intent(SleepIntent.this, LowtimeService.class));
                startService(new Intent(SleepIntent.this, LowtimeService.class));
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
    protected void onStart() {
        super.onStart();  
        EasyTracker.getInstance().activityStart(this);
//    	active = true;
    }


    @Override
    protected void onStop(){
    	super.onStop();
        EasyTracker.getInstance().activityStop(this);
//    	active = false;
    }
          
}
