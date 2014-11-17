package net.d_ichi84.task;


import java.security.KeyStore;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import net.d_ichi84.MySSLSocketFactory;
import net.d_ichi84.R;
import net.d_ichi84.Activity.AdapterActivity;
import net.d_ichi84.Activity.MyTweetActivity;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import data.Single_Tweet;
import net.d_ichi84.Single_Tweet_Comparator;
import android.os.AsyncTask;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

//�^�C�����C���擾�pAsyncTask
public class timeLineAsyncTask 
	extends AsyncTask<Object, String, List<Single_Tweet> > {

	Activity activity; 
	Boolean showDialog;
	Boolean addMode;
    ProgressDialog progDialog;	// �v���O���X�_�C�A���O
    ProgressBar progBar = null;
    ImageView stopImage;

    public timeLineAsyncTask(
    		Activity _activity,
    		Boolean _showDialog,
    		ProgressBar customProgressBar,
    		ImageView customStopIcon){
    	super();
    	activity = _activity;
    	showDialog = _showDialog;
    	progBar =  customProgressBar;
    	stopImage = customStopIcon;
    }
    
	@Override
	protected void onPreExecute(){
		if(progBar != null ){	//���邭���
			progBar.setVisibility(View.VISIBLE);
			stopImage.setVisibility(View.GONE);
		}
		
		if(!showDialog){
			return;
		}
		progDialog = new ProgressDialog(activity);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setMessage(activity.getResources().getText(R.string.progDialogfetTimeline));
		progDialog.setCancelable(true);
		progDialog.show();
		
		//�����̖��O���擾
		if(MyTweetActivity.MyName!=null){
			if(MyTweetActivity.MyName.toString()==""){
				FetchMyName name = new FetchMyName(MyTweetActivity.MyName);
				name.execute(MyTweetActivity.oauthConsumer);
			}
		}
		
	}
	
	@Override
	protected List<Single_Tweet> doInBackground(Object... params) {

		addMode = false;
		if(params.length>3){
			addMode = (Boolean)params[3];
		}
    	HttpClient httpclient = new DefaultHttpClient();
    	HttpContext httpcontext = new BasicHttpContext();
        try{
        	KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        	trustStore.load(null, null);
        	SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
        	sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        	Scheme https = new Scheme("https", sf, 443);
        
        	httpclient.getConnectionManager().getSchemeRegistry().register(https);
        	httpcontext.setAttribute(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        	httpcontext.setAttribute(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
        	httpcontext.setAttribute(CoreProtocolPNames.HTTP_CONTENT_CHARSET, HTTP.UTF_8);
        }catch (Throwable t) {
            Log.e("Main", "testHttpClient", t);
        }
        
		HttpGet request = new HttpGet((String) params[0]);
		//HttpGet request = new HttpGet((String) "https://twitter.com/statuses/friends_timeline.xml");
		
		//100-continue������
		HttpParams httpparams = new BasicHttpParams();
        HttpProtocolParams.setUseExpectContinue(httpparams, false);
        request.setParams(httpparams);//GET�ʐM�p�I�u�W�F�N�g�ɐݒ�
		
        try{
        	// GET�ʐM�p�I�u�W�F�N�g�ɏ���
            CommonsHttpOAuthConsumer consumer =
                (CommonsHttpOAuthConsumer)params[1];
            consumer.sign(request);
        }catch (OAuthMessageSignerException e) {
            Log.e(getClass().getSimpleName(), "OAuthMessageSignerException", e);
        } catch (OAuthExpectationFailedException e) {
            Log.e(getClass().getSimpleName(), "OAuthExpectationFailedException", e);
        } catch (OAuthCommunicationException e) {
            Log.e(getClass().getSimpleName(), "OAuthCommunicationException", e);
        }

        //HttpClient client = new DefaultHttpClient();// �N���C�A���g�I�u�W�F�N�g����
        List<Single_Tweet> result = 
        		new ArrayList<Single_Tweet>();		//�^�C�����C���擾�p���X�g����
        		
        // ���N�G�X�g���M���^�C�����C���擾
        try {
        	result = httpclient.execute(request, new TimeLineResponseHandler());
        }catch(ClientProtocolException e) {
        	Log.e(getClass().getSimpleName(), "Twitter TimeLineRequest Error", e);
        } catch (IOException e) {
        	Log.e(getClass().getSimpleName(), "Twitter TimeLineRequest Error", e);
        } finally {// �ʐM�I��
        	httpclient.getConnectionManager().shutdown();
        }
        
        return result;
	}
   
	@SuppressWarnings("unchecked")
	@Override
    protected void onPostExecute(List<Single_Tweet> result) {
		if(progBar != null ){	//���邭��~�߂�
			progBar.setVisibility(View.GONE);
			stopImage.setVisibility(View.VISIBLE);
		}
		
		ArrayAdapter<Single_Tweet> adapter = ((AdapterActivity<Single_Tweet>)activity).adapter;
        
		// ListView�Ƀ^�C�����C������ݒ�
		for(Integer i=0; i<result.size(); i++){
			Boolean exist = false;
			for(Integer j=0; j<adapter.getCount(); j++){ //���Ƀ��X�g���ɂ��邩�ǂ���
				if( adapter.getItem(j).id.compareTo(result.get(i).id) ==0 ){
					exist = true;
					break;
				}
			}
			
			if(!exist){
				if(addMode){
					adapter.insert(result.get(i),0);
				}else{
					adapter.add(result.get(i));
				}
			}
        }
        
		adapter.sort(new Single_Tweet_Comparator());
        if(showDialog){
        	progDialog.dismiss();	// �v���O���X�_�C�A���O�I��
        }
        super.onPostExecute(result);
    }
}
