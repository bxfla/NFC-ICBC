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
package com.nohttp.util;

import android.annotation.SuppressLint;

import com.nohttp.Application;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created in Jan 31, 2016 8:03:59 PM.
 *
 * @author Yan Zhenjie.
 */
public class SSLContextUtil {

    /**
     * 拿到https证书, SSLContext (NoHttp已经修补了系统的SecureRandom的bug)。
     */
    /**CertificateFactory.class
     * 此类定义了用于从相关的编码中生成证书、证书路径 (CertPath) 和证书撤消列表 (CRL) 对象的 CertificateFactory 功能。

     为了实现多个证书组成的编码，如果要解析一个可能由多个不相关证书组成的集合时，应使用 generateCertificates。
     否则，如果要生成 CertPath（证书链）并随后使用 CertPathValidator 验证它，则应使用 generateCertPath。

     X.509 的 CertificateFactory 返回的证书必须是 java.security.cert.X509Certificate 的实例，CRL 的 CertificateFactory 返回的证书则必须是 java.security.cert.X509CRL 的实例。

     以下示例代码读取一个具有 Base64 编码证书的文件，该证书由 -----BEGIN CERTIFICATE----- 语句开始，由 -----END CERTIFICATE----- 语句结束。我们将 FileInputStream（它不支持 mark 和 reset）转换成 BufferedInputStream（它支持这些方法），这样每次调用 generateCertificate 只需要一个证书，并将输入流的读取位置定位在文件中的下一个证书处：


     FileInputStream fis = new FileInputStream(filename);
     BufferedInputStream bis = new BufferedInputStream(fis);

     CertificateFactory cf = CertificateFactory.getInstance("X.509");

     while (bis.available() > 0) {
     Certificate cert = cf.generateCertificate(bis);
     System.out.println(cert.toString());
     }


     以下示例代码将解析存储在文件中的 PKCS#7 格式的证书答复，并从中提取所有的证书：


     FileInputStream fis = new FileInputStream(filename);
     CertificateFactory cf = CertificateFactory.getInstance("X.509");
     Collection c = cf.generateCertificates(fis);
     Iterator i = c.iterator();
     while (i.hasNext()) {
     Certificate cert = (Certificate)i.next();
     System.out.println(cert);
     }


     * */
    @SuppressLint("TrulyRandom")
    public static SSLContext getSSLContext() {
        SSLContext sslContext = null;
        try {
            //SSLSocketFactory需要一个SSLContext环境对象来构建，

            ///构建一个SSLContext 环境：// 构造SSL环境，指定SSL版本为3.0，也可以使用TLSv1，但是SSLv3更加常用。
            sslContext = SSLContext.getInstance("TLS");
            InputStream inputStream = Application.getInstance().getAssets().open("certs/tomcatcert.cer");
///PKCS12和JKS是keystore的type，不是Certificate的type，所以X.509不能用PKCS12代替
            CertificateFactory cerFactory = CertificateFactory.getInstance("X.509");
            Certificate cer = cerFactory.generateCertificate(inputStream);
            //keystore的类型，默认是jks
            /**CER(Canonical Encoding Rules,规范编码格式) 是数字证书的一种编码格式，它是BER(Basic Encoding Rules 基本编码格式) 的一个变种，
             *      比BER 规定得更严格。
             *  后缀的证书文件有两种编码:
             DER(Distinguished Encoding Rule 卓越编码格式) 同样是BER的一个变种，DER使用定长模式。
             PKCS(Public-Key Cryptography Standards,公钥加密标准) 由RSA实验室和其他安全系统开发商为公钥密码的发展而制定的一系列标准。
             * */

            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(null, null);
            keyStore.setCertificateEntry("trust", cer);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, null);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sslContext;
    }
    /**
     * 如果不需要https证书.(NoHttp已经修补了系统的SecureRandom的bug)。
     */
    public static SSLContext getDefaultSLLContext() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManagers}, new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslContext;
    }

    private static TrustManager trustManagers = new X509TrustManager() {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };

    public static final HostnameVerifier HOSTNAME_VERIFIER = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
/*
 * 在Security编程中，有几种典型的密码交换信息文件格式:
DER-encoded certificate: .cer, .crt
PEM-encoded message: .pem
PKCS#12 Personal Information Exchange: .pfx, .p12
PKCS#10 Certification Request: .p10 .csr
PKCS#7 cert request response: .p7r
PKCS#7 binary message: .p7b .p7c .spc

cer/.crt 是用于存放证书，它是2进制形式存放

pem 跟crt/cer的区别是它以Ascii来表示

pfx/p12 用于存放个人证书/私钥，他通常包含保护密码，2进制方式

p10 .csr 是证书请求

p7r是CA对证书请求的回复，只用于导入

p7b .p7c .spc 以树状展示证书链(certificate chain)，同时也支持单个证书，不含私钥*/
}
