package com.example.market.data;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.lifecycle.AndroidViewModel;

import com.example.market.BuildConfig;
import com.example.market.interfaces.HTTTPCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.LogRecord;

public class LoginViewModel extends AndroidViewModel {
    private final Executor executor = Executors.newCachedThreadPool();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static java.net.CookieManager msCookieManager = new java.net.CookieManager();

    public LoginViewModel(Application application) {
        super(application);
    }

    public void sendPOSTRequest(String endpointURL,Map<String,Object> payload, boolean saveCookies, boolean sendCookies, HTTTPCallback callback){
        executor.execute(()->{
            JSONObject jObject;
            try {
                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String,Object> param : payload.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);

                URL url=new URL("https://"+BuildConfig.API_ADDRESS+endpointURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                con.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                con.setDoOutput(true);
                if (sendCookies && msCookieManager.getCookieStore().getCookies().size() > 0) {
                    con.setRequestProperty("Cookie",TextUtils.join(";",  msCookieManager.getCookieStore().getCookies()));
                }
                con.getOutputStream().write(postDataBytes);


                Reader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                for (int c; (c = in.read()) >= 0;)
                    sb.append((char)c);
                in.close();
                String response = sb.toString();
                System.out.println(response);
                jObject = new JSONObject(response);
                jObject.put("endpoint",endpointURL);
                if(saveCookies){
                    Map<String, List<String>> headerFields = con.getHeaderFields();
                    List<String> cookiesHeader = headerFields.get("Set-Cookie");
                    if (cookiesHeader != null) {
                        for (String cookie : cookiesHeader) {
                            msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                        }
                    }
                }
                con.disconnect();
            } catch (IOException | JSONException e) {
                jObject= new JSONObject();
            }
            JSONObject finalJObject = jObject;
            handler.post(()->{
               callback.onComplete(finalJObject);
            });
        });
    }
}