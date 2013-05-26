package jie.android.lac.fragment.sliding;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import jie.android.lac.R;
import jie.android.lac.data.Dictionary;
import jie.android.lac.fragment.BaseFragment;
import jie.android.lac.fragment.DictionaryFragment;
import jie.android.lac.fragment.data.SlidingDictionaryTitleListAdapter;
import jie.android.lac.fragment.data.SlidingDictionaryTitleListAdapter.OnChildCheckedChangeListener;

public class DictionaryListSlidingFragment extends SlidingBaseFragment {
	
	private static final String Tag = DictionaryListSlidingFragment.class.getSimpleName();
	
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
		adapter.setOnChildCheckedChangeListener(new OnChildCheckedChangeListener() {

			@Override
			public void OnCheckedChange(ViewGroup parent, View view, int groupPosition, int childPosition, boolean checked) {
				Log.d(Tag, "childclick:" + groupPosition + " child:" + childPosition + " checked:" + checked);
			}
			
		});
		
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
