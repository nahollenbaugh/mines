package com.nahollenbaugh.mines.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.collection.ArraySet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.fragment.NavHostFragment;

import com.nahollenbaugh.mines.R;
import com.nahollenbaugh.mines.databinding.FragmentScoresBinding;
import com.nahollenbaugh.mines.dialogs.BasicDialog;
import com.nahollenbaugh.mines.dialogs.StoredGameDialog;
import com.nahollenbaugh.mines.drawing.DrawBack;
import com.nahollenbaugh.mines.drawing.DrawInfo;
import com.nahollenbaugh.mines.drawing.DrawSettings;
import com.nahollenbaugh.mines.t.SimpleSettingsManager;
import com.nahollenbaugh.mines.views.InfoDialog;
import com.nahollenbaugh.mines.dialogs.SettingsDialog;

import static com.nahollenbaugh.mines.storage.StoredDataStrings.*;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

public class ScoresFragment extends Fragment {

    private FragmentScoresBinding binding;
    private SharedPreferences pref;
    protected BoardsizesAdapter boardsizesAdapter;
    protected String displayingKey;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentScoresBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {


                        float viewWidth = getView().getWidth();
                        float viewHeight = getView().getHeight();
                        float topButtonHeight = GameFragment.getButtonHeight(viewHeight, viewWidth);

                        ViewGroup.LayoutParams params = binding.endSpace.getLayoutParams();
                        params.width = (int) (viewWidth * 0.05f);
                        binding.endSpace.setLayoutParams(params);

                        params = binding.startSpace.getLayoutParams();
                        params.width = (int) (viewWidth * 0.05f);
                        binding.startSpace.setLayoutParams(params);

                        params = binding.bottomSpace.getLayoutParams();
                        params.height = (int) (Math.max(viewWidth * 0.05f, viewHeight * 0.02f));
                        binding.bottomSpace.setLayoutParams(params);

                        params = binding.listScoreSizes.getLayoutParams();
                        params.height = (int) (viewHeight * 0.3f);
                        binding.listScoreSizes.setLayoutParams(params);

                        params = binding.buttonScoreFragmentInfo.getLayoutParams();
                        params.height = (int)(viewHeight * GameFragment.TOP_BUTTONS_HEIGHT);
                        params.width = params.height * DrawInfo.PREFERRED_ASPECT_RATIO;
                        binding.buttonScoreFragmentInfo.setLayoutParams(params);

                        params = binding.buttonScoreFragmentBack.getLayoutParams();
                        params.height = (int)(topButtonHeight);
                        params.width = params.height;
                        binding.buttonScoreFragmentBack.setLayoutParams(params);

                        params = binding.buttonScoreFragmentSettings.getLayoutParams();
                        params.height = (int)(topButtonHeight);
                        params.width = params.height;
                        binding.buttonScoreFragmentSettings.setLayoutParams(params);

                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
        accessStuff();
        createBoardSizesList();
        binding.listScores.setVisibility(View.INVISIBLE);

        int dark = ContextCompat.getColor(requireContext(),R.color.dark);
        for (int i = 0; i < rankGriditemIds.length; i++){
            ((TextView)getView().findViewById(rankGriditemIds[i])).setTextColor(dark);
            ((TextView)getView().findViewById(userGriditemIds[i])).setTextColor(dark);
            ((TextView)getView().findViewById(scoreGriditemIds[i])).setTextColor(dark);
        }
        binding.listScoresRankTitle.setTextColor(dark);
        binding.listScoresUserTitle.setTextColor(dark);
        binding.listScoresScoreTitle.setTextColor(dark);

        binding.buttonResetScores.setOnClickListener((v) -> new BasicDialog(
                getResources().getString(R.string.reset_scores_message),
                getResources().getString(R.string.reset_scores_positiveButton),
                getResources().getString(R.string.reset_scores_negativeButton),
                (a,b) -> clearAllScores(),
                BasicDialog.doNothing,
                ScoresFragment.this
        ).show());

        binding.buttonScoreFragmentBack.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(ScoresFragmentDirections.actionScorestomenu());
        });
        binding.buttonScoreFragmentBack.setDrawImage(new DrawBack(dark));

        binding.buttonScoreFragmentInfo.setOnClickListener(v -> {
            new InfoDialog(this).show();
        });
        binding.buttonScoreFragmentInfo.setDrawImage(new DrawInfo(dark));

        binding.buttonScoreFragmentSettings.setOnClickListener(v -> {
            new SettingsDialog(this, new SimpleSettingsManager(requireContext()) {
                public void viewStoredGames() {
                    new StoredGameDialog(ScoresFragment.this, gameName -> {
                        ScoresFragmentDirections.ActionScorestogame action
                                = ScoresFragmentDirections.actionScorestogame();
                        action.setGameName(gameName);
                        NavHostFragment.findNavController(ScoresFragment.this)
                                .navigate(action);
                    }).show(getParentFragmentManager(), null);
                }
            }).show(true,
                    true, true, true, false,
                    true, false, true, true,
                    false);
        });
        binding.buttonScoreFragmentSettings.setDrawImage(new DrawSettings(dark,
                ContextCompat.getColor(requireContext(), R.color.transparent)));

        getView().setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.background));
    }

    public void createBoardSizesList(){
        Set<String> scoreSizes = pref.getStringSet(scoreSizesKey, null);
        if (scoreSizes == null) {

        } else {
            String[] vs = new String[scoreSizes.size()];
            int i=0;
            for (String s : scoreSizes){
                vs[i] = toDisplayText(s);
                i++;
            }

            Arrays.sort(vs, new Comparator<String>(){
                public int compare(String a, String b){
                    if (a.equals(b)) return 0;
                    if (a.equals(smallString)) return -1;
                    if (b.equals(smallString)) return 1;
                    if (a.equals(mediumString)) return -1;
                    if (b.equals(mediumString)) return 1;
                    if (a.equals(bigString)) return -1;
                    if (b.equals(bigString)) return 1;
                    return String.CASE_INSENSITIVE_ORDER.compare(a,b);
                }
            });
            boardsizesAdapter = new BoardsizesAdapter(
                    getContext(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    vs);
            binding.listScoreSizes.setAdapter(boardsizesAdapter);
        }
    }

    int[] userGriditemIds = new int[] {R.id.list_scores_user_0, R.id.list_scores_user_1,
            R.id.list_scores_user_2, R.id.list_scores_user_3, R.id.list_scores_user_4,
            R.id.list_scores_user_5, R.id.list_scores_user_6, R.id.list_scores_user_7,
            R.id.list_scores_user_8, R.id.list_scores_user_9};
    int[] scoreGriditemIds = new int[] {R.id.list_scores_score_0, R.id.list_scores_score_1,
            R.id.list_scores_score_2, R.id.list_scores_score_3, R.id.list_scores_score_4,
            R.id.list_scores_score_5, R.id.list_scores_score_6, R.id.list_scores_score_7,
            R.id.list_scores_score_8, R.id.list_scores_score_9};
    int[] rankGriditemIds = new int[] {R.id.list_scores_rank_0, R.id.list_scores_rank_1,
            R.id.list_scores_rank_2, R.id.list_scores_rank_3, R.id.list_scores_rank_4,
            R.id.list_scores_rank_5, R.id.list_scores_rank_6, R.id.list_scores_rank_7,
            R.id.list_scores_rank_8, R.id.list_scores_rank_9};


    protected void accessStuff(){
        boldColor = ContextCompat.getColor(requireContext(), R.color.dark);
        nonboldColor = ContextCompat.getColor(requireContext(), R.color.covered);
        pref = ((FragmentActivity)getContext()).getPreferences(Context.MODE_PRIVATE);
    }

    public static String toDisplayText(String key){
        if (key.equals(smallStringKey)){
            return smallString;
        } else if (key.equals(mediumStringKey)){
            return mediumString;
        } else if (key.equals(bigStringKey)){
            return bigString;
        } else {
            return key;
        }
    }

    public static String toKey(String displayText){
        if (displayText.equals(smallString)){
            return smallStringKey;
        } else if (displayText.equals(mediumString)){
            return mediumStringKey;
        } else if (displayText.equals(bigString)){
            return bigStringKey;
        } else {
            return displayText;
        }
    }

    public void clearAllScores(){
        for (String boardsizeKey : pref.getStringSet(scoreSizesKey, null)){
            clearScores(boardsizeKey);
        }
    }
    public void clearScores(String boardsizeKey){
        binding.slaw.setText(boardsizeKey);
        SharedPreferences.Editor editor = pref.edit();
        for (int i = 0; i< 10; i++) {
            String scoreKey = String.format(scoreKeyFormat, boardsizeKey, i);
            editor.remove(scoreKey);
        }
        Set<String> boardsizes = pref.getStringSet(scoreSizesKey,null);
        Set<String> newBoardsizes = new ArraySet<>(boardsizes.size());
        newBoardsizes.addAll(boardsizes);
        newBoardsizes.remove(boardsizeKey);
        editor.putStringSet(scoreSizesKey,newBoardsizes);
        editor.apply();
        createBoardSizesList();

        if (displayingKey != null && displayingKey.equals(boardsizeKey)){
            binding.listScores.setVisibility(View.INVISIBLE);
        }
    }
    public void setDisplayingScores(String boardsizeKey){
        binding.listScores.setVisibility(View.VISIBLE);
        displayingKey = boardsizeKey;

        int i = 0;
        for (; i < 10; i++) {
            String scoreKey = String.format(scoreKeyFormat, boardsizeKey, i);
            String user = pref.getString(String.format(scoreOwnerKey, scoreKey), "o");
            int score = pref.getInt(scoreKey, Integer.MAX_VALUE);
            if (score == Integer.MAX_VALUE) {
                break;
            }
            ((TextView) getView().findViewById(userGriditemIds[i])).setText(user);
            ((TextView) getView().findViewById(scoreGriditemIds[i])).setText(score + "");
        }
        for (; i < 10; i++){
            ((TextView) getView().findViewById(userGriditemIds[i])).setText(
                    getResources().getString(R.string.list_scores_user_blank));
            ((TextView) getView().findViewById(scoreGriditemIds[i])).setText(
                    getResources().getString(R.string.list_scores_score_blank));
        }
    }

    protected void setListScoresSize(){
        float currentTextSize = binding.listScoresRankTitle.getTextSize();

        int ms = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        binding.listScoresUser7.measure(ms,ms);

        View thisView = getView();
        TextView item;
        float currentRankWidth = binding.listScoresRankTitle.getWidth();
        for (int i = 0; i < rankGriditemIds.length; i++){
            item = (TextView)(thisView.findViewById(rankGriditemIds[i]));
            item.measure(ms,ms);
            currentRankWidth = Math.max(currentRankWidth,item.getMeasuredWidth());
        }
        float currentUserWidth = binding.listScoresUserTitle.getWidth();
        for (int i = 0; i < userGriditemIds.length; i++){
            item = (TextView)(thisView.findViewById(userGriditemIds[i]));
            item.measure(ms,ms);
            currentUserWidth = Math.max(currentUserWidth,item.getMeasuredWidth());
        }
        float currentScoreWidth = binding.listScoresScoreTitle.getWidth();
        for (int i = 0; i < scoreGriditemIds.length; i++){
            item = (TextView)(thisView.findViewById(scoreGriditemIds[i]));
            item.measure(ms,ms);
            currentScoreWidth = Math.max(currentScoreWidth,item.getMeasuredWidth());
        }

        float newTextSize = currentTextSize
                * binding.listScores.getWidth()
                / (currentRankWidth+currentUserWidth+currentScoreWidth)
                / 1.3f;
        newTextSize = Math.min(newTextSize,
                0.6f*getView().getHeight()*currentTextSize/binding.listScores.getHeight());

        binding.listScoresRankTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,newTextSize);
        for (int i = 0; i < rankGriditemIds.length; i++) {
            item = (TextView) (thisView.findViewById(rankGriditemIds[i]));
            item.setTextSize(TypedValue.COMPLEX_UNIT_PX, newTextSize);
        }

        binding.listScoresUserTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,newTextSize);
        for (int i = 0; i < userGriditemIds.length; i++) {
            item = (TextView) (thisView.findViewById(userGriditemIds[i]));
            item.setTextSize(TypedValue.COMPLEX_UNIT_PX, newTextSize);
        }

        binding.listScoresScoreTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,newTextSize);
        for (int i = 0; i < scoreGriditemIds.length; i++) {
            item = (TextView) (thisView.findViewById(scoreGriditemIds[i]));
            item.setTextSize(TypedValue.COMPLEX_UNIT_PX, newTextSize);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    protected static class BoldUnboldString{
        public String string;
        public boolean isBold;
        public BoldUnboldString(String string){
            this.string = string;
            this.isBold = false;
        }
        @NonNull
        @Override
        public String toString(){
            return string;
        }
    }
    protected static BoldUnboldString[] wrapStringArray(String[] values){
        BoldUnboldString[] wrapped = new BoldUnboldString[values.length];
        for (int i = 0; i < values.length; i++){
            wrapped[i] = new BoldUnboldString(values[i]);
        }
        return wrapped;
    }
    protected int boldColor;
    protected int nonboldColor;
    protected class BoardsizesAdapter extends ArrayAdapter<BoldUnboldString> {
        public BoardsizesAdapter(Context context, int resource, int textViewResourceId,
                                 String[] values){
            super(context,resource,textViewResourceId,wrapStringArray(values));
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView v = (TextView)(super.getView(position, convertView, parent));
            v.setTypeface(null,getItem(position).isBold?Typeface.BOLD:Typeface.NORMAL);
            v.setTextColor(getItem(position).isBold?boldColor:nonboldColor);
            v.setOnLongClickListener(view -> {
                String boardsizeDisplay = v.getText().toString();
                new BasicDialog(
                        String.format(
                                getResources().getString(R.string.reset_single_score_message),
                                boardsizeDisplay),
                        getResources().getString(R.string.reset_single_score_positiveButton),
                        getResources().getString(R.string.reset_single_score_negativeButton),
                        (a,b) -> clearScores(toKey(boardsizeDisplay)),
                        BasicDialog.doNothing,
                        ScoresFragment.this
                ).show();
                return true;
            });
            v.setOnClickListener(w -> {
                if (getItem(position).isBold){
                    return;
                }
                setDisplayingScores(toKey(v.getText().toString()));
                for (int i = 0; i < boardsizesAdapter.getCount(); i++){
                    boardsizesAdapter.getItem(i).isBold=false;
                }
                getItem(position).isBold=true;
                boardsizesAdapter.notifyDataSetChanged();
                setListScoresSize();
            });
            return v;
        }


    }
}