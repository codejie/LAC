package jie.android.lac.fragment;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

import jie.android.lac.R;
import jie.android.lac.fragment.data.DictionaryFragmentListAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class DictionaryFragment extends ContentFragment {

	private PullToRefreshListView pullList = null;
	
	private DictionaryFragmentListAdapter adapter = null;
	
	public DictionaryFragment() {
		super(R.layout.fragment_dictionary_list);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);

		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		adapter = new DictionaryFragmentListAdapter(this.getLACActivity(), this.getLACActivity().getServiceAccess());
		
		pullList = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);
		
		pullList.setAdapter(adapter);
		
		Button btn = (Button) view.findViewById(R.id.button1);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				OnClick();
			}
			
		});
	}

	protected void OnClick() {
		adapter.load("");
	}
}
