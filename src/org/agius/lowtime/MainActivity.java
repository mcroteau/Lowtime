package org.agius.lowtime;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity{

	//TODO : Might use in for preview
	//private String sleeptoneUri;
	//private String waketoneUri;
	
	private String sleeptone;
	private String waketone;
	
	private SharedPreferences settings;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = getSharedPreferences("lowtimeSettings", 0);
        
        Button sleeptoneButton = (Button) findViewById(R.id.sleeptone_button);
        sleeptoneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SleepToneIntent.class);
                startActivity(i);
            }
        });
        
        Button waketoneButton = (Button) findViewById(R.id.waketone_button);
        waketoneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, WakeToneIntent.class);
                startActivity(i);
            }
        });
        
        sleeptone = settings.getString("sleeptone", "");
        TextView sleetoneView = (TextView) findViewById(R.id.sleeptone);
        sleetoneView.setText(sleeptone);
        
        waketone = settings.getString("waketone", "");
        TextView waketoneView = (TextView) findViewById(R.id.waketone);
        waketoneView.setText(waketone);
        
        System.out.println("sleeptone : " + sleeptone + " waketone : " + waketone);
        
    }

    
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
      super.onSaveInstanceState(savedInstanceState);
      // Save UI state changes to the savedInstanceState.
      // This bundle will be passed to onCreate if the process is
      // killed and restarted.
      savedInstanceState.putString("waketone", waketone);
      savedInstanceState.putString("sleeptone", sleeptone);
    }    
    
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
      super.onRestoreInstanceState(savedInstanceState);
      // Restore UI state from the savedInstanceState.
      // This bundle has also been passed to onCreate.
      waketone = savedInstanceState.getString("waketone");
      sleeptone = savedInstanceState.getString("sleeptone");     
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    
}
