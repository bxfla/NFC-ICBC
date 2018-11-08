package com.sdhy.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class EncCer {
	  final static String KEYSTORE_FILE = "d:/client.p12";  
      final static String KEYSTORE_PASSWORD = "client";  
	  //加密字符串
	public static String encStringByCer(String jsonStr){
		String encStr ="";//加密后的数据
		  try {
			KeyStore ks = KeyStore.getInstance("PKCS12");  
			  FileInputStream fis = new FileInputStream(KEYSTORE_FILE);  
			  char[] nPassword = null;  
			  if ((KEYSTORE_PASSWORD == null)|| KEYSTORE_PASSWORD.trim().equals("")) {  
			      nPassword = null;  
			  } else {  
			      nPassword = KEYSTORE_PASSWORD.toCharArray();  
			  }  
			 ks.load(fis, nPassword);  
			 fis.close();  
			 System.out.println("keystore type = " + ks.getType());  
			 Enumeration enuml = ks.aliases();  
			 String keyAlias = null;  
			 if (enuml.hasMoreElements()) {  
			    keyAlias = (String) enuml.nextElement();  
			     System.out.println("alias=[" + keyAlias + "]");  
			}  
			 System.out.println("is key entry = " + ks.isKeyEntry(keyAlias));  
			Certificate cert = ks.getCertificate(keyAlias);  
			 PublicKey pubkey = cert.getPublicKey();  
			 //-------公钥加密------------------------------
			   byte[] msg =jsonStr.getBytes("UTF8"); //   
	             Cipher c1 = Cipher.getInstance("RSA/ECB/PKCS1Padding"); //  
	             c1.init(Cipher.ENCRYPT_MODE, pubkey);  //
	             byte[] msg1 = c1.doFinal(msg); //   
	             encStr =Utils.bytesToHexString(msg1) ;
	             System.out.println("加密后的数据----"+encStr);  
	            
		} catch(NoSuchPaddingException e){
			e.printStackTrace();
		}catch(InvalidKeyException e){
			e.printStackTrace();
		}catch(BadPaddingException e){
			e.printStackTrace();
		}
		   catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		  return encStr;
	}
	/**
	 * 私钥解密数据
	 * */
	  //加密字符串
		public static String decStringByCer(String jsonStr){
			byte[] cardBytes=Utils.hexString2Bytes(jsonStr);
			String decStr ="";//解密后的数据
			try {
				KeyStore ks = KeyStore.getInstance("PKCS12");  
				  FileInputStream fis = new FileInputStream(KEYSTORE_FILE);  
				  char[] nPassword = null;  
				  if ((KEYSTORE_PASSWORD == null)|| KEYSTORE_PASSWORD.trim().equals("")) {  
				      nPassword = null;  
				  } else {  
				      nPassword = KEYSTORE_PASSWORD.toCharArray();  
				  }  
				 ks.load(fis, nPassword);  
				 fis.close();  
				 System.out.println("keystore type = " + ks.getType());  
				 Enumeration enuml = ks.aliases();  
				 String keyAlias = null;  
				 if (enuml.hasMoreElements()) {  
				    keyAlias = (String) enuml.nextElement();  
				     System.out.println("alias=[" + keyAlias + "]");  
				}  
				 System.out.println("is key entry = " + ks.isKeyEntry(keyAlias));  
				 PrivateKey prikey = (PrivateKey) ks.getKey(keyAlias, nPassword);  
				Certificate cert = ks.getCertificate(keyAlias);  
				  Cipher c2 = Cipher.getInstance("RSA/ECB/PKCS1Padding");  
				     c2.init(Cipher.DECRYPT_MODE, prikey);  
				     byte[] msg2 = c2.doFinal(cardBytes); // 解密后的数据  
				     decStr =new String(msg2, "UTF8");
				     // 打印解密字符串  
				    System.out.println("解密后的数据----"+decStr); // 将解密数据转为字符串  
			} catch (UnrecoverableKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CertificateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  return decStr;
		}
		 
}
	
        
       
