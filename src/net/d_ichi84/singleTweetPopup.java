package net.d_ichi84;

import android.view.View;
import android.widget.PopupWindow;

public class singleTweetPopup extends PopupWindow {
	@Override  
	  public void setAnimationStyle(int animationStyle) {  
	    //dismissNow()�p�Ƀ��[�U��`�̃A�j���[�V�����X�^�C����ۑ����Ă���  
	    //userDefinedAnimationStyle = animationStyle;  
	    super.setAnimationStyle(animationStyle);  
	  }  
	  
	  
	  @Override  
	  public void showAtLocation(View parent, int gravity, int x, int y) { 
	    //dismissNow()�ŃA�j���[�V�����𖳌��ɂ����ꍇ�ɍĐݒ肷��  
	    //if (userDefinedAnimationStyle != -1) {  
	    //  super.setAnimationStyle(userDefinedAnimationStyle);  
	    //}  
	    //if (duration > 0) { // duration��-1�Ȃ玩����\�����Ȃ�  
	    //  handler.postDelayed(this, duration);  
	    //}  
	    super.showAtLocation(parent, gravity, x, y);  
	  }  
}
