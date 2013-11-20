package net.d_ichi84.Activity;


import net.d_ichi84.userImage;
import net.d_ichi84.R;
import net.d_ichi84.TweetListArrayAdapter;
import net.d_ichi84.dialog.postDialog;
import net.d_ichi84.task.FetchUserIcon;
import net.d_ichi84.task.FetchUserInfo;
import net.d_ichi84.task.timeLineAsyncTask;

import data.Single_Tweet;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class IdPageActivity extends AdapterActivity<Single_Tweet> 
		implements OnClickListener, userImage, OnScrollListener{

	ImageButton refreshButotn;
	ImageButton tweetButotn;
	public ImageView userImage;
	public TextView userText;
	public TextView descriptionText;
	public TextView followText;
	public TextView followerText;
	public TextView followStatusText;
	String user_id;

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//Windowタイトルは要らない
        setContentView(R.layout.id_page); 
       	
        LoadBundle(savedInstanceState);
        
        findViews();
        listView.addFooterView(getFooter());        
        setAdapters(new TweetListArrayAdapter
        		(this, R.layout.single_tweet,TweetArray));
        listView.setOnScrollListener(this);;

    }


	@Override
	protected void onResume(){
		super.onResume();
		Intent intent = getIntent();
		if(intent == null){return;}

		user_id = intent.getStringExtra("to_user");
		BaseURL = "https://api.twitter.com/1.1/statuses/user_timeline/" + user_id + ".json?include_rts=1";
		
		Uri uri = intent.getData();
		if(uri == null){
			// タイムライン取得			
			//timeLineAsyncTask task = new timeLineAsyncTask(this, false);		
		    //task.execute(BaseURL , oauthConsumer);
			LoadFiestTimeline();
		    userText.setText(user_id);
		    
	        FetchUserIcon taskIcon = new FetchUserIcon(this);
	        taskIcon.execute("https://api.twitter.com/1.1/users/profile_image?screen_name=" +user_id +"&size=bigger", oauthConsumer);
	        
	        //ユーザー情報取得
	        FetchUserInfo user= new FetchUserInfo(this);
	        user.execute(user_id, oauthConsumer);

		    getIntent().setData( Uri.EMPTY );
		}
	}
	
	
	private void findViews(){
		
		refreshButotn = (ImageButton)findViewById(R.id.refreshbutton_inid);
		refreshButotn.setOnClickListener(this);
		tweetButotn = (ImageButton)findViewById(R.id.tweetButton_id);
		tweetButotn.setOnClickListener(this);
		
		userImage = (ImageView)findViewById(R.id.userImageView_inid);
		userText =  (TextView)findViewById(R.id.userTextView_inid);
		descriptionText =  (TextView)findViewById(R.id.descriptionTV);
		followText =  (TextView)findViewById(R.id.followTV);
		followerText =  (TextView)findViewById(R.id.followerTV);
		followStatusText = (TextView)findViewById(R.id.followStatus);
		
		listView = (ListView)findViewById(R.id.listView_inid);
	}
	
	
	@Override
	protected  String getLastStatusID(){
		return adapter.getItem(adapter.getCount()-1).id;
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		switch(v.getId()){
		case R.id.backbutton_inid://back
			Intent intent = new Intent(IdPageActivity.this, MyTweetActivity.class);
	    	startActivity(intent);
	    	finish();
			break;
			
		case R.id.tweetButton_id:
			Dialog dialog = new postDialog(null, null, null,false,null);
			dialog.show();
			break;
			
		case R.id.refreshbutton_inid:
			//ユーザー情報取得
	        FetchUserInfo user= new FetchUserInfo(this);
	        user.execute(user_id, oauthConsumer);
	        refresh();
			break;
		}
		
	}
	
	@Override
	public ImageView getuserImage() {
		return userImage;
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
		// TODO Auto-generated method stub
		
	}
}
