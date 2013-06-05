package jie.android.lac.app.importer;

import java.io.IOException;

import jie.java.android.lingoshook.DataFormat.Data;

import android.app.Activity;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HttpdActivity extends ImportBaseActivity {

	private final int HTTPD_PORT			=	8102;
	
	private HttpdServer server = null;
	
	private TextView comment = null;
	private TextView word = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.httpd);
		
		TextView tv = (TextView) this.findViewById(R.id.textView2);		
		comment = (TextView)this.findViewById(R.id.textView3);
		word = (TextView)this.findViewById(R.id.textView4);
		
		if(getLocalAddress(tv) == 0) {
			if(startHttdp() != 0) {
				Toast.makeText(this, "Httpd start failed.", Toast.LENGTH_SHORT).show();
			}			
		}
	}

	@Override
	protected void onDestroy() {
		if(server != null) {
			server.stop();
		}
		super.onDestroy();
	}

	private int getLocalAddress(TextView tv) {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		if(wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
			tv.setText("Wifi is NOT available.");
			Toast.makeText(this, "Wifi is NOT available.", Toast.LENGTH_SHORT).show();
			return -1;
		}
		   
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();
		if(ip == -1) {
			tv.setText("Get IP address failed.");
			Toast.makeText(this, "Get IP address failed.", Toast.LENGTH_SHORT).show();
			return -1;
		}
		
		tv.setText(String.format("http://%d.%d.%d.%d:%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff), HTTPD_PORT));
		
		return 0;		
	}
	
	private int startHttdp() {
		try {
			server = new HttpdServer(this, this.getHandler(), HTTPD_PORT);
		} catch (IOException e) {
			return -1;
		}
		return 0;	
	}

	@Override
	protected void onMsg_ImportFile(String file, String local, boolean overwrite) {
		comment.setText(R.string.str_httpd_importing);
	}

	@Override
	protected void onMsg_InputData(Data data) {
		comment.setText(R.string.str_httpd_importing);
	}

	@Override
	protected void onMsg_Word(String word) {
		this.word.setText(word);
	}

	@Override
	protected void onMsg_ImportFileDone(String file, String local, boolean overwrite) {
		comment.setText(String.format(getString(R.string.str_httpd_importdone), file));
		word.setText("DONE");
	}

	@Override
	protected void onMsg_InputDataDone(final String word) {
		comment.setText(R.string.str_httpd_inputdone);
		this.word.setText(word);
	}

	@Override
	protected void onMsg_ImportFileFailed(boolean isDBFile, final String file, final String local) {
		if(!isDBFile) {
			comment.setText(String.format(getString(R.string.str_httpd_importfail), file));
			Toast.makeText(HttpdActivity.this, "Import file failed.", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onMsg_InputDataFailed() {
		comment.setText(R.string.str_httpd_inputfail);	
	}

	@Override
	protected void onMsg_Exception() {
		Toast.makeText(HttpdActivity.this, "Import file failed - exception", Toast.LENGTH_LONG).show();
	}
	

}
