package net.d_ichi84;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.Single_Tweet;

import net.d_ichi84.Activity.MyTweetActivity;
import net.d_ichi84.dialog.IdDialog;
import net.d_ichi84.task.IconFetchAsyncTask;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.content.SharedPreferences;

//���X�g�̃A�_�v�^�[
public class TweetListArrayAdapter extends ArrayAdapter<Single_Tweet>{
	int resourceId;
	Context context;
	Boolean isReplyActivity=false;
	public static Date popup_now ;
	//View�z���_�B1�x�����View�͍ė��p���適�������Ȃ������ǂ��납�x���Ȃ����̂ł�߂�
	static class ViewHolder {  
		ImageView status_icon;
		LinearLayout retweet_layout;
		Single_Tweet item ;
		
		TextView TweetText;  
		TextView UserText;
		TextView name;
		TextView source;
		TextView time_short;
		TextView TimeText;
		ImageButton icon;
		ImageView r_icon;
	}  
	
	public TweetListArrayAdapter(Context context, int resourceID){
		super(context, resourceID);
		this.resourceId = resourceID;
		this.context = context;
	}
	
	public TweetListArrayAdapter(Context context, int resourceID, List<Single_Tweet> list){
		super(context, resourceID, list);
		this.resourceId = resourceID;
		this.context = context;	
	}
	
	public TweetListArrayAdapter(Context context, int resourceID, List<Single_Tweet> list, Boolean isReplyActivity){
		super(context, resourceID, list);
		this.resourceId = resourceID;
		this.context = context;
		this.isReplyActivity= isReplyActivity;
	}
	

	
	@Override
	public View getView(final int pos, View convertView, final ViewGroup parent){
		final ViewHolder holder;
		Bitmap bmp;
		
		final Resources res = context.getResources();
		holder = new ViewHolder();
		
		if(convertView == null){
			LayoutInflater inflater = 
					(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(resourceId, null);
			
			
		}else{
//			holder.icon = (ImageButton) convertView.findViewById(R.id.userIconView1);
		}
		
			holder.status_icon = (ImageView)convertView.findViewById(R.id.status_image);
			holder.retweet_layout = (LinearLayout)convertView.findViewById(R.id.retweet_layout);
			holder.TweetText = (TextView)convertView.findViewById(R.id.tweetTextView1);
			holder.UserText =  (TextView)convertView.findViewById(R.id.id);
			holder.source =   (TextView)convertView.findViewById(R.id.textView_source);
			holder.TimeText = (TextView)convertView.findViewById(R.id.time);
			holder.time_short = (TextView)convertView.findViewById(R.id.time_short);
			holder.name = (TextView)convertView.findViewById(R.id.name);
			
			holder.icon = (ImageButton) convertView.findViewById(R.id.userIconView1);
			holder.icon.setImageBitmap( BitmapFactory.decodeResource(res, R.drawable.icon_noone) );
			holder.icon.invalidate();
			/*
			//ListView�̕`�悪�����Ă�̂ŕ`�悵�����B
			
			Bitmap iconbmp = BitmapCache.getImage("icon_default");
			if(iconbmp == null){
				iconbmp =  BitmapFactory.decodeResource(res, R.drawable.icon_noone);
				BitmapCache.setImage("icon_default", iconbmp);
			}
			holder.icon.setImageBitmap(iconbmp);;
			*/
			//convertView.setTag(holder);
		//}else {  
	    //    holder = (ViewHolder)convertView.getTag();  
	    //}  
		 
			
		final ImageButton favButton = (ImageButton)convertView.findViewById(R.id.favImageButton1);
		final ImageButton retweetButton = (ImageButton)convertView.findViewById(R.id.retweetButton);
		
		SharedPreferences pref = context.getSharedPreferences("funfuvtwt_setting",Context.MODE_PRIVATE);
		boolean fav_on = pref.getBoolean("fav_on", true);
		boolean retweet_on = pref.getBoolean("retweet_on", false);
		if(fav_on) favButton.setVisibility(View.VISIBLE); else favButton.setVisibility(View.INVISIBLE);
		if(retweet_on) retweetButton.setVisibility(View.VISIBLE); else retweetButton.setVisibility(View.INVISIBLE);
		
		final Single_Tweet item = getItem(pos);
		
		//�Ԃ₫�{��
		item.text = item.text.replaceAll("&gt;", ">");
		item.text = item.text.replaceAll("&lt;", "<");
		item.text = item.text.replaceAll("&amp;", "&");
		item.text = item.text.replaceAll("�@", "  ");  //���K�\���̃}�b�`�����������Ȃ�i���Ԃ�SDK�̃o�O�j�̂őS�p�X�y�[�X�𔼊p2�ɂ���B
		
		
		final View fi_convertView = convertView ;
		SpannableString spannable = new SpannableString(item.text);
		
		// id����(@hoge)
		final Pattern patternId = Pattern.compile("@[\\p{ASCII}&&[^\\s:;()@]]+");
		Matcher matcherId = patternId.matcher(item.text);
					
		while(matcherId.find()){
            IdClick span = new IdClick(matcherId.group(), item.id);
            spannable.setSpan(span, matcherId.start(), matcherId.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		
		// �n�b�V���^�O����(#hoge)
		final Pattern patternHashtag = Pattern.compile("(#[\\S]+)");
		Matcher matcherHashtag = patternHashtag.matcher(item.text);
		while(matcherHashtag.find()){
            HashClick span = new HashClick(matcherHashtag.group(), item.id, context);
            spannable.setSpan(span, matcherHashtag.start(), matcherHashtag.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		// URL����
		String UrlPattern = "(http://|https://){1}[^\\p{ASCII}0-9A-Za-z_\\.\\-/:\\#\\?\\=\\&\\;\\%\\~\\+]+"; 
		final Pattern patternURL
		// �W���I��URL���o�p�^�[��
		= Pattern.compile(UrlPattern);
		 
		// �×~��URL���o�p�^�[�� 
		//= Pattern.compile("(http|https):([^\\x00-\\x20()\"<>\\x7F-\\xFF])*");
		Matcher matcherURL = patternURL.matcher(item.text);
		while(matcherURL.find()){
			//��������������ClickableSpan���p�������N���X������i���ł����Ă�j
            URLClick span = new URLClick(matcherURL.group(), context);
            spannable.setSpan(span, matcherURL.start(), matcherURL.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		
		
		/*
		//�S�����}�b�`������
		while( !matcherURL.find() && !matcherId.find() && !matcherHashtag.find()){
			spannable.setSpan( new ClickableSpan(){
					@Override	
					public void updateDrawState(TextPaint ds) {
						super.updateDrawState(ds);
						ds.setUnderlineText(false); //�����Ƃ�����Ȃ�
					}
		      		@Override
		      		public void onClick(View v){
		      			tweetClickEvent(fi_convertView);
		      		}
		      	
		      },
		      matcherAll.start(), matcherAll.end(),
		      spannable.getSpanFlags( new ForegroundColorSpan(Color.WHITE) ));
			
			//����Ƀ����N�F�ɂȂ�̂łȂ���
			//�F�𔒂ɖ߂�
			spannable.setSpan( new ForegroundColorSpan(Color.WHITE),
								matcherAll.start(), matcherAll.end(),
								spannable.getSpanFlags( new ForegroundColorSpan(Color.WHITE) ));
		}
      		
		*/
		
		holder.TweetText.setText(spannable);
		// LinkMovementMethod �̃C���X�^���X���擾���܂�
        MovementMethod movementmethod = LinkMovementMethod.getInstance();
        
        // TextView �� LinkMovementMethod ��o�^���܂�
        holder.TweetText.setMovementMethod(movementmethod);
		
		
      
        
        
		//���[�U�[ID
		holder.UserText.setText("@"+ item.user_id);
		holder.name.setText(item.name);
		holder.source.setText(item.source + "����");
		
		//����
		String [] strArray = item.createdTime.split(" ");
		Calendar calendar = Calendar.getInstance( TimeZone.getTimeZone( "UTC" ) );
		
		
		HashMap<String, Integer> my_month = new HashMap<String, Integer>(){
			private static final long serialVersionUID = 1L;{
			put("Jan", 0);put("Feb", 1);put("Mar", 2);put("Apr", 3);put("May", 4);put("Jun", 5);
			put("Jul", 6);put("Aug", 7);put("Sep", 8);put("Oct", 9);put("Nov", 10);put("Dec", 11);		
		}};
		String[] my_month2 = new String[]{
			"Jan", "Feb", "Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"		
		};
		
		try{
			calendar.set(Calendar.YEAR, Integer.parseInt(strArray[5])); //�N
			calendar.set(Calendar.MONTH, my_month.get(strArray[1])); //���i0..11�j
			calendar.set(Calendar.DAY_OF_MONTH,
					Integer.parseInt(strArray[2])); //���i0..30�j
			strArray = strArray[3].split(":");
			calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(strArray[0])); //���i0..23�j
			calendar.set(Calendar.MINUTE, Integer.parseInt(strArray[1])); //���i0..59�j
			calendar.set(Calendar.SECOND, Integer.parseInt(strArray[2])); //�b�i0..59�j
		}catch(Exception e){//�����̎��̓t�H�[�}�b�g���Ȃ����Ⴄ�B
			calendar.set(Calendar.YEAR, Integer.parseInt(strArray[3])); //�N
			calendar.set(Calendar.MONTH, my_month.get(strArray[2])); //���i0..11�j
			calendar.set(Calendar.DAY_OF_MONTH,
					Integer.parseInt(strArray[1])); //���i0..30�j
			strArray = strArray[4].split(":");
			calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(strArray[0])); //���i0..23�j
			calendar.set(Calendar.MINUTE, Integer.parseInt(strArray[1])); //���i0..59�j
			calendar.set(Calendar.SECOND, Integer.parseInt(strArray[2])); //�b�i0..59�j
		}
		//holder.TimeText.setText(item.createdTime);
		holder.TimeText.setText(calendar.getTime().toLocaleString());
		
		Calendar calendar_now = Calendar.getInstance( TimeZone.getTimeZone( "UTC" ) );
		long time_ago = calendar_now.getTimeInMillis() - calendar.getTimeInMillis();
		String time_ago_text;
		if(time_ago < 60 * 1000){
			time_ago_text = Integer.toString((int)time_ago/1000) + "s";
		}else if(time_ago < 60 * 60 * 1000){
			time_ago_text = Integer.toString((int)(time_ago/1000/60)) + "m";
		}else if(time_ago < 24 * 60 * 60 * 1000){
			time_ago_text = Integer.toString((int)(time_ago/1000/60/60)) + "h";
		}else{
			time_ago_text = "";
			if(calendar.get(Calendar.YEAR) != calendar_now.get(Calendar.YEAR))
				time_ago_text = time_ago_text + Integer.toString(calendar.get(Calendar.YEAR));
			
			time_ago_text = time_ago_text + " " + Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
			int month = calendar.get(Calendar.MONTH) ;
			time_ago_text = time_ago_text + " " + my_month2[calendar.get(Calendar.MONTH)];
		}
		holder.time_short.setText(time_ago_text);
		
	
		
		//fav�A�C�R���ݒ�
		if(item.faved){
			favButton.setImageResource(android.R.drawable.btn_star_big_on);
		}else{
			favButton.setImageResource(android.R.drawable.btn_star_big_off);
		}	
		
		//�X�e�[�^�X�\���̓f�t�H���g�ł͓���
		holder.status_icon.setVisibility(View.INVISIBLE);
		
		//�����[�U�������献��\��
		if(item.protect){
			bmp = BitmapFactory.decodeResource(res, R.drawable.ic_secure);
			holder.status_icon.setImageBitmap(bmp);
			holder.status_icon.setVisibility(View.VISIBLE);
		}
		
		
		if( MyTweetActivity.MyName.length() > 0 &&
				item.text.contains( MyTweetActivity.MyName.toString() ) &&
				!isReplyActivity){
				holder.TweetText.setTextColor(Color.argb(255, 255, 150, 150));
			}
		
		
		//retweet�{�^���ݒ�
		if(item.retweeted){
			bmp = BitmapFactory.decodeResource(res, R.drawable.retweeted);
			retweetButton.setImageBitmap(bmp);
			
		}else{
			bmp = BitmapFactory.decodeResource(res, R.drawable.no_retweet);
			retweetButton.setImageBitmap(bmp);
		}
		if(item.retweetedByOther){
			holder.TweetText.setTextColor(Color.argb(255, 255, 255, 100));
			bmp = BitmapFactory.decodeResource(res, R.drawable.retweeted);
			holder.status_icon.setImageBitmap(bmp);
			holder.status_icon.setVisibility(View.VISIBLE);
			
			//���c�C�[�g�����l�̏��\��
			ImageView r_icon = (ImageView) convertView.findViewById(R.id.retweeter_icon);
			r_icon.setTag(item.icon_url_retweeter);
			Bitmap bmpTemp = BitmapCache.getImage(item.icon_url_retweeter);
			if(bmpTemp == null){
				IconFetchAsyncTask task = new IconFetchAsyncTask(r_icon, item.icon_url_retweeter, context);
				task.execute();
			}else{
				r_icon.setImageBitmap(bmpTemp);
			}
			TextView retweeter_id = (TextView)convertView.findViewById(R.id.retweeter_name);
			retweeter_id.setText(item.screenName_retweeter + "��"+ item.retweet_count +"�l�����c�C�[�g");
			holder.retweet_layout.setVisibility(View.VISIBLE);
		}else{
			holder.TweetText.setTextColor(Color.WHITE);
			holder.retweet_layout.setVisibility(View.GONE);
		}
		
		if(item.text.startsWith("RT @")){
			holder.TweetText.setTextColor(Color.argb(255, 255, 255, 100));
			bmp = BitmapFactory.decodeResource(res, R.drawable.retweeted);
			holder.status_icon.setImageBitmap(bmp);
			holder.status_icon.setVisibility(View.VISIBLE);
		}
		
		
		// �A�C�R���ݒ�
		holder.icon.setTag(item.icon_url);
        Bitmap bmpTemp = BitmapCache.getImage(item.icon_url);
        if(bmpTemp == null){
            IconFetchAsyncTask task = new IconFetchAsyncTask(holder.icon, item.icon_url, context);
            task.execute();
        }else{
            holder.icon.setImageBitmap(bmpTemp);
        }

		
		holder.icon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String user = item.user_id;
				Dialog dialog;
				if(item.protect){
					dialog = new IdDialog(user, item.id, item.icon_url, null);
				}else{
					dialog = new IdDialog(user, item.id, item.icon_url, item.text);
				}
				dialog.show();
			}
		});
		                		
        
		//�N���b�N���X�i�[�ݒ�
		favButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SingleApi api = new SingleApi();
				if(item.faved){
					favButton.setImageResource(android.R.drawable.btn_star_big_off);//����Ȃ�����
					item.faved = false;	
					String twt_id = item.id;
					api.execute(
							"Fav:"+item.text,
							"http://api.twitter.com/1/favorites/destroy/"+twt_id+".json",
							MyTweetActivity.oauthConsumer,
							context);
				}else{
					favButton.setImageResource(android.R.drawable.btn_star_big_on);//����Ȃ�����
					item.faved = true;
					String twt_id = item.id;
					api.execute(
							"UnFav:"+item.text,
							"http://api.twitter.com/1/favorites/create/"+twt_id+".json",
							MyTweetActivity.oauthConsumer,
							context);
				}	
			}
		});
		
		retweetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(item.retweeted){
					//retweetButton.setImageResource(android.R.drawable.btn_star_big_off);//����Ȃ�����
					/*
					item.retweeted = false;	
					SingleApi api = new SingleApi();
					String twt_id = item.id;
					api.execute(
							"http://api.twitter.com/1/favorites/destroy/"+twt_id+".json",
							MyTweetActivity.oauthConsumer,
							context);
							*/
				}else{
					Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.retweeted);
					retweetButton.setImageBitmap(bmp);
					
					item.retweeted = true;
					SingleApi api = new SingleApi();
					String twt_id = item.id;
					api.execute(
							"Retweet:" + item.text,
							"http://api.twitter.com/1/statuses/retweet/"+twt_id+".json",
							MyTweetActivity.oauthConsumer,
							context);
				}	
			}
		});
		
		
		
		//final View fi_convertView = convertView;
		holder.TweetText.setOnLongClickListener( new View.OnLongClickListener(){
			@Override
			public boolean onLongClick(View v) {
				//tweetClickEvent(fi_convertView);
				return false;
			}
		});
		
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {		
				//tweetClickEvent(fi_convertView);         
			}
		});
		
		return convertView;
		
	}	
	private void tweetClickEvent(final View parent){
		
		Calendar calendar = Calendar.getInstance( TimeZone.getTimeZone( "UTC" ) );
		Date now = calendar.getTime();
		if(popup_now != null){
			if(now.getTime() - popup_now.getTime() < 250)
				return;
		}
		popup_now = now;
			
		LayoutInflater inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View popupView = (View)inflater.inflate(R.layout.single_tweet_popup, null);
	    popupView.setLayoutParams(new ViewGroup.LayoutParams(
	            ViewGroup.LayoutParams.FILL_PARENT,
	            ViewGroup.LayoutParams.WRAP_CONTENT));
	 
	    
	    final PopupWindow popupWindow = new PopupWindow(parent);
	    
	    popupWindow.setContentView(popupView);
	    popupWindow.setWidth((int)(parent.getWidth() *0.9));
	    popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
	    popupWindow.setOutsideTouchable(true);
	    popupWindow.setFocusable(true);
	    popupWindow.setAnimationStyle(R.style.Animation_OverDialog);
	    
	    BitmapDrawable bd = (BitmapDrawable) context.getResources().getDrawable(R.drawable.popup_tweet_back);
	    popupWindow.setBackgroundDrawable(bd);
	    
	    popupWindow.setOnDismissListener(new OnDismissListener(){
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				Calendar calendar = Calendar.getInstance( TimeZone.getTimeZone( "UTC" ) );
				Date now = calendar.getTime();
				popup_now = now;
			}
	    });
	    
		    
	    Handler mHandler = new Handler();
	    mHandler.post(new Runnable() {
	        @Override
	        public void run() {
	            IBinder windowToken = parent.getWindowToken();
	            if (windowToken != null && windowToken.isBinderAlive()) {
	                popupWindow.showAtLocation(parent, Gravity.TOP | Gravity.CENTER, parent.getLeft(), parent.getTop() );
	            }
	        }
	    });
		
	}
}	