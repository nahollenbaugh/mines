package com.nahollenbaugh.mines.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.nahollenbaugh.mines.R;
import com.nahollenbaugh.mines.drawing.DrawCrossedOut;
import com.nahollenbaugh.mines.drawing.DrawFlag;
import com.nahollenbaugh.mines.drawing.DrawFloppy;
import com.nahollenbaugh.mines.drawing.DrawImage;
import com.nahollenbaugh.mines.drawing.DrawInfo;
import com.nahollenbaugh.mines.drawing.DrawMinus;
import com.nahollenbaugh.mines.drawing.DrawNumberUtil;
import com.nahollenbaugh.mines.drawing.DrawPlus;
import com.nahollenbaugh.mines.drawing.DrawQuestionMark;
import com.nahollenbaugh.mines.drawing.DrawResetFace;
import com.nahollenbaugh.mines.drawing.DrawShovel;
import com.nahollenbaugh.mines.drawing.DrawSmall;
import com.nahollenbaugh.mines.drawing.DrawZoom;
import com.nahollenbaugh.mines.t.SettingsManager;
import com.nahollenbaugh.mines.views.CounterView;
import com.nahollenbaugh.mines.views.InfoGrid;

public class SettingsDialog extends DialogFragment {
    Fragment f;
    SettingsManager s;
    public SettingsDialog(Fragment f, SettingsManager s){
        super();
        this.f = f;
        this.s = s;
    }

    boolean noguess;
    boolean allowZoom;
    boolean longPressFlags;
    boolean useQuestionMarks;
    boolean fixedZoomLevel;
    boolean resetFace;
    boolean storeGame;
    boolean viewStoredGames;
    boolean scrollSensitivity;
    boolean hintBomb;

    public void show(boolean noguess,
                     boolean allowZoom, boolean longPressFlags, boolean useQuestionMarks,
                     boolean fixedZoomLevel, boolean resetFace, boolean storeGame,
                     boolean viewStoredGames, boolean hintBomb, boolean scrollSensitivity){
        this.noguess = noguess;
        this.allowZoom = allowZoom;
        this.longPressFlags = longPressFlags;
        this.useQuestionMarks = useQuestionMarks;
        this.fixedZoomLevel = fixedZoomLevel;
        this.resetFace = resetFace;
        this.storeGame = storeGame;
        this.viewStoredGames = viewStoredGames;
        this.hintBomb = hintBomb;
        this.scrollSensitivity = scrollSensitivity;
        show(f.getParentFragmentManager(),"");
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        Context ctxt = f.requireContext();
        InfoGrid grid = new InfoGrid(f.requireContext(), null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(grid);
        Dialog dialog = builder.create();
        grid.setBackgroundColor(ContextCompat.getColor(ctxt,R.color.background));


        int crossOutColor = ContextCompat.getColor(ctxt, R.color.settings_crossout);
        int dark = ContextCompat.getColor(ctxt, R.color.dark);
        if (noguess) {
            grid.addItem(new DrawShovel(dark),
                    crossOutColor,
                    s.isNoguessMode(),
                    getResources().getString(R.string.settings_noguess),
                    v -> s.toggleNoguessMode());
        }
        if (allowZoom) {
            grid.addItem(
                    new DrawZoom(ContextCompat.getColor(ctxt, R.color.zoom),
                            ContextCompat.getColor(ctxt, R.color.transparent)),
                    crossOutColor,
                    s.isZoomMode(),
                    getResources().getString(R.string.settings_zoom),
                    v -> s.toggleZoomMode());
        }
        if (longPressFlags) {
            grid.addItem(
                    new DrawFlag(
                            ContextCompat.getColor(ctxt, R.color.flag_ground),
                            ContextCompat.getColor(ctxt, R.color.flag_flagpole),
                            ContextCompat.getColor(ctxt, R.color.flag_flag)),
                    crossOutColor,
                    s.isLongPressFlagsMode(),
                    getResources().getString(R.string.settings_longPressFlags),
                    v -> s.toggleLongPressForFlagMode());
        }
        if (scrollSensitivity) {
            final CounterView counter = new CounterView(ctxt, null);
            counter.setCount(Math.max(Math.min((int) s.getSmallScrollSensitivity(), 9), 0));

            grid.addItem(
                    counter,
                    new DrawPlus(ContextCompat.getColor(ctxt, R.color.dark)),
                    v -> {
                        int sensitivity = counter.getCount();
                        if (sensitivity == 9) {
                            return;
                        }
                        counter.increase();
                        s.setSmallScrollSensitivity(sensitivity + 1);
                    },
                    new DrawMinus(ContextCompat.getColor(ctxt, R.color.dark)),
                    v -> {
                        int sensitivity = counter.getCount();
                        if (sensitivity == 0) {
                            return;
                        }
                        counter.decrease();
                        s.setSmallScrollSensitivity(sensitivity - 1);
                    },
                    getResources().getString(R.string.settings_scrollSensitivity));
        }
        if (useQuestionMarks) {
            grid.addItem(
                    new DrawQuestionMark(ContextCompat.getColor(ctxt, R.color.questionMark)),
                    crossOutColor,
                    s.isQuestionMarkMode(),
                    getResources().getString(R.string.settings_useQuestionMark),
                    v -> s.toggleQuestionMarkMode());
        }
        if (resetFace) {
            DrawResetFace draw = new DrawResetFace(
                    ContextCompat.getColor(ctxt, R.color.face_head),
                    ContextCompat.getColor(ctxt, R.color.face_face),
                    ContextCompat.getColor(ctxt, R.color.transparent));
            draw.state = DrawResetFace.HAPPY;
            grid.addItem(
                    draw,
                    crossOutColor,
                    s.isConfirmResetFace(),
                    getResources().getString(R.string.settings_quickResetFace),
                    v -> s.toggleConfirmResetFace());
        }
        if (storeGame) {
            grid.addItem(
                    new DrawFloppy(dark),
                    getResources().getString(R.string.settings_storeGame),
                    v -> s.storeGame());
        }
        if (viewStoredGames) {
            grid.addItem(
                    new DrawFloppy(dark),
                    getResources().getString(R.string.settings_viewStoredGames),
                    v -> s.viewStoredGames());
        }
        if (fixedZoomLevel) {
            CounterView zoomCounter = new CounterView(ctxt, null);
            zoomCounter.setCount((int)s.getZoomLevel());

            grid.addItem(
                    zoomCounter,
                    new DrawPlus(ContextCompat.getColor(ctxt, R.color.dark)),
                    v -> {
                        int zoom = zoomCounter.getCount();
                        if (zoom == 9) {
                            return;
                        }
                        zoomCounter.increase();
                        s.setZoomLevel(zoom + 1);
                    },
                    new DrawMinus(ContextCompat.getColor(ctxt, R.color.dark)),
                    v -> {
                        int zoom = zoomCounter.getCount();
                        if (zoom == 0) {
                            return;
                        }
                        zoomCounter.decrease();
                        s.setZoomLevel(zoom - 1);
                    },
                    getResources().getString(R.string.settings_fixedZoom));
        }
        if (hintBomb) {
            grid.addItem(
                    new DrawSmall(new DrawFlag(
                            ContextCompat.getColor(ctxt, R.color.hint),
                            ContextCompat.getColor(ctxt, R.color.hint),
                            ContextCompat.getColor(ctxt, R.color.hint)),
                            0.75f, 0.75f),
                    crossOutColor,
                    s.isHintBombMode(),
                    getResources().getString(R.string.settings_hintBomb),
                    v -> s.toggleHintBombMode());
        }

        return dialog;
    }

    public void onDismiss(DialogInterface dialogInterface){
        super.onDismiss(dialogInterface);

    }
}
