package jie.android.lac.service;

import jie.android.lac.app.Configuration;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

public class LACService extends Service {

	private static final String Tag = LACService.class.getName();

	private ServiceStub binder = null;
	
	private SharedPreferences prefs = null;

	@Override
	public IBinder onBind(Intent intent) {
		binder = new ServiceStub();
		// TODO Auto-generated method stub
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		prefs  = getSharedPreferences("LAC", Context.MODE_PRIVATE);
		Log.d(Tag, "service create : " + prefs.getInt(Configuration.PREFS_DATA_IN_CARD, 0));
		
		int dataFlag = prefs.getInt(Configuration.PREFS_DATA_IN_CARD, 0);
		if (dataFlag != 0) {
			initDBAccess(dataFlag == 1);
		} else {
			
		}
	}

	@Override
	public void onDestroy() {
		releaseDBAccess();
		super.onDestroy();
	}

	private void initDBAccess(boolean inCard) {
		// TODO Auto-generated method stub
		
	}
	
	private void releaseDBAccess() {
		// TODO Auto-generated method stub
		
	}

	
	

}
