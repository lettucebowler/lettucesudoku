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
//       for (int i = 0; i < board_size; i++) {
//           for (int j = 0; j < board_size; j++) {
//               for (int n = 0; n <= board_size; n++) {
//                   final int finalI = i;
//                   final int finalJ = j;
//                   final int finalN = n;
//                   super.addMove("Place " + n + " at " + i + " " + j, e -> this.tryMove(finalN, finalI, finalJ, e));
//               }
//           }
//       }
    }

    private SudokuState tryMove(final int num,final int row, final int col, final State state1) {
        final SudokuState state2 = (SudokuState) state1;
        final int[][] board  = state2.getTiles();

        // Check that input number is valid
        if (num < 0 || num > state2.getTiles().length) {
            return null;
        }

//        // Check row
//        for (int d = 0; d < board.length; d++) {
//            if (board[row][d] == num && num != 0) {
//                return null;
//            }
//        }
//        // Check column
//        for (int r = 0; r < board.length; r++) {
//            if (board[r][col] == num && num != 0) {
//                return null;
//            }
//        }
//        // Check box
//        int order = (int)Math.sqrt(board.length);
//        for (int i = 0; i < board.length; i++) {
//            for (int j = 0; j < board.length; j++) {
//                boolean same_row_block = i / order == row / order;
//                boolean same_col_block = j / order == col / order;
//                if (same_row_block && same_col_block) {
//                    if (board[i][j] == num && num != 0) {
//                        return null;
//                    }
//                }
//            }
//        }

        return new SudokuState(state2, num, row, col);
    }
}
