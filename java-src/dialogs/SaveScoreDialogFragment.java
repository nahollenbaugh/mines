package com.nahollenbaugh.mines.dialogs;

import android.content.DialogInterface;

import com.nahollenbaugh.mines.R;
import com.nahollenbaugh.mines.main.GameFragment;

public class SaveScoreDialogFragment extends StringInputDialogFragment {
    GameFragment f;
    public SaveScoreDialogFragment(GameFragment f) {
        super(f.requireContext().getResources().getString(R.string.save_score_dialog_message),
                f.requireContext().getResources().getString(R.string.save_score_dialog_labelText),
                f.requireContext().getResources().getString(R.string.save_score_dialog_positiveButton),
                f.requireContext().getResources().getString(R.string.save_score_dialog_negativeButton));
        this.f = f;
        setPositiveListener((dialogInterface, i) -> f.confirmSaveCurrentScore(readEnteredText()));
    }
    @Override
    public void onDismiss(DialogInterface dialogInterface){
        if (f.pendingSaveScore()) {
            f.addSaveScoreButton();
        }
        super.onDismiss(dialogInterface);
    }

}
