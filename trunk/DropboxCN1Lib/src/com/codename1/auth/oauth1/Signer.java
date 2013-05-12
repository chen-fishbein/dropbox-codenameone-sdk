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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;


import com.codename1.io.Log;
import com.codename1.io.Util;

/**
 * 
 * @author Eric Coolman
 *
 */
class Signer {
	static final String AUTHORIZATION = "Authorization";
	static final String CONSUMER_KEY = "oauth_consumer_key";
	static final String SIGNATURE_METHOD = "oauth_signature_method";
	static final String SIGNATURE = "oauth_signature";
	static final String TIMESTAMP = "oauth_timestamp";
	static final String NONCE = "oauth_nonce";
	static final String VERSION = "oauth_version";
	static final String POST = "POST";
	static final String GET = "GET";
	static final String DELIMITER = "&";
	private String consumerSecret;
	private String consumerKey;
	private SigningImplementation signer;

	Signer(SigningImplementation signer, String consumerKey,
			String consumerSecret) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.signer = signer;
	}

	void sign(SignedService request, Token token) {
		Hashtable params = createParameters();
		token.applyParameters(params);
		request.applyParameters(params);
		String base = createSignatureBase(request, params);
		params.put(SIGNATURE, createSignature(base, token));
		Log.p(createHeader(params), Log.DEBUG);
		request.addRequestHeader(AUTHORIZATION, createHeader(params));
	}

	protected Hashtable createParameters() {
		Hashtable params = new Hashtable();
		params.put(TIMESTAMP, String.valueOf(getTimestamp()));
		params.put(SIGNATURE_METHOD, signer.getId());
		params.put(VERSION, "1.0");
		params.put(NONCE, String.valueOf(getNonce()));
		return params;
	}

	protected String createHeader(Hashtable params) {
                return "OAuth " + toQueryString(params, true, "\"", ",");
	}

	protected String createSignatureBase(SignedService request,
			Hashtable params) {
		params.put(CONSUMER_KEY, consumerKey);
		String method = request.isPost() ? POST : GET;
		return method + DELIMITER + Util.encodeUrl(request.getUrl())
				+ DELIMITER + Util.encodeUrl(toQueryString(params));
	}

	protected String createSignature(String baseString, Token token) {
                return signer.createSignature(baseString, consumerSecret, DELIMITER, token);
	}

	protected String toQueryString(Hashtable params) {
		return toQueryString(params, false, null, DELIMITER);
	}

	protected String toQueryString(Hashtable params, boolean onlyOauth,
			String surroundWith, String delimiter) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		if (surroundWith == null) {
			surroundWith = "";
		}
		if (delimiter == null) {
			delimiter = DELIMITER;
		}
		for (Enumeration e = params.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			ps.print(Util.encodeUrl(key) + "=" + surroundWith
					+ Util.encodeUrl((String) params.get(key))
					+ surroundWith);
			if (e.hasMoreElements()) {
				ps.print(delimiter);
			}
		}
		return os.toString();
	}

	protected long getTimestamp() {
		return System.currentTimeMillis() / 1000L;
	}

	protected long getNonce() {
		return System.currentTimeMillis();
	}
}
