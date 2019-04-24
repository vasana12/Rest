package com.spring.tableau;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.BasicConfigurator;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class Util {
	
	//������Ƽ �б�
	public static Properties s_properties = new Properties();
	public static String local_path = null;
    static {
        // Configures the logger to log to stdout
        //BasicConfigurator.configure(); //�α� �ϴ� ������
        ClassLoader currentThreadClassLoader = Thread.currentThread().getContextClassLoader();
        local_path = currentThreadClassLoader.getResource("").getPath();
        // Loads the values from configuration file into the Properties instance
        try {
            s_properties.load(currentThreadClassLoader.getResource("config.properties").openStream());
            
        } catch (IOException e) {
        	System.out.println("Tableu configuration load error");
        }
    } 
    
    /*
     	saveTableauUrlLocalImg
		1. �º���URL�� ����Ǵ� ȭ���� �����̹����� ��ȯ�ϴ� �۾� ����
		2. �Ķ���� : ���ϰ��(filePath), �º���URL / ��ȯ�� ������ ������ ��ȯ
		3. 1) https�� ������ �� : SSLContext ���� ���� ���� -> SSL ��Ʈ, ���� ���� �ð� ���� -> HttpGet���·� �º���Url ȣ��
			-> ȣ��� ���� HttpResponse, HttpEntityȭ�� ���� ��ǲ��Ʈ���� ���� -> ��ǲ ������ ImageIO ��ü Ȱ���Ͽ� png�� ����
			-> ������ ����
		   2) https�� �������� ���� �� : HttpClientBuilder�� HttpClient ���� -> HttpGet���·� �º���Url ȣ��
			-> ȣ��� ���� HttpResponse, HttpEntityȭ�� ���� ��ǲ��Ʈ���� ���� -> ��ǲ ������ ImageIO ��ü Ȱ���Ͽ� png�� ����
			-> ������ ����
		4. ����ó���� �̻��� ��� -1�� ����
     */
	
	
	
	/**
	 * @return �޴�����Ʈ
	 */
	/*
		getMenuList
		1. Gson Ȱ���Ͽ� �޴�����Ʈ ȣ���ϴ� �۾� ����
		2. �Ķ���� ���� / �޴�����Ʈ ����
		3. currentThreadClassLoader ���� ���� -> menu_list.json ������ JsonReader�� ȣ�� -> Gson���� ȣ�� ���� �ؽ������� ��ȯ
			-> menu_list�� �ش��ϴ� ��ȯ���� �ʸ���Ʈ ���·� ȣ�� �� ���� (�޴������� �ε� ���� �� �ܼ� �˸� �� �� ����Ʈ ��ȯ)
	 */
	public static List<Map> getMenuList()
	{
		
		JsonParser parser = new JsonParser();
		Gson gson = new Gson();
	
        ClassLoader currentThreadClassLoader = Thread.currentThread().getContextClassLoader();
        
        JsonReader reader = null;
		try {
			reader = new JsonReader(new FileReader(currentThreadClassLoader.getResource("menu_list.json").getPath()));
			HashMap<Object,Object> tempObject = gson.fromJson(reader, HashMap.class);
	
			@SuppressWarnings("unchecked")
			List<Map> Menulist = (List<Map>)tempObject.get("menu_list");
			
			return Menulist;
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("�޴� ������ �ε� ����");
			List<Map> Menulist = new ArrayList<>();
			return Menulist;
		}
        
		

		
	}
	
	/**
	 * @param tableauUrl
	 * @param fileName
	 */
	/*
	 	createTableauImg
		1. �º��� url�� ����Ǵ� ȭ���� png�� �����ϴ� �۾� ����
		2. �Ķ���� : �º���URL, �����̸� / ���ϰ� ����
		3. name, contentUrl, data�� �Ķ���ͷ� getTicket �޼ҵ� �����Ͽ� ��ū ȣ�� -> url ��� ����
			->  ����ȣ��Ʈ�� token, ��θ� �����Ͽ� viewUrl ���ڿ� ���� -> 'img-cache/filename.png'�� �ش��ϴ� ���ϰ�� ����
			-> �ڷ� ��� �ֽ�ȭ ���� SimpleDateFormat Ȱ���� ����, ����, ������ ����
			 -> �º��� ������ �ֱٿ���, �񱳿���, �����ֿ� �ش��ϴ� �Ķ���� format���� ���� -> ������ ���ϰ�ο� url�� �̹����� ����
	 */

	
	
	/**
	 * �º��� �ŷ� Ƽ�� ��ȯ
	 */
	/*
	 	getTicket
		1. ���� ���� ���� Ƽ���� �޾ƿ��� �۾� ����
		2. �Ķ���� : �������̵�. ����Ʈ, ȣ��Ʈ / ���¸޽���, ���� ����, ������Ʈ�� ���� ����
		3. 1) https�� ������ �� : SSLContext ���� ���� ���� -> SSL ��Ʈ, ���� ���� �ð� ���� -> HttpGet���·� �º���Url ȣ��
			-> HttpPost�� ȣ��Ʈ���� + /trusted ���� -> ����Ʈ�� �������̵�� ����Ʈ�� ���� ����(params) ����
			-> UTF-8�� ���ڵ� -> ȣ��� ���� HttpResponse, HttpEntityȭ -> null�� �ƴ� ���� ���� ���¸޽���, ���� ����, HttpEntity ����
		   2) https�� �������� ���� �� : HttpClientBuilder�� HttpClient ���� -> HttpPost���·� ȣ��Ʈ���� + /trusted ����
			-> ����Ʈ�� �������̵�� ����Ʈ�� ���� ����(params) ����
			-> UTF-8�� ���ڵ� -> ȣ��� ���� HttpResponse, HttpEntityȭ -> null�� �ƴ� ���� ���� ���¸޽���, ���� ����, HttpEntity ����
				(���� �߻� �� �����޽����� -1�� ��ȯ)
	 */
	public static @ResponseBody Map<String,Object> getTicket(String userId,String site,String host) {
		
		ModelMap returnModel = new ModelMap();
		
		try {
			if (host.startsWith("https")) {

				HttpClient httpclient = new DefaultHttpClient();

				// SSLContext ������ ��ť�� ���� �������� ����
				SSLContext sslcontext = SSLContext.getInstance("TLS");
				sslcontext.init(null, new TrustManager[] { easyTrustManager }, null);
				SSLSocketFactory socketFactory = new SSLSocketFactory(sslcontext, SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);

				// SSL�⺻��Ʈ : 443
				Scheme sch = new Scheme("https", 443, socketFactory);
				httpclient.getConnectionManager().getSchemeRegistry().register(sch);
				// HttpClient POST ��û�� Expect ������� ��� x
				httpclient.getParams().setParameter("http.protocol.expect-continue", false);
				// ���� ȣ��Ʈ�� ������ �����ϴ� �ð�
				httpclient.getParams().setParameter("http.connection.timeout", 3 * 1000);
				// �����͸� ��ٸ��� �ð�
				httpclient.getParams().setParameter("http.socket.timeout", 3 * 1000);
				// ���� �� ���� �ð� �ʰ�
				httpclient.getParams().setParameter("http.connection-manager.timeout", 3 * 1000);
				httpclient.getParams().setParameter("http.protocol.head-body-timeout", 3 * 1000);
				HttpPost httpPost = new HttpPost(host+ "/trusted");
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("username", userId));
				params.add(new BasicNameValuePair("target_site", site));
				httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
				HttpResponse http_response = httpclient.execute(httpPost);
				HttpEntity respEntity = http_response.getEntity();

				if (respEntity != null) {
					String content = EntityUtils.toString(respEntity);
					
					returnModel.put("message", "�Ϸ�");
					returnModel.put("result", "�Ϸ�");
					returnModel.put("data", content);
					return returnModel;
				}

			} else {
				
				HttpClient httpClient = HttpClientBuilder.create().build();
				HttpPost httpPost = new HttpPost(host + "/trusted");
				
				System.out.println("1. httpPost : " + httpPost); // ws
				
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("username", userId));
				params.add(new BasicNameValuePair("target_site", site));
				httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
				
				System.out.println("2. setEntity�۾��� ��ģ httpPost : " + httpPost); // ws
				
				HttpResponse http_response = httpClient.execute(httpPost);
				
				System.out.println("3. HttpResponseȭ�� httpPost : " + http_response); // ws
				
				HttpEntity respEntity = http_response.getEntity();
				
				System.out.println("4. getEntity�۾��� ��ģ http_response : " + respEntity); // ws

				if (respEntity != null) {
					String content = EntityUtils.toString(respEntity);
					
					System.out.println("5. data�κп� �� content : " + content); // ws
					
					returnModel.put("message", "�Ϸ�");
					returnModel.put("result", "�Ϸ�");
					returnModel.put("data", content);
					return returnModel;
				}
				//logger.debug("[TableauBaseController] httpPost... => " + httpPost.toString());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		returnModel.put("message", "�߸��� ����� ID�Դϴ�.");
		returnModel.put("data", "-1");
		
		return returnModel;
	}
	
	
	//https �� ��� ���ÿ� https�� �ƴҰ�� ���� �� ��� function ����
    private static TrustManager easyTrustManager = new X509TrustManager() {

		//@Override
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			// TODO Auto-generated method stub
			return null;
		}

		//@Override
		public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
			// TODO Auto-generated method stub

		}

		//@Override
		public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
			// TODO Auto-generated method stub

		}
	};

}