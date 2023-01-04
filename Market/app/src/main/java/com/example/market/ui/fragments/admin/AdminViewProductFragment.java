package com.example.market.ui.fragments.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.market.BuildConfig;
import com.example.market.R;
import com.example.market.data.MarketViewModel;
import com.example.market.databinding.FragmentViewProductAdminBinding;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.ui.fragments.main.ViewProductFragmentDirections;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class AdminViewProductFragment extends Fragment implements HTTTPCallback, View.OnClickListener {
    private int id; //product id being viewed
    private FragmentViewProductAdminBinding binding;
    private MarketViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        id = AdminViewProductFragmentArgs.fromBundle(getArguments()).getId();

        viewModel = new ViewModelProvider(this).get(MarketViewModel.class);
        binding = FragmentViewProductAdminBinding.inflate(inflater, container, false);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("product_id", Integer.toString(id));
        viewModel.sendRequest("/product/details", "GET", params, null, false, false, true, this);

        binding.backButton.setOnClickListener(this);
        binding.deleteButton.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onComplete(JSONObject data) {
        int code;
        String url1 = "/product/details";
        String url2 = "/profile/info";
        String url3 = "/product/delete";
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
            } else if (endpoint.equals(url3)) {
                viewModel.deleteProductByID(id);
                Toast.makeText(getActivity().getApplicationContext(), R.string.successfull_deletion_product_message, Toast.LENGTH_SHORT).show();
                NavHostFragment navHostFragment =
                        (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
                NavController navController = navHostFragment.getNavController();
                navController.navigateUp();
            }
        } catch (JSONException | ParseException | NullPointerException e) {
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

        String profileName = data.getString("profile_name");
        Double ratingData = data.getDouble("rating");
        String rating = String.format(Locale.ENGLISH, "%.1f", ratingData);
        Double priceData = data.getDouble("price");
        String price = String.format(Locale.ENGLISH, "%.0f€", priceData);
        String city = data.getString("profile_location");
        String description = data.getString("description");

        nameView.setText(profileName);
        cityView.setText(city);
        priceView.setText(price);
        ratingView.setText(rating);
        descriptionView.setText(description);

        if (data.getDouble("rating") == 0.0) {
            ratingView.setVisibility(View.INVISIBLE);
        }
        if (city.equals("null")) {
            cityView.setVisibility(View.INVISIBLE);
        }
        //format publication date
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date d = format.parse(data.getString("date"));
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH);
        String strDate = dateFormat.format(d);
        dateView.setText(strDate);

        ArrayList<SlideModel> imageList = new ArrayList<SlideModel>();
        JSONArray images = data.getJSONArray("images");
        for (int j = 0; j < images.length(); j++) {
            String fullURL = "https://" + BuildConfig.API_ADDRESS + images.get(j);
            imageList.add(new SlideModel(fullURL, ScaleTypes.CENTER_CROP));
        }
        imageSlider.setImageList(imageList);
        if (imageList.size() <= 0) {
            imageSlider.setVisibility(View.GONE);
        }
    }

    private void initializeProfilePhoto(JSONObject data) throws JSONException, NullPointerException {
        ShapeableImageView photoView = binding.photo;
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
                        .into(photoView);
            }
        } else {
            photoView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.placeholder_avatar));
        }
    }

    @Override
    public void onClick(View view) {
        if (view == binding.backButton) {
            NavHostFragment navHostFragment =
                    (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_admin);
            NavController navController = navHostFragment.getNavController();
            navController.navigateUp();
        }
        if (view == binding.deleteButton) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), 0);
            alert.setTitle(R.string.confirm_deletion_product_title);
            alert.setMessage(R.string.confirm_deletion_product_message);
            alert.setPositiveButton(R.string.delete_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Map<String, Object> params = new LinkedHashMap<>();
                    params.put("product_id", Integer.toString(id));
                    viewModel.sendRequest("/product/delete", "GET", params, null, false, false, true, AdminViewProductFragment.this::onComplete);
                }
            });
            alert.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            alert.show();
        }
    }
}