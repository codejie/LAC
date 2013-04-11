package jie.android.lac.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ViewFlipper;
import jie.android.lac.R;
import jie.android.lac.app.LACActivity;
import jie.android.lac.app.ContentSwitcher.Frame;

public class WizardFragment extends ContentFragment implements OnClickListener {
	
	private int indexPage = 0;
	private ViewFlipper flipper = null;
	private Button btnNavigate = null;
	
	public WizardFragment() {
		super(R.layout.fragment_wizard);
	}	
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		LayoutInflater inflater = this.getLayoutInflater(savedInstanceState);
		
		initFlipper(inflater);
		
		initViews();
	}

	private void initFlipper(LayoutInflater inflater) {
		
		flipper = (ViewFlipper) this.getView().findViewById(R.id.flipper);
		
		flipper.addView(inflater.inflate(R.layout.fragment_wizard_setdatalocation, null));
		flipper.addView(inflater.inflate(R.layout.fragment_wizard_finished, null));		
	}

	private void initViews() {
		View view = this.getView();
		
		btnNavigate = (Button) view.findViewById(R.id.btnNavigate);
		btnNavigate.setOnClickListener(this);
		btnNavigate.setText(R.string.lac_wizard_next);
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.btnNavigate:
			onBtnNavigate();
			break;
		}		
	}

	private void onBtnNavigate() {
		++ indexPage;
		if (indexPage < flipper.getChildCount()) {
			flipper.showNext();
		} else {
			LACActivity activity = (LACActivity) this.getSherlockActivity();			
			activity.updateFrame(Frame.Welcome);
		}
	}
}
