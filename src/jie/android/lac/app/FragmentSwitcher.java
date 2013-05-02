package jie.android.lac.app;

import com.slidingmenu.lib.SlidingMenu;

public class FragmentSwitcher {
	
	public enum FragmentType {

		Welcome("welcome", true), Dictionary("dictionary", false), 
		Memory("memory", false), Wizard("wizard", true);
		
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
	
	private FragmentType current = null;

	public void create(FragmentType type);
	public boolean show(FragmentType type);
	private void hide(FragmentType type);
	private void remove(FragmentType type);
	public boolean postIntent(FragmentType type, final Intent intent);
	public boolean onKeyDown(int keyCode, KeyEvent event);
}
