package jie.android.lac.app;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnCloseListener;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

import jie.android.lac.R;
import jie.android.lac.app.FragmentSwitcher.FragmentType;
import jie.android.lac.data.Constants.MSG;
import jie.android.lac.data.Constants.SERVICE_STATE;
import jie.android.lac.service.aidl.Access;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;


public class LACActivity extends SlidingFragmentActivity {
	
	private static final String Tag = LACActivity.class.getSimpleName();
	
	private Configuration configuration = null;	
	private ServiceAccess serviceAccess = null;
	
	private FragmentSwitcher fragmentSwitcher = null;
	
	private ProgressDialog dlg = null;
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG.SERVICE_STATE_NOTIFY:
				Log.d(Tag, "get Service State notify - state : " + msg.arg1);
				onServiceState(msg);
				break;
			case MSG.WORD_XML_RESULT:
				break;
			default:;
			}
		}		
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initGlobalData();				
		initView();
		
		Log.d(Tag, "onCreate().");
	}

	@Override
	protected void onStart() {
		
		Log.d(Tag, "onStart().");		
		super.onStart();
		
		initService();		
	}	

	@Override
	protected void onStop() {
		Log.d(Tag, "onStop().");
		releaseService();		
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		
		Log.d(Tag, "onDestroy().");		
//		releaseService();

		super.onDestroy();
	}

	private void initGlobalData() {
		configuration = new Configuration(this);

		configuration.setWordPrePage(15);
	}

	private void initView() {
		this.setTitle(R.string.app_title);
		this.getSupportActionBar().setSubtitle(R.string.app_subtitle);
		this.setContentView(R.layout.lac);
		
		fragmentSwitcher = new FragmentSwitcher(this);
		
		initSlidingMenu();	
	}

	private void initService() {
		if (serviceAccess == null) {
			showProcessDialog(true);		
			serviceAccess = new ServiceAccess(this);
			serviceAccess.bindService();
		}
	}

	private void releaseService() {
		serviceAccess.unbindService();
	}
	
	private void initSlidingMenu() {
		
		this.setBehindContentView(R.layout.lac_left);
		this.setSlidingActionBarEnabled(false);
		
		SlidingMenu sm = this.getSlidingMenu(); //new SlidingMenu(this);		
		
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setBehindOffsetRes(R.dimen.sliding_offset);
		sm.setFadeEnabled(configuration.getSlidingFadeEnabled());		
		sm.setFadeDegree(configuration.getSlidingFadeDegree());
		sm.setSecondaryMenu(R.layout.lac_right);
		sm.setSecondaryShadowDrawable(R.drawable.shadow);
		
		sm.setSlidingEnabled(false);
	}

	public void showFragment(FragmentType type) {
		fragmentSwitcher.show(type);
	}
	
	public final Configuration getConfig() { 
		return configuration;
	}
	
	public final Access getServiceAccess() {
		return serviceAccess.getAccess();
	}
	
	public final Handler getHandler() {
		return handler;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (fragmentSwitcher.onKeyDown(keyCode, event)) {
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public void onServiceConnected() {
		updateProgressMessage(R.string.lac_service_sate_connecting);
	}
		
	public void onServiceDisconnected() {
		showProcessDialog(true);
		updateProgressMessage(R.string.lac_service_state_disconnected);
		showProcessDialog(false);
	}

	protected void onServiceState(Message msg) {
		switch(msg.arg1) {
		case SERVICE_STATE.DATA_INIT:
			updateProgressMessage(R.string.lac_service_state_init_data);
			break;			
		case SERVICE_STATE.DATA_READY:
			updateProgressMessage(R.string.lac_service_state_ready);
			showProcessDialog(false);
			onServiceReady();
			break;
		case SERVICE_STATE.DICTIONARY_INIT:
			updateProgressMessage(R.string.lac_service_state_init_dict);
			break;
		case SERVICE_STATE.DATA_UNZIP:
			updateProgressMessage(R.string.lac_service_state_unzip_data);
			break;
		case SERVICE_STATE.DATA_LOAD_FAIL:
			updateProgressMessage(R.string.lac_service_state_fail);
			showProcessDialog(false);
			onServiceFailed();
			break;			
		default:;
		}		
	}
	
	protected void onServiceReady() {
		showFragment(FragmentType.Dictionary);
	}
	
	private void onServiceFailed() {
		// TODO Auto-generated method stub
		
	}	
	
	private void showProcessDialog(boolean show) {
		if (dlg == null) {
			dlg = new ProgressDialog(this);
		}
		if (show) {
			dlg.show();
		} else {
			dlg.dismiss();
			dlg = null;
		}
	}
	
	private void updateProgressMessage(int resId) {
		if (dlg != null) {
			dlg.setMessage(this.getString(resId));
		}
	}
}
