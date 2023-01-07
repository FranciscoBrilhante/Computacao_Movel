package com.example.market.ui.fragments.main;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
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
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment implements RecyclerViewInterface, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, HTTTPCallback, SearchView.OnQueryTextListener {

    private FragmentHomeBinding binding;
    private MarketViewModel viewModel;
    private ProductListAdapter adapter;
    private boolean actionBarExpanded = false;

    private String queryText = "";
    private Category categorySelected;
    private PriceRange priceRangeSelected;
    private ArrayList<Product> products;

    private CategorySpinnerAdapter categorySpinnerAdapter;
    private PriceRangeSpinnerAdapter priceRangeSpinnerAdapter;

    private TextView emptyView;
    private RecyclerView recyclerView;

    private FrameLayout d4rkFrame;
    private Product productToReport;

    private int fragId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MarketViewModel.class);
        adapter = new ProductListAdapter(new ProductListAdapter.ProductDiff(), this);
        adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);

        categorySpinnerAdapter = new CategorySpinnerAdapter(getContext(), new ArrayList<>());
        priceRangeSpinnerAdapter = new PriceRangeSpinnerAdapter(getContext(), viewModel.getDefaultPriceRanges(getContext()));

        viewModel.getAllCategories().observe(requireActivity(),categories -> {
            categorySpinnerAdapter.clear();
            categorySpinnerAdapter.add(new Category(-1, "Any Category", "Qualquer Categoria"));
            categorySpinnerAdapter.addAll(categories);
            categorySpinnerAdapter.notifyDataSetChanged();
        });

        viewModel.getAllProducts().observe(requireActivity(), products -> {
            this.products=new ArrayList<>(products);
            filterProductsAndSubmit(this.products);
        });

        if ((Boolean) viewModel.getStoredCredentials().get("is_admin")) fragId = R.id.nav_host_fragment_activity_admin;
        else fragId = R.id.nav_host_fragment_activity_main;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        emptyView = binding.emptyViewHome;

        recyclerView = binding.productsList;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        d4rkFrame = binding.d4rkFrame;

        binding.categorySpinner.setAdapter(categorySpinnerAdapter);
        binding.priceRangeSpinner.setAdapter(priceRangeSpinnerAdapter);

        binding.moreButton.setOnClickListener(this);
        binding.swipeRefreshLayout.setOnRefreshListener(this);
        binding.searchInput.setOnQueryTextListener(this);
        binding.closeReportDialog.setOnClickListener(this);
        binding.confirmReportButton.setOnClickListener(this);
        binding.priceRangeSpinner.setOnItemSelectedListener(priceRangeSpinnerListener);
        binding.categorySpinner.setOnItemSelectedListener(categorySpinnerListener);

        binding.reportDialog.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);

        return binding.getRoot();
    }

    //go to product page
    @Override
    public void onClick(Product product) {
        boolean isOwner = product.getProfileName().equals(viewModel.getStoredCredentials().get("username"));

        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(fragId);
        NavController navController = navHostFragment.getNavController();


        NavDirections action = HomeFragmentDirections.actionNavigationHomeToNavigationViewProduct(isOwner, product.getId());
        navController.navigate(action);
    }

    //user clicked in delete product in product options
    @Override
    public void delete(Product product) {

    }
    //user clicked in send message in product options
    @Override
    public void sendMessage(int profileID) {

        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(fragId);
        NavController navController = navHostFragment.getNavController();

        NavDirections action = HomeFragmentDirections.actionNavigationHomeToUserChatFragment(profileID);
        navController.navigate(action);
    }

    //user wants to report a product
    @Override
    public void report(Product product) {
        binding.reportDialog.setVisibility(View.VISIBLE);
        d4rkFrame.setForeground(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.hide_bg)));
        productToReport=product;
    }

    //user clicked in arrow to show more filter options
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
        }else if(view==binding.closeReportDialog){
            binding.reportDialog.setVisibility(View.GONE);
            binding.reasonRadioGroup.clearCheck();
            binding.explainInput.setText("");
            d4rkFrame.setForeground(null);
            productToReport=null;
        }else if(view==binding.confirmReportButton){
            RadioButton buttonSelected=binding.getRoot().findViewById(binding.reasonRadioGroup.getCheckedRadioButtonId());
            if(buttonSelected!=null && productToReport!=null){
                String reason=buttonSelected.getText().toString();
                String explain = binding.explainInput.getText().toString();
                Map<String,Object> params=new LinkedHashMap<>();
                params.put("product_id",productToReport.getId());
                params.put("reason",reason);
                params.put("explain",explain);
                viewModel.sendRequest("/report/add", "POST", null, params, true, false, true, this);
                binding.reportDialog.setVisibility(View.GONE);
                d4rkFrame.setForeground(null);
            }
            if(buttonSelected==null){
                Toast.makeText(getActivity().getApplicationContext(), R.string.reason_required, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onComplete(JSONObject data) {
        String url1 = "/product/recommended";
        String url2 = "/report/add";
        try {
            int code = data.getInt("status");
            String endpoint = data.getString("endpoint");
            if (endpoint.equals(url1)) { // /product/recommended
                if (code == 200) {
                    ArrayList<Product> products = viewModel.productsFromJSONObject(data);
                    viewModel.addProducts(products);
                }
                binding.swipeRefreshLayout.setRefreshing(false);
            }
            if (endpoint.equals(url2)) { // /product/recommended
                if (code == 200) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.report_success_message, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void filterProductsAndSubmit(ArrayList<Product> productsToFilter){
        ArrayList<Product> aux=new ArrayList<>();
        if (productsToFilter != null) {
            for(Product product: productsToFilter){
                boolean toAdd=true;
                if(!product.getTitle().toLowerCase(Locale.ROOT).contains(queryText.toLowerCase(Locale.ROOT))){
                    toAdd=false;
                }
                if(priceRangeSelected!=null){
                    if(priceRangeSelected.getMaxPrice()!=null && priceRangeSelected.getMaxPrice()<product.getPrice()){
                        toAdd=false;
                    }
                    if(priceRangeSelected.getMinPrice()!=null && priceRangeSelected.getMinPrice()>product.getPrice()){
                        toAdd=false;
                    }
                }
                if(categorySelected!=null && categorySelected.getId()!=-1 && categorySelected.getId()!=product.getCategory()){
                    toAdd=false;
                }

                if(toAdd){
                    aux.add(product);
                }
            }
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

    //user forced page refresh
    @Override
    public void onRefresh() {
        binding.swipeRefreshLayout.setRefreshing(true);
        viewModel.sendRequest("/product/recommended", "GET", null, null, false, false, true, this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {//query text changed
        this.queryText = newText;
        filterProductsAndSubmit(this.products);
        return true;
    }

    //category selected changed
    private AdapterView.OnItemSelectedListener categorySpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            categorySelected = (Category) binding.categorySpinner.getSelectedItem();
            filterProductsAndSubmit(HomeFragment.this.products);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    //price range selected changed
    private AdapterView.OnItemSelectedListener priceRangeSpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            priceRangeSelected = (PriceRange) binding.priceRangeSpinner.getSelectedItem();
            filterProductsAndSubmit(HomeFragment.this.products);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
}