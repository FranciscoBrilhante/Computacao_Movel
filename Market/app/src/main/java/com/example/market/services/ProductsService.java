package com.example.market.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.market.BuildConfig;
import com.example.market.data.MarketViewModel;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.marketDatabase.MainRoomDatabase;
import com.example.market.marketDatabase.Product;
import com.example.market.marketDatabase.ProductDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ProductsService extends Service {
    Thread thread;
    String sessionID;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
    }


    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        Bundle bundle = intent.getExtras();
        sessionID = bundle.getString("sessionID");
        if(thread==null){
            thread=new Thread(routine);
            thread.start();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        thread.interrupt();
    }


    private Runnable routine =new Runnable() {
        @Override
        public void run() {
            System.out.println("started running");
            MainRoomDatabase db= MainRoomDatabase.getDatabase(getApplication());
            ProductDao productDao=db.productDao();
            try {
                while (true){
                    JSONObject data= getProductsRequest(sessionID);
                    processProductsResponse(data, productDao);
                    Thread.sleep(60_000);
                }

            } catch (InterruptedException | JSONException e) {
                System.out.println("--------------------Products Thread has been interrupted-----------------");
                Thread.currentThread().interrupt();

            }
        }
    };

    public JSONObject getProductsRequest(String sessionID){
        try {
            JSONObject jObject;

            URL url=new URL("https://"+ BuildConfig.API_ADDRESS+"/product/recommended");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setRequestProperty("Cookie","sessionid="+sessionID+";");
            Reader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int c; (c = in.read()) >= 0;)
                sb.append((char)c);
            in.close();
            con.disconnect();
            String response = sb.toString();
            jObject = new JSONObject(response);
            return  jObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void processProductsResponse(JSONObject data, ProductDao productDao) throws JSONException {
        if(data!=null && (Integer) data.get("status")==200){
            productDao.deleteAll();
            JSONArray array=data.getJSONArray("products");
            for (int i = 0 ; i < array.length(); i++) {
                JSONObject elem=array.getJSONObject(i);

                int id=elem.getInt("id");
                String title=elem.getString("title");
                String description=elem.getString("description");
                int category=elem.getInt("category");
                Double price=elem.getDouble("price");
                int profile=elem.getInt("profile");
                String date=elem.getString("date");

                String categoryName=elem.getString("category_name");
                String profileName=elem.getString("profile_name");

                JSONArray images=elem.getJSONArray("images");
                ArrayList<String> imagesURL=new ArrayList<>();
                for (int j = 0 ; j < images.length(); j++) {
                    imagesURL.add((String) images.get(j));
                }

                SimpleDateFormat format = new SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                format.setTimeZone(TimeZone.getTimeZone("UTC"));

                Double rating=elem.getDouble("rating");
                try {
                    Date d = format.parse(date);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(d);
                    Product product=new Product(id,title,description,category,price,profile,calendar,imagesURL,categoryName,profileName,rating);
                    productDao.insert(product);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}





