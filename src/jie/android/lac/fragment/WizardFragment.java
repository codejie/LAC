package jie.android.lac.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ViewFlipper;
import jie.android.lac.R;
import jie.android.lac.app.LACActivity;
import jie.android.lac.app.ContentSwitcher.Frame;

public class WizardFragment extends ContentFragment implements OnClickListener, OnFocusChangeListener {
	
	private int indexPage = 0;
	private ViewFlipper flipper = null;
	private Button btnNavigate = null;
	
	private View viewLocation = null;
	private View viewFinished = null;
	
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
		
		viewLocation = inflater.inflate(R.layout.fragment_wizard_setdatalocation, null);
		viewFinished = inflater.inflate(R.layout.fragment_wizard_finished, null);
		
		flipper.addView(viewLocation);
		flipper.addView(viewFinished);
		
		flipper.setOnFocusChangeListener(this);
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

	@Override
	public void onFocusChange(View view, boolean hasFocus) {
		if (view == viewLocation) {
			
			if (this.getLACActivity().getConfig().getDataInCard() != 1) {
				((RadioButton)view.findViewById(R.id.radio1)).setChecked(true);
			} else {
				((RadioButton)view.findViewById(R.id.radio0)).setChecked(true);
			}
			
			RadioGroup rg = (RadioGroup) view.findViewById(R.id.radioGroup1);
			
			rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup group, int id) {
					onDataStorageChanged(id == R.id.radio1);
				}				
			});
		} else if (view == viewFinished) {
			
		}
	}
	
	protected void onDataStorageChanged(boolean isCard) {
		// TODO Auto-generated method stub
		
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
