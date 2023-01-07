package com.example.market.ui.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.market.utils.ProductDateComparator;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class AdminHomeFragment extends Fragment implements RecyclerViewInterface, SwipeRefreshLayout.OnRefreshListener, HTTTPCallback, SearchView.OnQueryTextListener {

    private FragmentHomeAdminBinding binding;
    private MarketViewModel viewModel;
    private AdminProductListAdapter adapter;
    private String textQuery = "";

    private ArrayList<Product> productsReported;

    private TextView emptyView;
    private RecyclerView recyclerView;

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

        emptyView = binding.emptyViewAdmin;

        recyclerView = binding.productsList;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        binding.swipeRefreshLayout.setOnRefreshListener(this);
        binding.searchInput.setOnQueryTextListener(this);

        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);

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
    public void delete(Product product) {
        Map<String,Object> params=new LinkedHashMap<>();
        params.put("product_id",Integer.toString(product.getId()));
        viewModel.sendRequest("/product/delete", "GET", params, null, false, false, true, this);
        viewModel.deleteProductByID(product.getId());
    }

    @Override
    public void sendMessage(int profileID) {
    }

    @Override
    public void report(Product product) {

    }

    @Override
    public void onRefresh() {
        binding.swipeRefreshLayout.setRefreshing(true);
        viewModel.sendRequest("/report/allproductsreported", "GET", null, null, false, false, true, this);
    }

    @Override
    public void onComplete(JSONObject data) {
        String url1 = "/report/allproductsreported";
        String url2 = "/product/delete";
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
            if (endpoint.equals(url2)) {
                if (code == 200) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.successfull_deletion_product_message, Toast.LENGTH_SHORT).show();
                    viewModel.sendRequest("/report/allproductsreported", "GET", null, null, false, false, true, this);
                }
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

        if (!aux.isEmpty()) {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
        else {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }

        adapter.submitList(aux);
    }

}