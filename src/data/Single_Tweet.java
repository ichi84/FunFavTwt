package data;

import java.io.Serializable;


public class Single_Tweet implements Serializable{
	private static final long serialVersionUID = 1L;
	public String	id;			//�c�C�[�gID
	public String	icon_url;	//Twitter�A�C�R��
	public String	user_id;	//�����҂�ID
	public String	text;	//�c�C�[�g
	public Boolean	faved;	//fav�������ǂ���
	public Boolean  retweeted;  //retweet�������ǂ���
	public Boolean  retweetedByOther;  //retweet�������ǂ���
	public String	screenName_retweeter;
	public Long		retweet_count;  //retweet�������ǂ���
    public String	createdTime;
    public Boolean 	protect;
    public String	icon_url_retweeter;
    public String	name;
    public String   source;
}
