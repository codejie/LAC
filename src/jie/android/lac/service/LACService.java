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
	
	private SharedPreferences prefs = null;
	private String dataPath = null;
	
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
//			
//			super.onPostExecute(result);
		}
		
	};
	
	@Override
	public IBinder onBind(Intent intent) {				
		serviceStub = new ServiceStub(this);
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
		
		if (!isReady) {
			initDataTask.execute();
		} else if (isReady && appCallback != null) {
			postServiceStateNotify(SERVICE_STATE.DATA_READY);
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		prefs  = getSharedPreferences("LAC", Context.MODE_PRIVATE);
		Log.d(Tag, "service create : " + prefs.getInt(Configuration.PREFS_DATA_LOCATION, 0));
	
		dataPath = this.getDatabasePath(DBAccess.FILE).getParent() + File.separator;

//		initDataTask.execute();
	}

	@Override
	public void onDestroy() {
		releaseDictionary();
		releaseDBAccess();
		super.onDestroy();
	}

	private void initDBAccess() {
		dbAccess = new DBAccess(this, dataPath + DBAccess.FILE);
	}

	private void releaseDBAccess() {
		if (dbAccess != null) {
			dbAccess.close();
		}
	}
	
	private boolean initDictionary() {		
		dictionary = new Dictionary(dbAccess, dataPath);
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
			input = this.getAssets().open("lac2.zip");
			AssetsHelper.UnzipTo(input, target.getAbsolutePath(), null);			
		} catch (IOException e) {
			e.printStackTrace();			
			return false;
		}
		
		return true;
	}
	
//	private final String initData(int flag) {
//
//		File target = this.getDatabasePath(DBAccess.FILE);
//		if (!target.exists()) {
//			
//			postServiceStateNotify(SERVICE_STATE.DATA_UNZIP);
//			
//			File parent = target.getParentFile();
//	
//			if (!parent.exists()) {
//				parent.mkdir();
//			}
//			
//			InputStream input;
//			try {
//				input = this.getAssets().open("lac2.zip");
//				AssetsHelper.UnzipTo(input, parent.getAbsolutePath(), null);			
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//		return target.getAbsolutePath();
//	}

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
