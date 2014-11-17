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
	
			
	
	public static CommonsHttpOAuthProvider oauthProvider //OAuth�F�؃v���o�C�_ 
	= new CommonsHttpOAuthProvider(
			Constants.REQUEST_TOKEN_ENDPOINT_URL,
			Constants.ACCESS_TOKEN_ENDPOINT_URL,
			Constants.OAUTH_URL);	

	private Button oauthButton;
	private Button backButton;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//Window�^�C�g���͗v��Ȃ�
        setContentView(R.layout.setting); 
        
        findViews();
        //setListeners();     
        
    }
	
	@Override
	 public boolean dispatchKeyEvent(KeyEvent event) {
	  if(event.getAction() == KeyEvent.ACTION_DOWN){
	   switch(event.getKeyCode()){
	   case KeyEvent.KEYCODE_BACK:
		   this.finish(); //Back�L�[�ŏI������B
		   return true;
	   }
	  }
	  // �����������ꂽ���\�b�h�E�X�^�u
	  return super.dispatchKeyEvent(event);
	}
	
	private void findViews(){
		oauthButton = (Button)findViewById(R.id.authButton);
		backButton = (Button)findViewById(R.id.button1);
		
		oauthButton.setOnClickListener(this);
		backButton.setOnClickListener(this);
	}
	
    // onResume���\�b�h(Twitter�F�؉�ʌ�̏���)
    @Override
    protected void onResume() {
        super.onResume();

        // �C���e���g�擾
        Intent data = getIntent();

        // �C���e���g���擾�ł��Ȃ������ꍇ�I��
        if (data == null) {
            return;
        }

        // URI�擾
        Uri uri = data.getData();

        // URI���擾�ł��Ȃ������ꍇ�I��
        if (uri == null) {
            return;
        }

        // Twitter�F�؉�ʂ���łȂ��ꍇ�I��
        if (!uri.toString().startsWith(Constants.CALBACK_URL)) {
            return;
        }

        // Twitter�F�؉�ʂŋ����Ȃ������ꍇ�I��
        if (uri.getQueryParameter(Constants.REQUEST_TOKEN_DENIED) != null) {
            //oauthConsumer.setTokenWithSecret(null, null);
            return;
        }

        // Access Token�擾
        String token = uri.getQueryParameter(OAuth.OAUTH_TOKEN);
        // Verifier�擾
        String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);

        // Access Token Secret�擾
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
        // �C���e���g��URI���N���A
        getIntent().setData(null);
        
        Intent intent = new Intent(SettingActivity.this, MyTweetActivity.class);
    	startActivity(intent);
    	finish();
    }
    
    
	@Override
	public void onClick(View v){
		switch(v.getId()){
		case R.id.authButton://OAuth�F�؃{�^���������ꂽ�Ƃ��̓���
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
				
				// Twitter�F�؉�ʂ̋N���p�ݒ�
				CommonsHttpOAuthConsumer oauthConsumer =
		        		new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);	
	            authURL = oauthProvider.retrieveRequestToken
	            		(oauthConsumer, Constants.CALBACK_URL);

		        // Twitter�F�؉�ʋN��
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
			
		case R.id.button1://back�{�^���������ꂽ�Ƃ��̓���
			Intent intent = new Intent(SettingActivity.this, MyTweetActivity.class);
        	startActivity(intent);
        	finish();
			break;
		}
		
	}
	
}
