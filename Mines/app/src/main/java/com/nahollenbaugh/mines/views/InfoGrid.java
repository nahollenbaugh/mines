package com.nahollenbaugh.mines.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.nahollenbaugh.mines.R;
import com.nahollenbaugh.mines.drawing.DrawCrossedOut;
import com.nahollenbaugh.mines.drawing.DrawImage;
import com.nahollenbaugh.mines.drawing.DrawNumberUtil;

import java.util.ArrayList;
import java.util.List;

public class InfoGrid extends GridLayout {

    protected List<Item> rows = new ArrayList<>();
    protected Space topSpace;
    protected Space bottomSpace;

    protected float dim;
    protected int dark;
    protected int unit;
    protected float spaceWidth = 0.25f;
    protected float spaceHeight = 1.5f;
    float textSize = 20f;

    protected Context ctxt;

    public InfoGrid(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);
        this.ctxt = ctxt;
        dark = ContextCompat.getColor(ctxt, R.color.dark);
        unit = TypedValue.COMPLEX_UNIT_PX;

        topSpace = new Space(ctxt, null);
        addView(topSpace, new LayoutParams(GridLayout.spec(0, 1),
                GridLayout.spec(1,1)));
        bottomSpace = new Space(ctxt, null);
        addView(bottomSpace, new LayoutParams(GridLayout.spec(1, 1),
                GridLayout.spec(1,1)));
    }

    public Item addItem(Item item) {
        LayoutParams params;

        params = (LayoutParams) bottomSpace.getLayoutParams();
        params.rowSpec = GridLayout.spec(rows.size() + 2, 1);
        bottomSpace.setLayoutParams(params);

        addView(item.middleSpace);
        params = (LayoutParams) item.middleSpace.getLayoutParams();
        params.rowSpec = spec(rows.size() + 1, 1);
        params.columnSpec = spec(2, 1);
        item.middleSpace.setLayoutParams(params);

        addView(item.startSpace);
        params = (LayoutParams) item.startSpace.getLayoutParams();
        params.rowSpec = spec(rows.size() + 1, 1);
        params.columnSpec = spec(0, 1);
        item.startSpace.setLayoutParams(params);

        addView(item.endSpace);
        params = (LayoutParams) item.endSpace.getLayoutParams();
        params.rowSpec = spec(rows.size() + 1, 1);
        params.columnSpec = spec(4, 1);
        item.endSpace.setLayoutParams(params);

        addView(item.image);
        params = (GridLayout.LayoutParams) item.image.getLayoutParams();
        params.rowSpec = GridLayout.spec(rows.size() + 1, 1);
        params.columnSpec = GridLayout.spec(1, 1);
        params.setGravity(Gravity.CENTER_VERTICAL + Gravity.CENTER_HORIZONTAL);
        item.image.setLayoutParams(params);

        addView(item.description);
        params = (LayoutParams) item.description.getLayoutParams();
        params.rowSpec = spec(rows.size() + 1, 1);
        params.columnSpec = spec(3, 1);
        params.width = 0;
        params.setGravity(Gravity.FILL_HORIZONTAL + Gravity.CENTER_VERTICAL);
        item.description.setLayoutParams(params);
        if (item.description instanceof TextView) {
            ((TextView)item.description).setTextColor(dark);
        } else if (item.description instanceof IncDecDescription){
            IncDecDescription incDec = (IncDecDescription)item.description;
            LinearLayout.LayoutParams vgParams = (LinearLayout.LayoutParams)incDec.incButton.getLayoutParams();
            vgParams.gravity = Gravity.CENTER_VERTICAL;
            incDec.incButton.setLayoutParams(vgParams);

            vgParams = (LinearLayout.LayoutParams)incDec.decButton.getLayoutParams();
            vgParams.gravity = Gravity.CENTER_VERTICAL;
            incDec.decButton.setLayoutParams(vgParams);

            incDec.text.setTextColor(dark);
        }

        updateSize(item);

        rows.add(item);
        return item;
    }
    public void updateSize(Item item){
        ViewGroup.LayoutParams params;

        params = item.middleSpace.getLayoutParams();
        params.width = (int)(spaceWidth * dim);
        params.height = (int) (spaceHeight * dim);
        item.middleSpace.setLayoutParams(params);

        params = item.startSpace.getLayoutParams();
        params.width = (int)(0.7f * spaceWidth * dim);
        params.height = (int)(spaceHeight * dim);
        item.startSpace.setLayoutParams(params);

        params = item.endSpace.getLayoutParams();
        params.width = (int)(spaceWidth * dim);
        params.height = (int)(spaceHeight * dim);
        item.endSpace.setLayoutParams(params);

        if (item.image instanceof DrawnButton) {
            params = (LayoutParams) item.image.getLayoutParams();
            params.width = (int) dim;
            params.height = (int) dim;
            item.image.setLayoutParams(params);
        } else if (item.image instanceof CounterView) {
            params = item.image.getLayoutParams();
            params.width = (int)((dim - DrawNumberUtil.PREFERRED_SEPARATION)
                    * DrawNumberUtil.PREFERRED_ASPECT_RATIO + DrawNumberUtil.PREFERRED_SEPARATION);
            params.height = (int)dim;
            item.image.setLayoutParams(params);
        }

        if (item.description instanceof TextView) {
            ((TextView) item.description).setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        } else if (item.description instanceof IncDecDescription){
            IncDecDescription incDec = (IncDecDescription)item.description;
            incDec.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

            params = incDec.incButton.getLayoutParams();
            params.width = (int)dim;
            params.height = (int)dim;
            incDec.incButton.setLayoutParams(params);

            params = incDec.decButton.getLayoutParams();
            params.width = (int)dim;
            params.height = (int)dim;
            incDec.decButton.setLayoutParams(params);

            params = incDec.incSpace.getLayoutParams();
            params.width = (int)(0.1f * spaceWidth * dim);
            params.height = (int)dim;
            incDec.incSpace.setLayoutParams(params);

            params = incDec.decSpace.getLayoutParams();
            params.width = (int)(0.5f * spaceWidth * dim);
            params.height = (int)dim;
            incDec.decSpace.setLayoutParams(params);
        }
    }
    public Item addItem(DrawImage upImage, DrawImage downImage, boolean isUp, String text,
                        OnClickListener listener) {
        TwoStateDrawnButton button = new TwoStateDrawnButton(ctxt, null);
        button.setUpImage(upImage);
        button.setDownImage(downImage);
        button.setState(isUp);

        TextView description = new TextView(ctxt);
        description.setText(text);

        return addItem(new Item(button, description, listener, ctxt));
    }
    public Item addItem(DrawImage image, int crossOutColor, boolean isUp, String text,
                        OnClickListener listener) {
        return addItem(image, new DrawCrossedOut(image, crossOutColor), isUp, text, listener);
    }
    public Item addItem(DrawImage image, String text, OnClickListener listener) {
        DrawnButton button = new DrawnButton(ctxt, null);
        button.setDrawImage(image);

        TextView description = new TextView(ctxt);
        description.setTextColor(dark);
        description.setText(text);

        return addItem(new Item(button, description, listener, ctxt));
    }
    public Item addItem(CounterView image, DrawImage incImage, OnClickListener incListener,
                        DrawImage decImage, OnClickListener decListener, String text) {
        return addItem(new Item(image,
                new IncDecDescription(incImage, incListener, decImage, decListener, text, ctxt),
                null, ctxt));
    }
    public Item addItem(DrawnButton image, String text){
        TextView description = new TextView(ctxt);
        description.setText(text);
        return addItem(new Item(image, description, ctxt));
    }

    public void onSizeChanged(int w, int h, int oldW, int oldH){
        dim = w / 10f;
        ViewGroup.LayoutParams params = topSpace.getLayoutParams();
        params.width = (int)(spaceWidth*dim);
        params.height = (int)((spaceHeight-1)*dim);
        topSpace.setLayoutParams(params);
        params = bottomSpace.getLayoutParams();
        params.width = (int)(spaceWidth*dim);
        params.height = (int)((spaceHeight-1)*dim);
        bottomSpace.setLayoutParams(params);
        for (Item item : rows){
            updateSize(item);
        }
    }

    public static class Item {
        public View image;
        public View description;
        public Space middleSpace;
        public Space startSpace;
        public Space endSpace;

        public Item(final View image, View description, OnClickListener listener, Context ctxt) {
            image.setOnClickListener(listener);
            constructor(image, description, ctxt);
        }
        public Item(final View image, View description, Context ctxt) {
            constructor(image, description, ctxt);
        }
        protected void constructor(final View image, View description, Context ctxt){
            this.image = image;
            this.description = description;
            this.middleSpace = new Space(ctxt, null);
            this.startSpace = new Space(ctxt, null);
            this.endSpace = new Space(ctxt, null);
            OnClickListener l = v -> image.callOnClick();
            description.setOnClickListener(l);
            middleSpace.setOnClickListener(l);
            startSpace.setOnClickListener(l);
            endSpace.setOnClickListener(l);
        }

    }

    public static class IncDecDescription extends LinearLayoutReorder{
        public DrawnButton incButton;
        public DrawnButton decButton;
        public TextView text;
        public Space incSpace;
        public Space decSpace;

        public IncDecDescription(Context ctxt){
            super(ctxt);
        }

        public IncDecDescription(DrawImage incImage, OnClickListener incListener,
                                 DrawImage decImage, OnClickListener decListener,
                                 String descriptionText, Context ctxt) {
            super(ctxt);
            text = new TextView(ctxt);
            text.setText(descriptionText);
            incButton = new DrawnButton(ctxt, null);
            incButton.setDrawImage(incImage);
            incButton.setOnClickListener(incListener);
            decButton = new DrawnButton(ctxt, null);
            decButton.setDrawImage(decImage);
            decButton.setOnClickListener(decListener);
            incSpace = new Space(ctxt, null);
            incSpace.setOnClickListener(incListener);
            decSpace = new Space(ctxt, null);
            decSpace.setOnClickListener(decListener);

            addView(incButton);
            addView(decButton);
            addView(text);
            addView(incSpace);
            addView(decSpace);

            update();
        }

        public void update(){
            measureOrder = new View[5];
            displayOrder = new View[5];
            measureOrder[4] = text;
            displayOrder[2] = text;
            measureOrder[1] = incButton;
            displayOrder[4] = incButton;
            measureOrder[0] = decButton;
            displayOrder[0] = decButton;
            measureOrder[2] = incSpace;
            displayOrder[3] = incSpace;
            measureOrder[3] = decSpace;
            displayOrder[1] = decSpace;
        }
    }

}
