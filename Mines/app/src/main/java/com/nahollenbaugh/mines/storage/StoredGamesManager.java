package com.nahollenbaugh.mines.storage;

import android.content.Context;
import android.util.Log;

import com.nahollenbaugh.mines.gamelogic.GameData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class StoredGamesManager {
    public static String[] listGameNames(Context ctxt){
        try {
            FileInputStream in = ctxt.openFileInput(StoredDataStrings.listOfGamesFileName);
            String[] bs = new String[readNumberNames(in)];
            for (int i = 0; i < bs.length; i++){
                bs[i]=readNextName(in);
            }
            return bs;
        } catch (FileNotFoundException e){
            return new String[]{};
        } catch (IOException e){
            e.printStackTrace();
            return new String[]{};
        }
    }
    public static boolean addGame(String name, GameData gd, Context ctxt){
        String[] names = listGameNames(ctxt);
        if (names.length >= 255){
            return false;
        }
        try {
            FileOutputStream out = ctxt.openFileOutput(StoredDataStrings.listOfGamesFileName,
                    Context.MODE_PRIVATE);
            writeNumberNames(out,names.length+1);
            for (String s : names){
                writeName(out,s);
            }
            writeName(out,name);
            out.close();
            return new StoreGame(ctxt,name).writeGame(gd);
        } catch (IOException e){
            return false;
        }
    }
    public static boolean deleteGame(String name, Context ctxt){
        try {
            File f = ctxt.getFileStreamPath(name);
            if (f.exists() && !f.delete()){
                return false;
            }
            String[] names = listGameNames(ctxt);
            FileOutputStream out = ctxt.openFileOutput(StoredDataStrings.listOfGamesFileName,
                    Context.MODE_PRIVATE);
            writeNumberNames(out,names.length - 1);
            for (String n : names) {
                if (!n.equals(name)) {
                    writeName(out, n);
                }
            }
            out.close();
            return true;
        } catch (IOException e) {
            Log.println(Log.ERROR,"",e.toString());
            return false;
        }
    }

    protected static void writeNumberNames(FileOutputStream out, int number) throws IOException {
        out.write(number);
    }
    protected static int readNumberNames(FileInputStream in) throws IOException {
        return in.read();
    }

    protected static void writeName(FileOutputStream out, String name) throws IOException{
        for (int i = 0; i < name.length(); i++) {
            out.write(name.charAt(i) % 256);
            out.write((name.charAt(i) >> 8) % 256);
        }
        out.write(0);
        out.write(0);
    }

    protected static String readNextName(FileInputStream in) throws IOException {
        int fst = in.read();
        int snd = in.read();
        StringBuilder b = new StringBuilder();
        while ((fst | snd) != 0){
            b.append(Character.toChars(fst+256*snd));
            fst = in.read();
            snd = in.read();
        }
        return b.toString();
    }
}
