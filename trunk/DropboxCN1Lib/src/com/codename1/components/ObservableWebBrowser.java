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
package com.codename1.components;

import java.util.Enumeration;
import java.util.Vector;

import com.codename1.components.WebBrowser;
import com.codename1.ui.events.ActionListener;

/**
 * 
 * @author Eric Coolman
 *
 */
public class ObservableWebBrowser extends WebBrowser {
	private Vector loadListeners = new Vector();
	private Vector errorListeners = new Vector();
	private Vector startListeners = new Vector();
		
	public void addErrorListener(ActionListener l) {
		errorListeners.addElement(l);
	}

	public void removeErrorListener(ActionListener l) {
		errorListeners.removeElement(l);
	}

	public void addLoadListener(ActionListener l) {
		loadListeners.addElement(l);
	}

	public void removeLoadListener(ActionListener l) {
		loadListeners.removeElement(l);
	}

	public void addStartListener(ActionListener l) {
		startListeners.addElement(l);
	}

	public void removeStartListener(ActionListener l) {
		startListeners.removeElement(l);
	}

	/* (non-Javadoc)
	 * @see com.codename1.components.WebBrowser#onError(java.lang.String, int)
	 */
	public void onError(String message, int errorCode) {
		super.onError(message, errorCode);
		for (Enumeration e = errorListeners.elements(); e.hasMoreElements(); ) {
			((ActionListener)e.nextElement()).actionPerformed(new WebBrowserEvent(this, message, errorCode));
		}
	}

	/* (non-Javadoc)
	 * @see com.codename1.components.WebBrowser#onLoad(java.lang.String)
	 */
	public void onLoad(String url) {
		super.onLoad(url);
		for (Enumeration e = loadListeners.elements(); e.hasMoreElements(); ) {
			((ActionListener)e.nextElement()).actionPerformed(new WebBrowserEvent(this, url));
		}
	}

	/* (non-Javadoc)
	 * @see com.codename1.components.WebBrowser#onStart(java.lang.String)
	 */
	public void onStart(String url) {
		super.onStart(url);
		for (Enumeration e = startListeners.elements(); e.hasMoreElements(); ) {
			((ActionListener)e.nextElement()).actionPerformed(new WebBrowserEvent(this, url));
		}
	}

}
