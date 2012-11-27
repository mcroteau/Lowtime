package org.agius.lowtime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity{

	
	private String sleeptone;
	private String waketone;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
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
        
        
        if(savedInstanceState != null){
            sleeptone = savedInstanceState.getString("sleeptone");
            waketone = savedInstanceState.getString("waketone");
            if(sleeptone != null && !sleeptone.equals("")){
                TextView sleetoneView = (TextView) findViewById(R.id.sleeptone);
                sleetoneView.setText(sleeptone);
            }
           
            if(waketone != null && !waketone.equals("")){
                TextView waketoneView = (TextView) findViewById(R.id.waketone);
                waketoneView.setText(waketone);
            }
            
        }
        
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sleeptone = extras.getString("sleeptone");
            waketone = extras.getString("waketone");
            
            if(sleeptone != null && !sleeptone.equals("")){
                TextView sleetoneView = (TextView) findViewById(R.id.sleeptone);
                sleetoneView.setText(sleeptone);
            }
           
            if(waketone != null && !waketone.equals("")){
                TextView waketoneView = (TextView) findViewById(R.id.waketone);
                waketoneView.setText(waketone);
            }
        }
        
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    
}
