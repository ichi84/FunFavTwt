package net.d_ichi84;

import android.view.View;
import android.widget.PopupWindow;

public class singleTweetPopup extends PopupWindow {
	@Override  
	  public void setAnimationStyle(int animationStyle) {  
	    //dismissNow()用にユーザ定義のアニメーションスタイルを保存しておく  
	    //userDefinedAnimationStyle = animationStyle;  
	    super.setAnimationStyle(animationStyle);  
	  }  
	  
	  
	  @Override  
	  public void showAtLocation(View parent, int gravity, int x, int y) { 
	    //dismissNow()でアニメーションを無効にした場合に再設定する  
	    //if (userDefinedAnimationStyle != -1) {  
	    //  super.setAnimationStyle(userDefinedAnimationStyle);  
	    //}  
	    //if (duration > 0) { // durationが-1なら自動非表示しない  
	    //  handler.postDelayed(this, duration);  
	    //}  
	    super.showAtLocation(parent, gravity, x, y);  
	  }  
}
