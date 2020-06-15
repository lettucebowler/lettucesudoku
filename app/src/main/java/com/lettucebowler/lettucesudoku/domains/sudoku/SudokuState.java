package com.lettucebowler.lettucesudoku.domains.sudoku;

import com.lettucebowler.lettucesudoku.framework.problem.State;
import java.util.Arrays;

/**
 * This class represents states of various tile-moving puzzle problems,
 * including the 8-Puzzle, which involves a 3x3 display area. It can also
 * represent other displays of different dimensions, e.g. 4x4. A puzzle state is
 * represented with a 2D array of integers.
 *
 * @author tcolburn
 */
public class SudokuState implements State {

    /**
     * A 2D integer array represents the sudoku board.
     */
    private final int[][] tiles;

    /**
     * Getter for the underlying 2D array. Most users of this class will not
     * access these representation details. This will be useful when the problem
     * solving framework adds heuristic search to its abilities.
     *
     * @return the underlying 2D array
     */
    public int[][] getTiles() {
        return tiles;
    }

    /**
     * A puzzle state constructor that accepts a 2D array of integers.
     *
     * @param tiles a 2d array of integers
     */
    public SudokuState(int[][] tiles) { this.tiles = tiles; }

    /**
     * A SudokuState constructor that takes a given state, a number, and it's position represented
     * as a row and column integer.
     * and returns a new state with given number in given position, overwriting previous tiles
     * if necessary. The original puzzl state is not changed.
     *
     * @param state an existing puzzle state
     * @throws ArrayIndexOutOfBoundsException if either location is invalid for
     * this state
     */
    public SudokuState(SudokuState state, int num, int row, int col) {
        tiles = copyTiles(state.tiles);
        tiles[row][col] = num;
    }

    /**
     * Tests for equality of this puzzle state with another.
     *
     * @param o the other state
     * @return true if the underlying arrays are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o.getClass() != SudokuState.class) {
            return false;
        }
        SudokuState other = (SudokuState) o;
        if (this == other) {
            return true;
        }
        return Arrays.deepEquals(this.tiles, other.tiles);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Arrays.deepHashCode(this.tiles);
        return hash;
    }

    /**
     * Creates a string representation of this state, with the tiles laid out in
     * a table with dashes and vertical bars used to separate rows and columns.
     *
     * @return the string representation of this state
     */
    @Override
    public String toString() {
        int width = tiles[0].length;
        StringBuilder builder = new StringBuilder();
        for (int[] row : tiles) {
            builder.append(horizontalDivider(width));
            builder.append("\n");
            builder.append(horizontalRow(row));
            builder.append("\n");
        }
        builder.append(horizontalDivider(width));
        return builder.toString();
    }

    /*
    Private helper methods follow.
     */
    private static String horizontalDivider(int width) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < width; i++) {
            builder.append("+---");
        }
        builder.append("+");
        return builder.toString();
    }

    private static String horizontalRow(int[] tiles) {
        StringBuilder builder = new StringBuilder();
        for (int tile : tiles) {
            builder.append("|");
            builder.append(tileString(tile));
        }
        builder.append("|");
        return builder.toString();
    }

    private static String tileString(int tile) {
        if (tile == 0) {
            return "   ";
        }
        if (tile / 10 == 0) {
            return " " + tile + " ";
        }
        return tile + " ";
    }

    private static int[][] copyTiles(int[][] source) {
        int rows = source.length;
        int columns = source[0].length;
        int[][] dest = new int[rows][columns];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                dest[r][c] = source[r][c];
            }
        }
        return dest;
    }

//    @Override
//    public int getHeuristic(State goal) {
////        return tilesOutOfPlace(this, goal);
//        return sumManhattan(this, goal);
//    }
//
////    public static int sumManhattan(State current, State goal) {
////        int sum = 0;
////        PuzzleState goalPuzzle = (PuzzleState) goal;
////        PuzzleState currPuzzle = (PuzzleState) current;
////        for (int i = 1; i < ((goalPuzzle.getTiles().length)
////                * (goalPuzzle.getTiles().length)); i++) {
////            System.out.println(i);
////            sum += Math.abs(currPuzzle.getLocation(i).getRow()
////                    - goalPuzzle.getLocation(i).getRow());
////            sum += Math.abs(currPuzzle.getLocation(i).getColumn()
////                    - goalPuzzle.getLocation(i).getColumn());
////        }
////        System.out.println("sum : " + sum);
////        return sum;
////    }

}
