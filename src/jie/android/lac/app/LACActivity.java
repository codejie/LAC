package jie.android.lac.app;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

import jie.android.lac.R;
import jie.android.lac.app.ContentSwitcher.Frame;
import jie.android.lac.fragment.ColorFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;


public class LACActivity extends SlidingFragmentActivity {
	
	private Configuration configuration = null;	
	private ContentSwitcher contentSwitcher = null;
	private ServiceAccess serviceAccess = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initGlobalData();
		
		initService();
		
		initView();
		
		initFrame();
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
		
		contentSwitcher = new ContentSwitcher(this);
		
		initSlidingMenu();	
	}

	private void initService() {
		serviceAccess = new ServiceAccess(this);
		serviceAccess.bindService();
	}
	
	private void releaseService() {
		serviceAccess.unbindService();
	}
	
	private void initFrame() {
		updateFrame(Frame.Welcome);
	}

	private void initSlidingMenu() {
		
		this.setBehindContentView(R.layout.lac_left);
		this.setSlidingActionBarEnabled(false);
		
		SlidingMenu sm = this.getSlidingMenu(); //new SlidingMenu(this);
		
		sm.setMode(SlidingMenu.LEFT_RIGHT);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setBehindOffsetRes(R.dimen.sliding_offset);
		sm.setFadeEnabled(configuration.getSlidingFadeEnabled());		
		sm.setFadeDegree(configuration.getSlidingFadeDegree());
		sm.setSecondaryMenu(R.layout.lac_right);
		sm.setSecondaryShadowDrawable(R.drawable.shadow);		
				
		replaceSlidingFragment(R.id.lac_left, new ColorFragment(R.color.green));
		replaceSlidingFragment(R.id.lac_right, new ColorFragment(R.color.white));
	}

	private void replaceSlidingFragment(int id, Fragment fragment) {
		this.getSupportFragmentManager().beginTransaction().replace(id, fragment).commit();		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		SearchView sv = new SearchView(getSupportActionBar().getThemedContext());

		sv.setQueryHint("Keyword");
		
        menu.add("Search")
        .setIcon(R.drawable.abs__ic_search)
        .setActionView(sv)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

		this.getSupportMenuInflater().inflate(R.menu.lac, menu);
		

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	public void updateFrame(Frame frame) {
		contentSwitcher.update(frame);
	}
	
	public Configuration getConfig() { 
		return configuration;
	}
	
	public ServiceAccess getServiceAccess() {
		return serviceAccess;
	}
	
}
