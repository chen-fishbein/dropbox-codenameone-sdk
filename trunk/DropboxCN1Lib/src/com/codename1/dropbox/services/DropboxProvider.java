/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.dropbox.services;


import com.codename1.auth.oauth1.PlainText;
import com.codename1.auth.oauth1.ServiceProvider;
import com.codename1.auth.oauth1.SigningImplementation;

/**
 *
 * @author Chen
 */
public class DropboxProvider extends ServiceProvider{

	static final String BASEURL = "https://api.dropbox.com/1/oauth";
	

	public DropboxProvider() {
		super("Dropbox", 
                        BASEURL + "/request_token", 
                        BASEURL + "/access_token",
                        "https://www.dropbox.com/1/oauth/authorize", 
                        BASEURL + "/access_token", 
                        new PlainText());
	}
    
}
