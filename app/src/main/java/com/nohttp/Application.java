/*
 * Copyright 2015 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nohttp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.nohttp.config.AppConfig;
import com.sdhy.common.Utils;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;

import android.app.Activity;

/**
 * Created in Oct 23, 2015 12:59:13 PM.
 *
 * @author Yan Zhenjie.
 */
public class Application extends android.app.Application {

    private static Application _instance;
    private List<Activity> oList;//用于存放所有启动的Activity的集合

    @Override
    public void onCreate() {
        super.onCreate();
        oList = new ArrayList<Activity>();
        _instance = this;

        NoHttp.initialize(this);

        Logger.setTag("NoHttpSample");
        Logger.setDebug(true);// ��ʼNoHttp�ĵ���ģʽ, ������ܿ��������̺���־

        AppConfig.getInstance();
    }

    public static Application getInstance() {
        return _instance;
    }
	  final static String KEYSTORE_FILE = "client.p12";  
      final static String KEYSTORE_PASSWORD = "client";  
	  //�����ַ�
	public   String encStringByCer(String jsonStr){
		String encStr ="";//���ܺ�����
		  try {
			KeyStore ks = KeyStore.getInstance("PKCS12"); 
			  InputStream fis =_instance.getResources().getAssets().open(KEYSTORE_FILE);	
		//	  FileInputStream fis = new FileInputStream(KEYSTORE_FILE);  
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
			 //-------��Կ����------------------------------
			   byte[] msg =jsonStr.getBytes("UTF8"); //   
	             Cipher c1 = Cipher.getInstance("RSA/ECB/PKCS1Padding"); //  
	             c1.init(Cipher.ENCRYPT_MODE, pubkey);  //
	             byte[] msg1 = c1.doFinal(msg); //   
	             encStr =Utils.bytesToHexString(msg1) ;
	             System.out.println("���ܺ�����----"+encStr);  
	            
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
	public static byte[] hexString2Bytes(String hex) {

		if ((hex == null) || (hex.equals(""))) {
			return null;
		} else if (hex.length() % 2 != 0) {
			return null;
		} else {
			hex = hex.toUpperCase();
			int len = hex.length() / 2;
			byte[] b = new byte[len];
			char[] hc = hex.toCharArray();
			for (int i = 0; i < len; i++) {
				int p = 2 * i;
				b[i] = (byte) (charToByte(hc[p]) << 4 | charToByte(hc[p + 1]));
			}
			return b;
		}

	}
	/**
	 * �ַ�ת��Ϊ�ֽ�
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
	/**
	 * ˽Կ�������
	 * */
	  //�����ַ�
		public   String decStringByCer(String hex){
			
	//		byte[] cardBytes= hexString2Bytes(jsonStr);
			byte[] cardBytes =new byte[1024];
			if ((hex == null) || (hex.equals(""))) {
				return null;
			} else if (hex.length() % 2 != 0) {
				return null;
			} else {
				hex = hex.toUpperCase();
				int len = hex.length() / 2;
				byte[] b = new byte[len];
				char[] hc = hex.toCharArray();
				for (int i = 0; i < len; i++) {
					int p = 2 * i;
					b[i] = (byte) (charToByte(hc[p]) << 4 | charToByte(hc[p + 1]));
				}
				cardBytes =b;
			}
			String decStr ="";//���ܺ�����
			try {
				KeyStore ks = KeyStore.getInstance("PKCS12");  
				  InputStream fis =_instance.getResources().getAssets().open(KEYSTORE_FILE);	
			//	  FileInputStream fis = new FileInputStream(KEYSTORE_FILE);  
				  char[] nPassword = null;  
				  if ((KEYSTORE_PASSWORD == null)|| KEYSTORE_PASSWORD.trim().equals("")) {  
				      nPassword = null;  
				  } else {  
				      nPassword = KEYSTORE_PASSWORD.toCharArray();  
				  }  
				 ks.load(fis, nPassword);  
				 fis.close();  
				 Enumeration enuml = ks.aliases();  
				 String keyAlias = null;  
				 if (enuml.hasMoreElements()) {  
				    keyAlias = (String) enuml.nextElement();  
				}  
				 PrivateKey prikey = (PrivateKey) ks.getKey(keyAlias, nPassword);  
				  Cipher c2 = Cipher.getInstance("RSA/ECB/PKCS1Padding");  
				     c2.init(Cipher.DECRYPT_MODE, prikey);  
				     byte[] msg2 = c2.doFinal(cardBytes); // ���ܺ�����  
				     decStr =new String(msg2, "UTF8");
				     // ��ӡ�����ַ�  
			//	    Log.e("���ܺ�����----",decStr); // ���������תΪ�ַ�  
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
		
		
		/**
		* 添加Activity
		*/
		public void addActivity_(Activity activity) {
		// 判断当前集合中不存在该Activity
		if (!oList.contains(activity)) {
		oList.add(activity);//把当前Activity添加到集合中
		}
		}

		/**
		* 销毁单个Activity
		*/
		public void removeActivity_(Activity activity) {
		//判断当前集合中存在该Activity
		if (oList.contains(activity)) {
		    oList.remove(activity);//从集合中移除
		    activity.finish();//销毁当前Activity
		}
		}

		/**
		* 销毁所有的Activity
		*/
		public void removeALLActivity_() {
		     //通过循环，把集合中的所有Activity销毁
		for (Activity activity : oList) {
		     activity.finish();
		}
		
		
		}
		
		
		
}
