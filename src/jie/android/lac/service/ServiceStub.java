package jie.android.lac.service;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.os.RemoteException;
import jie.android.lac.data.WordData;
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
	public List<WordData> queryWordData(final String condition, int offset, int limit) throws RemoteException {
		
		ArrayList<WordData> ret = new ArrayList<WordData>();
		
		Cursor cursor = dbAccess.getWord(condition, offset, limit);		
		if (cursor != null && cursor.moveToFirst()) {
			do {
				WordData data = new WordData(cursor.getInt(0), cursor.getString(1));
				
				ret.add(data);
				
			} while (cursor.moveToNext());
		}	

		return ret;
	}

	@Override
	public String queryWordResult(int index) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
}
