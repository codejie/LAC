package jie.android.lac.fragment;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import jie.android.lac.R;
import jie.android.lac.fragment.data.DictionaryFragmentListAdapter;
import jie.android.lac.fragment.data.DictionaryFragmentListAdapter.OnRefreshResultListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ViewSwitcher;

public class DictionaryFragment extends ContentFragment implements OnRefreshResultListener, OnRefreshListener<ListView>, OnItemClickListener  {
	
	private static final String Tag = DictionaryFragment.class.getName();

	private ViewSwitcher viewSwitcher = null;
	
	private PullToRefreshListView pullList = null;	
	private DictionaryFragmentListAdapter adapter = null;
	
	public DictionaryFragment() {
		//super(R.layout.fragment_dictionary_list);
		super(R.layout.fragment_dictionary_switcher);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);

		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		viewSwitcher = (ViewSwitcher) view.findViewById(R.id.viewSwitcher);
		
		adapter = new DictionaryFragmentListAdapter(this.getLACActivity(), this.getLACActivity().getServiceAccess(), this);
		
		pullList = (PullToRefreshListView) viewSwitcher.findViewById(R.id.pull_refresh_list);
		pullList.setMode(Mode.PULL_FROM_END);
		pullList.setOnRefreshListener(this);
		pullList.setOnItemClickListener(this);
	
		pullList.setAdapter(adapter);		
		
		Button btn = (Button) viewSwitcher.findViewById(R.id.button1);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				OnClick();
			}
			
		});
	}

	protected void OnClick() {
		adapter.load("z");
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		viewSwitcher.showNext();
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		adapter.refresh();
	}	

	@Override
	public void onLoadResultEnd(int count, int total) {
		pullList.onRefreshComplete();
	}


	@Override
	public void onIntent(Intent intent) {
		if (intent != null) {
			String condition = intent.getStringExtra("keyword");
			if (condition != null) {
				adapter.load(condition);
			}
		}
		super.onIntent(intent);
	}
	
}





