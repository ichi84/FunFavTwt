package data;

public class Constants {
	
	//request token�擾�purl
	public static final String REQUEST_TOKEN_ENDPOINT_URL 
		= "https://api.twitter.com/oauth/request_token";
	       
	
	//Access token�擾�purl
	public static final String ACCESS_TOKEN_ENDPOINT_URL 
	= "https://api.twitter.com/oauth/access_token";

	//Oauth�F�ؗpurl
	public static final String OAUTH_URL 
	= "https://api.twitter.com/oauth/authorize";

	//�R�[���o�b�N�purl
	public static final String CALBACK_URL 
	= "app://FunFav";
	//= "http://ichi84.blogspot.com/";

	//�R���V���[�}�L�[
	public static final String CONSUMER_KEY
	= "P80lTx8An2Lw8O3XouPfQ";

	//�R���V���[�}�V�[�N���b�g�L�[
	public static final String CONSUMER_SECRET
	= "dSwJJyntZYErB2bNiS9CPEotFxyg9jHeuR2QlrHswNM";
	
//
	// CommonsHttpOAuthConsumer�����n���p�p�����[�^��
    public static final String OAUTH_CONSUMER = "oauthConsumer";

    // �^�C�����C����URI
    public static final String TIMELINE_REQUEST_URL =
        "https://api.twitter.com/1.1/statuses/home_timeline.json";

    public static final String STREAM_TIMELINE_REQUEST_URL = 
    		"https://userstream.twitter.com/2/user.json";

    public static final String MENTION_REQUEST_URL =
            "https://api.twitter.com/1.1/statuses/mentions_timeline.json?include_rts=1";

    
    
    // �Ԃ₫�𓊍e����URI
    public static final String TWEET_REQUEST_URL =
        "https://api.twitter.com/1.1/statuses/update.json";
    
    // �F�؉�ʂŋ����Ȃ������ꍇ�̔���p
    public static final String REQUEST_TOKEN_DENIED = "denied";
}
