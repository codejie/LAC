package jie.android.lac.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import jie.android.lac.R;
import jie.android.lac.data.Dictionary;
import jie.android.lac.data.Word;
import jie.android.lac.fragment.data.DictionaryFragmentListAdapter;
import jie.android.lac.fragment.data.XmlTranslator;
import jie.android.lac.fragment.data.DictionaryFragmentListAdapter.OnRefreshResultListener;
import jie.android.lac.fragment.sliding.DictionaryListSlidingFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.TranslateAnimation;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class DictionaryFragment extends BaseFragment implements OnRefreshResultListener, OnRefreshListener<ListView>, OnItemClickListener  {

	private class LoadWordXmlResultTask extends AsyncTask<Word.Info, Void, String> {

		private Word.Info info = null;
		@Override
		protected String doInBackground(Word.Info... params) {
			info = params[0];
			
			Word.XmlResult result = null;
			try {
				result = getLACActivity().getServiceAccess().queryWordXmlResult(info.getIndex());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			return  XmlResultToHtml(info, result);
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				webTextView.setText(info.getText());
				webView.loadDataWithBaseURL(null, result, "text/html", "utf-8", null);			
			}			
			super.onPostExecute(result);
		}
	}	
	
	private enum ViewState {
		WORD_LIST, WORD_RESULT;
	}
	
	private static final String Tag = DictionaryFragment.class.getSimpleName();

	private ViewSwitcher viewSwitcher = null;
	private ViewState viewState = ViewState.WORD_LIST;
	
	private PullToRefreshListView pullList = null;	
	private DictionaryFragmentListAdapter adapter = null;
	
	private TextView webTextView = null;
	private WebView webView = null;
	private GestureDetector gestureDetector = null;
	
	private SearchView searchView = null;
	
	private HashMap<Integer, Dictionary.SimpleInfo> dictInfo = null;
	
	private TranslateAnimation aniResultIn = null;
	private TranslateAnimation aniWord = null;
	private TranslateAnimation aniResultOut = null;
	
//	private LinearLayout emptyLayout = null;
//	private TextView emptyTextView = null;

	private LinearLayout footLayout = null;
	private TextView footTextView = null;	
	
	private boolean isQueryCheckThreadRun = false;
	private final Object queryLock = new Object();

	private Thread inputCheckThread = new Thread(new Runnable() {

		@Override
		public void run() {
			while (true) {
				synchronized(queryLock) {
					try {
						queryLock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				if (!isQueryCheckThreadRun()) {
					break;
				}
				
				final String query = searchView.getQuery().toString();
				if (query == null || query.isEmpty()) {
					continue;
				}
				adapter.load(query);
//				loadWordList(query);
			}
		}		
	});
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//this.setSlidingMenu(new ColorFragment(this, R.color.red), new ColorFragment(this, R.color.white));
		setSlidingMenu(new DictionaryListSlidingFragment(this), null);
		setOptionsMenu(R.menu.dictionary_word_list);
		
		initData();
		
		startQueryCheckThread();
	}	


	@Override
	public void onDestroy() {
		releaseQueryCheckThread();
		super.onDestroy();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_dictionary_switcher, container, false);

		viewSwitcher = (ViewSwitcher) v.findViewById(R.id.viewSwitcher);
		
		initAnimation();		
		initListView(viewSwitcher);
		initWebView(viewSwitcher);

		return v;
	}

	private void initAnimation() {
		
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getLACActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        
//        int SCREEN_HEIGHT = displaymetrics.heightPixels;
        int SCREEN_WIDTH = displaymetrics.widthPixels;        
        
//        if(SCREEN_HEIGHT > SCREEN_WIDTH) {		
			aniResultIn = new TranslateAnimation(SCREEN_WIDTH, 0, 0, 0);
			aniResultIn.setDuration(300);
			aniWord = new TranslateAnimation(0, 0, 0, 0);
			aniWord.setDuration(300);
			aniResultOut = new TranslateAnimation(0, SCREEN_WIDTH, 0, 0);
			aniResultOut.setDuration(300);
//        }
//        else {
//        	aniResultIn = new TranslateAnimation(0, 0, SCREEN_HEIGHT, 0);
//			aniResultIn.setDuration(400);
//			aniWord = new TranslateAnimation(0, 0, 0, 0);
//			aniWord.setDuration(700);
//			aniResultOut = new TranslateAnimation(0, 0, 0, SCREEN_HEIGHT);
//			aniResultOut.setDuration(400);
//        }        
        
	}
	private void initListView(View parent) {
		pullList = (PullToRefreshListView) parent.findViewById(R.id.pull_refresh_list);
		pullList.setMode(Mode.PULL_FROM_END);
			
		//listener
		pullList.setOnRefreshListener(this);
		pullList.setOnItemClickListener(this);
		
		//adapter
		adapter = new DictionaryFragmentListAdapter(getLACActivity(), getLACActivity().getServiceAccess(), this);
		adapter.setMaxPrePage(getLACActivity().getConfig().getWordPrePage());
		pullList.setAdapter(adapter);
		
//		View v = getLACActivity().getLayoutInflater().inflate(R.layout.fragment_dictionary_list_foot, null);
//		emptyLayout = (LinearLayout) parent.findViewById(R.id.footLayout);
//		emptyTextView = (TextView) parent.findViewById(R.id.textView1);
//		pullList.setEmptyView(emptyLayout);
//		emptyTextView.setText("EMPTY");
		
		View v1 = getLACActivity().getLayoutInflater().inflate(R.layout.fragment_dictionary_list_foot, null);
		footLayout = (LinearLayout) v1.findViewById(R.id.footLayout);
		footTextView = (TextView) v1.findViewById(R.id.textFoot);
		pullList.getRefreshableView().addFooterView(v1);	
		pullList.getRefreshableView().setFooterDividersEnabled(false);
	}

	private void initWebView(View parent) {
		
		webTextView = (TextView) parent.findViewById(R.id.textWord);
		
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
			List<Dictionary.SimpleInfo> info = this.getLACActivity().getServiceAccess().queryDictionarySimpleInfo();
			dictInfo = new HashMap<Integer, Dictionary.SimpleInfo>();
			for (Dictionary.SimpleInfo i : info) {
				dictInfo.put(i.getIndex(), i);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		try {
			XmlTranslator.setPattern(getLACActivity().getAssets().open("ld2.xsl"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void onWebViewFling(boolean flingToPrevious) {
		if (flingToPrevious) {
			showWordList();
		}
	}

	protected void OnClick() {

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (id != -1) {
			showWordResult(position, id);
		}
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		adapter.refresh();
	}	

	@Override
	public void onLoadResultEnd(int count, int total, int maxPerPage) {
		pullList.onRefreshComplete();
		if (total == 0) {
			pullList.setMode(Mode.DISABLED);
			showListFootTip(true, R.string.lac_list_empty);
		} else if (count == 0 || total < maxPerPage) {
			pullList.setMode(Mode.DISABLED);
			showListFootTip(true, R.string.lac_list_lastword);
		} else {
			showListFootTip(false, -1);
		}
	}

	private void showWordList() {
		viewSwitcher.clearAnimation();
		viewSwitcher.setInAnimation(aniWord);
		viewSwitcher.setOutAnimation(aniResultOut);
		
		viewSwitcher.showPrevious();
		setOptionsMenu(R.menu.dictionary_word_list);
		
		enableSlidingFragment(true, true);
		
		viewState = ViewState.WORD_LIST;
	}
	
	private void showWordResult(int position, long id) {

		searchView.setIconified(true);
		
//		Word.Info info = (Word.Info) adapter.getItem(position - 1);
//		webTextView.setText(info.getText());

		LoadWordXmlResultTask task = new LoadWordXmlResultTask();
		task.execute((Word.Info) adapter.getItem(position - 1));

		viewSwitcher.clearAnimation();
		viewSwitcher.setInAnimation(aniResultIn);
		viewSwitcher.setOutAnimation(aniWord);
		
		viewSwitcher.showNext();
		setOptionsMenu(R.menu.dictionary_word_result);
		
		enableSlidingFragment(false, false);
		
		viewState = ViewState.WORD_RESULT;
	}

	private String XmlResultToHtml(final Word.Info info, final Word.XmlResult result) {
		if (result == null) {
			return null;
		}
		
		final String xml = assembleXmlResult(info, result);
		
		return XmlTranslator.trans(xml);
	}

	private final String assembleXmlResult(final Word.Info info, final Word.XmlResult result) {
		
		String ret = "<LAC><LAC-W>" + info.getText() + "</LAC-W>";
		for (final Word.XmlResult.XmlData data : result.getXmlData()) {
			ret += "<LAC-R><LAC-D>" + dictInfo.get(data.getDictIndex()).getTitle() + "</LAC-D>";
			for(final String xml : data.getXml()) {
				ret += xml;
			}
			ret += "</LAC-R>";
		}
		ret += "</LAC>";
		
		return ret;
	}	
	
	public List<Dictionary.SimpleInfo> getDictionarySimpleInfo() {
		List<Dictionary.SimpleInfo> ret = new ArrayList<Dictionary.SimpleInfo>();
		for (Dictionary.SimpleInfo info : dictInfo.values()) {
			ret.add(info);
		}
		return ret;		
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		
		if (viewState == ViewState.WORD_LIST) {
			initListMenu(menu);
		} else {
			initResultMenu(menu);
		}
	}
	
	private void initListMenu(Menu menu) {
		searchView = (SearchView) menu.findItem(R.id.list_search).getActionView();
		searchView.setQueryHint("Keyword");
		searchView.setIconifiedByDefault(true);
		searchView.setIconified(false);
		
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				synchronized(queryLock) {
					queryLock.notify();
				}
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				if (pullList.getMode() == Mode.DISABLED) {
					pullList.setMode(Mode.PULL_FROM_END);
					showListFootTip(false, -1);
				}

				synchronized(queryLock) {
					queryLock.notify();
				}
				return true;
			}
			
		});
	}
	
	private synchronized boolean isQueryCheckThreadRun() {
		return isQueryCheckThreadRun;
	}
	
	private synchronized void setQueryCheckThreadRun(boolean value) {
		isQueryCheckThreadRun = value;
	}
	
	private void startQueryCheckThread() {
		setQueryCheckThreadRun(true);
		inputCheckThread.start();
	}

	private void releaseQueryCheckThread() {
		try {
			setQueryCheckThreadRun(false);
			synchronized(queryLock) {
				queryLock.notify();
			}
			inputCheckThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initResultMenu(Menu menu) {
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (viewState == ViewState.WORD_RESULT) {				
				showWordList();				
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch(item.getItemId()) {
		case R.id.list_search:
			onListSearchClick();
			break;
		case R.id.result_search:
			onResultSearchClick();
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}

	private void onListSearchClick() {
		// TODO Auto-generated method stub
		
	}

	private void onResultSearchClick() {
		showWordList();		
	}
	
	private void showListFootTip(boolean show, int resId) {
		if (show) {
			footLayout.setVisibility(View.VISIBLE);
			if (resId != -1) {
				footTextView.setText(resId);
			}
		} else {
			footLayout.setVisibility(View.GONE);
		}
	}


	@Override
	public void onSlidingMenuOpen() {
		if (!searchView.isIconified()) {
			searchView.setIconified(true);
		}
		super.onSlidingMenuOpen();
	}
}





