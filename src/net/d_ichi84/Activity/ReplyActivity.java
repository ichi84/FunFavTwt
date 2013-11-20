package net.d_ichi84.Activity;

import data.Constants;
import data.Single_Tweet;
import net.d_ichi84.R;
import net.d_ichi84.TweetListArrayAdapter;
import net.d_ichi84.dialog.postDialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

public class ReplyActivity extends AdapterActivity<Single_Tweet>
	implements OnClickListener, OnScrollListener{
	ImageButton refreshButotn;
	ImageButton tweetButotn;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//Windowタイトルは要らない
        setContentView(R.layout.reply); 
        
        LoadBundle(savedInstanceState);
        findViews();
        
        listView.addFooterView(getFooter());   
        setAdapters(new TweetListArrayAdapter(
        		this, R.layout.single_tweet,TweetArray, true)
        );
        listView.setOnScrollListener(this);
        BaseURL = Constants.MENTION_REQUEST_URL;
    }
	
	@Override
	protected void onResume(){
		super.onResume();
		LoadFiestTimeline();
	}

	private void findViews(){
		refreshButotn = (ImageButton)findViewById(R.id.refreshButtonInrep);
		refreshButotn.setOnClickListener(this);
		
		tweetButotn = (ImageButton)findViewById(R.id.tweetButton_id_rep);
		tweetButotn.setOnClickListener(this);
		
		listView = (ListView)findViewById(R.id.listViewinRep);
	}
	@Override
	protected  String getLastStatusID(){
		return adapter.getItem(adapter.getCount()-1).id;
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch(v.getId()){
		case R.id.refreshButtonInrep:
			refresh();
			break;
			
		case R.id.tweetButton_id_rep:
			Dialog dialog = new postDialog(null, null, null,false,null);
			dialog.show();
			break;
		}
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
