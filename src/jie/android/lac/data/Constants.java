package jie.android.lac.data;

public final class Constants {
	public interface MSG {
		public static final int SERVICE_STATE_NOTIFY	=	0x01;
		public static final int WORD_XML_RESULT			=	0x10;
	}
	
	public interface SERVICE_STATE {
		public static final int DATA_INIT		=	0x01;
		public static final int DATA_UNZIP		=	0x02;
		public static final int DATA_READY		=	0x03;
		public static final int DICTIONARY_INIT	=	0x04;
		
		public static final int DATA_LOAD_FAIL	=	0xF0;
	}
}
