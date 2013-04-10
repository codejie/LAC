package jie.android.lac.fragment;

import jie.android.lac.R;
import jie.android.lac.app.ContentSwitcher.Frame;
import jie.android.lac.app.LACActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class SettingFragment extends ContentFragment {

	public SettingFragment() {
		super(R.layout.fragment_setting);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		
		Button btn = (Button) v.findViewById(R.id.button1);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onButtonClick();
			}
			
		});
		
		return v;
	}

	protected void onButtonClick() {
		LACActivity activity = (LACActivity) this.getSherlockActivity();
		
		activity.updateFrame(Frame.Welcome);
	}

	
	
}
