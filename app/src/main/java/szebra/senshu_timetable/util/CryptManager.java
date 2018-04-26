package szebra.senshu_timetable.util;

import android.util.Log;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by s-zebra on 2017/11/27.
 */

public class CryptManager {
  private static final String TAG = "CryptManager";
  private static final String AES = "AES";
  private static int KEY_LENGTH_BYTES = 64;
  private static int KEY_LENGTH_BITS = KEY_LENGTH_BYTES * 8;
  
  public static Key generateKey() {
    try {
      KeyGenerator gen = KeyGenerator.getInstance(AES);
      SecureRandom rnd = SecureRandom.getInstance("SHA1PRNG");
      gen.init(KEY_LENGTH_BITS, rnd);
      return gen.generateKey();
    } catch (NoSuchAlgorithmException e) {
      Log.e(TAG, "No Such Algorithm... Should not happen.");
      return null;
    }
  }
  
  public static Key getKey(byte[] bytes) {
    byte[] b = new byte[KEY_LENGTH_BYTES];
    for (int i = 0; i < KEY_LENGTH_BYTES; i++) {
      b[i] = i < bytes.length ? bytes[i] : 0;
    }
    return new SecretKeySpec(b, AES);
  }
  
  public static byte[] encrypt(byte[] src, byte[] key) throws GeneralSecurityException {
    return encrypt(src, getKey(key));
  }
  
  public static byte[] encrypt(byte[] src, Key skey) throws GeneralSecurityException {
    Cipher cipher = Cipher.getInstance(AES);
    cipher.init(Cipher.ENCRYPT_MODE, skey);
    return cipher.doFinal(src);
  }
  
  public static byte[] decrypt(byte[] src, byte[] key) throws GeneralSecurityException {
    return decrypt(src, getKey(key));
  }
  
  public static byte[] decrypt(byte[] src, Key skey) throws GeneralSecurityException {
    Cipher cipher = Cipher.getInstance(AES);
    cipher.init(Cipher.DECRYPT_MODE, skey);
    return cipher.doFinal(src);
  }
}
