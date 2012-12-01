package org.mij.suiga;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class LowtimeService extends Service {
	private static String TAG = "Lowtime";
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        Log.d(TAG, "LowtimeService started");
       // this.stopSelf();
    }
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.d(TAG, "LowtimeService destroyed");
    }
}
