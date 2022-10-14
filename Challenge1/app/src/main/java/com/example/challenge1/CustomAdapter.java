package com.example.challenge1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Animal> animals;
    LayoutInflater inflater;

    public CustomAdapter(Context context, ArrayList<Animal> animals){
        this.context=context;
        this.animals=animals;
        inflater=(LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return this.animals.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.custom_spinner_item,null);
        ImageView icon= (ImageView) view.findViewById(R.id.image_icon_item);
        TextView name= (TextView) view.findViewById(R.id.name_item);
        icon.setImageResource(animals.get(i).getImage());
        name.setText(animals.get(i).getName());
        return view;
    }
}
