package com.example.market.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.market.MarketViewModel;
import com.example.market.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

private FragmentNotificationsBinding binding;

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