package jie.android.lac.fragment;

import jie.android.lac.R;
import android.app.Activity;
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
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		Log.d(TAG, "onAttach");
		
	}

	@Override
	public void onDetach() {
		Log.d(TAG, "onDetach");
		
		super.onDetach();
	}

	@Override
	public boolean onPrepareEnter() {
		return true;
	}

	@Override
	public boolean onPrepareExit() {
		return true;
	}

}
