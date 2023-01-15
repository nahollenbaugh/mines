package com.nahollenbaugh.mines.gamelogic;

import android.util.Pair;

import androidx.annotation.NonNull;

import java.util.Random;

public class Game {
    protected int numbombs;
    protected int height;
    protected int width;

    protected boolean[][] bombs;
    protected boolean[][] flags;
    protected boolean[][] questionMarks;
    protected boolean[][] uncovereds;
    protected int[][] numbers;

    protected int lostX = -1;
    protected int lostY = -1;

    protected boolean isWon = false;
    protected boolean isWonStale = true;

    public int countUncovers;

    public long seed;

    public Game(int numbombs, int height, int width){
        this(numbombs, height, width, (long)(Math.random()*9007199254740991l));
    }
    public Game(int numbombs, int height, int width, long seed){
        if (numbombs > height * width - 1){
            throw new IllegalArgumentException("Too many bombs");
        }
        this.seed = seed;
        this.numbombs = numbombs;
        this.height = height;
        this.width = width;
        flags = new boolean[width][height];
        questionMarks = new boolean[width][height];
        uncovereds = new boolean[width][height];
        numbers = new int[width][height];
    }

    public Game(boolean[][] bombs, boolean[][] flags,
                boolean[][] uncovereds, boolean[][] questionMarks){
        this.bombs = bombs;
        this.flags = flags;
        this.uncovereds = uncovereds;
        this.questionMarks = questionMarks;
        height = bombs[0].length;
        width = bombs.length;
        for (int i = 0; i < bombs.length; i++){
            for (int j = 0; j < bombs[i].length; j++){
                numbombs = numbombs + (bombs[i][j]?1:0);
            }
        }
        numbers = new int[width][height];
        populateNumbers();
    }

    /**
     * @param x the width-dimension location of the square to uncover
     * @param y the height-dimension location of the square to uncover
     * @return whether this action causes the game to start.
     * @throws NonexistantSquareException if (x,y) is not a square
     * */
    public boolean uncover(int x, int y){
        if (isWon() || isLost()){
            return false;
        }
        checkIsSquare(x,y);
        if (uncovereds[x][y] || flags[x][y] || questionMarks[x][y]){
            return false;
        }
        isWonStale = true;
        if (bombs == null){
            createBoard(x,y);
            return true;
        }
        uncovereds[x][y] = true;
        countUncovers = countUncovers + 1;
        if (bombs[x][y]) {
            lostX = x;
            lostY = y;
        } else if (numbers[x][y] == 0){
            uncoverZeroes();
        }
        return true;
    }

    public boolean chord(int x, int y){
        if (isWon() || isLost()){
            return false;
        }
        checkIsSquare(x,y);
        if (!uncovereds[x][y] || flags[x][y] || questionMarks[x][y]){
            return false;
        }
        int adjacentFlags = 0;
        if (x < width-1 && y < height-1) if (flags[x+1][y+1]) adjacentFlags++;
        if (x < width-1)                 if (flags[x+1][y])   adjacentFlags++;
        if (x < width-1 && y > 0)        if (flags[x+1][y-1]) adjacentFlags++;
        if (               y < height-1) if (flags[x][y+1])   adjacentFlags++;
        if (               y > 0)        if (flags[x][y-1])   adjacentFlags++;
        if (x > 0       && y < height-1) if (flags[x-1][y+1]) adjacentFlags++;
        if (x > 0)                       if (flags[x-1][y])   adjacentFlags++;
        if (x > 0       && y > 0)        if (flags[x-1][y-1]) adjacentFlags++;
        if (adjacentFlags == numbers[x][y]){
            if (x < width-1 && y < height-1) questionMarks[x+1][y+1] = false;
            if (x < width-1)                 questionMarks[x+1][y] = false;
            if (x < width-1 && y > 0)        questionMarks[x+1][y-1] = false;
            if (               y < height-1) questionMarks[x][y+1] = false;
            if (               y > 0)        questionMarks[x][y-1] = false;
            if (x > 0       && y < height-1) questionMarks[x-1][y+1] = false;
            if (x > 0)                       questionMarks[x-1][y] = false;
            if (x > 0       && y > 0)        questionMarks[x-1][y-1] = false;

            boolean changed = false;
            if (x < width-1 && y < height-1) if (!flags[x+1][y+1]) changed = quietUncover(x+1,y+1) || changed;
            if (x < width-1)                 if (!flags[x+1][y])   changed = quietUncover(x+1,y) || changed;
            if (x < width-1 && y > 0)        if (!flags[x+1][y-1]) changed = quietUncover(x+1,y-1) || changed;
            if (               y < height-1) if (!flags[x][y+1])   changed = quietUncover(x,y+1) || changed;
            if (               y > 0)        if (!flags[x][y-1])   changed = quietUncover(x,y-1) || changed;
            if (x > 0       && y < height-1) if (!flags[x-1][y+1]) changed = quietUncover(x-1,y+1) || changed;
            if (x > 0)                       if (!flags[x-1][y])   changed = quietUncover(x-1,y) || changed;
            if (x > 0       && y > 0)        if (!flags[x-1][y-1]) changed = quietUncover(x-1,y-1) || changed;
            if (changed){
                isWonStale = true;
                uncoverZeroes();
                countUncovers = countUncovers + 1;
            }
            return changed;
        }
        return false;
    }

    protected void checkIsSquare(int x, int y) {
        if (!isSquare(x,y)) {
            throw new NonexistantSquareException("(" + x + "," + y + ")");
        }
    }
    public boolean isSquare(int x, int y){
        return !(x < 0 || x > width - 1 || y < 0 || y > height - 1);
    }

    protected void uncoverZeroes(){
        boolean changed = false;
        for (int i = 0; i < width; i++){
            for (int j = 0; j < height; j++){
                if (uncovereds[i][j] && numbers[i][j] == 0){
                    if (i < width-1 && j < height-1) changed = changed || quietUncover(i+1,j+1);
                    if (i < width-1)                 changed = changed || quietUncover(i+1,j);
                    if (i < width-1 && j > 0)        changed = changed || quietUncover(i+1,j-1);
                    if (               j < height-1) changed = changed || quietUncover(i,j+1);
                    if (               j > 0)        changed = changed || quietUncover(i,j-1);
                    if (i > 0       && j < height-1) changed = changed || quietUncover(i-1,j+1);
                    if (i > 0)                       changed = changed || quietUncover(i-1,j);
                    if (i > 0       && j > 0)        changed = changed || quietUncover(i-1,j-1);
                }
            }
        }
        if (changed){
            uncoverZeroes();
        }
    }
    protected boolean quietUncover(int x, int y){
        if (uncovereds[x][y] || flags[x][y] || questionMarks[x][y]){
            return false;
        }
        if (bombs[x][y]) {
            uncover(x,y);
        }
        uncovereds[x][y] = true;
        return true;
    }

    protected int s(int x, int y){
        bombs[x][y] = !bombs[x][y];
        int c = 1;
        if (checkSpaceForFirstClick(x,y)) {
            if (x < width - 1 && y < height - 1) {
                c++;
                bombs[x + 1][y + 1] = !bombs[x + 1][y + 1];
            }
            if (x < width - 1) {
                c++;
                bombs[x + 1][y] = !bombs[x + 1][y];
            }
            if (x < width - 1 && y > 0) {
                c++;
                bombs[x + 1][y - 1] = !bombs[x + 1][y - 1];
            }
            if (y < height - 1) {
                c++;
                bombs[x][y + 1] = !bombs[x][y + 1];
            }
            if (y > 0) {
                c++;
                bombs[x][y - 1] = !bombs[x][y - 1];
            }
            if (x > 0 && y < height - 1) {
                c++;
                bombs[x - 1][y + 1] = !bombs[x - 1][y + 1];
            }
            if (x > 0) {
                c++;
                bombs[x - 1][y] = !bombs[x - 1][y];
            }
            if (x > 0 && y > 0) {
                c++;
                bombs[x - 1][y - 1] = !bombs[x - 1][y - 1];
            }
        }
        return c;
    }
    protected boolean checkSpaceForFirstClick(int x, int y){
        return numbombs <= height * width - 9
                || (numbombs <= height * width - 6
                    && (x > 0 || x < width - 1 || y > 0 || y < height - 1));
    }
    protected void createBoard(int x, int y){
        countUncovers = 0;
        Random rnd = new Random(seed);

        bombs = new boolean[width][height];
        checkSpaceForFirstClick(x,y);
        int spacesBlocked = s(x,y);
        for (int bombsPlaced = 0; bombsPlaced < numbombs; bombsPlaced++){
            int next = (int)(rnd.nextDouble()*(height*width-bombsPlaced-spacesBlocked));
            int s = 0;
            for (; s < width*height; s++){
                if (!bombs[s%width][s/width]){
                    if (next == 0){
                        break;
                    }
                    next--;
                }
            }
            bombs[s%width][s/width]=true;
        }
        s(x,y);

        populateNumbers();
        uncover(x,y);
    }

    public void setUnopened(){
        bombs = null;
    }
    public void setNumbombs(int numbombs){
        this.numbombs = numbombs;
    }

    protected void populateNumbers(){
        for (int i=0; i<width; i++){
            for (int j=0; j<height; j++){
                if (i < width-1 && j < height-1) if (bombs[i+1][j+1]) numbers[i][j]++;
                if (i < width-1)                 if (bombs[i+1][j])   numbers[i][j]++;
                if (i < width-1 && j > 0)        if (bombs[i+1][j-1]) numbers[i][j]++;
                if (               j < height-1) if (bombs[i][j+1])   numbers[i][j]++;
                if (               j > 0)        if (bombs[i][j-1])   numbers[i][j]++;
                if (i > 0       && j < height-1) if (bombs[i-1][j+1]) numbers[i][j]++;
                if (i > 0)                       if (bombs[i-1][j])   numbers[i][j]++;
                if (i > 0       && j > 0)        if (bombs[i-1][j-1]) numbers[i][j]++;
            }
        }
    }

    /**
     * @param x the width-dimension location of the square to (un)flag
     * @param y the height-dimension location of the square to (un)flag
     * @return whether anything happens
     * @throws NonexistantSquareException if (x,y) is not a square
     * */
    public boolean flag(int x, int y){
        if (isWon() || isLost()){
            return false;
        }
        checkIsSquare(x,y);
        if (uncovereds[x][y]) {
            return false;
        } else {
            if (questionMarks[x][y]){
                questionMarks[x][y] = false;
            } else {
                flags[x][y] = !flags[x][y];
            }
            return true;
        }
    }

    /** Cycle unmarked, flag, question mark, in that order.
     * @param x the width-dimension location of the square to change
     * @param y the height-dimension location of the square to change
     * @return whether a flag appears or disappears.
     * @throws NonexistantSquareException if (x,y) is not a square
     * */
    public boolean cycleFlagQuestionMark(int x, int y){
        checkIsSquare(x,y);
        if (uncovereds[x][y]){
            return false;
        } else {
            if (flags[x][y]){
                flags[x][y]=false;
                questionMarks[x][y]=true;
                return true;
            } else if (questionMarks[x][y]){
                questionMarks[x][y]=false;
                return false;
            } else {
                flags[x][y]=true;
                return true;
            }
        }
    }

    /**
     * @return whether the game has been won.  False if the game has not
     * been created
     */
    public boolean isWon(){
        if (isWonStale) {
            int countUncovered = 0;
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (uncovereds[i][j] && !bombs[i][j]) {
                        countUncovered = countUncovered + 1;
                    }
                }
            }
            isWon = countUncovered + numbombs == height * width;
            isWonStale = false;
        }
        return isWon;
    }

    /**
     * @return whether the game has been won.  False if the game has not
     * been created
     */
    public boolean isLost(){
        return lostX >= 0;
    }

    /**
     * @return A {@link android.util.Pair} of integers representing the location of the
     * uncovered bomb.
     * @throws NonexistantSquareException if the game has not been lost
     */
    public Pair<Integer,Integer> lostSquare(){
        if (!isLost()) {
            throw new NonexistantSquareException("Game has not been lost");
        }
        return new Pair<>(lostX, lostY);
    }

    public boolean isOpen(){
        for (boolean[] u : uncovereds){
            for (boolean v : u){
                if (v){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param x the width-dimension location of the square to (un)flag
     * @param y the height-dimension location of the square to (un)flag
     * @return covered, flagged, uncovered_bomb, or the number of bombs
     * surrounding the square.
     * @throws NonexistantSquareException if (x,y) is not a square
     */
    public int visibleState(int x, int y){
        checkIsSquare(x,y);
        if (isWon()){
            if (flags[x][y]){
                return numbers[x][y]+WON_FLAG;
            } else if (questionMarks[x][y]){
                return numbers[x][y]+WON_QUESTION_MARK;
            } else if (bombs[x][y]){
                return numbers[x][y]+WON_BOMB;
            } else {
                return numbers[x][y]+UNCOVERED_NUMBER;
            }
        } else if (isLost()){
            if (x == lostX && y == lostY){
                return numbers[x][y]+EXPLODED_BOMB;
            } else if (flags[x][y]){
                return numbers[x][y]+(bombs[x][y] ? LOST_FLAG : FALSE_FLAG);
            } else if (questionMarks[x][y]){
                return numbers[x][y]+(bombs[x][y] ? LOST_QUESTION_MARK : FALSE_QUESTION_MARK);
            } else if (bombs[x][y]) {
                return numbers[x][y] + LOST_BOMB;
            } else if (!uncovereds[x][y]){
                return numbers[x][y] + COVERED;
            } else {
                return numbers[x][y]+UNCOVERED_NUMBER;
            }
        } else {
            if (flags[x][y]) {
                return numbers[x][y]+FLAGGED;
            } else if (questionMarks[x][y]) {
                return numbers[x][y]+QUESTION_MARK;
            } else if (!uncovereds[x][y]) {
                return numbers[x][y]+COVERED;
            } else {
                return numbers[x][y]+UNCOVERED_NUMBER;
            }
        }
    }

    public int getNumbombs(){
        return numbombs;
    }
    public int getHeight(){
        return height;
    }
    public int getWidth(){
        return width;
    }
    public int flagsLeft(){
        int flagsLeft = numbombs;
        for (int i = 0; i < flags.length; i++){
            for (int j = 0; j < flags[i].length; j++){
                flagsLeft = flagsLeft - (flags[i][j]?1:0);
            }
        }
        return flagsLeft;
    }

    /**
     * @return the grid of bombs if it already exists.  Null otherwise.
     */
    public boolean[][] getBombs(){
        return bombs;
    }
    /**
     * @return the grid of question marks.
     */
    @NonNull
    public boolean[][] getQuestionMarks(){
        return questionMarks != null ? questionMarks : new boolean[width][height];
    }
    /**
     * @return the grid of flags.
     */
    @NonNull
    public boolean[][] getFlags(){
        return flags != null ? flags : new boolean[width][height];
    }
    /**
     * @return the grid of uncovereds if it already exists.  Null otherwise.
     */
    public boolean[][] getUncovereds(){
        return uncovereds;
    }


    public static final int STATE_MASK = 0x00FFFFF0;
    public static final int NUMBER_MASK = 15;
    public static final int NUMBER_BITS = 4;

    public static final int COVERED = 1 << NUMBER_BITS;
    public static final int FLAGGED = 2 << NUMBER_BITS;
    public static final int QUESTION_MARK = 3 << NUMBER_BITS;
    public static final int FALSE_FLAG = 4 << NUMBER_BITS;
    public static final int LOST_BOMB = 5 << NUMBER_BITS;
    public static final int EXPLODED_BOMB = 6 << NUMBER_BITS;
    public static final int LOST_FLAG = 7 << NUMBER_BITS;
    public static final int LOST_QUESTION_MARK = 8 << NUMBER_BITS;
    public static final int FALSE_QUESTION_MARK = 9 << NUMBER_BITS;
    public static final int WON_BOMB = 10 << NUMBER_BITS;
    public static final int WON_FLAG = 11 << NUMBER_BITS;
    public static final int WON_QUESTION_MARK = 12 << NUMBER_BITS;
    public static final int UNCOVERED_NUMBER = 13 << NUMBER_BITS;


}
