package net.d_ichi84.Activity;

import oauth.signpost.OAuth;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import data.Constants;
import net.d_ichi84.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class Setting2Activity extends Activity implements OnClickListener {
	
	public static CommonsHttpOAuthProvider oauthProvider //OAuth�F�؃v���o�C�_ 
	= new CommonsHttpOAuthProvider(
			Constants.REQUEST_TOKEN_ENDPOINT_URL,
			Constants.ACCESS_TOKEN_ENDPOINT_URL,
			Constants.OAUTH_URL);	

	Button oauthButton;
	TextView oauthText;
	CheckBox whiteBack;
	CheckBox displayOn;
	CheckBox favVisible;
	CheckBox RetweetVisible;
	RadioButton Num25;
	RadioButton Num50;
	RadioButton Num100;
	RadioButton Num150;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//Window�^�C�g���͗v��Ȃ�
        setContentView(R.layout.setting2);         
        findViews();
    }

	private void findViews() {
		oauthButton = (Button)findViewById(R.id.authButton_setting);
		oauthButton.setOnClickListener(this);
		
		oauthText = (TextView)findViewById(R.id.oauthText);
		whiteBack = (CheckBox)findViewById(R.id.checkBox_whiteback);
		whiteBack.setOnClickListener(this);
		
		displayOn = (CheckBox)findViewById(R.id.checkBox_displayon);
		displayOn.setOnClickListener(this);
		
		favVisible = (CheckBox)findViewById(R.id.checkBox_fav);
		favVisible.setOnClickListener(this);
		
		RetweetVisible = (CheckBox)findViewById(R.id.checkBox_retweet);
		RetweetVisible.setOnClickListener(this);
		
		Num25 = (RadioButton)findViewById(R.id.tweet_num25);
		Num25.setOnClickListener(this);
				
		Num50 = (RadioButton)findViewById(R.id.tweet_num50);
		Num50.setOnClickListener(this);
		
		Num100 = (RadioButton)findViewById(R.id.tweet_num100);
		Num100.setOnClickListener(this);
		
		Num150 = (RadioButton)findViewById(R.id.tweet_num150);
		Num150.setOnClickListener(this);
	}
	

	@Override
	public void onClick(View v){
		
	   SharedPreferences pref = getSharedPreferences("funfuvtwt_setting",MODE_WORLD_WRITEABLE );
	   SharedPreferences.Editor editor = pref.edit();
	        
		switch(v.getId()){
		case R.id.authButton_setting://OAuth�F�؃{�^���������ꂽ�Ƃ��̓���
			String authURL = "";
			try{
				Toast.makeText(Setting2Activity.this,
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
	            Toast.makeText(Setting2Activity.this,
	                    getResources().getText(R.string.OauthErrorMsg),Toast.LENGTH_LONG).show();
	        } catch (OAuthNotAuthorizedException e) {
	            Log.e(getClass().getSimpleName(), "Twitter OAuth Error", e);
	            Toast.makeText(Setting2Activity.this,
	                    getResources().getText(R.string.OauthErrorMsg),Toast.LENGTH_LONG).show();
	        } catch (OAuthExpectationFailedException e) {
	            Log.e(getClass().getSimpleName(), "Twitter OAuth Error", e);
	            Toast.makeText(Setting2Activity.this,
	                    getResources().getText(R.string.OauthErrorMsg),Toast.LENGTH_LONG).show();
	        } catch (OAuthCommunicationException e) {
	            Log.e(getClass().getSimpleName(), "Twitter OAuth Error", e);
	            Toast.makeText(Setting2Activity.this,
	            		getResources().getText(R.string.OauthErrorMsg),Toast.LENGTH_LONG).show();
	        }
			break;
			
		case R.id.checkBox_whiteback:
			editor = editor.putBoolean("white_back",whiteBack.isChecked());
			break;
		case R.id.checkBox_displayon:
			editor = editor.putBoolean("display_on",displayOn.isChecked());
			break;
			
		case R.id.checkBox_fav:
			editor = editor.putBoolean("fav_on",favVisible.isChecked());
			break;
			
		case R.id.checkBox_retweet:
			editor = editor.putBoolean("retweet_on",RetweetVisible.isChecked());
			break;
			
			
		case R.id.tweet_num25:
			editor = editor.putInt("tweet_num", 25);
			break;			
		case R.id.tweet_num50:
			editor = editor.putInt("tweet_num", 50);
			break;
		case R.id.tweet_num100:
			editor = editor.putInt("tweet_num", 100);
			break;
		case R.id.tweet_num150:
			editor = editor.putInt("tweet_num", 150);
			break;
		}
		editor.commit();
	}

	private void loadSetting(){
		SharedPreferences pref = getSharedPreferences("funfuvtwt_token",MODE_PRIVATE);
        String token = pref.getString("token","");
        String tokenSecret = pref.getString("tokenSecret", "");
		
        if(token.length()>0 && tokenSecret.length()>0)
			oauthText.setText("�F�؍ς݂ł��B");
		else
			oauthText.setText("�F�؂���Ă��܂���B");
			
		pref = getSharedPreferences("funfuvtwt_setting",MODE_PRIVATE);
		boolean white_back = pref.getBoolean("white_back", false);
		boolean display_on = pref.getBoolean("display_on", true);
		boolean fav_on = pref.getBoolean("fav_on", true);
		boolean retweet_on = pref.getBoolean("retweet_on", false);
		
		int tweet_num = pref.getInt("tweet_num", 50);
		
		if(white_back) whiteBack.setChecked(true); else whiteBack.setChecked(false);
		if(display_on) displayOn.setChecked(true); else displayOn.setChecked(false);
		if(fav_on) favVisible.setChecked(true); else favVisible.setChecked(false);
		if(retweet_on) RetweetVisible.setChecked(true); else RetweetVisible.setChecked(false);
		
		if(tweet_num == 150) 
			Num150.setChecked(true);
		else if(tweet_num == 100)
			Num100.setChecked(true);
		else if(tweet_num == 50)
			Num50.setChecked(true);
		else
			Num25.setChecked(true);
	}
	
    // onResume���\�b�h
    @Override
    protected void onResume() {
        super.onResume();
        loadSetting();
        
        //Twitter�F�؉�ʂ���߂��Ă����Ƃ��̏���

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
        editor = editor.putString("token", oauthConsumer.getToken());
        editor = editor.putString("tokenSecret", oauthConsumer.getTokenSecret());
        editor.commit();  
        // �C���e���g��URI���N���A
        getIntent().setData(null);
        
        Intent intent = new Intent(Setting2Activity.this, MyTweetActivity.class);
    	startActivity(intent);
    	finish();
    }
}
