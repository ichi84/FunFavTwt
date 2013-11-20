package net.d_ichi84;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import net.d_ichi84.Activity.SearchActivity;
import android.content.Context;
import android.content.Intent;
import android.text.style.ClickableSpan;
import android.view.View;

public class HashClick extends ClickableSpan{
	String url;
	String id;
	 Context context;
	 
	public HashClick(String url, String id, Context context) {
		super();
		this.url = url;
		this.id = id;
		this.context = context;
	}
	
	@Override
	public void onClick(View v){
		Calendar calendar = Calendar.getInstance( TimeZone.getTimeZone( "UTC" ) );
		TweetListArrayAdapter.popup_now = calendar.getTime();
		
		Intent intent = new Intent(context, SearchActivity.class);
		intent.putExtra("search", url);
		context.startActivity(intent);
    	
	}
}