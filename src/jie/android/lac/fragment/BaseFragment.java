package jie.android.lac.fragment;

import jie.android.lac.R;

import jie.android.lac.app.FragmentSwitcher.FragmentType;
import jie.android.lac.app.LACActivity;
import jie.android.lac.fragment.sliding.SlidingBaseFragment;
import android.content.Intent;
import android.view.KeyEvent;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;

public abstract class BaseFragment extends SherlockFragment {
	
	private int menuId = -1;
	private SlidingBaseFragment leftFragment = null;
	private SlidingBaseFragment rightFragment = null;
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		
		if (menuId != -1) {
			menu.clear();
			getLACActivity().getSupportMenuInflater().inflate(menuId, menu);
		}
	}	
	
	public LACActivity getLACActivity() {
		return (LACActivity) getSherlockActivity();
	}
	
	public void setSlidingMenu(final SlidingBaseFragment left, final SlidingBaseFragment right) {
		
		if (left != null && right != null) {
			getLACActivity().getSlidingMenu().setMode(SlidingMenu.LEFT_RIGHT);
			
			replaceSlidingFragment(true, left);
			replaceSlidingFragment(false, right);
			
			getLACActivity().getSlidingMenu().setSlidingEnabled(true);
		} else if (left != null) {
			getLACActivity().getSlidingMenu().setMode(SlidingMenu.LEFT);
			
			replaceSlidingFragment(true, left);
			
			getLACActivity().getSlidingMenu().setSlidingEnabled(true);
		} else if (right != null) {
			getLACActivity().getSlidingMenu().setMode(SlidingMenu.RIGHT);
			
			replaceSlidingFragment(false, right);
			
			getLACActivity().getSlidingMenu().setSlidingEnabled(true);
		} else {
			getLACActivity().getSlidingMenu().setSlidingEnabled(false);
		}
		
		leftFragment = left;
		rightFragment = right;
	}
	
	protected void replaceSlidingFragment(boolean left, final SlidingBaseFragment fragment) {
		getLACActivity().getSupportFragmentManager().beginTransaction().replace((left ? R.id.lac_left : R.id.lac_right), fragment).commit();
	}
	
	protected void showFragment(final FragmentType type) {
		getLACActivity().showFragment(type);
	}
	
	protected void setOptionsMenu(int menuId) {
		if (this.menuId != menuId) {
			this.menuId = menuId;
			setHasOptionsMenu(true);
			getLACActivity().supportInvalidateOptionsMenu();
		}
	}
	
//	public boolean onOptionsItemSelected(MenuItem item) {
//		return false;
//	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}
	
	public void onIntent(final Intent intent) {
		
	}
	
	public final SlidingBaseFragment getLeftSlidingFragment() {
		return leftFragment;
	}
	
	public final SlidingBaseFragment getRightSlidingFragment() {
		return rightFragment;
	}
	
	public void enableSlidingFragment(boolean enableLeft, boolean enableRight) {
		if (leftFragment != null && rightFragment != null) {
			if (enableLeft && enableRight) {
				getLACActivity().getSlidingMenu().setMode(SlidingMenu.LEFT_RIGHT);
				getLACActivity().getSlidingMenu().setSlidingEnabled(true);
			} else if (enableLeft) {
				getLACActivity().getSlidingMenu().setMode(SlidingMenu.LEFT);
				getLACActivity().getSlidingMenu().setSlidingEnabled(true);
			} else if (enableRight) {
				getLACActivity().getSlidingMenu().setMode(SlidingMenu.RIGHT);
				getLACActivity().getSlidingMenu().setSlidingEnabled(true);
			} else {
				getLACActivity().getSlidingMenu().setSlidingEnabled(false);
			}
		} else if (leftFragment != null) {
			if (enableLeft) {
				getLACActivity().getSlidingMenu().setMode(SlidingMenu.LEFT);
				getLACActivity().getSlidingMenu().setSlidingEnabled(true);
			} else {
				getLACActivity().getSlidingMenu().setSlidingEnabled(false);
			}
		} else {
			if (enableRight) {
				getLACActivity().getSlidingMenu().setMode(SlidingMenu.RIGHT);
				getLACActivity().getSlidingMenu().setSlidingEnabled(true);
			} else {
				getLACActivity().getSlidingMenu().setSlidingEnabled(false);
			}			
		}
	}

}
