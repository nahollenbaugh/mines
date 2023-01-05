package com.nahollenbaugh.mines.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.nahollenbaugh.mines.drawing.DrawQuestionMark;
import com.nahollenbaugh.mines.drawing.DrawResetFace;
import com.nahollenbaugh.mines.drawing.DrawShovel;
import com.nahollenbaugh.mines.drawing.DrawSmall;
import com.nahollenbaugh.mines.gamelogic.GameWatcher;
import com.nahollenbaugh.mines.gamelogic.HintGame;
import com.nahollenbaugh.mines.main.GameFragment;
import com.nahollenbaugh.mines.R;
import com.nahollenbaugh.mines.drawing.DrawBomb;
import com.nahollenbaugh.mines.drawing.DrawCrossedOut;
import com.nahollenbaugh.mines.drawing.DrawFlag;
import com.nahollenbaugh.mines.drawing.DrawImage;
import com.nahollenbaugh.mines.drawing.DrawNumberUtil;
import com.nahollenbaugh.mines.gamelogic.Game;
import com.nahollenbaugh.mines.gamelogic.NonexistantSquareException;
import com.nahollenbaugh.mines.gamelogic.Solver;

import static com.nahollenbaugh.mines.drawing.DrawImageUtil.*;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class GameView extends ZoomableView{
    protected Game game;
    protected int numbombs;
    protected int height;
    protected int width;
    protected int startI;
    protected int startJ;

    protected float dx = 0;
    protected float squareBorderThickness;
    protected float gameBorderThickness;
    protected float shift;
    protected static DrawNumberUtil drawNumber;
    protected static DrawImage drawExplodedBomb;
    protected static DrawImage drawWonBomb;
    protected static DrawImage drawLostBomb;
    protected static DrawImage drawFlag;
    protected static DrawImage drawFalseFlag;
    protected static DrawImage drawLostFlag;
    protected static DrawImage drawWonFlag;
    protected static DrawImage drawQuestionMark;
    protected static DrawImage drawFalseQuestionMark;
    protected static DrawImage drawLostQuestionMark;
    protected static DrawImage drawWonQuestionMark;
    protected static DrawImage drawFlaggingModeCovered;
    protected static DrawImage drawSafeHint;
    protected static DrawImage drawBombHint;

    protected static Context context;
    public GameFragment gameFragment;
    protected boolean drawAllCovered;

    protected static int number_blue;
    protected static int number_green;
    protected static int number_red;
    protected static int number_darkBlue;
    protected static int number_darkRed;
    protected static int number_lightBlue;
    protected static int number_black;
    protected static int number_gray;
    protected static int background;
    protected static int gameBorderColor;
    protected static int squareBorderColor;
    protected static int coveredColor;
    protected static int uncoveredColor;

    protected HintGame.Hint[][] hints;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }
    public static void initialize(Context context){
        if (context.equals(GameView.context)){
            return;
        }
        GameView.context = context;
        int dark = ContextCompat.getColor(context,R.color.dark);
        int light = ContextCompat.getColor(context,R.color.light);
        int crossOut = ContextCompat.getColor(context,R.color.false_flag_crossout);

        drawNumber = new DrawNumberUtil(0,
                ContextCompat.getColor(context,R.color.number_outline),
                0.3f, 0,0.02f);
        drawExplodedBomb = new DrawBomb(
                ContextCompat.getColor(context,R.color.bomb_explodedBomb),
                ContextCompat.getColor(context,R.color.bomb_explodedShine));
        drawLostBomb = new DrawBomb(
                ContextCompat.getColor(context,R.color.bomb_bomb),
                ContextCompat.getColor(context,R.color.bomb_shine));
        drawWonBomb = new DrawBomb(dark,light);
        drawFlag = new DrawFlag(
                ContextCompat.getColor(context,R.color.flag_ground),
                ContextCompat.getColor(context,R.color.flag_flagpole),
                ContextCompat.getColor(context,R.color.flag_flag));
        drawFalseFlag = new DrawCrossedOut(drawFlag, crossOut);
        drawLostFlag = drawFlag;
        drawWonFlag = drawFlag;
        drawQuestionMark = new DrawQuestionMark(
                ContextCompat.getColor(context,R.color.questionMark));
        drawFalseQuestionMark = new DrawCrossedOut(drawQuestionMark,crossOut);
        drawLostQuestionMark = drawQuestionMark;
        drawWonQuestionMark = drawQuestionMark;
        drawFlaggingModeCovered = new DrawSmall(new DrawFlag(dark,dark,dark),0.6f,0.6f);
        drawSafeHint = new DrawShovel(ContextCompat.getColor(context, R.color.hint_safe));
        drawBombHint = new DrawBomb(ContextCompat.getColor(context, R.color.hint_safe),
                ContextCompat.getColor(context, R.color.bomb_shine));

        number_blue = ContextCompat.getColor(context,R.color.number_blue);
        number_green = ContextCompat.getColor(context,R.color.number_green);
        number_red = ContextCompat.getColor(context,R.color.number_red);
        number_darkBlue = ContextCompat.getColor(context,R.color.number_darkBlue);
        number_darkRed = ContextCompat.getColor(context,R.color.number_darkRed);
        number_lightBlue = ContextCompat.getColor(context,R.color.number_lightBlue);
        number_black = ContextCompat.getColor(context,R.color.number_black);
        number_gray = ContextCompat.getColor(context,R.color.number_gray);
        background = ContextCompat.getColor(context, R.color.background);

        gameBorderColor = ContextCompat.getColor(context,R.color.gameBorder);
        squareBorderColor = ContextCompat.getColor(context,R.color.squareBorder);

        coveredColor = ContextCompat.getColor(context,R.color.covered);
        uncoveredColor = ContextCompat.getColor(context,R.color.uncovered);
    }

    public void setGameParameters(int numbombs, int height, int width){
        this.numbombs = numbombs;
        this.height = height;
        this.width = width;
        hints = new HintGame.Hint[width][height];
    }
    public void setGame(Game game){
        hints = new HintGame.Hint[game.getWidth()][game.getHeight()];
        this.game = game;
        numbombs = game.getNumbombs();
        height = game.getHeight();
        width = game.getWidth();
        setGameSizes(getWidth(),getHeight());
    }

    public void checkWon(){
        if (game.isWon()){
            if (gameFragment != null) {
                gameFragment.alertGameChange(GameWatcher.CHANGE_WON);
            }
        }
    }
    public void checkLost(){
        if (game.isLost()) {
            if (gameFragment != null) {
                gameFragment.alertGameChange(GameWatcher.CHANGE_LOST);
            }
        }
    }
    public Game getGame(){
        return game;
    }
    public void reset(){
        if (gameFragment.isNoguessMode()){
            Solver.NoguessGame noguessGame = Solver.newNoguessGame(numbombs,height,width);
            game = noguessGame.game;
            startI = noguessGame.startI;
            startJ = noguessGame.startJ;
        } else {
            game = new Game(numbombs, height, width);
        }
        stopHints();
        invalidate();
    }

    public void drawAllCovered(boolean drawAllCovered){
        this.drawAllCovered = drawAllCovered;
        invalidate();
    }

    public void showHint(HintGame.Hint hint){
        hints[hint.i][hint.j] = hint;
        invalidate();
    }
    public HintGame.Hint[] stopHints(){
        ArrayList<HintGame.Hint> listHints = new ArrayList<>();
        for (int i = 0; i < hints.length; i++){
            for (int j = 0; j < hints[i].length; j++){
                if (hints[i][j] != null){
                    listHints.add(hints[i][j]);
                    hints[i][j] = null;
                }
            }
        }
        invalidate();
        HintGame.Hint[] arrayHints = new HintGame.Hint[listHints.size()];
        for (int i = 0; i < arrayHints.length; i++){
            arrayHints[i] = listHints.get(i);
        }
        return arrayHints;
    }

    @Override
    public void onDraw(Canvas c){
        int gameWidth = game.getWidth();
        int gameHeight = game.getHeight();
        float boardWidth = getWidth() - (2*gameBorderThickness);
        float boardHeight = getHeight() - (2*gameBorderThickness);

        fillRectangle(transformXToScreen(shift),
                0,
                transformXToScreen(shift+(gameWidth+1)*dx),
                transformYToScreen((gameHeight+1)*dx),
                c, gameBorderColor);


        int startX = Math.max(0,getGameXFromReal(transformXToReal(0))-1);
        int startY = Math.max(0,getGameYFromReal(transformYToReal(0))-1);
        int endX = Math.min(gameWidth-1,getGameXFromReal(transformXToReal(boardWidth))+1);
        int endY = Math.min(gameHeight-1,getGameYFromReal(transformYToReal(boardHeight))+1);
        for (int i = startX; i <= endX; i++){
            for (int j = startY; j <= endY; j++){
                drawSquare(i,j,i*dx+gameBorderThickness+shift,j*dx+gameBorderThickness,c);
            }
        }
    }

    protected static final int STATE_SAFE_HINT = Game.STATE_MASK + Game.NUMBER_MASK + 1;
    protected static final int STATE_BOMB_HINT = Game.STATE_MASK + Game.NUMBER_MASK + 2;
    protected void drawSquare(int i, int j, float x, float y, Canvas c){
        int state = drawAllCovered ? Game.COVERED : game.visibleState(i,j);
        state = hints[i][j] != null && hints[i][j].isSafe ? STATE_SAFE_HINT : state;
        state = hints[i][j] != null && !hints[i][j].isSafe ? STATE_BOMB_HINT : state;
        state = !game.isOpen() && gameFragment.isNoguessMode() && (startI == i) && (startJ == j)
                ? STATE_SAFE_HINT : state;

        float sx = transformXToScreen(x);
        float sy = transformYToScreen(y);
        float sxdx = transformXToScreen(x+dx);
        float sydx = transformYToScreen(y+dx);
        float sep = transformDistanceToScreen(squareBorderThickness);

        fillRectangle(sx,sy,sxdx,sydx,c,squareColor(state));
        drawSquareNumber(sx,sxdx,sy,sydx,state,c);
        drawSquareImage(sx,sy,state,c);
        drawLineHorizontal(sx,sxdx,sy,sep,DOWN,c,squareBorderColor);
        drawLineHorizontal(sx,sxdx,sydx,sep,UP,c,squareBorderColor);
        drawLineVertical(sx,sy,sydx,sep,RIGHT,c,squareBorderColor);
        drawLineVertical(sxdx,sy,sydx,sep,LEFT,c,squareBorderColor);
    }
    protected void drawSquareImage(float x, float y, int state, Canvas c) {
        DrawImage image = null;
        switch (state & Game.STATE_MASK) {
            case Game.FLAGGED:
                image = drawFlag;
                break;
            case Game.FALSE_FLAG:
                image = drawFalseFlag;
                break;
            case Game.LOST_FLAG:
                image = drawLostFlag;
                break;
            case Game.WON_FLAG:
                image = drawWonFlag;
                break;
            case Game.WON_BOMB:
                image = drawWonBomb;
                break;
            case Game.EXPLODED_BOMB:
                image = drawExplodedBomb;
                break;
            case Game.LOST_BOMB:
                image = drawLostBomb;
                break;
            case Game.QUESTION_MARK:
                image = drawQuestionMark;
                break;
            case Game.FALSE_QUESTION_MARK:
                image = drawFalseQuestionMark;
                break;
            case Game.WON_QUESTION_MARK:
                image = drawWonQuestionMark;
                break;
            case Game.LOST_QUESTION_MARK:
                image = drawLostQuestionMark;
                break;
            case Game.COVERED:
                if (gameFragment.isFlagging()) {
                    image = drawFlaggingModeCovered;
                }
                break;
        }
        if (state == STATE_BOMB_HINT) {
            image = drawBombHint;
        }
        if (state == STATE_SAFE_HINT) {
            image = drawSafeHint;
        }
        if (image != null) {
            int screenDX = (int) transformDistanceToScreen(dx);
            c.save();
            c.translate(x, y);
            image.draw(screenDX, screenDX, c);
            c.restore();
        }
    }
    protected int squareColor(int state){
        if (state == STATE_SAFE_HINT || state == STATE_BOMB_HINT){
            return coveredColor;
        }
        switch (state & Game.STATE_MASK){
            case Game.COVERED:
            case Game.FLAGGED:
            case Game.QUESTION_MARK:
            case Game.WON_BOMB:
            case Game.WON_FLAG:
            case Game.WON_QUESTION_MARK:
            case Game.FALSE_FLAG:
            case Game.FALSE_QUESTION_MARK:
            case Game.LOST_QUESTION_MARK:
            case Game.LOST_FLAG:
                return coveredColor;
            case Game.EXPLODED_BOMB:
            case Game.UNCOVERED_NUMBER:
                return uncoveredColor;
            case Game.LOST_BOMB:
                return ContextCompat.getColor(context,R.color.lostBomb_background);
        }
        return Color.YELLOW;
    }
    protected static int numberColor(int state){
        switch(state & Game.NUMBER_MASK){
            case 1: return number_blue;
            case 2: return number_green;
            case 3: return number_red;
            case 4: return number_darkBlue;
            case 5: return number_darkRed;
            case 6: return number_lightBlue;
            case 7: return number_black;
            case 8: return number_gray;
            case 0: return background;
        }
        return Color.YELLOW;
    }
    protected void drawSquareNumber(float xstart, float xend, float ystart,
                                   float yend, int state, Canvas c){
        if ((state & Game.STATE_MASK) == Game.UNCOVERED_NUMBER
                && (state & Game.NUMBER_MASK) > 0) {
            drawNumber.color = numberColor(state);
            float height = (yend - ystart) * .75f;
            float width = height * 0.625f;
            float xOffset = ((xend - xstart) - width) / 2;
            float yOffset = (yend - ystart) * .125f;
            drawNumber.drawNumber(xstart + xOffset, ystart + yOffset, width, height,
                    state & Game.NUMBER_MASK, c);
        }
    }

    protected int getGameXFromReal(float xReal){
        return (int)((xReal-gameBorderThickness-shift)/dx);
    }
    protected int getGameYFromReal(float yReal){
        return (int)((yReal-gameBorderThickness)/dx);
    }

    protected void flag(int gameX, int gameY) {
        boolean flagged;
        if (gameFragment.isQuestionMarkMode()){
            flagged = game.cycleFlagQuestionMark(gameX, gameY);
        } else {
            flagged = game.flag(gameX, gameY);
        }
        if (flagged){
            flagAlert(gameX, gameY);
        }
    }
    protected void flagAlert(int gameX, int gameY){
        if ((game.visibleState(gameX, gameY) & Game.STATE_MASK) == Game.FLAGGED) {
            gameFragment.alertGameChange(GameWatcher.CHANGE_FLAGGED);
        } else {
            gameFragment.alertGameChange(GameWatcher.CHANGE_UNFLAGGED);
        }
        invalidate();
    }

    @Override
    public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        if (game == null) {
            return;
        }
        setGameSizes(width, height);
    }
    protected void setGameSizes(int width, int height){
        // the +1's give us a half-square border.
        float gameHeight = game.getHeight()+1;
        float gameWidth = game.getWidth()+1;
        if (gameHeight/gameWidth > height*1f/width) { // canvas is too short
            dx = height/gameHeight;
            shift = Math.max(0,(width-gameWidth*dx)/2);
            setEffectiveDimensions(gameWidth*dx+2*shift, height);
        } else {
            dx = width/gameWidth;
            setEffectiveDimensions(width,dx*gameWidth);
        }

        squareBorderThickness = dx*0.05f;
        gameBorderThickness = dx*0.5f;
    }

    @Override
    public void onClick(float x, float y){
        int gameX = getGameXFromReal(x);
        int gameY = getGameYFromReal(y);
        if (game.isWon() || game.isLost()
                || (!game.isOpen() && gameFragment.isNoguessMode()
                    && (((startI != gameX) || (startJ != gameY))
                        || (gameFragment.isFlagging() && (startI == gameX) && (startJ == gameY))))){
            return;
        }
        try {
            if (gameFragment.isFlagging()) {
                flag(gameX,gameY);
            } else {
                if (game.uncover(gameX,gameY)){
                    gameFragment.alertGameChange(GameWatcher.CHANGE_UNCOVERED);
                    if (game.countUncovers == 1) {
                        gameFragment.alertGameChange(GameWatcher.CHANGE_STARTED);
                    }
                }
                checkWon();
                checkLost();
                invalidate();
            }
        } catch (NonexistantSquareException exception){
        }
    }

    @Override
    public void onLongClick(float x, float y){
        if (gameFragment.isLongPressFlagsMode()) {
            flag(getGameXFromReal(x), getGameYFromReal(y));
        }
    }

    // the way I did zoomview is definitely wrong here
    public boolean onTouchEvent(MotionEvent e){
        if (!game.isWon() && !game.isLost()) {
            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_DOWN:
                    gameFragment.setResetFace(DrawResetFace.SURPRISED);
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_UP:
                    gameFragment.setResetFace(DrawResetFace.HAPPY);
                    break;
            }
        }
        return super.onTouchEvent(e);
    }
}
