package jie.android.lac.app;

import com.actionbarsherlock.app.SherlockActivity;

import jie.android.lac.R;
import android.os.Bundle;

public class LACActivity extends SherlockActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.lac);
		
		initSlidingMenu();
	}

	private void initSlidingMenu() {		
		
	}

}
