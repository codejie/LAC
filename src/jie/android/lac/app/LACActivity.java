package jie.android.lac.app;

import com.actionbarsherlock.app.SherlockActivity;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingActivity;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

import jie.android.lac.R;
import jie.android.lac.fragment.ColorFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class LACActivity extends SlidingFragmentActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setTitle(R.string.app_title);
		this.setContentView(R.layout.lac);
		
		initSlidingMenu();
	}

	private void initSlidingMenu() {
		
		this.setBehindContentView(R.layout.lac_left);
		this.setSlidingActionBarEnabled(false);
		
		SlidingMenu sm = this.getSlidingMenu(); new SlidingMenu(this);
		
//		sm.attachToActivity(this, SlidingMenu.SLIDING_WINDOW | SlidingMenu.SLIDING_CONTENT);
		
		sm.setMode(SlidingMenu.LEFT_RIGHT);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setBehindOffsetRes(R.dimen.sliding_offset);
		sm.setFadeEnabled(true);
		sm.setFadeDegree(0.35f);
//		sm.setMenu(R.layout.lac_left);
		sm.setSecondaryMenu(R.layout.lac_right);
		sm.setSecondaryShadowDrawable(R.drawable.shadow);
//		
		Fragment fgt = new ColorFragment();
		
		this.getSupportFragmentManager().beginTransaction().replace(R.id.lac_left, fgt).commit();
	}

}
