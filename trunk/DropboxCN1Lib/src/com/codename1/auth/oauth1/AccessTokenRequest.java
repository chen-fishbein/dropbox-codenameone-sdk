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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.codename1.io.Log;
import com.codename1.io.NetworkManager;
import com.codename1.io.Util;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;

/**
 *
 * @author Eric Coolman
 *
 */
class AccessTokenRequest extends Request implements ActionListener {

    private RequestToken requestToken;
    private AccessToken accessToken;
    private Vector receiveListeners = new Vector();
    private Vector verifiedListeners = new Vector();
    private Vector deniedListeners = new Vector();

    /**
     * @param consumerSecret
     * @param consumerKey
     */
    AccessTokenRequest(ServiceProvider provider, Signer signer, RequestToken requestToken) {
        super(signer);
        this.requestToken = requestToken;
        setUrl(provider.getAccessTokenUrl());
        setPost(true);
    }

    public void actionPerformed(ActionEvent e) {
        String url = (String) e.getSource();
        Log.p("actionPerformed URL:" + e.getSource(), Log.DEBUG);
        if (url.startsWith(requestToken.getCallback())) {
            Hashtable params = getUrlParameters(url);
            requestToken.read(params);
            if (requestToken.isDenied()) {
                onDenied(requestToken);
            } else {
                onVerified(requestToken);
            } 
        }
    }

    void onDenied(RequestToken token) {
        Log.p("Denied", Log.DEBUG);
        // user triggered, so by default do nothing, just continue with limited
        // privileges. Override this to handle otherwise.
        for (Enumeration e = deniedListeners.elements(); e.hasMoreElements();) {
            ((ActionListener) e.nextElement()).actionPerformed(new ActionEvent(token));
        }
    }

    void onVerified(RequestToken token) {
        Log.p("Verified: " + token.getVerifier(), Log.DEBUG);
        for (Enumeration e = verifiedListeners.elements(); e.hasMoreElements();) {
            ((ActionListener) e.nextElement()).actionPerformed(new ActionEvent(token));
        }
        signRequest(token);
        NetworkManager.getInstance().addToQueueAndWait(this);
    }

    protected void readResponse(InputStream input) throws IOException {
        byte b[] = new byte[10000];
        Util.readAll(input, b);
        Hashtable response = parseQuery(new String(b));
        AccessToken token = new AccessToken();
        token.read(response);
        onReceiveAccessToken(token);
    }

    public void onReceiveAccessToken(AccessToken token) {
        this.accessToken = token;
        for (Enumeration e = receiveListeners.elements(); e.hasMoreElements();) {
            ((ActionListener) e.nextElement()).actionPerformed(new ActionEvent(accessToken));
        }
    }

    /**
     * @return the accessToken
     */
    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void addReceiveTokenListener(ActionListener l) {
        receiveListeners.addElement(l);
    }

    public void addVerifiedListener(ActionListener l) {
        verifiedListeners.addElement(l);
    }

    public void addDeniedListener(ActionListener l) {
        deniedListeners.addElement(l);
    }
}
