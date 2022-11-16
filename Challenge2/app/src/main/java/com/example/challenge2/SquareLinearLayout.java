package com.example.challenge2;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SquareLinearLayout extends LinearLayout {
    public SquareLinearLayout(Context context) {
        super(context);
    }

    public SquareLinearLayout(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    public SquareLinearLayout(Context context, AttributeSet attributes, int defStyleAttr) {
        super(context, attributes, defStyleAttr);
    }

    @Override
    public void onMeasure(int width, int height) {
        super.onMeasure(width, width);
    }
}