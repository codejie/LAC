package jie.android.lac.data;

import android.os.Parcel;
import android.os.Parcelable;

public class WordData implements Parcelable {
	
    public static final Parcelable.Creator<WordData> CREATOR = new Parcelable.Creator<WordData>() {

		@Override
		public WordData createFromParcel(Parcel source) {
			WordData data = new WordData();
			data.setIndex(source.readInt());
			data.setWord(source.readString());
			return data;
		}

		@Override
		public WordData[] newArray(int size) {
			return new WordData[size];
		}


    };	
	

	private int index = -1;
	private String word = null;
	
	public WordData() {
		
	}
	
	public WordData(int id, final String word) {
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
