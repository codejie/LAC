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
//		super(context, DBAccess.FILE, null, DBAccess.VERSION);
	}
//
//	@Override
//	public void onCreate(SQLiteDatabase db) {
//
//	}
//
//	@Override
//	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//
//	}
//
//	@Override
//	public void onOpen(SQLiteDatabase db) {
//		super.onOpen(db);
//		
//		this.db = db;
//		Log.d(Tag, "DBAccess - onOpen()" + db);
//	}
	
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

}
