package com.example.market.ui.components.holder;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.market.marketDatabase.Report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class ReportViewHolder extends RecyclerView.ViewHolder{
    private final TextView reasonTextView;
    private final TextView detailsTextView;

    private Report report;

    private final View rootView;

    private ReportViewHolder(View itemView) {
        super(itemView);
        rootView = itemView;

        reasonTextView = itemView.findViewById(R.id.reason);
        detailsTextView = itemView.findViewById(R.id.details);

    }

    public void bind(Report report) {
        this.report = report;
        String lang = Resources.getSystem().getConfiguration().getLocales().get(0).getLanguage();
        String reason=report.getReason();
        String reasonTranslated=translateReason(reason);

        reasonTextView.setText(reasonTranslated);
        detailsTextView.setText(report.getExplain());
        if(report.getExplain().equals("")){
            rootView.findViewById(R.id.details_label).setVisibility(View.GONE);
            rootView.findViewById(R.id.details).setVisibility(View.GONE);
        }
    }

    public static ReportViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_report, parent, false);
        return new ReportViewHolder(view);
    }

    public String translateReason(String reason){
        switch (reason){
            case "Fraud":
                return rootView.getResources().getString(R.string.reason_fraud);
            case "Illegal activity":
                return rootView.getResources().getString(R.string.reason_illegal);
            case "Racism, sexism, homophobia or other discrimination":
                return rootView.getResources().getString(R.string.reason_racism);
            case "Hate Speech":
                return rootView.getResources().getString(R.string.reason_hate_speech);
            case "Missinformation":
                return rootView.getResources().getString(R.string.reason_missinformation);
            case "Disrespectful, Uncivil, unkind or violation of someone's privacy":
                return rootView.getResources().getString(R.string.reason_disrespectful);
            case "Others":
                return rootView.getResources().getString(R.string.reason_other);
        }
        return reason;
    }
}


