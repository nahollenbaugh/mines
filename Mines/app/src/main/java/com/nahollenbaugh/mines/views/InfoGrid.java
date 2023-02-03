package com.nahollenbaugh.mines.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
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

public class InfoGrid extends LinearLayoutReorder {

    protected List<Item> rows = new ArrayList<>();

    protected int dark;
    protected int unit;
    float textSize = 20f;

    protected Context ctxt;

    public InfoGrid(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);
        this.setVerticalScrollBarEnabled(true);
        setOrientation(VERTICAL);
        setShowDividers(SHOW_DIVIDER_MIDDLE+SHOW_DIVIDER_BEGINNING+SHOW_DIVIDER_END);
        setDividerDrawable(getResources().getDrawable(R.drawable.infogrid_vertical_divider));
        this.ctxt = ctxt;
        dark = ContextCompat.getColor(ctxt, R.color.dark);
        unit = TypedValue.COMPLEX_UNIT_PX;
    }

    public Item addItem(Item item) {
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

        rows.add(item);
        addView(item);
        LayoutParams params = (LayoutParams)item.getLayoutParams();
        params.width = LayoutParams.MATCH_PARENT;
        item.setLayoutParams(params);



        return item;
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

    int dim;
    public void onSizeChanged(int w, int h, int oldW, int oldH){
        dim = w / 10;
    }

    public class Item extends LinearLayoutReorder {
        public View image;
        public View description;
        public View left;
        public View middle;
        public View right;
        public View farRight;

        public Item(final View image, View description, OnClickListener listener, Context ctxt) {
            super(ctxt);
            image.setOnClickListener(listener);
            constructor(image, description, ctxt);
        }

        public Item(final View image, View description, Context ctxt) {
            super(ctxt);
            constructor(image, description, ctxt);
        }

        protected void constructor(final View image, View description, Context ctxt) {
            this.image = image;
            this.description = description;
            this.left = new View(ctxt);
            this.middle = new View(ctxt);
            this.right = new View(ctxt);
            this.farRight = new View(ctxt);
            setOrientation(HORIZONTAL);
            setDividerDrawable(getResources().getDrawable(R.drawable.infogrid_vertical_divider));

            setVerticalGravity(Gravity.CENTER_VERTICAL);

            addView(left);
            addView(image);
            addView(middle);
            addView(description);
            addView(right);
            addView(farRight);
            OnClickListener l = v -> image.callOnClick();
            description.setOnClickListener(l);
            left.setOnClickListener(l);
            middle.setOnClickListener(l);
            right.setOnClickListener(l);
            farRight.setOnClickListener(l);

            View[] measureOrder = new View[6];
            measureOrder[0] = left;
            measureOrder[1] = image;
            measureOrder[2] = middle;
            measureOrder[3] = right;
            measureOrder[4] = description;
            measureOrder[5] = farRight;
            setMeasureOrder(measureOrder);
            View[] displayOrder = new View[6];
            displayOrder[0] = left;
            displayOrder[1] = image;
            displayOrder[2] = middle;
            displayOrder[3] = description;
            displayOrder[4] = right;
            displayOrder[5] = farRight;
            setDisplayOrder(displayOrder);

            updateSize();
        }

        public void updateSize() {
            int space = dim / 5;
            LinearLayout.LayoutParams params;

            if (image instanceof DrawnButton) {
                params = (LinearLayout.LayoutParams) image.getLayoutParams();
                params.width = dim;
                params.height = dim;
                image.setLayoutParams(params);

                params = (LinearLayout.LayoutParams) left.getLayoutParams();
                params.width = space;
                params.height = LayoutParams.MATCH_PARENT;
                left.setLayoutParams(params);

                params = (LinearLayout.LayoutParams) middle.getLayoutParams();
                params.width = space;
                params.height = LayoutParams.MATCH_PARENT;
                middle.setLayoutParams(params);
            } else if (image instanceof CounterView) {
                int counterWidth = (int)((dim - DrawNumberUtil.PREFERRED_SEPARATION)
                        * DrawNumberUtil.PREFERRED_ASPECT_RATIO + DrawNumberUtil.PREFERRED_SEPARATION);
                params = (LinearLayout.LayoutParams) image.getLayoutParams();
                params.width = counterWidth;
                params.height = dim;
                image.setLayoutParams(params);

                params = (LinearLayout.LayoutParams) left.getLayoutParams();
                params.width = space + (dim-counterWidth)/2;
                params.height = LayoutParams.MATCH_PARENT;
                left.setLayoutParams(params);

                params = (LinearLayout.LayoutParams) middle.getLayoutParams();
                params.width = space + (dim-counterWidth)/2;
                params.height = LayoutParams.MATCH_PARENT;
                middle.setLayoutParams(params);
            }

            if (description instanceof TextView) {
                ((TextView) description).setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            } else if (description instanceof IncDecDescription) {
                ((IncDecDescription) description).text.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            }

            params = (LinearLayout.LayoutParams) right.getLayoutParams();
            params.width = space;
            params.height = LayoutParams.MATCH_PARENT;
            right.setLayoutParams(params);

            params = (LinearLayout.LayoutParams) farRight.getLayoutParams();
            params.width = LayoutParams.WRAP_CONTENT;
            params.height = LayoutParams.MATCH_PARENT;
            farRight.setLayoutParams(params);

        }

        public void onSizeChanged(int w, int h, int oldW, int oldH) {
            updateSize();
            post(() -> requestLayout());
        }
    }

    public class IncDecDescription extends LinearLayoutReorder{
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

        public void onSizeChanged(int w, int h, int oldW, int oldH) {
            LinearLayout.LayoutParams params;

            params = (LinearLayout.LayoutParams) incButton.getLayoutParams();
            params.width = dim;
            params.height = dim;
            incButton.setLayoutParams(params);

            params = (LinearLayout.LayoutParams) decButton.getLayoutParams();
            params.width = dim;
            params.height = dim;
            decButton.setLayoutParams(params);

            params = (LinearLayout.LayoutParams) incSpace.getLayoutParams();
            params.width = dim / 5;
            params.height = LayoutParams.MATCH_PARENT;
            incSpace.setLayoutParams(params);

            params = (LinearLayout.LayoutParams) decSpace.getLayoutParams();
            params.width = dim / 5;
            params.height = LayoutParams.MATCH_PARENT;
            decSpace.setLayoutParams(params);
        }

        public void update(){
            View[] measureOrder = new View[5];
            measureOrder[0] = decButton;
            measureOrder[1] = decSpace;
            measureOrder[2] = incSpace;
            measureOrder[3] = incButton;
            measureOrder[4] = text;
            setMeasureOrder(measureOrder);
            View[] displayOrder = new View[5];
            displayOrder[0] = decButton;
            displayOrder[1] = decSpace;
            displayOrder[2] = text;
            displayOrder[3] = incSpace;
            displayOrder[4] = incButton;
            setDisplayOrder(displayOrder);
        }
    }

}


