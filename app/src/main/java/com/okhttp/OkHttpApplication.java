package com.okhttp;

import com.nohttp.Application;

import java.io.IOException;
import java.io.InputStream;

public class OkHttpApplication extends android.app.Application{
	private static Application _instance;

	@Override
	public void onCreate() {
		super.onCreate();
		_instance = Application.getInstance();
		//   AppConfig.getInstance();
		// 添加https证书
		try {
			String[]  certFiles=this.getAssets().list("certs");
			if (certFiles != null) {
				for (String cert:certFiles) {
					InputStream is = getAssets().open("certs/" + cert);
					NetConfig.addCertificate(is); // 这里将证书读取出来，，放在配置中byte[]里
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static Application getInstance() {
		return _instance;
	}
}
