package com.example.market.ui.components.holder;

import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.market.BuildConfig;
import com.example.market.R;
import com.example.market.data.MarketViewModel;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.interfaces.RecyclerViewInterface;
import com.example.market.marketDatabase.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;


public class AdminProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ItemClickListener, HTTTPCallback {
    private final TextView titleView;
    private final TextView categoryTextView;
    private final TextView locationTextView;
    private final TextView priceTextView;
    private final TextView dateTextView;
    private final ImageSlider imageSlider;
    private final TextView reportCountTextView;

    private final View rootView;
    private final RecyclerViewInterface recyclerViewInterface;
    private Product product;
    private MarketViewModel viewModel;


    private AdminProductViewHolder(View itemView, RecyclerViewInterface recyclerViewInterface, MarketViewModel viewModel) {
        super(itemView);
        rootView = itemView;
        this.recyclerViewInterface = recyclerViewInterface;
        this.viewModel=viewModel;

        titleView = itemView.findViewById(R.id.title_label);
        imageSlider = itemView.findViewById(R.id.image_slider);
        categoryTextView = itemView.findViewById(R.id.category_label);
        locationTextView = itemView.findViewById(R.id.city_label);
        priceTextView = itemView.findViewById(R.id.price_label);
        dateTextView = itemView.findViewById(R.id.date_label);
        reportCountTextView = itemView.findViewById(R.id.report_count);
        itemView.findViewById(R.id.more_options_admin).setOnClickListener(this);
        itemView.setOnClickListener(this);
    }

    public void bind(Product product) {
        this.product = product;
        String lang = Resources.getSystem().getConfiguration().getLocales().get(0).getLanguage();
        String city = product.getProfileLocation();
        String categoryNamePT = product.getCategoryNamePT();
        String categoryName = product.getCategoryName();
        Double price = product.getPrice();

        titleView.setText(product.getTitle());
        priceTextView.setText(String.format(Locale.ENGLISH, "%.0f€", price));

        if (lang.equals("pt")) {
            categoryTextView.setText(categoryNamePT);
        } else {
            categoryTextView.setText(categoryName);
        }
        if (city.equals("null"))
            city = rootView.getResources().getString(R.string.location_unknow_product);
        locationTextView.setText(city);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH);
        String strDate = dateFormat.format(product.getDate().getTime());
        dateTextView.setText(strDate);

        ArrayList<SlideModel> imageList = new ArrayList<SlideModel>();
        if (product.getImages().size() > 0) {
            for (String imgURL : product.getImages()) {
                String fullURL = "https://" + BuildConfig.API_ADDRESS + imgURL;
                imageList.add(new SlideModel(fullURL, ScaleTypes.CENTER_CROP));
            }
        } else {
            if (lang.equals("pt")) {
                imageList.add(new SlideModel(R.drawable.placeholder_no_image_pt, ScaleTypes.CENTER_CROP));
            } else {
                imageList.add(new SlideModel(R.drawable.placeholder_no_image_en, ScaleTypes.CENTER_CROP));
            }
        }
        imageSlider.setImageList(imageList);
        imageSlider.setItemClickListener(this);

        Map<String,Object> params=new LinkedHashMap<>();
        params.put("product_id", Integer.toString(product.getId()));
        viewModel.sendRequest("/report/byproduct", "GET", params, null, false, false, true, this);
    }

    public static AdminProductViewHolder create(ViewGroup parent, RecyclerViewInterface recyclerViewInterface, MarketViewModel viewModel) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_product_admin, parent, false);
        return new AdminProductViewHolder(view, recyclerViewInterface, viewModel);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == rootView.getId() || view.getId() == R.id.image_slider) {
            recyclerViewInterface.onClick(product);
        } else if (view.getId() == R.id.more_options_admin) {
            handleOptionsClick(view);
        }
    }

    @Override
    public void onItemSelected(int i) {
        recyclerViewInterface.onClick(product);
    }

    private void handleOptionsClick(View view){
        PopupMenu popupMenu = new PopupMenu(rootView.getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.admin_options_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.more_options_delete) {
                    recyclerViewInterface.delete(product);
                    return true;
                } else if (menuItem.getItemId() == R.id.more_options_details) {
                    recyclerViewInterface.onClick(product);
                    return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public void onComplete(JSONObject data) {
        String url1 = "/report/byproduct";
        try {
            int code = data.getInt("status");
            String endpoint = data.getString("endpoint");
            if (endpoint.equals(url1)) {
                if (code == 200) {
                    JSONArray reports = data.getJSONArray("reports");
                    reportCountTextView.setText(String.format(Locale.ENGLISH,"%d %s",reports.length(),rootView.getResources().getString(R.string.reports)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
