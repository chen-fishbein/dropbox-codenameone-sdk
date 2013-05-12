/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.dropbox.services;

import com.codename1.auth.oauth1.SignedService;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.JSONParser;
import com.codename1.io.NetworkEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

/**
 *
 * @author Chen
 */
public class DropboxInfoRequest extends ConnectionRequest implements SignedService {

    public DropboxInfoRequest() {
        setPost(false);
        setUrl("https://api.dropbox.com/1/account/info");
    }
    

    protected void readResponse(InputStream input) throws IOException {
        InputStreamReader i = new InputStreamReader(input, "UTF-8");
        fireResponseListener(new NetworkEvent(this, new JSONParser().parse(i)));
    }

    public void applyParameters(Hashtable target) {
    }
}