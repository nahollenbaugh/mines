package com.nahollenbaugh.mines.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

public class ZoomableView extends View {

    // keeping track of the screen.  See zoomableview.pdf for geometry.
    private float scaleFactor = 1.0f;
    private float xOffset = 0;
    private float yOffset = 0;
    private float realWidth = 1;
    private float realHeight = 1;

    protected ScaleGestureDetector scaleGestureDetector;
    protected GestureDetector gestureDetector;

    protected ScaleGestureDetector zoomScaleGestureDetector;
    protected ScaleGestureDetector nothingScaleGestureDetector;

    public ZoomableView(Context context, AttributeSet attrs){
        super(context,attrs);

        zoomScaleGestureDetector = new ScaleGestureDetector(context
          , new ScaleGestureDetector.SimpleOnScaleGestureListener(){
                @Override
                public boolean onScale(ScaleGestureDetector detector){
                    float mouseScale = detector.getScaleFactor();
                    scaleFactor = scaleFactor * mouseScale;
                    scaleFactor = Math.max(1.0f,scaleFactor);
                    xOffset = xOffset + detector.getFocusX()*(1-(1/mouseScale))*(realWidth / scaleFactor / getWidth());
                    yOffset = yOffset + detector.getFocusY()*(1-(1/mouseScale))*(realHeight / scaleFactor / getHeight());
                    boundOffsets();

                    invalidate();
                    return true;
                }
            });
        zoomScaleGestureDetector.setQuickScaleEnabled(false);
        scaleGestureDetector = zoomScaleGestureDetector;
        nothingScaleGestureDetector = new ScaleGestureDetector(context,
                new ScaleGestureDetector.SimpleOnScaleGestureListener());
        gestureDetector = new GestureDetector(context
          , new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    xOffset = xOffset + transformDistanceToReal(distanceX);
                    yOffset = yOffset + transformDistanceToReal(distanceY);
                    boundOffsets();

                    invalidate();
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    ZoomableView.this.onLongClick(transformXToReal(e.getX()),
                            transformYToReal(e.getY()));
                    pointerStarts.remove(e.getPointerId(e.getActionIndex()));
                    invalidate();
                }

            });
    }

    // read click gestures
    protected Stack<ClickTimer> unusedTimers = new Stack<>();
    protected List<ClickTimer> activeTimers = new ArrayList<>();
    protected class ClickTimer{
        public ClickTimer(){
            timer = new Timer(false);
        }
        long time;
        int component;
        Timer timer;
        CallOnSingleClick task;
    }
    protected class CallOnSingleClick extends TimerTask {
        public float x;
        public float y;
        public CallOnSingleClick(float x, float y){
            this.x = x;
            this.y = y;
        }
        public void run(){
            onSingleClickConfirmed(x,y);
            Iterator<ClickTimer> it = activeTimers.iterator();
            while (it.hasNext()){
                ClickTimer t = it.next();
                if (this == t.task) {
                    it.remove();
                    unusedTimers.push(t);
                    break;
                }
            }
        }
    }
    protected Map<Integer, Pair<Pair<Float,Float>,Timer>> pointerStarts = new ArrayMap<>();
    @Override
    public boolean onTouchEvent(MotionEvent e){
        scaleGestureDetector.onTouchEvent(e);
        gestureDetector.onTouchEvent(e);

        int action = e.getActionMasked();
        int index = e.getActionIndex();
        int pointerId = e.getPointerId(e.getActionIndex());
        float x = e.getX(index);
        float y = e.getY(index);
        if (action == MotionEvent.ACTION_DOWN
                || action == MotionEvent.ACTION_POINTER_DOWN
                || action == MotionEvent.ACTION_POINTER_1_DOWN
                || action == MotionEvent.ACTION_POINTER_2_DOWN
                || action == MotionEvent.ACTION_POINTER_3_DOWN){
            pointerStarts.put(pointerId, new Pair<>(new Pair<>(x,y), null));
        } else if (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_POINTER_UP
                || action == MotionEvent.ACTION_POINTER_1_UP
                || action == MotionEvent.ACTION_POINTER_2_UP
                || action == MotionEvent.ACTION_POINTER_3_UP) {
            Pair<Pair<Float, Float>,Timer> start = pointerStarts.get(pointerId);
            if (start != null) {
                float realX = transformXToReal(x);
                float realY = transformYToReal(y);
                if (isSmallScroll(start.first.first, start.first.second, x, y)) {
                    onClick(realX, realY);

                    Iterator<ClickTimer> it = activeTimers.iterator();
                    long currentTime = Calendar.getInstance().getTimeInMillis();
                    boolean hasDoubleClicked = false;
                    while (it.hasNext()) {
                        ClickTimer t = it.next();
                        if (currentTime - t.time < doubleTapDelay
                                && t.component == getComponent(realX, realY)) {
                            t.task.cancel();
                            t.timer.purge();
                            unusedTimers.push(t);
                            hasDoubleClicked = true;
                            onDoubleClick(t.task.x, t.task.y);
                            it.remove();
                        }
                    }
                    if (!hasDoubleClicked) {
                        ClickTimer timer = unusedTimers.size() > 0
                                ? unusedTimers.pop()
                                : new ClickTimer();
                        timer.task = new CallOnSingleClick(realX, realY);
                        timer.component = getComponent(realX, realY);
                        timer.time = currentTime;
                        timer.timer.schedule(timer.task, doubleTapDelay);
                        activeTimers.add(timer);
                    }
                    invalidate();
                }
                pointerStarts.remove(pointerId);
            }
        } else if (action == MotionEvent.ACTION_CANCEL){
            pointerStarts.remove(pointerId);
        }
        return true;
    }
    protected int getComponent(float realX, float realY){
        return 0;
    }

    protected float smallScrollX = 10;
    protected float smallScrollY = 10;
    public void setSmallScrollSensitivity(float smallScroll){
        smallScrollX = (float)Math.pow(10d,0.2d*smallScroll);
        smallScrollY = smallScrollX;
    }
    public float getSmallScrollSensitivity(){
        return (float)Math.log10(smallScrollX)*5f;
    }
    protected boolean isSmallScroll(float xStart, float yStart, float xEnd, float yEnd){
        float xDist = xStart - xEnd;
        if (xDist < 0) xDist = -xDist;
        float yDist = yStart - yEnd;
        if (yDist < 0) yDist = -yDist;
        return (xDist <= smallScrollX) && (yDist <= smallScrollY);
    }

    protected long doubleTapDelay = 200;
    public void setDoubleTapDelay(int delay){
        this.doubleTapDelay = delay;
    }
    public int getDoubleTapDelay(){
        return (int)doubleTapDelay;
    }

    public void onSingleClickConfirmed(float x, float y){
        // do nothing.  Override to do stuff.
    }
    public void onDoubleClick(float x, float y){
        // do nothing.  Override to do stuff.
    }
    public void onClick(float x, float y) {
        // do nothing.  Override to do stuff.
    }
    public void onLongClick(float x, float y){
        // do nothing.  Override to do stuff.
    }

    // translate between real and screen coords
    protected float transformXToScreen(float realX) {
        return (realX - xOffset) * scaleFactor;
    }
    protected float transformYToScreen(float realY) {
        return (realY - yOffset) * scaleFactor;
    }
    protected float transformXToReal(float screenX) {
        return screenX / scaleFactor + xOffset;
    }
    protected float transformYToReal(float screenY) {
        return screenY / scaleFactor + yOffset;
    }
    protected float transformDistanceToReal(float distance){
        return distance / scaleFactor;
    }
    protected float transformDistanceToScreen(float distance){
        return distance * scaleFactor;
    }

    // define real coords of the view.  Need not relate to screen coords in any way.
    protected void setEffectiveDimensions(float xEff, float yEff){
        this.realWidth = xEff;
        this.realHeight = yEff;
        invalidate();
    }

    public void turnZoomOn(){
        scaleGestureDetector = zoomScaleGestureDetector;
    }
    public void turnZoomOff(){
        scaleGestureDetector = nothingScaleGestureDetector;
    }

    public void setZoom(float zoom){
        if (zoom <= 0){
            return;
        }
        float xCenter = transformXToReal(getWidth()/2f);
        float yCenter = transformYToReal(getHeight()/2f);
        scaleFactor = Math.max(1f,zoom);
        xOffset = xCenter - transformDistanceToReal(getWidth()/2f);
        yOffset = yCenter - transformDistanceToReal(getHeight()/2f);
        boundOffsets();
        invalidate();
    }
    public float getZoom(){
        return scaleFactor;
    }

    protected void boundOffsets(){
        xOffset = Math.min(realWidth*(1-(1/scaleFactor)),Math.max(0,xOffset));
        yOffset = Math.min(realHeight*(1-(1/scaleFactor)),Math.max(0,yOffset));
    }
}
