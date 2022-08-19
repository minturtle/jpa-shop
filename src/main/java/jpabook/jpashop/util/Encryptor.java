package jpabook.jpashop.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryptor {

    public static String encrypt(String string){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(string.getBytes());
            return String.format("%064x", new BigInteger(1, md.digest()));
        }catch (NoSuchAlgorithmException e){}
        throw new EncryptFailed();
    }
}

class EncryptFailed extends RuntimeException{}
