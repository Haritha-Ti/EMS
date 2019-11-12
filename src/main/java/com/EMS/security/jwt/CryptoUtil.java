package com.EMS.security.jwt;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CryptoUtil {
	
	

	
    private static final String characterEncoding       = "UTF-8";
    private static final String cipherTransformation    = "AES/CBC/PKCS5PADDING";
    private static final String aesEncryptionAlgorithem = "AES";
    private final static Logger log = LoggerFactory.getLogger(CryptoUtil.class);
	
    
    /**
     * Method for Encrypt Plain String Data
     * @param plainText
     * @return encryptedText
     */
    public static String encrypt(String plainText) {
        String encryptedText = "";
        try {
            Cipher cipher   = Cipher.getInstance(cipherTransformation);
            byte[] key      = EnvironmentConstants.ENCRYPTION_KEY.getBytes("UTF-8");
            SecretKeySpec secretKey = new SecretKeySpec(key, aesEncryptionAlgorithem);
            IvParameterSpec ivparameterspec = new IvParameterSpec(key);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivparameterspec);
            byte[] cipherText = cipher.doFinal(plainText.getBytes("UTF8"));
            Base64.Encoder encoder = Base64.getEncoder();
            encryptedText = encoder.encodeToString(cipherText);

        } catch (Exception e) {
             e.printStackTrace();
             
        }
        return encryptedText;
    }
    
    
    /**
     * Method For Get encryptedText and Decrypted provided String
     * @param encryptedText
     * @return decryptedText
     * 
     */
    
    public static String decrypt(String encrypted) {
        String decryptedText = "";
        try {  
        	decryptedText=decryptE(encrypted);
        } catch (Exception e) {
        	  e.printStackTrace();
        }
        return decryptedText;
    }
    
    
    public static String decryptE(String encrypted) throws Exception{
        String decryptedText = "";
        	log.debug("encrypted:"+encrypted);
            Cipher cipher = Cipher.getInstance(cipherTransformation);
            byte[] key = EnvironmentConstants.ENCRYPTION_KEY.getBytes(characterEncoding);
            SecretKeySpec secretKey = new SecretKeySpec(key, aesEncryptionAlgorithem);
            IvParameterSpec ivparameterspec = new IvParameterSpec(key);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivparameterspec);
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] cipherText = decoder.decode(encrypted.getBytes("UTF8"));
            decryptedText = new String(cipher.doFinal(cipherText), "UTF-8");
            log.debug(decryptedText);
        return decryptedText;
    }


}
