package com.bawer.puzzles.tetris;

import java.util.Scanner;
import java.util.regex.Pattern;

import com.bawer.puzzles.tetris.PiecesManager.Piece;


public class Game {

    private static class GameStatistics {
        private int points = 0;
        private int noOfPieces = 0;
        private int noOfCrashes = 0;
        private int noOfCrashedLines = 0;
    }

    private GameStatistics statistics = new GameStatistics();
    private GameOptions options;
    private byte[][] board;
    private Piece currentPiece = null;
    private final Scanner scanner = new Scanner(System.in);

    private static final Pattern VALID_COMMANDS_PATTERN = Pattern.compile("[asdwx]");

    public Game(GameOptions options) {
        this.options = options;
        initializeBoard();
        addNewPieceToBoard();
    }

    private boolean addNewPieceToBoard() {
        statistics.noOfPieces += 1;
        currentPiece = PiecesManager.getNextPiece();
        final int placementIndex = (int) ((board.length + 1 - currentPiece.getColumnSize()) * Math.random());
        currentPiece.setCurrentColumnIndex(placementIndex - currentPiece.getColumnStart());
        currentPiece.setCurrentRowIndex(0 - currentPiece.getRowStart());
        return tryPlacePiece();
    }

    private boolean tryPlacePiece() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if ( currentPiece.isSolidAt(i,j) ) {
                    int newRowIndex = currentPiece.getCurrentRowIndex() + i;
                    int newColumnIndex = currentPiece.getCurrentColumnIndex() + j;
                    if ( (newColumnIndex < 0) || (newRowIndex < 0) ||
                            (newColumnIndex >= board.length) ||
                            (newRowIndex >= board[0].length)) {
                        return false;
                    }
                    if (board[newRowIndex][newColumnIndex] != GameOptions.EMPTY_CELL) {
                        return false;
                    }
                    board[newRowIndex][newColumnIndex] = GameOptions.NEW_CELL;
                }
            }
        }
        return true;
    }

    private void initializeBoard() {
        board = new byte[options.getNoOfColumns()][];
        for (int i = 0; i < options.getNoOfColumns(); i++) {
            board[i] = new byte[options.getNoOfRows()];
            for (int j = 0; j < options.getNoOfRows(); j++) {
                board[i][j] = GameOptions.EMPTY_CELL;
            }
        }
    }

    private void simulate() {
        do {
            printTitle();
            printHelp();
            printBoard();
            printGameData();
        } while (gameStep());
    }

    private void printGameData() {
        System.out.println();
        System.out.println(String.format("*** points: %d", statistics.points));
        System.out.println(String.format("*** no of pieces: %d", statistics.noOfPieces));
        System.out.println(String.format("*** no of crashes: %d", statistics.noOfCrashes));
        System.out.println(String.format("*** no of crashed lines: %d", statistics.noOfCrashedLines));
    }

    private void printBoard() {
        System.out.println();
        for (int i = 0; i < board.length; i++) {
            System.out.print("        ***");
            for (int j = 0; j < board[0].length; j++) {
                System.out.print((char)board[i][j]);
            }
            System.out.println("***       ");
        }
        System.out.print("        ***");
        for (int j = 0; j < board[0].length; j++) {
            System.out.print('*');
        }
        System.out.println("***       ");
    }

    private void printHelp() {
        System.out.println();
        System.out.println("*** control keys: *************************");
        System.out.println("*** 'a' : move piece left *****************");
        System.out.println("*** 'd' : move piece right ****************");
        System.out.println("*** 'w' : rotate piece counter-clockwise **");
        System.out.println("*** 's' : rotate piece clockwise **********");
        System.out.println("*** 'x' : drop piece one downwards ********");
    }

    private void printTitle() {
        System.out.println();
        System.out.println();
        System.out.println("***** TETRIS by ferit baver elhüseyni *****");
    }

    private boolean gameStep() {
        String command = "";
        try {
            command = scanner.next(VALID_COMMANDS_PATTERN);
        } catch (Exception e) {
            scanner.nextLine();
            return true; //doNothing;
        }
        clearPieceFromBoard();
        boolean result = true;
        switch (command) {
        case "a":
            currentPiece.setCurrentColumnIndex(currentPiece.getCurrentColumnIndex() - 1);
            result = tryPlacePiece();
            if (result == false) {
                clearPieceFromBoard();
                currentPiece.setCurrentColumnIndex(currentPiece.getCurrentColumnIndex() + 1);
                tryPlacePiece();
            }
            break;
        case "d":
            currentPiece.setCurrentColumnIndex(currentPiece.getCurrentColumnIndex() + 1);
            result = tryPlacePiece();
            if (result == false) {
                clearPieceFromBoard();
                currentPiece.setCurrentColumnIndex(currentPiece.getCurrentColumnIndex() - 1);
                tryPlacePiece();
            }
            break;
        case "s":
            currentPiece.rotateRight();
            result = tryPlacePiece();
            if (result == false) {
                clearPieceFromBoard();
                currentPiece.rotateLeft();
                tryPlacePiece();
            }
            break;
        case "w":
            currentPiece.rotateLeft();
            result = tryPlacePiece();
            if (result == false) {
                clearPieceFromBoard();
                currentPiece.rotateRight();
                tryPlacePiece();
            }
            break;
        case "x":
            currentPiece.setCurrentRowIndex(currentPiece.getCurrentRowIndex() + 1);
            result = tryPlacePiece();
            if (result == false) {
                clearPieceFromBoard();
                currentPiece.setCurrentRowIndex(currentPiece.getCurrentRowIndex() - 1);
                solidifyPiece();
                checkForLineCrash();
                return addNewPieceToBoard();
            }
            break;
        default:
            break; // unexpected
        }
        return true;
    }

    private void solidifyPiece() {
        tryPlacePiece();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == GameOptions.NEW_CELL) {
                    board[i][j] = GameOptions.FULL_CELL;
                }
            }
        }
    }

    private void checkForLineCrash() {
        int noOfCrashedLines = 0;

        rowTraversing:
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[0].length; j++) {
                    if (board[i][j] == GameOptions.EMPTY_CELL) {
                        continue rowTraversing;
                    }
                }
                noOfCrashedLines += 1;
                removeRow(i);
            }

        if (noOfCrashedLines > 0) {
            statistics.noOfCrashes += 1;
            statistics.noOfCrashedLines += noOfCrashedLines;
        }
    }

    private void removeRow(int i) {
        for (; i > 1; i--) {
            for (int j = 0; j < board[0].length; j++) {
                board[i][j] = board[i-1][j];
            }
        }
        for (int j = 0; j < board[0].length; j++) {
            board[0][j] = GameOptions.EMPTY_CELL;
        }
    }

    private void clearPieceFromBoard() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == GameOptions.NEW_CELL) {
                    board[i][j] = GameOptions.EMPTY_CELL;
                }
            }
        }
    }

    public static void main(String[] args) {
        GameOptions options = GameOptions.fromArgs(args);
        new Game(options).simulate();
    }
}
