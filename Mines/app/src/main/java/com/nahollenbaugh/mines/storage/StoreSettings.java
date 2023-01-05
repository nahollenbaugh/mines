package com.nahollenbaugh.mines.storage;

import android.content.Context;
import android.util.Log;

import com.nahollenbaugh.mines.t.SettingsManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class StoreSettings {

    public static void store(Context ctxt, SettingsManager s){
        FileOutputStream out;
        try {
            out = ctxt.openFileOutput(StoredDataStrings.settingsFileName, Context.MODE_PRIVATE);
            out.write(ALLOW_ZOOM);
            out.write(s.isZoomMode() ? 1 : 0);
            out.write(LONG_PRESS_FLAGS);
            out.write(s.isLongPressFlagsMode() ? 1 : 0);
            out.write(USE_QUESTION_MARKS);
            out.write(s.isQuestionMarkMode() ? 1 : 0);
            out.write(CONFIRM_RESET_FACE);
            out.write(s.isConfirmResetFace() ? 1 : 0);
            out.write(SCROLL_SENSITIVITY);
            out.write((int)s.getSmallScrollSensitivity());
            out.write(HINT_BOMB);
            out.write(s.isHintBombMode() ? 1 : 0);
            out.write(END);
            out.flush();
            out.close();
        } catch (IOException e) { }
    }

    public static void read(Context ctxt, SettingsManager s) {
        try {
            FileInputStream in = ctxt.openFileInput(StoredDataStrings.settingsFileName);
            boolean done = false;
            while (!done) {
                switch (in.read()) {
                    case ALLOW_ZOOM:
                        if (s.isZoomMode() != (in.read() == 1)) s.toggleZoomMode();
                        break;
                    case LONG_PRESS_FLAGS:
                        if (s.isLongPressFlagsMode() != (in.read() == 1))
                            s.toggleLongPressForFlagMode();
                        break;
                    case USE_QUESTION_MARKS:
                        if (s.isQuestionMarkMode() != (in.read() == 1)) s.toggleQuestionMarkMode();
                        break;
                    case CONFIRM_RESET_FACE:
                        if (s.isConfirmResetFace() != (in.read() == 1)) s.toggleConfirmResetFace();
                        break;
                    case SCROLL_SENSITIVITY:
                        s.setSmallScrollSensitivity(in.read());
                        break;
                    case HINT_BOMB:
                        if (s.isHintBombMode() != (in.read() == 1)) s.toggleHintBombMode();
                        break;
                    case END:
                        done = true;
                        break;
                    default:
                        throw new StoredDataStrings.BadFileException();
                }
            }
            in.close();
        } catch (IOException e) {
        }
    }

    protected static final int ALLOW_ZOOM = 1;
    protected static final int LONG_PRESS_FLAGS = 2;
    protected static final int USE_QUESTION_MARKS = 3;
    protected static final int CONFIRM_RESET_FACE = 5;
    protected static final int SCROLL_SENSITIVITY = 6;
    protected static final int HINT_BOMB = 7;
    protected static final int END = 0;

}
