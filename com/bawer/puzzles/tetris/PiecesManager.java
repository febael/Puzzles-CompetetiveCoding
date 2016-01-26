package com.bawer.puzzles.tetris;

public class PiecesManager {

    public static Piece getNextPiece() {
        int i = (int) (Math.random() * pieces.length);
        pieces[i].currentVariationIndex = pieces[i].defaultVariationIndex;
        return pieces[i];
    }

    private PiecesManager() {}

    static class Piece {
        private byte[][][] variations;
        private int currentVariationIndex;
        private int defaultVariationIndex;
        private int columnStart, rowStart, columnSize, rowSize;
        private int currentRowIndex, currentColumnIndex;

        public int getCurrentRowIndex() {
            return currentRowIndex;
        }

        public void setCurrentRowIndex(int currentRowIndex) {
            this.currentRowIndex = currentRowIndex;
        }

        public int getCurrentColumnIndex() {
            return currentColumnIndex;
        }

        public void setCurrentColumnIndex(int currentColumnIndex) {
            this.currentColumnIndex = currentColumnIndex;
        }

        public int getColumnStart() {
            return columnStart;
        }

        public int getRowStart() {
            return rowStart;
        }

        public int getColumnSize() {
            return columnSize;
        }

        public int getRowSize() {
            return rowSize;
        }

        public Piece(byte[][][] variations) {
            this.variations = variations;
            this.currentVariationIndex = this.defaultVariationIndex = variations.length / 2;
            setLimits();
        }

        private void setLimits() {
            byte[][] defaultVariation = variations[defaultVariationIndex];
            rowStart = 0;
            rowStartLoop:
                for (int i = 0; i < defaultVariation.length; i++) {
                    for (int j = 0; j < defaultVariation[i].length; j++) {
                        if (defaultVariation[i][j] != GameOptions.EMPTY_CELL) break rowStartLoop;
                    }
                    rowStart += 1;
                }
            int rowEnd = defaultVariation.length - 1;
            rowEndLoop:
                for (int i = rowEnd; i < defaultVariation.length; i--) {
                    for (int j = 0; j < defaultVariation[i].length; j++) {
                        if (defaultVariation[i][j] != GameOptions.EMPTY_CELL) break rowEndLoop;
                    }
                    rowEnd -= 1;
                }
            columnStart = 0;
            columnStartLoop:
                for (int j = 0; j < defaultVariation[0].length; j++) {
                    for (int i = 0; i < defaultVariation.length; i++) {
                        if (defaultVariation[i][j] != GameOptions.EMPTY_CELL) break columnStartLoop;
                    }
                    columnStart += 1;
                }
            int columnEnd = defaultVariation[0].length - 1;
            columnEndLoop:
                for (int j = rowEnd; j < defaultVariation.length; j--) {
                    for (int i = 0; i < defaultVariation.length; i++) {
                        if (defaultVariation[i][j] != GameOptions.EMPTY_CELL) break columnEndLoop;
                    }
                    columnEnd -= 1;
                }
            rowSize = rowEnd - rowStart + 1;
            columnSize = columnEnd - columnStart + 1;
        }

        public void rotateRight() {
            currentVariationIndex = (currentVariationIndex == variations.length-1) ?
                    defaultVariationIndex : (currentVariationIndex + 1);
        }

        public void rotateLeft() {
            currentVariationIndex = currentVariationIndex == 0 ?
                    defaultVariationIndex : (currentVariationIndex - 1);
        }

        public boolean isSolidAt(int i, int j) {
            return variations[currentVariationIndex][i][j] != GameOptions.EMPTY_CELL;
        }
    }

    private final static byte e = GameOptions.EMPTY_CELL;
    private final static byte f = GameOptions.FULL_CELL;

    private static byte[][][] variations1 =  {
        {
            { e,f,e,e },
            { e,f,e,e },
            { e,f,e,e },
            { e,f,e,e }
        },
        {
            { f,f,f,f },
            { e,e,e,e },
            { e,e,e,e },
            { e,e,e,e }
        },
        {
            { e,e,f,e },
            { e,e,f,e },
            { e,e,f,e },
            { e,e,f,e }
        }
    };

    private static byte[][][] variations2 =  {
        {
            { e,e,e,e },
            { e,e,e,e },
            { f,f,f,e },
            { f,e,e,e }
        },
        {
            { e,e,e,e },
            { e,f,f,e },
            { e,e,f,e },
            { e,e,f,e }
        },
        {
            { e,e,e,e },
            { e,e,e,e },
            { e,e,f,e },
            { f,f,f,e }
        },
        {
            { e,e,e,e },
            { e,f,e,e },
            { e,f,e,e },
            { e,f,f,e }
        },
        {
            { e,e,e,e },
            { e,e,e,e },
            { e,f,f,f },
            { e,f,e,e }
        },
        {
            { e,e,e,e },
            { e,f,f,e },
            { e,e,f,e },
            { e,e,f,e }
        },
        {
            { e,e,e,e },
            { e,e,e,e },
            { e,e,e,f },
            { e,f,f,f }
        },
    };

    private static byte[][][] variations3 =  {
        {
            { e,e,e,e },
            { e,e,e,e },
            { f,e,e,e },
            { f,f,f,e }
        },
        {
            { e,e,e,e },
            { e,f,f,e },
            { e,f,e,e },
            { e,f,e,e }
        },
        {
            { e,e,e,e },
            { e,e,e,e },
            { f,f,f,e },
            { e,e,f,e }
        },
        {
            { e,e,e,e },
            { e,e,f,e },
            { e,e,f,e },
            { e,f,f,e }
        },
        {
            { e,e,e,e },
            { e,e,e,e },
            { e,f,e,e },
            { e,f,f,f }
        },
        {
            { e,e,e,e },
            { e,f,f,e },
            { e,f,e,e },
            { e,f,e,e }
        },
        {
            { e,e,e,e },
            { e,e,e,e },
            { e,f,f,f },
            { e,e,e,f }
        }
    };

    private static byte[][][] variations4 =  {
        {
            { e,e,e,e },
            { e,e,e,e },
            { f,f,e,e },
            { e,f,f,e }
        },
        {
            { e,e,e,e },
            { e,e,f,e },
            { e,f,f,e },
            { e,f,e,e }
        },
        {
            { e,e,e,e },
            { e,e,e,e },
            { e,f,f,e },
            { e,e,f,f }
        }
    };

    private static byte[][][] variations5 =  {
        {
            { e,e,e,e },
            { e,e,e,e },
            { e,f,f,e },
            { e,f,f,e }
        }
    };

    private static Piece[] pieces = {
        new Piece(variations1),
        new Piece(variations2),
        new Piece(variations3),
        new Piece(variations4),
        new Piece(variations5)
    };
}
