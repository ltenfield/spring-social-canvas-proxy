package org.springframework.social.canvas.config;

import org.apache.http.client.HttpClient;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;
import org.springframework.social.oauth2.OAuth2Template;

/**
 * Facebook ServiceProvider implementation.
 * @author Keith Donald
 * @author Craig Walls
 */

public class FacebookServiceProvider extends AbstractOAuth2ServiceProvider<Facebook> {
	
	private HttpClient httpClient;
	
	private String appNamespace;
	
	private static final String API_VERSION = "2.5";

	private static final String GRAPH_API_URL = "https://graph.facebook.com/v" + API_VERSION + "/";


	/**
	 * Creates a FacebookServiceProvider for the given application ID, secret, and namespace.
	 * @param appId The application's App ID as assigned by Facebook 
	 * @param appSecret The application's App Secret as assigned by Facebook
	 * @param appNamespace The application's App Namespace as configured with Facebook. Enables use of Open Graph operations.
	 */
	public FacebookServiceProvider(String appId, String appSecret, String appNamespace) {
		super(getOAuth2Template(appId, appSecret,null));
		this.appNamespace = appNamespace;
	}
	
	public FacebookServiceProvider(String appId, String appSecret, String appNamespace, HttpClient httpClient) {
		super(getOAuth2Template(appId, appSecret, httpClient));		
		this.appNamespace = appNamespace;
		this.httpClient = httpClient;
	}

	private static OAuth2Template getOAuth2Template(String appId, String appSecret, HttpClient httpClient) {
		OAuth2Template oAuth2Template = new OAuth2Template(appId, appSecret,
				"https://www.facebook.com/v" + API_VERSION + "/dialog/oauth",
				GRAPH_API_URL + "oauth/access_token");
		oAuth2Template.setUseParametersForClientAuthentication(true);
		if (null != httpClient) {
			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
			oAuth2Template.setRequestFactory(requestFactory);
		}
		return oAuth2Template;
	}
	
	public Facebook getApi(String accessToken) {
		return new FacebookTemplate(accessToken, appNamespace);
	}
	
}