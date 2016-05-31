/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.canvas.config;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.canvas.user.SecurityContext;
import org.springframework.social.canvas.user.SimpleConnectionSignUp;
import org.springframework.social.canvas.user.SimpleSignInAdapter;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
//import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.facebook.web.CanvasSignInController;

/**
 * Spring Social Configuration.
 * @author Craig Walls
 */
@Configuration
@EnableSocial
public class SocialConfig implements SocialConfigurer {

    private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 20;

    @Inject
	private DataSource dataSource;
	
	public HttpClient httpClientCentral;

// old factory creation without web proxy
//	@Override
//	public void addConnectionFactories(ConnectionFactoryConfigurer cfConfig, Environment env) {
//		cfConfig.addConnectionFactory(new FacebookConnectionFactory(env.getProperty("facebook.appKey"), env.getProperty("facebook.appSecret")));
//	}

    @Bean(name="httpClientCentral")
    public HttpClient getHttpClient() {
    	if (httpClientCentral == null) {
	    	CredentialsProvider cp = new BasicCredentialsProvider();
	    	//TODO: place these value in config file via env.getProperty("facebook.appKey")
	    	cp.setCredentials(new AuthScope("localhost",3128), new UsernamePasswordCredentials("user2","pass2"));
	    	
	    	RequestConfig rc = RequestConfig.custom()
	    			.setAuthenticationEnabled(true)
	    			.setProxy(new HttpHost("localhost",3128))
	    			.build();
	    	
	    	
	    	HttpClientBuilder hcb = HttpClients.custom();
	        hcb.setMaxConnTotal(DEFAULT_MAX_TOTAL_CONNECTIONS)
	        .setMaxConnPerRoute(10)
	        .setDefaultCredentialsProvider(cp)
	        .setDefaultRequestConfig(rc);
	        
	        httpClientCentral = hcb.build();
    	}
        
        return httpClientCentral;
    }

    @Override
	public void addConnectionFactories(ConnectionFactoryConfigurer cfConfig, Environment env) {
		FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(
				env.getProperty("facebook.appKey"),
				env.getProperty("facebook.appSecret"),
				getHttpClient());
		cfConfig.addConnectionFactory(connectionFactory);
	}

	/**
	 * Singleton data access object providing access to connections across all users.
	 */
	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
		repository.setConnectionSignUp(new SimpleConnectionSignUp());
		return repository;
	}
	
	public UserIdSource getUserIdSource() {
		return new UserIdSource() {
			@Override
			public String getUserId() {
				return SecurityContext.getCurrentUser().getId();
			}
		};
	}

	@Bean
	@Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)
	public Facebook facebook(ConnectionRepository repository) {
		Connection<Facebook> connection = repository.findPrimaryConnection(Facebook.class);
		return connection != null ? connection.getApi() : null;
	}
	
	@Bean
	public CanvasSignInController canvasSignInController(ConnectionFactoryLocator connectionFactoryLocator, UsersConnectionRepository usersConnectionRepository, Environment env) {
		return new CanvasSignInController(connectionFactoryLocator, usersConnectionRepository, new SimpleSignInAdapter(), env.getProperty("facebook.appKey"), env.getProperty("facebook.appSecret"), env.getProperty("facebook.canvasPage"));
	}
	
}
