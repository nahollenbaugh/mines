package com.nahollenbaugh.mines.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.nahollenbaugh.mines.R;
import com.nahollenbaugh.mines.drawing.DrawBomb;
import com.nahollenbaugh.mines.drawing.DrawFlag;
import com.nahollenbaugh.mines.drawing.DrawNumber;
import com.nahollenbaugh.mines.drawing.DrawQuestionMark;
import com.nahollenbaugh.mines.drawing.DrawResetFace;
import com.nahollenbaugh.mines.drawing.DrawSettings;
import com.nahollenbaugh.mines.drawing.DrawWithBackground;
import com.nahollenbaugh.mines.drawing.DrawZoom;
import com.nahollenbaugh.mines.views.DrawnCycle;
import com.nahollenbaugh.mines.views.GameView;
import com.nahollenbaugh.mines.views.InfoGrid;

public class InfoDialog extends DialogFragment {
    protected Fragment f;
    public InfoDialog(Fragment f){
        this.f = f;
    }
    public void show(){
        super.show(f.getParentFragmentManager(), "");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Context ctxt = requireContext();
        InfoGrid grid = new InfoGrid(ctxt, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(grid);
        Dialog dialog = builder.create();
        grid.setBackgroundColor(ContextCompat.getColor(ctxt, R.color.background));
        Resources res = getResources();

        grid.addItem(
                new DrawBomb(
                        ContextCompat.getColor(ctxt,R.color.bomb_explodedBomb),
                        ContextCompat.getColor(ctxt,R.color.bomb_shine)),
                res.getString(R.string.info_rulesBomb),
                null);

        DrawnCycle numbers = new DrawnCycle(ctxt,null,9);
        GameView.initialize(ctxt);
        for (int i = 3; i <= 11; i++){
            numbers.addImage(new DrawNumber(i % 9, GameView.numberColor(i % 9),
                    ContextCompat.getColor(ctxt,R.color.number_outline)));
        }
        grid.addItem(numbers, res.getString(R.string.info_rulesSafe));

        final DrawResetFace drawFace = new DrawResetFace(
                ContextCompat.getColor(ctxt, R.color.face_head),
                ContextCompat.getColor(ctxt, R.color.face_face),
                ContextCompat.getColor(ctxt, R.color.transparent));
        drawFace.state = DrawResetFace.COOL;
        final InfoGrid.Item rulesWinItem
                = grid.addItem(drawFace, res.getString(R.string.info_rulesWin), null);
        View.OnTouchListener surpriseListener = (view, motionEvent) -> {
            int action = motionEvent.getActionMasked();
            if (action == MotionEvent.ACTION_DOWN
                    || action == MotionEvent.ACTION_POINTER_DOWN) {
                drawFace.state = DrawResetFace.SURPRISED;
                rulesWinItem.image.invalidate();
            } else if (action == MotionEvent.ACTION_UP
                    || action == MotionEvent.ACTION_POINTER_UP){
                drawFace.state = DrawResetFace.COOL;
                rulesWinItem.image.invalidate();
            }
            return false;
        };
        rulesWinItem.image.setOnTouchListener(surpriseListener);
        rulesWinItem.description.setOnTouchListener(surpriseListener);

        grid.addItem(new DrawWithBackground(
                        ContextCompat.getColor(ctxt, R.color.button_background),
                        new DrawFlag(
                                ContextCompat.getColor(ctxt, R.color.flag_ground),
                                ContextCompat.getColor(ctxt, R.color.flag_flagpole),
                                ContextCompat.getColor(ctxt, R.color.flag_flag))),
                res.getString(R.string.info_flag), null);

        DrawnCycle questionMarkFlag = new DrawnCycle(ctxt, null, 3);
        questionMarkFlag.addImage(
                new DrawQuestionMark(ContextCompat.getColor(ctxt, R.color.questionMark)));
        questionMarkFlag.addImage(new DrawFlag(
                        ContextCompat.getColor(ctxt, R.color.flag_ground),
                        ContextCompat.getColor(ctxt, R.color.flag_flagpole),
                        ContextCompat.getColor(ctxt, R.color.flag_flag)));
        questionMarkFlag.addImage((w, h, c) -> {});
        grid.addItem(questionMarkFlag, res.getString(R.string.info_questionMark));

        grid.addItem(new DrawSettings(
                        ContextCompat.getColor(ctxt, R.color.dark),
                        ContextCompat.getColor(ctxt, R.color.transparent)),
                res.getString(R.string.info_questionMarkSettings), null);

        grid.addItem(new DrawQuestionMark(
                        ContextCompat.getColor(ctxt, R.color.hint),
                        ContextCompat.getColor(ctxt, R.color.button_background)),
                res.getString(R.string.info_hint), null);

        grid.addItem(new DrawZoom(
                        ContextCompat.getColor(ctxt, R.color.questionMark),
                        ContextCompat.getColor(ctxt, R.color.button_background)),
                res.getString(R.string.info_zoom), null);

        return dialog;
    }
}
