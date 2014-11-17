package net.d_ichi84;
import net.d_ichi84.Activity.AdapterActivity;
import net.d_ichi84.Activity.MyTweetActivity;
import net.d_ichi84.Activity.SettingActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import data.Single_Tweet;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class SingleApi 
	extends AsyncTask<Object, String, Boolean > { 
	String ErrMsg = "";	//0
	String url;			//1
	CommonsHttpOAuthConsumer consumer; //2
	Context context;	//3
	String data =null;	//4
	Integer sltime;		//5
	AdapterActivity<Single_Tweet> activity; //6
	
	Integer ParamLen;
	
	@Override
	protected void onPreExecute(){
	}
	
	@Override
	protected Boolean doInBackground(Object... params) {
		ParamLen = params.length;
		url = (String) params[1];
		HttpPost request = new HttpPost(url);
		ErrMsg = (String)params[0];
		
		
		if(params.length >= 5){
			data = (String)params[4];
			String[] str1Array = data.split("&");//&�ł΂炷
			List<NameValuePair> httpparams=new ArrayList<NameValuePair>();
			for(int i =0; i<str1Array.length; i++){
				String[] str2Array = str1Array[i].split("=");//=�ł΂炷
				httpparams.add(new BasicNameValuePair(str2Array[0],str2Array[1])); 
			}
			try{
				request.setEntity(new UrlEncodedFormEntity(httpparams, HTTP.UTF_8));
			}catch(Exception e){
				return null;
			}
		}
		if(params.length >= 6){
			sltime = (Integer)params[5];
			if(sltime>0)
				SystemClock.sleep(sltime);
		}
		if(params.length >=7){
			activity = (AdapterActivity<Single_Tweet>)params[6];
		}
		
		//100-continue������
		HttpParams httpparams = new BasicHttpParams();
        HttpProtocolParams.setUseExpectContinue(httpparams, false);
        context = (Context)params[3];
        httpparams.setParameter("http.useragent", context.getResources().getText(R.string.UA));
        
        request.setParams(httpparams);//POST�ʐM�p�I�u�W�F�N�g�ɐݒ�
		
        try{
        	
        	// POST�ʐM�p�I�u�W�F�N�g�ɏ���
            consumer =
                (CommonsHttpOAuthConsumer)params[2];
            consumer.sign(request);
        }catch (OAuthMessageSignerException e) {
            Log.e(getClass().getSimpleName(), "OAuthMessageSignerException", e);
        } catch (OAuthExpectationFailedException e) {
            Log.e(getClass().getSimpleName(), "OAuthExpectationFailedException", e);
        } catch (OAuthCommunicationException e) {
            Log.e(getClass().getSimpleName(), "OAuthCommunicationException", e);
        }
        
        HttpClient client = new DefaultHttpClient();// �N���C�A���g�I�u�W�F�N�g����
        		
        // ���N�G�X�g���M���^�C�����C���擾
        Boolean result = false;
        try {
        	result = client.execute(request, new HttpResponseHandler());
        }catch(ClientProtocolException e) {
        	Log.e(getClass().getSimpleName(), "Twitter TimeLineRequest Error", e);
        } catch (IOException e) {
        	Log.e(getClass().getSimpleName(), "Twitter TimeLineRequest Error", e);
        } finally {// �ʐM�I��
        	client.getConnectionManager().shutdown();
        }
		
		return result;
	}

	private class HttpResponseHandler implements ResponseHandler<Boolean>{
		@Override
		public Boolean handleResponse(HttpResponse response)
				throws ClientProtocolException, IOException {
			
			//�X�e�[�^�X�R�[�h�擾
	        int statuscode = response.getStatusLine().getStatusCode();
	        String jsonStr = EntityUtils.toString(response.getEntity());
	        // �X�e�[�^�X������łȂ��ꍇ
	        if (statuscode != HttpStatus.SC_OK) {
	            Log.e(getClass().getSimpleName(), "HTTP StatusCode Error");
	            return false;
	        }
			
			return true;
		}
	}
	
	@Override
    protected void onPostExecute(Boolean result) {
		if(context == null) return;
		if(result == false){
			
			AlertDialog.Builder ab = new AlertDialog.Builder(context);
			AlertDialog ad = ab.create();
			ad.setTitle("�ʐM���s");
			ad.setMessage(ErrMsg);
			
			ad.setButton("�Ď��s", new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int which) {
			    	SingleApi api = new SingleApi();
			    	if(ParamLen>=6){
			    		api.execute(ErrMsg, url, consumer, context, data, sltime);
			    		return;
			    		}
			    	if(ParamLen>=5){
			    		api.execute(ErrMsg, url, consumer, context, data);
			    		return;
			    	}
			    	api.execute(ErrMsg, url, consumer, context);
			    }
			});
			ad.setButton2("�L�����Z��", new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int which) {
			    }
			});
			ad.setCancelable(true);
			ad.show();
		}
		
		if(activity != null){
			activity.getTimeline(1000, true);
		}
	}


}
