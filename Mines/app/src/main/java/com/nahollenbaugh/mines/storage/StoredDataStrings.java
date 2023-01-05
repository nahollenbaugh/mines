package com.nahollenbaugh.mines.storage;

import android.content.res.Resources;
import android.util.Log;

import com.nahollenbaugh.mines.R;

import java.io.IOException;

public class StoredDataStrings {

    protected static boolean populated = false;

    public static String scoreSizesKey;
    public static String boardSizeFormat;
    public static String scoreKeyFormat;
    public static String smallString;
    public static String mediumString;
    public static String bigString;
    public static String smallStringKey;
    public static String mediumStringKey;
    public static String bigStringKey;
    public static String scoreOwnerKey;

    public static String questionMarkModeKey;
    public static String zoomModeKey;
    public static String longPressFlagsModeKey;
    public static String smallScrollSensitivityKey;
    public static String zoomLevelKey;

    public static String currentGameFileName;
    public static String listOfGamesFileName;
    public static String settingsFileName;

    public static void accessStuff(Resources res){
        if (populated){
            return;
        }
        zoomLevelKey = res.getString(R.string.zoom_level_key);
        boardSizeFormat = res.getString(R.string.board_size);
        scoreSizesKey = res.getString(R.string.score_sizes_key);
        smallStringKey = formatSizeKey(
                res.getInteger(R.integer.small_numbombs),
                res.getInteger(R.integer.small_width),
                res.getInteger(R.integer.small_height));
        mediumStringKey = formatSizeKey(
                res.getInteger(R.integer.medium_numbombs),
                res.getInteger(R.integer.medium_width),
                res.getInteger(R.integer.medium_height));
        bigStringKey = formatSizeKey(
                res.getInteger(R.integer.big_numbombs),
                res.getInteger(R.integer.big_width),
                res.getInteger(R.integer.big_height));
        smallString = res.getString(R.string.small);
        mediumString = res.getString(R.string.medium);
        bigString = res.getString(R.string.big);
        scoreKeyFormat = res.getString(R.string.score_key);
        scoreOwnerKey = res.getString(R.string.score_owner_key);

        currentGameFileName = res.getString(R.string.current_game_file);
        listOfGamesFileName = res.getString(R.string.listOfGames_file);
        settingsFileName = res.getString(R.string.settings_file);
    }

    public static String formatSizeKey(int bombs, int width, int height){
        int a = (width < height) ? width : height;
        int b = (a == width) ? height : width;
        return String.format(boardSizeFormat, bombs, a ,b);
    }

    public static boolean isReservedFileName(String name){
        return name == null
                || name.equals("")
                || name.equals(currentGameFileName)
                || name.equals(listOfGamesFileName)
                || name.equals(settingsFileName);
    }


    public static class BadFileException extends IOException {}
}
