package com.nahollenbaugh.mines.gamelogic;

public class NonexistantSquareException extends IllegalStateException {
    public NonexistantSquareException(String message){
        super(message);
    }
}

