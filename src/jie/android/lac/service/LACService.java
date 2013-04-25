package jie.android.lac.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import jie.android.lac.app.Configuration;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class LACService extends Service {

	private static final String Tag = LACService.class.getName();

	private ServiceStub binder = null;
	
	private SharedPreferences prefs = null;

	private DBAccess dbAccess = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		binder = new ServiceStub(dbAccess);
		// TODO Auto-generated method stub
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		prefs  = getSharedPreferences("LAC", Context.MODE_PRIVATE);
		Log.d(Tag, "service create : " + prefs.getInt(Configuration.PREFS_DATA_LOCATION, 0));
	
		initDBAccess();
	}

	@Override
	public void onDestroy() {
		releaseDBAccess();
		super.onDestroy();
	}

	private void initDBAccess() {
		int dataFlag = prefs.getInt(Configuration.PREFS_DATA_LOCATION, 0);
		
		String dbfile = initData(dataFlag);
		
		dbAccess = new DBAccess(this, dbfile);
	}

	private void releaseDBAccess() {
		// TODO Auto-generated method stub
		
	}
	
	private final String initData(int flag) {

		File target = this.getDatabasePath(DBAccess.FILE);
		if (!target.exists()) {
			target = target.getParentFile();
	
			if (!target.exists()) {
				target.mkdir();
			}
			
			InputStream input;
			try {
				input = this.getAssets().open("lac2.zip");
				AssetsHelper.UnzipTo(input, target.getAbsolutePath());			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return target.getAbsolutePath();
	}	
	

}
