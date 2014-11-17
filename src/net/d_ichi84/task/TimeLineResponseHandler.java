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
		
		// �X�e�[�^�X�R�[�h�擾
        int statuscode = response.getStatusLine().getStatusCode();

        // �^�C�����C���擾�p���X�g
        List<Single_Tweet> list = new ArrayList<Single_Tweet>();

        // �X�e�[�^�X������łȂ��ꍇ
        if (statuscode != HttpStatus.SC_OK) {
            Log.e(getClass().getSimpleName(), "HTTP StatusCode Error");
            return list;
        }
		
        // ���X�|���X�I�u�W�F�N�g����JSON�f�[�^�擾
        String jsonStr = EntityUtils.toString(response.getEntity());
        JSONArray EntityArray = null;
        try{
        	JSONObject jsonItem = new JSONObject(jsonStr);
        	EntityArray = jsonItem.getJSONArray("statuses");
        }catch(Exception e){}
        try {
            // JSON�f�[�^����JSONTokener�擾
        	if(EntityArray == null)
        		EntityArray = new JSONArray(jsonStr);

            // JSON�I�u�W�F�N�g1��������
            for (int i = 0; i < EntityArray.length(); i++) {
                // JSON�I�u�W�F�N�g�擾
                JSONObject jsonItem = EntityArray.getJSONObject(i);
                String str = jsonItem.toString();
                // "user"�v�f��JSON�I�u�W�F�N�g�擾
                JSONObject user;
                
                try{
                	user = jsonItem.getJSONObject("user");
                }catch(Exception e){
                	try{
                		user = jsonItem.getJSONObject("sender");//reply�̂Ƃ�
                	}catch(Exception ee){
                		user = null;//�����̎�
                	}
                }
                
                //�c�C�[�gID�擾
                Long id = jsonItem.getLong("id");
                
                // �Ԃ₫�{���擾
                String text = jsonItem.getString("text");
                // ���[�U���擾
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
                	// �A�C�R��URL�擾
                	profileImageUrlHttps =
                			user.getString("profile_image_url_https");
                	protect = user.getBoolean("protected");
                }else{//�����̎��͂������ɗ���
                	screenName = jsonItem.getString("from_user");
                	name = jsonItem.getString("from_user_name");
                	profileImageUrlHttps =
                			jsonItem.getString("profile_image_url_https");
                }
                
                if(jsonItem.has("retweeted_status")){//���c�C�[�g
                	screenNameRetweeter = screenName;
                	iconUrlRetweeter = profileImageUrlHttps;
                	
                	JSONObject rt = jsonItem.getJSONObject("retweeted_status");
                	JSONObject rt_user = rt.getJSONObject("user");
                	screenName = rt_user.getString("screen_name");
                	name = rt_user.getString("name");
                	profileImageUrlHttps = rt_user.getString("profile_image_url_https");
                	text = rt.getString("text");
                	
                }
                
                
                //Fav���Ă邩
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
                
                                
                // �Ԃ₫���ێ��p�I�u�W�F�N�g����
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
                // �^�C�����C���擾�p���X�g�ɒǉ�
                list.add(entity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (StringIndexOutOfBoundsException e){
        }

        return list;
	}

}
