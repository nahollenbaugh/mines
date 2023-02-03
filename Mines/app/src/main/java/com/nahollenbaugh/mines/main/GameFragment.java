package com.nahollenbaugh.mines.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.nahollenbaugh.mines.R;
import com.nahollenbaugh.mines.databinding.FragmentGameBinding;
import com.nahollenbaugh.mines.dialogs.StoredGameDialog;
import com.nahollenbaugh.mines.drawing.DrawFloppy;
import com.nahollenbaugh.mines.drawing.DrawInfo;
import com.nahollenbaugh.mines.drawing.DrawQuestionMark;
import com.nahollenbaugh.mines.drawing.DrawWithBackground;
import com.nahollenbaugh.mines.storage.StoreSettings;
import com.nahollenbaugh.mines.storage.StoredGamesManager;
import com.nahollenbaugh.mines.dialogs.InfoDialog;
import com.nahollenbaugh.mines.dialogs.SettingsDialog;
import com.nahollenbaugh.mines.drawing.DrawBack;
import com.nahollenbaugh.mines.drawing.DrawNumberUtil;
import com.nahollenbaugh.mines.drawing.DrawResetFace;
import com.nahollenbaugh.mines.drawing.DrawSettings;
import com.nahollenbaugh.mines.drawing.DrawZoom;
import com.nahollenbaugh.mines.gamelogic.Game;
import com.nahollenbaugh.mines.gamelogic.HintGame;
import com.nahollenbaugh.mines.gamelogic.Solver;
import com.nahollenbaugh.mines.drawing.DrawFlag;
import com.nahollenbaugh.mines.gamelogic.GameData;
import com.nahollenbaugh.mines.gamelogic.GameWatcher;
import com.nahollenbaugh.mines.dialogs.BasicDialog;
import com.nahollenbaugh.mines.dialogs.SaveScoreDialogFragment;
import com.nahollenbaugh.mines.dialogs.StringInputDialogFragment;
import com.nahollenbaugh.mines.storage.StoreGame;
import com.nahollenbaugh.mines.t.SettingsManager;
import com.nahollenbaugh.mines.storage.StoredDataStrings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameFragment extends Fragment implements GameWatcher, SettingsManager {

    FragmentGameBinding binding;
    protected int chordMode = SettingsManager.CHORD_MODE_DEFAULT;
    protected boolean doubleTapFlagsMode = SettingsManager.DOUBLE_TAP_FLAGS_DEFAULT;
    protected int doubleTapDelay = SettingsManager.DOUBLE_TAP_DELAY_DEFAULT;
    protected boolean noguessMode = SettingsManager.NOGUESS_MODE_DEFAULT;
    protected boolean questionMarkMode = SettingsManager.QUESTION_MARK_MODE_DEFAULT;
    protected boolean allowingZoom = SettingsManager.ZOOM_MODE_DEFAULT;
    protected boolean longPressFlags = SettingsManager.LONG_PRESS_FOR_FLAGS_MODE_DEFAULT;
    protected boolean confirmResetFace = SettingsManager.CONFIRM_RESET_FACE_DEFAULT;
    protected boolean hintBomb = SettingsManager.HINT_BOMB_MODE_DEFAULT;
    public float fixedZoom = 0;
    protected boolean resumingGame;

    public static final float TOP_BUTTONS_HEIGHT = 1f/18f;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentGameBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    protected static float timerRatio = (4f + 4f * DrawNumberUtil.PREFERRED_SPACING
            + DrawNumberUtil.PREFERRED_THICKNESS
            + 2f * DrawNumberUtil.PREFERRED_SEPARATION)
            * DrawNumberUtil.PREFERRED_ASPECT_RATIO;
    protected static float buttonRatio = 1f;
    protected static float counterRatio = (3f + 2f * DrawNumberUtil.PREFERRED_SPACING)
            * DrawNumberUtil.PREFERRED_ASPECT_RATIO;
    protected static float ratio = timerRatio + 3f * buttonRatio + counterRatio * 2.5f;
    public static float getButtonHeight(float viewHeight, float viewWidth) {
        float height = viewHeight * GameFragment.TOP_BUTTONS_HEIGHT;
        if (ratio * height > viewWidth) {
            height = viewWidth / ratio;
        }
        return height;
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context ctxt = getContext();
        if (ctxt == null) {
            throw new RuntimeException();
        }
        binding.gameView.gameFragment = this;
        StoreSettings.read(requireContext(), this);

        String gameName = GameFragmentArgs.fromBundle(getArguments()).getGameName();
        if (gameName != null && !gameName.equals("")){
            StoreGame storeGame;
            if (gameName.equals(getResources().getString(R.string.current_game_file))) {
                storeGame = new StoreGame(requireContext());
            } else {
                storeGame = new StoreGame(requireContext(), gameName);
            }
            GameData gameData = storeGame.readGame();
            if (gameData != null) {
                binding.gameView.setGame(gameData.game);
                binding.timer.setStartingTime(gameData.time);
                resumingGame = true;
            } else {
                new BasicDialog(
                        getResources().getString(R.string.dialog_gameNotFound_message),
                        getResources().getString(R.string.dialog_gameNotFound_positive),
                        null,
                        (di, i) -> NavHostFragment.findNavController(this)
                                .navigate(GameFragmentDirections.actionBackToMenu()),
                        null,
                        GameFragment.this);

            }
        }
        if (!resumingGame) {
            binding.gameView.setGameParameters(
                    GameFragmentArgs.fromBundle(getArguments()).getNumbombs(),
                    GameFragmentArgs.fromBundle(getArguments()).getHeight(),
                    GameFragmentArgs.fromBundle(getArguments()).getWidth());
            binding.gameView.reset();
        }

        binding.buttonIngameRestart.setDrawImage(new DrawResetFace(
                ContextCompat.getColor(ctxt, R.color.face_head),
                ContextCompat.getColor(ctxt, R.color.face_face),
                ContextCompat.getColor(ctxt, R.color.button_background)));
        binding.buttonIngameRestart.setOnLongClickListener(v -> {
            resetGame();
            return true;
        });
        binding.buttonIngameRestart.setOnClickListener(v -> {
            if (confirmResetFace) {
                resetDialog();
            } else {
                resetGame();
            }
        });
        binding.buttonFlag.setUpImage(new DrawWithBackground(
                ContextCompat.getColor(ctxt, R.color.button_background),
                new DrawFlag(
                        ContextCompat.getColor(ctxt, R.color.flag_ground),
                        ContextCompat.getColor(ctxt, R.color.flag_flagpole),
                        ContextCompat.getColor(ctxt, R.color.flag_flag))));
        binding.buttonFlag.setDownImage(new DrawWithBackground(
                ContextCompat.getColor(ctxt, R.color.button_background),
                new DrawFlag(
                        ContextCompat.getColor(ctxt, R.color.flag_pressed),
                        ContextCompat.getColor(ctxt, R.color.flag_pressed),
                        ContextCompat.getColor(ctxt, R.color.flag_pressed))));
        binding.buttonFlag.setOnClickListener(v -> {
            if (binding.gameView.getGame().isWon() || binding.gameView.getGame().isLost()){
                return;
            }
            if (binding.buttonFlag.isUp()) {
                binding.counterFlags.onFlaggingColor();
                binding.timer.onFlaggingColor();
            } else {
                binding.counterFlags.offFlaggingColor();
                binding.timer.offFlaggingColor();
            }
            binding.gameView.invalidate();
        });
        binding.buttonFlag.setOnLongClickListener(v -> {
            toggleQuestionMarkMode();
            return true;
        });
        binding.buttonZoom.setDrawImage(new DrawZoom(
                ContextCompat.getColor(ctxt, R.color.zoom),
                ContextCompat.getColor(ctxt, R.color.button_background)));
        binding.buttonZoom.setOnClickListener(v -> {
            if (fixedZoom == 0) {
                binding.gameView.setZoom(1f);
            } else {
                if (binding.gameView.getZoom() == fixedZoom) {
                    binding.gameView.setZoom(1f);
                } else {
                    binding.gameView.setZoom(fixedZoom);
                }
            }
        });
        binding.buttonZoom.setOnLongClickListener(v -> {
            fixedZoom = binding.gameView.getZoom();
            return true;
        });
        binding.buttonHint.setDrawImage(new DrawQuestionMark(
                ContextCompat.getColor(ctxt, R.color.hint),
                ContextCompat.getColor(ctxt, R.color.button_background)));
        binding.buttonHint.setOnClickListener(v -> {
            if (binding.gameView.getGame().isWon() || binding.gameView.getGame().isLost()){
                return;
            }
            showHint();
        });
        binding.buttonHint.setOnLongClickListener(v -> {
            if (binding.gameView.getGame().isWon() || binding.gameView.getGame().isLost()){
                return true;
            }
            showAllHints();
            return true;
        });
        binding.counterFlags.setCount(binding.gameView.getGame().flagsLeft());

        binding.buttonSettings.setDrawImage(new DrawSettings(
                ContextCompat.getColor(ctxt, R.color.dark),
                ContextCompat.getColor(ctxt, R.color.transparent)));
        binding.buttonSettings.setOnClickListener(v -> {
                    new SettingsDialog(this, this).show(true, true,
                            true, true,
                            true, true,
                            true, true, true, true,
                            true, true, false);
                });

        binding.buttonBack.setDrawImage(new DrawBack(
                ContextCompat.getColor(ctxt, R.color.dark)));
        binding.buttonBack.setOnClickListener(v -> NavHostFragment.findNavController(this)
                .navigate(GameFragmentDirections.actionBackToMenu()));

        binding.buttonInfo.setDrawImage(new DrawInfo(
                ContextCompat.getColor(ctxt, R.color.dark)));
        binding.buttonInfo.setOnClickListener(v -> {
            new InfoDialog(this).show();
        });

        new StoreGame(requireContext()).writeNoGame();

        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
////        https://stackoverflow.com/questions/6798867/android-how-to-programmatically
////        -set-the-size-of-a-layout
                        float height = GameFragment.getButtonHeight(view.getHeight(),
                                view.getWidth());

                        ViewGroup.LayoutParams params = binding.timer.getLayoutParams();
                        params.height = (int) height;
                        params.width = (int) (height * timerRatio);
                        binding.timer.setLayoutParams(params);
                        params = binding.buttonFlag.getLayoutParams();
                        params.height = (int) height;
                        params.width = (int) (height * buttonRatio);
                        binding.buttonFlag.setLayoutParams(params);
                        params = binding.buttonIngameRestart.getLayoutParams();
                        params.height = (int) height;
                        params.width = (int) (height * buttonRatio);
                        binding.buttonIngameRestart.setLayoutParams(params);
                        params = binding.buttonZoom.getLayoutParams();
                        params.height = (int) height;
                        params.width = (int) (height * buttonRatio);
                        binding.buttonZoom.setLayoutParams(params);
                        params = binding.counterFlags.getLayoutParams();
                        params.height = (int) height;
                        params.width = (int) (height * counterRatio);
                        binding.counterFlags.setLayoutParams(params);
                        params = binding.buttonSettings.getLayoutParams();
                        params.height = (int) height;
                        params.width = (int) (height * buttonRatio);
                        binding.buttonSettings.setLayoutParams(params);
                        params = binding.buttonBack.getLayoutParams();
                        params.height = (int) height;
                        params.width = (int) (height * buttonRatio);
                        binding.buttonBack.setLayoutParams(params);
                        params = binding.buttonHint.getLayoutParams();
                        params.height = (int) height;
                        params.width = (int) (height * buttonRatio);
                        binding.buttonHint.setLayoutParams(params);
                        params = binding.buttonInfo.getLayoutParams();
                        params.height = (int) height;
                        params.width = (int) (height * DrawInfo.PREFERRED_ASPECT_RATIO);
                        binding.buttonInfo.setLayoutParams(params);
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
        binding.gameView.gameFragment = this;
        getView().setBackgroundColor(
                ContextCompat.getColor(ctxt, R.color.background));

        fixedZoom = ((FragmentActivity) getContext()).getPreferences(Context.MODE_PRIVATE)
                .getFloat(formatZoomKey(binding.gameView.getGame().getNumbombs(),
                        binding.gameView.getGame().getWidth(),
                        binding.gameView.getGame().getHeight()), 0);

        binding.buttonSaveScore.setVisibility(View.INVISIBLE);
    }
    protected String formatZoomKey(int bombs, int width, int height) {
        return String.format(
                getResources().getString(R.string.zoom_level_key),
                StoredDataStrings.formatSizeKey(bombs, width, height));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.timer.close();
    }
    @Override
    public void onPause() {
        super.onPause();
        Game game = binding.gameView.getGame();
        if (game.isOpen() && !game.isWon() && !game.isLost()) {
            new StoreGame(requireContext()).writeGame(new GameData(game, binding.timer.stop()));
        }
        binding.gameView.drawAllCovered(true);
        StoreSettings.store(requireContext(), this);
        ((FragmentActivity) getContext()).getPreferences(Context.MODE_PRIVATE)
                .edit()
                .putFloat(formatZoomKey(
                        binding.gameView.getGame().getNumbombs(),
                        binding.gameView.getGame().getWidth(),
                        binding.gameView.getGame().getHeight()),
                        fixedZoom)
                .apply();

    }
    @Override
    public void onResume() {
        super.onResume();
        Game game = binding.gameView.getGame();
        if (game.isOpen() && !game.isLost() && !game.isWon()) {
            binding.timer.start();
        }
        binding.gameView.drawAllCovered(false);
        StoreSettings.read(requireContext(), this);
    }

    public int getChordMode(){
        return chordMode;
    }
    public void setChordMode(int chordMode){
        this.chordMode = chordMode;
    }
    public boolean isDoubleTapFlagsMode(){
        return doubleTapFlagsMode;
    }
    public void toggleDoubleTapFlagsMode(){
        doubleTapFlagsMode = !doubleTapFlagsMode;
    }
    public void setDoubleTapDelay(int delay){
        this.doubleTapDelay = delay;
        binding.gameView.setDoubleTapDelay(SettingsManager.DOUBLE_TAP_DELAYS[delay]);
    }
    public int getDoubleTapDelay(){
        return doubleTapDelay;
    }
    public boolean isNoguessMode() {
        return noguessMode;
    }
    public void toggleNoguessMode() {
        noguessMode = !noguessMode;
//        new BasicDialog(requireContext().getString(R.string.newGame_message),
//                requireContext().getString(R.string.newGame_positive),
//                requireContext().getString(R.string.newGame_negative),
//                (dialogInterface,i) -> resetGame(),
//                (dialogInterface,i) -> {},
//                this).show();
    }
    public void toggleQuestionMarkMode() {
        questionMarkMode = !questionMarkMode;
    }
    public boolean isQuestionMarkMode() {
        return questionMarkMode;
    }
    public void toggleZoomMode() {
        allowingZoom = !allowingZoom;
        if (allowingZoom) {
            binding.gameView.turnZoomOn();
        } else {
            binding.gameView.turnZoomOff();
        }
    }
    public boolean isZoomMode() {
        return allowingZoom;
    }
    public void toggleLongPressForFlagMode() {
        longPressFlags = !longPressFlags;
    }
    public boolean isLongPressFlagsMode() {
        return longPressFlags;
    }
    public void setSmallScrollSensitivity(float sensitivity) {
        binding.gameView.setSmallScrollSensitivity(sensitivity);
    }
    public float getSmallScrollSensitivity() {
        return binding.gameView.getSmallScrollSensitivity();
    }
    public void toggleConfirmResetFace() {
        confirmResetFace = !confirmResetFace;
    }
    public boolean isConfirmResetFace() {
        return confirmResetFace;
    }
    public void storeGame() {
        if (binding.gameView.getGame() != null) {
            storeGame(false);
        }
    }
    protected void storeGame(boolean hasEnteredInvalid) {
        StringInputDialogFragment dialog = new StringInputDialogFragment(
                hasEnteredInvalid
                        ? getResources().getString(R.string.storeGame_invalidMessage)
                        : getResources().getString(R.string.storeGame_message),
                getResources().getString(R.string.storeGame_label),
                getResources().getString(R.string.storeGame_positive),
                getResources().getString(R.string.storeGame_negative));
        dialog.setPositiveListener((dialogInterface, i) -> {
            String name = dialog.readEnteredText();
            if (StoredDataStrings.isReservedFileName(name)) {
                storeGame(true);
            } else {
                StoredGamesManager.addGame(name, new GameData(
                        binding.gameView.getGame(), binding.timer.value()), requireContext());
            }
        });
        dialog.show(getParentFragmentManager(), "");
    }
    public void viewStoredGames() {
        new StoredGameDialog(this, gameName -> {
            resetGame(new StoreGame(requireContext(),gameName).readGame());
        }).show(getParentFragmentManager(), null);
    }
    public void setZoomLevel(float zoom) {
        fixedZoom = zoom / 2f + 1f;
    }
    public float getZoomLevel() {
        return (fixedZoom - 1) * 2f;
    }
    public void toggleHintBombMode(){
        hintBomb = !hintBomb;
        if (!hintBomb && hints != null){
            for (HintGame.Hint hint : binding.gameView.stopHints()){
                if (hint.isSafe){
                    binding.gameView.showHint(hint);
                }
            }

        }
    }
    public boolean isHintBombMode(){
        return hintBomb;
    }
    public boolean isFlagging() {
        return !binding.buttonFlag.isUp();
    }

    protected List<HintGame.Hint> hints;
    protected int lastHint = 0;
    public void showHint() {
        if (hints == null) {
            playHintGame();
            lastHint = -1;
        }
        if (hints.size() == 0) {
            displayNoHints();
            return;
        }
        lastHint = lastHint + 1;
        if (hints.size() <= lastHint) {
            lastHint = 0;
        }
        if (!isHintBombMode()) {
            while (!hints.get(lastHint).isSafe) {
                lastHint = lastHint + 1;
                if (hints.size() <= lastHint) {
                    lastHint = 0;
                }
            }
        }
        binding.gameView.stopHints();
        binding.gameView.showHint(hints.get(lastHint));
    }
    public void showAllHints(){
        if (hints == null){
            playHintGame();
        }
        for (HintGame.Hint h : hints){
            if (isHintBombMode() || h.isSafe) {
                binding.gameView.showHint(h);
            }
        }
    }
    protected void playHintGame(){
        Game g = binding.gameView.getGame();
        if (!g.isOpen()) {
            hints = new ArrayList<>();
            return;
        }
        HintGame hg = new HintGame(g.getBombs(), g.getFlags(), g.getUncovereds(),
                g.getQuestionMarks());
        Solver.play(hg);
        hints = hg.hints;
    }
    protected void displayNoHints(){
        Snackbar.make(binding.gameView,R.string.noHint_message,Snackbar.LENGTH_SHORT).show();
    }

    public void resetGame() {
        binding.gameView.reset();
        binding.timer.reset();
        binding.buttonFlag.setState(true);
        binding.counterFlags.setCount(binding.gameView.getGame().getNumbombs());
        binding.counterFlags.offFlaggingColor();
        binding.timer.offFlaggingColor();
        setResetFace(DrawResetFace.HAPPY);
        binding.buttonSaveScore.setVisibility(View.INVISIBLE);
        workingScoreSaver = null;
        hints = null;
    }
    public void resetGame(GameData gd) {
        resetGame();
        binding.gameView.setGame(gd.game);
        binding.timer.setStartingTime(gd.time);
        binding.counterFlags.setCount(gd.game.flagsLeft());
        if (gd.game.isOpen()) {
            binding.timer.start();
        }
    }
    public void resetDialog() {
        new BasicDialog(
                getResources().getString(R.string.newGame_message),
                getResources().getString(R.string.newGame_positive),
                getResources().getString(R.string.newGame_negative),
                (dialogInterface, i) -> resetGame(),
                BasicDialog.doNothing,
                GameFragment.this
        ).show();
    }

    public void setResetFace(int state) {
        ((DrawResetFace) binding.buttonIngameRestart.getDrawImage()).state = state;
        binding.buttonIngameRestart.invalidate();
    }
    public void alertGameChange(int change) {
        switch (change) {
            case GameWatcher.CHANGE_FLAGGED:
                hints = null;
                binding.gameView.stopHints();
                binding.counterFlags.decrease();
                break;
            case GameWatcher.CHANGE_UNFLAGGED:
                hints = null;
                binding.gameView.stopHints();
                binding.counterFlags.increase();
                break;
            case GameWatcher.CHANGE_STARTED:
                binding.timer.start();
                break;
            case GameWatcher.CHANGE_WON:
                setResetFace(DrawResetFace.COOL);
                binding.timer.stop();
                saveScore();
                break;
            case GameWatcher.CHANGE_LOST:
                binding.timer.stop();
                setResetFace(DrawResetFace.DEAD);
                break;
            case GameWatcher.CHANGE_UNCOVERED:
                hints = null;
                binding.gameView.stopHints();
                break;
        }
    }

    public boolean pendingSaveScore(){
        return workingScoreSaver != null;
    }
    public void addSaveScoreButton(){
        if (binding.buttonSaveScore.getVisibility() != View.INVISIBLE){
            return;
        }
        binding.buttonSaveScore.setVisibility(View.VISIBLE);
        binding.buttonSaveScore.setDrawImage(new DrawWithBackground(
                ContextCompat.getColor(requireContext(), R.color.background), new DrawFloppy(
                ContextCompat.getColor(requireContext(),R.color.dark))));
        binding.buttonSaveScore.setOnClickListener(v -> {
            binding.buttonSaveScore.setVisibility(View.INVISIBLE);
            new SaveScoreDialogFragment(GameFragment.this)
                    .show(getParentFragmentManager(), "");
        });
        ViewGroup.LayoutParams params = binding.buttonSaveScore.getLayoutParams();
        params.height = (int)(binding.gameView.getHeight() / 10f);
        params.width = params.height;
        binding.buttonSaveScore.setLayoutParams(params);
    }
    public void confirmSaveCurrentScore(String name) {
        workingScoreSaver.writeScore(name);
        workingScoreSaver = null;
    }
    ScoreSaver workingScoreSaver;
    public void saveScore() {
        Game game = binding.gameView.getGame();
        int score = binding.timer.value();
        workingScoreSaver = new ScoreSaver(
                game.getNumbombs(), game.getWidth(), game.getHeight(), score);
        if (workingScoreSaver.isHighScore()) {
            try {
                new SaveScoreDialogFragment(this).show(getParentFragmentManager(), "");
            } catch (Throwable e) {
                binding.slew.setText(e.toString());
            }
        } else {
            workingScoreSaver = null;
        }
    }
    protected class ScoreSaver {
        SharedPreferences pref;
        String scoreKeyFormat;

        int numbombs;
        int a;
        int b;
        int score;
        int[] values;
        int scoreAfterNew;

        String boardSizeString;

        public ScoreSaver(int numbombs, int height, int width, int score) {
            pref = ((FragmentActivity) getContext()).getPreferences(Context.MODE_PRIVATE);
            scoreKeyFormat = getResources().getString(R.string.score_key);

            this.numbombs = numbombs;
            a = (width < height) ? width : height;
            b = (a == width) ? height : width;
            this.score = score;
            boardSizeString = String.format(getResources().getString(R.string.board_size), numbombs, a, b);
        }

        protected boolean isHighScore() {
            int i = 9;
            values = new int[10];
            while (i >= 0) {
                values[i] = pref.getInt(String.format(scoreKeyFormat, boardSizeString, i), Integer.MAX_VALUE);
                if (values[i] < score) {
                    break;
                }
                i--;
            }
            scoreAfterNew = i + 1;
            return scoreAfterNew < 9;
        }

        public void writeScore(String user) {
            if (values == null && !isHighScore()) {
                return;
            }
            SharedPreferences.Editor editor = pref.edit();
            String scoreOwnerKey = getResources().getString(R.string.score_owner_key);
            for (int j = 9; j > scoreAfterNew && j > 0; j--) {
                String rankingKeyJ = String.format(scoreKeyFormat, boardSizeString, j);
                String rankingKeyJMinusOne = String.format(scoreKeyFormat, boardSizeString, j - 1);
                editor.putInt(rankingKeyJ, values[j - 1]);
                editor.putString(String.format(scoreOwnerKey, rankingKeyJ),
                        pref.getString(String.format(scoreOwnerKey, rankingKeyJMinusOne), ""));
            }
            String scoreKey = String.format(scoreKeyFormat, boardSizeString, scoreAfterNew);
            editor.putInt(scoreKey, score);
            editor.putString(String.format(scoreOwnerKey, scoreKey), user);
            if (values[0] == Integer.MAX_VALUE) {
                String scoreSizesKey = getResources().getString(R.string.score_sizes_key);
                Set<String> newScoreSizes = new HashSet<>();
                Set<String> scoreSizes = pref.getStringSet(scoreSizesKey, newScoreSizes);
                newScoreSizes.addAll(scoreSizes);
                newScoreSizes.add(boardSizeString);
                editor.putStringSet(scoreSizesKey, newScoreSizes);
            }
            editor.apply();
        }
    }

}
