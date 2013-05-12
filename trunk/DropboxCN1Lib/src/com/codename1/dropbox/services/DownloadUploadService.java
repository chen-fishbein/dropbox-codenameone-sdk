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
public class DownloadUploadService extends ConnectionRequest implements SignedService{
    
    private byte [] data;
    private boolean download;
    
    public DownloadUploadService(boolean download, String filePath) {
        setUrl("https://api-content.dropbox.com/1/files/dropbox/" + filePath);
        if(download){
            setPost(false);
        }else{
            setPost(true);
        }
        this.download = download;
    }
    
    protected void readResponse(InputStream input) throws IOException {
        if(download){
            data = Util.readInputStream(input);
        }
        
    }
    
    public void applyParameters(Hashtable target) {
    }
    
    public byte [] getFileData(){
        return data;
    }
}
