package jie.android.lac.app;

import jie.android.lac.R;
import jie.android.lac.fragment.ContentFragment;
import jie.android.lac.fragment.DictionaryFragment;
import jie.android.lac.fragment.WelcomeFragment;
import jie.android.lac.fragment.WizardFragment;

import android.content.Intent;
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
		return update(frame, null);	
	}
	
	public boolean update(final Frame frame, Intent intent) {

		if(frame != currentFrame) {
			
			if(!hideCurrentFrame()) {
				return false;
			}
			
			updataSlidingMenu(frame.getSlidingMode());
	
			if (!showFrame(frame, intent)) {
				return false;
			}
	
			prevFrame = currentFrame;
			currentFrame = frame;
			
			return true;
		} else {
			return showCurrentFrame(intent);
		}
	}

	private boolean showFrame(Frame frame, Intent intent) {
				
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
		
		fragment.onIntent(intent);
		
		return true;
	}
	
	private boolean showCurrentFrame(Intent intent) {
		if (currentFrame == null) {
			return false;
		}
		
		ContentFragment fragment = (ContentFragment) this.activity.getSupportFragmentManager().findFragmentByTag(currentFrame.getName());
		if (fragment == null) {
			return false;
		}
		
		fragment.onIntent(intent);
		
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
		if(fragment == null || !fragment.onEnter()) {
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
		
		if(!fragment.onExit()) {
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
	
	public void postIntent(Frame frame) {
		update(frame);		
	}
}
