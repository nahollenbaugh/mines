package com.nahollenbaugh.mines.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.nahollenbaugh.mines.R;
import com.nahollenbaugh.mines.drawing.DrawCrossedOut;
import com.nahollenbaugh.mines.drawing.DrawImage;
import com.nahollenbaugh.mines.drawing.DrawNumberUtil;

import java.util.ArrayList;
import java.util.List;

public class InfoGridB extends GridLayout {

    protected List<Item> rows = new ArrayList<>();
    protected Space topSpace;
    protected Space bottomSpace;

    protected float dim;
    protected int dark;
    protected int unit;
    protected float spaceWidth=0.25f;
    protected float spaceHeight=1.5f;
    float textSize = 20f;

    protected Context ctxt;

    public InfoGridB(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);
        this.ctxt = ctxt;
        dark = ContextCompat.getColor(ctxt, R.color.dark);
        unit = TypedValue.COMPLEX_UNIT_PX;
//        float textSize = ((TextView)view.findViewById(R.id.settings_label_useQuestionMark))
//                .getTextSize() * 1.5f;
        Log.println(Log.ERROR,"","default text size? cf {^}");

        topSpace = new Space(ctxt, null);
        addView(topSpace, new LayoutParams(GridLayout.spec(0, 1),
                GridLayout.spec(1,1)));
        bottomSpace = new Space(ctxt, null);
        addView(bottomSpace, new LayoutParams(GridLayout.spec(1, 1),
                GridLayout.spec(1,1)));
    }

    public void addItem(Item item){
        addView(item.image);
        addView(item.space);
        addView(item.description);

        updateSize(item);

        LayoutParams params = (LayoutParams) bottomSpace.getLayoutParams();
        params.rowSpec = GridLayout.spec(rows.size() + 2, 1);
        bottomSpace.setLayoutParams(params);
        Log.println(Log.ERROR, "", "InfoGrid.java put the bottom space in the right spot?");

        params = (LayoutParams)item.space.getLayoutParams();
        params.columnSpec = spec(1,1);
        params.rowSpec = spec(rows.size() + 1, 1);
        item.space.setLayoutParams(params);

        params = (LayoutParams)item.image.getLayoutParams();
        params.columnSpec = spec(0,1);
        params.rowSpec = spec(rows.size() + 1, 1);
        item.image.setLayoutParams(params);

        params = (LayoutParams)item.description.getLayoutParams();
        params.columnSpec = spec(2,1);
        params.rowSpec = spec(rows.size() + 1, 1);
        if (item.description instanceof TextView){
            TextView label = (TextView)item.description;
            label.setTextColor(Color.YELLOW);
//            label.setTextColor(dark);
            label.setTextSize(unit, textSize);
            params.width = 0;
//            params.setGravity(Gravity.FILL_HORIZONTAL + Gravity.CENTER_VERTICAL);
            params.setGravity(Gravity.FILL_HORIZONTAL);
        } else if (item.description instanceof IncDecDescription){
            ((IncDecDescription)item.description).text.setTextColor(dark);
            ((IncDecDescription)item.description).text.setTextSize(unit, textSize);
        }
        item.description.setLayoutParams(params);

        rows.add(item);
    }

    protected void updateSize(Item item){
        ViewGroup.LayoutParams params = item.space.getLayoutParams();
        params.width = (int)(spaceWidth * dim);
        params.height = (int) (spaceHeight * dim);
        item.space.setLayoutParams(params);

        if (item.image instanceof DrawnButton) {
            params = item.image.getLayoutParams();
            params.width = (int)dim;
            params.height = (int)dim;
            item.image.setLayoutParams(params);
        } else if (item.image instanceof CounterView) {
            params = item.image.getLayoutParams();
            params.width = (int)((dim - DrawNumberUtil.PREFERRED_SEPARATION)
                    * DrawNumberUtil.PREFERRED_ASPECT_RATIO + DrawNumberUtil.PREFERRED_SEPARATION);
            params.height = (int)dim;
            item.image.setLayoutParams(params);
        }

        if (item.description instanceof IncDecDescription){
            Log.println(Log.ERROR,"","InfoGrid.java incdec in update size "
                    + item.description);
            IncDecDescription incDec = (IncDecDescription)item.description;
            params = incDec.incButton.getLayoutParams();
            params.width = (int)dim;
            params.height = (int)dim;
            incDec.incButton.setLayoutParams(params);
            params = incDec.decButton.getLayoutParams();
            params.width = (int)dim;
            params.height = (int)dim;
            incDec.decButton.setLayoutParams(params);
        }
    }

    public void onSizeChanged(int w, int h, int oldW, int oldH){
        super.onSizeChanged(w, h, oldW, oldH);
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
        public OnClickListener listener;
        public Space space;

        public Item(View image, View description, OnClickListener listener, Context ctxt) {
            this.image = image;
            this.description = description;
            this.listener = listener;
            this.space = new Space(ctxt, null);
            image.setOnClickListener(listener);
            description.setOnClickListener(listener);
            space.setOnClickListener(listener);
        }
    }
    public void addItem(DrawImage upImage, DrawImage downImage, boolean isUp, String text,
                        OnClickListener listener){
        TwoStateDrawnButton button = new TwoStateDrawnButton(ctxt, null);
        button.setUpImage(upImage);
        button.setDownImage(downImage);
        button.setState(isUp);

        TextView description = new TextView(ctxt);
        description.setText(text);

        addItem(new Item(button, description, listener, ctxt));
    }
    public void addItem(DrawImage image, int crossOutColor, boolean isUp, String text,
                        OnClickListener listener){
        addItem(image, new DrawCrossedOut(image, crossOutColor), isUp, text, listener);
    }
    public void addItem(DrawImage image, String text, OnClickListener listener){
        DrawnButton button = new DrawnButton(ctxt, null);
        button.setDrawImage(image);

        TextView description = new TextView(ctxt);
        description.setText(text);
        addItem(new Item(button, description, listener, ctxt));
    }
    public void addItem(CounterView image, DrawImage incImage, OnClickListener incListener,
                        DrawImage decImage, OnClickListener decListener, String text) {
        IncDecDescription description = new IncDecDescription(
                incImage, incListener, decImage, decListener, text, ctxt);
        addItem(new Item(image, description, null, ctxt));
    }

    public static class IncDecDescription extends LinearLayoutReorder{
        public DrawnButton incButton;
        public DrawnButton decButton;
        public TextView text;

        public IncDecDescription(Context ctxt){
            super(ctxt);
        }

        public IncDecDescription(DrawImage incImage, OnClickListener incListener,
                                 DrawImage decImage, OnClickListener decListener,
                                 String descriptionText, Context ctxt) {
            super(ctxt);
            text = new TextView(ctxt);
            text.setText(descriptionText);
            this.incButton = new DrawnButton(ctxt, null);
            incButton.setDrawImage(incImage);
            incButton.setOnClickListener(incListener);
            this.decButton = new DrawnButton(ctxt, null);
            decButton.setDrawImage(decImage);
            decButton.setOnClickListener(decListener);

            addView(incButton);
            addView(decButton);
            addView(text);

            update();
        }

        public void update(){
            measureOrder = new View[3];
            displayOrder = new View[3];
            measureOrder[2] = text;
            displayOrder[1] = text;
            measureOrder[1] = incButton;
            displayOrder[2] = incButton;
            measureOrder[0] = decButton;
            displayOrder[0] = decButton;
        }
    }
}