package net.d_ichi84;

import java.util.Calendar;
import java.util.TimeZone;

import net.d_ichi84.dialog.IdDialog;
import android.app.Dialog;
import android.text.style.ClickableSpan;
import android.view.View;

public class IdClick extends ClickableSpan{
	String url;
	String id;
	
	public IdClick(String url, String id) {
		super();
		this.url = url;
		this.id = id;
	}
	
	@Override
	public void onClick(View v){
		Calendar calendar = Calendar.getInstance( TimeZone.getTimeZone( "UTC" ) );
		TweetListArrayAdapter.popup_now = calendar.getTime();

		Dialog dialog = new IdDialog(url.substring(1), id, null, null);
		dialog.show();
	}
}