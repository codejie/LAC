package jie.android.lac.service;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAccess {

	private static final class Projection {
		public static final String[] DictionaryInfo = new String[] {"idx", "title", "file", "offset", "d_decoder", "x_decoder"};
		public static final String[] DictionaryBlock = new String[] {/*"idx", */"offset", "length", "start", "end"};
		public static final String[] WordIndex = new String[] { "offset", "length", "block1" };
	}
	
	private static final String Tag = DBAccess.class.getSimpleName();
	
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
		return db.query("dict_info", Projection.DictionaryInfo, null, null, null, null, null);
	}
	
	public Cursor queryBlockData(int dictIndex) {
		return db.query("block_info_" + dictIndex, Projection.DictionaryBlock, null, null, null, null, null);
	}

	public Cursor queryWordXmlIndex(int dictIndex, int wordIndex) {
		return db.query("word_index_" + dictIndex, Projection.WordIndex, "word_idx=" + wordIndex, null, null,null, null);
	}

	public void beginTransaction() {
		db.beginTransaction();
	}
	
	public void endTransaction(boolean succ) {
		if (succ) {
			db.setTransactionSuccessful();
		}
		db.endTransaction();
	}
	
	public long importDictInfo(ContentValues values) {
		return db.insert("dict_info", null, values);
	}

	public boolean createBlockDataTable(int dictid) {
		String sql = "CREATE TABLE [block_info_" + dictid + "] ([idx] INTEGER PRIMARY KEY, [offset] INTEGER, [length] INTEGER, [start] INTEGER, [end] INTEGER);";
		db.execSQL(sql);
		
		return true;
	}

	public long importBlockData(int dictid, ContentValues values) {
		return db.insert("block_info_" + dictid, null, values);
	}

	public boolean createWordIndexTable(int dictid) {
		String sql = "CREATE TABLE [word_index_" + dictid + "] ( [word_idx] INTEGER, [idx] INTEGER, [offset] INTEGER, [length] INTEGER, [block1] INTEGER);";
		db.execSQL(sql);
		
		sql = "CREATE INDEX [index_word_index_" + dictid + "_word_idx] ON [word_index_" + dictid + "] (word_idx ASC);";
		db.execSQL(sql);
		
		return true;
	}

	public long importWordData(ContentValues values) {
		//query
		Cursor cursor = db.query("word_info", new String[] { "idx" }, "word=?", new String[] { values.getAsString("word") }, null, null, null);
		try {
			if (cursor == null || !cursor.moveToFirst()) {
				return insertWordData(values);
			} else {
				return cursor.getLong(0);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	private long insertWordData(ContentValues values) {
		return db.insert("word_info", null, values);
	}

	public long importWordIndex(int dictid, ContentValues values) {
		return db.insert("word_index_" + dictid, null, values);
		
	}

}
