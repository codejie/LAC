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

		return true;
	}

	private boolean importDictInfo() throws RemoteException {
		Cursor cursor = importDb.query("dict_info", null, null, null, null, null, null);
		try {
			if (cursor != null && cursor.moveToFirst()) {
				dbAccess.beginTransaction();
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

					dbAccess.beginTransaction();

					Long rowid = dbAccess.importDictInfo(values);
					if (importListener != null) {
						importListener.onImported(rowid.intValue(), "Dictionary Info : " + cursor.getInt(2));
					}
					importBlockData(cursor.getInt(0));
					if (importListener != null) {
						importListener.onImported(rowid.intValue(), "Dictionary Block Info complete.");
					}

					importWordData(cursor.getInt(0));
					if (importListener != null) {
						importListener.onImported(rowid.intValue(), "Dictionary Word Info complete.");
					}
					
					dbAccess.endTransaction(true);
					
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
		}
		return true;
	}

	private boolean importBlockData(int dictid) throws RemoteException {
		if (!dbAccess.createBlockDataTable(dictid))
			return false;
		
		Cursor cursor = importDb.query("block_info_" + dictid, null, null, null, null, null, null);
		try {
			if (cursor != null && cursor.moveToFirst()) {

				do {
					ContentValues values = new ContentValues();
					values.put("idx", cursor.getInt(0));
					values.put("offset", cursor.getInt(1));
					values.put("length", cursor.getInt(2));
					values.put("start", cursor.getInt(3));
					values.put("end", cursor.getInt(4));
					
					if (dbAccess.importBlockData(dictid, values) == -1) {
						if (importListener != null) {
							importListener.onImported(-1, "Dictionary Block Info failed.");
						}						
					}
					
				} while (cursor.moveToNext());
			}
			
		} finally {
			cursor.close();
		}
		
		return true;
	}

	private boolean importWordData(int dictid) {
		
		try {
			
		} finally {
			
		}
		
		return false;
	}	
}
