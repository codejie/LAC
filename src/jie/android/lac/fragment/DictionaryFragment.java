package jie.android.lac.fragment;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import jie.android.lac.R;
import jie.android.lac.data.Dictionary;
import jie.android.lac.data.Dictionary.SimpleInfo;
import jie.android.lac.fragment.data.DictionaryFragmentListAdapter;
import jie.android.lac.fragment.data.DictionaryFragmentListAdapter.OnRefreshResultListener;
import jie.android.lac.fragment.sliding.DictionaryListSlidingFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ViewSwitcher;

public class DictionaryFragment extends BaseFragment implements OnRefreshResultListener, OnRefreshListener<ListView>, OnItemClickListener  {
	
	private static final String Tag = DictionaryFragment.class.getSimpleName();

	private ViewSwitcher viewSwitcher = null;
	
	private PullToRefreshListView pullList = null;	
	private DictionaryFragmentListAdapter adapter = null;
	
	private WebView webView = null;
	private GestureDetector gestureDetector = null;
	
	private List<Dictionary.SimpleInfo> dictInfo = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//this.setSlidingMenu(new ColorFragment(this, R.color.red), new ColorFragment(this, R.color.white));
		this.setSlidingMenu(new DictionaryListSlidingFragment(this), null);
		this.setOptionsMenu(R.menu.dictionary);
		
		initData();
	}	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_dictionary_switcher, container, false);

		viewSwitcher = (ViewSwitcher) v.findViewById(R.id.viewSwitcher);
		
		initListView(viewSwitcher);
		initWebView(viewSwitcher);

		Button btn = (Button) viewSwitcher.findViewById(R.id.button1);
		btn.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View arg0) {
				OnClick();
			}
			
		});		
		
		return v;
	}

	private void initListView(View parent) {
		pullList = (PullToRefreshListView) parent.findViewById(R.id.pull_refresh_list);
		pullList.setMode(Mode.PULL_FROM_END);
			
		//listener
		pullList.setOnRefreshListener(this);
		pullList.setOnItemClickListener(this);
		
		//adapter
		adapter = new DictionaryFragmentListAdapter(getLACActivity(), getLACActivity().getServiceAccess(), this);		
		pullList.setAdapter(adapter);
	}

	private void initWebView(View parent) {
		
		gestureDetector = new GestureDetector(this.getLACActivity(), new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onFling(MotionEvent start, MotionEvent end, float velocityX, float velocityY) {
	            int distanceX = Math.abs((int) end.getX() - (int) start.getX());
	            int distanceY = Math.abs((int) end.getY() - (int) start.getY());

	            if (distanceX < 100 || distanceX < (2*distanceY)
	                    || Math.abs(velocityX) < 800) {
	                return false;
	            }
	            boolean flingToPrevious = velocityX > 0;
	            Log.d(Tag, "fling previous : " + flingToPrevious);
	            
	            onWebViewFling(flingToPrevious);

	            return true;
	        }			
		});
		
		webView = (WebView) viewSwitcher.findViewById(R.id.webView1);
		webView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
			
		});		
	}

	private void initData() {
		try {
			dictInfo = this.getLACActivity().getServiceAccess().getDictionarySimpleInfo();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	protected void onWebViewFling(boolean flingToPrevious) {
		if (flingToPrevious) {
			showWordList();
		}
	}

	protected void OnClick() {
		adapter.load("z");
//		try {
//			List<Dictionary.SimpleInfo> dictInfo = this.getLACActivity().getServiceAccess().getDictionarySimpleInfo();
//			
			for (final Dictionary.SimpleInfo info : dictInfo) {
				Log.d(Tag, "Dict = " + info.getIndex() + " - " + info.getTitle());
			}
//			
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		showWordResult();
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		adapter.refresh();
	}	

	@Override
	public void onLoadResultEnd(int count, int total) {
		pullList.onRefreshComplete();
	}

	@Override
	public void onIntent(Intent intent) {
		if (intent != null) {
			String condition = intent.getStringExtra("keyword");
			if (condition != null) {
				adapter.load(condition);
			}
		}
		super.onIntent(intent);
	}

	private void showWordList() {		
		viewSwitcher.showPrevious();
		setOptionsMenu(R.menu.dictionary);		
	}
	
	private void showWordResult() {
		viewSwitcher.showNext();
		setOptionsMenu(R.menu.lac);
	}

	public List<Dictionary.SimpleInfo> getDictionarySimpleInfo() {
		return dictInfo;		
	}
}





