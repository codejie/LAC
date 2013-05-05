package jie.android.lac.fragment.data;

import java.util.ArrayList;
import java.util.List;

import jie.android.lac.app.ServiceAccess;
import jie.android.lac.data.Word;
import jie.android.lac.service.aidl.Access;

import android.content.Context;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DictionaryFragmentListAdapter extends BaseAdapter {

	private static final String Tag = DictionaryFragmentListAdapter.class.getName();
	
	public static interface OnRefreshResultListener {
		public void onLoadResultEnd(int count,int total);
	}
	
	private Context context = null;
	private Access access = null;
	private OnRefreshResultListener resultListener = null;
	
	private ArrayList<Word.Info> dataArray = new ArrayList<Word.Info>();
	
	private int maxItem = 15;
	private String condition = null;
	
	public DictionaryFragmentListAdapter(Context context, ServiceAccess service, OnRefreshResultListener resultListener) {
		this.context = context;
		this.access = service.getAccess();
		this.resultListener = resultListener;
	}
	
	@Override
	public int getCount() {
		return dataArray.size();
	}

	@Override
	public Object getItem(int position) {
		return dataArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		return dataArray.get(position).getId();
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		if(view == null) {
			view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, viewGroup, false);
		}
		TextView tv = (TextView) view.findViewById(android.R.id.text1);
		tv.setText(dataArray.get(position).getWord());

		return view;
	}
	
	public void load(final String condition) {
		this.condition= condition;
		
		dataArray.clear();
		
		refresh();
	}
	
	public void refresh() {
		new LoadDataTask().execute();
	}

	private class LoadDataTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			try {
				List<Word.Info> l = access.queryWordInfo(condition, dataArray.size(), maxItem);
				if (l != null) {
					dataArray.addAll(l);
					return l.size();
				} else {
					return -1;
				}
			} catch (RemoteException e) {
				return -1;
			}
		}

		@Override
		protected void onPostExecute(Integer result) {
			DictionaryFragmentListAdapter.this.notifyDataSetChanged();
			if (resultListener != null) {
				resultListener.onLoadResultEnd(result.intValue(), dataArray.size());
			}
			super.onPostExecute(result);
		}	
		
	}

}
