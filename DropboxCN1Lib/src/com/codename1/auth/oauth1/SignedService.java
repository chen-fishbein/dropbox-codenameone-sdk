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
 * @author Eric Coolman
 *
 */
public interface SignedService {
	/**
	 * Implementer should pass all header and body parameters to the target
	 * to be used in the signing process. 
	 * 
	 * @param target
	 */
	public void applyParameters(Hashtable target);
	
	/**
	 * Get the service endpoint URL for this service.
	 * 
	 * @return service endpoint URL.
	 */
	public String getUrl();
	
	/**
	 * Determine if request is a POST or a GET request. 
	 * 
	 * @return true of a POST request, otherwise false.
	 */
	public boolean isPost();
	
	/**
	 * Add a header name/value to the service request.
	 * 
	 * @param name name of header value.
	 * @param value value of header value.
	 */
	public void addRequestHeader(String name, String value);
}
