package jie.android.lac.data;

import java.io.RandomAccessFile;
import java.util.ArrayList;

import jie.android.lac.service.DBAccess;

import android.os.Parcel;
import android.os.Parcelable;

public class Dictionary {
	public static final class Info implements Parcelable {

		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flag) {
			// TODO Auto-generated method stub
			
		}

		private int index = -1;
		private String title = null;
		private String file = null;
		private int offset = -1;
		private int d_decoder = -1;
		private int x_decoder = -1;
	}
	
	public final class Entity { 
		public final class BlockData {
			public int offset = 0;
			public int length = 0;
			public int start = 0;
			public int end = 0;					
		}

		private Info info = null;
		private DBAccess dbAccess = null;	
		private RandomAccessFile fileAccess = null;
		
		private final ArrayList<BlockData> blockData = new ArrayList<BlockData>();
		
		public Entity(final Info info, final DBAccess dbAccess) {
			this.info = info;
			this.dbAccess = dbAccess;
			
			init();
		}
	
		
	}	
	
	
	
}
