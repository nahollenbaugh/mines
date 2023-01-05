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

import java.util.Calendar;
import java.util.TimerTask;

public class TimerView extends View {
    protected Timer timer;
    protected DrawNumberUtil drawNumber;

    protected int color;
    protected int flaggingColor;

    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        timer = new Timer(new TimerTask(){
            public void run(){
                invalidate();
            }
        });
        Paint numberStrokePaint = new Paint();
        numberStrokePaint.setStrokeWidth(10);
        numberStrokePaint.setStyle(android.graphics.Paint.Style.STROKE);
        color = ContextCompat.getColor(context,R.color.timer_number);
        flaggingColor = ContextCompat.getColor(context,R.color.timer_number_flagging);
        drawNumber = new DrawNumberUtil(color, Color.BLACK, DrawNumberUtil.PREFERRED_THICKNESS,
                DrawNumberUtil.PREFERRED_BORDER_THICKNESS, DrawNumberUtil.PREFERRED_SEPARATION);
    }

    public void start(){
        timer.start();
    }
    public int stop(){
        return timer.stop();
    }
    public int value() {
        return timer.getTime();
    }
    public void reset(){
        timer.close();
        timer = new Timer(new TimerTask(){
            public void run(){
                invalidate();
            }
        });
        invalidate();
    }

    public void close(){
        timer.close();
    }

    @Override
    public void onDraw(Canvas c){
        float thickness = drawNumber.getThickness();
        float spacing = DrawNumberUtil.PREFERRED_SPACING;
        float height = getHeight();
        float width = Math.min(getWidth()/(4+4*spacing+thickness),
                height * DrawNumberUtil.PREFERRED_ASPECT_RATIO);
        int time = timer.getTime();
        drawNumber.drawNumber(0,0,width,getHeight(),time/600,c);
        drawNumber.drawNumber(width*(1+spacing),0,width,getHeight(),time/60%10,c);
        drawNumber.drawNumber(width*(2+3*spacing+thickness),0,width,getHeight(),time/10%6,c);
        drawNumber.drawNumber(width*(3+4*spacing+thickness),0,width,getHeight(),time%10,c);
        drawNumber.strokeRectangle(width,
                2*width*(1+spacing),getHeight()/2f-width*thickness*1.5f,
                2*width*(1+spacing)+width*thickness,getHeight()/2f-width*thickness*0.5f,
                c);
        drawNumber.strokeRectangle(width,
                2*width*(1+spacing),getHeight()/2f+width*thickness*0.5f,
                2*width*(1+spacing)+width*thickness,getHeight()/2f+width*thickness*1.5f,
                c);
    }

    public void onFlaggingColor(){
        drawNumber.color = flaggingColor;
        invalidate();
    }
    public void offFlaggingColor(){
        drawNumber.color = color;
        invalidate();
    }
    public void setStartingTime(int time){
        timer.addTime(time);
    }

    protected static class Timer {
        java.util.Timer animationTimer;
        long startTime = 0;
        int extraTime = 0;
        boolean isRunning;

        public Timer(TimerTask runnable){
            animationTimer = new java.util.Timer();
            animationTimer.schedule(runnable,0,100);
        }
        public void start(){
            if (isRunning){
                throw new IllegalStateException("already running");
            }
            isRunning = true;
            startTime = Calendar.getInstance().getTimeInMillis();
        }

        public int stop(){
            addTime(getRunningTime());
            isRunning = false;
            return getTime();
        }

        public void addTime(int time){
            extraTime = extraTime + time;
        }

        public int getTime(){
            if (isRunning) {
                return extraTime + getRunningTime();
            } else {
                return extraTime;
            }
        }
        
        protected int getRunningTime(){
            return (int) ((Calendar.getInstance().getTimeInMillis() - startTime) / 1000);
        }

        public void close(){
            animationTimer.cancel();
            animationTimer = null;
        }
        @Override
        public void finalize(){
            if (animationTimer != null){
                throw new RuntimeException();
            }
        }
    }
}
