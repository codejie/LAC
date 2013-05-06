package jie.android.lac.service;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAccess {// extends SQLiteOpenHelper {

	private static final String Tag = DBAccess.class.getName();
	
	public static String FILE	=	"lac2.db";
	private static int VERSION	=	1;
	
	private SQLiteDatabase db = null;
	
	public DBAccess(Context context, final String name) {
		Log.d(Tag, "open db : " + name);
		
		db = SQLiteDatabase.openOrCreateDatabase(name, null);
	}
	
	public void close() {
		if (db != null) {
			db.close();
		}
	}
	
	public int getState() {
		Cursor cursor = db.query("dict_info", new String[] { "offset" }, "idx=0", null, null, null, null);
		
		try {			
			if (cursor != null && cursor.moveToFirst()) {
				return cursor.getInt(0);
			}
		} finally {
			cursor.close();
		}
		return -1;
	}

	public Cursor getWord(String condition, int offset, int limit) {
		condition = "word LIKE '" + condition + "%'";
		String sql = "SELECT idx, word, flag FROM word_info";
		if(condition != null) {
			sql += " WHERE " + condition;
		}
		sql += " LIMIT " + limit + " OFFSET " + offset ;
		
		return db.rawQuery(sql, null);
	}

	public Cursor queryDictionaryInfo() {
		Cursor cursor = db.query("dict_info", new String[] {"idx", "title", "file", "offset", "d_decoder", "x_decoder"},
				null, null, null, null, null);
		return cursor;
	}
	
	public Cursor queryBlockData(int index) {
		// TODO Auto-generated method stub
		return null;
	}

}
