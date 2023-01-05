package com.example.market.ui.fragments.admin;

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
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.market.R;
import com.example.market.data.MarketViewModel;
import com.example.market.databinding.FragmentHomeAdminBinding;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.interfaces.RecyclerViewInterface;
import com.example.market.marketDatabase.Product;
import com.example.market.ui.components.adapter.AdminProductListAdapter;
import com.example.market.ui.fragments.main.HomeFragmentDirections;
import com.example.market.utils.ProductDateComparator;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

public class AdminHomeFragment extends Fragment implements RecyclerViewInterface, SwipeRefreshLayout.OnRefreshListener, HTTTPCallback, SearchView.OnQueryTextListener {

    private FragmentHomeAdminBinding binding;
    private MarketViewModel viewModel;
    private AdminProductListAdapter adapter;
    private String textQuery = "";

    private ArrayList<Product> productsReported;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(MarketViewModel.class);
        binding = FragmentHomeAdminBinding.inflate(inflater, container, false);

        adapter = new AdminProductListAdapter(new AdminProductListAdapter.ProductDiff(), this, viewModel);
        adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);

        RecyclerView recyclerView = binding.productsList;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        binding.swipeRefreshLayout.setOnRefreshListener(this);
        binding.searchInput.setOnQueryTextListener(this);

        viewModel.sendRequest("/report/allproductsreported", "GET", null, null, false, false, true, this);
        return binding.getRoot();
    }

    @Override
    public void onClick(Product product) {
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_admin);
        NavController navController = navHostFragment.getNavController();
        NavDirections action = AdminHomeFragmentDirections.actionNavigationHomeToNavigationViewProduct2(product.getId());
        navController.navigate(action);
    }

    @Override
    public void sendMessage(int profileID) {
    }

    @Override
    public void onRefresh() {
        binding.swipeRefreshLayout.setRefreshing(true);
        viewModel.sendRequest("/report/allproductsreported", "GET", null, null, false, false, true, this);
    }

    @Override
    public void onComplete(JSONObject data) {
        String url1 = "/report/allproductsreported";
        try {
            int code = data.getInt("status");
            String endpoint = data.getString("endpoint");
            if (endpoint.equals(url1)) {
                if (code == 200) {
                    productsReported = viewModel.productsFromJSONObject(data);
                    filterProductsAndSubmit(productsReported);
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
        textQuery=newText;
        filterProductsAndSubmit(productsReported);
        return false;
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

}