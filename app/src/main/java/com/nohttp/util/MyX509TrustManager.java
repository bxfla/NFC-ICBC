package com.nohttp.util;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;  
import java.security.cert.CertificateException;  
import java.security.cert.X509Certificate;  
import javax.net.ssl.TrustManager;  
import javax.net.ssl.TrustManagerFactory;  
import javax.net.ssl.X509TrustManager;

import android.content.Context;


public class MyX509TrustManager implements X509TrustManager {  
	
    X509TrustManager sunJSSEX509TrustManager; 
    public MyX509TrustManager(Context context) throws Exception {  
        //KeyStore ks = KeyStore.getInstance("JKS"); 
    	KeyStore ks = KeyStore.getInstance("PKCS12");
    	//获取证书路径和密钥密码
    	InputStream is = context.getAssets().open("214988583880107.pfx");    
    	
        ks.load(is,"214988583880107".toCharArray());  
        TrustManagerFactory tmf =  
        TrustManagerFactory.getInstance("X509");  
        tmf.init(ks);  
        TrustManager tms [] =tmf.getTrustManagers();  
        for (int i = 0; i < tms.length; i++) {  
            if(tms[i] instanceof X509TrustManager) {  
                sunJSSEX509TrustManager =(X509TrustManager) tms[i];  
                return;  
            }  
        }  
        throw new Exception("Couldn't initialize");  
    }  
     
    // 检查客户端证书
    public void checkClientTrusted(X509Certificate[] chain,String authType)  
                throws CertificateException {  
        try {  
           sunJSSEX509TrustManager.checkClientTrusted(chain, authType);  
        } catch(CertificateException excep) {  
       }  
    }  
    
    // 检查服务器端证书
    public void checkServerTrusted(X509Certificate[] chain, String authType)  
                throws CertificateException {  
        try {  
           sunJSSEX509TrustManager.checkServerTrusted(chain, authType);  
        } catch(CertificateException excep) {  
        }  
    }  
    
    // 返回受信任的X509证书数组
    public X509Certificate[] getAcceptedIssuers() {  
        return sunJSSEX509TrustManager.getAcceptedIssuers();  
    }  
}  