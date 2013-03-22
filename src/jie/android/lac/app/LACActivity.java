package jie.android.lac.app;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnCloseListener;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

import jie.android.lac.R;
import jie.android.lac.fragment.ColorFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class LACActivity extends SlidingFragmentActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setTitle(R.string.app_title);
		this.getSupportActionBar().setSubtitle(R.string.app_subtitle);
		this.setContentView(R.layout.lac);
		
		initSlidingMenu();
		
		initViews();
	}

	private void initViews() {
		Button b = (Button)this.findViewById(R.id.button1);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onButtonClick();
			}
			
		});
	}

	protected void onButtonClick() {
//		this.getSlidingMenu().toggle();
//		this.getSlidingMenu().showSecondaryMenu(true);
		
		replaceSlidingFragment(R.id.fragment1, new ColorFragment(R.color.red));

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
		sm.setFadeEnabled(Configuration.Sliding.FADE_ENABLED);		
		sm.setFadeDegree(Configuration.Sliding.FADE_DEGREE);
		sm.setSecondaryMenu(R.layout.lac_right);
		sm.setSecondaryShadowDrawable(R.drawable.shadow);		
				
		replaceSlidingFragment(R.id.lac_left, new ColorFragment());
		replaceSlidingFragment(R.id.lac_right, new ColorFragment());
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

	
	
}
