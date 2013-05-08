package jie.android.lac.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import jie.android.lac.R;
import jie.android.lac.data.Dictionary;
import jie.android.lac.data.Word;
import jie.android.lac.data.Word.Info;
import jie.android.lac.fragment.data.DictionaryFragmentListAdapter;
import jie.android.lac.fragment.data.XmlTranslator;
import jie.android.lac.fragment.data.DictionaryFragmentListAdapter.OnRefreshResultListener;
import jie.android.lac.fragment.sliding.DictionaryListSlidingFragment;
import android.content.Context;
import android.content.Intent;
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
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//this.setSlidingMenu(new ColorFragment(this, R.color.red), new ColorFragment(this, R.color.white));
		setSlidingMenu(new DictionaryListSlidingFragment(this), null);
		setOptionsMenu(R.menu.dictionary_word_list);
		
		initData();
	}	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_dictionary_switcher, container, false);

		viewSwitcher = (ViewSwitcher) v.findViewById(R.id.viewSwitcher);
		
		initAnimation();		
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
		pullList.setAdapter(adapter);
	}

	private void initWebView(View parent) {
		
		webTextView = (TextView) parent.findViewById(R.id.textView1);
		
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
		adapter.load("z");
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		showWordResult(position, id);
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
		viewSwitcher.clearAnimation();
		viewSwitcher.setInAnimation(aniWord);
		viewSwitcher.setOutAnimation(aniResultOut);
		
		viewSwitcher.showPrevious();
		setOptionsMenu(R.menu.dictionary_word_list);
		
		enableSlidingFragment(true, true);
		
		viewState = ViewState.WORD_LIST;
	}
	
	private void showWordResult(int position, long id) {
		
//		InputMethodManager imm = (InputMethodManager)getLACActivity().getSystemService(
//			      Context.INPUT_METHOD_SERVICE);
//		imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
		
		searchView.setIconified(true);
		
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
		
		initSearchView(menu);
	}
	
	private void initSearchView(Menu menu) {
		if (searchView == null && viewState == ViewState.WORD_LIST) {
			searchView = (SearchView) menu.findItem(R.id.item1).getActionView();
			searchView.setIconified(false);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (viewState == ViewState.WORD_RESULT) {
			
			showWordList();
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	
}





