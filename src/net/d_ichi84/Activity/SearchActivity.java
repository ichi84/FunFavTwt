package net.d_ichi84.Activity;

import data.Single_Tweet;
import net.d_ichi84.R;
import net.d_ichi84.TweetListArrayAdapter;
import net.d_ichi84.task.timeLineAsyncTask;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SearchActivity extends AdapterActivity<Single_Tweet>
	implements OnClickListener, OnScrollListener{
	ImageButton refreshButotn;
	ImageButton SearchButton;
	TextView	SearchText;
	EditText	edit;
	final String BaseBaseURL = "https://api.twitter.com/1.1/search/tweets.json";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//Windowタイトルは要らない
        setContentView(R.layout.search); 
        LoadBundle(savedInstanceState);
        findViews();
        
        //listView.addFooterView(getFooter());   
        setAdapters(new TweetListArrayAdapter
        		(this, R.layout.single_tweet,TweetArray));
        listView.setOnScrollListener(this);
    }
	
	@Override
	protected void onResume(){
		super.onResume();
		//LoadFiestTimeline();
		Intent intent = getIntent();
		if(intent == null){return;}

		String search_text = intent.getStringExtra("search");
		if(search_text !=null){
			SearchText.setText(search_text);
			search();
			this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			
		}
		
	}

	private void findViews(){
		refreshButotn = (ImageButton)findViewById(R.id.refreshButtonInSearch);
		refreshButotn.setOnClickListener(this);
		
		SearchButton = (ImageButton)findViewById(R.id.SearchButton1);
		SearchButton.setOnClickListener(this);
		
		SearchText = (TextView)findViewById(R.id.SearchText1);
		listView = (ListView)findViewById(R.id.listViewinSearch);
		
		 edit = (EditText)findViewById(R.id.SearchText1);
		 edit.setOnEditorActionListener(new OnEditorActionListener() {  
		    @Override  
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {  
		        if (actionId == EditorInfo.IME_ACTION_SEARCH || event.getAction() == KeyEvent.ACTION_UP) {  
		            search(); // search処理  
		            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		        	imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
		        	imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		        }  
		        return true; // falseを返すと, IMEがSearch→Doneへと切り替わる  
		    }  
		 });

	}
	@Override
	protected  String getLastStatusID(){
		return adapter.getItem(adapter.getCount()-1).id;
	}
	
	
	@Override
	 public boolean dispatchKeyEvent(KeyEvent event) {
	  if(event.getAction() == KeyEvent.ACTION_DOWN){
	   switch(event.getKeyCode()){
	   case KeyEvent.KEYCODE_BACK:
		   Intent intent = new Intent(SearchActivity.this, MyTweetActivity.class);
		   startActivity(intent);
		   finish();
		   return true;
	   }
	  }
	  // 自動生成されたメソッド・スタブ
	  return super.dispatchKeyEvent(event);
	}
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch(v.getId()){
		case R.id.refreshButtonInrep:
			refresh();
			break;
			
		case R.id.SearchButton1:
			search();
			break;
		}
	}
	
	void search(){
		adapter.clear();
		listView.addFooterView(getFooter());
		setAdapters(new TweetListArrayAdapter
        		(this, R.layout.single_tweet,TweetArray));
		String text = SearchText.getText().toString();
		BaseURL = BaseBaseURL + "?q=" + text;
		
		timeLineAsyncTask task = new timeLineAsyncTask(this, true, null,null);
		task.execute(BaseURL, oauthConsumer, true);
	}
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (totalItemCount == firstVisibleItem + visibleItemCount) {
            //ここに最後までスクロールしたときの処理を書く
        	LoadMoreTimeline();
        }
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
	}
}
