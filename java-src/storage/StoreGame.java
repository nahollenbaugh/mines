package com.nahollenbaugh.mines.storage;


import android.content.Context;
import android.util.Log;

import com.nahollenbaugh.mines.R;
import com.nahollenbaugh.mines.gamelogic.Game;
import com.nahollenbaugh.mines.gamelogic.GameData;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class StoreGame {
    FileOutputStream out;
    FileInputStream in;
    Context ctxt;
    String fileName;

    public StoreGame(Context ctxt) {
        this(ctxt,ctxt.getResources().getString(R.string.current_game_file));
    }
    public StoreGame(Context ctxt, String fileName) {
        this.ctxt = ctxt;
        this.fileName = fileName;
    }
    public void openOut() throws IOException {
        out = ctxt.openFileOutput(fileName, Context.MODE_PRIVATE);
    }
    public void openIn() throws IOException {
        in = ctxt.openFileInput(fileName);
    }
    public boolean closeOut(){
        try {
            out.close();
            return true;
        } catch (IOException e){
            return false;
        }
    }
    public boolean closeIn(){
        try {
            in.close();
            return true;
        } catch (IOException e){
            return false;
        }
    }

    public boolean writeGame(GameData gd){
        try {
            openOut();
            out.write(TIME_FIRST);
            out.write(gd.time & MAX_MASK);
            out.write(NUMUNCOVERS);
            out.write(gd.game.countUncovers & MAX_MASK);
            out.write(gd.game.countUncovers >> MAX_BITS);
            out.write(TIME_SECOND);
            out.write((gd.time >> MAX_BITS) & MAX_MASK);
            out.write(TIME_THIRD);
            out.write(((gd.time >> MAX_BITS) >> MAX_BITS) & MAX_MASK);
            out.write(BOARD);
            out.write(OUTER_DIM);
            out.write(gd.game.getWidth() & MAX_MASK);
            out.write(gd.game.getWidth() >> MAX_BITS);
            out.write(INNER_DIM);
            out.write(gd.game.getHeight() & MAX_MASK);
            out.write(gd.game.getHeight() >> MAX_BITS);
            int b;
            boolean[][] bombs = gd.game.getBombs();
            boolean[][] flags = gd.game.getFlags();
            boolean[][] questionMarks = gd.game.getQuestionMarks();
            boolean[][] uncovereds = gd.game.getUncovereds();
            for (int i = 0; i < gd.game.getWidth(); i++) {
                for (int j = 0; j < gd.game.getHeight(); j++) {
                    b = 0;
                    if (bombs != null) b = add(b, bombs[i][j], BOMB_SHIFT);
                    b = add(b, flags[i][j], FLAG_SHIFT);
                    b = add(b, questionMarks[i][j], QUESTIONMARK_SHIFT);
                    if (uncovereds != null) b = add(b, uncovereds[i][j], UNCOVEREDS_SHIFT);
                    out.write(b);
                }
                out.write(END_OF_ROW);
            }
            out.write(END_OF_GRID);
            if (bombs == null){
                out.write(IS_OPENED);
                out.write(0);
                out.write(NUMBOMBS);
                out.write(gd.game.getNumbombs() & MAX_MASK);
                out.write((gd.game.getNumbombs() >> MAX_BITS) & MAX_MASK);
            } else {
                out.write(IS_OPENED);
                out.write(1);
            }
            out.write(END);
            out.flush();
        } catch (IOException e){
            Log.println(Log.ERROR,"","write "+e);
            closeOut();
            return false;
        }
        return closeOut();
    }

    public boolean writeNoGame(){
        try {
            openOut();
            out.write(EMPTY);
        } catch (IOException e){
            Log.println(Log.ERROR,"","write none " + e);
            closeOut();
            return false;
        }
        return closeOut();
    }

    public GameData readGame() {
        GameData g = new GameData();
        try {
            openIn();
            boolean done = false;
            int countUncovers = 0;
            while (!done) {
                switch (in.read()) {
                    case EMPTY:
                        return null;
                    case BOARD:
                        check(in.read(), OUTER_DIM);
                        int outerDim = in.read() + (in.read() << 7);
                        check(in.read(), INNER_DIM);
                        int innerDim = in.read() + (in.read() << 7);
                        boolean[][] bombs = new boolean[outerDim][innerDim];
                        boolean[][] flags = new boolean[outerDim][innerDim];
                        boolean[][] uncovereds = new boolean[outerDim][innerDim];
                        boolean[][] questionMarks = new boolean[outerDim][innerDim];
                        int aaa = 0;
                        for (int i = 0; i < outerDim; i++) {
                            for (int j = 0; j < innerDim; j++) {
                                int v = in.read();
                                aaa = 1 - aaa;
                                bombs[i][j] = value(v, BOMB_SHIFT);
                                flags[i][j] = value(v, FLAG_SHIFT);
                                uncovereds[i][j] = value(v, UNCOVEREDS_SHIFT);
                                questionMarks[i][j] = value(v, QUESTIONMARK_SHIFT);
                            }
                            check(in.read(), END_OF_ROW);
                        }
                        check(in.read(), END_OF_GRID);
                        g.game = new Game(bombs, flags, uncovereds, questionMarks);
                        g.game.countUncovers = countUncovers;
                        break;
                    case TIME_FIRST:
                        int firstTime = in.read();
                        g.time = g.time + firstTime;
                        if (firstTime != (firstTime & MAX_MASK)) {
                            badFormat();
                        }
                        break;
                    case TIME_SECOND:
                        int secondTime = in.read();
                        g.time = g.time + (secondTime << MAX_BITS);
                        if (secondTime != (secondTime & MAX_MASK)) {
                            badFormat();
                        }
                        break;
                    case TIME_THIRD:
                        int thirdTime = in.read();
                        g.time = g.time + ((thirdTime << MAX_BITS) << MAX_BITS);
                        if (thirdTime != (thirdTime & MAX_MASK)) {
                            badFormat();
                        }
                        break;
                    case IS_OPENED:
                        if (in.read() == 0) {
                            g.game.setUnopened();
                        }
                        break;
                    case NUMBOMBS:
                        g.game.setNumbombs(in.read() + (in.read() << MAX_BITS));
                        break;
                    case NUMUNCOVERS:
                        countUncovers = in.read() + (in.read() << MAX_BITS);
                        if (g.game != null) {
                            g.game.countUncovers = countUncovers;
                        }
                        break;
                    case END:
                        done = true;
                        break;
                    default:
                        badFormat();
                }
            }
        } catch (IOException e){
            Log.println(Log.ERROR,"","StoreGame.java IOException");
            Log.println(Log.ERROR,"","StoreGame.java "+e.getLocalizedMessage());
            Log.println(Log.ERROR,"","StoreGame.java "+e.getMessage());
            Object[] a = e.getStackTrace();
            for (Object s : a){
                Log.println(Log.ERROR,"","StoreGame.java "+s.toString());
            }
            return null;
        }
        closeIn();
        return g;
    }

    protected void check(int a, int b) throws StoredDataStrings.BadFileException {
        if (a != b){
            badFormat();
        }
    }

    protected void badFormat() throws StoredDataStrings.BadFileException {
        throw new StoredDataStrings.BadFileException();
    }

    protected boolean value(int v, int shift){
        return (v & MASKS[shift]) >> shift == 1;
    }
    protected int add(int c, boolean v, int shift){
        if (v){
            return c + (1 << shift);
        } else {
            return c;
        }
    }

    protected final int END = 128;
    protected final int EMPTY = 139;

    protected final int INNER_DIM = 129;
    protected final int OUTER_DIM = 130;
    protected final static int END_OF_ROW = 131;
    protected final static int END_OF_GRID = 132;

    protected final int BOARD = 133;
    protected final static int TIME_FIRST = 134;
    protected final static int TIME_SECOND = 135;
    protected final static int TIME_THIRD = 136;
    protected final static int IS_OPENED = 137;
    protected final static int NUMBOMBS = 138;
    protected final static int NUMUNCOVERS = 140;

    // values leq than MAX_MASK are numbers, greater than MAX_MASK are enumerated constants.
    protected final static int MAX_MASK = 127;
    protected final static int MAX_BITS = 8;

    protected final static int[] MASKS = {1,2,4,8};
    protected final static int BOMB_SHIFT = 0;
    protected final static int FLAG_SHIFT = 1;
    protected final static int UNCOVEREDS_SHIFT = 2;
    protected final static int QUESTIONMARK_SHIFT = 3;

}
