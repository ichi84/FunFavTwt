package net.d_ichi84;

import java.util.HashMap;

import android.graphics.Bitmap;

public class BitmapCache {
	private static HashMap<String,Bitmap> cache = new HashMap<String,Bitmap>();
	
    //�L���b�V�����摜�f�[�^���擾
    public static Bitmap getImage(String key) {
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        //���݂��Ȃ��ꍇ��NULL��Ԃ�
        return null;
    }
 
    //�L���b�V���ɉ摜�f�[�^��ݒ�
    public static void setImage(String key, Bitmap image) {
        cache.put(key, image);
    }
 
    //�L���b�V���̏������i���X�g�I���I�����ɌĂяo���A�L���b�V���Ŏg�p���Ă������������������j
    public static void clearCache(){
        cache = null;
        cache = new HashMap<String,Bitmap>();
    }
}
