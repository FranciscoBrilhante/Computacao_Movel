package com.example.market.ui.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.market.BuildConfig;
import com.example.market.R;
import com.example.market.data.MarketViewModel;
import com.example.market.databinding.FragmentViewProductBinding;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.ui.activities.MainActivity;
import com.example.market.ui.components.ProductListAdapter;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ViewProductFragment extends Fragment implements HTTTPCallback {
    private boolean owner;
    private int id;

    private FragmentViewProductBinding binding;
    private MarketViewModel viewModel;
    private ProductListAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        id = ViewProductFragmentArgs.fromBundle(getArguments()).getId();
        owner = ViewProductFragmentArgs.fromBundle(getArguments()).getOwner();
        viewModel = new ViewModelProvider(this).get(MarketViewModel.class);
        binding = FragmentViewProductBinding.inflate(inflater, container, false);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("product_id", Integer.toString(id));
        viewModel.sendRequest("/product/details", "GET", params, null, false, false, true, this);

        if(owner){
            binding.sendMessageButton.setVisibility(View.GONE);
        }
        return binding.getRoot();
    }

    @Override
    public void onComplete(JSONObject data) {
        Integer code;
        String url1 = "/product/details";
        String url2 = "/profile/info";
        try {
            String endpoint = (String) data.get("endpoint");
            if (endpoint.equals(url1)) {
                code = (Integer) data.get("status");
                if (code == 200) {
                    initializeValues(data);

                    int profile_id = data.getInt("profile");
                    Map<String, Object> params = new LinkedHashMap<>();
                    params.put("profile_id", Integer.toString(profile_id));
                    viewModel.sendRequest(url2, "GET", params, null, false, false, true, this);
                } else {
                    NavHostFragment navHostFragment =
                            (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
                    NavController navController = navHostFragment.getNavController();
                    NavDirections action = ViewProductFragmentDirections.actionNavigationViewProductToNavigationHome();
                    navController.navigate(action);
                }
            } else if (endpoint.equals(url2)) {
                initializeProfilePhoto(data);
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void initializeValues(JSONObject data) throws JSONException, ParseException {
        TextView nameView = binding.nameLabel;
        TextView ratingView = binding.ratingLabelViewProduct;
        TextView dateView = binding.dateLabelViewItem;
        TextView priceView = binding.priceLabelViewItem;
        TextView cityView = binding.cityLabelViewItem;
        ImageSlider imageSlider = binding.imageSlider;
        TextView descriptionView = binding.descriptionLabelViewItem;

        nameView.setText(data.getString("profile_name"));
        String rating = String.format(Locale.ENGLISH, "%.1f", data.getDouble("rating"));
        ratingView.setText(rating);

        //format publication date
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date d = format.parse(data.getString("date"));
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH);
        String strDate = dateFormat.format(d);
        dateView.setText(strDate);

        String price = String.format(Locale.ENGLISH, "%.0f€", data.getDouble("price"));
        priceView.setText(price);

        cityView.setText("Coimbra");

        ArrayList<SlideModel> imageList = new ArrayList<SlideModel>();
        JSONArray images = data.getJSONArray("images");
        for (int j = 0; j < images.length(); j++) {
            String fullURL = "https://" + BuildConfig.API_ADDRESS + images.get(j);
            imageList.add(new SlideModel(fullURL, ScaleTypes.CENTER_CROP));
        }
        imageSlider.setImageList(imageList);

        descriptionView.setText(data.getString("description"));
    }

    private void initializeProfilePhoto(JSONObject data) throws JSONException {
        ShapeableImageView photoView = binding.photo;
        int code = (Integer) data.get("status");
        if (code == 200) {
            String photoURL = data.getString("image");

            if (photoURL.equals("null")) {
                photoView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.user));
                return;
            }
            String fullURL = "https://" + BuildConfig.API_ADDRESS + photoURL;
            Glide.with(getContext())
                    .load(fullURL)
                    .override(500, 500) //give resize dimension, you could calculate those
                    .centerCrop() // scale to fill the ImageView
                    .into(photoView);
            //Picasso.get().load(fullURL).into(photoView);
        } else {
            photoView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.user));
        }
    }
}