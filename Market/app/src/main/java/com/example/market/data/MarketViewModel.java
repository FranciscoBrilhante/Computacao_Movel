package com.example.market.data;

import static android.content.Context.MODE_PRIVATE;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import android.app.Application;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.TextView;

import com.example.market.BuildConfig;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.marketDatabase.Category;
import com.example.market.marketDatabase.CategoryDao;
import com.example.market.marketDatabase.MainRoomDatabase;
import com.example.market.marketDatabase.Product;
import com.example.market.marketDatabase.ProductDao;
import com.example.market.ui.fragments.LoginFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class MarketViewModel extends AndroidViewModel {
    private Application application;
    private final MainRoomDatabase db;
    private final ProductDao productDao;
    private final CategoryDao categoryDao;

    private final Executor executor = Executors.newCachedThreadPool();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static java.net.CookieManager msCookieManager = new java.net.CookieManager();

    public MarketViewModel(Application application) {
        super(application);
        this.application = application;
        db = MainRoomDatabase.getDatabase(application);
        productDao = db.productDao();
        categoryDao = db.categoryDao();
    }

    public LiveData<List<Product>> getAllProducts() {
        return productDao.getAll();
    }

    public LiveData<List<Category>> getAllCategories() {
        return categoryDao.getAll();
    }

    public void addCategories(ArrayList<Category> categories) {
        MainRoomDatabase.databaseWriteExecutor.execute(()->{
            for(Category category:categories){
                categoryDao.insert(category);
            }
        });
    }

    public boolean areCredentialsStored() {
        SharedPreferences sharedPref = application.getSharedPreferences("credentials", MODE_PRIVATE);
        String username = sharedPref.getString("username", null);
        String password = sharedPref.getString("password", null);

        return username != null && password != null;
    }

    public Map<String, Object> getStoredCredentials() {
        Map<String, Object> map = new LinkedHashMap<>();
        SharedPreferences sharedPref = application.getSharedPreferences("credentials", MODE_PRIVATE);
        String username = sharedPref.getString("username", null);
        String password = sharedPref.getString("password", null);

        map.put("username", username);
        map.put("password", password);
        return map;
    }

    public void removeStoredCredentials() {
        SharedPreferences sharedPref = getApplication().getSharedPreferences("credentials", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("username");
        editor.remove("password");
        editor.commit();
    }

    public String getSessionID() {
        String sessionID = msCookieManager.getCookieStore().getCookies().get(0).getValue();
        return sessionID;
    }

    public void clearCookies() {
        msCookieManager.getCookieStore().removeAll();
    }

    //general purpose api request method
    public void sendRequest(String endpointURL, String method, Map<String, Object> params, Map<String, Object> payload, boolean sendPayload, boolean saveCookies, boolean sendCookies, HTTTPCallback callback) {
        executor.execute(() -> {
            JSONObject jObject;
            try {
                String paths[] = endpointURL.split("/", 0);
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https").authority(BuildConfig.API_ADDRESS);
                for (String path : paths) {
                    builder.appendPath(path);
                }
                if (method.equals("GET") && params != null) {
                    for (Map.Entry<String, Object> param : params.entrySet()) {
                        builder.appendQueryParameter(param.getKey(), (String) param.getValue());
                    }
                }
                String fullUrl = builder.build().toString();
                URL url = new URL(fullUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod(method);

                if (method.equals("POST")) {
                    con.setDoOutput(true);
                } else if (method.equals("GET")) {
                    con.setDoOutput(false);
                    con.setDoInput(true);
                }

                if (sendCookies && msCookieManager.getCookieStore().getCookies().size() > 0) {
                    con.setRequestProperty("Cookie", TextUtils.join(";", msCookieManager.getCookieStore().getCookies()));
                }

                if (sendPayload && payload != null) {
                    StringBuilder postData = new StringBuilder();
                    for (Map.Entry<String, Object> param : payload.entrySet()) {
                        if (postData.length() != 0) postData.append('&');
                        postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                        postData.append('=');
                        postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                    }
                    byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);
                    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    con.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                    con.getOutputStream().write(postDataBytes);
                }

                Reader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                for (int c; (c = in.read()) >= 0; )
                    sb.append((char) c);
                in.close();
                String response = sb.toString();
                jObject = new JSONObject(response);
                jObject.put("endpoint", endpointURL);
                System.out.println(response);
                if (saveCookies) {
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
                e.printStackTrace();
                jObject = new JSONObject();

            }
            JSONObject finalJObject = jObject;
            handler.post(() -> {
                callback.onComplete(finalJObject);
            });
        });
    }

}
