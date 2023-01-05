package com.nahollenbaugh.mines.t;

import android.content.Context;

import com.nahollenbaugh.mines.gamelogic.Game;
import com.nahollenbaugh.mines.storage.StoreSettings;

public class SimpleSettingsManager implements SettingsManager{
    public SimpleSettingsManager(Context ctxt){
        this.ctxt = ctxt;
        isReading = true;
        StoreSettings.read(ctxt, this);
        isReading = false;
    }
    protected Context ctxt;
    protected boolean isReading;
    protected boolean isNoguessMode = NOGUESS_MODE_DEFAULT;
    protected boolean isQuestionMarkMode = QUESTION_MARK_MODE_DEFAULT;
    protected boolean isZoomMode = ZOOM_MODE_DEFAULT;
    protected boolean isLongPressFlagsMode = LONG_PRESS_FOR_FLAGS_MODE_DEFAULT;
    protected float smallScrollSensitivity;
    protected boolean isConfirmResetFace = CONFIRM_RESET_FACE_DEFAULT;
    protected boolean isHintBombMode = HINT_BOMB_MODE_DEFAULT;

    public void toggleNoguessMode(){
        isNoguessMode = !isNoguessMode();
        write();
    }
    public boolean isNoguessMode(){
        return isNoguessMode;
    }

    public void toggleQuestionMarkMode(){
        isQuestionMarkMode = !isQuestionMarkMode;
        write();
    }
    public boolean isQuestionMarkMode(){
        return isQuestionMarkMode;
    }
    public void toggleZoomMode(){
        isZoomMode = !isZoomMode;
        write();
    }
    public boolean isZoomMode(){
        return isZoomMode;
    }

    public void toggleLongPressForFlagMode(){
        isLongPressFlagsMode = !isLongPressFlagsMode;
        write();
    }
    public boolean isLongPressFlagsMode(){
        return isLongPressFlagsMode;
    }

    public void setSmallScrollSensitivity(float sensitivity){
        smallScrollSensitivity = sensitivity;
        write();
    }
    public float getSmallScrollSensitivity(){
        return smallScrollSensitivity;
    }

    public void toggleConfirmResetFace(){
        isConfirmResetFace = !isConfirmResetFace;
        write();
    }
    public boolean isConfirmResetFace(){
        return isConfirmResetFace;
    }

    public void storeGame(){ }

    public void viewStoredGames(){ }

    public void setZoomLevel(float zoom){ }
    public float getZoomLevel(){
        return 1;
    }

    public void toggleHintBombMode(){
        isHintBombMode = !isHintBombMode;
        write();
    }
    public boolean isHintBombMode(){
        return isHintBombMode;
    }

    protected void write(){
        if (isReading){
            return;
        }
        StoreSettings.store(ctxt,this);
    }
}
