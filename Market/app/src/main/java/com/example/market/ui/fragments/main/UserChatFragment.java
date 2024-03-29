package com.example.market.ui.fragments.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.market.BuildConfig;
import com.example.market.R;
import com.example.market.data.MarketViewModel;
import com.example.market.databinding.FragmentUserChatBinding;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.marketDatabase.Message;
import com.example.market.marketDatabase.Product;
import com.example.market.ui.components.adapter.MessageListAdapter;
import com.example.market.utils.MessageComparator;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class UserChatFragment extends Fragment implements HTTTPCallback, View.OnClickListener {
    private FragmentUserChatBinding binding;
    private MarketViewModel viewModel;
    private MessageListAdapter adapter;

    private int profileID;
    private int fragId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(MarketViewModel.class);
        binding = FragmentUserChatBinding.inflate(inflater, container, false);
        profileID = UserChatFragmentArgs.fromBundle(getArguments()).getProfileId();

        adapter = new MessageListAdapter(new MessageListAdapter.MessageDiff(), (int) viewModel.getStoredCredentials().get("profile_id"));
        adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        adapter.setHasStableIds(true);

        RecyclerView recyclerView = binding.messageList;
        recyclerView.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setReverseLayout(true);

        recyclerView.setLayoutManager(manager);

        viewModel.getMessagesWithUser(profileID).observe(requireActivity(), messages -> {
            System.out.println("hello?");
            System.out.println(messages.size());
            filterAndSubmit(new ArrayList<>(messages));
        });

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("profile_id", Integer.toString(profileID));
        viewModel.sendRequest("/profile/info", "GET", params, null, false, false, true, this);

        viewModel.sendRequest("/message/withuser", "GET", params, null, false, false, true, this);

        if ((Boolean) viewModel.getStoredCredentials().get("is_admin"))
            fragId = R.id.nav_host_fragment_activity_admin;
        else fragId = R.id.nav_host_fragment_activity_main;

        binding.backButton.setOnClickListener(this);
        binding.sendMessageButton.setOnClickListener(this);
        binding.profileName.setOnClickListener(this);
        binding.profileIcon.setOnClickListener(this);
        binding.swipeRefreshLayout.setEnabled(false);
        return binding.getRoot();
    }

    @Override
    public void onComplete(JSONObject data) {
        String url1 = "/profile/info";
        String url2 = "/message/withuser";
        String url3 = "/message/send";
        try {
            int code = data.getInt("status");
            String endpoint = data.getString("endpoint");
            if (endpoint.equals(url1)) {
                initializeProfilePhoto(data);
                if (code == 200) {
                    binding.profileName.setText(data.getString("username"));
                }
            } else if (endpoint.equals(url2)) {
                ArrayList<Message> aux = viewModel.messagesFromJSONObject(data);
                filterAndSubmit(aux);
            } else if (endpoint.equals(url3)) {
                if (code == 200) {
                    Map<String, Object> params = new LinkedHashMap<>();
                    params.put("profile_id", Integer.toString(profileID));
                    viewModel.sendRequest("/message/withuser", "GET", params, null, false, false, true, this);
                }
            }
        } catch (JSONException | ParseException| NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == binding.backButton) { // go back
            NavHostFragment navHostFragment =
                    (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(fragId);
            NavController navController = navHostFragment.getNavController();
            navController.navigateUp();
        }
        if (view == binding.sendMessageButton) { //send message
            String text = binding.messageInput.getText().toString();
            if (text.length() > 0) {
                binding.messageInput.getText().clear();
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("profile_id", profileID);
                params.put("content", text);
                viewModel.sendRequest("/message/send", "POST", null, params, true, false, true, this);
            }
        }
        if (view == binding.profileIcon || view == binding.profileName) { //see profile
            NavHostFragment navHostFragment =
                    (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(fragId);
            NavController navController = navHostFragment.getNavController();
            NavDirections action = UserChatFragmentDirections.actionUserChatFragmentToOtherProfileFragment(profileID);
            navController.navigate(action);
        }
    }

    private void initializeProfilePhoto(JSONObject data) throws JSONException, NullPointerException {
        ShapeableImageView photoView = binding.profileIcon;
        int code = (Integer) data.get("status");
        if (code == 200) {
            String photoURL = data.getString("image");
            if (photoURL.equals("null")) {
                photoView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.placeholder_avatar));
            } else {
                String fullURL = "https://" + BuildConfig.API_ADDRESS + photoURL;
                Glide.with(getActivity().getApplicationContext())
                        .load(fullURL)
                        .override(500, 500) //give resize dimension, you could calculate those
                        .centerCrop() // scale to fill the ImageView
                        .placeholder(R.drawable.placeholder_avatar)
                        .error(R.drawable.placeholder_avatar)
                        .into(photoView);
            }
        } else {
            photoView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.placeholder_avatar));
        }
    }

    private void filterAndSubmit(ArrayList<Message> messagesToFilter) {
        ArrayList<Message> aux = new ArrayList<>();

        messagesToFilter.sort(new MessageComparator());
        Collections.reverse(messagesToFilter);
        adapter.submitList(messagesToFilter);
    }
}
