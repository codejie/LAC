package jie.android.lac.fragment;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import jie.android.lac.R;
import jie.android.lac.fragment.data.DictionaryFragmentListAdapter;
import jie.android.lac.fragment.data.DictionaryFragmentListAdapter.OnRefreshResultListener;
import android.content.Intent;
import android.os.Bundle;
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

public class DictionaryFragment extends ContentFragment implements OnRefreshResultListener, OnRefreshListener<ListView>, OnItemClickListener  {
	
	private static final String Tag = DictionaryFragment.class.getSimpleName();

	private ViewSwitcher viewSwitcher = null;
	
	private PullToRefreshListView pullList = null;	
	private DictionaryFragmentListAdapter adapter = null;
	
	private WebView webView = null;
	private GestureDetector gestureDetector = null;
	
	private int i = 0;
	
	public DictionaryFragment() {
		//super(R.layout.fragment_dictionary_list);
		super(R.layout.fragment_dictionary_switcher);
		
		this.setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);

		viewSwitcher = (ViewSwitcher) v.findViewById(R.id.viewSwitcher);
		
		adapter = new DictionaryFragmentListAdapter(this.getLACActivity(), this.getLACActivity().getServiceAccess(), this);
		
		pullList = (PullToRefreshListView) viewSwitcher.findViewById(R.id.pull_refresh_list);
		pullList.setMode(Mode.PULL_FROM_END);
		pullList.setOnRefreshListener(this);
		pullList.setOnItemClickListener(this);
	
		pullList.setAdapter(adapter);
		
		gestureDetector = new GestureDetector(this.getLACActivity(), new OnGestureListener() {

			@Override
			public boolean onDown(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}

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

			@Override
			public void onLongPress(MotionEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}
			
		});
		
		webView = (WebView) viewSwitcher.findViewById(R.id.webView1);
		webView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				return onWebViewTouch(event);
			}
			
		});
		webView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				Log.d(Tag, "setOnKeyListener : " + arg1);
				return false;
			}
			
		});
		
		Button btn = (Button) viewSwitcher.findViewById(R.id.button1);
		btn.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View arg0) {
				OnClick();
			}
			
		});		
		
		return v;
	}

	protected void onWebViewFling(boolean flingToPrevious) {
		if (flingToPrevious) {
			viewSwitcher.showPrevious();
			i = 0;
		}
	}

	protected boolean onWebViewTouch(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

	protected void OnClick() {
		adapter.load("z");
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		viewSwitcher.showNext();
		i = 1;
		this.getLACActivity().supportInvalidateOptionsMenu();
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		adapter.refresh();
		this.getLACActivity().supportInvalidateOptionsMenu();
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

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onPrepareOptionsMenu(menu);
		if (i == 1) {
			menu.clear(); 
		} else {
			this.getLACActivity().getSupportMenuInflater().inflate(R.menu.dictionary, menu);
		}
	}
	
}





