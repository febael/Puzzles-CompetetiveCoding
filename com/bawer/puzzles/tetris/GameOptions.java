package com.bawer.puzzles.tetris;


public class GameOptions {

    public static final byte EMPTY_CELL = ' ';
    public static final byte FULL_CELL = 'o';
    public static final byte NEW_CELL = '+';

    public static GameOptions fromArgs(String[] args) {
        GameOptions go = new GameOptions();

        for ( String arg : args ) {
            if ((arg.length() < 2) || (arg.charAt(0) != '/')) {
                continue;
            }
            if (arg.charAt(1) == 'l') {
                int level = arg.charAt(2) - 48; // '0' == 48 (dec)
                go.setStartingLevel(level);
            }
        }

        return go;
    }

    /**
     * Valid values are 0-9, while;
     * 0 = no automatic move downwards
     * 1-9 = speed at which the piece moves downwards increases at each level.
     *
     */
    private int startingLevel = 0;

    private void setStartingLevel(int level) {
        if ( (level > 0) && (level < 10) ) {
            startingLevel = level;
        }
    }

    public int getStartingLevel() {
        return startingLevel;
    }

    /**
     * Fixed setting for now.
     *
     */
    private int noOfRows = 20;

    /**
     * Fixed setting for now.
     *
     */
    private int noOfColumns = 20;

    public int getNoOfRows() {
        return noOfRows;
    }

    public int getNoOfColumns() {
        return noOfColumns;
    }
}
