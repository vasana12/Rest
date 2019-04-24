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
	
	//프로퍼티 읽기
	public static Properties s_properties = new Properties();
	public static String local_path = null;
    static {
        // Configures the logger to log to stdout
        //BasicConfigurator.configure(); //로그 일단 사용안함
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
		1. 태블로URL로 연결되는 화면을 로컬이미지로 변환하는 작업 수행
		2. 파라미터 : 파일경로(filePath), 태블로URL / 변환된 파일의 저장경로 반환
		3. 1) https로 시작할 때 : SSLContext 보안 소켓 생성 -> SSL 포트, 각종 연결 시간 설정 -> HttpGet형태로 태블로Url 호출
			-> 호출된 값을 HttpResponse, HttpEntity화한 다음 인풋스트림에 삽입 -> 인풋 파일을 ImageIO 객체 활용하여 png로 저장
			-> 저장경로 리턴
		   2) https로 시작하지 않을 때 : HttpClientBuilder로 HttpClient 생성 -> HttpGet형태로 태블로Url 호출
			-> 호출된 값을 HttpResponse, HttpEntity화한 다음 인풋스트림에 삽입 -> 인풋 파일을 ImageIO 객체 활용하여 png로 저장
			-> 저장경로 리턴
		4. 예외처리로 이상값은 모두 -1로 리턴
     */
	
	
	
	/**
	 * @return 메뉴리스트
	 */
	/*
		getMenuList
		1. Gson 활용하여 메뉴리스트 호출하는 작업 수행
		2. 파라미터 없음 / 메뉴리스트 리턴
		3. currentThreadClassLoader 변수 생성 -> menu_list.json 정보를 JsonReader로 호출 -> Gson으로 호출 정보 해쉬맵으로 변환
			-> menu_list에 해당하는 변환값을 맵리스트 형태로 호출 및 리턴 (메뉴데이터 로드 실패 시 콘솔 알림 후 빈 리스트 반환)
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
			System.out.println("메뉴 데이터 로드 실패");
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
		1. 태블로 url로 연결되는 화면을 png로 저장하는 작업 수행
		2. 파라미터 : 태블로URL, 파일이름 / 리턴값 없음
		3. name, contentUrl, data를 파라미터로 getTicket 메소드 실행하여 토큰 호출 -> url 경로 생성
			->  서버호스트와 token, 경로를 종합하여 viewUrl 문자열 생성 -> 'img-cache/filename.png'에 해당하는 파일경로 생성
			-> 자료 상시 최신화 위해 SimpleDateFormat 활용한 오늘, 전월, 전전월 설정
			 -> 태블로 변수인 최근연월, 비교연월, 전전주에 해당하는 파라미터 format으로 적용 -> 추출한 파일경로와 url을 이미지로 저장
	 */

	
	
	/**
	 * 태블로 신뢰 티켓 반환
	 */
	/*
	 	getTicket
		1. 접근 권한 관련 티켓을 받아오는 작업 수행
		2. 파라미터 : 유저아이디. 사이트, 호스트 / 상태메시지, 성공 여부, 업데이트된 정보 리턴
		3. 1) https로 시작할 때 : SSLContext 보안 소켓 생성 -> SSL 포트, 각종 연결 시간 설정 -> HttpGet형태로 태블로Url 호출
			-> HttpPost에 호스트정보 + /trusted 삽입 -> 리스트에 유저아이디와 사이트를 담은 변수(params) 생성
			-> UTF-8로 인코딩 -> 호출된 값을 HttpResponse, HttpEntity화 -> null이 아닌 값에 한해 상태메시지, 성공 여부, HttpEntity 리턴
		   2) https로 시작하지 않을 때 : HttpClientBuilder로 HttpClient 생성 -> HttpPost형태로 호스트정보 + /trusted 삽입
			-> 리스트에 유저아이디와 사이트를 담은 변수(params) 생성
			-> UTF-8로 인코딩 -> 호출된 값을 HttpResponse, HttpEntity화 -> null이 아닌 값에 한해 상태메시지, 성공 여부, HttpEntity 리턴
				(예외 발생 시 오류메시지와 -1값 반환)
	 */
	public static @ResponseBody Map<String,Object> getTicket(String userId,String site,String host) {
		
		ModelMap returnModel = new ModelMap();
		
		try {
			if (host.startsWith("https")) {

				HttpClient httpclient = new DefaultHttpClient();

				// SSLContext 지정된 시큐어 소켓 프로토콜 구현
				SSLContext sslcontext = SSLContext.getInstance("TLS");
				sslcontext.init(null, new TrustManager[] { easyTrustManager }, null);
				SSLSocketFactory socketFactory = new SSLSocketFactory(sslcontext, SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);

				// SSL기본포트 : 443
				Scheme sch = new Scheme("https", 443, socketFactory);
				httpclient.getConnectionManager().getSchemeRegistry().register(sch);
				// HttpClient POST 요청시 Expect 헤더정보 사용 x
				httpclient.getParams().setParameter("http.protocol.expect-continue", false);
				// 원격 호스트와 연결을 설정하는 시간
				httpclient.getParams().setParameter("http.connection.timeout", 3 * 1000);
				// 데이터를 기다리는 시간
				httpclient.getParams().setParameter("http.socket.timeout", 3 * 1000);
				// 연결 및 소켓 시간 초과
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
					
					returnModel.put("message", "완료");
					returnModel.put("result", "완료");
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
				
				System.out.println("2. setEntity작업을 거친 httpPost : " + httpPost); // ws
				
				HttpResponse http_response = httpClient.execute(httpPost);
				
				System.out.println("3. HttpResponse화된 httpPost : " + http_response); // ws
				
				HttpEntity respEntity = http_response.getEntity();
				
				System.out.println("4. getEntity작업을 거친 http_response : " + respEntity); // ws

				if (respEntity != null) {
					String content = EntityUtils.toString(respEntity);
					
					System.out.println("5. data부분에 들어갈 content : " + content); // ws
					
					returnModel.put("message", "완료");
					returnModel.put("result", "완료");
					returnModel.put("data", content);
					return returnModel;
				}
				//logger.debug("[TableauBaseController] httpPost... => " + httpPost.toString());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		returnModel.put("message", "잘못된 사용자 ID입니다.");
		returnModel.put("data", "-1");
		
		return returnModel;
	}
	
	
	//https 일 경우 무시용 https가 아닐경우 제거 후 상단 function 수정
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
