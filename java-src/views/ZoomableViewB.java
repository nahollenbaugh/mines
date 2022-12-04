package com.nahollenbaugh.mines.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.util.ArrayMap;

import java.util.Map;

public class ZoomableViewB extends View {

    // keeping track of the screen.  See zoomableview.pdf for geometry.
    private float scaleFactor = 1.0f;
    private float xOffset = 0;
    private float yOffset = 0;
    private float xEff = 0;
    private float yEff = 0;

    protected ScaleGestureDetector scaleGestureDetector;
    protected GestureDetector gestureDetector;

    protected ScaleGestureDetector zoomScaleGestureDetector;
    protected ScaleGestureDetector nothingScaleGestureDetector;

    public ZoomableViewB(Context context, AttributeSet attrs){
        super(context,attrs);

        scaleGestureDetector = new ScaleGestureDetector(context
                , new ScaleGestureDetector.SimpleOnScaleGestureListener(){
            @Override
            public boolean onScale(ScaleGestureDetector detector){
                float mouseScale = detector.getScaleFactor();
                scaleFactor = scaleFactor * mouseScale;
                scaleFactor = Math.max(1.0f,scaleFactor);
                xOffset = xOffset + detector.getFocusX()*(1-(1/mouseScale));
                yOffset = yOffset + detector.getFocusY()*(1-(1/mouseScale));
                boundOffsets();

                invalidate();
                return true;
            }
        });
        scaleGestureDetector.setQuickScaleEnabled(false);
        zoomScaleGestureDetector = scaleGestureDetector;
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
                ZoomableViewB.this.onLongClick(transformXToReal(e.getX()),
                        transformYToReal(e.getY()));
                pointerStarts.remove(e.getPointerId(e.getActionIndex()));
                invalidate();
            }
        });
    }

    // read click gestures
    protected Map<Integer, Pair<Float,Float>> pointerStarts = new ArrayMap<>();
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
            pointerStarts.put(pointerId, new Pair<>(x,y));
        } else if (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_POINTER_UP
                || action == MotionEvent.ACTION_POINTER_1_UP
                || action == MotionEvent.ACTION_POINTER_2_UP
                || action == MotionEvent.ACTION_POINTER_3_UP) {
            Pair<Float, Float> start = pointerStarts.get(pointerId);
            if (start != null) {
                if (isSmallScroll(start.first, start.second, x, y)) {
                    onClick(transformXToReal(x), transformYToReal(y));
                    invalidate();
                }
                pointerStarts.remove(pointerId);
            }
        } else if (action == MotionEvent.ACTION_CANCEL){
            pointerStarts.remove(pointerId);
        }
        return true;
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
        this.xEff = xEff;
        this.yEff = yEff;
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
        xOffset = Math.min(xEff*(1-(1/scaleFactor)),Math.max(0,xOffset));
        yOffset = Math.min(yEff*(1-(1/scaleFactor)),Math.max(0,yOffset));
    }
}
