package com.example.market.ui.components;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.market.BuildConfig;
import com.example.market.R;
import com.example.market.interfaces.ContactRecyclerViewInterface;
import com.example.market.interfaces.RecyclerViewInterface;
import com.example.market.marketDatabase.Contact;
import com.example.market.marketDatabase.Product;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ContactViewHolder extends RecyclerView.ViewHolder{
    private final ContactRecyclerViewInterface recyclerViewInterface;
    private Contact contact;
    private final View rootView;
    public ContactViewHolder(@NonNull View itemView, ContactRecyclerViewInterface recyclerViewInterface) {
        super(itemView);
        this.rootView=itemView;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    public void bind(Contact contact) {
        this.contact = contact;

    }

    static ContactViewHolder create(ViewGroup parent, ContactRecyclerViewInterface recyclerViewInterface) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_contact, parent, false);
        return new ContactViewHolder(view, recyclerViewInterface);
    }

}
