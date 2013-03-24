package jie.android.lac.fragment;

import jie.android.lac.R;
import jie.android.lac.app.LACActivity;
import jie.android.lac.app.ContentSwitcher.Frame;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ToggleButton;

public class WelcomeContent extends ContentFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_welcome, container, false);
		
		ToggleButton btn = (ToggleButton) v.findViewById(R.id.toggleButton1);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onButtonClick();
			}
			
		});
		
		return v;
	}

	protected void onButtonClick() {
		LACActivity activity = (LACActivity) this.getSherlockActivity();
		
		activity.updateFrame(Frame.Setting);
	}

}
