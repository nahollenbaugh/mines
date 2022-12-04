package com.nahollenbaugh.mines.views;

import android.content.Context;
import android.util.AttributeSet;
import com.nahollenbaugh.mines.drawing.DrawImage;

public class TwoStateDrawnButton extends DrawnButton {

    protected DrawImage upImage;
    protected DrawImage downImage;
    protected boolean isUp=true;

    public TwoStateDrawnButton(Context context, AttributeSet attrs){
        super(context,attrs);
        setOnClickListener(v -> {
            // do nothing.  We just want to register the state-switching
            // listener that comes for free in (the overridden)
            // setOnClickListener.  I.e. in order to allow arbitrary
            // onclick listeners to be added to buttons, we override
            // setOnClickListener so that we can both switch the state of
            // the button and pass events to the supplied listener.
            // So... empty listener now.
        });
    }

    public void setUpImage(DrawImage upImage) {
        this.upImage = upImage;
        if (isUp){
            setDrawImage(upImage);
        }
    }

    public void setDownImage(DrawImage downImage) {
        this.downImage = downImage;
        if (!isUp){
            setDrawImage(downImage);
        }
    }

    @Override
    public void setOnClickListener(OnClickListener listener){
        super.setOnClickListener(v -> {
            listener.onClick(v);
            switchState();
        });
    }

    public void switchState(){
        isUp = !isUp;
        if (isUp) {
            setDrawImage(upImage);
        } else {
            setDrawImage(downImage);
        }
    }

    public void setState(boolean toUp){
        if (isUp != toUp){
            switchState();
        }
    }

    public boolean isUp(){
        return isUp;
    }

}
