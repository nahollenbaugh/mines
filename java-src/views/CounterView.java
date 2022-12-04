package com.nahollenbaugh.mines.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.nahollenbaugh.mines.R;
import com.nahollenbaugh.mines.drawing.DrawNumberUtil;

public class CounterView extends View {
    protected DrawNumberUtil drawNumber;
    protected int value = 99;

    int color;
    int flaggingColor;

    public CounterView(Context context, AttributeSet atts) {
        super(context, atts);
        Paint numberStrokePaint = new Paint();
        numberStrokePaint.setStrokeWidth(10);
        numberStrokePaint.setStyle(android.graphics.Paint.Style.STROKE);
        color = ContextCompat.getColor(context,R.color.timer_number);
        flaggingColor = ContextCompat.getColor(context,R.color.timer_number_flagging);
        drawNumber = new DrawNumberUtil(color, Color.BLACK, DrawNumberUtil.PREFERRED_THICKNESS,
                DrawNumberUtil.PREFERRED_BORDER_THICKNESS, DrawNumberUtil.PREFERRED_SEPARATION);
    }

    public void onFlaggingColor(){
        drawNumber.color = flaggingColor;
        invalidate();
    }
    public void offFlaggingColor(){
        drawNumber.color = color;
        invalidate();
    }

    public void setCount(int value){
        this.value = value;
        invalidate();
    }
    public void increase(){
        this.value = this.value + 1;
        invalidate();
    }
    public void decrease(){
        this.value = this.value - 1;
        invalidate();
    }
    public int getCount(){
        return value;
    }

    @Override
    public void onDraw(Canvas c){
        float height = getHeight();
        float width = height * DrawNumberUtil.PREFERRED_ASPECT_RATIO;
        float spacing = DrawNumberUtil.PREFERRED_SPACING;

        int value = this.value > 0 ? this.value : -this.value;
        if (value == 0){
            drawNumber.drawNumber(getWidth()-width,0,width,height,0,c);
        } else {
            int i = 0;
            while (value > 0) {
                drawNumber.drawNumber(getWidth() - ((i+1) * width) - (i * spacing * width),
                        0, width, height, value % 10, c);
                value = value / 10;
                i = i + 1;
            }
            if (this.value < 0) {
                drawNumber.strokeMiddle(getWidth() - ((i+1) * width) - (i * spacing * width),
                        0, width, height, c);
            }
        }
    }
}
