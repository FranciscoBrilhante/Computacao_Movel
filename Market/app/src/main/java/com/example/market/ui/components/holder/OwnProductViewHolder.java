package com.example.market.ui.components.holder;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.market.BuildConfig;
import com.example.market.R;
import com.example.market.interfaces.RecyclerViewInterface;
import com.example.market.marketDatabase.Product;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class OwnProductViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener, ItemClickListener {
    private final TextView titleView;
    private final TextView categoryTextView;
    private final TextView locationTextView;
    private final TextView priceTextView;
    private final TextView dateTextView;
    private final ImageSlider imageSlider;
    //private final ImageButton moreOptionsButton;
    private final RecyclerViewInterface recyclerViewInterface;
    private Product product;
    private final View rootView;

    private OwnProductViewHolder(View itemView, RecyclerViewInterface recyclerViewInterface) {
        super(itemView);
        rootView=itemView;
        this.recyclerViewInterface=recyclerViewInterface;

        titleView = itemView.findViewById(R.id.title_label);
        imageSlider= itemView.findViewById(R.id.image_slider);
        categoryTextView=itemView.findViewById(R.id.category_label);
        locationTextView=itemView.findViewById(R.id.city_label);
        priceTextView=itemView.findViewById(R.id.price_label);
        dateTextView=itemView.findViewById(R.id.date_label);

        //moreOptionsButton = itemView.findViewById(R.id.more_options);
        //moreOptionsButton.setOnClickListener(this);

        itemView.setOnClickListener(this);
    }

    public void bind(Product product) {
        this.product = product;
        String lang= Resources.getSystem().getConfiguration().getLocales().get(0).getLanguage();

        titleView.setText(product.getTitle());

        if (lang.equals("pt")) {
            categoryTextView.setText(product.getCategoryNamePT());
        }else{
            categoryTextView.setText(product.getCategoryName());
        }

        priceTextView.setText(String.format(Locale.ENGLISH, "%.2f€", product.getPrice()));

        String city = product.getProfileLocation();
        if (city.equals("null")) city = rootView.getResources().getString(R.string.location_unknow_product);
        locationTextView.setText(city);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy",Locale.ENGLISH);
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
    }

    public static OwnProductViewHolder create(ViewGroup parent, RecyclerViewInterface recyclerViewInterface) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_own_product, parent, false);
        return new OwnProductViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==rootView.getId() || view.getId()==R.id.image_slider){
            recyclerViewInterface.onClick(product);
        }
        /*else if(view.getId()==R.id.more_options){
            PopupMenu popupMenu=new PopupMenu(rootView.getContext(),view);
            MenuInflater inflater=popupMenu.getMenuInflater();
            inflater.inflate(R.menu.own_more_options_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if(menuItem.getItemId()==R.id.more_options_send_message){
                        recyclerViewInterface.sendMessage(product.getProfile());
                        return true;
                    }
                    return false;
                }
            });

            popupMenu.show();
        }*/

    }

    @Override
    public void onItemSelected(int i) {
        recyclerViewInterface.onClick(product);
    }
}
