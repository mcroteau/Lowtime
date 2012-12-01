package org.agius.lowtime;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class LowtimeService extends Service {
	
	private static String TAG = "Lowtime";
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG, "LowtimeService started");
    }
    
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "LowtimeService destroyed");
    }
    
}
