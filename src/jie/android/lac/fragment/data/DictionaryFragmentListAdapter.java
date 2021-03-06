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

	private class LoadDataTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			isloading = true;
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
				resultListener.onLoadResultEnd(result.intValue(), dataArray.size(), maxItem);
			}
			
			super.onPostExecute(result);
			isloading = false;			
		}
	}	

	private static final String Tag = DictionaryFragmentListAdapter.class.getName();
	
	public static interface OnRefreshResultListener {
		public void onLoadResultEnd(int count,int total, int maxPerPage);
	}
	
	private Context context = null;
	private Access access = null;
	private OnRefreshResultListener resultListener = null;
	
	private ArrayList<Word.Info> dataArray = new ArrayList<Word.Info>();
	
	private int maxItem = 15;
	private String condition = null;
	
	private Boolean isloading = false;
	
	public DictionaryFragmentListAdapter(Context context, Access access, OnRefreshResultListener resultListener) {
		this.context = context;
		this.access = access;
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
		return dataArray.get(position).getIndex();
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		if(view == null) {
			view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, viewGroup, false);
		}
		TextView tv = (TextView) view.findViewById(android.R.id.text1);
		tv.setText(dataArray.get(position).getText());

		return view;
	}
	
	public void load(final String condition) {
		this.condition= condition;
		
		dataArray.clear();
		
		refresh();
	}
	
	public void refresh() {
		synchronized(isloading) {
			if (!isloading) {
				new LoadDataTask().execute();
			}
		}
	}

	public void setMaxPrePage(int value) {
		maxItem = value;
	}
}
