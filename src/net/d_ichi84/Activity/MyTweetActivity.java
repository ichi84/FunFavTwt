package net.d_ichi84.Activity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import data.Constants;
import data.Single_Tweet;

import net.d_ichi84.MySSLSocketFactory;
import net.d_ichi84.R;
import net.d_ichi84.ToastMaster;
import net.d_ichi84.TweetListArrayAdapter;
import net.d_ichi84.dialog.postDialog;
import net.d_ichi84.task.FetchMyName;
import net.d_ichi84.task.timeLineAsyncTask;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.*;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

//メイン画面
public class MyTweetActivity extends AdapterActivity<Single_Tweet> 
implements OnClickListener {
	
	public static ListView mylistView;
	ImageButton	TempButton;		//再生/ポーズボタン
	ImageButton	TweetButton;	//ツイートボタン
	ImageButton	AtButton;		//＠ボタン
	ImageButton SettingButton;	//設定ボタン
	ImageButton DMButton;		//DMボタン
	ImageButton Refresh;		//更新ボタン
	
	static Thread thread = null;					//ストリーム処理のスレッド
	Handler handler		 = new Handler();			//ストリーム処理のスレッドからUIを操作する用
	boolean mKeepRunning = false; 					//ストリーム処理のスレッドが動いているかどうか(UI用)
	static  ArrayAdapter<Single_Tweet> innerAdapter //内部用のスティックなアダプタ。スレッドが乱立しないために重要
						 = null; 
	static MyRunnable looper ;						//スレッド。これもスタティック
	
	boolean isActive = false;	
    PowerManager.WakeLock wl;
    public static StringBuffer MyName = new StringBuffer();
    
    int retry;
    
    /** Called when the activity is first created. */
	@SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//Windowタイトルは要らない
        setContentView(R.layout.main);
        
        //Bundleのチェック。画面回転後の処理
        if (savedInstanceState != null) {
        	TweetArray = (List<Single_Tweet>)savedInstanceState.getSerializable("ListDataKey");
        }else{
        	//TempListData = new ArrayList<Single_Tweet>(); 
            try {
                FileInputStream fis = openFileInput("SaveData.dat");
                ObjectInputStream ois = new ObjectInputStream(fis);
                TweetArray = (List<Single_Tweet>) ois.readObject();
                ois.close();
            } catch (Exception e) {
            	Log.e("Twitter", "onCreate" + e.toString());
            }
        }
            
        findViews();
        setListeners();
        if(innerAdapter == null){
       		if(TweetArray == null)
       			TweetArray =  new ArrayList<Single_Tweet>(); 
       		innerAdapter = new TweetListArrayAdapter(this, R.layout.single_tweet,TweetArray);
        }
        setAdapters(innerAdapter);
        listView.setAdapter(innerAdapter);
        mylistView = listView;
        
        try{
        	SharedPreferences pref = getSharedPreferences("funfuvtwt_setting",MODE_PRIVATE);
        	int tweet_num = pref.getInt("tweet_num", 50);
			if(innerAdapter.getCount()>tweet_num){ //保持しておくリストの行数
				while(innerAdapter.getCount()>tweet_num)
					innerAdapter.remove( innerAdapter.getItem(innerAdapter.getCount()-1)) ;		
			}
		}catch(Exception e){}
        
    	//startStream();
    }

    
	@Override
	protected void onResume(){
		super.onResume();
		
		//認証してない場合、認証画面を開く
		if(oauthConsumer == null){
			oauthConsumer =
	        		new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
		}
		if( oauthConsumer.getToken() == null ||
			oauthConsumer.getTokenSecret() == null){
			Intent intent = new Intent(MyTweetActivity.this, SettingActivity.class);
			startActivity(intent);
			setOAuthFromPreference();
		}
		
	  
	
		//自分の名前を取得
		if(MyName!=null){
			if(MyName.toString()==""){
				FetchMyName name = new FetchMyName(MyName);
				name.execute(oauthConsumer);
			}
		}
		
		isActive = true;
		
		if(mKeepRunning == true){
			Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_media_pause);
			TempButton.setImageBitmap(bmp);
			Refresh.setVisibility(View.GONE);
		}else{
			Refresh.setVisibility(View.VISIBLE);
		}
		
		
		if(thread != null){
			if((!looper.disable) && thread.isAlive()){
				startStream();
			}
		}
		
		retry = 0;
	}
	
	@Override
	protected void onPause(){
		try {
		    FileOutputStream fos = openFileOutput("SaveData.dat", MODE_WORLD_WRITEABLE );
		    ObjectOutputStream oos = new ObjectOutputStream(fos);
		    oos.writeObject((Serializable)TweetArray);
		    oos.close();
		    //stopStream();
		} catch (Exception e) {
			Log.e("Twitter", "onDestroy" + e.toString());
		}
	    

		if(wl != null){
			try{
				wl.release();
			}catch(Exception e){}
			wl = null;
		}
		isActive = false;
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
	//メニュー設定
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.mainmenu, menu);
	    return true;
	}
    

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    super.onOptionsItemSelected(item);
	    Intent intent ;
	    Bitmap bmp;
	    switch(item.getItemId()){
	    case R.id.SettingMenu :
	    	intent = new Intent(MyTweetActivity.this, Setting2Activity.class);
			startActivity(intent);
	        return true;
	    case R.id.SearchMenu :
	    	intent = new Intent(MyTweetActivity.this, SearchActivity.class);
			startActivity(intent);
	        return true;
    		
	    }
    
	    return false;
	}
	
	
	//Viewのセット
	protected void findViews(){
		listView		= (ListView)findViewById(R.id.listView1);
		TempButton		= (ImageButton)findViewById(R.id.button1);
		TweetButton		= (ImageButton)findViewById(R.id.tweetButton1);	
		AtButton		= (ImageButton)findViewById(R.id.AtButton1);		
		SettingButton	= (ImageButton)findViewById(R.id.button2);
		DMButton		= (ImageButton)findViewById(R.id.DMButton);
		Refresh			= (ImageButton)findViewById(R.id.imageButton_refreshMain);
	}
	
	//クリックリスナーのセット
	protected void setListeners(){
		TempButton.setOnClickListener(this);
		TweetButton.setOnClickListener(this);
		AtButton.setOnClickListener(this);
		SettingButton.setOnClickListener(this);
		DMButton.setOnClickListener(this);
		Refresh.setOnClickListener(this);
		
	}
	

	@Override
	public void onClick(View v){
		Intent intent;
		switch(v.getId()){
		case R.id.button1://再生/停止ボタンが押されたときの動作
			retry=0;
			if(mKeepRunning){
				stopStream();
			}else{			
				startStream();
			}
			break;
		case R.id.tweetButton1://ツイートボタンがおされたときの動作
			Dialog dialog;
			if(mKeepRunning){
				dialog = new postDialog(null, null, null, false, null);
			}else{
				dialog = new postDialog(null, null, null, false, this);
			}
			dialog.show();
			break;
		case R.id.AtButton1://＠ボタンがおされたときの動作
			intent = new Intent(MyTweetActivity.this, ReplyActivity.class);
			intent.putExtra("to_user", "self:");
			startActivity(intent);
			break;
		case R.id.button2://Settingボタンがおされたときの動作
			openOAuthActivity();
			break;
			
		case R.id.DMButton://Settingボタンがおされたときの動作
			intent = new Intent(MyTweetActivity.this, DirectMessageActivity.class);
			startActivity(intent);
			break;
			
		case R.id.imageButton_refreshMain://更新ボタンがおされたときの動作
			getTimeline(true);
			
		}
		
	}
	
	@Override
	 public boolean dispatchKeyEvent(KeyEvent event) {
	  if(event.getAction() == KeyEvent.ACTION_DOWN){
		  Intent intent;
	   switch(event.getKeyCode()){
	   case KeyEvent.KEYCODE_BACK:
		   
		   //とりあえずホームに飛ばしてお茶を濁す
		   intent = new Intent(Intent.ACTION_MAIN);
		   intent.addCategory(Intent.CATEGORY_HOME);
		   startActivity(intent);
		   
		   //一応止める
		   stopStream();
		   finish();
		   return true;
		   
	   case KeyEvent.KEYCODE_SEARCH:
		   intent = new Intent(MyTweetActivity.this, SearchActivity.class);
		   startActivity(intent);
		   return true;
	   }
	  }
	  // 自動生成されたメソッド・スタブ
	  return super.dispatchKeyEvent(event);
	}

	
	//認証用画面を開く
	protected void openOAuthActivity(){
		Intent intent = new Intent(MyTweetActivity.this, ListActivity.class);
		startActivity(intent);
	}
	
	

	
	// タイムライン取得
	public void getTimeline(boolean kurukuru){
		SharedPreferences pref = getSharedPreferences("funfuvtwt_setting",MODE_PRIVATE);
    	int tweet_num = pref.getInt("tweet_num", 50);
    	
		timeLineAsyncTask task = new timeLineAsyncTask(this, kurukuru, null,null);		
		task.execute("https://api.twitter.com/1.1/statuses/home_timeline.json?count="+ Integer.toString(tweet_num),
						oauthConsumer,false, true);
		try{
			if(innerAdapter.getCount()>tweet_num){ //保持しておくリストの行数
				while(innerAdapter.getCount()>tweet_num)
					innerAdapter.remove( innerAdapter.getItem(innerAdapter.getCount()-1)) ;		
			}
		}catch(Exception e){}
	}
	
	void startStream(int sleep){
		try{
			Thread.sleep(sleep);
		}catch(Exception e){
			return;
		}
		startStream();
	}
	//ストリーム取得スレッドを開始する
	public void startStream(){
		if(!isActive) return;
		getTimeline(false);
		
		
		//ストリーム中、スクリーンをオンにする
		SharedPreferences pref = getSharedPreferences("funfuvtwt_setting",MODE_PRIVATE);
		boolean display_on = pref.getBoolean("display_on", true);
		if(display_on){
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
			wl.acquire();
		}
		
		if(oauthConsumer == null){
			oauthConsumer =
	        		new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
		}
		if( oauthConsumer.getToken() == null ||
			oauthConsumer.getTokenSecret() == null){
			Intent intent = new Intent(MyTweetActivity.this, SettingActivity.class);
			startActivity(intent);
			setOAuthFromPreference();
		}
		
		//自分の名前を取得
		if(MyName!=null){
			if(MyName.toString()==""){
				FetchMyName name = new FetchMyName(MyName);
				name.execute(oauthConsumer);
			}
		}
		
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_media_pause);
		TempButton.setImageBitmap(bmp);
		Refresh.setVisibility(View.GONE);
		
		mKeepRunning = true;	
		
		if(thread != null){
			if((!looper.disable) && thread.isAlive()){
				return;
			}
		}
		looper = new MyRunnable();
		thread = new Thread(looper);
		thread.start();
	
	}
	
	//ストリーム取得スレッドを停止する
	public void stopStream(){
		if(thread != null){
			try{
				thread.interrupt();
				looper.disable = true;
				Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_media_play);
				TempButton.setImageBitmap(bmp);
				Refresh.setVisibility(View.VISIBLE);
			}catch (Exception e) {
				Log.e("Twitter", "Thread interupte" + e.toString());
			}
		}
		mKeepRunning = false;
		if(wl != null)
			wl.release();
	}
	
	
	
	//ストリーム取得用Runnable
	private class MyRunnable implements Runnable{
		public Boolean disable = false;
		@Override
        public void run() {      
			handler.post(new Runnable() {
        		@Override
                public void run() {
        			Toast.makeText(MyTweetActivity.this, "ストリームを開始しています。", Toast.LENGTH_SHORT).show();

                    }
               });
    		//https通信が証明書がうんたらで失敗するので無理矢理通す
    		HttpClient httpclient = new DefaultHttpClient();
        	HttpContext httpcontext = new BasicHttpContext();
            try{
            	KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            	trustStore.load(null, null);
            	SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            	sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            	Scheme https = new Scheme("https", sf, 443);
            	httpclient.getConnectionManager().getSchemeRegistry().register(https);
            	httpcontext.setAttribute(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            	httpcontext.setAttribute(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            	httpcontext.setAttribute(CoreProtocolPNames.HTTP_CONTENT_CHARSET, HTTP.UTF_8);
            }catch (Throwable t) {
                Log.e("Main", "testHttpClient", t);
            }
            
            //一応タイムアウト設定。長めに15秒ほど
            HttpParams httpParamsObj = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParamsObj, 2000);	//ソケットのタイムアウト
            HttpConnectionParams.setSoTimeout(httpParamsObj, 60000);  		//データのタイムアウト
            
            //いまのところ"https://userstream.twitter.com/2/user.json"だけ
    		HttpGet request = new HttpGet("https://userstream.twitter.com/2/user.json");
    		//100-continueを解除
    		HttpParams httpparams = new BasicHttpParams();
            HttpProtocolParams.setUseExpectContinue(httpparams, false);
            request.setParams(httpparams);//GET通信用オブジェクトに設定
    		
            try{
            	// GET通信用オブジェクトに署名
                oauthConsumer.sign(request);
            }catch (OAuthMessageSignerException e) {
                Log.e(getClass().getSimpleName(), "OAuthMessageSignerException", e);
            } catch (OAuthExpectationFailedException e) {
                Log.e(getClass().getSimpleName(), "OAuthExpectationFailedException", e);
            } catch (OAuthCommunicationException e) {
                Log.e(getClass().getSimpleName(), "OAuthCommunicationException", e);
            }
            //List<Single_Tweet> list = null;
            boolean catchException = false;
    		try {
    			HttpResponse response = httpclient.execute(request);
    			int statuscode = response.getStatusLine().getStatusCode();
    			if (statuscode != HttpStatus.SC_OK) {
    				if(statuscode == HttpStatus.SC_FORBIDDEN){
    					handler.post(new Runnable() {
    						@Override
    						public void run() {
    							//Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_media_play);
    							//TempButton.setImageBitmap(bmp);
    							stopStream();
    							Toast.makeText(MyTweetActivity.this, "API要求を拒否されました。\n最近多いよね。", Toast.LENGTH_LONG).show();
    	                    	}
    					});
    				}else{
    					handler.post(new Runnable() {
    						@Override
    						public void run() {
    							//Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_media_play);
    							//TempButton.setImageBitmap(bmp);
    							stopStream();
    							Toast.makeText(MyTweetActivity.this, "通信失敗：ネットワーク接続を確認してください。\n認証失敗か規制の可能性もあります。", Toast.LENGTH_LONG).show();
    	                    	}
    					});
    				}
    				return;
    			}
    			InputStream in = response.getEntity().getContent();
    			BufferedReader reader = new BufferedReader( new InputStreamReader(in) );
    			parseTweets(reader);
    			in.close(); 
    		}catch(RuntimeException IllegalStateException){
    			catchException = true;
    		}catch(Exception IOException){
     			Log.e("Twitter", "doInBackground_" + IOException.toString());
     			catchException = true;
    		}
    		if(mKeepRunning/*catchException*/){
    			handler.post(new Runnable(){
	        		@Override
	                public void run() {
	            		if(retry < 20){
	            			stopStream();
	        				startStream(3000);
	        					retry++;
	        			}
	                }
	           });
    		}
        }
		
		
		//httpから取得したストリームを延々と読み出してパースする
		private void parseTweets( BufferedReader reader ) {
			try {
				String line = "";
				do {		
					line =  reader.readLine();
				
					JSONObject jsonObj;
					JSONObject User;
					Long id = new Long(0);
					String createdTime = "";
					String Text = "";
					String screenName = "";
					String profileImageUrlHttps = "";
					Boolean faved = false;
					Boolean retweetedByOther =false;
					long retweet_count = new Long(0);
					String screenName_retweeter = "";
					Boolean protect = false;
					String icon_url_retweeter = "";
					String name = "";
					String source = "";
					try{
						// JSONデータからJSONObject取得
						jsonObj = new JSONObject(line);
						
						//ふぁぼられた
						if(parseFavo(jsonObj)){
							continue;
						}
						
						id = jsonObj.getLong("id");
						Text = jsonObj.getString("text");  

						source = jsonObj.getString("source");;
		                int start = source.indexOf(">");
		                int end;
		                
		                if(start<0){
		                	start = 0;
		                	end = source.length();
		                }else{
		                	end = source.indexOf("<", start);
		                	if(end<0)
		                		end = source.length();
		                	start++;
		                }
		                source = source.substring(start, end);

						faved = jsonObj.getBoolean("favorited");
						retweet_count = jsonObj.getLong("retweet_count");
						createdTime = jsonObj.getString("created_at");
						
						User = jsonObj.getJSONObject("user");
						screenName = User.getString("screen_name");
						profileImageUrlHttps = User.getString("profile_image_url_https");
						protect = User.getBoolean("protected");
						name =  User.getString("name");
						
						//リツイートの時
						if(!jsonObj.isNull("retweeted_status")){
							retweetedByOther = true;	
							screenName_retweeter = screenName;
							icon_url_retweeter = profileImageUrlHttps;
							screenName = Text.substring(3, Text.indexOf(":"));
							JSONObject retweet = jsonObj.getJSONObject("retweeted_status");
							Text = retweet.getString("text");
							
							JSONObject retweet_user = retweet.getJSONObject("user");
							screenName = retweet_user.getString("screen_name");
							name = retweet_user.getString("name");
							profileImageUrlHttps = retweet_user.getString("profile_image_url_https");
						}
						
					}catch(JSONException  e){
						Log.e(getClass().getSimpleName(), "JSON Error", e);
						continue;
					}
					
					// つぶやき情報保持用オブジェクト生成
	                final Single_Tweet entity =  new Single_Tweet();     
	                entity.id = Long.toString(id);
	                entity.user_id = screenName;
	                entity.text = Text;
	                entity.icon_url = profileImageUrlHttps;
	                entity.createdTime = createdTime;
	                entity.faved = faved;
	                entity.retweeted = false;
	                entity.retweet_count = retweet_count;
	                entity.retweetedByOther = retweetedByOther;
	                entity.protect = protect;
	                entity.screenName_retweeter = screenName_retweeter;
	                entity.icon_url_retweeter = icon_url_retweeter;
	                entity.name = name;
	                entity.source = source;
	                
	                handler.post(new Runnable() {
	                	@Override
	    	            public void run() {	    	  
	                		if(innerAdapter == null){
	                			return;
	                		}
	                		int position = 0;
	                		int y = 0;  
	                		try{
	                			position = listView.getFirstVisiblePosition();  
	                			y = listView.getChildAt(0).getTop();  
	                		}catch(Exception e){}
	                		
	                		innerAdapter.insert(entity,0);

	                		
	                        try{
	                        	SharedPreferences pref = getSharedPreferences("funfuvtwt_setting",MODE_PRIVATE);
	                        	int tweet_num = pref.getInt("tweet_num", 50);
	                			if(innerAdapter.getCount()>tweet_num){ //保持しておくリストの行数
	                				while(innerAdapter.getCount()>tweet_num)
	                					innerAdapter.remove( innerAdapter.getItem(innerAdapter.getCount()-1)) ;		
	                			}
	                		}catch(Exception e){}
	                			
	                		//listView.invalidateViews();
	                		innerAdapter.notifyDataSetChanged();
	                		if(position!=0)
	                			listView.setSelectionFromTop(position+1, y); 
	                			                		
	                	}
	    	        });
        
				} while (mKeepRunning && !disable);	//一応念のためwhile(true)にしない
			} //catch (StringIndexOutOfBoundsException e){
	        catch (Exception e) {			
				Log.e(getClass().getSimpleName(), "parseTweets Error", e);
				return;
	        }
		}	

		
		//ふぁぼのJsonオブジェクト解析
		private Boolean parseFavo(JSONObject jsonObj){
			try{
				if(jsonObj.isNull("target_object")){
					return false; //ふぁぼじゃない
				}
				if(jsonObj.getString("event").compareTo("unfavorite") == 0){
					return false; //あんふぁぼ
				}
				
				JSONObject target_object = jsonObj.getJSONObject("target_object");
				String text = target_object.getString("text");
				//if(text.length()>36){
				//	text = text.substring(0, 40) + "…";
				//}
				final String shortText = text;
				
				JSONObject source = jsonObj.getJSONObject("source");
				final String screen_name = source.getString("screen_name");
				//final Long count = source.getLong("favourites_count");
				String My = MyName.toString();
				if(My.compareTo(screen_name) == 0){
					return false;
				}			
				handler.post(new Runnable() {
					@Override
					public void run() {
						ToastMaster.makeText(context,  "@"+ screen_name +"さんがふぁぼ！:「" +shortText + "」", ToastMaster.LENGTH_LONG).show();
	                   	}
				});
			}catch(Exception e){
				return false;	
			}
			return true;
		}
	}
}

