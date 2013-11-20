package net.d_ichi84.dialog;
import net.d_ichi84.SingleApi;
import net.d_ichi84.userImage;
import net.d_ichi84.R;
import net.d_ichi84.Activity.AdapterActivity;
import net.d_ichi84.Activity.IdPageActivity;
import net.d_ichi84.task.FetchUserIcon;
import net.d_ichi84.task.IconFetchAsyncTask;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.*;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class IdDialog  extends MyDialog implements userImage{

	ImageView userImage;
	Button idStatus;
	
	String targetId;
	String status_id;
	Context context;
	String icon_url;
	String text;
	
	public IdDialog( String id, String status_id,  String icon_url, String text){
		super(AdapterActivity.context,R.style.Theme_CustomDialog );		
		setContentView(R.layout.id_clickdialog);
		this.context = AdapterActivity.context;
		targetId = id;
		this.status_id = status_id;
		this.icon_url = icon_url;
		this.text = text;
		
		userImage = null;
		findViews();
	}
	

	private void findViews(){
		//IDページボタン
		( (Button)findViewById(R.id.status_btn) ).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(context, IdPageActivity.class);
						intent.putExtra("to_user", targetId);
						context.startActivity(intent);
						dismiss();
					}
				});
				
		//返信するボタン
		Button idReply = (Button)findViewById(R.id.reply_btn);
		idReply.setText("> " + "返信する");
		idReply.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {		
						Dialog dialog = new postDialog(targetId, status_id, null,false,null);
						dialog.show();
						dismiss();
					}
				});

		//RT
		( (Button)findViewById(R.id.rt_button) ).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {		
						SingleApi api = new SingleApi();
						api.execute(
								"Retweet",
								"http://api.twitter.com/1/statuses/retweet/"+status_id+".json",
								oauthConsumer, context);
						dismiss();
					}
				});

		
		//非公式RT
		( (Button)findViewById(R.id.qt_button) ).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {		
						Dialog dialog = new postDialog(targetId, status_id, text,false,null);
						dialog.show();
						dismiss();
					}
				});

		//DM
		( (Button)findViewById(R.id.DM_button) ).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {		
						Dialog dialog = new postDialog(targetId, null, null, true,null);
						dialog.show();
						dismiss();
					}
				});
		
		//フォローする
		( (Button)findViewById(R.id.follow_button) ).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {		
						SingleApi api = new SingleApi();
						api.execute(
								"フォロー",
								"https://api.twitter.com/1.1/friendships/create.json?screen_name="+targetId,
								oauthConsumer, context);
						dismiss();
					}
				});
				
		//フォロー解除
		( (Button)findViewById(R.id.unfollow_button) ).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {		
						AlertDialog.Builder ab = new AlertDialog.Builder(context);
						AlertDialog ad = ab.create();
						ad.setTitle("フォロー解除");
						ad.setMessage("本当に解除しますか？");
						
						ad.setButton("OK", new DialogInterface.OnClickListener() {
						    public void onClick(DialogInterface dialog, int which) {
						    	SingleApi api = new SingleApi();
								api.execute(
										"フォロー解除",
										"https://api.twitter.com/1.1/friendships/destroy.json?screen_name="+targetId,
										oauthConsumer, context);
						    }
						});
						ad.setCancelable(true);
						ad.show();
						dismiss();
					}
				});
		
		//ユーザアイコンのセット。
		userImage = (ImageView)findViewById(R.id.userIconView2);
		if(icon_url != null){
			ImageButton icon = (ImageButton)findViewById(R.id.userIconView2);
			icon.setTag(icon_url);
			IconFetchAsyncTask task = new IconFetchAsyncTask(icon, icon_url, context);
			task.execute();
		}else{
			FetchUserIcon task = new FetchUserIcon(this);
			task.execute("https://api.twitter.com/1.1/users/profile_image?screen_name=" +targetId +"&size=normal", oauthConsumer);
		}
		TextView text = (TextView)findViewById(R.id.id_textView1);
		text.setText(targetId);
	}

	@Override
	public ImageView getuserImage() {
		return userImage;
	}
}
