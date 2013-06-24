package jie.android.lac.service;

import java.util.ArrayList;

import jie.android.lac.service.aidl.ImportDatabaseListener;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.RemoteException;

public class DBImportHelper {

	private DBAccess dbAccess = null;
	private String importFile = null;
	
	private SQLiteDatabase importDb = null;	
	private ImportDatabaseListener importListener = null;
	
	public DBImportHelper(DBAccess dbAccess, final String importFile) {
		this.dbAccess = dbAccess;
		this.importFile = importFile;
	}
	
	public boolean init() {
		try {
			importDb = SQLiteDatabase.openDatabase(importFile, null, SQLiteDatabase.OPEN_READONLY);
		} catch (SQLException e) {
			return false;
		}
		return true;
	}
	
	public boolean importData(ImportDatabaseListener listener) throws RemoteException {
		importListener = listener;
		//dictionary
		if (!importDictInfo())
			return false;
		//data
		if (!importWordData()) {
			
		}
		return true;
	}

	private boolean importDictInfo() throws RemoteException {
		Cursor cursor = importDb.query("dict_info", null, null, null, null, null, null);
		try {
			if (cursor != null && cursor.moveToFirst()) {
				do {
					ContentValues values = new ContentValues();
					values.put("idx", cursor.getInt(0));
					values.put("state", cursor.getInt(1));
					values.put("title", cursor.getString(2));
					values.put("file", cursor.getString(3));
					values.put("revision", cursor.getInt(4));
					values.put("offset", cursor.getInt(5));
					values.put("d_decoder", cursor.getInt(6));
					values.put("x_decoder", cursor.getInt(7));
					values.put("source", cursor.getString(8));
					values.put("target", cursor.getString(9));
					values.put("owner", cursor.getString(10));

					dbAccess.importDictInfo(values);
					
					if (importListener != null) {
						importListener.onImported(cursor.getInt(0), "Dictionary : " + cursor.getShort(2));
					}
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
		}
		return true;
	}

	private boolean importWordData() {
		// TODO Auto-generated method stub
		return false;
	}	
}
