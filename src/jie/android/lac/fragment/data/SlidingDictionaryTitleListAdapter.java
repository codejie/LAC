package jie.android.lac.fragment.data;

import java.util.ArrayList;
import java.util.HashMap;

import jie.android.lac.R;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

public class SlidingDictionaryTitleListAdapter implements ExpandableListAdapter {

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
		
		data.get(0).addItem(0, "memory");
		data.get(0).addItem(1, "default");
		
		data.get(1).addItem(11, "Vicon EC");
		data.get(1).addItem(12, "Vicon CE");
		
		data.get(3).addItem(11, "GOOGLE");
	}
	
	public void addItem(int group, int id, final String title) {
		if (data.get(group) != null) {
			data.get(group).addItem(id, title);
		}
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
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
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.sliding_dictionary_title_list_item, null);
		
		Log.d("==", "groupPos:" + groupPosition + " childPos:" + childPosition);
		
		TextView tv = (TextView) v.findViewById(R.id.textView1);
		tv.setText(data.get(groupPosition).item.get(childPosition).title);
		
		return v;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return data.get(groupPosition).item.size();
	}

	@Override
	public long getCombinedChildId(long groupId, long childId) {
		long id = (groupId << 16) | childId;
		Log.d("==", "id=" + id + " groupId:" + groupId + " childId:" + childId);
		return id;
	}

	@Override
	public long getCombinedGroupId(long groupId) {
		return groupId;
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
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return data.size() == 0;
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		
	}

}
