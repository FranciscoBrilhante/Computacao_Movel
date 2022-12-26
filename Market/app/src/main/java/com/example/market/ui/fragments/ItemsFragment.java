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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class ItemsFragment extends Fragment implements RecyclerViewInterface, HTTTPCallback, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private FragmentItemsBinding binding;
    private MarketViewModel viewModel;
    private OwnProductListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(MarketViewModel.class);
        binding = FragmentItemsBinding.inflate(inflater, container, false);

        //configure costume recyclerview for products
        adapter = new OwnProductListAdapter(new OwnProductListAdapter.ProductDiff(), this);
        RecyclerView recyclerView = binding.productsList;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.createButton.setOnClickListener(this);
        binding.swipeRefreshLayout.setOnRefreshListener(this);
        //request own user products to populate screen
        viewModel.sendRequest("/product/myproducts", "GET", null, null, false, false, true, this);
        return binding.getRoot();
    }

    //user clicked on a product, redirect to view product screen
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
            String url1 = "/product/myproducts";
            int code = data.getInt("status");
            String endpoint = data.getString("endpoint");
            if (endpoint.equals(url1)) {
                if (code == 200) {
                    ArrayList<Product> products = viewModel.productsFromJSONObject(data);
                    adapter.submitList(products);
                }
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    //user wants to create new product
    @Override
    public void onClick(View view) {
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();
        NavDirections action = ItemsFragmentDirections.actionNavigationItemsToNavigationCreateProduct();

        navController.navigate(action);
    }

    @Override
    public void onRefresh() {
        binding.swipeRefreshLayout.setRefreshing(true);
        viewModel.sendRequest("/product/myproducts", "GET", null, null, false, false, true, this);
    }
}