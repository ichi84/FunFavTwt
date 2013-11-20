package net.d_ichi84;

import java.util.Calendar;
import java.util.TimeZone;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.style.ClickableSpan;
import android.view.View;

public class URLClick extends ClickableSpan{
	String url;
	String id;
	Context con;
	
	public URLClick(String url, Context con) {
		super();
		this.url = url;
		this.con = con;
	
	}
	
	@Override
	public void onClick(View v){
		Calendar calendar = Calendar.getInstance( TimeZone.getTimeZone( "UTC" ) );
		TweetListArrayAdapter.popup_now = calendar.getTime();
		
		if(!url.startsWith("http://") && !url.startsWith("https://")){
			url = "http://" + url;
		}
		Uri uri = Uri.parse(url);
		Intent i = new Intent(Intent.ACTION_VIEW,uri);
		con.startActivity(i);
	}
}