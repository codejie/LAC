package jie.android.lac.fragment;

import jie.android.lac.R;
import jie.android.lac.app.LACActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public abstract class ContentFragment extends SherlockFragment implements OnSwitcherEventListener {
	
	private static final String TAG = "ContentFragment";
	
	private final int resourceId;
	
	public ContentFragment() {
		this(-1);
	}
	
	public ContentFragment(int resourceId) {
		this.resourceId = resourceId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (resourceId != -1) {
			View v = inflater.inflate(resourceId, container, false);		
			return v;
		} else {
			return super.onCreateView(inflater, container, savedInstanceState);
		}
	}

	@Override
	public boolean onEnter() {
		return true;
	}

	@Override
	public boolean onExit() {
		return true;
	}
	
	public LACActivity getLACActivity() {
		return (LACActivity) this.getSherlockActivity();
	}
	
	public void onIntent(Intent intent) {
		
	}

}
