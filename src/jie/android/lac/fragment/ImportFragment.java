package jie.android.lac.fragment;

import java.io.IOException;

import jie.android.lac.R;
import jie.android.lac.fragment.data.HttpdServer;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ImportFragment extends BaseFragment {

	private static final String Tag = ImportFragment.class.getSimpleName();
	
	private static final int HTTPD_PORT		=	20812;
	private static final int MSG_START_HTTPD	=	1;
	private static final int MSG_STOP_HTTPD		=	2;
	
	private TextView textAddress = null;
	
	private HttpdServer server = null;
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_START_HTTPD:
				startHttpd();
				break;
			case MSG_STOP_HTTPD:
				stopHttpd();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_import, container, false);
		
		textAddress = (TextView) v.findViewById(R.id.textView2);		
		
		Button btn = (Button) v.findViewById(R.id.button1);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onRefreshWifi();
			}
			
		});
		
		onRefreshWifi();
		
		return v;
	}
 
	@Override
	public void onDestroy() {
		stopHttpd();
		super.onDestroy();
	}

	protected void onRefreshWifi() {
		String address = null;
		try {
			address = getLocalAddress();
			
			startHttpd();
		} catch (IOException e) {
			address = e.getMessage();
		}		
		textAddress.setText(address);
		
	}

	private String getLocalAddress() throws IOException {
		getLACActivity();
		WifiManager wm = (WifiManager) getLACActivity().getSystemService(Context.WIFI_SERVICE);
		if(wm.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
			Toast.makeText(getLACActivity(), "Wifi is NOT available.", Toast.LENGTH_SHORT).show();
			throw new IOException(getText(R.string.lac_import_wifi_not_available).toString());
//			return getText(R.string.lac_import_wifi_not_available).toString();
		}
		   
		WifiInfo wi = wm.getConnectionInfo();
		int ip = wi.getIpAddress();
		if(ip == -1) {
			Toast.makeText(getLACActivity(), "Get IP address failed.", Toast.LENGTH_SHORT).show();
			throw new IOException(getText(R.string.lac_import_wifi_ip_failed).toString());
//			return getText(R.string.lac_import_wifi_ip_failed).toString();
		}
		
		return String.format("http://%d.%d.%d.%d:%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff), HTTPD_PORT);
	}

	protected void startHttpd() {
		stopHttpd();
		try {
			server = new HttpdServer(HTTPD_PORT, getLACActivity().getConfig().getDataFolder());
			server.start();
		} catch (IOException e) {
			Log.e(Tag, "start httpd failed - " + e.getMessage());
		}
	}


	protected void stopHttpd() {
		if (server != null) {
			server.stop();
			server = null;
		}
	}	
}
