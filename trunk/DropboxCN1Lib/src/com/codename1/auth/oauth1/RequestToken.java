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

/**
 * 
 * @author Eric Coolman
 *
 */
public class RequestToken implements Token {
	static final String TOKEN = "oauth_token";
	static final String TOKEN_SECRET = TOKEN + "_secret";
	static final String CALLBACK = "oauth_callback";
	static final String CALLBACK_CONFIRMED = CALLBACK + "_confirmed";
	static final String VERIFIER = "oauth_verifier";
	static final String DENIED = "denied";
	private String token;
	private String secret;
	private String callback;
	private boolean callbackVerified;
	private boolean denied;
	private String verifier;

	RequestToken(String callback) {
		this("","",callback,false);
	}

	/**
	 * @param token
	 * @param secret
	 * @param callback
	 */
	RequestToken(String token, String secret, String callback,
			boolean callbackVerified) {
		super();
		this.token = token;
		this.secret = secret;
		this.callback = callback;
		this.callbackVerified = callbackVerified;
	}

	public void applyParameters(Hashtable target) {
		if (getVerifier() == null) {
			target.put(TOKEN, getToken());
		} else {
			target.put(TOKEN, getToken());
			target.put(VERIFIER, getVerifier());
		}
	}
	/**
	 * @return the verifier
	 */
	String getVerifier() {
		return verifier;
	}
	/**
	 * @param verifier the verifier to set
	 */
	void setVerifier(String verifier) {
		this.verifier = verifier;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.coolman.auth.oauth1.Token#getToken()
	 */
	public String getToken() {
		return token;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.coolman.auth.oauth1.Token#getSecret()
	 */
	public String getSecret() {
		return secret;
	}
	/**
	 * @return the callback
	 */
	String getCallback() {
		return callback;
	}
	/**
	 * @return the callbackVerified
	 */
	boolean isCallbackVerified() {
		return callbackVerified;
	}

	public void read(Hashtable h) {
            System.out.println("token0 " + token);
            System.out.println("secret0 " + secret);
            System.out.println("hash " + h);
		if (h.containsKey(VERIFIER)) {
			// Should prob test equals() here instead.
			token = (String) h.get(TOKEN);
			verifier = (String) h.get(VERIFIER);
		} else if (h.containsKey(DENIED)) {
			denied = true;
		} else {
			token = (String) h.get(TOKEN);
			String sec = (String) h.get(TOKEN_SECRET);
                        if(sec != null){
                            secret = sec;
                        }
			callbackVerified = "true".equals((String) h.get(CALLBACK_CONFIRMED));
		}
            System.out.println("token " + token);
            System.out.println("secret " + secret);
	}

	/**
	 * @return the denied
	 */
	boolean isDenied() {
		return denied;
	}

}
