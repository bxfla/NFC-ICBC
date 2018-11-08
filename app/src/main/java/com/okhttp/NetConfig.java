package com.okhttp;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class NetConfig {
	private static String TAG="NETCONFIG";
	 // ֤������
    private static List<byte[]> CERTIFICATES_DATA = new ArrayList<byte[]>();

	  public synchronized static void addCertificate(InputStream inputStream) {
	       Log.i(TAG,"#addCertificate inputStream = " + inputStream);
	       if (inputStream != null) {

	           try {
	               int ava = 0;// ���ݵ��οɶ�����
	               int len = 0;// �����ܳ���
	               ArrayList<byte[]> data = new ArrayList<byte[]>();
	               while ((ava = inputStream.available()) > 0) {
	                   byte[] buffer = new byte[ava];
	                   inputStream.read(buffer);
	                   data.add(buffer);
	                   len += ava;
	                 
	               }

	               byte[] buff = new byte[len];
	               int dstPos = 0;
	               for (byte[] bytes:data) {
	                   int length = bytes.length;
	                   System.arraycopy(bytes, 0, buff, dstPos, length);
	                   dstPos += length;
	               }

	               CERTIFICATES_DATA.add(buff);
	           } catch (IOException e) {
	               e.printStackTrace();
	           }

	       }
	   }

	   /**
	  * https֤��
	  * @return
	  */
	 public static List<byte[]> getCertificatesData() {
	     return CERTIFICATES_DATA;
	 }
	 //֤�����������ϵ�������ʱѡ��X509�����������xxx.cer��
	 //���֤�飬������okhttp��������ǰ�����ҷ�����Application.OnCreate�Ŀ��Ϊ����okhttpʹ��ʱ���ô���Context��Ҳ����Context����ɡ�
}
