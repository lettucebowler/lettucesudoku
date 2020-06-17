package com.lettucebowler.lettucesudoku.domains.sudoku;

import com.lettucebowler.lettucesudoku.framework.problem.Mover;
import com.lettucebowler.lettucesudoku.domains.sudoku.SudokuMover;
import com.lettucebowler.lettucesudoku.framework.problem.Problem;
import com.lettucebowler.lettucesudoku.domains.sudoku.SudokuState;
import com.lettucebowler.lettucesudoku.domains.sudoku.Sudoku;

/**
 *
 * @author Grant Montgomery
 * Student ID : 5349790
 * Lab Section 003
 */
public class SudokuProblem extends Problem {

    Sudoku sudoku;

    public SudokuProblem(int cell_size) {
        super();
        super.setName("Sudoku");
        super.setIntroduction("Place the numbers 1-9 in each of the three 3x3 grids. "
                + "Each row must contain each number 1-9. "
                + "Each Column must contain each number 1-9"
                + "for each cell in the grid, there can be no other cell with the same "
                + "row or column that contains the same number. "
                + "The game is finished when the grid is full.");
        sudoku = new Sudoku(cell_size);
        super.setMover(new SudokuMover(cell_size * cell_size));
        super.setInitialState(new SudokuState(sudoku.get_board_emptied()));
        super.setCurrentState(super.getInitialState());
        super.setFinalState(new SudokuState(sudoku.get_board_filled()));
    }

    public Sudoku get_sudoku() {
        return sudoku;
    }

    public boolean is_initial_hint(int row, int col) {
        return ((SudokuState) this.getInitialState()).getTiles()[row][col] != 0;
    }

    public boolean is_correct(int row, int col) {
        return ((SudokuState) this.getCurrentState()).getTiles()[row][col] == ((SudokuState) this.getFinalState()).getTiles()[row][col];
    }

    public boolean is_row_complete(int row) {
        boolean complete = true;
        int[][] current_board = ((SudokuState) this.getCurrentState()).getTiles();
        int[][] final_board = ((SudokuState) this.getFinalState()).getTiles();
        for(int i = 0; i < this.get_sudoku().get_board_size(); i++) {
            if (current_board[row][i] != final_board[row][i]) {
                complete = false;
            }
        }
        return complete;
    }

    @Override
    public boolean success() {
        return getCurrentState().equals(getFinalState());
    }
}
