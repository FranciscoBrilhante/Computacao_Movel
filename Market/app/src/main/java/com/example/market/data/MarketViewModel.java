package com.example.market.data;

import static android.content.Context.MODE_PRIVATE;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.example.market.BuildConfig;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.marketDatabase.Category;
import com.example.market.marketDatabase.CategoryDao;
import com.example.market.marketDatabase.Contact;
import com.example.market.marketDatabase.ContactDao;
import com.example.market.marketDatabase.Image;
import com.example.market.marketDatabase.MainRoomDatabase;
import com.example.market.marketDatabase.Message;
import com.example.market.marketDatabase.MessageDao;
import com.example.market.marketDatabase.PriceRange;
import com.example.market.marketDatabase.Product;
import com.example.market.marketDatabase.ProductDao;
import com.example.market.marketDatabase.Report;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MarketViewModel extends AndroidViewModel {
    private Application application;
    private final MainRoomDatabase db;
    private final ProductDao productDao;
    private final CategoryDao categoryDao;
    private final ContactDao contactDao;
    private final MessageDao messageDao;

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static java.net.CookieManager msCookieManager = new java.net.CookieManager();

    public MarketViewModel(Application application) {
        super(application);
        this.application = application;
        db = MainRoomDatabase.getDatabase(application);
        productDao = db.productDao();
        categoryDao = db.categoryDao();
        contactDao = db.contactDao();
        messageDao = db.messageDao();
    }

    public LiveData<List<Product>> getAllProducts() {
        return productDao.getAll();
    }

    public LiveData<List<Category>> getAllCategories() {
        return categoryDao.getAll();
    }

    public LiveData<List<Contact>> getAllContacts() {
        return contactDao.getAll();
    }

    public LiveData<List<Message>> getAllMessages() {
        return messageDao.getAll();
    }

    public LiveData<List<Message>> getMessagesWithUser(int id) {
        return messageDao.getMessagesWithUser(id);
    }

    public LiveData<List<Product>> getProductsByProfileID(int id){
        return productDao.getAllByProfileID(id);
    }

    public void addProducts(ArrayList<Product> products) {
        MainRoomDatabase.databaseWriteExecutor.execute(() -> {
            for (Product product : products) {
                productDao.insert(product);
            }
        });
    }

    public void addCategories(ArrayList<Category> categories) {
        MainRoomDatabase.databaseWriteExecutor.execute(() -> {
            for (Category category : categories) {
                categoryDao.insert(category);
            }
        });
    }

    public void addContacts(ArrayList<Contact> contacts) {
        MainRoomDatabase.databaseWriteExecutor.execute(() -> {
            for (Contact contact : contacts) {
                contactDao.insert(contact);
            }
        });
    }

    public void addMessages(ArrayList<Message> messages) {
        MainRoomDatabase.databaseWriteExecutor.execute(() -> {
            for (Message message : messages) {
                messageDao.insert(message);
            }
        });
    }

    public void deleteProductByID(int id) {
        MainRoomDatabase.databaseWriteExecutor.execute(() -> {
            productDao.deleteByID(id);
        });
    }

    public void deleteAllMessages() {
        MainRoomDatabase.databaseWriteExecutor.execute(()->{
            messageDao.deleteAll();
        });
    }

    public void deleteAllProducts() {
        MainRoomDatabase.databaseWriteExecutor.execute(()->{
            productDao.deleteAll();
        });

    }

    public void deleteAllContacts() {
        MainRoomDatabase.databaseWriteExecutor.execute(()->{
            contactDao.deleteAll();
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
        Boolean isAdmin = sharedPref.getBoolean("is_admin", false);
        int profileID = sharedPref.getInt("profile_id", -1);
        map.put("username", username);
        map.put("password", password);
        map.put("profile_id", profileID);
        map.put("is_admin", isAdmin);
        return map;
    }

    public void removeStoredCredentials() {
        SharedPreferences sharedPref = getApplication().getSharedPreferences("credentials", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("username");
        editor.remove("password");
        editor.remove("profile_id");
        editor.remove("is_admin");
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
                System.out.println(endpointURL + response);
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

    public ArrayList<Product> productsFromJSONObject(JSONObject data) throws JSONException, ParseException {
        JSONArray array = data.getJSONArray("products");
        ArrayList<Product> products = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject elem = array.getJSONObject(i);
            int id = elem.getInt("id");
            String title = elem.getString("title");
            String description = elem.getString("description");
            int category = elem.getInt("category");
            Double price = elem.getDouble("price");
            int profile = elem.getInt("profile");
            String date = elem.getString("date");

            String categoryName = elem.getString("category_name");
            String categoryNamePT = elem.getString("category_name_pt");
            String profileName = elem.getString("profile_name");
            String profileLocation = elem.getString("profile_location");

            JSONArray images = elem.getJSONArray("images");
            ArrayList<String> imagesURL = new ArrayList<>();
            for (int j = 0; j < images.length(); j++) {
                imagesURL.add((String) images.get(j));
            }

            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));

            Double rating = elem.getDouble("rating");
            Date d = format.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            Product product = new Product(id, title, description, category, price, profile, calendar, imagesURL, categoryName,categoryNamePT, profileName, rating, profileLocation);
            products.add(product);

        }
        return products;
    }

    public ArrayList<Category> categoriesFromJSONObject(JSONObject data) throws JSONException {
        JSONArray array = data.getJSONArray("categories");
        ArrayList<Category> categories = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject elem = array.getJSONObject(i);
            int id = elem.getInt("id");
            String name = elem.getString("name");
            String namePT = elem.getString("name_pt");
            Category category = new Category(id, name, namePT);
            categories.add(category);
        }
        return categories;
    }

    public ArrayList<Message> messagesFromJSONObject(JSONObject data) throws JSONException, ParseException {
        ArrayList<Message> messages = new ArrayList<>();
        int myProfileID = (int) getStoredCredentials().get("profile_id");

        JSONArray array = data.getJSONArray("received");
        for (int i = 0; i < array.length(); i++) {
            JSONObject elem = array.getJSONObject(i);

            int messageID = elem.getInt("message_id");
            int profileID = elem.getInt("profile_id");
            String content = elem.getString("content");
            String timestamp = elem.getString("timestamp");

            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date d = format.parse(timestamp);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);

            Message message = new Message(messageID,content, calendar, profileID, myProfileID);
            messages.add(message);
        }
        array = data.getJSONArray("sent");
        for (int i = 0; i < array.length(); i++) {
            JSONObject elem = array.getJSONObject(i);

            int messageID = elem.getInt("message_id");
            int profileID = elem.getInt("profile_id");
            String content = elem.getString("content");
            String timestamp = elem.getString("timestamp");

            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date d = format.parse(timestamp);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);

            Message message = new Message(messageID,content, calendar, myProfileID, profileID);
            messages.add(message);
        }
        return messages;
    }

    public ArrayList<Report> reportsFromJSONObject(JSONObject data) throws JSONException {
        JSONArray array = data.getJSONArray("reports");
        ArrayList<Report> reports = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject elem = array.getJSONObject(i);
            String reason = elem.getString("reason");
            String explain = elem.getString("explain");
            int profileID= elem.getInt("profile_id");
            int productID= elem.getInt("product_id");
            int reportID=elem.getInt("report_id");
            reports.add(new Report(reason,explain,productID,profileID,reportID));
        }
        return reports;
    }

    public void sendProductPhotos(int productID, ArrayList<Image> productImages) throws IOException {
        for (Image image : productImages) {
            File file = new File(image.getPath());

            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("file");
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("photo", "image.jpg",
                            RequestBody.create(file, MediaType.parse("file")))
                    .addFormDataPart("product_id", Integer.toString(productID))
                    .build();
            Request request = new Request.Builder()
                    .url("https://" + BuildConfig.API_ADDRESS + "/product/addphoto")
                    .method("POST", body)
                    .addHeader("Cookie", "sessionid=" + this.getSessionID())
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println("Error uploading photo");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    System.out.println(response);
                }
            });
        }
    }

    public void updateProfilePic(Image pic, HTTTPCallback callback) throws IOException, JSONException {
        File file = new File(pic.getPath());
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("file");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("profile_photo", "image.jpg",
                        RequestBody.create(file, MediaType.parse("file")))
                .build();
        Request request = new Request.Builder()
                .url("https://" + BuildConfig.API_ADDRESS + "/profile/setphoto")
                .method("POST", body)
                .addHeader("Cookie", "sessionid=" + this.getSessionID())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                JSONObject data = new JSONObject();
                try {
                    data.put("status", 400);
                    data.put("endpoint", "/profile/setphoto");
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                callback.onComplete(data);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject data = new JSONObject();
                try {
                    data.put("status", 200);
                    data.put("endpoint", "/profile/setphoto");
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                callback.onComplete(data);
            }
        });
    }

    public ArrayList<Contact> contactsFromJSONObject(JSONObject data) throws JSONException, ParseException {
        JSONArray array = data.getJSONArray("contacts");
        ArrayList<Contact> contacts = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject elem = array.getJSONObject(i);
            int id = elem.getInt("profile_id");
            String name = elem.getString("profile_name");
            String imageURL = elem.getString("profile_image");
            String lastMessage = elem.getString("last_message");

            String timestamp = elem.getString("last_message_timestamp");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date d = format.parse(timestamp);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);

            Contact contact = new Contact(id, imageURL, name, lastMessage, calendar);
            contacts.add(contact);
        }
        return contacts;
    }

    public ArrayList<PriceRange> getDefaultPriceRanges(Context context) {
        ArrayList<PriceRange> array = new ArrayList<>();
        array.add(new PriceRange(null, null, context));
        array.add(new PriceRange(null, 10.0, context));
        array.add(new PriceRange(10.0, 50.0, context));
        array.add(new PriceRange(50.0, 100.0, context));
        array.add(new PriceRange(100.0, 500.0, context));
        array.add(new PriceRange(500.0, null, context));
        return array;
    }
}
