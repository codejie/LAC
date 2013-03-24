package jie.android.lac.fragment;

import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragment;

public abstract class ContentFragment extends SherlockFragment implements OnSwitcherEventListener {
	
	@Override
	public boolean OnPrepareEnter() {
		return true;
	}

	@Override
	public boolean OnPrepareExit() {
		return true;
	}

}
