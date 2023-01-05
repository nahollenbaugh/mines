package com.nahollenbaugh.mines.gamelogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HintGame extends Game{
    protected boolean[][] existingFlags;
    public HintGame(boolean[][] bombs, boolean[][] flags, boolean[][] uncovereds,
                    boolean[][] questionMarks){
        super(bombs, new boolean[flags.length][flags[0].length], uncovereds,
                new boolean[questionMarks.length][questionMarks[0].length]);
        existingFlags = flags;
    }
    public List<Hint> hints = new ArrayList<>();
    protected boolean[][] hintUncovereds = new boolean[getWidth()][getHeight()];

    @Override
    public boolean flag(int i, int j){
        if (super.flag(i,j)) {
            if (!existingFlags[i][j]) {
                hints.add(new Hint(i, j, false));
            }
            return true;
        }
        return false;
    }
    @Override
    public boolean uncover(int i, int j){
        if (uncovereds[i][j] || hintUncovereds[i][j]){
            return false;
        }
        hintUncovereds[i][j] = true;
        hints.add(new Hint(i,j,true));
        return true;
    }
    public class Hint{
        public int i;
        public int j;
        public boolean isSafe;
        public Hint(int i, int j, boolean isSafe){
            this.i = i;
            this.j = j;
            this.isSafe = isSafe;
        }
        public boolean equals(Hint h){
            return (h.i == i) && (h.j == j) && (h.isSafe == isSafe);
        }
    }
}
