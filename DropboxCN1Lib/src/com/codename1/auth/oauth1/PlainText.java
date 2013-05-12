/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.auth.oauth1;

/**
 *
 * @author Chen
 */
public class PlainText extends SigningImplementation {

    public final static String ID = "PLAINTEXT";

    public String getId() {
        return ID;
    }

    protected String createSignature(String baseString, String consumerSecret, String DELIMITER, Token token){
        return consumerSecret + DELIMITER + token.getSecret();
    }
    @Override
    protected byte[] createSignature(byte[] key, byte[] data) {
        return key;
    }
}
