/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.dropbox.services;

import com.codename1.auth.oauth1.SignedService;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.JSONParser;
import com.codename1.io.NetworkEvent;
import com.codename1.io.Util;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

/**
 *
 * @author Chen
 */
public class DropboxService extends ConnectionRequest implements SignedService {

    public static final String THUMBNAILS = "thumbnails";
    public static final String METADATA = "metadata";
    private String method;

    public DropboxService(String method, String path) {
        setPost(false);

        String url = "https://api.dropbox.com/1/";
        if (method.equals(THUMBNAILS)) {
            url = "https://api-content.dropbox.com/1/";
        }
        url += method + "/dropbox";
        if (path.startsWith("/")) {
            url += path;
        } else {
            url += "/" + path;
        }
        setUrl(url);
        this.method = method;
    }

    protected void readResponse(InputStream input) throws IOException {
        if (method.equals(METADATA)) {
            InputStreamReader i = new InputStreamReader(input, "UTF-8");
            fireResponseListener(new NetworkEvent(this, new JSONParser().parse(i)));
        } else if (method.equals(THUMBNAILS)) {
            byte[] data = Util.readInputStream(input);
            fireResponseListener(new NetworkEvent(this, data));
        }

    }

    public void applyParameters(Hashtable target) {
    }
}
