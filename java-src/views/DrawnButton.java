package com.nahollenbaugh.mines.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.nahollenbaugh.mines.R;
import com.nahollenbaugh.mines.drawing.DrawImage;

public class DrawnButton extends View {
    protected DrawImage draw;

    protected boolean hasLongPressDraw = false;
    protected boolean isLongPressed = false;
    protected DrawImage longPressDraw;

    public DrawnButton(Context context, AttributeSet attrs){
        super(context,attrs);
        if (defaultLongPressDraw == null){
            defaultLongPressDraw = new DrawImage(){
                int color = ContextCompat.getColor(context, R.color.longpress);
                public void draw(int width, int height, Canvas c){
                    c.save();
                    c.clipRect(0,0,width,height);
                    c.drawColor(color);
                    c.restore();
                }
            };
        }
        this.longPressDraw = defaultLongPressDraw;
        this.setOnTouchListener((view, motionEvent) -> {
            switch(motionEvent.getActionMasked()){
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_BUTTON_RELEASE:
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_UP:
                    isLongPressed = false;
                    invalidate();
            }
            return false;
        });
    }

    public void setDrawImage(DrawImage draw){
        if (draw == null){
            throw new NullPointerException();
        }
        this.draw = draw;
        invalidate();
    }

    public DrawImage getDrawImage(){
        return draw;
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener listener){
        hasLongPressDraw = true;
        super.setOnLongClickListener(view -> {
            isLongPressed = true;
            invalidate();
            return listener.onLongClick(view);
        });
    }

    @Override
    public void onDraw(Canvas c){
        if (hasLongPressDraw && isLongPressed){
            longPressDraw.draw(getWidth(), getHeight(), c);
            return;
        }
        draw.draw(getWidth(), getHeight(), c);
    }

    public void setLongPressDraw(DrawImage draw){
        if (draw == null){
            hasLongPressDraw = false;
        } else {
            this.longPressDraw = draw;
            hasLongPressDraw = true;
        }
    }

    protected static DrawImage defaultLongPressDraw;
}
