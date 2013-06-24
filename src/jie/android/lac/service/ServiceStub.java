package jie.android.lac.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.os.RemoteException;
import android.util.Log;
import jie.android.lac.data.Dictionary;
import jie.android.lac.data.Word;
import jie.android.lac.service.aidl.Access;
import jie.android.lac.service.aidl.Callback;
import jie.android.lac.service.aidl.ImportDatabaseListener;

public class ServiceStub extends Access.Stub {
	
	private LACService service = null;
			
	public ServiceStub(LACService service) {
		this.service = service;
	}
	
	@Override
	public int checkState() {
		return service.getDBAccess().getState();
		//return 100;
	}

	@Override
	public List<Word.Info> queryWordInfo(final String condition, int offset, int limit) throws RemoteException {
		
		ArrayList<Word.Info> ret = new ArrayList<Word.Info>();
		
		Cursor cursor = service.getDBAccess().getWord(condition, offset, limit);		
		if (cursor != null && cursor.moveToFirst()) {
			do {
				Word.Info data = new Word.Info(cursor.getInt(0), cursor.getString(1));
				
				ret.add(data);
				
			} while (cursor.moveToNext());
		}	

		return ret;
	}

	@Override
	public final Word.XmlResult queryWordXmlResult(int index) throws RemoteException {
		return service.getDictionary().getWordXmlResult(index);
	}

	@Override
	public List<Dictionary.SimpleInfo> queryDictionarySimpleInfo() throws RemoteException {
		return service.getDictionary().getSimpleInfo();
	}
	
	@Override
	public void setDictionaryOrder(int index, int order) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEnableDictionary(int index, boolean enable)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerCallback(int id, Callback callback)	throws RemoteException {
		service.setAppCallback(id, callback);
	}

	@Override
	public void unregisterCallback(int id) throws RemoteException {
		service.setAppCallback(id, null);
	}

	@Override
	public void ImportDBFile(String lfile, ImportDatabaseListener listener) throws RemoteException {
		
		Log.d("===", "service:ImportDBFile");
		if (listener != null) {
			listener.onStarted(lfile);
		}
		
		DBImportHelper helper = new DBImportHelper(service.getDBAccess(), lfile);
		if (helper.init()) {
			helper.importData(listener);
		} else {
			if (listener != null) {
				listener.onFailed("import init failed.");
			}
			return;
		}
		
		if (listener != null) {
			listener.onCompleted(100);
		}		
	}


}
