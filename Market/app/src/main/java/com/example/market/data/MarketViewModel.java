package com.example.market.data;

import static android.content.Context.MODE_PRIVATE;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import android.app.Application;
import android.content.SharedPreferences;

import com.example.market.marketDatabase.MainRoomDatabase;
import com.example.market.marketDatabase.Product;
import com.example.market.marketDatabase.ProductDao;

import java.util.List;


public class MarketViewModel extends AndroidViewModel {
    private final ProductDao productDao;
    private Application application;
    public MarketViewModel(Application application) {
        super(application);
        this.application=application;
        MainRoomDatabase db= MainRoomDatabase.getDatabase(application);
        productDao=db.productDao();

    }

    public LiveData<List<Product>> getAllProducts() {
        return productDao.getAll();
    }

    public boolean areCredentialsStored(){
        SharedPreferences sharedPref = application.getSharedPreferences("credentials",MODE_PRIVATE);
        String username=sharedPref.getString("username",null);
        String password=sharedPref.getString("password",null);

        return username!=null && password!=null;
    }
}
