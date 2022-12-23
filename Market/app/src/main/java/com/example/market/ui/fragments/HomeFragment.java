package com.example.market.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.market.data.LoginViewModel;
import com.example.market.data.MarketViewModel;
import com.example.market.databinding.FragmentHomeBinding;
import com.example.market.databinding.FragmentLoginBinding;
import com.example.market.interfaces.RecyclerViewInterface;
import com.example.market.marketDatabase.Product;
import com.example.market.ui.components.ProductListAdapter;

public class HomeFragment extends Fragment implements RecyclerViewInterface {

    private FragmentHomeBinding binding;
    private MarketViewModel viewModel;
    private ProductListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel =  new ViewModelProvider(this).get(MarketViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        adapter = new ProductListAdapter(new ProductListAdapter.ProductDiff(), this);
        RecyclerView recyclerView=binding.productsList;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.getAllProducts().observe(requireActivity(), products -> {
            adapter.submitList(products);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(Product product) {

    }
}