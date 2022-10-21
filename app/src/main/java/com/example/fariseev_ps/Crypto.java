package com.example.fariseev_ps;

import android.util.Base64;
import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {

    public static SecretKey stringToKey(String stringKey) {
        byte[] encodedKey = Base64.decode(stringKey.trim(), Base64.DEFAULT);
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }
    public static String decryptString(byte[] cipherText, SecretKey secret) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret);
        return new String(cipher.doFinal(cipherText), "UTF-8");
    }

 public void crypto () {
      String password= "";
    //  String key = "Lovelymouse";
  //    SecretKey secretkey = stringToKey(key);
      String secretkey_string = "s/7s0nxKWp6VxmZ5S0hVXA==";
      SecretKey newsecretkey = stringToKey(secretkey_string);

      try {
      byte[] encrypted = encryptString(password, newsecretkey);
      Log.d("--", "key: " + keyToString(newsecretkey));
      String crypted_pass=Base64.encodeToString(encrypted, Base64.DEFAULT);
      String decrypted_pass =decryptString(Base64.decode(crypted_pass, Base64.DEFAULT), newsecretkey);
      Log.d("--", "encrypted password: " + crypted_pass);
      Log.d("--", "dencrypted password: " +decrypted_pass);
      } catch (Exception e) {
          e.printStackTrace();
          Log.d("--","errorr "+e.getMessage());
      }
  }

  public static String keyToString(SecretKey secretKey) {
      return Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
  }

  public static SecretKey generateKey() throws Exception {
      KeyGenerator keyGen = KeyGenerator.getInstance("AES");
      keyGen.init(128);
      return keyGen.generateKey();
  }
  public static byte[] encryptString(String message, SecretKey secret) throws Exception {
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, secret);
      return cipher.doFinal(message.getBytes("UTF-8"));
  }
}
