package net.d_ichi84.Activity;

import net.d_ichi84.R;
import net.d_ichi84.SingleApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.WindowManager.LayoutParams;

public class TweetActivity extends Activity implements OnClickListener, TextWatcher {
	Button tweetButton;
	EditText textView;
	TextView number_textView;
	String status_id;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//Windowタイトルは要らない
        setContentView(R.layout.tweet); 
        
        findViews();
        //setListeners();     
    }
	
	@Override
	public void onResume(){
		super.onResume();
		Intent intent = getIntent();
		// インテントに保存されたデータを取得
		String data = intent.getStringExtra("to_user");
		if(data != null){
			textView.setText("@" + data +" ");
			textView.setSelection(textView.getText().length());
		}
		this.status_id = intent.getStringExtra("to_id");
	}
	
	@Override
	 public boolean dispatchKeyEvent(KeyEvent event) {
	  if(event.getAction() == KeyEvent.ACTION_DOWN){
	   switch(event.getKeyCode()){
	   case KeyEvent.KEYCODE_BACK:
		   this.finish(); //Backキーで終了する。
		   return true;
	   }
	  }
	  return super.dispatchKeyEvent(event);
	}
	
	private void findViews(){
		tweetButton = (Button)findViewById(R.id.tweetButtoninTweet);
		tweetButton.setOnClickListener(this);
		
		textView = (EditText)findViewById(R.id.TweetEditText1);
		textView.addTextChangedListener(this);
		
		number_textView = (TextView)findViewById(R.id.numberofChar);
	}
	
	
	private void onTweet(){
		
		SingleApi api = new SingleApi();
		if(status_id == null){
			api.execute(
				"投稿:"+textView.getText().toString(),
				"http://api.twitter.com/1/statuses/update.json",
				MyTweetActivity.oauthConsumer,
				this,
				textView.getText().toString());
		}else{
			api.execute(
					"投稿:"+textView.getText().toString(),
					"http://api.twitter.com/1/statuses/update.json?in_reply_to_status_id="+status_id,
					MyTweetActivity.oauthConsumer,
					this,
					textView.getText().toString());			
		}
		Intent intent = new Intent(TweetActivity.this, MyTweetActivity.class);
		startActivity(intent);
    	finish();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.tweetButtoninTweet://ツイートボタンが押されたときの動作
			onTweet();
			break;
		}
	}
	
	@Override
    public void afterTextChanged(Editable s) {
       
    }

	@Override
	public void beforeTextChanged(CharSequence s, int arg1, int arg2,int arg3) {
		 // EditTextの更新内容をTextViewに反映
	}

	@Override
	public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		Integer i = s.toString().length();
		number_textView.setText( String.valueOf(i));
	}
	

}
