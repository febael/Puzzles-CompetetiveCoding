package com.bawer.puzzles;


import java.util.Arrays;


public class SubsetSum_GivenSubsetSize {

    private static int solutionByteIndex;
    private static byte solutionBitMask;
    private static int solutionHolderSize;

    public static void main(String[] args) {
        System.out.println(
                solve(292, 8, 5, 7, 8, 11, 2, 4, 33, 24, 1, 19, 43, 184, 23, 12, 18, 65, 34, 69, 31, 15, 40, 37, 111, 29, 10, 14));
    }

    private static boolean solve(int target, int subsetSize, int... numbers) {
        // Edge cases
        if (target == 0) {
            return subsetSize == 0;
        }
        if (subsetSize == 0) {
            return false;
        }
        Arrays.sort(numbers);
        int length = numbers.length;
        while (numbers[--length] > target);
        if (length < numbers.length-1) {
            numbers = Arrays.copyOfRange(numbers, 0, length);
        }
        if (subsetSize > numbers.length) {
            return false;
        }
        if (numbers.length < 1) {
            return false;
        }
        if (subsetSize == numbers.length) {
            return Arrays.stream(numbers).reduce((i,j) -> i+j).getAsInt() == target;
        }
        if (subsetSize == 1) {
            for (int n : numbers) {
                if (n == target) return true;
            }
            return false;
        }
        int sumOfLastSubSetSize = Arrays.stream(numbers, numbers.length-subsetSize, numbers.length).reduce((i,j) -> i+j).getAsInt();
        if (sumOfLastSubSetSize < target) {
            return false;
        }
        if (sumOfLastSubSetSize == target) {
            return true;
        }

        // Prepare data
        solutionByteIndex = (subsetSize+1) / 8;
        solutionBitMask = (byte) (0x80>>(subsetSize % 8));
        solutionHolderSize = 1 + solutionByteIndex;

        // fill first row
        byte[][][] solutionMatrix = new byte[numbers.length][target+1][];
        for (int j = 1; j < numbers[0]; j++) { // fill first row
            solutionMatrix[0][j] = new byte[solutionHolderSize];
            unsetCell(solutionMatrix[0][j]);
        }
        solutionMatrix[0][numbers[0]] = new byte[solutionHolderSize];
        setCell(1, solutionMatrix[0][numbers[0]]);
        for (int j = numbers[0]+1; j < target+1; j++) { // fill first row
            solutionMatrix[0][j] = new byte[solutionHolderSize];
            unsetCell(solutionMatrix[0][j]);
        }

        // fill first column
        for (int i = 0; i < numbers.length; i++) {
            solutionMatrix[i][0] = new byte[solutionHolderSize];
        }

        // fill other rows
        for (int i = 1; i < numbers.length; i++) {
            int number = numbers[i];
            int j = 1;
            for (; j < number; j++) {
                solutionMatrix[i][j] = getCopy(solutionMatrix[i-1][j]);
            }
            solutionMatrix[i][j] = setSpecialCellValue(solutionMatrix[i-1][j], number);
            for (j++; j < target; j++) {
                solutionMatrix[i][j] = setCellValue(solutionMatrix[i-1], number, j);
            }
            solutionMatrix[i][target] = setCellValue(solutionMatrix [i-1], number, target);
            if (checkIfSolved(solutionMatrix[i][target])) {
                return true;
            }
        }

        return false;
    }

    private static boolean checkIfSolved(byte[] bs) {
        return (bs[solutionByteIndex] & solutionBitMask) > 0;
    }

    private static byte[] getCopy(byte[] bs) {
        return Arrays.copyOf(bs, bs.length);
    }

    private static byte[] setCellValue(byte[][] previousRow, int number, int columnNo) {
        byte[] bsInherited = previousRow[columnNo-number];
        if (!solutionAvailable(bsInherited)) {
            return Arrays.copyOf(previousRow[columnNo], previousRow[columnNo].length);
        }
        byte[] bsToReturn = getAShiftedCopy(bsInherited);
        if (solutionAvailable(previousRow[columnNo])) {
            merge(bsToReturn, previousRow[columnNo]);
        }
        return bsToReturn;
    }

    private static byte[] setSpecialCellValue(byte[] previousCell, int number) {
        byte[] bsToReturn = solutionAvailable(previousCell) ?
                Arrays.copyOf(previousCell, previousCell.length)
                : new byte[solutionHolderSize];
        setCell(1, bsToReturn);
        return bsToReturn;
    }

    private static void merge(byte[] cell, byte[] bs) {
        for (int i = 0; i < cell.length; i++) {
            cell[i] |= bs[i];
        }
    }

    private static byte[] getAShiftedCopy(byte[] bsInherited) {
        byte[] bsToReturn = new byte[bsInherited.length];
        for (int i = bsInherited.length-1; i > 0; i--) {
            bsToReturn[i] = (byte) (bsInherited[i]>>1);
            if (0x01 == (bsInherited[i-1] & 0x01)) {
                bsToReturn[i] |= 0x80;
            } else {
                bsToReturn[i] &= 0x7F;
            }
        }
        bsToReturn[0] = (byte) (bsInherited[0]>>1);
        return bsToReturn;
    }

    private static boolean solutionAvailable(byte[] bsInherited) {
        return 0 == (bsInherited[0] & 0x80);
    }

    private static void setCell(int i, byte[] bs) {
        bs[i/8] |= (byte) (0x80>>>(i%8));
    }

    private static void unsetCell(byte[] bs) {
        bs[0] |= 0x80;
    }
}
