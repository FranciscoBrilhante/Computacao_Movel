package com.example.market.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.market.data.MarketViewModel;
import com.example.market.databinding.FragmentCreateProductBinding;
import com.example.market.databinding.FragmentViewProductBinding;
import com.example.market.ui.components.ProductListAdapter;

public class CreateProductFragment extends Fragment {
    private FragmentCreateProductBinding binding;
    private MarketViewModel viewModel;
    private ProductListAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(MarketViewModel.class);
        binding = FragmentCreateProductBinding.inflate(inflater, container, false);
        
        return binding.getRoot();
    }
}
