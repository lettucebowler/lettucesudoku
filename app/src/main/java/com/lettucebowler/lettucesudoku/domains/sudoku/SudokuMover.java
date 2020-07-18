/*
 * Grant Montgomery
 * CS 2511
 * 12pm lab
 * 5349790
 */
package com.lettucebowler.lettucesudoku.domains.sudoku;

import java.util.function.UnaryOperator;

import com.lettucebowler.lettucesudoku.framework.problem.Mover;
import com.lettucebowler.lettucesudoku.framework.problem.State;

/**
 *
 * @author Grant Montgomery
 * Student ID : 5349790
 * Lab Section 003
 */
public class SudokuMover extends Mover {

    public SudokuMover(int board_size) {
       for (int i = 0; i <= board_size; i++) {
           for (int j = 0; j < board_size; j++) {
               for (int k = 0; k < board_size; k++) {
                   final int finalI = i;
                   final int finalJ = j;
                   final int finalK = k;
                   super.addMove("Place " + i + " at " + j + " " + k, e -> this.tryMove(finalI, finalJ, finalK, e));
               }
           }
       }
    }

    private SudokuState tryMove(final int num,final int row, final int col, final State state1) {
        final SudokuState state2 = (SudokuState) state1;
        final int[][] board  = state2.getTiles();

        // Check that input number is valid
        if (num < 0 || num > state2.getTiles().length) {
            return null;
        }

        return new SudokuState(state2, num, row, col);
    }
}
