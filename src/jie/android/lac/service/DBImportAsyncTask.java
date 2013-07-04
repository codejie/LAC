package jie.android.lac.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import jie.android.lac.service.aidl.ImportDatabaseListener;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

public class DBImportAsyncTask extends AsyncTask<String, String, String> {

	private static final String Tag = DBImportAsyncTask.class.getSimpleName();
	
	private DBAccess dbAccess = null;
	private String updateFile = null;
	
	private ImportDatabaseListener importListener = null;
	
	private SQLiteDatabase importDb = null;
	
	public DBImportAsyncTask(DBAccess dbAccess, final String importFile) {
		this.dbAccess = dbAccess;
		this.updateFile = importFile;
	}
	
	public void setListener(ImportDatabaseListener listener) {
		importListener = listener;
	}
	
	public boolean init() {
		try {
			importDb = SQLiteDatabase.openDatabase(updateFile, null, SQLiteDatabase.OPEN_READONLY);
		} catch (SQLException e) {
			return false;
		}
		return true;		
	}	
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
		if (importListener != null) {
			try {
				importListener.onImported(values[0]);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			StringBuffer importFile = new StringBuffer();
			ArrayList<String> arrayFile = new ArrayList<String>();
			
			unfoldUpdateFile(updateFile, importFile, arrayFile);
			importDbFile(importFile.toString());
			copyDbFiles(arrayFile);
			
//			importDictInfo();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private boolean unfoldUpdateFile(String file, StringBuffer importFile, ArrayList<String> arrayFile) throws RemoteException {
		
//		File f = new File(file);
//		if (!f.exists()) {
//			return false;
//		}
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			Document doc = factory.newDocumentBuilder().parse("file://" + file);
			NodeList node = doc.getElementsByTagName("database");
			if (node == null) {
				return false;
			}
			importFile.append(node.item(0).getChildNodes().item(0).getNodeValue());
			
			node = doc.getElementsByTagName("dictionary");
			if (node == null) {
				return false;
			}
			
			NodeList item = node.item(0).getChildNodes();
		
			for (int i = 0; i < item.getLength(); ++ i) {
				Node n = item.item(i);
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					if (n.getNodeName().equals("item")) {
						arrayFile.add(n.getChildNodes().item(0).getNodeValue());
					}
				}
				
			}
			
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

	private boolean importDbFile(String string) throws RemoteException {
		return false;
	}

	private boolean copyDbFiles(ArrayList<String> arrayFile) throws RemoteException {
		return false;
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
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

					dbAccess.beginTransaction();
					try {
						Long rowid = dbAccess.importDictInfo(values);
						if (importListener != null) {
							importListener.onImported("Dictionary Info : " + cursor.getInt(2));
						}
						importBlockData(cursor.getInt(0));
						if (importListener != null) {
							importListener.onImported("Dictionary Block Info complete.");
						}
	
						importWordData(cursor.getInt(0));
						if (importListener != null) {
							importListener.onImported("Dictionary Word Info complete.");
						}
					} finally {					
						dbAccess.endTransaction(true);
					}
					
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
							importListener.onImported("Dictionary Block Info failed.");
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
		if (!dbAccess.createWordIndexTable(dictid)) {
			return false;
		}
		
		Cursor cursor = importDb.query("word_info", null, null, null, null, null, null);
		try {
			if (cursor != null && cursor.moveToFirst()) {
				do {
					ContentValues values = new ContentValues();
					values.put("word", cursor.getString(1));
					values.put("flag", cursor.getInt(2));
					
					long rowid = dbAccess.importWordData(values);
					if (rowid != -1) {
						importWordIndex(dictid, cursor.getInt(0), rowid);
						this.publishProgress(cursor.getString(1));
					} else {
						Log.e(Tag, "importWordData() failed.");
					}
				} while (cursor.moveToNext());
			}
			
		} finally {
			cursor.close();
		}
		
		return true;
	}

	private boolean importWordIndex(int dictid, int idx, long rowid) {
		Cursor cursor = importDb.query("word_index_" + dictid, new String[] { "idx", "offset", "length", "block1" }, "word_idx=?", new String[] { String.valueOf(idx) }, null, null, null);
		
		try {
			if (cursor != null && cursor.moveToFirst()) {
				do {
					ContentValues values = new ContentValues();
					values.put("word_idx", rowid);
					values.put("idx", cursor.getInt(0));
					values.put("offset", cursor.getInt(1));
					values.put("length", cursor.getInt(2));
					values.put("block1", cursor.getInt(3));
					
					if (dbAccess.importWordIndex(dictid, values) == -1) {
						return false;
					}
					
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
		}
		
		return true;
	}		
}
