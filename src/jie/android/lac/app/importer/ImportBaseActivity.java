package jie.android.lac.app.importer;

import jie.java.android.lingoshook.DataFormat.Data;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public abstract class ImportBaseActivity extends Activity {

	private final static int MSG_WORD			=	0x0F01;
	private final static int MSG_DB_DONE		=	0x0F00;
	private final static int MSG_XML_DONE		=	0x0F05;
	private final static int MSG_DATA_DONE		=	0x0F06;
	private final static int MSG_EXCEPTION		=	0x0F02;
	private final static int MSG_DB_FAILED		=	0x0F03;	
	private final static int MSG_XML_FAILED		=	0x0F04;
	private final static int MSG_DATA_FAILED	=	0x0F07;
	
	public final static int MSG_IMPORT_FILE	=	0;
	public final static int MSG_INPUT_DATA =	1;
	
	private Handler handler = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initHandler();
	}
	
	public Handler getHandler() {
		return handler;
	}
	
	protected void initHandler() {
		
		handler = new Handler() {
			private String src = "";
			private String file = "";
			private boolean overwrite = false;			
			@Override
			public void handleMessage(Message msg) {

				switch(msg.what) {
					case MSG_IMPORT_FILE: {

						src = msg.getData().getString("file");
						file = msg.getData().getString("local");
						overwrite = msg.getData().getBoolean("overwrite");						

						onMsg_ImportFile(src, file, overwrite);
						
						importDB(file, overwrite);
						break;
					}
					case MSG_INPUT_DATA: {
											
						DataFormat.Data data = new DataFormat.Data();
						data.dict = msg.getData().getString("dict");
						data.word = msg.getData().getString("word");
						data.symbol = msg.getData().getString("symbol");
						data.category.add(msg.getData().getString("category1"));
						data.meaning.add(msg.getData().getString("meaning1"));
						if(msg.getData().getString("meaning2") != null) {
							data.category.add(msg.getData().getString("category2"));
							data.meaning.add(msg.getData().getString("meaning2"));							
						}
						if(msg.getData().getString("meaning3") != null) {
							data.category.add(msg.getData().getString("category3"));
							data.meaning.add(msg.getData().getString("meaning3"));							
						}

						onMsg_InputData(data);
						
						inputData(data);
						
						break;
					}
					case MSG_WORD: {
						onMsg_Word((String)msg.obj);
						break;
					}
					case MSG_DB_DONE:
					case MSG_XML_DONE: {
						onMsg_ImportFileDone(src, file, overwrite);
						break;
					}
					case MSG_DATA_DONE: {
						onMsg_InputDataDone((String)msg.obj);
						break;						
					}
					case MSG_DB_FAILED: {
						onMsg_ImportFileFailed(true, src, file);
						
						importXml(file, overwrite);
						break;
					}
					case MSG_XML_FAILED: {
						onMsg_ImportFileFailed(false, src, file);
						break;
					}
					case MSG_DATA_FAILED: {
						onMsg_InputDataFailed();
						break;
					}
					case MSG_EXCEPTION: {
						onMsg_Exception();
						break;
					}					
					default: {
						break;
					}
				}
				
				super.handleMessage(msg);
			}			
		};
	}
		
		
	private int importDB(final String file, final boolean overwrite) {
		try {
			new Thread() {
				@Override
				public void run() {
					if(DBAccess.importData(handler, MSG_WORD, file, (overwrite ? DBAccess.IMPORTTYPE_OVERWRITE : DBAccess.IMPORTTYPE_APPEND)) == 0) {
						handler.sendMessage(Message.obtain(handler, MSG_DB_DONE));
					}
					else {
						handler.sendMessage(Message.obtain(handler, MSG_DB_FAILED));
					}
				}
				
			}.start();
		}
		catch (Exception e) {
			handler.sendMessage(Message.obtain(handler, MSG_EXCEPTION));
		}
		return 0;
	}
	
	private int importXml(final String file, final boolean overwrite) {
		try {
			new Thread() {
				@Override
				public void run() {
					if(DBAccess.importXml(handler, MSG_WORD, file, (overwrite ? DBAccess.IMPORTTYPE_OVERWRITE : DBAccess.IMPORTTYPE_APPEND)) == 0) {
						handler.sendMessage(Message.obtain(handler, MSG_XML_DONE));
					}
					else {
						handler.sendMessage(Message.obtain(handler, MSG_XML_FAILED));
					}
				}
				
			}.start();
		}
		catch (Exception e) {
			handler.sendMessage(Message.obtain(handler, MSG_EXCEPTION));
		}
		return 0;
	}	

	private void inputData(Data data) {
		if(DBAccess.inputData(data) == 0) {
			handler.sendMessage(Message.obtain(handler, MSG_DATA_DONE, data.word));
		}
		else {
			handler.sendMessage(Message.obtain(handler, MSG_DATA_FAILED));
		}
	}
	
	protected void onMsg_ImportFile(final String file, final String local, boolean overwrite) {
		
	}
	
	protected void onMsg_InputData(final DataFormat.Data data) {
		
	}
	
	protected void onMsg_Word(final String word) {
		
	}
	
	protected void onMsg_ImportFileDone(final String file, final String local, boolean overwrite) {
		
	}
	
	protected void onMsg_InputDataDone(final String word) {
		
	}
	
	protected void onMsg_ImportFileFailed(boolean isDBFile, final String file, final String local) {
		
	}
	
	protected void onMsg_InputDataFailed() {
		
	}
	
	protected void onMsg_Exception() {
		
	}
}
