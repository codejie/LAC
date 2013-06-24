package jie.android.lac.service;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBImportHelper {

	private DBAccess dbAccess = null;
	private String importFile = null;
	
	private SQLiteDatabase importDb = null;
	
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
	
	public boolean importData() {
		//dictionary
		if (!importDictInfo())
			return false;
		//data
		return true;
	}

	private boolean importDictInfo() {
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
					
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
		}
		return true;
	}
	
}
