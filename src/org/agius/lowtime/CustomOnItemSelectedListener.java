package org.agius.lowtime;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;
 
public class CustomOnItemSelectedListener implements OnItemSelectedListener {

	private SharedPreferences settings;
	
	public CustomOnItemSelectedListener(SharedPreferences settings){
    	this.settings = settings;
	}
	
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//		Toast.makeText(parent.getContext(), 
//				"OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
//				Toast.LENGTH_SHORT).show();

        SharedPreferences.Editor editor = settings.edit();
        
        editor.putString("minutes", parent.getContext().toString());
        
	}
 
	
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}
 
}
