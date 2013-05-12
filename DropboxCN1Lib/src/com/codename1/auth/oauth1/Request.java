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

import com.codename1.io.ConnectionRequest;
import com.codename1.io.Log;
import com.codename1.io.Util;

/**
 * 
 * @author Eric Coolman
 *
 */
class Request extends ConnectionRequest implements SignedService {
	private Signer signer;
	
	/**
	 * 
	 */
	Request(Signer signer) {
		this.signer = signer;
	}

	protected void signRequest(Token token) {
		signer.sign(this, token);
	}

	protected Hashtable getUrlParameters(String url) {
		String query = Util.getURLPath(url);
		int index = query.indexOf("?");
		if (index == -1) {
			return null;
		}
		query = query.substring(index + 1);
		return parseQuery(query);
	}

	protected Hashtable parseQuery(String query) {
		return parseQuery(query, "&");
	}
	
	protected Hashtable parseQuery(String query, String delimiter) {
		String elements[] = Util.split(query, delimiter);
		Hashtable response = new Hashtable();
		for (int i = 0; i < elements.length; i++) {
			String namevalue[] = Util.split(elements[i], "=");
			if (namevalue.length == 2) {
				response.put(namevalue[0], namevalue[1]);
			} else if (namevalue.length == 1) {
				response.put(namevalue[0], "");
			} else {
				Log.p("Error parsing query " + i + ":" + elements[i]);
			}
		}
		return response;
	}

	public void applyParameters(Hashtable target) {
		// oauth requests shouldn't have any parameters.
	}

}
