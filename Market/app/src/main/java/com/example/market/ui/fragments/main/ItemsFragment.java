package com.example.market.ui.fragments.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.market.R;
import com.example.market.data.MarketViewModel;
import com.example.market.databinding.FragmentItemsBinding;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.interfaces.RecyclerViewInterface;
import com.example.market.marketDatabase.Product;
import com.example.market.ui.components.adapter.OwnProductListAdapter;
import com.example.market.utils.ProductDateComparator;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

public class ItemsFragment extends Fragment implements RecyclerViewInterface, HTTTPCallback, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    private FragmentItemsBinding binding;
    private MarketViewModel viewModel;
    private OwnProductListAdapter adapter;

    private ArrayList<Product> ownProducts;
    private String textQuery="";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MarketViewModel.class);
        adapter = new OwnProductListAdapter(new OwnProductListAdapter.ProductDiff(), this);
        adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);

        viewModel.getProductsByProfileID((int) viewModel.getStoredCredentials().get("profile_id")).observe(requireActivity(),products -> {
            this.ownProducts=new ArrayList<>(products);
            filterProductsAndSubmit(ownProducts);
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentItemsBinding.inflate(inflater, container, false);
        RecyclerView recyclerView = binding.productsList;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        binding.createButton.setOnClickListener(this);
        binding.swipeRefreshLayout.setOnRefreshListener(this);
        binding.searchInput.setOnQueryTextListener(this);
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
    public void delete(Product product) {

    }

    @Override
    public void sendMessage(int profileID) {
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
                    this.ownProducts=products;
                    viewModel.addProducts(ownProducts);
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

    private void filterProductsAndSubmit(ArrayList<Product> products){
        if(products==null){
            return;
        }
        ArrayList<Product> aux=new ArrayList<>();
        products.sort(new ProductDateComparator());
        if (textQuery!=null){
            for(Product product:products){
                if(product.getTitle().toLowerCase().contains(textQuery.toLowerCase())){
                    aux.add(product);
                }
            }
        }
        else{
            aux=new ArrayList<>(products);
        }
        adapter.submitList(aux);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        textQuery=newText;
        filterProductsAndSubmit(ownProducts);
        return false;
    }
}