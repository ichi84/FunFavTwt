package net.d_ichi84.Activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.d_ichi84.R;
import net.d_ichi84.task.FetchList;
import net.d_ichi84.task.timeLineAsyncTask;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import data.Constants;
import android.widget.ProgressBar;

//リストとリストアダプターを持つこのアプリの基本Activity。継承して使う
public class AdapterActivity<T> extends Activity implements OnClickListener{
	public ArrayAdapter<T> adapter =null;
	protected List<T> TweetArray 				//タイムライン取得用リスト生成
	  			= new ArrayList<T>();		
	protected ListView listView;				//タイムラインを保持しているリスト
	public static CommonsHttpOAuthConsumer oauthConsumer //OAuth認証コンシューマ
		= new CommonsHttpOAuthConsumer(	Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
	//protected List<T> TempListData;				//画面遷移時の保存用
    public static Context context = null;		//ダイアログ表示用。一番最後に表示されたActivityが入ってるはず。無理矢理感があるけど
	View	mFooter = null;						//リストのフッター
	String	BaseURL = "";						//タイムライン取得のベースURL
	String lastid = "0";
	
    @Override 
    protected void onCreate(Bundle savedInstanceState){
    	//setTheme(android.R.style.Theme_Light);
    	
    	super.onCreate(savedInstanceState);
        setOAuthFromPreference();
    }
	@Override 
	protected void onResume(){
		super.onResume();
		context = this;
		if(mFooter != null)
			mFooter.setOnClickListener(this);
		
	}

	public void getTimeline(boolean kurukuru){
	}
	
	public void getTimeline(int sleep, boolean kurukuru){
		try{
			Thread.sleep(sleep);
		}catch(Exception e){
			return;
		}
		getTimeline(kurukuru);
	}
	
	//画面回転対応。リストビューのリストを保存する
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (TweetArray != null) {
			outState.putSerializable("ListDataKey", (Serializable)TweetArray);
		}
	}
	
	
	public void setAdapters(ArrayAdapter<T> _adapter){			
		adapter = _adapter;
		listView.setAdapter(adapter);
	}
		
	//PreferenceからOAuthトークンを取得してセットする
	protected void setOAuthFromPreference(){
		 SharedPreferences pref = getSharedPreferences("funfuvtwt_token",MODE_PRIVATE);
	        String token = pref.getString("token","");
	        String tokenSecret = pref.getString("tokenSecret", "");
	        
	        if(token.length()>0 && tokenSecret.length()>0){
	        	oauthConsumer.setTokenWithSecret(token, tokenSecret);
	        }
	}
	
	//画面回転対応。バンドルからデータをロードする
	@SuppressWarnings("unchecked")
	protected void LoadBundle(Bundle savedInstanceState){
        //Bundleのチェック
        if (savedInstanceState != null) {
        	TweetArray = (List<T>)savedInstanceState.getSerializable("ListDataKey");
        }else{
        	TweetArray = new ArrayList<T>(); 
        }
	}
	
	//最初にタイムラインを表示する処理
	protected void LoadFiestTimeline(){
		Intent intent = getIntent();
		if(intent == null){
			return;
		}
		Uri uri = intent.getData();
		// インテントが取得できた = 別画面からとんできたらタイムライン更新
		if(uri == null){
			// タイムライン取得
			timeLineAsyncTask task = new timeLineAsyncTask(this, false
					, (ProgressBar)mFooter.findViewById(R.id.progressbar1)
					, (ImageView)mFooter.findViewById(R.id.imageMore));		
		    task.execute(BaseURL, oauthConsumer,false);
		    getIntent().setData( Uri.EMPTY );
		}
	}
	
	protected  String getLastStatusID(){
		return "0";		//スタブ
	}
	
	protected void LoadMoreTimeline(){
		String param;
		if(BaseURL.contains("?")){
			param = "&max_id=";
		}else
			param = "?max_id=";
		
    	//タイムライン取得
		if(adapter.getCount()==0){
			return;
		}
		String statusid = getLastStatusID();
		if(lastid.compareTo(statusid)!=0){
			lastid = statusid;
			String maxid = Long.toString( Long.valueOf(lastid) -1 );
			timeLineAsyncTask task = new timeLineAsyncTask(this, false
					, (ProgressBar)mFooter.findViewById(R.id.progressbar1)
					, (ImageView)mFooter.findViewById(R.id.imageMore));			
			
			task.execute(BaseURL + param + maxid, oauthConsumer, false);
			
		}
	}
	
	private void LoadMoreTimelineForce(){
		String param;
		if(BaseURL.contains("?")){
			param = "&max_id=";
		}else
			param = "?max_id=";
		
    	//タイムライン取得
		if(adapter.getCount()==0) return;
		
		String statusid = getLastStatusID();		
		lastid = statusid;
		String maxid = Long.toString( Long.valueOf(lastid) -1 );
		timeLineAsyncTask task = new timeLineAsyncTask(this, false
					, (ProgressBar)mFooter.findViewById(R.id.progressbar1)
					, (ImageView)mFooter.findViewById(R.id.imageMore));			
			
		task.execute(BaseURL + param + maxid, oauthConsumer, false);
	}
	
	//更新ボタンが押されたときの処理
	protected void refresh(){
		adapter.clear();
		lastid = "0";
		timeLineAsyncTask task = new timeLineAsyncTask(this, false
				, (ProgressBar)mFooter.findViewById(R.id.progressbar1)
				, (ImageView)mFooter.findViewById(R.id.imageMore));		
        task.execute(BaseURL, oauthConsumer, false);
	}
	
	//リストのフッターをリソースから取得
    protected View getFooter() {
        if (mFooter == null) {
            mFooter = getLayoutInflater().inflate(R.layout.listview_footer, null);
        }
        return mFooter;
    }
	@Override
	public void onClick(View v) {
		LoadMoreTimelineForce();
	}

	
}
