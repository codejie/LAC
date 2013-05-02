package jie.android.lac.fragment;

import jie.android.lac.R;

import jie.android.lac.app.FragmentSwitcher.FragmentType;
import jie.android.lac.app.LACActivity;
import android.content.Intent;
import android.view.KeyEvent;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.slidingmenu.lib.SlidingMenu;

public abstract class BaseFragment extends SherlockFragment {
	
	private int menuId = -1;

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		
		if (menuId != -1) {
			menu.clear();
			getLACActivity().getSupportMenuInflater().inflate(menuId, menu);
		}
	}	
	
	protected LACActivity getLACActivity() {
		return (LACActivity) getSherlockActivity();
	}
	
	protected void setSlidingMenu(final SlidingBaseFragment left, final SlidingBaseFragment right) {
		
		if (left != null && right != null) {
			getLACActivity().getSlidingMenu().setMode(SlidingMenu.LEFT_RIGHT);
			
			replaceSlidingFragment(true, left);
			replaceSlidingFragment(false, right);			
		} else if (left != null) {
			getLACActivity().getSlidingMenu().setMode(SlidingMenu.LEFT);
			
			replaceSlidingFragment(true, left);
		} else if (right != null) {
			getLACActivity().getSlidingMenu().setMode(SlidingMenu.RIGHT);
			
			replaceSlidingFragment(false, right);
		} else {
			getLACActivity().getSlidingMenu().setSlidingEnabled(false);
		}
	}
	
	protected void replaceSlidingFragment(boolean left, final SlidingBaseFragment fragment) {
		getLACActivity().getSupportFragmentManager().beginTransaction().replace((left ? R.id.lac_left : R.id.lac_right), fragment).commit();
	}
	
	public void showFragment(final FragmentType type) {
		getLACActivity().showFragment(type);
	}
	
	protected void setOptionsMenu(int menuId) {
		this.menuId = menuId;
		getLACActivity().supportInvalidateOptionsMenu();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}
	
	public void onIntent(final Intent intent) {
		
	}	
}
