package me.psrcek.networkingAPI;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;

public class EncryptionHandler {

	public static SealedObject encrypt(Serializable o, PublicKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, IOException {
		Cipher encryCipher = Cipher.getInstance("RSA");
		encryCipher.init(Cipher.ENCRYPT_MODE, key);
		
		return new SealedObject(o, encryCipher);
	}
	
	public static Serializable decrypt(SealedObject o, PrivateKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, ClassNotFoundException, IllegalBlockSizeException, BadPaddingException, IOException {
		Cipher decryCipher = Cipher.getInstance("RSA");
		decryCipher.init(Cipher.DECRYPT_MODE, key);
		
		return (Serializable) o.getObject(decryCipher);
	}
	
	public static String encode(Object o) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
	    new ObjectOutputStream(out).writeObject(o);
		
		return Base64.getEncoder().encodeToString(out.toByteArray());
	}
	
	public static Object decode(String s) throws ClassNotFoundException, IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(s.getBytes()));
		
		return new ObjectInputStream(in).readObject();
	}
	
	public static String encryptAndEncode(Serializable o, PublicKey key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, IOException {
		return encode(encrypt(o, key));
	}
	
	public static Serializable decodeAndDecrypt(String s, PrivateKey key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException, IllegalBlockSizeException, BadPaddingException, IOException {
		return decrypt((SealedObject) decode(s), key);
	}
	
}
