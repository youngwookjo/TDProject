package td.login;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Component
public class LoginAPI {

   public String getKakaoAccessToken(String authorize_code) {
      String access_Token = "";
      String refresh_Token = "";
      String reqURL = "https://kauth.kakao.com/oauth/token";

		try {
			URL url = new URL(reqURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			// POST 요청을 위해 기본값이 false인 setDoOutput을 true로
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);

         // POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
         BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
         StringBuilder sb = new StringBuilder();
         sb.append("grant_type=authorization_code");
         sb.append("&client_id=48bdb629c0cc1beac9d7d5f5cdead8b2");
         sb.append("&redirect_uri=http://localhost:8000/kakaoLogin");
         sb.append("&code=" + authorize_code);
         bw.write(sb.toString());
         bw.flush();
         System.out.println("sb : "+sb);

         // 결과 코드가 200이라면 성공
         int responseCode = conn.getResponseCode();
         System.out.println("responseCode : " + responseCode);

         // 요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
         BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
         String line = "";
         String result = "";

         while ((line = br.readLine()) != null) {
            result += line;
         }
         System.out.println("response body : " + result);

         // Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
         JsonParser parser = new JsonParser();
         JsonElement element = parser.parse(result);

         access_Token = element.getAsJsonObject().get("access_token").getAsString();
         refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

         System.out.println("access_token : " + access_Token);
         System.out.println("refresh_token : " + refresh_Token);

         br.close();
         bw.close();
      } catch (IOException e) {
         e.printStackTrace();
      }

      return access_Token;
   }

   public HashMap<String, Object> getKakaoUserInfo(String access_Token) {

      // 요청하는 클라이언트마다 가진 정보가 다를 수 있기에 HashMap타입으로 선언
      HashMap<String, Object> userInfo = new HashMap<>();
      String reqURL = "https://kapi.kakao.com/v2/user/me";
      try {
         URL url = new URL(reqURL);
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.setRequestMethod("POST");

         // 요청에 필요한 Header에 포함될 내용
         conn.setRequestProperty("Authorization", "Bearer " + access_Token);

         int responseCode = conn.getResponseCode();
         System.out.println("responseCode : " + responseCode);

         BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

         String line = "";
         String result = "";

         while ((line = br.readLine()) != null) {
            result += line;
         }
         System.out.println("response body : " + result);

         JsonParser parser = new JsonParser();
         JsonElement element = parser.parse(result);

         JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
         JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

         String id = element.getAsJsonObject().get("id").getAsString();
         String nickName = properties.getAsJsonObject().get("nickname").getAsString();
         String email = kakao_account.getAsJsonObject().get("email").getAsString();

         userInfo.put("id", id);
         userInfo.put("nickName", nickName);
         userInfo.put("email", email);

      } catch (IOException e) {
         e.printStackTrace();
      }

      return userInfo;
   }

   // naver Login
   public String getNaverAccessToken(String authorize_code) {
      String access_Token = "";
      String refresh_Token = "";
      String reqURL = "https://nid.naver.com/oauth2.0/token";

      try {
         URL url = new URL(reqURL);
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();

         // POST 요청을 위해 기본값이 false인 setDoOutput을 true로
         conn.setRequestMethod("POST");
         conn.setDoOutput(true);
         // POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
         BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
         StringBuilder sb = new StringBuilder();
         sb.append("grant_type=authorization_code");
         sb.append("&client_id=YgSTzaDFAOIL6DsaS9Cy");
         sb.append("&client_secret=gaSba7w9aU");
         sb.append("&code=" + authorize_code);
         bw.write(sb.toString());
         bw.flush();
         System.out.println("sb : "+ sb);
         System.out.println("bw : "+ bw);

         // 결과 코드가 200이라면 성공
         int responseCode = conn.getResponseCode();
         System.out.println("responseCode : " + responseCode);

         // 요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
         BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
         String line = "";
         String result = "";

         while ((line = br.readLine()) != null) {
            result += line;
         }
         System.out.println("response body : " + result);

         // Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
         JsonParser parser = new JsonParser();
         JsonElement element = parser.parse(result);

         access_Token = element.getAsJsonObject().get("access_token").getAsString();
         refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();
         
         System.out.println("access_token : " + access_Token);
         System.out.println("refresh_token : " + refresh_Token);

         br.close();
         bw.close();
      } catch (IOException e) {
         e.printStackTrace();
      }

      return access_Token;
   }

   
   /* Callback으로 전달받은 세선검증용 난수값과 세션에 저장되어있는 값이 일치하는지 확인 */
   public boolean token(HttpSession session, String state) throws IOException {
      System.out.println(session.getAttribute("state"));
      System.out.println(state);
      String sessionState = (String) session.getAttribute("state");
      if (sessionState.equals(state)) {
         System.out.println("통과");
         return true;
      }
      return false;
   }

   
   public HashMap<String, Object> getNaverUserInfo(String access_Token) {
      // 요청하는 클라이언트마다 가진 정보가 다를 수 있기에 HashMap타입으로 선언
      HashMap<String, Object> userInfo = new HashMap<>();
      String reqURL = "https://openapi.naver.com/v1/nid/me";
      try {
         URL url = new URL(reqURL);
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.setRequestMethod("POST");

         // 요청에 필요한 Header에 포함될 내용
         conn.setRequestProperty("Authorization", "Bearer " + access_Token);

         int responseCode = conn.getResponseCode();
         System.out.println("responseCode : " + responseCode);

         BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

         String line = "";
         String result = "";

         while ((line = br.readLine()) != null) {
            result += line;
         }
         System.out.println("response body : " + result);

         JsonParser parser = new JsonParser();
         JsonElement element = parser.parse(result);

         JsonObject properties = element.getAsJsonObject().get("response").getAsJsonObject();

         String nickName = properties.getAsJsonObject().get("name").getAsString();
         String email = properties.getAsJsonObject().get("email").getAsString();
         String id = properties.getAsJsonObject().get("id").getAsString();

         userInfo.put("nickName", nickName);
         userInfo.put("email", email);
         userInfo.put("id", id);

      } catch (IOException e) {
         e.printStackTrace();
      }

      return userInfo;
   }

   public void kakaoLogout(String access_Token) {
      String reqURL = "https://kapi.kakao.com/v1/user/logout";
      try {
         URL url = new URL(reqURL);
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.setRequestMethod("POST");
         conn.setRequestProperty("Authorization", "Bearer " + access_Token);

         int responseCode = conn.getResponseCode();
         System.out.println("responseCode : " + responseCode);

         BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

         String result = "";
         String line = "";

         while ((line = br.readLine()) != null) {
            result += line;
         }
         System.out.println(result);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
