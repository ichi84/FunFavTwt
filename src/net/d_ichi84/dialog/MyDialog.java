package net.d_ichi84.dialog;

import data.Constants;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;

public class MyDialog extends Dialog{
	protected CommonsHttpOAuthConsumer oauthConsumer = 
		new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
	protected Context context;
	
	public MyDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
		setOAuthFromPreference();
	}

	//PreferenceからOAuthトークンを取得してセットする	
	protected void setOAuthFromPreference(){
		 SharedPreferences pref = ((Activity)context).getSharedPreferences("funfuvtwt_token",Context.MODE_WORLD_READABLE);
	        String token = pref.getString("token","");
	        String tokenSecret = pref.getString("tokenSecret", "");
	        
	        if(token.length()>0 && tokenSecret.length()>0){
	        	oauthConsumer.setTokenWithSecret(token, tokenSecret);
	        }
	}

}
