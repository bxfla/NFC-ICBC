package com.okhttp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.OkHttpClient;

public class CreateHttpClient {
	public OkHttpClient createOkhttp() {

	      OkHttpClient.Builder builder = new OkHttpClient.Builder();

	       // ���֤��

	       List<InputStream> certificates = new ArrayList<InputStream>();



	    //   List<String> certs = NetConfig.getCertificates();



	       List<byte[]> certs_data = NetConfig.getCertificatesData();

	       // ���ֽ�����תΪ����������

	       if (certs_data != null && !certs_data.isEmpty()) {

	           for (byte[] bytes:certs_data) {

	               certificates.add(new ByteArrayInputStream(bytes));

	           }

	       }



	       SSLSocketFactory sslSocketFactory = getSocketFactory(certificates);

	       if (sslSocketFactory != null) {

	           builder.sslSocketFactory(sslSocketFactory);

	       }



	       return builder.build();



	  }



	  /**

	     * ���֤��

	     *

	     * @param certificates

	     */

	    private static SSLSocketFactory getSocketFactory(List<InputStream> certificates) {



	        try {

	            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

	            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

	            keyStore.load(null);



	            try {

	                for (int i = 0, size = certificates.size(); i < size; ) {

	                    InputStream certificate = certificates.get(i);

	                    String certificateAlias = Integer.toString(i++);

	                    keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));



	                    if (certificate != null)

	                        certificate.close();

	                }

	            } catch (IOException e) {

	                e.printStackTrace();

	            }



	            SSLContext sslContext = SSLContext.getInstance("TLS");



	            TrustManagerFactory trustManagerFactory =

	                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());



	            trustManagerFactory.init(keyStore);

	            sslContext.init

	                    (

	                            null,

	                            trustManagerFactory.getTrustManagers(),

	                            new SecureRandom()

	                    );



	            return sslContext.getSocketFactory();



	        } catch (Exception e) {

	            e.printStackTrace();

	        }



	        return null;

	    }
}
