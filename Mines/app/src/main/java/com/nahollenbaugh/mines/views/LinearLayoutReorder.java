package com.nahollenbaugh.mines.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class LinearLayoutReorder extends LinearLayout {

    public LinearLayoutReorder(Context context) {
        super(context);
    }
    public LinearLayoutReorder(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public LinearLayoutReorder(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public LinearLayoutReorder(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected View[] measureOrder;
    protected View[] displayOrder;

    /**
     * Sets the order in which children are to be measured and, unless the display order is
     * set, the order in which children are to appear.
     * @param measureOrder
     */
    public void setMeasureOrder(View[] measureOrder) {
        this.measureOrder = measureOrder;
    }
    protected void setDisplayOrder(View[] displayOrder){
        this.displayOrder = displayOrder;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        if (measureOrder != null) {
            for (int i = 0; i < measureOrder.length; i++) {
                measureOrder[i].bringToFront();
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (displayOrder != null) {
            for (int i = 0; i < displayOrder.length; i++) {
                displayOrder[i].bringToFront();
            }
        }
    }
}
