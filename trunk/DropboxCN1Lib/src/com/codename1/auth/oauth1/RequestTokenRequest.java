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

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import com.codename1.io.Util;

/**
 * 
 * @author Eric Coolman
 * 
 */
class RequestTokenRequest extends Request {
	private String callback;
	private RequestToken token;

	/**
	 * 
	 */
	RequestTokenRequest(ServiceProvider provider, Signer signer, String callback) {
		super(signer);
		setPost(true);
		setUrl(provider.getRequestTokenUrl());
		this.callback = callback;
		signRequest(new RequestToken(callback));
	}

	protected void readResponse(InputStream input) throws IOException {
		int i = getContentLength();
		if (i < 0) {
			// TODO: 8k is prob excessive for a request token.
			i = 8192;
		}
		byte b[] = new byte[i];
		i = Util.readAll(input, b);
		String s = new String(b, 0, i);
		Hashtable response = parseQuery(s);
		RequestToken token = new RequestToken(callback);
		token.read(response);
		onReceiveRequestToken(token);
	}

	/**
	 * Handle receiving the request token.
	 * 
	 * @param token
	 */
	public void onReceiveRequestToken(RequestToken token) {
		this.token = token;
	}

	/**
	 * Get the retrieved request token.
	 * 
	 * NOTE: The result of calling this method will be volatile request is made
	 * asynchronously!
	 * 
	 * @return the token
	 */
	public RequestToken getToken() {
		return token;
	}
}
