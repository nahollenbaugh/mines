package com.nahollenbaugh.mines.t;

import com.nahollenbaugh.mines.gamelogic.Game;

public interface SettingsManager {
    void toggleQuestionMarkMode();
    boolean isQuestionMarkMode();

    void toggleZoomMode();
    boolean isZoomMode();

    void toggleLongPressForFlagMode();
    boolean isLongPressFlagsMode();

    void setSmallScrollSensitivity(float sensitivity);
    float getSmallScrollSensitivity();

    void toggleConfirmResetFace();
    boolean isConfirmResetFace();

    void storeGame();

    void viewStoredGames();

    void setZoomLevel(float zoom);
    float getZoomLevel();

    void toggleHintBombMode();
    boolean isHintBombMode();

}
