package net.d_ichi84.task;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import net.d_ichi84.BitmapCache;
import net.d_ichi84.R;
import net.d_ichi84.Activity.MyTweetActivity;

import data.Single_Tweet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class IconFetchAsyncTask extends AsyncTask<String, String, Bitmap > {

	ImageView image;
	String urlString;
	ArrayAdapter<Single_Tweet> adapter;
	private String tag = ""; 
	Context context;
	
	public IconFetchAsyncTask(ImageView _image, String _url, Context _context){
		image = _image;		
		urlString = _url;
		if(_image.getTag() != null)
			tag = image.getTag().toString();
		context = _context;
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		Bitmap bitmap = BitmapCache.getImage(urlString);
		if(bitmap != null){
			return bitmap;
		}
		URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(getClass().getSimpleName(), "Malformed URL Error", e);
        }
       
        try {
            bitmap = BitmapFactory.decodeStream(url.openStream());
            BitmapCache.setImage(urlString, bitmap);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Bitmap decodeStream Error", e);
        }
        return bitmap;
	}
	@Override
    protected void onPostExecute(Bitmap result) {
		if(	!tag.equals(image.getTag())/* && image.getTag() != null*/){
			image.setImageBitmap( BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_noone) );
			image.invalidate();
			return;
	    } 
		if(result == null){
			image.setImageBitmap( BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_noone) );
			image.invalidate();
			return;
		}
		image.setImageBitmap(result);
		image.invalidate();	
	}
}
	

