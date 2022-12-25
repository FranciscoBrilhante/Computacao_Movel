package com.example.market.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.market.R;
import com.example.market.data.MarketViewModel;
import com.example.market.databinding.FragmentItemsBinding;
import com.example.market.databinding.FragmentProfileBinding;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.interfaces.RecyclerViewInterface;
import com.example.market.marketDatabase.Product;
import com.example.market.ui.activities.LoginActivity;
import com.example.market.ui.components.OwnProductListAdapter;
import com.example.market.ui.components.ProductListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ItemsFragment extends Fragment implements RecyclerViewInterface, HTTTPCallback {

    private FragmentItemsBinding binding;
    private MarketViewModel viewModel;
    private OwnProductListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel =new ViewModelProvider(this).get(MarketViewModel.class);
        binding= FragmentItemsBinding.inflate(inflater,container,false);

        adapter = new OwnProductListAdapter(new OwnProductListAdapter.ProductDiff(), this);
        RecyclerView recyclerView=binding.productsList;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.sendRequest("/product/myproducts","GET",null, null,false,false,true,this);
        return binding.getRoot();
    }

    @Override
    public void onClick(Product product) {
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();
        NavDirections action = ItemsFragmentDirections.actionNavigationItemsToNavigationViewProduct(product.getId());

        navController.navigate(action);
    }

    @Override
    public void onComplete(JSONObject data) {
        try {
            String url1="/product/myproducts";
            int code = data.getInt("status");
            String endpoint=data.getString("endpoint");
            if(endpoint.equals(url1)){
                if(code==200){
                    ArrayList<Product> products = processData(data);
                    adapter.submitList(products);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Product> processData(JSONObject data) throws JSONException{
        JSONArray array=data.getJSONArray("products");
        ArrayList<Product> products=new ArrayList<>();
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
                products.add(product);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return products;
    }
}