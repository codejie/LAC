package jie.android.lac.app;

import jie.android.lac.R;
import jie.android.lac.fragment.ContentFragment;
import jie.android.lac.fragment.SettingContent;
import jie.android.lac.fragment.WelcomeContent;

import android.support.v4.app.FragmentManager;

import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class ContentSwitcher {
	
	public enum Frame {
		Welcome("welcome", SlidingMenu.LEFT), Dictionary("dictionary", SlidingMenu.LEFT_RIGHT), 
		Memory("memory", SlidingMenu.LEFT_RIGHT), Setting("setting", SlidingMenu.LEFT_RIGHT);
		
		private final String name;
		private final int slidingMode;
		private Frame(final String name, int mode) {
			this.name = name;
			this.slidingMode = mode;
		}
		
		public final String getName() {
			return name;
		}
		
		public final int getSlidingMode() {
			return slidingMode;
		}
	}
	
	private final SlidingFragmentActivity activity;

	private Frame currentFrame = Frame.Welcome; 
	private ContentFragment currentFragment = null;

	public ContentSwitcher(final SlidingFragmentActivity activity) {
		this.activity = activity;
	}
	
	public boolean update(final Frame frame) {
		if(!hideFragment(currentFragment)) {
			return false;
		}
		
		updataSlidingMenu(frame.getSlidingMode());	
		
		ContentFragment fragment = (ContentFragment) this.activity.getSupportFragmentManager().findFragmentByTag(frame.getName());
		if(fragment == null) {
			switch(frame) {
			case Welcome:
				fragment = createWelcomeFrame();
				break;
			case Setting:
				fragment = createSettingFrame();
				break;
			default:
				return false;
			}

			if(!addFragment(frame, fragment)) {
				return false;
			}
		} else {
			if(!!showFragment(frame, fragment)) {
				return false;
			}
		}
				
		return true;
	}

	private void updataSlidingMenu(int slidingMode) {
		SlidingMenu slidingmenu = this.activity.getSlidingMenu();
		slidingmenu.setMode(slidingMode);
	}

	private boolean addFragment(final Frame frame, final ContentFragment fragment) {
		if(fragment == null || !fragment.OnPrepareEnter()) {
			return false;
		}
				
		FragmentManager fm = this.activity.getSupportFragmentManager();
		fm.beginTransaction().add(R.id.lac, fragment, frame.getName()).commit();
				
		currentFrame = frame;
		currentFragment = fragment;	
		
		return true;
	}
	
	private boolean showFragment(final Frame frame, final ContentFragment fragment) {
		if(fragment == null) {
			return false;
		}
		
		FragmentManager fm = this.activity.getSupportFragmentManager();
		fm.beginTransaction().show(fragment).commit();
		
		currentFrame = frame;
		currentFragment = fragment;	
		return true;
	}
	
	private boolean hideFragment(final ContentFragment fragment) {
		if(fragment == null) {
			return true;
		}
		
		if(!fragment.OnPrepareExit()) {
			return false;
		}

		FragmentManager fm = this.activity.getSupportFragmentManager();
		fm.beginTransaction().hide(fragment).commit();
		currentFrame = null;
		currentFragment = null;
		
		return true;
	}	
	
	private ContentFragment createWelcomeFrame() {
		return new WelcomeContent();
	}

	private ContentFragment createSettingFrame() {
		return new SettingContent();
	}	
	
	public final Frame getCurrentFrame() {
		return currentFrame;
	}

	public final ContentFragment getCurrentFragment() {
		return currentFragment;
	}
	
	
//	private boolean replaceFragment(final Frame frame, final ContentFragment content) {
//		
//		if(content == null || !content.OnPrepareEnter()) {
//			return false;
//		}
//		
//		SlidingMenu slidingmenu = this.activity.getSlidingMenu();
//		FragmentManager fm = this.activity.getSupportFragmentManager();
//		
//		if(left == null && right == null) {
//			slidingmenu.setSlidingEnabled(false);
//		} else if(left != null && right != null) {
//			slidingmenu.setMode(SlidingMenu.LEFT_RIGHT);
//			fm.beginTransaction().replace(R.id.lac_left, left).commit();
//			fm.beginTransaction().replace(R.id.lac_right, right).commit();
//		} else if(left != null) {
//			slidingmenu.setMode(SlidingMenu.LEFT);
//			fm.beginTransaction().replace(R.id.lac_left, left).commit();
//		} else {
//			slidingmenu.setMode(SlidingMenu.RIGHT);
//			fm.beginTransaction().replace(R.id.lac_right, right).commit();
//		}
//		
//		fm.beginTransaction().add .replace(R.id.lac, content, frame).commit();
//		
//		currentFrame = frame;
//		currentFragment = content;
//		
//		return true;
//	}

//	private ContentFragment updateToWelcomeFrame() {
//		FragmentManager fm = this.activity.getSupportFragmentManager();
//		
//		
////		if(fm.findFragmentByTag(arg0))
////		
////		return replaceFragment(Frame.Welcome, new WelcomeFragment(), null, null);
//		return null;
//	}	
}
