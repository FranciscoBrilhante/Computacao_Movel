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

import com.example.market.R;
import com.example.market.data.MarketViewModel;
import com.example.market.databinding.FragmentHomeBinding;
import com.example.market.interfaces.RecyclerViewInterface;
import com.example.market.marketDatabase.Product;
import com.example.market.ui.components.CategorySpinnerAdapter;
import com.example.market.ui.components.ProductListAdapter;

public class HomeFragment extends Fragment implements RecyclerViewInterface, View.OnClickListener {

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

        viewModel.getAllProducts().observe(requireActivity(), products -> {
            adapter.submitList(products);
        });

        // CategorySpinnerAdapter spinnerAdapter= new CategorySpinnerAdapter(getContext());
        //binding.categorySpinner.setAdapter(spinnerAdapter);

        viewModel.getAllCategories().observe(requireActivity(),categories -> {
            //spinnerAdapter.clear();
            //spinnerAdapter.addAll(categories);
        });

        binding.moreButton.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onClick(Product product) {
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();
        NavDirections action = HomeFragmentDirections.actionNavigationHomeToNavigationViewProduct(product.getId());

        navController.navigate(action);
    }


    @Override
    public void onClick(View view) {
        if (view == binding.moreButton) {
            if (actionBarExpanded) {
                binding.moreFiltersView.setVisibility(View.GONE);
                binding.moreButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,null, getResources().getDrawable(R.drawable.chevron_down));
                actionBarExpanded=false;
            } else {
                binding.moreFiltersView.setVisibility(View.VISIBLE);
                binding.moreButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,null, getResources().getDrawable(R.drawable.chevron_up));
                actionBarExpanded=true;
            }

        }
    }
}