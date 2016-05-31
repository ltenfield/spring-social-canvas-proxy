package org.springframework.social.canvas.config;

import org.apache.http.client.HttpClient;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookAdapter;

/**
 * Facebook ConnectionFactory implementation.
 * 
 * @author Keith Donald
 * @author Craig Walls
 */
public class FacebookConnectionFactory extends OAuth2ConnectionFactory<Facebook> {

	/**
	 * Creates a FacebookConnectionFactory for the given application ID and
	 * secret. Using this constructor, no application namespace is set (and
	 * therefore Facebook's Open Graph operations cannot be used).
	 * 
	 * @param appId
	 *            The application's App ID as assigned by Facebook
	 * @param appSecret
	 *            The application's App Secret as assigned by Facebook
	 */
	public FacebookConnectionFactory(String appId, String appSecret) {
		this(appId, appSecret, null, null);
	}

	public FacebookConnectionFactory(String appId, String appSecret, HttpClient httpClient) {
		this(appId, appSecret, null,httpClient);
	}
	/**
	 * Creates a FacebookConnectionFactory for the given application ID, secret,
	 * and namespace.
	 * 
	 * @param appId
	 *            The application's App ID as assigned by Facebook
	 * @param appSecret
	 *            The application's App Secret as assigned by Facebook
	 * @param appNamespace
	 *            The application's App Namespace as configured with Facebook.
	 *            Enables use of Open Graph operations.
	 */
	public FacebookConnectionFactory(String appId, String appSecret, String appNamespace) {
		super("facebook", new FacebookServiceProvider(appId, appSecret, appNamespace), new FacebookAdapter());
	}

	public FacebookConnectionFactory(String appId, String appSecret, String appNamespace, HttpClient httpClient) {
		super("facebook", new FacebookServiceProvider(appId, appSecret, appNamespace, httpClient),
				new FacebookAdapter());
	}

}