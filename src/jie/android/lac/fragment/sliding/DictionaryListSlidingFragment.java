package jie.android.lac.fragment.sliding;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import jie.android.lac.R;
import jie.android.lac.data.Dictionary;
import jie.android.lac.fragment.BaseFragment;
import jie.android.lac.fragment.DictionaryFragment;
import jie.android.lac.fragment.data.SlidingDictionaryTitleListAdapter;

public class DictionaryListSlidingFragment extends SlidingBaseFragment {
	
	private SlidingDictionaryTitleListAdapter adapter = null;
	private ExpandableListView listView = null;

	public DictionaryListSlidingFragment(BaseFragment fragment) {
		super(fragment);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.sliding_dictionary_list, container, false);
		
		initList(v);
		
		refreshList();
		
		return v;
	}

	private void initList(View parent) {
		
		DictionaryFragment fragment = (DictionaryFragment) this.getAttachFragment();
		
		adapter = new SlidingDictionaryTitleListAdapter(fragment.getLACActivity());
		
		listView = (ExpandableListView) parent.findViewById(R.id.expandableListView1);
		
		listView.setAdapter(adapter);
	}
	
	private void refreshList() {
		DictionaryFragment fragment = (DictionaryFragment) this.getAttachFragment();		
		for (Dictionary.SimpleInfo info :fragment.getDictionarySimpleInfo()) {
			adapter.addItem(1, info.getIndex(), info.getTitle());
		}
		adapter.notifyDataSetChanged();
	}

}
