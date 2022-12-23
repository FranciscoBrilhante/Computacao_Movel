package com.example.market.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.market.data.MarketViewModel;
import com.example.market.databinding.FragmentItemsBinding;

public class ItemsFragment extends Fragment {

    private FragmentItemsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MarketViewModel marketViewModel =
                new ViewModelProvider(this).get(MarketViewModel.class);
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}