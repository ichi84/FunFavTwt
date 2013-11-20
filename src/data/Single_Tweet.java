package data;

import java.io.Serializable;


public class Single_Tweet implements Serializable{
	private static final long serialVersionUID = 1L;
	public String	id;			//ツイートID
	public String	icon_url;	//Twitterアイコン
	public String	user_id;	//発言者のID
	public String	text;	//ツイート
	public Boolean	faved;	//favったかどうか
	public Boolean  retweeted;  //retweetしたかどうか
	public Boolean  retweetedByOther;  //retweetしたかどうか
	public String	screenName_retweeter;
	public Long		retweet_count;  //retweetしたかどうか
    public String	createdTime;
    public Boolean 	protect;
    public String	icon_url_retweeter;
    public String	name;
    public String   source;
}
