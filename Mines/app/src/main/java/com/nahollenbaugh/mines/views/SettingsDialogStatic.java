package com.nahollenbaugh.mines.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
import com.nahollenbaugh.mines.drawing.DrawBomb;
import com.nahollenbaugh.mines.drawing.DrawCrossedOut;
import com.nahollenbaugh.mines.drawing.DrawFlag;
import com.nahollenbaugh.mines.drawing.DrawFloppy;
import com.nahollenbaugh.mines.drawing.DrawImage;
import com.nahollenbaugh.mines.drawing.DrawMinus;
import com.nahollenbaugh.mines.drawing.DrawNumberUtil;
import com.nahollenbaugh.mines.drawing.DrawPlus;
import com.nahollenbaugh.mines.drawing.DrawQuestionMark;
import com.nahollenbaugh.mines.drawing.DrawResetFace;
import com.nahollenbaugh.mines.drawing.DrawSmall;
import com.nahollenbaugh.mines.drawing.DrawZoom;
import com.nahollenbaugh.mines.t.SettingsManager;

public class SettingsDialogStatic extends DialogFragment {
    Fragment f;
    SettingsManager s;
    public SettingsDialogStatic(Fragment f, SettingsManager s){
        super();
        this.f = f;
        this.s = s;
    }

    boolean allowZoom;
    boolean longPressFlags;
    boolean useQuestionMarks;
    boolean fixedZoomLevel;
    boolean resetFace;
    boolean storeGame;
    boolean viewStoredGames;
    boolean scrollSensitivity;
    boolean hintBomb;

    public void show(boolean allowZoom, boolean longPressFlags, boolean useQuestionMarks,
                     boolean fixedZoomLevel, boolean resetFace, boolean storeGame,
                     boolean viewStoredGames, boolean hintBomb, boolean scrollSensitivity){
        this.allowZoom = allowZoom;
        this.longPressFlags = longPressFlags;
        this.useQuestionMarks = useQuestionMarks;
        this.fixedZoomLevel = fixedZoomLevel;
        this.resetFace = resetFace;
        this.storeGame = storeGame;
        this.viewStoredGames = viewStoredGames;
        this.hintBomb = hintBomb;
        this.scrollSensitivity = scrollSensitivity;
        show(f.getParentFragmentManager(),"");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Context ctxt = f.requireContext();
        ViewGroup view = (ViewGroup)requireActivity().getLayoutInflater().inflate(
                R.layout.view_settings,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        Dialog dialog = builder.create();
        view.setBackgroundColor(ContextCompat.getColor(ctxt,R.color.background));

        ViewGroup.LayoutParams params;
        DrawnButton button;
        TextView label;
        DrawImage draw;
        View space;

        float dim = f.getView().getWidth()/10f;
        int dark = ContextCompat.getColor(ctxt,R.color.dark);
        int unit = TypedValue.COMPLEX_UNIT_PX;
        float textSize = ((TextView)view.findViewById(R.id.settings_label_useQuestionMark))
                .getTextSize() * 1.5f;
        float spaceWidth=0.25f;
        float spaceHeight=1.5f;

        space = view.findViewById(R.id.settings_hspace_start);
        params = space.getLayoutParams();
        params.width=(int)(spaceWidth*dim);
        params.height=(int)((spaceHeight-1)*dim);
        space.setLayoutParams(params);
        space = view.findViewById(R.id.settings_hspace_end);
        params = space.getLayoutParams();
        params.width=(int)(spaceWidth*dim);
        params.height=(int)((spaceHeight-1)*dim);
        space.setLayoutParams(params);

        // Allow zoom
        if (allowZoom) {
            TwoStateDrawnButton zoomButton = view.findViewById(R.id.settings_button_zoom);
            draw = new DrawZoom(
                    ContextCompat.getColor(ctxt, R.color.zoom),
                    ContextCompat.getColor(ctxt, R.color.transparent));
            zoomButton.setUpImage(draw);
            zoomButton.setDownImage(new DrawCrossedOut(draw,
                    ContextCompat.getColor(ctxt, R.color.settings_crossout)));
            if (!s.isZoomMode()) {
                zoomButton.setState(false);
            }
            zoomButton.setOnClickListener(v -> s.toggleZoomMode());
            params = zoomButton.getLayoutParams();
            params.height = (int) dim;
            params.width = (int) dim;
            zoomButton.setLayoutParams(params);

            label = view.findViewById(R.id.settings_label_zoom);
            label.setTextColor(dark);
            label.setTextSize(unit, textSize);
            label.setOnClickListener(v -> zoomButton.callOnClick());

            space = view.findViewById(R.id.settings_vspace_zoom);
            params = space.getLayoutParams();
            params.width = (int) (spaceWidth * dim);
            params.height = (int) (spaceHeight * dim);
            space.setLayoutParams(params);
            space.setOnClickListener(v -> zoomButton.callOnClick());
        } else {
            view.findViewById(R.id.settings_label_zoom).setVisibility(View.GONE);
            view.findViewById(R.id.settings_button_zoom).setVisibility(View.GONE);
        }

        // long press flags
        if (longPressFlags) {
            TwoStateDrawnButton longPressFlagsButton
                    = view.findViewById(R.id.settings_button_longPressFlags);
            DrawImage drawFlag = new DrawFlag(
                    ContextCompat.getColor(ctxt, R.color.flag_ground),
                    ContextCompat.getColor(ctxt, R.color.flag_flagpole),
                    ContextCompat.getColor(ctxt, R.color.flag_flag));
            longPressFlagsButton.setUpImage(drawFlag);
            longPressFlagsButton.setDownImage(new DrawCrossedOut(drawFlag,
                    ContextCompat.getColor(ctxt, R.color.settings_crossout)));
            if (!s.isLongPressFlagsMode()) {
                longPressFlagsButton.setState(false);
            }
            longPressFlagsButton.setOnClickListener(v -> s.toggleLongPressForFlagMode());
            params = longPressFlagsButton.getLayoutParams();
            params.height = (int) dim;
            params.width = (int) dim;
            longPressFlagsButton.setLayoutParams(params);

            label = view.findViewById(R.id.settings_label_longPressFlags);
            label.setTextColor(dark);
            label.setTextSize(unit, textSize);
            label.setOnClickListener(v -> longPressFlagsButton.callOnClick());

            space = view.findViewById(R.id.settings_vspace_longPressFlags);
            params = space.getLayoutParams();
            params.width = (int) (spaceWidth * dim);
            params.height = (int) (spaceHeight * dim);
            space.setLayoutParams(params);
            space.setOnClickListener(v -> longPressFlagsButton.callOnClick());
        } else {
            view.findViewById(R.id.settings_label_longPressFlags).setVisibility(View.GONE);
            view.findViewById(R.id.settings_button_longPressFlags).setVisibility(View.GONE);
        }

        // Scroll sensitivity
        if (scrollSensitivity) {
            View[] measureOrder = new View[3];
            View[] displayOrder = new View[3];

            label = view.findViewById(R.id.settings_label_scrollSensitivity);
            label.setTextColor(dark);
            label.setTextSize(unit, textSize);
            measureOrder[2] = label;
            displayOrder[1] = label;

            final CounterView scrollCounter = view.findViewById(R.id.settings_counter_scrollSensitivity);
            scrollCounter.setCount(Math.max(Math.min((int) s.getSmallScrollSensitivity(), 9), 0));
            params = scrollCounter.getLayoutParams();
            params.height = (int) dim;
            params.width = (int) ((dim - DrawNumberUtil.PREFERRED_SEPARATION)
                    * DrawNumberUtil.PREFERRED_ASPECT_RATIO + DrawNumberUtil.PREFERRED_SEPARATION);
            scrollCounter.setLayoutParams(params);

            button = view.findViewById(R.id.settings_button_increaseScrollSensitivity);
            button.setDrawImage(new DrawPlus(ContextCompat.getColor(ctxt, R.color.dark)));
            button.setOnClickListener(v -> {
                int sensitivity = scrollCounter.getCount();
                if (sensitivity == 9) {
                    return;
                }
                scrollCounter.increase();
                s.setSmallScrollSensitivity(sensitivity + 1);
            });
            measureOrder[1] = button;
            displayOrder[2] = button;
            params = button.getLayoutParams();
            params.height = (int) dim;
            params.width = (int) dim;
            button.setLayoutParams(params);

            button = view.findViewById(R.id.settings_button_decreaseScrollSensitivity);
            button.setDrawImage(new DrawMinus(ContextCompat.getColor(ctxt, R.color.dark)));
            button.setOnClickListener(v -> {
                int sensitivity = scrollCounter.getCount();
                if (sensitivity == 0) {
                    return;
                }
                scrollCounter.decrease();
                s.setSmallScrollSensitivity(sensitivity - 1);
            });
            params = button.getLayoutParams();
            params.height = (int) dim;
            params.width = (int) dim;
            button.setLayoutParams(params);
            measureOrder[0] = button;
            displayOrder[0] = button;

            space = view.findViewById(R.id.settings_vspace_scrollSensitivity);
            params = space.getLayoutParams();
            params.width = (int) (spaceWidth * dim);
            params.height = (int) (spaceHeight * dim);
            space.setLayoutParams(params);

            LinearLayoutReorder control = view.findViewById(R.id.settings_control_zoomLevel);
            control.setMeasureOrder(measureOrder);
            control.setDisplayOrder(displayOrder);
        } else {
            view.findViewById(R.id.settings_label_scrollSensitivity).setVisibility(View.GONE);
            view.findViewById(R.id.settings_counter_scrollSensitivity).setVisibility(View.GONE);
            view.findViewById(R.id.settings_button_increaseScrollSensitivity).setVisibility(View.GONE);
            view.findViewById(R.id.settings_button_decreaseScrollSensitivity).setVisibility(View.GONE);
        }


        // Use question marks
        if (useQuestionMarks) {
            TwoStateDrawnButton questionMarkButton
                    = view.findViewById(R.id.settings_button_useQuestionMark);
            draw = new DrawQuestionMark(ContextCompat.getColor(ctxt, R.color.questionMark));
            questionMarkButton.setUpImage(draw);
            questionMarkButton.setDownImage(new DrawCrossedOut(draw,
                    ContextCompat.getColor(ctxt, R.color.settings_crossout)));
            if (!s.isQuestionMarkMode()) {
                questionMarkButton.setState(false);
            }
            questionMarkButton.setOnClickListener(v -> s.toggleQuestionMarkMode());
            params = questionMarkButton.getLayoutParams();
            params.height = (int) dim;
            params.width = (int) dim;
            questionMarkButton.setLayoutParams(params);

            label = view.findViewById(R.id.settings_label_useQuestionMark);
            label.setTextColor(dark);
            label.setTextSize(unit, textSize);
            label.setOnClickListener(v -> questionMarkButton.callOnClick());

            space = view.findViewById(R.id.settings_vspace_useQuestionMark);
            params = space.getLayoutParams();
            params.width = (int) (spaceWidth * dim);
            params.height = (int) (spaceHeight * dim);
            space.setLayoutParams(params);
            space.setOnClickListener(v -> questionMarkButton.callOnClick());
        } else {
            view.findViewById(R.id.settings_label_useQuestionMark).setVisibility(View.GONE);
            view.findViewById(R.id.settings_button_useQuestionMark).setVisibility(View.GONE);
        }

        // reset face actions
        if (resetFace) {
            TwoStateDrawnButton resetFaceButton
                    = view.findViewById(R.id.settings_button_quickResetFace);
            draw = new DrawResetFace(
                    ContextCompat.getColor(ctxt, R.color.face_head),
                    ContextCompat.getColor(ctxt, R.color.face_face),
                    ContextCompat.getColor(ctxt, R.color.transparent));
            ((DrawResetFace) draw).state = DrawResetFace.SURPRISED;
            resetFaceButton.setUpImage(draw);
            resetFaceButton.setDownImage(new DrawCrossedOut(draw,
                    ContextCompat.getColor(ctxt, R.color.settings_crossout)));
            if (!s.isConfirmResetFace()) {
                resetFaceButton.setState(false);
            }
            resetFaceButton.setOnClickListener(v -> s.toggleConfirmResetFace());
            params = resetFaceButton.getLayoutParams();
            params.height = (int) dim;
            params.width = (int) dim;
            resetFaceButton.setLayoutParams(params);

            label = view.findViewById(R.id.settings_label_quickResetFace);
            label.setTextColor(dark);
            label.setTextSize(unit, textSize);
            label.setOnClickListener(v -> resetFaceButton.callOnClick());

            space = view.findViewById(R.id.settings_vspace_quickResetFace);
            params = space.getLayoutParams();
            params.width = (int) (spaceWidth * dim);
            params.height = (int) (spaceHeight * dim);
            space.setLayoutParams(params);
            space.setOnClickListener(v -> resetFaceButton.callOnClick());
        } else {
            view.findViewById(R.id.settings_label_quickResetFace).setVisibility(View.GONE);
            view.findViewById(R.id.settings_button_quickResetFace).setVisibility(View.GONE);
        }

        // store game
        if (storeGame) {
            DrawnButton storeGameButton = view.findViewById(R.id.settings_button_storeGame);
            draw = new DrawFloppy(dark);
            storeGameButton.setDrawImage(draw);
            storeGameButton.setOnClickListener(v -> s.storeGame());
            params = storeGameButton.getLayoutParams();
            params.height = (int) dim;
            params.width = (int) dim;
            storeGameButton.setLayoutParams(params);

            label = view.findViewById(R.id.settings_label_storeGame);
            label.setTextColor(dark);
            label.setTextSize(unit, textSize);
            label.setOnClickListener(v -> storeGameButton.callOnClick());

            space = view.findViewById(R.id.settings_vspace_storeGame);
            params = space.getLayoutParams();
            params.width = (int) (spaceWidth * dim);
            params.height = (int) (spaceHeight * dim);
            space.setLayoutParams(params);
            space.setOnClickListener(v -> storeGameButton.callOnClick());
        } else {
            view.findViewById(R.id.settings_label_storeGame).setVisibility(View.GONE);
            view.findViewById(R.id.settings_button_storeGame).setVisibility(View.GONE);
        }

        // view stored games
        if (viewStoredGames) {
            DrawnButton viewStoredGamesButton
                    = view.findViewById(R.id.settings_button_viewStoredGames);
            draw = new DrawFloppy(dark);
            viewStoredGamesButton.setDrawImage(draw);
            viewStoredGamesButton.setOnClickListener(v -> s.viewStoredGames());
            params = viewStoredGamesButton.getLayoutParams();
            params.height = (int) dim;
            params.width = (int) dim;
            viewStoredGamesButton.setLayoutParams(params);

            label = view.findViewById(R.id.settings_label_viewStoredGames);
            label.setTextColor(dark);
            label.setTextSize(unit, textSize);
            label.setOnClickListener(v -> viewStoredGamesButton.callOnClick());

            space = view.findViewById(R.id.settings_vspace_viewStoredGames);
            params = space.getLayoutParams();
            params.width = (int) (spaceWidth * dim);
            params.height = (int) (spaceHeight * dim);
            space.setLayoutParams(params);
            space.setOnClickListener(v -> viewStoredGamesButton.callOnClick());
        } else {
            view.findViewById(R.id.settings_label_viewStoredGames).setVisibility(View.GONE);
            view.findViewById(R.id.settings_button_viewStoredGames).setVisibility(View.GONE);
        }

        // fixed zoom level
        if (fixedZoomLevel) {
            CounterView zoomCounter = view.findViewById(R.id.settings_counter_zoomLevel);
            zoomCounter.setCount((int)s.getZoomLevel());
            params = zoomCounter.getLayoutParams();
            params.height = (int) ((dim / 2 - DrawNumberUtil.PREFERRED_SEPARATION)
                    / DrawNumberUtil.PREFERRED_ASPECT_RATIO + DrawNumberUtil.PREFERRED_SEPARATION);
            params.width = (int) dim;
            zoomCounter.setLayoutParams(params);

            View[] measureOrder = new View[3];
            View[] displayOrder = new View[3];
            label = view.findViewById(R.id.settings_label_zoomLevel);
            label.setTextColor(dark);
            label.setTextSize(unit, textSize);
            displayOrder[1] = label;
            measureOrder[2] = label;

            button = view.findViewById(R.id.settings_button_increaseZoomLevel);
            button.setDrawImage(new DrawPlus(ContextCompat.getColor(ctxt, R.color.dark)));
            button.setOnClickListener(v -> {
                int zoom = zoomCounter.getCount();
                if (zoom == 99) {
                    return;
                }
                zoomCounter.increase();
                s.setZoomLevel(zoom + 1);
            });
            params = button.getLayoutParams();
            params.height = (int) dim;
            params.width = (int) dim;
            button.setLayoutParams(params);
            displayOrder[2] = button;
            measureOrder[0] = button;

            button = view.findViewById(R.id.settings_button_decreaseZoomLevel);
            button.setDrawImage(new DrawMinus(ContextCompat.getColor(ctxt, R.color.dark)));
            button.setOnClickListener(v -> {
                int zoom = zoomCounter.getCount();
                if (zoom == 0) {
                    return;
                }
                zoomCounter.decrease();
                s.setZoomLevel(zoom - 1);
            });
            params = button.getLayoutParams();
            params.height = (int) dim;
            params.width = (int) dim;
            button.setLayoutParams(params);
            displayOrder[0] = button;
            measureOrder[1] = button;

            LinearLayoutReorder control = view.findViewById(R.id.settings_control_zoomLevel);
            control.setMeasureOrder(measureOrder);
            control.setDisplayOrder(displayOrder);

            space = view.findViewById(R.id.settings_vspace_zoomLevel);
            params = space.getLayoutParams();
            params.width = (int) (spaceWidth * dim);
            params.height = (int) (spaceHeight * dim);
            space.setLayoutParams(params);

        } else {
            view.findViewById(R.id.settings_label_zoomLevel).setVisibility(View.GONE);
            view.findViewById(R.id.settings_counter_zoomLevel).setVisibility(View.GONE);
            view.findViewById(R.id.settings_button_increaseZoomLevel).setVisibility(View.GONE);
            view.findViewById(R.id.settings_button_decreaseZoomLevel).setVisibility(View.GONE);
        }

        // Hint bombs
        if (hintBomb) {
            TwoStateDrawnButton hintBombButton = view.findViewById(R.id.settings_button_hintBomb);
            draw = new DrawSmall(new DrawFlag(
                    ContextCompat.getColor(ctxt, R.color.hint),
                    ContextCompat.getColor(ctxt, R.color.hint),
                    ContextCompat.getColor(ctxt, R.color.hint)),
                    0.75f,0.75f);
            hintBombButton.setUpImage(draw);
            hintBombButton.setDownImage(new DrawCrossedOut(draw,
                    ContextCompat.getColor(ctxt, R.color.settings_crossout)));
            if (!s.isHintBombMode()) {
                hintBombButton.setState(false);
            }
            hintBombButton.setOnClickListener(v -> s.toggleHintBombMode());
            params = hintBombButton.getLayoutParams();
            params.height = (int) dim;
            params.width = (int) dim;
            hintBombButton.setLayoutParams(params);

            label = view.findViewById(R.id.settings_label_hintBomb);
            label.setTextColor(dark);
            label.setTextSize(unit, textSize);
            label.setOnClickListener(v -> hintBombButton.callOnClick());

            space = view.findViewById(R.id.settings_vspace_zoom);
            params = space.getLayoutParams();
            params.width = (int) (spaceWidth * dim);
            params.height = (int) (spaceHeight * dim);
            space.setLayoutParams(params);
            space.setOnClickListener(v -> hintBombButton.callOnClick());
        } else {
            view.findViewById(R.id.settings_label_hintBomb).setVisibility(View.GONE);
            view.findViewById(R.id.settings_button_hintBomb).setVisibility(View.GONE);
        }

        return dialog;
    }
}
