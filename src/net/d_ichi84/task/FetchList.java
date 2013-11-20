package net.d_ichi84.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyStore;

import net.d_ichi84.MySSLSocketFactory;
import net.d_ichi84.R;
import net.d_ichi84.userImage;
import net.d_ichi84.Activity.AdapterActivity;
import data.Single_List;
import data.Single_Tweet;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
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
import org.json.JSONArray;
import org.json.JSONObject;

import data.Single_List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

//リストの情報取得
public class FetchList 
extends AsyncTask<Object, String, String >{
	Activity activity;
    ProgressDialog progDialog;	// プログレスダイアログ

	public FetchList(Activity activity){
		super();
		this.activity = activity;
	}
	
	@Override
	protected void onPreExecute(){
		progDialog = new ProgressDialog(activity);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setMessage("リスト取得中");
		progDialog.setCancelable(true);
		progDialog.show();
	}
	
	
	@Override
	protected String doInBackground(Object... params){
		String result = "";
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
        @SuppressWarnings("unused")
		StringBuffer user_name = (StringBuffer)params[1];
        HttpGet request =
        	new HttpGet(
        		(String)"https://api.twitter.com/1.1/lists/list.json");
		
		//100-continueを解除
		HttpParams httpparams = new BasicHttpParams();
        HttpProtocolParams.setUseExpectContinue(httpparams, false);
        request.setParams(httpparams);//GET通信用オブジェクトに設定
		
        try{
        	CommonsHttpOAuthConsumer consumer = (CommonsHttpOAuthConsumer)params[0];
        	// GET通信用オブジェクトに署名
            consumer.sign(request);
        }catch (OAuthMessageSignerException e) {
            Log.e(getClass().getSimpleName(), "OAuthMessageSignerException", e);
        } catch (OAuthExpectationFailedException e) {
            Log.e(getClass().getSimpleName(), "OAuthExpectationFailedException", e);
        } catch (OAuthCommunicationException e) {
            Log.e(getClass().getSimpleName(), "OAuthCommunicationException", e);
        }

	
        // リクエスト送信しタイムライン取得
        try {
        	HttpResponse response = httpclient.execute(request);
        	int statuscode = response.getStatusLine().getStatusCode();
			if (statuscode != HttpStatus.SC_OK) {
				return result;
			}
			
			BufferedReader bufedReader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = bufedReader.readLine()) != null) {
			    total.append(line);
			}
			result = total.toString();
			
			
        }catch(ClientProtocolException e) {
        	Log.e(getClass().getSimpleName(), "Twitter TimeLineRequest Error", e);
        } catch (IOException e) {
        	Log.e(getClass().getSimpleName(), "Twitter TimeLineRequest Error", e);
        } finally {// 通信終了
        	httpclient.getConnectionManager().shutdown();
        }
        
        return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
    protected void onPostExecute(String result) {
		super.onPostExecute(result);
		try{
			JSONArray array = new JSONArray(result);
			for(int i=0; i<array.length();i++){
				Single_List list = new Single_List();

				JSONObject jsonObj = array.getJSONObject(i);
				JSONObject user = jsonObj.getJSONObject("user");
				
				list.id_str = jsonObj.getString("id_str");
				list.ListName = jsonObj.getString("full_name");
				String mode = jsonObj.getString("mode");
				if(mode.compareTo("private")==0){
					list.secret = true;
				}else{
					list.secret = false;
				}
				list.IconURL = user.getString("profile_image_url_https");
				((AdapterActivity<Single_List>)activity).adapter.add(list);
			}
		}catch(Exception e){
			
		}
		progDialog.dismiss();	// プログレスダイアログ終了
    }

}
