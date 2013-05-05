package jie.android.lac.service;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.os.RemoteException;
import jie.android.lac.data.Word;
import jie.android.lac.service.aidl.Access;;

public class ServiceStub extends Access.Stub {
	
	private DBAccess dbAccess = null;
			
	public ServiceStub(DBAccess dbAccess) {
		this.dbAccess = dbAccess;
	}
	
	@Override
	public int checkState() {
		return dbAccess.getState();
		//return 100;
	}

	@Override
	public List<Word.Info> queryWordInfo(final String condition, int offset, int limit) throws RemoteException {
		
		ArrayList<Word.Info> ret = new ArrayList<Word.Info>();
		
		Cursor cursor = dbAccess.getWord(condition, offset, limit);		
		if (cursor != null && cursor.moveToFirst()) {
			do {
				Word.Info data = new Word.Info(cursor.getInt(0), cursor.getString(1));
				
				ret.add(data);
				
			} while (cursor.moveToNext());
		}	

		return ret;
	}

	@Override
	public String queryWordResult(int index) throws RemoteException {
		return null;
	}

	@Override
	public void setDictionaryOrder(int index, int order) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enableDictionary(int index, boolean enable)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}
}
