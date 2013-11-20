package net.d_ichi84.Activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.d_ichi84.ListListArrayAdapter;
import net.d_ichi84.R;
import net.d_ichi84.dialog.postDialog;
import net.d_ichi84.task.FetchList;
import net.d_ichi84.task.timeLineAsyncTask;
import data.Single_List;
import data.Constants;
import data.Single_List;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

public class ListActivity extends AdapterActivity<Single_List> 
	implements OnClickListener, OnItemClickListener{
	ImageButton backButton;
	ImageButton refreshButotn;
	ImageButton tweetButton;
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//Windowタイトルは要らない
        setContentView(R.layout.reply); 
        
        //Bundleのチェック
        if (savedInstanceState != null) {
        	TweetArray = (List<Single_List>)savedInstanceState.getSerializable("ListDataKey");
        }else{
        	TweetArray = new ArrayList<Single_List>(); 
        }
        
        findViews();
        setAdapters(new ListListArrayAdapter
        		(this, R.layout.single_list,TweetArray));
        
        listView.setOnItemClickListener(this);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		Intent intent = getIntent();
		
		if(intent == null){
			return;
		}
		Uri uri = intent.getData();
		
		// インテントが取得できた = 別画面からとんできたらタイムライン更新
		if(uri == null){
			adapter.clear();
			FetchList task = new FetchList(this);		
			task.execute(oauthConsumer, MyTweetActivity.MyName);
			getIntent().setData( Uri.EMPTY );
		}
		
		tweetButton = (ImageButton)findViewById(R.id.tweetButton_id_rep);
		tweetButton.setVisibility(View.GONE);
		
	}
	private void findViews(){
		backButton = (ImageButton)findViewById(R.id.backButtoninrep);
		backButton.setOnClickListener(this);
		
		refreshButotn = (ImageButton)findViewById(R.id.refreshButtonInrep);
		refreshButotn.setOnClickListener(this);
		
		tweetButton = (ImageButton)findViewById(R.id.tweetButton_id_rep);
		tweetButton.setOnClickListener(this);
			
		listView = (ListView)findViewById(R.id.listViewinRep);
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		//画面回転対応。リストビューのリストを保存する
		super.onSaveInstanceState(outState);
		if (TweetArray != null) {
			outState.putSerializable("ListDataKey", (Serializable)TweetArray);
		}
	}

	@Override
	public void onClick(View v) {
//		super.onClick(v);
		switch(v.getId()){
		case R.id.backButtoninrep://ツイートボタンが押されたときの動作
			Intent intent = new Intent(ListActivity.this, MyTweetActivity.class);
	    	startActivity(intent);
	    	finish();
			break;
			
		case R.id.refreshButtonInrep:
			adapter.clear();
			FetchList task = new FetchList(this);		
		    task.execute(oauthConsumer, MyTweetActivity.MyName,false);
		    break;
		}	
		
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		Single_List list = (Single_List)adapter.getItemAtPosition(position);
		
		Intent intent = new Intent(ListActivity.this, SingleListActivity.class);
		intent.putExtra("list_id", list.id_str);
		startActivity(intent);
	}

}
