package jie.android.lac.fragment.sliding;

import jie.android.lac.fragment.BaseFragment;
import android.support.v4.app.Fragment;

public class SlidingBaseFragment extends Fragment {

	protected BaseFragment attachFragment = null;
	
	public SlidingBaseFragment() {
		super();
	}
	
	public SlidingBaseFragment(final BaseFragment fragment) {
		super();
		this.attachFragment = fragment;
	}
	
	public void setAttachFragment(final BaseFragment fragment) {
		attachFragment = fragment;
	}
	
	public final BaseFragment getAttachFragment() {
		return attachFragment;
	}
}
