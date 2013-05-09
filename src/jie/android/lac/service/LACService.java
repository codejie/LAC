package jie.android.lac.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import jie.android.lac.app.Configuration;
import jie.android.lac.data.Dictionary;
import jie.android.lac.service.aidl.Callback;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class LACService extends Service {

	private static final String Tag = LACService.class.getSimpleName();

	private ServiceStub serviceStub = null;
	
	private SharedPreferences prefs = null;
	private String dataPath = null;
	
	private DBAccess dbAccess = null;
	private Dictionary dictionary = null;
	
	private Callback appCallback = null;

	private AsyncTask<Void, Void, Void> initDataTask = new AsyncTask<Void, Void, Void>() {

		@Override
		protected Void doInBackground(Void... params) {
			initDBAccess();
			initDictionary();
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			
			if (appCallback != null) {
				try {
					appCallback.onServiceStartup(0);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			
			super.onPostExecute(result);
		}
		
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		serviceStub = new ServiceStub(this);
		// TODO Auto-generated method stub
		return serviceStub;
	}

	public final DBAccess getDBAccess() {
		return dbAccess;
	}
	
	public final Dictionary getDictionary() {
		return dictionary;
	}
	
	public final Callback getAppCallback() {
		return appCallback;
	}
	
	public void setAppCallback(int id, Callback callback) {
		appCallback = callback;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		prefs  = getSharedPreferences("LAC", Context.MODE_PRIVATE);
		Log.d(Tag, "service create : " + prefs.getInt(Configuration.PREFS_DATA_LOCATION, 0));
	
		dataPath = this.getDatabasePath(DBAccess.FILE).getParent() + File.separator;

		initDataTask.execute();
		
//		initDBAccess();
//		initDictionary();
	}

	@Override
	public void onDestroy() {
		releaseDictionary();
		releaseDBAccess();
		super.onDestroy();
	}

	private void initDBAccess() {
		int dataFlag = prefs.getInt(Configuration.PREFS_DATA_LOCATION, 0);
		
		String dbfile = initData(dataFlag);
		
		dbAccess = new DBAccess(this, dbfile);
	}

	private void releaseDBAccess() {
		if (dbAccess != null) {
			dbAccess.close();
		}
	}
	
	private void initDictionary() {
		dictionary = new Dictionary(dbAccess, dataPath);
		dictionary.load();
	}
	
	private void releaseDictionary() {
		if (dictionary != null) {
			dictionary.close();
		}
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
				AssetsHelper.UnzipTo(input, target.getAbsolutePath(), null);			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return target.getAbsolutePath();
	}
	
	

}
