package com.example.market.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.market.R;
import com.example.market.data.MarketViewModel;
import com.example.market.databinding.FragmentMessagesBinding;
import com.example.market.interfaces.ContactRecyclerViewInterface;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.marketDatabase.Contact;
import com.example.market.marketDatabase.Product;
import com.example.market.ui.components.ContactListAdapter;
import com.example.market.ui.components.ProductListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

public class MessagesFragment extends Fragment implements ContactRecyclerViewInterface, SwipeRefreshLayout.OnRefreshListener, HTTTPCallback {
    private FragmentMessagesBinding binding;
    private MarketViewModel viewModel;
    private ContactListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(MarketViewModel.class);
        binding = FragmentMessagesBinding.inflate(inflater, container, false);

        adapter = new ContactListAdapter(new ContactListAdapter.ContactDiff(), this);
        RecyclerView recyclerView = binding.messagesList;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.getAllContacts().observe(requireActivity(), contacts -> {
            adapter.submitList(contacts);
        });

        binding.swipeRefreshLayout.setOnRefreshListener(this);
        viewModel.sendRequest("/message/users","GET",null,null,false,false,true,this);
        return binding.getRoot();
    }

    @Override
    public void onClick(int profileID) {
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();
        NavDirections action = MessagesFragmentDirections.actionNavigationMessagesToUserChatFragment(profileID);
        navController.navigate(action);
    }

    @Override
    public void onRefresh() {
        binding.swipeRefreshLayout.setRefreshing(true);
        viewModel.sendRequest("/message/users","GET",null,null,false,false,true,this);
    }

    @Override
    public void onComplete(JSONObject data) {
        String url1 = "/message/users";
        try {
            int code = data.getInt("status");
            String endpoint = data.getString("endpoint");
            if (endpoint.equals(url1)) {
                if (code == 200) {
                    ArrayList<Contact> contacts = viewModel.contactsFromJSONObject(data);
                    viewModel.addContacts(contacts);
                }
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
