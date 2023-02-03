package com.nahollenbaugh.mines.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.nahollenbaugh.mines.databinding.FragmentMenuBinding;

import com.nahollenbaugh.mines.R;
import com.nahollenbaugh.mines.dialogs.ResumeGameDialog;
import com.nahollenbaugh.mines.dialogs.StoredGameDialog;
import com.nahollenbaugh.mines.drawing.DrawInfo;
import com.nahollenbaugh.mines.drawing.DrawSettings;
import com.nahollenbaugh.mines.gamelogic.GameData;
import com.nahollenbaugh.mines.storage.StoreGame;
import com.nahollenbaugh.mines.t.SimpleSettingsManager;
import com.nahollenbaugh.mines.dialogs.InfoDialog;
import com.nahollenbaugh.mines.dialogs.SettingsDialog;

public class MenuFragment extends Fragment {

    private FragmentMenuBinding binding;

    public boolean gameInProgress;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentMenuBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    protected void sendStartgameAction(int numbombs, int height, int width, String gameName) {
        MenuFragmentDirections.ActionStartgame action
                = MenuFragmentDirections.actionStartgame();
        action.setNumbombs(numbombs);
        action.setHeight(height);
        action.setWidth(width);
        action.setGameName(gameName);
        NavHostFragment.findNavController(MenuFragment.this).navigate(action);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context ctxt = requireContext();

        binding.edittextMenuBombsInput.setText(
                getResources().getInteger(R.integer.bombs_default) + "");
        binding.edittextMenuBombsInput.setTextColor(
                ContextCompat.getColor(ctxt, R.color.dark));
        binding.textviewMenuBombsLabel.setTextColor(
                ContextCompat.getColor(ctxt, R.color.dark));

        binding.edittextMenuHeightInput.setText(
                getResources().getInteger(R.integer.height_default) + "");
        binding.edittextMenuHeightInput.setTextColor(
                ContextCompat.getColor(ctxt, R.color.dark));
        binding.textviewMenuHeightLabel.setTextColor(
                ContextCompat.getColor(ctxt, R.color.dark));

        binding.edittextMenuWidthInput.setText(
                getResources().getInteger(R.integer.width_default) + "");
        binding.edittextMenuWidthInput.setTextColor(
                ContextCompat.getColor(ctxt, R.color.dark));
        binding.textviewMenuWidthLabel.setTextColor(
                ContextCompat.getColor(ctxt, R.color.dark));

        String startButtonFormat = getResources().getString(R.string.start);

        binding.buttonMenuBigstart.setOnClickListener(v -> {
            if (gameInProgress) {
                new ResumeGameDialog((d, i) -> sendStartgameAction(
                        getResources().getInteger(R.integer.big_numbombs),
                        getResources().getInteger(R.integer.big_height),
                        getResources().getInteger(R.integer.big_width),
                        ""), this).show();
            } else {
                sendStartgameAction(
                        getResources().getInteger(R.integer.big_numbombs),
                        getResources().getInteger(R.integer.big_height),
                        getResources().getInteger(R.integer.big_width),
                        "");
            }
        });
        binding.buttonMenuBigstart.setText(String.format(startButtonFormat,
                getResources().getString(R.string.big)));

        binding.buttonMenuMediumstart.setOnClickListener(v -> {
            if (gameInProgress) {
                new ResumeGameDialog((d, i) -> sendStartgameAction(
                        getResources().getInteger(R.integer.medium_numbombs),
                        getResources().getInteger(R.integer.medium_height),
                        getResources().getInteger(R.integer.medium_width),
                        ""), this).show();
            } else {
                sendStartgameAction(
                        getResources().getInteger(R.integer.medium_numbombs),
                        getResources().getInteger(R.integer.medium_height),
                        getResources().getInteger(R.integer.medium_width),
                        "");
            }
        });
        binding.buttonMenuMediumstart.setText(String.format(startButtonFormat,
                getResources().getString(R.string.medium)));

        binding.buttonMenuSmallstart.setOnClickListener(v -> {
            if (gameInProgress) {
                new ResumeGameDialog((d, i) -> sendStartgameAction(
                        getResources().getInteger(R.integer.small_numbombs),
                        getResources().getInteger(R.integer.small_height),
                        getResources().getInteger(R.integer.small_width),
                        ""), this).show();
            } else {
                sendStartgameAction(
                        getResources().getInteger(R.integer.small_numbombs),
                        getResources().getInteger(R.integer.small_height),
                        getResources().getInteger(R.integer.small_width),
                        "");
            }
        });
        binding.buttonMenuSmallstart.setText(String.format(startButtonFormat,
                getResources().getString(R.string.small)));

        binding.buttonMenuCustomstart.setOnClickListener(v -> {
            if (gameInProgress) {
                new ResumeGameDialog((d, i) -> {
                    EditText bombs = binding.getRoot().findViewById(R.id.edittext_menu_bombs_input);
                    EditText height = binding.getRoot().findViewById(R.id.edittext_menu_height_input);
                    EditText width = binding.getRoot().findViewById(R.id.edittext_menu_width_input);
                    sendStartgameAction(Integer.parseInt(String.valueOf(bombs.getText()))
                            , Integer.parseInt(String.valueOf(height.getText()))
                            , Integer.parseInt(String.valueOf(width.getText())), "");
                }, this).show();
            } else {
                EditText bombs = binding.getRoot().findViewById(R.id.edittext_menu_bombs_input);
                EditText height = binding.getRoot().findViewById(R.id.edittext_menu_height_input);
                EditText width = binding.getRoot().findViewById(R.id.edittext_menu_width_input);
                sendStartgameAction(Integer.parseInt(String.valueOf(bombs.getText()))
                        , Integer.parseInt(String.valueOf(height.getText()))
                        , Integer.parseInt(String.valueOf(width.getText())), "");
            }
        });
        binding.buttonMenuToScores.setOnClickListener(v
                -> NavHostFragment.findNavController(MenuFragment.this)
                .navigate(MenuFragmentDirections.actionScores()));

        binding.buttonMenuInfo.setOnClickListener(v -> {
            new InfoDialog(this).show();
        });
        binding.buttonMenuInfo.setDrawImage(new DrawInfo(
                ContextCompat.getColor(requireContext(), R.color.dark)));

        binding.buttonMenuSettings.setOnClickListener(v -> {
            new SettingsDialog(this, new SimpleSettingsManager(requireContext()) {
                public void viewStoredGames() {
                    new StoredGameDialog(MenuFragment.this, name -> {
                        MenuFragmentDirections.ActionStartgame action
                                = MenuFragmentDirections.actionStartgame();
                        action.setGameName(name);
                        NavHostFragment.findNavController(MenuFragment.this)
                                .navigate(action);
                    }).show();
                }
            }).show(true,true,true,true,
                    true,true,true,false,
                    true,false,true,true,false);
        });
        binding.buttonMenuSettings.setDrawImage(new DrawSettings(
                ContextCompat.getColor(requireContext(), R.color.dark),
                ContextCompat.getColor(requireContext(), R.color.transparent)));

        GameData gameData = new StoreGame(requireContext()).readGame();
        gameInProgress = gameData != null;

        getView().setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.background));

        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        float topButtonHeight = GameFragment.getButtonHeight(getView().getHeight(),
                                getView().getWidth());

                        ViewGroup.LayoutParams params = binding.buttonMenuInfo.getLayoutParams();
                        params.height = (int) topButtonHeight;
                        params.width = (int) (topButtonHeight * DrawInfo.PREFERRED_ASPECT_RATIO);
                        binding.buttonMenuInfo.setLayoutParams(params);

                        params = binding.buttonMenuSettings.getLayoutParams();
                        params.height = (int) topButtonHeight;
                        params.width = params.height;
                        binding.buttonMenuSettings.setLayoutParams(params);

                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void startSavedGame(){
        sendStartgameAction(-1,-1,-1,
                getResources().getString(R.string.current_game_file));
    }
}