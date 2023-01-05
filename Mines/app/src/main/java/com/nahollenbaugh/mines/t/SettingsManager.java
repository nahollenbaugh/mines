package com.nahollenbaugh.mines.t;

public interface SettingsManager {

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
