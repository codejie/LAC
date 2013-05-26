package jie.android.lac.fragment.data;

import java.util.ArrayList;
import java.util.HashMap;

import jie.android.lac.R;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SlidingDictionaryTitleListAdapter extends BaseExpandableListAdapter { //implements ExpandableListAdapter {
	
	public interface OnChildCheckedChangeListener {
		public void OnCheckedChange(ViewGroup parent, View view, int groupPosition, int childPosition, boolean checked);
	}

	private class ItemData {
		public boolean isChecked = false;
		public int id = -1;
		public String title = null;
		
		public ItemData(int id, final String title) {
			this.isChecked = true;
			this.id = id;
			this.title = title;
		}
	}
	
	private class GroupData {
		public int index = -1;
		public String title = null;
		public ArrayList<ItemData> item = new ArrayList<ItemData>();
		
		public GroupData(int index, final String title) {
			this.index = index;
			this.title = title;
		}
		
		public void addItem(int id, final String title) {
			item.add(new ItemData(id, title));
		}
	}
	
	private ArrayList<GroupData> data = null;
	
	private final Context context;
	
	public OnChildCheckedChangeListener onChildCheckChangedListener = null;
	
	public SlidingDictionaryTitleListAdapter(Context context) {
		this.context = context;
		
		initData();
	}	
	
	private void initData() {
		data = new ArrayList<GroupData>();
		
		data.add(0, new GroupData(0, context.getString(R.string.lac_dictionarygroup_internal)));
		data.add(1, new GroupData(1, context.getString(R.string.lac_dictionarygroup_bundled)));
		data.add(2, new GroupData(2, context.getString(R.string.lac_dictionarygroup_external)));
		data.add(3, new GroupData(3, context.getString(R.string.lac_dictionarygroup_online)));
		
		data.get(0).addItem(0, context.getString(R.string.lac_dictionary_internal_notebook));
		data.get(0).addItem(1, context.getString(R.string.lac_dictionary_internal_lingoshook));
	}
	
	public void addItem(int group, int id, final String title) {
		if (data.get(group) != null) {
			data.get(group).addItem(id, title);
		}
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return data.get(groupPosition).item.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return (data.get(groupPosition).item.get(childPosition).id);
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,	boolean isLastChild, View convertView, final ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View v = inflater.inflate(R.layout.sliding_dictionary_title_list_item, null);
		
		Log.d("==", "groupPos:" + groupPosition + " childPos:" + childPosition);
		
		ItemData item = data.get(groupPosition).item.get(childPosition);
		final CheckBox cb = (CheckBox) v.findViewById(R.id.checkBox1);
		cb.setChecked(item.isChecked);
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton view, boolean checked) {
				if (onChildCheckChangedListener != null) {
					onChildCheckChangedListener.OnCheckedChange(parent, v, groupPosition, childPosition, checked);
				}
			}
			
		});
		
		TextView tv = (TextView) v.findViewById(R.id.textView1);
		tv.setText(item.title);
		
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.linearLayout1);
		layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				cb.setChecked(!cb.isChecked());
			}
			
		});
		
		return v;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return data.get(groupPosition).item.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return data.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return data.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return data.get(groupPosition).index;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,	View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.sliding_dictionary_title_list_group, null);
		
		TextView tv = (TextView) v.findViewById(R.id.textView1);
		tv.setText(data.get(groupPosition).title);
		
		return v;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	public void setOnChildCheckedChangeListener(OnChildCheckedChangeListener listener) {
		onChildCheckChangedListener = listener;
	}
	
}
