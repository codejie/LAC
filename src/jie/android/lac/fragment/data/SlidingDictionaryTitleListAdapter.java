package jie.android.lac.fragment.data;

import java.util.ArrayList;
import java.util.HashMap;

import jie.android.lac.R;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;

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
	
	private ArrayList<String> groupData = null;
	private HashMap<Integer, ArrayList<ItemData>> data = null;
	
	private final Context context;
	
	public SlidingDictionaryTitleListAdapter(Context context) {
		this.context = context;
		
		initData();
	}
	
	private void initData() {
		groupData = new ArrayList<String>();
		data = new HashMap<Integer, ArrayList<ItemData>>();
		
		groupData.add(0, context.getString(R.string.lac_dictionarygroup_internal));
		groupData.add(1, context.getString(R.string.lac_dictionarygroup_bundled));
		groupData.add(2, context.getString(R.string.lac_dictionarygroup_external));
		groupData.add(3, context.getString(R.string.lac_dictionarygroup_online));
		
		data.put(0, new ArrayList<ItemData>());
		data.get(0).add(new ItemData(0, "memory"));
		data.get(0).add(new ItemData(1, "default"));
		
		data.put(1, new ArrayList<ItemData>());
		data.get(1).add(new ItemData(11, "Vicon EC"));
		data.get(1).add(new ItemData(12, "Vicon CE"));
		
		data.put(3, new ArrayList<ItemData>());
		data.get(3).add(new ItemData(21, "GOOGLE"));
	}
	
	public void addItem(int group, int id, final String title) {
		if (data.get(group) == null) {
			data.put(group, new ArrayList<ItemData>());
		}
		data.get(group).add(new ItemData(id, title));
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return data.get(groupData).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		ItemData item = data.get(groupData).get(childPosition);
		if (item != null) {
			return item.id;
		}
		return -1;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getCombinedChildId(long groupId, long childId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getCombinedGroupId(long groupId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
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
