package jie.android.lac.app;

import jie.android.lac.R;
import jie.android.lac.fragment.ContentFragment;
import jie.android.lac.fragment.DictionaryFragment;
import jie.android.lac.fragment.WelcomeFragment;
import jie.android.lac.fragment.WizardFragment;

import android.support.v4.app.FragmentManager;

import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class ContentSwitcher {
	
	public enum Frame {
		Welcome("welcome", SlidingMenu.LEFT, true), Dictionary("dictionary", SlidingMenu.LEFT_RIGHT, false), 
		Memory("memory", SlidingMenu.LEFT_RIGHT, false), Wizard("wizard", -1, true);
		
		private final String name;
		private final int slidingMode;
		private final boolean removed;//remove if hided
		private Frame(final String name, int mode, boolean removed) {
			this.name = name;
			this.slidingMode = mode;
			this.removed = removed;
		}
		
		public final String getName() {
			return name;
		}
		
		public int getSlidingMode() {
			return slidingMode;
		}
		
		public boolean hasRemoved() {
			return removed;
		}
	}
	
	private final SlidingFragmentActivity activity;

	private Frame currentFrame = null;
	
	private Frame prevFrame = null;

	public ContentSwitcher(final SlidingFragmentActivity activity) {
		this.activity = activity;
	}
	
	public boolean update(final Frame frame) {
		if(frame == currentFrame)
			return true;
		
		if(!hideCurrentFrame()) {
			return false;
		}
		
		updataSlidingMenu(frame.getSlidingMode());

		if (!showFrame(frame)) {
			return false;
		}

		prevFrame = currentFrame;		
		currentFrame = frame;
		
		return true;		
	}

	private boolean showFrame(Frame frame) {
				
		ContentFragment fragment = (ContentFragment) this.activity.getSupportFragmentManager().findFragmentByTag(frame.getName());
		
		if(fragment == null) {
			switch(frame) {
			case Welcome:
				fragment = createWelcomeFrame();
				break;
			case Dictionary:
				fragment = createDictionaryFrame();
				break;
			case Wizard:
				fragment = createWizardFrame();
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

	public boolean removeFrame(final Frame frame) {
		if(frame == currentFrame) {
			return false;
		}
		
		ContentFragment fragment = (ContentFragment) this.activity.getSupportFragmentManager().findFragmentByTag(frame.getName());
		if (fragment == null) {
			return true;
		}
		
		FragmentManager fm = this.activity.getSupportFragmentManager();
		fm.beginTransaction().remove(fragment).commit();		
		
		return true;
	}
	
	private void updataSlidingMenu(int slidingMode) {
		SlidingMenu slidingmenu = this.activity.getSlidingMenu();
		
		if(slidingMode != -1) {
			slidingmenu.setMode(slidingMode);
		} else {
			slidingmenu.setSlidingEnabled(false);
		}
	}

	private boolean addFragment(final Frame frame, final ContentFragment fragment) {
		if(fragment == null || !fragment.onPrepareEnter()) {
			return false;
		}
		
		FragmentManager fm = this.activity.getSupportFragmentManager();
		fm.beginTransaction().add(R.id.lac, fragment, frame.getName()).commit();
		
		return true;
	}
	
	private boolean showFragment(final Frame frame, final ContentFragment fragment) {
		if(fragment == null) {
			return false;
		}
		
		FragmentManager fm = this.activity.getSupportFragmentManager();
		fm.beginTransaction().show(fragment).commit();
		
		return true;
	}
	
	private boolean hideCurrentFrame() {
		if (currentFrame == null) {
			return true;
		}
			
		ContentFragment fragment = (ContentFragment) this.activity.getSupportFragmentManager().findFragmentByTag(currentFrame.getName());
		
		if(fragment == null) {
			return true;
		}
		
		if(!fragment.onPrepareExit()) {
			return false;
		}

		FragmentManager fm = this.activity.getSupportFragmentManager();
		if (!currentFrame.hasRemoved()) {
			fm.beginTransaction().hide(fragment).commit();
		} else {
			fm.beginTransaction().remove(fragment).commit();
		}
		
		currentFrame = null;
		
		return true;
	}
	
	public final Frame getCurrentFrame() {
		return currentFrame;
	}
	
	private ContentFragment createWelcomeFrame() {
		return new WelcomeFragment();
	}

	private ContentFragment createDictionaryFrame() {
		return new DictionaryFragment();
	}
	
	private ContentFragment createWizardFrame() {
		return new WizardFragment();
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
