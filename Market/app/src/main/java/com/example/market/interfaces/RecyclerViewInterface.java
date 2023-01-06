package com.example.market.interfaces;

import com.example.market.marketDatabase.Product;

public interface RecyclerViewInterface {
    void onClick(Product product);
    void delete(Product product);
    void sendMessage(int profileID);
    void report(Product product);
}

