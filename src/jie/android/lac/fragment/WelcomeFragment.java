package jie.android.lac.fragment;

import java.io.IOException;
import java.io.InputStream;

import jie.android.lac.R;
import jie.android.lac.app.FragmentSwitcher.FragmentType;
import jie.android.lac.service.AssetsHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ToggleButton;

public class WelcomeFragment extends BaseFragment {

	private static final String TAG = "WelcomeContent";

	public WelcomeFragment() {
		super();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
		getLACActivity().showFragment(FragmentType.Dictionary);
//		LACActivity activity = this.getLACActivity();//(LACActivity) this.getSherlockActivity();
//		
//		activity.updateFrame(Frame.Dictionary);// .Wizard);// .Dictionary);// .Setting);
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
