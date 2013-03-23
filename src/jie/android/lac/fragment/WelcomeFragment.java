package jie.android.lac.fragment;

import jie.android.lac.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WelcomeFragment extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_welcome, container, false);
		return v;
	}

}
