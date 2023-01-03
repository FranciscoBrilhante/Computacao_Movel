package com.example.market.utils;

import com.example.market.marketDatabase.Contact;

import java.util.Comparator;

public class ContactComparator implements Comparator<Contact> {
    @Override
    public int compare(Contact o1, Contact o2) {
        return - o1.getLastMessageTimestamp().compareTo(o2.getLastMessageTimestamp());
    }
}
