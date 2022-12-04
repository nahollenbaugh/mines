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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.nahollenbaugh.mines.R;
import com.nahollenbaugh.mines.gamelogic.GameData;
import com.nahollenbaugh.mines.main.GameFragment;
import com.nahollenbaugh.mines.storage.StoreGame;
import com.nahollenbaugh.mines.storage.StoredGamesManager;

import java.util.ArrayList;
import java.util.Collections;

public class StoredGameDialog extends DialogFragment {
    Fragment f;
    StoredGameStarter s;
    public StoredGameDialog(Fragment f, StoredGameStarter s){
        super();
        this.f = f;
        this.s = s;
    }
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Context ctxt = f.requireContext();
        ArrayList<String> gameNames = new ArrayList<>();
        Collections.addAll(gameNames, StoredGamesManager.listGameNames(ctxt));
        if (gameNames.size() == 0){
            return new BasicDialog(
                    getResources().getString(R.string.dialog_storedGame_noGames_message),
                    getResources().getString(R.string.dialog_storedGame_noGames_positive), f)
                    .onCreateDialog(null);
        }

        View dialogView = f.requireActivity().getLayoutInflater().inflate(
                R.layout.dialog_stored_game,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        final Dialog dialog = builder.create();

        ListView gameList = dialogView.findViewById(R.id.dialog_storedGame_list_games);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctxt,
                android.R.layout.simple_list_item_1,
                gameNames){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                TextView gameLabel = (TextView)super.getView(position, convertView, parent);
                gameLabel.setTextColor(ContextCompat.getColor(ctxt, R.color.dark));
                return gameLabel;
            }
        };
        gameList.setAdapter(adapter);
        gameList.setOnItemClickListener((adapterView, view, i, l) -> {
            s.startGame(((TextView)view).getText().toString());
            dialog.dismiss();
        });
        gameList.setOnItemLongClickListener((adapterView, view, position, l) -> {
            String name = ((TextView)view).getText().toString();
            new BasicDialog(String.format(
                    getResources().getString(R.string.dialog_deleteGame_message),name),
                    getResources().getString(R.string.dialog_deleteGame_positive),
                    getResources().getString(R.string.dialog_deleteGame_negative),
                    (dialogInterface, i) -> {
                        boolean successful = StoredGamesManager.deleteGame(name, ctxt);
                        if (!successful) {
                            Log.println(Log.ERROR, "", "StoredGameDialog.java delete");
                        }
                        adapter.remove(name);
                        if (adapter.getCount() == 0){
                            dialog.dismiss();
                            new StoredGameDialog(f, s).show();
                        }
                    },
                    BasicDialog.doNothing,
                    f).show();
            return true;
        });

        TextView title = dialogView.findViewById(R.id.dialog_storedGame_message);
        title.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark));
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, title.getTextSize() * 2);

        Space topSpace = dialogView.findViewById(R.id.dialog_storedGame_topSpace);
        ViewGroup.LayoutParams params = topSpace.getLayoutParams();
        params.height = title.getLayoutParams().height / 2;
        topSpace.setLayoutParams(params);

        TextView bottom = dialogView.findViewById(R.id.dialog_storedGame_deleteMessage);
        bottom.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark));
        bottom.setTextSize(TypedValue.COMPLEX_UNIT_PX, bottom.getTextSize() * 1.5f);
        bottom.setOnLongClickListener((View view) -> {
            new BasicDialog(
                    getResources().getString(R.string.dialog_deleteAllGames_message),
                    getResources().getString(R.string.dialog_deleteAllGames_positive),
                    getResources().getString(R.string.dialog_deleteAllGames_negative),
                    (DialogInterface dialogInterface, int i) -> {
                        for (String name : gameNames){
                            if (!StoredGamesManager.deleteGame(name, requireContext())){
                                Log.println(Log.ERROR,"","StoredGameDialog.java can't "
                                        + "delete game " + name);
                            }
                        }
                        dialog.dismiss();
                        dialog.dismiss();
                    },
                    BasicDialog.doNothing,
                    f).show();
            return true;
        });


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(
                ContextCompat.getColor(requireContext(),R.color.background)));
        return dialog;
    }
    public void show(){
        show(f.getParentFragmentManager(),"");
    }

    public interface StoredGameStarter{
        void startGame(String name);
    }
}
