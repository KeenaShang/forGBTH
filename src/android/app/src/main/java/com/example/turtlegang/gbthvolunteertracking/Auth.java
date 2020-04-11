package com.example.turtlegang.gbthvolunteertracking;

import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Auth {

    public static String hashSimple(String password, byte[] salt) throws Exception{
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = f.generateSecret(spec).getEncoded();
        return String.valueOf(hash);
    }

    public static boolean isValid(String username, String password) {
        // needs an endpoint that will fetch the salt and hashed password associated with username
        // return false if username dne
        // check if
        String stored_pass = "";  // replace with password retrieved from db
        SecureRandom random = new SecureRandom();
        byte salt[] = new byte[20]; // replace with salt from db and delete above line
        try {
            return stored_pass.equals(hashSimple(password, salt));
        } catch (Exception e) {
            return false;
        }

    }
}
