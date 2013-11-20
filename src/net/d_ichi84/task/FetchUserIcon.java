package net.d_ichi84.task;

import java.io.IOException;
import java.security.KeyStore;

import net.d_ichi84.MySSLSocketFactory;
import net.d_ichi84.userImage;
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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class FetchUserIcon 
extends AsyncTask<Object, String, Bitmap >{
	userImage activity;
	
    public FetchUserIcon(userImage context){
    	super();
    	activity = context;
    }
    
	@Override
	protected Bitmap doInBackground(Object... params){
	//(String url, CommonsHttpOAuthConsumer consumer)
		Bitmap result = null;
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
        
        String url = (String)params[0];
		HttpGet request = new HttpGet(url);
		//HttpGet request = new HttpGet((String) "https://twitter.com/statuses/friends_timeline.xml");
		
		//100-continueを解除
		HttpParams httpparams = new BasicHttpParams();
        HttpProtocolParams.setUseExpectContinue(httpparams, false);
        request.setParams(httpparams);//GET通信用オブジェクトに設定
		
        try{
        	CommonsHttpOAuthConsumer consumer = (CommonsHttpOAuthConsumer)params[1];
        	// GET通信用オブジェクトに署名
            consumer.sign(request);
        }catch (OAuthMessageSignerException e) {
            Log.e(getClass().getSimpleName(), "OAuthMessageSignerException", e);
        } catch (OAuthExpectationFailedException e) {
            Log.e(getClass().getSimpleName(), "OAuthExpectationFailedException", e);
        } catch (OAuthCommunicationException e) {
            Log.e(getClass().getSimpleName(), "OAuthCommunicationException", e);
        }

        //HttpClient client = new DefaultHttpClient();// クライアントオブジェクト生成
        		
        // リクエスト送信しタイムライン取得
        try {
        	HttpResponse response = httpclient.execute(request);
        	int statuscode = response.getStatusLine().getStatusCode();
			if (statuscode != HttpStatus.SC_OK) {
				return result;
			}
			
//			BufferedReader bufedReader = new BufferedReader(new InputStreamReader(response
//					.getEntity().getContent()));
			result = BitmapFactory.decodeStream(response
					.getEntity().getContent());
			
			
        }catch(ClientProtocolException e) {
        	Log.e(getClass().getSimpleName(), "Twitter TimeLineRequest Error", e);
        } catch (IOException e) {
        	Log.e(getClass().getSimpleName(), "Twitter TimeLineRequest Error", e);
        } finally {// 通信終了
        	httpclient.getConnectionManager().shutdown();
        }
        
        return result;
	}
	
	@Override
    protected void onPostExecute(Bitmap result) {
		ImageView iv = activity.getuserImage();
		iv.setImageBitmap(result);
		iv.invalidate();
		
        super.onPostExecute(result);
    }

}
