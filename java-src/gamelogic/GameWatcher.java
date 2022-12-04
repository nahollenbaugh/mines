package com.nahollenbaugh.mines.gamelogic;

public interface GameWatcher {
    void alertGameChange(int change);

    int CHANGE_STARTED = 0;
    int CHANGE_WON = 1;
    int CHANGE_LOST = 2;
    int CHANGE_FLAGGED = 3;
    int CHANGE_UNFLAGGED = 4;
    int CHANGE_UNCOVERED = 5;

}
