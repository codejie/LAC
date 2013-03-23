package jie.android.lac.app;

import jie.android.lac.R;
import jie.android.lac.fragment.BaseFragment;
import jie.android.lac.fragment.WelcomeFragment;

import android.support.v4.app.FragmentManager;

import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class FragmentSwitcher {
	
	public enum Frame {
		Welcome, Dictionary, Memory, Setting
	}
	
	private final SlidingFragmentActivity activity;

	private Frame currentFrame = Frame.Welcome; 
	private BaseFragment currentFragment = null;

	public FragmentSwitcher(final SlidingFragmentActivity activity) {
		this.activity = activity;
	}
	
	public boolean update(final Frame frame) {
		if(currentFragment != null) {
			if(!currentFragment.OnPrepareExit()) {
				return false;
			}
		}
		
		switch(frame) {
		case Welcome:
			return updateToWelcomeFrame();
		default:
			return false;
		}
	}
	
	private void enableSlidingMenu(boolean enabled) {
		SlidingMenu slidingmenu = this.activity.getSlidingMenu();
		slidingmenu.setSlidingEnabled(false);		
	}
	
	private boolean replaceFragment(final Frame frame, final BaseFragment content, final BaseFragment left, final BaseFragment right) {
		
		if(content == null || !content.OnPrepareEnter()) {
			return false;
		}
		
		SlidingMenu slidingmenu = this.activity.getSlidingMenu();
		FragmentManager fm = this.activity.getSupportFragmentManager();
		
		if(left == null && right == null) {
			slidingmenu.setSlidingEnabled(false);
		} else if(left != null && right != null) {
			slidingmenu.setMode(SlidingMenu.LEFT_RIGHT);
			fm.beginTransaction().replace(R.id.lac_left, left).commit();
			fm.beginTransaction().replace(R.id.lac_right, right).commit();
		} else if(left != null) {
			slidingmenu.setMode(SlidingMenu.LEFT);
			fm.beginTransaction().replace(R.id.lac_left, left).commit();
		} else {
			slidingmenu.setMode(SlidingMenu.RIGHT);
			fm.beginTransaction().replace(R.id.lac_right, right).commit();
		}
		
		this.activity.getSupportFragmentManager().beginTransaction().replace(R.id.lac, content).commit();
		
		currentFrame = frame;
		currentFragment = content;
		
		return true;
	}
	

	private boolean updateToWelcomeFrame() {	
		return replaceFragment(Frame.Welcome, new WelcomeFragment(), null, null);
	}	
}
