package com.pompip.testHttp;

//import com.bonree.testapkserver.util.Logger;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class HttpsClientUtil {
	private static final String TAG = "HttpsClientUtil";
	
	public static HttpResult uploadData(byte[] datas, String address){
		if(datas==null || address==null){
//			Logger.info(TAG, "datas is null ? "+ datas+" ;address is null? "+address);
			return null;
		}
		HttpURLConnection httpUrlConnection = null;
		OutputStream outputStream = null;
		InputStream inputStream = null;
		ByteArrayOutputStream baos = null;
		try {
			URL url = new URL(address);
			trustAllHosts();
			httpUrlConnection = (HttpURLConnection) url.openConnection();
			if (httpUrlConnection instanceof HttpsURLConnection) {
				((HttpsURLConnection)httpUrlConnection).setHostnameVerifier(DO_NOT_VERIFY);
			}
			httpUrlConnection.setDoInput(true);
			httpUrlConnection.setDoOutput(true);
			httpUrlConnection.setRequestMethod("POST");
			httpUrlConnection.setRequestProperty("Content-Type", "application/json ");
			httpUrlConnection.setRequestProperty("Host", url.getHost());
			httpUrlConnection.setConnectTimeout(2000);
			httpUrlConnection.setReadTimeout(5000);

			httpUrlConnection.connect();
			
			outputStream = httpUrlConnection.getOutputStream();
			outputStream.write(datas);
			outputStream.flush();

			inputStream = httpUrlConnection.getInputStream();
			
			baos = new ByteArrayOutputStream();
			int read;
			while((read = inputStream.read()) != -1){
				baos.write(read);
			}
			byte[] receivebytes = baos.toByteArray();
			return new HttpResult(httpUrlConnection.getResponseCode(),receivebytes);
		} catch (Exception e) {
//			Logger.error(TAG, e);
			e.printStackTrace();
		}finally{
			try{
				if(baos!=null)
					baos.close();
				if(inputStream!=null)
					inputStream.close();
				if(outputStream!=null)
					outputStream.close();
			}catch(Exception e){
				
			}
			if(httpUrlConnection!=null)
				httpUrlConnection.disconnect();
		}
		return null;
	}
	
	
	private static void trustAllHosts(){
		try{
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, new TrustManager[]{x509trustManager}, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		}catch(Exception e){
//			Logger.error(TAG, e);
		}
	}
	private static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier(){
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
		
	};
	
	private static final  X509TrustManager x509trustManager = new X509TrustManager(){

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
			
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
			
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[]{};
		}
		
	};

	public static HttpResult get(String address) {
		HttpURLConnection httpUrlConnection = null;
		InputStream inputStream = null;
		ByteArrayOutputStream baos = null;
		try {
			URL url = new URL(address);
			trustAllHosts();
			httpUrlConnection = (HttpURLConnection) url.openConnection();
			if (httpUrlConnection instanceof HttpsURLConnection) {
				((HttpsURLConnection)httpUrlConnection).setHostnameVerifier(DO_NOT_VERIFY);
			}
			httpUrlConnection.setDoInput(true);
			httpUrlConnection.setDoOutput(true);
			httpUrlConnection.setRequestMethod("GET");
			httpUrlConnection.setConnectTimeout(2000);
			httpUrlConnection.setReadTimeout(5000);
			httpUrlConnection.connect();

			inputStream = httpUrlConnection.getInputStream();

			baos = new ByteArrayOutputStream();
			int read;
			while((read = inputStream.read()) != -1){
				baos.write(read);
			}
			byte[] receivebytes = baos.toByteArray();
			return new HttpResult(httpUrlConnection.getResponseCode(),receivebytes);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if(baos!=null)
					baos.close();
				if(inputStream!=null)
					inputStream.close();
			}catch(Exception e){

			}
			if(httpUrlConnection!=null)
				httpUrlConnection.disconnect();
		}
		return null;
	}
}
