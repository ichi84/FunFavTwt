package net.d_ichi84.dialog;

import data.Single_Tweet;
import net.d_ichi84.R;
import net.d_ichi84.SingleApi;
import net.d_ichi84.Activity.AdapterActivity;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class postDialog extends MyDialog implements OnClickListener, TextWatcher{

	Button idPost;
	EditText edit;
	TextView count_text;
	Context context;
	String status_id;
	String text;
	Boolean DMMode;
	String id;
	AdapterActivity activity;
	
	public postDialog( String id, String status_id, String text, Boolean DMMode , AdapterActivity<Single_Tweet> activity){
		super(AdapterActivity.context,R.style.Theme_CustomDialog);
		this.status_id = status_id;
		this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		setContentView(R.layout.postdialog);
		this.context = AdapterActivity.context;
		this.text = text;
		this.DMMode = DMMode;
		this.id = id;
		findViews();
		this.activity = activity;
		if(status_id != null){
			String s;
			if(text==null){
				s = "@" + id +" ";
				edit.setText(s);
				edit.setSelection(edit.getText().length());
			}else{
				s = " RT @" + id +": "+text;
				edit.setText(s);
				edit.setSelection(0);
			}		
		}
	}
	
	private void findViews(){
		//文字数表示用
		count_text = (TextView)findViewById(R.id.count_text);
		
		//投稿エディットボックス
		edit = (EditText)findViewById(R.id.post_editText1);
		edit.addTextChangedListener(this);
		
		//投稿ボタン 
		idPost = (Button)findViewById(R.id.post_button1);
		idPost.setOnClickListener(this);
	}

	@Override
	public void afterTextChanged(Editable arg0) {
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
		Integer i = s.toString().length();
		i = 140 - i;
		String str;
		if(i<0){
			count_text.setTextColor(Color.RED);
			str = new String(-i + "字オーバー");
		}else{
			count_text.setTextColor(Color.WHITE);
			str = new String("残り" + i + "字");
		}
		count_text.setText( str);
	}

	@Override
	public void onClick(View v) {
		if( edit.getText().length() > 140){
			Toast.makeText(context, "文字数オーバーです", Toast.LENGTH_SHORT).show();
			return; //140字以上だったらtoast表示して何もしない
		}
		SingleApi api = new SingleApi();
		
		if(DMMode){
			api.execute(
					"DM:" + edit.getText().toString(),
					"https://api.twitter.com/1.1/direct_messages/new.json",
					oauthConsumer,
					this.context,
					"screen_name=" +id+"&text=" +edit.getText().toString(),
					0,
					activity);
		}else{			
			if(status_id == null){
				api.execute(
						"投稿:" + edit.getText().toString(),
						"https://api.twitter.com/1.1/statuses/update.json",
						oauthConsumer,
						this.context,
						"status=" +edit.getText().toString(),
						0,
						activity);
			}else{
				api.execute(
						"投稿:" + edit.getText().toString(),
						"https://api.twitter.com/1.1/statuses/update.json?in_reply_to_status_id="+status_id,
						oauthConsumer,
						this.context,
						"status=" +edit.getText().toString(),
						0,
						activity);
			}
		}
		dismiss();
	}
	
}
