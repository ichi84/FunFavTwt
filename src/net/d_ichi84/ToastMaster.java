package net.d_ichi84;

import android.content.Context;
import android.widget.Toast;

//�O�̂��c���Ă�������Ă���\������B��̃g�[�X�g
//�g�p��
//ToastMaster.makeText(this, text, ToastMaster.LENGTH_SHORT).show();
public class ToastMaster extends Toast{
	private static Toast sToast = null;

    public ToastMaster(Context context) {
		super(context);
	}
    
    @Override
    public void show() {
    	ToastMaster.setToast(this);
    	super.show();
    }
    public static void setToast(Toast toast) {
        if (sToast != null)
            sToast.cancel();
        sToast = toast;
    }

    public static void cancelToast() {
        if (sToast != null)
            sToast.cancel();
        sToast = null;
    }
}