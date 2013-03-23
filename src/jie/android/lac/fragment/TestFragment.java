package jie.android.lac.fragment;

import jie.android.lac.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public class TestFragment extends SherlockFragment {

	
	
	public TestFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
//		RelativeLayout rl = new RelativeLayout(getActivity());
//		rl.setBackgroundResource(R.color.white);
		
		View v = inflater.inflate(R.layout.test_fragment, container, false);
//		rl.addView(v);
		final Activity atv = this.getActivity();
		Button btn = (Button) v.findViewById(R.id.button1);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast.makeText(atv, "button", Toast.LENGTH_LONG).show();
			}
			
		});
		//View v = inflater.inflate(R.layout.hello_world, container, false);
		return v;

//		return super.onCreateView(inflater, container, savedInstanceState);
	}

}
