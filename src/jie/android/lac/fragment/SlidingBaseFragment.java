package jie.android.lac.fragment;

import android.support.v4.app.Fragment;

public class SlidingBaseFragment extends Fragment {

	protected BaseFragment attachFragment = null;
	
	public SlidingBaseFragment(final BaseFragment fragment) {
		this.attachFragment = fragment;
	}
}
