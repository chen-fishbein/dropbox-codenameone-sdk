/*
 * Copyright (c) 2012, Eric Coolman, 1815750 Ontario Inc. and/or its 
 * affiliates. All rights reserved.
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  1815750 Ontario Inc designates 
 * this  * particular file as subject to the "Classpath" exception as provided
 * in the LICENSE file that accompanied this code.
 *  
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 * 
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Please contact 1815750 Ontario Inc. through http://www.coolman.ca/ if you 
 * need additional information or have any questions.
 */
package com.codename1.auth.oauth1;

import java.util.Hashtable;

import com.codename1.components.ObservableWebBrowser;

import com.codename1.components.WebBrowser;
import com.codename1.io.Log;
import com.codename1.io.NetworkManager;
import com.codename1.io.Storage;
import com.codename1.io.Util;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.html.DocumentInfo;
import com.codename1.ui.layouts.BorderLayout;

/**
 * The OAuth1 class implements the OAuth flow, as documented here, for example:
 * 
 * <pre>
 * https://dev.twitter.com/docs/auth/implementing-sign-twitter
 * </pre>
 * 
 * @author Eric Coolman
 * 
 */
public class OAuth1 {
	private static Hashtable credentials;
	private ServiceProvider serviceProvider;
	private Signer requestSigner;
	private Signer signer;
	private String callback;
	private AccessToken accessToken;

	public OAuth1(String callback) {
		this.callback = callback;
		Hashtable c = getCredentials();
		String consumerKey = (String) c.get("key");
		String consumerSecret = (String) c.get("secret");
		serviceProvider = (ServiceProvider) c.get("provider");
		//this.requestSigner = new Signer(new HmacSha1(), consumerKey, consumerSecret);
		this.signer = new Signer(serviceProvider.getSigner(), consumerKey, consumerSecret);
		this.requestSigner = signer;//new Signer(new HmacSha1(), consumerKey, consumerSecret);
	}

	/**
	 * Return the ID of the authenticated user, or 0L if not authenticated.
	 * 
	 * @return user ID, or 0L.
	 */
	public long getUserId() {
		if (accessToken == null) {
			return 0L;
		}
		return accessToken.getId();
	}

	/**
	 * Return the screen name of the authenticated user, or null if not
	 * authenticated.
	 * 
	 * @return users screen name, or null.
	 */
	public String getScreenName() {
		if (accessToken == null) {
			return null;
		}
		return accessToken.getScreenName();
	}
        
	private Hashtable getCredentials() {
		if (credentials == null) {
			throw new IllegalStateException("Consumer credentials has not been initialized!");
		}
		String appId = callback.replace('/', '_');
		Hashtable h = (Hashtable) credentials.get(appId);
		if (h == null || h.containsKey("key") == false || h.containsKey("secret") == false) {
			throw new IllegalStateException("Consumer credentials has not been initialized for callback!");
		}
		if (h.containsKey("provider") == false) {
			throw new IllegalStateException("Service provider has not been initialized!");
		}
		return h;
	}

	public void signRequest(SignedService request) {
		signer.sign(request, accessToken);
	}

	public static void register(String callback, ServiceProvider provider, final String consumerKey,
			final String consumerSecret) {
		if (credentials == null) {
			credentials = new Hashtable();
			// Initialize serialization
			Util.register(AccessToken.OBJECT_ID, AccessToken.class);
			// Initialize the HTMLComponent browser with defaults
			NetworkManager.getInstance().addDefaultHeader("Accept-Language", "en-us");
			DocumentInfo.setDefaultEncoding("UTF-8");
		}
		Hashtable credential = new Hashtable();
		credential.put("key", consumerKey);
		credential.put("secret", consumerSecret);
		credential.put("provider", provider);
		credentials.put(callback.replace('/', '_'), credential);
	}

	public void authenticate() {
		authenticate(null);
	}

	public void authenticate(final Form currentForm) {
		if (onLoadAccessToken() == true) {
			return;
		}
		final Form backForm = (currentForm == null) ? Display.getInstance().getCurrent() : currentForm;

		RequestTokenRequest rtr = new RequestTokenRequest(serviceProvider, requestSigner, callback);
		// Retrieve (and wait) for the request token
		NetworkManager.getInstance().addToQueueAndWait(rtr);
		RequestToken requestToken = rtr.getToken();
		
		// Use the request token to retrieve an access token for the authorizing user.
		final ObservableWebBrowser wb = new ObservableWebBrowser();
		AccessTokenRequest atr = new AccessTokenRequest(serviceProvider, signer, requestToken);
		atr.addReceiveTokenListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Log.p("onAccessToken()", Log.DEBUG);
				AccessToken at = (AccessToken)evt.getSource();
				onReceiveAccessToken(at);
				onSaveAccessToken(at);
				onDisposeLogin(backForm, wb);
				onAuthenticated();
			}
		});
		atr.addDeniedListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onDisposeLogin(backForm, wb);
			}
		});

		wb.addStartListener(atr);
		String url = serviceProvider.getAuthenticateUrl(requestToken);
		wb.setURL(url);
		onDisplayLogin(backForm, wb);
	}

	/**
	 * Handle placement of the web browser, ie. in a form or dialog.
	 * 
	 * @param webBrowser
	 */
	public void onDisplayLogin(final Form backForm, final WebBrowser webBrowser) {
		Form form = new Form("Login");
		form.setScrollableY(false);
		if (backForm != null) {
			Command cancel = new Command("Cancel") {
				public void actionPerformed(ActionEvent ev) {
					backForm.showBack();
				}
			};
			form.addCommand(cancel);
			form.setBackCommand(cancel);
		}
		form.setLayout(new BorderLayout());
		form.addComponent(BorderLayout.CENTER, webBrowser);
		form.show();
	}

	/**
	 * Handle disposal of the web browser after auth complete
	 * 
	 * @param webBrowser
	 */
	public void onDisposeLogin(final Form backForm, final WebBrowser webBrowser) {
		webBrowser.stop();
		Container parent = webBrowser.getParent();
		parent.removeComponent(webBrowser);
		parent.revalidate();
		if (backForm != null) {
			backForm.showBack();
		}
	}

	/**
	 * Handle a newly received access token, use this to handle one-time
	 * operations, not including persistence (see onSaveAccessToken()).
	 * 
	 * @param token
	 */
	public void onReceiveAccessToken(AccessToken token) {
		this.accessToken = token;
	}

	/**
	 * Handle a newly received access token, use this to handle one-time
	 * operations such as persistence, etc.
	 * 
	 * @param token
	 */
	public void onSaveAccessToken(AccessToken token) {
		Storage.getInstance().writeObject(serviceProvider.getId(), token);
	}

	/**
	 * Handle a request made to retrieve a previously stored access token. On
	 * successful retrieval, implementors of this method should chain to
	 * onAccessToken() to elevate privileges.
	 * 
	 * @param token
	 */
	public boolean onLoadAccessToken() {
		if (accessToken == null) {
			accessToken = (AccessToken) Storage.getInstance().readObject(serviceProvider.getId());
		}
		if (accessToken != null) {
			onAuthenticated();
			return true;
		}
		return false;
	}

	/**
	 * Handle elevating access privileges.
	 * 
	 * @param token
	 */
	public void onAuthenticated() {
		System.out.println("User fully authenticated: " + accessToken.getScreenName());
	}

	/**
	 * User denied, continue with limited access.
	 */
	public void onDeniedAccess() {
	}

	/**
	 * Retrieve the current authenticated access token. This method should not
	 * be called unless you previously handle onAuthenticated().
	 * 
	 * @return
	 */
	public AccessToken getAccessToken() {
		return accessToken;
	}
}
