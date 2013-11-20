package data;

public class Constants {
	
	//request token取得用url
	public static final String REQUEST_TOKEN_ENDPOINT_URL 
		= "https://api.twitter.com/oauth/request_token";
	       
	
	//Access token取得用url
	public static final String ACCESS_TOKEN_ENDPOINT_URL 
	= "https://api.twitter.com/oauth/access_token";

	//Oauth認証用url
	public static final String OAUTH_URL 
	= "https://api.twitter.com/oauth/authorize";

	//コールバック用url
	public static final String CALBACK_URL 
	= "app://FunFav";
	//= "http://ichi84.blogspot.com/";

	//コンシューマキー
	public static final String CONSUMER_KEY
	= "P80lTx8An2Lw8O3XouPfQ";

	//コンシューマシークレットキー
	public static final String CONSUMER_SECRET
	= "dSwJJyntZYErB2bNiS9CPEotFxyg9jHeuR2QlrHswNM";
	
//
	// CommonsHttpOAuthConsumer引き渡し用パラメータ名
    public static final String OAUTH_CONSUMER = "oauthConsumer";

    // タイムラインのURI
    public static final String TIMELINE_REQUEST_URL =
        "https://api.twitter.com/1.1/statuses/home_timeline.json";

    public static final String STREAM_TIMELINE_REQUEST_URL = 
    		"https://userstream.twitter.com/2/user.json";

    public static final String MENTION_REQUEST_URL =
            "https://api.twitter.com/1.1/statuses/mentions_timeline.json?include_rts=1";

    
    
    // つぶやきを投稿するURI
    public static final String TWEET_REQUEST_URL =
        "https://api.twitter.com/1.1/statuses/update.json";
    
    // 認証画面で許可しなかった場合の判定用
    public static final String REQUEST_TOKEN_DENIED = "denied";
}
