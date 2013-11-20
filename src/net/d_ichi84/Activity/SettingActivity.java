package net.d_ichi84.Activity;

import data.Constants;
import net.d_ichi84.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.*;
import android.content.Intent;
import android.net.Uri;

import oauth.signpost.OAuth;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import android.content.SharedPreferences;

public class SettingActivity extends Activity implements OnClickListener{
	
			
	
	public static CommonsHttpOAuthProvider oauthProvider //OAuth認証プロバイダ 
	= new CommonsHttpOAuthProvider(
			Constants.REQUEST_TOKEN_ENDPOINT_URL,
			Constants.ACCESS_TOKEN_ENDPOINT_URL,
			Constants.OAUTH_URL);	

	private Button oauthButton;
	private Button backButton;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//Windowタイトルは要らない
        setContentView(R.layout.setting); 
        
        findViews();
        //setListeners();     
        
    }
	
	@Override
	 public boolean dispatchKeyEvent(KeyEvent event) {
	  if(event.getAction() == KeyEvent.ACTION_DOWN){
	   switch(event.getKeyCode()){
	   case KeyEvent.KEYCODE_BACK:
		   this.finish(); //Backキーで終了する。
		   return true;
	   }
	  }
	  // 自動生成されたメソッド・スタブ
	  return super.dispatchKeyEvent(event);
	}
	
	private void findViews(){
		oauthButton = (Button)findViewById(R.id.authButton);
		backButton = (Button)findViewById(R.id.button1);
		
		oauthButton.setOnClickListener(this);
		backButton.setOnClickListener(this);
	}
	
    // onResumeメソッド(Twitter認証画面後の処理)
    @Override
    protected void onResume() {
        super.onResume();

        // インテント取得
        Intent data = getIntent();

        // インテントが取得できなかった場合終了
        if (data == null) {
            return;
        }

        // URI取得
        Uri uri = data.getData();

        // URIが取得できなかった場合終了
        if (uri == null) {
            return;
        }

        // Twitter認証画面からでない場合終了
        if (!uri.toString().startsWith(Constants.CALBACK_URL)) {
            return;
        }

        // Twitter認証画面で許可しなかった場合終了
        if (uri.getQueryParameter(Constants.REQUEST_TOKEN_DENIED) != null) {
            //oauthConsumer.setTokenWithSecret(null, null);
            return;
        }

        // Access Token取得
        String token = uri.getQueryParameter(OAuth.OAUTH_TOKEN);
        // Verifier取得
        String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);

        // Access Token Secret取得
        CommonsHttpOAuthConsumer oauthConsumer =
        		new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);	
	
        try {
        	oauthConsumer.setTokenWithSecret(token, "");
            oauthProvider.retrieveAccessToken(oauthConsumer, verifier);
        } catch (OAuthMessageSignerException e) {
            Log.e(getClass().getSimpleName(),
                "Twitter Access Token Secret Error", e);
        } catch (OAuthNotAuthorizedException e) {
            Log.e(getClass().getSimpleName(),
                "Twitter Access Token Secret Error", e);
        } catch (OAuthExpectationFailedException e) {
            Log.e(getClass().getSimpleName(),
                "Twitter Access Token Secret Error", e);
        } catch (OAuthCommunicationException e) {
            Log.e(getClass().getSimpleName(),
                "Twitter Access Token Secret Error", e);
        }

        SharedPreferences pref = getSharedPreferences("funfuvtwt_token",MODE_WORLD_WRITEABLE );
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("token", oauthConsumer.getToken());
        editor.putString("tokenSecret", oauthConsumer.getTokenSecret());
        editor.commit();  
        // インテントのURIをクリア
        getIntent().setData(null);
        
        Intent intent = new Intent(SettingActivity.this, MyTweetActivity.class);
    	startActivity(intent);
    	finish();
    }
    
    
	@Override
	public void onClick(View v){
		switch(v.getId()){
		case R.id.authButton://OAuth認証ボタンが押されたときの動作
			/*
			if( oauthConsumer.getToken().length()>0 &&
				oauthConsumer.getTokenSecret().length()>0){	
				break;
			}
			*/
			
			String authURL = "";
			try{
				Toast.makeText(SettingActivity.this,
	                    getResources().getText(R.string.Wait),Toast.LENGTH_LONG).show();
				
				// Twitter認証画面の起動用設定
				CommonsHttpOAuthConsumer oauthConsumer =
		        		new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);	
	            authURL = oauthProvider.retrieveRequestToken
	            		(oauthConsumer, Constants.CALBACK_URL);

		        // Twitter認証画面起動
		        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authURL)));
		        
			}catch (OAuthMessageSignerException e) {
	            Log.e(getClass().getSimpleName(), "Twitter OAuth Error", e);
	            Toast.makeText(SettingActivity.this,
	                    getResources().getText(R.string.OauthErrorMsg),Toast.LENGTH_LONG).show();
	        } catch (OAuthNotAuthorizedException e) {
	            Log.e(getClass().getSimpleName(), "Twitter OAuth Error", e);
	            Toast.makeText(SettingActivity.this,
	                    getResources().getText(R.string.OauthErrorMsg),Toast.LENGTH_LONG).show();
	        } catch (OAuthExpectationFailedException e) {
	            Log.e(getClass().getSimpleName(), "Twitter OAuth Error", e);
	            Toast.makeText(SettingActivity.this,
	                    getResources().getText(R.string.OauthErrorMsg),Toast.LENGTH_LONG).show();
	        } catch (OAuthCommunicationException e) {
	            Log.e(getClass().getSimpleName(), "Twitter OAuth Error", e);
	            Toast.makeText(SettingActivity.this,
	            		getResources().getText(R.string.OauthErrorMsg),Toast.LENGTH_LONG).show();
	        }
			break;
			
		case R.id.button1://backボタンがおされたときの動作
			Intent intent = new Intent(SettingActivity.this, MyTweetActivity.class);
        	startActivity(intent);
        	finish();
			break;
		}
		
	}
	
}
