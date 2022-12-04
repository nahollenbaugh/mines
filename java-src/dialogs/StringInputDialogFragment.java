package com.nahollenbaugh.mines.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import com.nahollenbaugh.mines.R;

public class StringInputDialogFragment extends DialogFragment {
    protected String label;
    protected String positiveButtonText;
    protected String negativeButtonText;
    protected String message;

    protected StringInputDialogListener listener;
    protected DialogInterface.OnClickListener positiveListener;
    protected DialogInterface.OnClickListener negativeListener;

    public StringInputDialogFragment(String message, String label,
                                     String positiveButtonText, String negativeButtonText){
        super();
        this.message = message;
        this.label = label;
        this.positiveButtonText = positiveButtonText;
        this.negativeButtonText = negativeButtonText;
    }
    public void setPositiveListener(DialogInterface.OnClickListener positiveListener){
        this.positiveListener = positiveListener;
    }
    public void setNegativeListener(DialogInterface.OnClickListener negativeListener){
        this.negativeListener = negativeListener;
    }
    public StringInputDialogFragment(){
        super();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = requireActivity().getLayoutInflater().inflate(
                R.layout.dialog_string_input,null);

        TextView text;
        int dark = ContextCompat.getColor(requireContext(),R.color.dark);

        text = view.findViewById(R.id.dialog_string_input_label);
        text.setText(label);
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, text.getTextSize()*1.5f);
        text.setTextColor(dark);
        text = view.findViewById(R.id.dialog_string_input_message);
        text.setText(message);
        text.setTextColor(dark);
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, text.getTextSize()*1.5f);
        EditText input = view.findViewById(R.id.dialog_string_input_edittext);
        input.setTextColor(dark);
        input.setTextSize(TypedValue.COMPLEX_UNIT_PX,input.getTextSize()*1.5f);

        builder.setView(view);

        builder.setPositiveButton(positiveButtonText, positiveListener);
        builder.setNegativeButton(negativeButtonText, negativeListener);
        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(
                ContextCompat.getColor(requireContext(),R.color.background)));
        return dialog;
    }

    public String readEnteredText(){
        return ((EditText)getDialog().findViewById(R.id.dialog_string_input_edittext))
                .getText().toString();
    }

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);

        if (context instanceof StringInputDialogListener){
            listener = (StringInputDialogListener) context;
        } else {
            throw new ClassCastException(
                    "StringInputDialog's context must implement StringInputDialogListener");
        }
    }


}
