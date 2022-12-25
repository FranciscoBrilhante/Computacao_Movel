package com.example.market.ui.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.market.R;
import com.example.market.marketDatabase.Category;
import com.example.market.marketDatabase.PriceRange;

import java.util.ArrayList;


public class PriceRangeSpinnerAdapter extends ArrayAdapter<PriceRange> {

    public PriceRangeSpinnerAdapter(Context context, ArrayList<PriceRange> categories) {
        super(context, 0, categories);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.spinner_view_category, parent, false
            );
        }
        TextView nameView = convertView.findViewById(R.id.category_name);
        PriceRange currentItem = getItem(position);
        if (currentItem != null) {
            nameView.setText(currentItem.toString());
        }
        return convertView;
    }
}