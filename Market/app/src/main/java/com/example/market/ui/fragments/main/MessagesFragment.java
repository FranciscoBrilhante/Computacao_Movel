package com.example.market.ui.fragments.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    private RecyclerView recyclerView;
    private TextView emptyView;

    private int fragId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MarketViewModel.class);

        adapter = new ContactListAdapter(new ContactListAdapter.ContactDiff(), this);
        adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        adapter.setHasStableIds(true); //prevent blinking on refresh (combined with getItemID inside adapter class

        viewModel.getAllContacts().observe(requireActivity(), contacts -> {
            this.ownContacts=new ArrayList<>(contacts);
            filterContactsAndSubmit(this.ownContacts);
            System.out.println(this.ownContacts.size());
        });

        if ((Boolean) viewModel.getStoredCredentials().get("is_admin")) fragId = R.id.nav_host_fragment_activity_admin;
        else fragId = R.id.nav_host_fragment_activity_main;
        viewModel.sendRequest("/message/users","GET",null,null,false,false,true,this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentMessagesBinding.inflate(inflater, container, false);
        emptyView = binding.emptyView;
        recyclerView = binding.messagesList;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.searchInput.setOnQueryTextListener(this);
        binding.swipeRefreshLayout.setOnRefreshListener(this);



        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        return binding.getRoot();
    }

    @Override
    public void onClick(int profileID) {
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(fragId);
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
                    this.ownContacts= viewModel.contactsFromJSONObject(data);
                    viewModel.addContacts(this.ownContacts);
                    //filterContactsAndSubmit(this.ownContacts);
                }
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void filterContactsAndSubmit(ArrayList<Contact> contacts){
        ArrayList<Contact> aux=new ArrayList<>();
        if(contacts==null){
            adapter.submitList(aux);
            return;
        }
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
