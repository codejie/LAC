package jie.android.lac.fragment;

import jie.android.lac.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class TestFragment extends BaseFragment {

	private ExpandableListView list = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_test, container, false);
		
		initListView(v);
	
		return v;
	}

	private void initListView(View v) {
		list = (ExpandableListView) v.findViewById(R.id.expandableListView1);
		
		list.setad
	}

}
