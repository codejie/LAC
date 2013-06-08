package jie.android.lac.fragment;

import jie.android.lac.R;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ImportFragment extends BaseFragment {

	private static final String Tag = ImportFragment.class.getSimpleName();
	
	private static final int HTTPD_PORT	=	20812;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_import, container, false);
		
		TextView textAddress = (TextView) v.findViewById(R.id.textView2);
		textAddress.setText(getLocalAddress());
		return super.onCreateView(inflater, container, savedInstanceState);
	}
 
	private String getLocalAddress() {
		getLACActivity();
		WifiManager wm = (WifiManager) getLACActivity().getSystemService(Context.WIFI_SERVICE);
		if(wm.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
			Toast.makeText(getLACActivity(), "Wifi is NOT available.", Toast.LENGTH_SHORT).show();
			return getText(R.string.lac_import_wifi_not_available).toString();
		}
		   
		WifiInfo wi = wm.getConnectionInfo();
		int ip = wi.getIpAddress();
		if(ip == -1) {
			Toast.makeText(getLACActivity(), "Get IP address failed.", Toast.LENGTH_SHORT).show();
			return getText(R.string.lac_import_wifi_ip_failed).toString();
		}
		
		return String.format("http://%d.%d.%d.%d:%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff), HTTPD_PORT);
	}	
	
}
