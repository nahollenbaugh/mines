package com.nahollenbaugh.mines.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.nahollenbaugh.mines.R;

public class BasicDialog extends DialogFragment {
    Fragment f;
    String message;
    String positive;
    String negative;
    DialogInterface.OnClickListener positiveClick;
    DialogInterface.OnClickListener negativeClick;
    public BasicDialog(String message, String positive, String negative,
                       DialogInterface.OnClickListener positiveClick,
                       DialogInterface.OnClickListener negativeClick,
                       Fragment f){
        super();
        this.message = message;
        this.positive = positive;
        this.negative = negative;
        this.positiveClick = positiveClick;
        this.negativeClick = negativeClick;
        this.f = f;
    }

    public BasicDialog(String message, String positive,
                       Fragment f) {
        this(message,positive,"",null,null,f);
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(f.requireContext());
        if (positive != null) {
            builder.setPositiveButton(positive, positiveClick);
        }
        if (negative != null) {
            builder.setNegativeButton(negative, negativeClick);
        }
        builder.setView(createView());
        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(
                ContextCompat.getColor(f.requireContext(),R.color.background)));
        return dialog;
    }
    public View createView() {
        View view = f.requireActivity().getLayoutInflater().inflate(
                R.layout.dialog_basic, null);

        int s = (int) (f.requireView().getWidth() * 0.05f);
        View space = view.findViewById(R.id.dialog_basic_NWSpace);
        ViewGroup.LayoutParams params = space.getLayoutParams();
        params.height = s;
        params.width = s;
        space.setLayoutParams(params);
        space = view.findViewById(R.id.dialog_basic_NWSpace);
        params = space.getLayoutParams();
        params.height = s;
        params.width = s;
        space.setLayoutParams(params);

        TextView text = view.findViewById(R.id.dialog_basic_message);
        text.setTextColor(ContextCompat.getColor(f.requireContext(), R.color.dark));
        text.setText(message);
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, text.getTextSize() * 1.5f);
        return view;
    }

    public void show(){
        show(f.getParentFragmentManager(),"");
    }

    public static DialogInterface.OnClickListener doNothing = (dialogInterface, i) -> { };
}
