package jie.android.lac.app;

import jie.android.lac.R;
import jie.android.lac.fragment.BaseFragment;
import jie.android.lac.fragment.DictionaryFragment;
import jie.android.lac.fragment.TestFragment;
import jie.android.lac.fragment.WelcomeFragment;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;

import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;

public class FragmentSwitcher {
	
	public enum FragmentType {

		Welcome("welcome", true), Dictionary("dictionary", false), 
		Memory("memory", false), Wizard("wizard", true), Test("test", true);
		
		private final String name;
		private final boolean removed;//remove if hided
		private FragmentType(final String name, boolean removed) {
			this.name = name;
			this.removed = removed;			
		}
		
		public final String getName() {
			return name;
		}
		
		public boolean hasRemoved() {
			return removed;
		}		
	}
	
	private LACActivity lacActivity = null;
	private FragmentManager fragmentManager = null;
	private FragmentType current = null;
	
	public FragmentSwitcher(final LACActivity activity) {
		lacActivity = (LACActivity) activity;
		fragmentManager = lacActivity.getSupportFragmentManager();
	}

	public final BaseFragment create(FragmentType type) {
		
		BaseFragment fragment = null;
		
		switch (type) {
		case Welcome:
			fragment = new WelcomeFragment(); 
			break;
		case Dictionary:
			fragment = new DictionaryFragment();
			break;
		case Test:
			fragment = new TestFragment();
			break;
		default:
			return null;
		}
		
		fragmentManager.beginTransaction().add(R.id.lac, fragment, type.getName()).commit();
		
		return fragment;
	}
	
	public boolean show(FragmentType type) {
		
		if (current != null && current == type) {
			return true;
		} else if (current != null) {
			hide(current);
		}

		current = null;
		
		BaseFragment fragment = (BaseFragment) fragmentManager.findFragmentByTag(type.getName());
		if (fragment == null) {
			fragment = create(type);
		}
		if (fragment == null) {
			return false;
		}
		
		fragmentManager.beginTransaction().show(fragment).commit();
		current = type;
		
		return true;
	}
	
	private void hide(FragmentType type) {
		BaseFragment fragment = (BaseFragment) fragmentManager.findFragmentByTag(type.getName());
		if (fragment != null) {
			if (type.hasRemoved()) {
				fragmentManager.beginTransaction().remove(fragment).commit();
			} else {
				fragmentManager.beginTransaction().hide(fragment).commit();
			}
		}
	}

	public boolean postIntent(FragmentType type, final Intent intent) {
		if (type == null) {
			return false;
		}
		
		BaseFragment fragment = (BaseFragment) fragmentManager.findFragmentByTag(type.getName());
		if (fragment == null) {
			return false;
		}
		
		fragment.onIntent(intent);
		
		return true;
	}
	
	public boolean postIntent(final Intent intent) {
		return postIntent(current, intent);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (current == null) {
			return false;
		}
		
		BaseFragment fragment = (BaseFragment) fragmentManager.findFragmentByTag(current.getName());
		if (fragment == null) {
			return false;
		}
		
		return fragment.onKeyDown(keyCode, event);
	}

//	public boolean onOptionsItemSelected(MenuItem item) {
//		if (current == null) {
//			return false;
//		}
//		
//		BaseFragment fragment = (BaseFragment) fragmentManager.findFragmentByTag(current.getName());
//		if (fragment == null) {
//			return false;
//		}
//		
//		return fragment.onOptionsItemSelected(item);
//	}
	
	public FragmentType getCurrentFragmentType() {
		return current;
	}

}
