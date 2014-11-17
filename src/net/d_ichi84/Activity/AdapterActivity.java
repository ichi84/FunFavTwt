package net.d_ichi84.Activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.d_ichi84.R;
import net.d_ichi84.task.FetchList;
import net.d_ichi84.task.timeLineAsyncTask;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import data.Constants;
import android.widget.ProgressBar;

//���X�g�ƃ��X�g�A�_�v�^�[�������̃A�v���̊�{Activity�B�p�����Ďg��
public class AdapterActivity<T> extends Activity implements OnClickListener{
	public ArrayAdapter<T> adapter =null;
	protected List<T> TweetArray 				//�^�C�����C���擾�p���X�g����
	  			= new ArrayList<T>();		
	protected ListView listView;				//�^�C�����C����ێ����Ă��郊�X�g
	public static CommonsHttpOAuthConsumer oauthConsumer //OAuth�F�؃R���V���[�}
		= new CommonsHttpOAuthConsumer(	Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
	//protected List<T> TempListData;				//��ʑJ�ڎ��̕ۑ��p
    public static Context context = null;		//�_�C�A���O�\���p�B��ԍŌ�ɕ\�����ꂽActivity�������Ă�͂��B������������邯��
	View	mFooter = null;						//���X�g�̃t�b�^�[
	String	BaseURL = "";						//�^�C�����C���擾�̃x�[�XURL
	String lastid = "0";
	
    @Override 
    protected void onCreate(Bundle savedInstanceState){
    	//setTheme(android.R.style.Theme_Light);
    	
    	super.onCreate(savedInstanceState);
        setOAuthFromPreference();
    }
	@Override 
	protected void onResume(){
		super.onResume();
		context = this;
		if(mFooter != null)
			mFooter.setOnClickListener(this);
		
	}

	public void getTimeline(boolean kurukuru){
	}
	
	public void getTimeline(int sleep, boolean kurukuru){
		try{
			Thread.sleep(sleep);
		}catch(Exception e){
			return;
		}
		getTimeline(kurukuru);
	}
	
	//��ʉ�]�Ή��B���X�g�r���[�̃��X�g��ۑ�����
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (TweetArray != null) {
			outState.putSerializable("ListDataKey", (Serializable)TweetArray);
		}
	}
	
	
	public void setAdapters(ArrayAdapter<T> _adapter){			
		adapter = _adapter;
		listView.setAdapter(adapter);
	}
		
	//Preference����OAuth�g�[�N�����擾���ăZ�b�g����
	protected void setOAuthFromPreference(){
		 SharedPreferences pref = getSharedPreferences("funfuvtwt_token",MODE_PRIVATE);
	        String token = pref.getString("token","");
	        String tokenSecret = pref.getString("tokenSecret", "");
	        
	        if(token.length()>0 && tokenSecret.length()>0){
	        	oauthConsumer.setTokenWithSecret(token, tokenSecret);
	        }
	}
	
	//��ʉ�]�Ή��B�o���h������f�[�^�����[�h����
	@SuppressWarnings("unchecked")
	protected void LoadBundle(Bundle savedInstanceState){
        //Bundle�̃`�F�b�N
        if (savedInstanceState != null) {
        	TweetArray = (List<T>)savedInstanceState.getSerializable("ListDataKey");
        }else{
        	TweetArray = new ArrayList<T>(); 
        }
	}
	
	//�ŏ��Ƀ^�C�����C����\�����鏈��
	protected void LoadFiestTimeline(){
		Intent intent = getIntent();
		if(intent == null){
			return;
		}
		Uri uri = intent.getData();
		// �C���e���g���擾�ł��� = �ʉ�ʂ���Ƃ�ł�����^�C�����C���X�V
		if(uri == null){
			// �^�C�����C���擾
			timeLineAsyncTask task = new timeLineAsyncTask(this, false
					, (ProgressBar)mFooter.findViewById(R.id.progressbar1)
					, (ImageView)mFooter.findViewById(R.id.imageMore));		
		    task.execute(BaseURL, oauthConsumer,false);
		    getIntent().setData( Uri.EMPTY );
		}
	}
	
	protected  String getLastStatusID(){
		return "0";		//�X�^�u
	}
	
	protected void LoadMoreTimeline(){
		String param;
		if(BaseURL.contains("?")){
			param = "&max_id=";
		}else
			param = "?max_id=";
		
    	//�^�C�����C���擾
		if(adapter.getCount()==0){
			return;
		}
		String statusid = getLastStatusID();
		if(lastid.compareTo(statusid)!=0){
			lastid = statusid;
			String maxid = Long.toString( Long.valueOf(lastid) -1 );
			timeLineAsyncTask task = new timeLineAsyncTask(this, false
					, (ProgressBar)mFooter.findViewById(R.id.progressbar1)
					, (ImageView)mFooter.findViewById(R.id.imageMore));			
			
			task.execute(BaseURL + param + maxid, oauthConsumer, false);
			
		}
	}
	
	private void LoadMoreTimelineForce(){
		String param;
		if(BaseURL.contains("?")){
			param = "&max_id=";
		}else
			param = "?max_id=";
		
    	//�^�C�����C���擾
		if(adapter.getCount()==0) return;
		
		String statusid = getLastStatusID();		
		lastid = statusid;
		String maxid = Long.toString( Long.valueOf(lastid) -1 );
		timeLineAsyncTask task = new timeLineAsyncTask(this, false
					, (ProgressBar)mFooter.findViewById(R.id.progressbar1)
					, (ImageView)mFooter.findViewById(R.id.imageMore));			
			
		task.execute(BaseURL + param + maxid, oauthConsumer, false);
	}
	
	//�X�V�{�^���������ꂽ�Ƃ��̏���
	protected void refresh(){
		adapter.clear();
		lastid = "0";
		timeLineAsyncTask task = new timeLineAsyncTask(this, false
				, (ProgressBar)mFooter.findViewById(R.id.progressbar1)
				, (ImageView)mFooter.findViewById(R.id.imageMore));		
        task.execute(BaseURL, oauthConsumer, false);
	}
	
	//���X�g�̃t�b�^�[�����\�[�X����擾
    protected View getFooter() {
        if (mFooter == null) {
            mFooter = getLayoutInflater().inflate(R.layout.listview_footer, null);
        }
        return mFooter;
    }
	@Override
	public void onClick(View v) {
		LoadMoreTimelineForce();
	}

	
}
