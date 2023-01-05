package com.example.market.ui.components.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.market.interfaces.RecyclerViewInterface;
import com.example.market.marketDatabase.Product;
import com.example.market.marketDatabase.Report;
import com.example.market.ui.components.holder.ProductViewHolder;
import com.example.market.ui.components.holder.ReportViewHolder;

import java.util.List;

public class ReportListAdapter extends ListAdapter<Report, ReportViewHolder> {
    private List<Report> reports;
    public ReportListAdapter(@NonNull DiffUtil.ItemCallback<Report> diffCallback){
        super(diffCallback);
    }

    @Override
    public void onCurrentListChanged(@NonNull List<Report> previousList, @NonNull List<Report> currentList) {
        super.onCurrentListChanged(previousList, currentList);
        this.reports=currentList;
    }

    @Override
    public long getItemId(int position) {
        return reports.get(position).getReportID();
        //return super.getItemId(position);
    }

    @Override
    public ReportViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        return ReportViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(ReportViewHolder holder, int position) {
        Report current=getItem(position);
        holder.bind(current);
    }

    public static class ReportDiff extends DiffUtil.ItemCallback<Report>{

        @Override
        public boolean areItemsTheSame(@NonNull Report oldItem,@NonNull Report newItem){
            return oldItem==newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Report oldItem,@NonNull Report newItem){
            return oldItem.getReportID()== newItem.getReportID();
        }
    }
}