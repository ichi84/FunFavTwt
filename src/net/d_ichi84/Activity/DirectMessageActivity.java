package net.d_ichi84.Activity;


import data.Single_Tweet;
import net.d_ichi84.R;
import net.d_ichi84.TweetListArrayAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageButton;
import android.widget.ListView;

public class DirectMessageActivity extends AdapterActivity<Single_Tweet> 
	implements OnClickListener, OnScrollListener{
	ImageButton refreshButotn;

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//Window�^�C�g���͗v��Ȃ�
        setContentView(R.layout.reply); 
        LoadBundle(savedInstanceState);  
        findViews();
        listView.addFooterView(getFooter());     
        setAdapters(new TweetListArrayAdapter
        		(this, R.layout.single_tweet,TweetArray));
        listView.setOnScrollListener(this);
        BaseURL = "https://api.twitter.com/1.1/direct_messages.json";
    }

	
	@Override
	protected void onResume(){
		super.onResume();
		LoadFiestTimeline();
	}
	
	
	private void findViews(){
		refreshButotn = (ImageButton)findViewById(R.id.refreshButtonInrep);
		refreshButotn.setOnClickListener(this);
		
		listView = (ListView)findViewById(R.id.listViewinRep);
	}
	
	
	@Override
	protected  String getLastStatusID(){
		return adapter.getItem(adapter.getCount()-1).id;
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.refreshButtonInrep:
			refresh();
			break;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (totalItemCount == firstVisibleItem + visibleItemCount) {
            //�����ɍŌ�܂ŃX�N���[�������Ƃ��̏���������
        	LoadMoreTimeline();
        }
	}
	
	

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
	}
}
