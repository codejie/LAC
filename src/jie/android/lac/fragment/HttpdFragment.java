package jie.android.lac.fragment;

import java.io.File;
import java.io.IOException;

import jie.android.lac.R;
import jie.android.lac.app.Configuration;
import jie.android.lac.fragment.data.HttpdServer;
import jie.android.lac.service.aidl.ImportDatabaseListener;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class HttpdFragment extends BaseFragment {

	private static final String Tag = HttpdFragment.class.getSimpleName();
	
	
	private class Listener extends ImportDatabaseListener.Stub {

		@Override
		public void onStarted(String file) throws RemoteException {
			Log.d(Tag, "listener:onStarted() - file:" + file);			
		}

		@Override
		public void onImported(int position, String text)throws RemoteException {
			Log.d(Tag, "listener:onImported() text:" + text);			
		}

		@Override
		public void onCompleted(int total) throws RemoteException {
			Log.d(Tag, "listener:onSCompleted() - total : " + total);
		}
		
		@Override
		public void onFailed(String what) throws RemoteException {
			Log.d(Tag, "listener:onFailed() what:" + what);			
		}
		
	}
	
	
	
	private static final int HTTPD_PORT		=	20812;
	public static final int MSG_START_HTTPD	=	1;
	public static final int MSG_STOP_HTTPD		=	2;
	public static final int MSG_IMPORT_DATABASE	=	3;
	
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
			case MSG_IMPORT_DATABASE:
				importDatabase(msg.getData().getString("localfile"));
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
			
			handler.sendMessage(Message.obtain(handler, MSG_START_HTTPD));
//			startHttpd();
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
			String root = getLACActivity().getFilesDir() + File.separator + Configuration.SUB_FOLDER_HTTPD;
			server = new HttpdServer(HTTPD_PORT, root, handler);
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

	protected void importDatabase(String file) {
		try {
			getLACActivity().getServiceAccess().ImportDBFile(file, new Listener());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}
