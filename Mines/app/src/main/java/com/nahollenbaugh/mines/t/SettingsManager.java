package com.nahollenbaugh.mines.t;

public interface SettingsManager {

    boolean DOUBLE_TAP_FLAGS_DEFAULT = false;
    void toggleDoubleTapFlagsMode();
    boolean isDoubleTapFlagsMode();

    int[] DOUBLE_TAP_DELAYS = new int[]{80,110,140,170,200,250,300,400,600,1000};
    int DOUBLE_TAP_DELAY_DEFAULT = 3;
    void setDoubleTapDelay(int delay);
    int getDoubleTapDelay();

    boolean NOGUESS_MODE_DEFAULT = false;
    void toggleNoguessMode();
    boolean isNoguessMode();

    boolean QUESTION_MARK_MODE_DEFAULT = true;
    void toggleQuestionMarkMode();
    boolean isQuestionMarkMode();

    boolean ZOOM_MODE_DEFAULT = true;
    void toggleZoomMode();
    boolean isZoomMode();

    boolean LONG_PRESS_FOR_FLAGS_MODE_DEFAULT = true;
    void toggleLongPressForFlagMode();
    boolean isLongPressFlagsMode();

    void setSmallScrollSensitivity(float sensitivity);
    float getSmallScrollSensitivity();

    boolean CONFIRM_RESET_FACE_DEFAULT = true;
    void toggleConfirmResetFace();
    boolean isConfirmResetFace();

    void storeGame();

    void viewStoredGames();

    void setZoomLevel(float zoom);
    float getZoomLevel();

    boolean HINT_BOMB_MODE_DEFAULT = false;
    void toggleHintBombMode();
    boolean isHintBombMode();
}
