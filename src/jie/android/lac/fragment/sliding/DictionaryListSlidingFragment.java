package jie.android.lac.fragment.sliding;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import jie.android.lac.R;
import jie.android.lac.data.Dictionary;
import jie.android.lac.fragment.BaseFragment;
import jie.android.lac.fragment.DictionaryFragment;

public class DictionaryListSlidingFragment extends SlidingBaseFragment {

	
	private class DictionaryListAdapter extends BaseAdapter {
		
		private final Context context;
		private List<Dictionary.SimpleInfo> array = new ArrayList<Dictionary.SimpleInfo>();

		public DictionaryListAdapter(Context context) {
			this.context = context;
		}
		
		@Override
		public int getCount() {
			return array.size();
		}

		@Override
		public Object getItem(int position) {
			return array.get(position);
		}

		@Override
		public long getItemId(int position) {
			return array.get(position).getIndex();
		}

		@Override
		public View getView(int position, View view, ViewGroup viewGroup) {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, viewGroup, false);
			}
			
			TextView tv = (TextView) view.findViewById(android.R.id.text1);
			tv.setText(array.get(position).getTitle());
			
			return view;
		}
		
		public void update(final List<Dictionary.SimpleInfo> value) {
			array = value;
			notifyDataSetChanged();
		}		
	}
	
	private DictionaryListAdapter adapter = null;
	private ListView listView = null;

	public DictionaryListSlidingFragment(BaseFragment fragment) {
		super(fragment);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.sliding_dictionary_list, container, false);
		
		initList(v);
		
		return v;
	}

	private void initList(View parent) {
		
		DictionaryFragment fragment = (DictionaryFragment) this.getAttachFragment();
		
		adapter = new DictionaryListAdapter(fragment.getLACActivity());

		
		listView = (ListView) parent.findViewById(R.id.listView1);
		
		listView.setAdapter(adapter);
		
		adapter.update(fragment.getDictionarySimpleInfo());
	}
	
	

}
