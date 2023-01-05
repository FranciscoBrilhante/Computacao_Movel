package com.example.market.ui.fragments.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import androidx.recyclerview.widget.GridLayoutManager;
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
import com.example.market.ui.components.adapter.CategorySpinnerAdapter;
import com.example.market.ui.components.adapter.PriceRangeSpinnerAdapter;
import com.example.market.ui.components.adapter.ProductListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements RecyclerViewInterface, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, HTTTPCallback, SearchView.OnQueryTextListener {

    private FragmentHomeBinding binding;
    private MarketViewModel viewModel;
    private ProductListAdapter adapter;
    private boolean actionBarExpanded = false;

    private String queryText = "";
    private Category categorySelected;
    private PriceRange priceRangeSelected;

    private CategorySpinnerAdapter categorySpinnerAdapter;
    private PriceRangeSpinnerAdapter priceRangeSpinnerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MarketViewModel.class);
        adapter = new ProductListAdapter(new ProductListAdapter.ProductDiff(), this);
        adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);

        categorySpinnerAdapter = new CategorySpinnerAdapter(getContext(), new ArrayList<>());
        priceRangeSpinnerAdapter = new PriceRangeSpinnerAdapter(getContext(), initPriceRange());

        viewModel.getAllCategories().observe(requireActivity(), categories -> {
            categorySpinnerAdapter.clear();
            categorySpinnerAdapter.add(new Category(-1, "Any Category", "Qualquer Categoria"));
            categorySpinnerAdapter.addAll(categories);
            categorySpinnerAdapter.notifyDataSetChanged();
        });

        viewModel.getAllProducts().observe(requireActivity(), products -> {
            if (queryText.equals("") && (categorySelected == null || categorySelected.getId() == -1) && (priceRangeSelected == null || (priceRangeSelected.getMaxPrice() == null && priceRangeSelected.getMinPrice() == null))) {
                adapter.submitList(products);
            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        RecyclerView recyclerView = binding.productsList;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        binding.categorySpinner.setAdapter(categorySpinnerAdapter);
        binding.priceRangeSpinner.setAdapter(priceRangeSpinnerAdapter);


        binding.moreButton.setOnClickListener(this);
        binding.swipeRefreshLayout.setOnRefreshListener(this);
        binding.searchInput.setOnQueryTextListener(this);
        binding.priceRangeSpinner.setOnItemSelectedListener(priceRangeSpinnerListener);
        binding.categorySpinner.setOnItemSelectedListener(categorySpinnerListener);
        return binding.getRoot();
    }

    @Override
    public void onClick(Product product) {
        boolean isOwner = product.getProfileName().equals(viewModel.getStoredCredentials().get("username"));
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();
        NavDirections action = HomeFragmentDirections.actionNavigationHomeToNavigationViewProduct(isOwner, product.getId());

        navController.navigate(action);
    }

    @Override
    public void delete(Product product) {

    }

    @Override
    public void sendMessage(int profileID) {
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();
        NavDirections action = HomeFragmentDirections.actionNavigationHomeToUserChatFragment(profileID);
        navController.navigate(action);
    }


    @Override
    public void onClick(View view) {
        if (view == binding.moreButton) {
            if (actionBarExpanded) {
                binding.moreFiltersView.setVisibility(View.GONE);
                binding.moreButton.setImageResource(R.drawable.chevron_down);
                actionBarExpanded = false;
            } else {
                binding.moreFiltersView.setVisibility(View.VISIBLE);
                binding.moreButton.setImageResource(R.drawable.chevron_up);
                actionBarExpanded = true;
            }

        }
    }

    private ArrayList<PriceRange> initPriceRange() {
        ArrayList<PriceRange> array = new ArrayList<>();
        array.add(new PriceRange(null, null, getActivity().getApplicationContext()));
        array.add(new PriceRange(null, 10.0, getContext()));
        array.add(new PriceRange(10.0, 50.0, getContext()));
        array.add(new PriceRange(50.0, 100.0, getContext()));
        array.add(new PriceRange(100.0, 500.0, getContext()));
        array.add(new PriceRange(500.0, null, getContext()));
        return array;
    }

    @Override
    public void onRefresh() {
        binding.swipeRefreshLayout.setRefreshing(true);
        sendFilterRequest();
    }

    @Override
    public void onComplete(JSONObject data) {
        String url1 = "/product/recommended";
        String url2 = "/product/filter";
        try {
            int code = data.getInt("status");
            String endpoint = data.getString("endpoint");
            if (endpoint.equals(url1)) {
                if (code == 200) {
                    ArrayList<Product> products = viewModel.productsFromJSONObject(data);
                    viewModel.addProducts(products);
                    adapter.submitList(products);
                }
                binding.swipeRefreshLayout.setRefreshing(false);
            }
            if (endpoint.equals(url2)) {
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        this.queryText = newText;
        sendFilterRequest();
        return true;
    }

    private AdapterView.OnItemSelectedListener categorySpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Category aux=categorySelected;
            categorySelected = (Category) binding.categorySpinner.getSelectedItem();
            if(aux!=null){
                sendFilterRequest();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private AdapterView.OnItemSelectedListener priceRangeSpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            PriceRange aux=priceRangeSelected;
            priceRangeSelected = (PriceRange) binding.priceRangeSpinner.getSelectedItem();
            if(aux!=null){
                sendFilterRequest();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private void sendFilterRequest() {
        if (queryText.equals("") && (categorySelected == null || categorySelected.getId() == -1) && (priceRangeSelected == null || (priceRangeSelected.getMaxPrice() == null && priceRangeSelected.getMinPrice() == null))) {
            viewModel.sendRequest("/product/recommended", "GET", null, null, false, false, true, this);
            return;
        }

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("searchText", queryText);
        params.put("page", Integer.toString(1));
        System.out.println(categorySelected);
        if (categorySelected == null || categorySelected.getId() == -1) {
            params.put("category", Integer.toString(-1));
        } else {
            params.put("category", Integer.toString(categorySelected.getId()));
        }
        if (priceRangeSelected == null) {
            params.put("minPrice", Integer.toString(-1));
            params.put("maxPrice", Integer.toString(-1));
        } else {
            if (priceRangeSelected.getMinPrice() == null)
                params.put("minPrice", Integer.toString(-1));
            else params.put("minPrice", Double.toString(priceRangeSelected.getMinPrice()));

            if (priceRangeSelected.getMaxPrice() == null)
                params.put("maxPrice", Integer.toString(-1));
            else params.put("maxPrice", Double.toString(priceRangeSelected.getMaxPrice()));
        }

        System.out.println(params);
        viewModel.sendRequest("/product/filter", "GET", params, null, false, false, true, this);
    }
}