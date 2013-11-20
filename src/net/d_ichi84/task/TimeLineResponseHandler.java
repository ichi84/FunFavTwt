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

public class TimeLineResponseHandler 
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
        JSONArray EntityArray = null;
        try{
        	JSONObject jsonItem = new JSONObject(jsonStr);
        	EntityArray = jsonItem.getJSONArray("statuses");
        }catch(Exception e){}
        try {
            // JSONデータからJSONTokener取得
        	if(EntityArray == null)
        		EntityArray = new JSONArray(jsonStr);

            // JSONオブジェクト1件ずつ処理
            for (int i = 0; i < EntityArray.length(); i++) {
                // JSONオブジェクト取得
                JSONObject jsonItem = EntityArray.getJSONObject(i);
                String str = jsonItem.toString();
                // "user"要素のJSONオブジェクト取得
                JSONObject user;
                
                try{
                	user = jsonItem.getJSONObject("user");
                }catch(Exception e){
                	try{
                		user = jsonItem.getJSONObject("sender");//replyのとき
                	}catch(Exception ee){
                		user = null;//検索の時
                	}
                }
                
                //ツイートID取得
                Long id = jsonItem.getLong("id");
                
                // つぶやき本文取得
                String text = jsonItem.getString("text");
                // ユーザ情報取得
                String screenName;
                String name;
                String profileImageUrlHttps;
                Boolean protect = false;
                String screenNameRetweeter = "";
                String iconUrlRetweeter = "";
                
                String source = "";
                try{
                	source = jsonItem.getString("source");;
                	source = source.replaceAll("\\&gt;", ">");
                	source = source.replaceAll("\\&lt;", "<");
                	int start = source.indexOf(">");
                	if(start<0) start =0;
                	int end;
                	if(start<0){
                		start = 0;
                		end = source.length();
                	}else{
                		end = source.indexOf("<", start);
                		if(end<0)
                			end = source.length();
                		start++;
                	}
                	source = source.substring(start, end);
                }catch(Exception e){}
                
                if(user !=null){
                	screenName = user.getString("screen_name");
                	name = user.getString("name");
                	// アイコンURL取得
                	profileImageUrlHttps =
                			user.getString("profile_image_url_https");
                	protect = user.getBoolean("protected");
                }else{//検索の時はこっちに来る
                	screenName = jsonItem.getString("from_user");
                	name = jsonItem.getString("from_user_name");
                	profileImageUrlHttps =
                			jsonItem.getString("profile_image_url_https");
                }
                
                if(jsonItem.has("retweeted_status")){//リツイート
                	screenNameRetweeter = screenName;
                	iconUrlRetweeter = profileImageUrlHttps;
                	
                	JSONObject rt = jsonItem.getJSONObject("retweeted_status");
                	JSONObject rt_user = rt.getJSONObject("user");
                	screenName = rt_user.getString("screen_name");
                	name = rt_user.getString("name");
                	profileImageUrlHttps = rt_user.getString("profile_image_url_https");
                	text = rt.getString("text");
                	
                }
                
                
                //Favられてるか
                Boolean faved = false; 
				Boolean retweetedByOther =false;
				long retweet_count = 0;
				try{
					faved = jsonItem.getBoolean("favorited"); 
					retweetedByOther =jsonItem.getBoolean("retweeted");;
					retweet_count = jsonItem.getLong("retweet_count");;
				}catch(Exception e){
					
				}
                String createdTime = jsonItem.getString("created_at");
                
                                
                // つぶやき情報保持用オブジェクト生成
                Single_Tweet entity =  new Single_Tweet();
                entity.id = Long.toString(id);
                
                entity.user_id = screenName;
                entity.name = name;
                
                entity.text = text;
                entity.icon_url = profileImageUrlHttps;
                entity.createdTime = createdTime;
                
                entity.faved = faved;
                
                entity.retweeted = false;
                entity.protect = protect;
                
                if(jsonItem.has("retweeted_status"))
                	entity.retweetedByOther = true;
                else
                	entity.retweetedByOther = false;
                
                entity.retweet_count = retweet_count;
                
                
                entity.icon_url_retweeter = iconUrlRetweeter;
                entity.screenName_retweeter = screenNameRetweeter;
                
                entity.source = source;
                // タイムライン取得用リストに追加
                list.add(entity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (StringIndexOutOfBoundsException e){
        }

        return list;
	}

}
