package net.d_ichi84;

import java.util.List;

import data.Single_List;
import net.d_ichi84.task.IconFetchAsyncTask;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

//リストのアダプター
public class ListListArrayAdapter extends ArrayAdapter<Single_List>{
	int resourceId;
	Context context;
	
	public ListListArrayAdapter(Context context, int resourceID){
		super(context, resourceID);
		this.resourceId = resourceID;
		this.context = context;
	}
	
	public ListListArrayAdapter(Context context, int resourceID, List<Single_List> list){
		super(context, resourceID, list);
		this.resourceId = resourceID;
		this.context = context;
	}

	@Override
	public View getView(final int pos, View convertView, final ViewGroup parent){
		if(convertView == null){
			LayoutInflater inflater = 
					(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(resourceId, null);
		}
		ImageView user_icon = (ImageView)convertView.findViewById(R.id.list_ownerImg);
		user_icon.setFocusable(false);
		user_icon.setFocusableInTouchMode(false);
		
		ImageView status_icon = (ImageView)convertView.findViewById(R.id.list_status);
		status_icon.setFocusable(false);
		status_icon.setFocusableInTouchMode(false);
		
		TextView ListText = (TextView)convertView.findViewById(R.id.listname_tv);
		ListText.setFocusable(false);
		ListText.setFocusableInTouchMode(false);
		  
		Single_List item = getItem(pos);
		//リスト名
		ListText.setText(item.ListName);
	
		Resources res = context.getResources();
		Bitmap bmp;
		
		//ステータス表示はデフォルトでは透明
		status_icon.setVisibility(View.INVISIBLE);
		
		//鍵ユーザだったら鍵を表示
		if(item.secret){
			bmp = BitmapFactory.decodeResource(res, R.drawable.ic_secure);
			status_icon.setImageBitmap(bmp);
			status_icon.setVisibility(View.VISIBLE);
		}
		
		
		// アイコン設定
		user_icon.setTag(item.IconURL);
		IconFetchAsyncTask task = new IconFetchAsyncTask(user_icon, item.IconURL, context);
		task.execute();
		user_icon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				
			}
		});
		return convertView;
	}	
}	