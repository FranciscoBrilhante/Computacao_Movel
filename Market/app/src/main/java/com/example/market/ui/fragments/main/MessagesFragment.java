package com.example.market.ui.fragments.main;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.market.R;
import com.example.market.data.MarketViewModel;
import com.example.market.databinding.FragmentMessagesBinding;
import com.example.market.interfaces.ContactRecyclerViewInterface;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.marketDatabase.Contact;
import com.example.market.ui.components.adapter.ContactListAdapter;
import com.example.market.utils.ContactComparator;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

public class MessagesFragment extends Fragment implements ContactRecyclerViewInterface, SwipeRefreshLayout.OnRefreshListener, HTTTPCallback, SearchView.OnQueryTextListener {
    private FragmentMessagesBinding binding;
    private MarketViewModel viewModel;
    private ContactListAdapter adapter;
    private ArrayList<Contact> ownContacts;
    private String textQuery;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(MarketViewModel.class);
        binding = FragmentMessagesBinding.inflate(inflater, container, false);

        adapter = new ContactListAdapter(new ContactListAdapter.ContactDiff(), this);
        adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        adapter.setHasStableIds(true); //prevent blinking on refresh (combined with getItemID inside adapter class

        RecyclerView recyclerView = binding.messagesList;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.getAllContacts().observe(requireActivity(), contacts -> {
            ownContacts=new ArrayList<>(contacts);
            filterContactsAndSubmit(ownContacts);
        });

        binding.searchInput.setOnQueryTextListener(this);
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
                    ownContacts=contacts;
                    filterContactsAndSubmit(ownContacts);
                }
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void filterContactsAndSubmit(ArrayList<Contact> contacts){
        if(contacts==null){
            return;
        }
        ArrayList<Contact> aux=new ArrayList<>();
        contacts.sort(new ContactComparator());
        if (textQuery!=null){
            for(Contact contact:contacts){
                if(contact.getProfileName().toLowerCase().contains(textQuery.toLowerCase())){
                    aux.add(contact);
                }
            }
        }
        else{
            aux=new ArrayList<>(contacts);
        }
        adapter.submitList(aux);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        textQuery=newText;
        filterContactsAndSubmit(ownContacts);
        return false;
    }
}
