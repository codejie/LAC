package jie.android.lac.fragment;

import com.actionbarsherlock.app.SherlockFragment;

public class BaseFragment extends SherlockFragment implements OnSwitcherEventListener {

	@Override
	public boolean OnPrepareEnter() {
		return true;
	}

	@Override
	public boolean OnPrepareExit() {
		return true;
	}

}
