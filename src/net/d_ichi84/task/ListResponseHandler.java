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
        try {
            // JSON�f�[�^����JSONTokener�擾
            //JSONTokener jsonObj = new JSONTokener(jsonStr);
            // JSONTokener����JSONArray�擾
            JSONObject jsonItem = new JSONObject(jsonStr);
            JSONObject user= jsonItem.getJSONObject("user");
            
  

                //�c�C�[�gID�擾
                Long id = jsonItem.getLong("id");
                
                // �Ԃ₫�{���擾
                String text = jsonItem.getString("text");
                // ���[�U���擾
                String screenName = user.getString("screen_name");

                // �A�C�R��URL�擾
                String profileImageUrlHttps =
                    user.getString("profile_image_url_https");
                
                //Fav���Ă邩
                Boolean faved = jsonItem.getBoolean("favorited"); 
                String createdTime = jsonItem.getString("created_at");
                Boolean protect = user.getBoolean("protected");
                
				Boolean retweetedByOther =jsonItem.getBoolean("retweeted");;
				long retweet_count = jsonItem.getLong("retweet_count");;
                                
                // �Ԃ₫���ێ��p�I�u�W�F�N�g����
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
                
                // �^�C�����C���擾�p���X�g�ɒǉ�
                list.add(entity);
            
        } catch (JSONException e) {
            Log.e(getClass().getSimpleName(), "JSON Error", e);
        }

        return list;
	}

}
