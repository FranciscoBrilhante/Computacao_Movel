package com.example.market.utils;

import com.example.market.marketDatabase.Message;
import com.example.market.marketDatabase.Product;

import java.util.Comparator;

public class ProductDateComparator implements Comparator<Product> {

    @Override
    public int compare(Product o1, Product o2) {
        return o2.getDate().compareTo(o1.getDate());
    }
}
