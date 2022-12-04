package com.nahollenbaugh.mines.dialogs;

import android.content.DialogInterface;

import com.nahollenbaugh.mines.R;
import com.nahollenbaugh.mines.main.MenuFragment;

public class ResumeGameDialog extends BasicDialog {
    public ResumeGameDialog(DialogInterface.OnClickListener negativeListener, MenuFragment f) {
        super(f.getResources().getString(R.string.dialog_resumeGame_message),
                f.getResources().getString(R.string.dialog_resumeGame_positive),
                f.getResources().getString(R.string.dialog_resumeGame_negative_newGame),
                (d, i) -> f.startSavedGame(),
                negativeListener,
                f);
    }
}
