package com.example.market.ui.fragments;

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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.market.BuildConfig;
import com.example.market.R;
import com.example.market.data.MarketViewModel;
import com.example.market.databinding.FragmentMessagesBinding;
import com.example.market.databinding.FragmentUserChatBinding;
import com.example.market.interfaces.ContactRecyclerViewInterface;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.marketDatabase.Contact;
import com.example.market.marketDatabase.Message;
import com.example.market.ui.components.ContactListAdapter;
import com.example.market.ui.components.MessageListAdapter;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class UserChatFragment extends Fragment implements HTTTPCallback, View.OnClickListener {
    private FragmentUserChatBinding binding;
    private MarketViewModel viewModel;
    private int profileID;
    private MessageListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(MarketViewModel.class);
        binding = FragmentUserChatBinding.inflate(inflater, container, false);

        profileID = UserChatFragmentArgs.fromBundle(getArguments()).getProfileId();

        adapter = new MessageListAdapter(new MessageListAdapter.MessageDiff());
        RecyclerView recyclerView = binding.messageList;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.getAllMessages().observe(requireActivity(), messages -> {
            adapter.submitList(messages);
        });

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("profile_id", Integer.toString(profileID));
        viewModel.sendRequest("/profile/info", "GET", params, null, false, false, true, this);

        viewModel.sendRequest("/message/withuser", "GET", params, null, false, false, true, this);
        binding.backButton.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onComplete(JSONObject data) {
        String url1 = "/profile/info";
        String url2 = "/message/withuser";
        try {
            int code = data.getInt("status");
            String endpoint = data.getString("endpoint");
            if (endpoint.equals(url1)) {
                initializeProfilePhoto(data);
            }
            else if(endpoint.equals(url2)){
                viewModel.deleteAllMessages();
                ArrayList<Message> messages=viewModel.messagesFromJSONObject(data,profileID);
                viewModel.addMessages(messages);
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == binding.backButton) {
            NavHostFragment navHostFragment =
                    (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
            NavController navController = navHostFragment.getNavController();
            navController.navigateUp();
        }
    }

    private void initializeProfilePhoto(JSONObject data) throws JSONException {
        ShapeableImageView photoView = binding.profileIcon;
        int code = (Integer) data.get("status");
        if (code == 200) {
            String photoURL = data.getString("image");
            if (photoURL.equals("null")) {
                photoView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.placeholder_avatar));
            } else {
                String fullURL = "https://" + BuildConfig.API_ADDRESS + photoURL;
                Glide.with(getContext())
                        .load(fullURL)
                        .override(500, 500) //give resize dimension, you could calculate those
                        .centerCrop() // scale to fill the ImageView
                        .into(photoView);
                //Picasso.get().load(fullURL).into(photoView);
            }
        } else {
            photoView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.placeholder_avatar));
        }
    }
}
