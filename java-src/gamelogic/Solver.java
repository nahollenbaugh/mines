package com.nahollenbaugh.mines.gamelogic;

import android.util.Log;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class Solver {

    // i's go first in arrays, j's first in Game constructor, i's in visible state

    public static void play(Game g, boolean[][] known){
        if (data.theCovereds == null || data.theCovereds.length != known.length
                || data.theCovereds[0].length != known[0].length) {
            data.theCovereds = new boolean[known.length][known[0].length];
            inPiece = new Square[known.length][known[0].length];
        }
        boolean doFull = false;
        boolean didSomething = true;
        while (true){
            if (doFull){
                doFull = false;
                if (full(g)){
                    didSomething = true;
                } else {
                    if (!didSomething){
                        break;
                    }
                    didSomething = false;
                }
            } else {
                doFull = true;
                if (two(g,known)){
                    didSomething = true;
                } else {
                    if (!didSomething){
                        break;
                    }
                    didSomething = false;
                }
            }
        }
    }

    public static Value flagsLeft = new Value(){
        public int value(int i, int j, Game g, boolean[][] known, Object data){
            if ((g.visibleState(i,j) & Game.STATE_MASK) != Game.UNCOVERED_NUMBER){
                return 0;
            }
            return (g.visibleState(i,j) & Game.NUMBER_MASK)
                    - addAdjacents(i, j, g, 0, known, g.getFlags(), isInData);
        }
    };
    public static Value isUncoveredNumber = new Value(){
        public int value(int i, int j, Game g, boolean[][] known, Object data){
            return (g.visibleState(i,j) & Game.STATE_MASK) == Game.UNCOVERED_NUMBER
                    ? 1 : 0;
        }
    };
    public static Value addUncoveredNumberToData = new Value(){
        public int value(int i, int j, Game g, boolean[][] known, Object data){
            if ((g.visibleState(i,j) & Game.STATE_MASK) != Game.UNCOVERED_NUMBER){
                return 0;
            }
            Square[] bs = (Square[])data;
            for (int l = 0; l < bs.length; l++){
                if (bs[l] == null){
                    bs[l] = new Square(i, j, Square.NUMBER);
                    return 1;
                }
            }
            return 0;
        }
    };
    public static Value checkDataGuess = new Value(){
        public int value(int i, int j, Game g, boolean[][] known, Object data){
            if ((g.visibleState(i,j) & Game.STATE_MASK) != Game.UNCOVERED_NUMBER){
                return 0;
            }
            int bombs = addAdjacents(i, j, g, 0, known, data, isFlagOrGuess);
            int unknowns = addAdjacents(i, j, g, 0, known, data, isCoveredInGuess);
            int number = g.visibleState(i,j) & Game.NUMBER_MASK;
            return ((bombs > number) || (bombs + unknowns < number)) ? 1 : 0;
        }
    };
    public static Value isFlag = new Value(){
        public int value(int i, int j, Game g, boolean [][] known, Object data){
            return (g.visibleState(i,j) & Game.STATE_MASK) == Game.FLAGGED ? 1 : 0;
        }
    };
    public static Value isCoveredInGuess = new Value(){
        public int value(int i, int j, Game g, boolean[][] known, Object data){
            Square sq = ((Square[][])data)[i][j];
            return ((sq != null) && (sq.state == Square.UNKNOWN)) ? 1 : 0;
        }
    };
    public static Value isFlagOrGuess = new Value(){
        public int value(int i, int j, Game g, boolean[][] known, Object data){
            Square sq = ((Square[][])data)[i][j];
            return (((sq != null) && (sq.state == Square.BOMB))
                    || ((g.visibleState(i,j) & Game.STATE_MASK) == Game.FLAGGED)) ? 1 : 0;
        }
    };
    public static boolean full(Game g){
        Square[][] involved = new Square[g.getWidth()][g.getHeight()];
        List<PieceData> pieces = new ArrayList<>();
        int totalMax = 0;
        int totalMin = 0;
        for (int i = 0; i < involved.length; i++){
            for (int j = 0; j < involved[i].length; j++){
                if ((involved[i][j] == null)
                        && ((g.visibleState(i,j) & Game.STATE_MASK) == Game.COVERED)
                        && (addAdjacents(i, j, g, 0, null, null, flagsLeft) > 0)){
                    PieceData piece = createPiece(i,j,g,involved);
                    if (piece != null){
                        if (piece.didSomething){
                            // didSomething = true;
                            return true;
                        }
                        pieces.add(piece);
                        totalMax = totalMax + piece.max;
                        totalMin = totalMin + piece.min;
                    }
                }
            }
        }

        int uninvolvedCovereds = 0;
        for (int i = 0; i < g.getWidth(); i++){
            for (int j = 0; j < g.getHeight(); j++){
                if ((involved[i][j] == null)
                        && ((g.visibleState(i,j) & Game.STATE_MASK) == Game.COVERED)){
                    uninvolvedCovereds = uninvolvedCovereds + 1;
                }
            }
        }
        for (boolean changedPiece = true; changedPiece; ){
            changedPiece = false;
            if (totalMin == g.flagsLeft()){
                boolean didSomething = false;
                for (PieceData piece : pieces){
                    PieceData uncoverPiece
                            = checkPossibilities(g, involved, piece.piece, piece.min, piece.min);
                    didSomething = uncoverPiece.didSomething || didSomething;
                }
                for (int i = 0; i < g.getWidth(); i++) {
                    for (int j = 0; j < g.getHeight(); j++) {
                        if ((involved[i][j] == null)
                                && ((g.visibleState(i,j) & Game.STATE_MASK) == Game.COVERED)){
                            if (g.uncover(i,j)) {
                                didSomething = true;
                            }
                        }
                    }
                }
                if (didSomething){
                    return true;
                }
            }
            if (totalMax > g.flagsLeft()){
                boolean didSomething = false;
                for (PieceData piece : pieces){
                    int othersMin = 0;
                    for (PieceData otherPiece : pieces){
                        if (piece == otherPiece){
                            continue;
                        } else {
                            othersMin = othersMin + otherPiece.min;
                        }
                    }
                    PieceData newPiece = checkPossibilities(g, involved, piece.piece,
                            piece.min,
                            g.flagsLeft() - othersMin);
                    didSomething = newPiece.didSomething || didSomething;
                    if ((newPiece.max != piece.max) || (newPiece.min != piece.min)) {
                        changedPiece = true;
                    }
                }
                if (didSomething){
                    return true;
                }
            }
            if (totalMin + uninvolvedCovereds < g.flagsLeft()){
                boolean didSomething = false;
                for (PieceData piece : pieces){
                    int othersMax = 0;
                    for (PieceData otherPiece : pieces){
                        if (piece == otherPiece){
                            continue;
                        } else {
                            othersMax = othersMax + otherPiece.max;
                        }
                    }
                    PieceData newPiece
                            = checkPossibilities(g, involved, piece.piece,
                            g.flagsLeft() - othersMax - uninvolvedCovereds,
                            piece.max);
                    didSomething = newPiece.didSomething || didSomething;
                    if ((newPiece.max != piece.max) || (newPiece.min != piece.min)) {
                        changedPiece = true;
                    }
                }
                if (didSomething){
                    return true;
                }
            }
            totalMax = 0;
            totalMin = 0;
            for (PieceData piece : pieces){
                totalMax = totalMax + piece.max;
                totalMin = totalMin + piece.min;
            }
        }
        return false;
    }
    public static void add(Square[] vs, List<Square[]> done){
        Square[] copy = new Square[vs.length];
        for (int l = 0; l < vs.length; l++){
            copy[l] = new Square(vs[l].i, vs[l].j, vs[l].state);
        }
        done.add(copy);
    }


    public static Square[][] inPiece;
    public static Value expandPieceFromCovered = new Value(){
        public int value(int i, int j, Game g, boolean[][] known, Object data){
            if ((g.visibleState(i,j) & Game.STATE_MASK) != Game.COVERED){
                return 0;
            }
            Square[][] inPiece = (Square[][]) data;
            if (inPiece[i][j] != null){
                return 0;
            }
            inPiece[i][j] = new Square(i, j, Square.UNKNOWN);
            return 1 + addAdjacents(i, j, g, 0, null, data, expandPieceFromNumber);
        }
    };
    public static Value expandPieceFromNumber = new Value(){
        public int value(int i, int j, Game g, boolean[][] known, Object data){
            int state = g.visibleState(i,j);
            if ((state & Game.STATE_MASK) != Game.UNCOVERED_NUMBER){
                return 0;
            }
            if ((state & Game.NUMBER_MASK) == addAdjacents(i, j, g, 0, null, null, isFlag)){
                return 0;
            }
            return addAdjacents(i, j, g, 0, null, data, expandPieceFromCovered);
        }
    };
    public static PieceData createPiece(int startI, int startJ, Game g, Square[][] involved){
        for (int i = 0; i < inPiece.length; i++){
            for (int j = 0; j < inPiece[i].length; j++){
                inPiece[i][j] = null;
            }
        }
        int size = expandPieceFromCovered.value(startI, startJ, g, null, inPiece);
        if (size == 1){
            return null;
        }
        for (int i = 0; i < inPiece.length; i++){
            for (int j = 0; j < inPiece[i].length; j++){
                if (involved[i][j] == null){
                    involved[i][j] = inPiece[i][j];
                }
            }
        }

        Square[] covereds = new Square[size];
        int l = 0;
        for (int i = 0; i < g.getWidth(); i++){
            for (int j = 0; j < g.getHeight(); j++){
                if (inPiece[i][j] != null){
                    covereds[l] = inPiece[i][j];
                    l++;
                }
            }
        }
        int flagsLeft = g.flagsLeft();
        int coveredsLeft = 0;
        for (int i = 0; i < g.getWidth(); i++){
            for (int j = 0; j < g.getHeight(); j++){
                if (isCovered.value(i, j, g, null, null) == 1){
                    coveredsLeft = coveredsLeft + 1;
                }
            }
        }
        PieceData r = checkPossibilities(g, involved, covereds,
                flagsLeft + covereds.length - coveredsLeft,
                flagsLeft);
        if (r != null){
            return r;
        } else {
            return null;
        }
    }

    public static PieceData
    checkPossibilities(Game g, Square[][] involved, Square[] covereds,int min, int max){
        List<Square[]> bs = new ArrayList<>();
        buildPossibilities(g,involved,covereds,0,bs);

        boolean didSomething = false;

        Iterator<Square[]> it = bs.iterator();
        int actualMin = covereds.length;
        int actualMax = 0;

        while (it.hasNext()){
            int guessBombs = 0;
            Square[] vs = it.next();
            for (Square v : vs){
                if (v.state == Square.BOMB){
                    guessBombs = guessBombs + 1;
                }
            }
            if ((guessBombs > max) || (guessBombs < min)){
                it.remove();
            }
            if (guessBombs > actualMax){
                actualMax = guessBombs;
            }
            if (guessBombs < actualMin){
                actualMin = guessBombs;
            }
        }

        for (int k = 0; k < covereds.length; k++){
            boolean foundBomb = false;
            boolean foundSafe = false;
            for (Square[] vs : bs){
                if (vs[k].state == Square.BOMB){
                    foundBomb = true;
                } else {
                    foundSafe = true;
                }
            }
            if (!foundBomb){
                if (g.uncover(covereds[k].i,covereds[k].j)) {
                    didSomething = true;
                }
            }
            if (!foundSafe){
                g.flag(covereds[k].i,covereds[k].j);
                didSomething = true;
            }
        }
        PieceData r = new PieceData(actualMin, actualMax, covereds, didSomething);
        return r;
    }

    public static class PieceData {
        public int min;
        public int max;
        public Square[] piece;
        public boolean didSomething;
        public PieceData(int min, int max, Square[] piece, boolean didSomething){
            this.min = min;
            this.max = max;
            this.piece = piece;
            this.didSomething = didSomething;
        }
        public void print(int a, int b){
            println("["+min+","+max+"]");
            for (int i = 0; i < a; i++){
                Solver.print("|");
                for (int j = 0; j < b; j++){
                    boolean found = false;
                    for (Square s : piece){
                        if (s.i == i && s.j == j){
                            Solver.print("*");
                            found = true;
                            break;
                        }
                    }
                    if (!found){
                        Solver.print(" ");
                    }
                }
                println("|");
            }
            println();
        }
    }
    public static class Square {
        int i;
        int j;
        int state;
        public Square(int i, int j, int state){
            this.i = i;
            this.j = j;
            this.state = state;
        }
        public String toString(){
            switch(state){
                case BOMB: return "*";
                case SAFE: return "-";
                case UNKNOWN: return "+";
                case NUMBER: return "n";
                default: return "e";
            }
        }
        public static final int UNKNOWN = 0;
        public static final int BOMB = 1;
        public static final int SAFE = 2;
        public static final int NUMBER = 3;
    }
    public static void buildPossibilities(Game g, Square[][] involved, Square[] vs, int start,
                                          List<Square[]> done){
        if (start == vs.length){
            add(vs,done);
            return;
        }
        vs[start].state = Square.BOMB;
        if (addAdjacents(vs[start].i, vs[start].j, g, 0, null, involved, checkDataGuess) == 0){
            buildPossibilities(g,involved,vs,start+1,done);
        }
        vs[start].state = Square.SAFE;
        if (addAdjacents(vs[start].i, vs[start].j, g, 0, null, involved, checkDataGuess) == 0){
            buildPossibilities(g,involved,vs,start+1,done);
        }

        vs[start].state = Square.UNKNOWN;
    }

    public static boolean two(Game g, int i, int j, boolean[][] known){
        int state = g.visibleState(i,j);
        boolean didSomething = false;

        if ((state & Game.STATE_MASK) != Game.UNCOVERED_NUMBER){
            return false;
        }

        data.centerCovereds = addAdjacents(i, j, g, 0, known, data.theCovereds, setCoveredToData);
        data.centerFlagsLeft = (g.visibleState(i,j) & Game.NUMBER_MASK)
                - addAdjacents(i, j, g, 0, known, g.getFlags(), isInData);

        if (data.centerCovereds == data.centerFlagsLeft){ // flag one
            if (addAdjacents(i, j, g, 0, known, data.theCovereds,
                    flagIfUnflaggedRemoveFromData) > 0) {
                data.centerFlagsLeft = 0;
                data.centerCovereds = 0;
                didSomething = true;
            }
        }
        if (data.centerFlagsLeft == 0){ // uncover one
            if (addAdjacents(i, j, g, 0, known, data.theCovereds, uncoverRemoveFromData) > 0){
                data.centerCovereds = 0;
                didSomething = true;
            }
        }
        didSomething = (addTwoAway(i, j, g, 0, known, data, uncoverIfDataIsEnough) > 0)
                || didSomething;
        didSomething = (addAdjacents(i, j, g, 0, known, data, uncoverIfDataIsEnough) > 0)
                || didSomething;
        didSomething = (addTwoAway(i, j, g, 0, known, data, flagIfDataIsEnough) > 0)
                || didSomething;
        didSomething = (addAdjacents(i, j, g, 0, known, data, flagIfDataIsEnough) > 0)
                || didSomething;
        if (data.centerFlagsLeft == 0 && !didSomething){
            known[i][j] = true;
        }
        adjacents(i, j, g, 0, known, data.theCovereds, setDataToFalse);
        return didSomething;
    }
    // clearing a square can make adjacentCoveredData out of date in such a way as that
    // flagIfDataIsEnough is disrupted.  The square deciding whether to flag needs to
    // access to the number of shared covereds and the number of covereds it doesn't share.
    // the former we can only take at the time of the begining of the two(i,j), and the
    // latter only at the present time. Wtf no, we can compute covereds adjacent to the
    // edge square that are not in data, that gives us the old number, and the only other
    // stuff we ned are edge flags left and center flags left which have not changed.
    // E.g.
    // |  1XX|    |  1XX|    |  1XF|
    // |112XX| -> |11211| -> |11211|
    // |XX1XX|    |XX1  |    |XX1  |
    // |XXXXX|    |111  |    |111  |
    public static boolean two(Game g, boolean[][] known){
        for (int i = 0; i < known.length; i++){
            for (int j = 0; j < known[i].length; j++){
                known[i][j] = false;
            }
        }
        boolean hasChanged = true;
        boolean everChanged = false;
        while(hasChanged){
            hasChanged = false;
            for (int i = 0; i < g.getWidth(); i++){
                for (int j = 0; j < g.getHeight(); j++){
                    if (known[i][j]){
                        continue;
                    }
                    if (two(g, i, j, known)){
                        if (g.isLost()){
                            return false;
                        }
                        hasChanged = true;
                        everChanged = true;
                    }
                }
            }
        }
        return everChanged;
    }

    public static int addAdjacents(int i, int j, Game g, int def, boolean[][] known, Object data,
                                   Value value){
        int b = 0;
        for (int v : adjacents(i,j,g,def,known,data,value)){
            b = b + v;
        }
        return b;
    }
    public static int[] adjacents(int i, int j, Game g, int def, boolean[][] known, Object data,
                                  Value value){
        int[] vs = new int[]{def,def,def,def,def,def,def,def};
        int s = g.getWidth()-1;
        int t = g.getHeight()-1;
        if (i > 0 && j > 0) vs[0] = value.value(i-1,j-1,g,known,data);
        if (i > 0         ) vs[1] = value.value(i-1,j  ,g,known,data);
        if (i > 0 && j < t) vs[2] = value.value(i-1,j+1,g,known,data);
        if (         j < t) vs[3] = value.value(i  ,j+1,g,known,data);
        if (i < s && j < t) vs[4] = value.value(i+1,j+1,g,known,data);
        if (i < s         ) vs[5] = value.value(i+1,j  ,g,known,data);
        if (i < s && j > 0) vs[6] = value.value(i+1,j-1,g,known,data);
        if (         j > 0) vs[7] = value.value(i  ,j-1,g,known,data);
        return vs;
    }
    public static int addTwoAway(int i, int j, Game g, int def, boolean[][] known, Object data,
                                 Value value){
        int b = 0;
        for (int v : twoAway(i, j, g, def, known, data, value)){
            b = b + v;
        }
        return b;
    }
    public static int[] twoAway(int i, int j, Game g, int def, boolean[][] known, Object data,
                                Value value){
        int[] vs = new int[]{def,def,def,def,def,def,def,def,def,def,def,def,def,def,def,def};
        int s = g.getWidth()-2;
        int t = g.getHeight()-2;
        if (i > 1   && j > 1  ) vs[0]  = value.value(i-2,j-2,g,known,data);
        if (i > 1   && j > 0  ) vs[1]  = value.value(i-2,j-1,g,known,data);
        if (i > 1             ) vs[2]  = value.value(i-2,j,  g,known,data);
        if (i > 1   && j < t+1) vs[3]  = value.value(i-2,j+1,g,known,data);
        if (i > 1   && j < t  ) vs[4]  = value.value(i-2,j+2,g,known,data);
        if (i > 0   && j < t  ) vs[5]  = value.value(i-1,j+2,g,known,data);
        if (           j < t  ) vs[6]  = value.value(i,  j+2,g,known,data);
        if (i < s+1 && j < t  ) vs[7]  = value.value(i+1,j+2,g,known,data);
        if (i < s   && j < t  ) vs[8]  = value.value(i+2,j+2,g,known,data);
        if (i < s   && j < t+1) vs[9]  = value.value(i+2,j+1,g,known,data);
        if (i < s             ) vs[10] = value.value(i+2,j,  g,known,data);
        if (i < s   && j > 0  ) vs[11] = value.value(i+2,j-1,g,known,data);
        if (i < s   && j > 1  ) vs[12] = value.value(i+2,j-2,g,known,data);
        if (i < s+1 && j > 1  ) vs[13] = value.value(i+1,j-2,g,known,data);
        if (           j > 1  ) vs[14] = value.value(i,  j-2,g,known,data);
        if (i > 0   && j > 1  ) vs[15] = value.value(i-1,j-2,g,known,data);
        return vs;
    }

    public static abstract class Value {
        public abstract int value(int i, int j, Game g, boolean[][] known, Object data);
    }
    public static Value setCoveredToData = new Value(){
        public int value(int i, int j, Game g, boolean[][] known, Object data){
            if ((g.visibleState(i,j) & Game.STATE_MASK) == Game.COVERED){
                ((boolean[][])data)[i][j] = true;
                return 1;
            } else {
                return 0;
            }
        }
    };
    public static Value isCoveredOrFlag = new Value(){
        public int value(int i, int j, Game g, boolean[][] known, Object data){
            int state = (g.visibleState(i,j) & Game.STATE_MASK);
            return (state == Game.COVERED) || (state == Game.FLAGGED)
                    ? 1 : 0;
        }
    };
    public static Value isCovered = new Value(){
        public int value(int i, int j, Game g, boolean[][] known, Object data){
            return ((g.visibleState(i,j) & Game.STATE_MASK) == Game.COVERED) ? 1 : 0;
        }
    };
    public static Value isCoveredNotInData = new Value(){
        public int value(int i, int j, Game g, boolean[][] known, Object data){
            return (!((boolean[][])data)[i][j]
                    && (g.visibleState(i,j) & Game.STATE_MASK) == Game.COVERED) ? 1 : 0;
        }
    };
    public static Value flagIfDataIsEnough = new Value(){
        public int value(int i, int j, Game g, boolean[][] known, Object d){
            if ((g.visibleState(i,j) & Game.STATE_MASK) != Game.UNCOVERED_NUMBER){
                return 0;
            }
            int flagsRemaining = (g.visibleState(i,j) & Game.NUMBER_MASK)
                    - addAdjacents(i, j, g, 0, known, g.getFlags(), isInData);
            if (flagsRemaining == 0){
                return 0;
            }
            AdjacentCoveredData data = (AdjacentCoveredData)d;
            int coveredsNotShared
                    = addAdjacents(i, j, g, 0, known, data.theCovereds, isCoveredNotInData);
            if (coveredsNotShared == 0){
                return 0;
            }
            int neighborsInData
                    = addAdjacents(i, j, g, 0, known, data.theCovereds, isInData);
            int possibleAmongShared = (neighborsInData < data.centerFlagsLeft)
                    ? neighborsInData : data.centerFlagsLeft;
            if (flagsRemaining == possibleAmongShared + coveredsNotShared){
                int t = addAdjacents(i, j, g, 0, known, data.theCovereds, flagIfUnflagged);
                return t;
            }
            return 0;
        }
    };
    public static Value uncoverIfDataIsEnough = new Value(){
        public int value(int i, int j, Game g, boolean[][] known, Object d){
            if ((g.visibleState(i,j) & Game.STATE_MASK) != Game.UNCOVERED_NUMBER){
                return 0;
            }
            int flagsRemaining = (g.visibleState(i,j) & Game.NUMBER_MASK)
                    - addAdjacents(i, j, g, 0, known, g.getFlags(), isInData);
            if (flagsRemaining == 0){
                return 0;
            }
            AdjacentCoveredData data = (AdjacentCoveredData)d;
            int neighborsInData
                    = addAdjacents(i, j, g, 0, known, data.theCovereds, isInData);
            if (neighborsInData == data.centerCovereds - data.centerFlagsLeft
                    + flagsRemaining){
                return addAdjacents(i, j, g, 0, known, data.theCovereds, uncoverIfNotInData);
            }
            return 0;
        }
    };
    public static Value uncoverRemoveFromData = new Value(){
        public int value(int i, int j, Game g, boolean[][] known, Object data){
            ((boolean[][]) data)[i][j] = false;
            if (((g.visibleState(i,j)&Game.STATE_MASK)==Game.COVERED)){
                return g.uncover(i,j) ? 1 : 0;
            }
            return 0;
        }
    };
    public static Value uncoverIfNotInData = new Value(){
        public int value(int i, int j, Game g, boolean[][] known, Object data){
            if ((data != null) && ((boolean[][])data)[i][j]){
                return 0;
            }
            if (((g.visibleState(i,j)&Game.STATE_MASK)==Game.COVERED)){
                return g.uncover(i,j) ? 1 : 0;
            }
            return 0;
        }
    };
    public static Value isInData = new Value(){
        public int value(int i, int j, Game g, boolean[][] known, Object data){
            return ((boolean[][])data)[i][j] ? 1 : 0;
        }
    };
    public static Value setDataToFalse = new Value(){
        public int value(int i, int j, Game g, boolean[][] known, Object data){
            ((boolean[][])data)[i][j] = false;
            return 0;
        }
    };
    public static Value flagIfUnflaggedRemoveFromData = new Value(){
        public int value(int i, int j, Game g, boolean[][] known, Object data){
            ((boolean[][])data)[i][j]=false;
            if ((g.visibleState(i,j) & Game.STATE_MASK) == Game.COVERED){
                g.flag(i,j);
                return 1;
            } else {
                return 0;
            }
        }
    };
    public static Value flagIfUnflagged = new Value(){
        public int value(int i, int j, Game g, boolean[][] known, Object data){
            if (data != null && ((boolean[][])data)[i][j]){
                return 0;
            } else {
                if ((g.visibleState(i,j) & Game.STATE_MASK) == Game.COVERED){
                    g.flag(i,j);
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    };

    public static class AdjacentCoveredData {
        boolean[][] theCovereds;
        int centerCovereds;
        int centerFlagsLeft;
    }
    public static AdjacentCoveredData data = new AdjacentCoveredData();

    public static void print(boolean[][] vss){
        if (vss == null){
            println("null");
        } else {
            int i = 1;
            for (boolean[] vs : vss){
                print(i + "|");
                i = i + 1;
                for (boolean v : vs){
                    print(v?"*":" ");
                }
                println("|");
            }
            println();
        }
    }
    public static void printArrays(Game g){
        println("Bombs:");
        print(g.getBombs());
        println();
        println("Uncovereds:");
        print(g.getUncovereds());
        println();
        println("Flags:");
        print(g.getFlags());
        println();
        println("QuestionMarks:");
        print(g.getQuestionMarks());
    }
    public static void printStates(Game g){
        for (int i = 0; i < g.getWidth(); i++){
            print("|");
            for (int j = 0; j < g.getHeight(); j++){
                print(String.format("%2d",g.visibleState(i,j)>>4));
                print(" ");
            }
            println("|");
        }
        println();
    }
    public static void printNumbers(Game g){
        for (int i = 0; i < g.getWidth(); i++){
            print("|");
            for (int j = 0; j < g.getHeight(); j++){
                print(g.visibleState(i,j)&15);
            }
            println("|");
        }
        println();
    }
    public static void print(Game g){
        for (int i = 0; i < g.getWidth(); i++){
            print("|");
            for (int j = 0; j < g.getHeight(); j++){
                switch(g.visibleState(i,j)&(255-15)){
                    case Game.COVERED:
                    case Game.WON_BOMB:
                        print("+");
                        break;
                    case Game.FLAGGED:
                    case Game.WON_FLAG:
                        print("*");
                        break;
                    case Game.QUESTION_MARK:
                    case Game.WON_QUESTION_MARK:
                        print("?");
                        break;
                    case Game.FALSE_FLAG:
                    case Game.FALSE_QUESTION_MARK:
                        print("F");
                        break;
                    case Game.LOST_BOMB:
                        print("e");
                        break;
                    case Game.LOST_FLAG:
                    case Game.LOST_QUESTION_MARK:
                        print("f");
                        break;
                    case Game.EXPLODED_BOMB:
                        print("!");
                        break;
                    case Game.UNCOVERED_NUMBER:
                        print(g.visibleState(i,j)&15);
                        break;
                }
            }
            println("|");
        }
        println();
    }
    public static String p(int i, int j){
        return "("+i+","+j+")";
    }
    public static void print(Object[] vs){
        if (vs == null){
            print("null");
            return;
        }
        for (Object v : vs){
            print(v);
        }
        println();
    }
    public static void print(Object[][] vss){
        if (vss == null){
            println("null");
            return;
        }
        for (Object[] vs : vss){
            print("|");
            for (Object v : vs){
                if (v == null){
                    print(" ");
                } else {
                    print(v);
                }
            }
            println("|");
        }
    }
    public static void print(Game g, Square[] vs){
        for (int i = 0; i < g.getWidth(); i++){
            print("|");
            for (int j = 0; j < g.getHeight(); j++){
                boolean found = false;
                for (Square v : vs){
                    if (v == null){
                        continue;
                    }
                    if (v.i == i && v.j == j){
                        print(v);
                        found = true;
                    }
                }
                if (!found){
                    print(" ");
                }
            }
            println("|");
        }
        println();
    }
    static String printing = "";
    public static void print(Object o){
        printing = printing + o.toString();
    }
    public static void println(Object o){
        print(o);
        Log.println(Log.ERROR,"",printing);
        printing = "";
    }
    public static void println(){
        println("");
    }
}
