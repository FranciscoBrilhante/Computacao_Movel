package com.example.market.marketDatabase;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.NonNull;

import com.example.market.R;

import java.util.Locale;

public class PriceRange {
    private Double minPrice;
    private Double maxPrice;
    private Context context;

    public PriceRange(Double minPrice, Double maxPrice, Context context){
        this.minPrice=minPrice;
        this.maxPrice=maxPrice;
        this.context=context;
    }

    public Double getMinPrice(){
        return this.minPrice;
    }

    public Double getMaxPrice(){
        return this.maxPrice;
    }

    @NonNull
    public String toString(){
        if(this.minPrice==null){
            return String.format(Locale.ENGLISH,"%.0f€ %s",maxPrice,context.getResources().getString(R.string.or_less));
        }
        if(this.maxPrice==null){
            return String.format(Locale.ENGLISH,"%.0f€ %s",minPrice, context.getResources().getString(R.string.or_more));
        }
        return String.format(Locale.ENGLISH,"%.0f€ - %.0f€",minPrice,maxPrice);
    }
}
