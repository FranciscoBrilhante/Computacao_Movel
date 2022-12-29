package com.example.market.ui.fragments;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
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
import com.example.market.databinding.FragmentHomeBinding;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.interfaces.RecyclerViewInterface;
import com.example.market.marketDatabase.Category;
import com.example.market.marketDatabase.PriceRange;
import com.example.market.marketDatabase.Product;
import com.example.market.ui.components.CategorySpinnerAdapter;
import com.example.market.ui.components.PriceRangeSpinnerAdapter;
import com.example.market.ui.components.ProductListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements RecyclerViewInterface, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, HTTTPCallback {

    private FragmentHomeBinding binding;
    private MarketViewModel viewModel;
    private ProductListAdapter adapter;
    private boolean actionBarExpanded = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(MarketViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        adapter = new ProductListAdapter(new ProductListAdapter.ProductDiff(), this);
        RecyclerView recyclerView = binding.productsList;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        CategorySpinnerAdapter categorySpinnerAdapter= new CategorySpinnerAdapter(getContext(),new ArrayList<>());
        binding.categorySpinner.setAdapter(categorySpinnerAdapter);

        PriceRangeSpinnerAdapter priceRangeSpinnerAdapter= new PriceRangeSpinnerAdapter(getContext(),initPriceRange());
        binding.priceRangeSpinner.setAdapter(priceRangeSpinnerAdapter);


        viewModel.getAllProducts().observe(requireActivity(), products -> {
            adapter.submitList(products);
        });

        viewModel.getAllCategories().observe(requireActivity(),categories -> {
            categorySpinnerAdapter.clear();
            categorySpinnerAdapter.addAll(categories);
            categorySpinnerAdapter.notifyDataSetChanged();
        });

        binding.moreButton.setOnClickListener(this);
        binding.swipeRefreshLayout.setOnRefreshListener(this);
        return binding.getRoot();
    }

    @Override
    public void onClick(Product product) {
        boolean isOwner= product.getProfileName().equals(viewModel.getStoredCredentials().get("username"));
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();
        NavDirections action = HomeFragmentDirections.actionNavigationHomeToNavigationViewProduct(isOwner,product.getId());

        navController.navigate(action);
    }


    @Override
    public void onClick(View view) {
        if (view == binding.moreButton) {
            if (actionBarExpanded) {
                binding.moreFiltersView.setVisibility(View.GONE);
                binding.moreButton.setImageResource(R.drawable.chevron_down);
                actionBarExpanded=false;
            } else {
                binding.moreFiltersView.setVisibility(View.VISIBLE);
                binding.moreButton.setImageResource(R.drawable.chevron_up);
                actionBarExpanded=true;
            }

        }
    }

    private ArrayList<PriceRange> initPriceRange(){
        ArrayList<PriceRange> array=new ArrayList<>();
        array.add(new PriceRange(null,10.0,getContext()));
        array.add(new PriceRange(10.0,50.0,getContext()));
        array.add(new PriceRange(50.0,100.0,getContext()));
        array.add(new PriceRange(100.0,500.0,getContext()));
        array.add(new PriceRange(500.0,null,getContext()));
        return array;
    }

    @Override
    public void onRefresh() {
        binding.swipeRefreshLayout.setRefreshing(true);
        viewModel.sendRequest("/product/recommended", "GET", null, null, false, false, true, this);
    }

    @Override
    public void onComplete(JSONObject data) {
        String url1 = "/product/recommended";
        try {
            int code = data.getInt("status");
            String endpoint = data.getString("endpoint");
            if (endpoint.equals(url1)) {
                if (code == 200) {
                    ArrayList<Product> products = viewModel.productsFromJSONObject(data);
                    viewModel.addProducts(products);
                    //adapter.submitList(products);
                }
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }
}