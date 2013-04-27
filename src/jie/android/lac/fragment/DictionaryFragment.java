package jie.android.lac.fragment;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnPullEventListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import jie.android.lac.R;
import jie.android.lac.fragment.data.DictionaryFragmentListAdapter;
import jie.android.lac.fragment.data.DictionaryFragmentListAdapter.OnRefreshResultListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class DictionaryFragment extends ContentFragment implements OnRefreshResultListener, OnRefreshListener<ListView>, OnItemClickListener  {
	
	private static final String Tag = DictionaryFragment.class.getName();

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
		
		adapter = new DictionaryFragmentListAdapter(this.getLACActivity(), this.getLACActivity().getServiceAccess(), this);
		
		pullList = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);
		pullList.setMode(Mode.PULL_FROM_END);
		pullList.setOnRefreshListener(this);
	
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
		adapter.load("z");
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		new GetDataTask().execute();
//		adapter.refresh();
//		pullList.onRefreshComplete();		
	}	

	@Override
	public void onLoadResult(int count, int total) {
	}

	@Override
	public void onLoadResultEnd(int count, int total) {
//		pullList.getRefreshableView().smoothScrollToPosition(total - count + 5);
//		State state = pullList.getState();
//		Log.d(Tag, "current state : " + state);
	}
	
	@Override
	public void onNoResult() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNomoreResult(int total) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAceessFailed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onIntent(intent);
	}

	private class GetDataTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			adapter.refresh();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			adapter.notifyDataSetChanged();
			pullList.onRefreshComplete();
			super.onPostExecute(result);
		}

	}	
	
}





