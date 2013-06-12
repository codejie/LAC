package jie.android.lac.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import jie.android.lac.app.Configuration;
import jie.android.lac.data.Constants.SERVICE_STATE;
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
	
//	private SharedPreferences prefs = null;
//	private String dataPath = null;
	
	private DBAccess dbAccess = null;
	private Dictionary dictionary = null;
	
	private Callback appCallback = null;
	
	private boolean isReady = false;

	private AsyncTask<Void, Void, Integer> initDataTask = new AsyncTask<Void, Void, Integer>() {

		@Override
		protected Integer doInBackground(Void... params) {			
			postServiceStateNotify(SERVICE_STATE.DATA_INIT);
			if (!checkData()) {
				postServiceStateNotify(SERVICE_STATE.DATA_UNZIP);
				if (!unzipData()) {
					return -1;
				}
			}
			
			initDBAccess();
			postServiceStateNotify(SERVICE_STATE.DICTIONARY_INIT);
			if (!initDictionary()) {
				return -1;
			}
		
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			postServiceStateNotify(result == 0 ? SERVICE_STATE.DATA_READY : SERVICE_STATE.DATA_LOAD_FAIL);
			isReady = (result == 0);
		}
		
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		
		Log.d(Tag, "onBind()");
		
		serviceStub = new ServiceStub(this);
		return serviceStub;
	}
	

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(Tag, "onUnbind()");
		return super.onUnbind(intent);
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
		Log.d(Tag, "set app callback : " + id);
		appCallback = callback;
		
		if (!isReady) {
			initDataTask.execute();
		} else if (isReady && appCallback != null) {
			postServiceStateNotify(SERVICE_STATE.DATA_READY);
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
//		prefs  = getSharedPreferences("LAC", Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
//		Log.d(Tag, "service create : " + prefs.getInt(Configuration.PREFS_DATA_LOCATION, 12));
//	
//		dataPath = this.getDatabasePath(DBAccess.FILE).getParent() + File.separator;
//		prefs.edit().putString(Configuration.PREFS_DATA_FOLDER, dataPath).commit();
//		initDataTask.execute();
	}
	
//	private void updateConfiguration() {
//		prefs  = getSharedPreferences("LAC", Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
////		Log.d(Tag, "service create : " + prefs.getInt(Configuration.PREFS_DATA_LOCATION, 12));	
//		dataPath = this.getDatabasePath(DBAccess.FILE).getParent() + File.separator;
//		Log.d(Tag, "service datapath : " + dataPath);
//		prefs.edit().putString(Configuration.PREFS_DATA_FOLDER, dataPath).commit();
//	}

	@Override
	public void onDestroy() {
		Log.d(Tag, "onDestroy()");
		releaseDictionary();
		releaseDBAccess();
		super.onDestroy();
	}

	private void initDBAccess() {
		
//		updateConfiguration();
		
		dbAccess = new DBAccess(this, this.getDatabasePath(DBAccess.FILE).getParent() + File.separator + DBAccess.FILE);
	}

	private void releaseDBAccess() {
		if (dbAccess != null) {
			dbAccess.close();
		}
	}
	
	private boolean initDictionary() {		
		dictionary = new Dictionary(dbAccess, this.getDatabasePath(DBAccess.FILE).getParent());
		return dictionary.load();
	}
	
	private void releaseDictionary() {
		if (dictionary != null) {
			dictionary.close();
		}
	}

	private boolean checkData() {
		return getDatabasePath(DBAccess.FILE).exists();
	}
	
	private boolean unzipData() {
		
		File target = this.getDatabasePath(DBAccess.FILE).getParentFile();		

		if (!target.exists()) {
			target.mkdir();
		}
		
		InputStream input;
		try {
			//lac2.zip
			input = this.getAssets().open("lac2.zip");
			AssetsHelper.UnzipTo(input, target.getAbsolutePath(), null);
			//httpd.zip
			input = this.getAssets().open("httpd.zip");
			AssetsHelper.UnzipTo(input, this.getFilesDir() + File.separator + Configuration.SUB_FOLDER_HTTPD, null);
		} catch (IOException e) {
			e.printStackTrace();			
			return false;
		}
		
		return true;
	}

	private void postServiceStateNotify(int state) {
		if (appCallback != null) {
			try {
				Log.d(Tag, "postServiceStateNotify() - state : " + state);
				appCallback.onServiceState(state);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}		
	}

}
