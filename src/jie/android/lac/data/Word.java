package jie.android.lac.data;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Word {
	
	public static final class Info implements Parcelable {
		
	    public static final Parcelable.Creator<Info> CREATOR = new Parcelable.Creator<Info>() {

			@Override
			public Info createFromParcel(Parcel source) {
				Info data = new Info();
				data.setIndex(source.readInt());
				data.setWord(source.readString());
				return data;
			}

			@Override
			public Info[] newArray(int size) {
				return new Info[size];
			}

	    };	
		

		private int index = -1;
		private String word = null;
		
		public Info() {			
		}
		
		public Info(int id, final String word) {
			this.index = id;
			this.word = word;
		}
		
		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flag) {
			dest.writeInt(index);
			dest.writeString(word);
		}

		@Override
		public String toString() {
			return super.toString();
		}

		public int getId() {
			return index;
		}

		protected void setIndex(int value) {
			index = value;
		}

		public final String getWord() {
			return word;
		}
		
		protected void setWord(final String value) {
			word = value;
		}
	}
	
	public class Index {
		
	}
	
	public static class XmlResult implements Parcelable {

		public static final class XmlData {
			private int dict = -1;
			private List<String> xml = null;
			
			public XmlData(int dict, final ArrayList<String> xml) {
				this.dict = dict;
				this.xml = xml;
			}

			public int getDictIndex() {
				return dict;
			}

			public final List<String> getXml() {
				return xml;
			}
		}
		
		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flag) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
