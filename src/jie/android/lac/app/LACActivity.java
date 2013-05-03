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
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;


public class LACActivity extends SlidingFragmentActivity {
	
	private static final String Tag = LACActivity.class.getSimpleName();
	
	private Configuration configuration = null;	
	private ServiceAccess serviceAccess = null;
	
	private FragmentSwitcher fragmentSwitcher = null;
	
	private SearchView searchView = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initGlobalData();
		
		initService();
		
		initView();
	}

	@Override
	protected void onDestroy() {
		releaseService();

		super.onDestroy();
	}

	private void initGlobalData() {
		configuration = new Configuration(this);		
	}

	private void initView() {
		this.setTitle(R.string.app_title);
		this.getSupportActionBar().setSubtitle(R.string.app_subtitle);
		this.setContentView(R.layout.lac);
		
		fragmentSwitcher = new FragmentSwitcher(this);
		
		initSlidingMenu();	
	}

	private void initService() {
		serviceAccess = new ServiceAccess(this);
		serviceAccess.bindService();
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
				
//		replaceSlidingFragment(R.id.lac_left, new ColorFragment(R.color.green));
//		replaceSlidingFragment(R.id.lac_right, new ColorFragment(R.color.white));
	}

//	private void replaceSlidingFragment(int id, Fragment fragment) {
//		this.getSupportFragmentManager().beginTransaction().replace(id, fragment).commit();		
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		this.getSupportMenuInflater().inflate(R.menu.lac, menu);

		searchView = (SearchView) menu.findItem(R.id.item4).getActionView();
		searchView.setQueryHint("Keyword");
		searchView.setIconifiedByDefault(true);
		
		searchView.setOnSearchClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(Tag, "setOnSearchClickListener");
//				onSearchViewChange(true);
			}
			
		});
		
		searchView.setOnCloseListener(new OnCloseListener() {

			@Override
			public boolean onClose() {
				Log.d(Tag, "setOnCloseListener");
//				onSearchViewChange(false);
				return false;
			}
			
		});
		
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				Log.d(Tag, "search key submit : " + query);
//				onSearchViewQueryChanged(query, true);
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				Log.d(Tag, "search key change : " + newText);
//				onSearchViewQueryChanged(newText, false);
				return true;
			}
			
		});		

		return super.onCreateOptionsMenu(menu);
	}

//	protected void onSearchViewQueryChanged(String query, boolean isSubmitted) {
//		Intent intent = new Intent();
//		intent.putExtra("keyword", query);
//		intent.putExtra("submit", isSubmitted);
//	
//		contentSwitcher.postIntent(Frame.Dictionary, intent);
//	}
//
//	protected void onSearchViewChange(boolean isOpen) {
//		contentSwitcher.update(Frame.Dictionary);
//
//	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		Log.d(Tag, "MenuItem : " + item.getItemId());
		
//		searchView.setIconified(false);
		return super.onOptionsItemSelected(item);
	}

	public void showFragment(FragmentType type) {
		fragmentSwitcher.show(type);
	}
	
	public Configuration getConfig() { 
		return configuration;
	}
	
	public ServiceAccess getServiceAccess() {
		return serviceAccess;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			searchView.setIconified(false);
			return true;
		} else if(keyCode == KeyEvent.KEYCODE_BACK) {
			if (!searchView.isIconified()) {
				searchView.setIconified(true);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onServiceConnected() {
		showFragment(FragmentType.Dictionary);
	}
		
	public void onServiceDisconnected() {
		// TODO Auto-generated method stub
		
	}
}
