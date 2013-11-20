package net.d_ichi84.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import data.Single_Tweet;

import android.util.Log;

public class ListResponseHandler 
	implements ResponseHandler<List<Single_Tweet>> {

	@Override
	public List<Single_Tweet> handleResponse(HttpResponse response)
			throws ClientProtocolException, IOException {
		
		// ステータスコード取得
        int statuscode = response.getStatusLine().getStatusCode();

        // タイムライン取得用リスト
        List<Single_Tweet> list = new ArrayList<Single_Tweet>();

        // ステータスが正常でない場合
        if (statuscode != HttpStatus.SC_OK) {
            Log.e(getClass().getSimpleName(), "HTTP StatusCode Error");
            return list;
        }
		
        // レスポンスオブジェクトからJSONデータ取得
        String jsonStr = EntityUtils.toString(response.getEntity());
        try {
            // JSONデータからJSONTokener取得
            //JSONTokener jsonObj = new JSONTokener(jsonStr);
            // JSONTokenerからJSONArray取得
            JSONObject jsonItem = new JSONObject(jsonStr);
            JSONObject user= jsonItem.getJSONObject("user");
            
  

                //ツイートID取得
                Long id = jsonItem.getLong("id");
                
                // つぶやき本文取得
                String text = jsonItem.getString("text");
                // ユーザ名取得
                String screenName = user.getString("screen_name");

                // アイコンURL取得
                String profileImageUrlHttps =
                    user.getString("profile_image_url_https");
                
                //Favられてるか
                Boolean faved = jsonItem.getBoolean("favorited"); 
                String createdTime = jsonItem.getString("created_at");
                Boolean protect = user.getBoolean("protected");
                
				Boolean retweetedByOther =jsonItem.getBoolean("retweeted");;
				long retweet_count = jsonItem.getLong("retweet_count");;
                                
                // つぶやき情報保持用オブジェクト生成
                Single_Tweet entity =  new Single_Tweet();
                          
                entity.id = Long.toString(id);
                entity.user_id = screenName;
                entity.text = text;
                entity.icon_url = profileImageUrlHttps;
                entity.createdTime = createdTime;
                entity.faved = faved;
                entity.retweetedByOther = retweetedByOther;
                entity.retweet_count = retweet_count;
                entity.retweeted = false;
                entity.protect = protect;
                entity.retweetedByOther = false;
                
                // タイムライン取得用リストに追加
                list.add(entity);
            
        } catch (JSONException e) {
            Log.e(getClass().getSimpleName(), "JSON Error", e);
        }

        return list;
	}

}
