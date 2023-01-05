package com.nahollenbaugh.mines.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.nahollenbaugh.mines.R;

public class InfoGridDialog extends DialogFragment {
    Fragment f;

    public InfoGridDialog(Fragment f){
        super();
        this.f = f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context ctxt = f.requireContext();
        InfoGrid view = new InfoGrid(ctxt,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        Dialog dialog = builder.create();
        view.setBackgroundColor(ContextCompat.getColor(ctxt, R.color.background));

        ViewGroup.LayoutParams params;
        float dim = f.getView().getWidth() / 10f;
        int dark = ContextCompat.getColor(ctxt, R.color.dark);
        int unit = TypedValue.COMPLEX_UNIT_PX;
        float textSize = ((TextView) view.findViewById(R.id.settings_label_useQuestionMark))
                .getTextSize() * 1.5f;
        float spaceWidth = 0.25f;
        float spaceHeight = 1.5f;

        TwoStateDrawnButton button = new TwoStateDrawnButton(f.requireContext(),null);

        return dialog;
    }

    public void show(){
        super.show(f.getParentFragmentManager(), "");
    }


}
