package jie.android.lac.fragment.data;

import java.util.ArrayList;

import jie.android.lac.app.ServiceAccess;
import jie.android.lac.data.WordData;
import jie.android.lac.service.aidl.Access;

import android.content.Context;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DictionaryFragmentListAdapter extends BaseAdapter {

	private Context context = null;
	private Access access = null;
	
	private ArrayList<WordData> dataArray = new ArrayList<WordData>();
	
	private int offset = 0;
	private int maxItem = 0;
	private String condition = null;
	
	public DictionaryFragmentListAdapter(Context context, ServiceAccess service) {
		this.context = context;
		this.access = service.getAccess();
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
		try {
			dataArray.addAll(access.queryWordData(condition));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.notifyDataSetChanged();
	}

}
