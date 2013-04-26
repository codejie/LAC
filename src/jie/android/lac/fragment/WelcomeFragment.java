package jie.android.lac.fragment;

import java.io.IOException;
import java.io.InputStream;

import jie.android.lac.R;
import jie.android.lac.app.LACActivity;
import jie.android.lac.app.ContentSwitcher.Frame;
import jie.android.lac.service.AssetsHelper;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ToggleButton;

public class WelcomeFragment extends ContentFragment {

	private static final String TAG = "WelcomeContent";

	public WelcomeFragment() {
		super(R.layout.fragment_welcome);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		
		Log.d(TAG, "onCreateView");
		
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
		//initData(0);
		
		
		LACActivity activity = this.getLACActivity();//(LACActivity) this.getSherlockActivity();
		
		activity.updateFrame(Frame.Dictionary);// .Wizard);// .Dictionary);// .Setting);
	}
	
	private void initData(int flag) {

		String target = this.getLACActivity().getDatabasePath("lac").getParent();// .getAbsolutePath();// .getd .getFilesDir().getAbsolutePath();//  Environment.getDataDirectory().getAbsolutePath();
		
		try {
			InputStream input = this.getLACActivity().getAssets().open("lac2.zip");// this.getAssets().open("lac2.zip");
			AssetsHelper.UnzipTo(input, target);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	

}
