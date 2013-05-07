package jie.android.lac.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jie.android.lac.data.Word.Info;
import jie.android.lac.service.DBAccess;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Dictionary {
	
	private static final String Tag = Dictionary.class.getSimpleName();
	
	//SimpleInfo
	public static final class SimpleInfo implements Parcelable {

	    public static final Parcelable.Creator<SimpleInfo> CREATOR = new Parcelable.Creator<SimpleInfo>() {

			@Override
			public SimpleInfo createFromParcel(Parcel source) {
				SimpleInfo info = new SimpleInfo();
				info.setIndex(source.readInt());
				info.setTitle(source.readString());
				return info;
			}

			@Override
			public SimpleInfo[] newArray(int size) {
				return new SimpleInfo[size];
			}
	    };			
		
		private int index = -1;
		private String title = null;
		
		public SimpleInfo() {			
		}
		
		public SimpleInfo(int index, final String title) {
			this.index = index;
			this.title = title;
		}
		
		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flag) {
			dest.writeInt(index);
			dest.writeString(title);
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
		
	}
	//Info
	public final class Info {
		public int index = -1;
		public String title = null;
		public String file = null;
		public int offset = -1;
		public int d_decoder = -1;
		public int x_decoder = -1;
		
		public Info(int index, final String title, final String file, int offset, int d_decoder, int x_decoder) {
			this.index = index;
			this.title = title;
			this.file = file;
			this.offset = offset;
			this.d_decoder = d_decoder;
			this.x_decoder = x_decoder;
		}
	}
	
	//entity
	public final class Entity { 
		public final class BlockData {
			public int offset = 0;
			public int length = 0;
			public int start = 0;
			public int end = 0;					
		}

		private Info info = null;
		private RandomAccessFile fileAccess = null;
		
		private final ArrayList<BlockData> blockData = new ArrayList<BlockData>();
		
		public Entity(final Info info) {
			this.info = info;
		}

		private void init(final String dataPath) {
			try {
				fileAccess = new RandomAccessFile(dataPath + info.file, "r");
				
				loadBlockData();
				
			} catch (FileNotFoundException e) {
				Log.e(Tag, "init() failed - " + e.getMessage());
			}
		}
		
		private void close(){
			if (fileAccess != null) {
				try {
					fileAccess.close();
				} catch (IOException e) {
					Log.e(Tag, "close() failed - " + e.getMessage());
				}
			}
		}
		
		private boolean loadBlockData() {
			Cursor cursor = dbAccess.queryBlockData(info.index);
			if(cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						BlockData block = new BlockData();
						block.offset = cursor.getInt(0);
						block.length = cursor.getInt(1);
						block.start = cursor.getInt(2);
						block.end = cursor.getInt(3);
						
						blockData.add(block);
					} while (cursor.moveToNext());
				}
				cursor.close();
			} else {
				return false;
			}
			return true;
		}
		
		public final Info getInfo() {
			return info;
		}
	}	
	
	//dictionay
	private DBAccess dbAccess = null;
	private String dataPath = null;
	private HashMap<Integer, Entity> mapEntity = new HashMap<Integer, Entity>();

	public Dictionary(DBAccess dbAccess, final String dataPath) {
		this.dbAccess = dbAccess;
		this.dataPath = dataPath;
	}
	
	public boolean load() {
		Cursor cursor = dbAccess.queryDictionaryInfo();
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					do {
						Info info = new Info(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5));
						Entity entity = new Entity(info);
						entity.init(dataPath);
						mapEntity.put(cursor.getInt(0), entity);
					} while (cursor.moveToNext());
				}
			} finally {
				cursor.close();
			}
		}		
		return true;
	}

	public void close() {
		for(final Entity entity : mapEntity.values()) {
			entity.close();
		}
	}
	
	public List<SimpleInfo> getSimpleInfo() {
		List<SimpleInfo> ret = new ArrayList<SimpleInfo>();
		for(final Entity entity : mapEntity.values()) {
			ret.add(new SimpleInfo(entity.getInfo().index, entity.getInfo().title));
		}
		return ret;
	}
	
}
