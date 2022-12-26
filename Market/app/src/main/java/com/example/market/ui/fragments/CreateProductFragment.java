package com.example.market.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.market.R;
import com.example.market.data.MarketViewModel;
import com.example.market.databinding.FragmentCreateProductBinding;
import com.example.market.databinding.FragmentViewProductBinding;
import com.example.market.ui.components.ProductListAdapter;

public class CreateProductFragment extends Fragment implements View.OnClickListener {
    private FragmentCreateProductBinding binding;
    private MarketViewModel viewModel;
    private ProductListAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(MarketViewModel.class);
        binding = FragmentCreateProductBinding.inflate(inflater, container, false);

        binding.backButton.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        if(view==binding.backButton){
            NavHostFragment navHostFragment =
                    (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
            NavController navController = navHostFragment.getNavController();
            navController.navigateUp();
        }
    }
}
