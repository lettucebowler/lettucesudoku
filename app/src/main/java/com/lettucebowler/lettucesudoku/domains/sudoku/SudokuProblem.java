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

    public SudokuProblem(int cell_size, int hint_offset) {
        super();
        super.setName("Sudoku");
        super.setIntroduction("Place the numbers 1-9 in each of the three 3x3 grids. "
                + "Each row must contain each number 1-9. "
                + "Each Column must contain each number 1-9"
                + "for each cell in the grid, there can be no other cell with the same "
                + "row or column that contains the same number. "
                + "The game is finished when the grid is full.");
        sudoku = new Sudoku(cell_size, hint_offset);
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

    public boolean is_legal(int row, int col) {
        return !(same_in_row(row, col) || same_in_col(row, col) || same_in_block(row, col));
    }

    private boolean same_in_row(int row, int col) {
        int[][] tiles = ((SudokuState) this.getCurrentState()).getTiles();
        int count = 0;
        for(int i = 0; i < this.get_sudoku().get_board_size(); i++) {
            if(tiles[row][i] == tiles[row][col]) {
                count++;
            }
        }
        return count > 1;
    }

    private boolean same_in_col(int row, int col) {
        int[][] tiles = ((SudokuState) this.getCurrentState()).getTiles();
        int count = 0;
        for(int i = 0; i < this.get_sudoku().get_board_size(); i++) {
            if(tiles[i][col] == tiles[row][col]) {
                count++;
            }
        }
        return count > 1;
    }

    private boolean same_in_block(int row, int col) {
        int[][] tiles = ((SudokuState) this.getCurrentState()).getTiles();
        int count = 0;
        int block_size = this.get_sudoku().get_cell_size();
        int start_row = row / block_size * block_size;
        int start_col = col / block_size * block_size;
        for(int i = 0; i < block_size; i++) {
            for(int j = 0; j < block_size; j++) {
                if(tiles[start_row + i][start_col + j] == tiles[row][col]) {
                    count++;
                }
            }
        }
        return count > 1;
    }

    public boolean is_row_complete(int row) {
        boolean complete = true;
        int[][] current_board = ((SudokuState) this.getCurrentState()).getTiles();
        int[][] final_board = ((SudokuState) this.getFinalState()).getTiles();
        for(int i = 0; i < current_board.length; i++) {
            if (current_board[row][i] != final_board[row][i]) {
                complete = false;
            }
        }
        return complete;
    }

    public boolean is_column_complete(int col) {
        boolean complete = true;
        int[][] current_board = ((SudokuState) this.getCurrentState()).getTiles();
        int[][] final_board = ((SudokuState) this.getFinalState()).getTiles();
        for(int i = 0; i < current_board.length; i++) {
            if (current_board[i][col] != final_board[i][col]) {
                complete = false;
            }
        }
        return complete;
    }

    public boolean is_block_complete(int row, int col) {
        boolean complete = true;
        int[][] current_board = ((SudokuState) this.getCurrentState()).getTiles();
        int[][] final_board = ((SudokuState) this.getFinalState()).getTiles();
        int cell_size = this.sudoku.get_cell_size();
        for(int i = 0; i < current_board.length; i++) {
            for(int j = 0; j < current_board.length; j++) {
                if(i / cell_size == row / cell_size && j / cell_size == col / cell_size) {
                    if(current_board[i][j] != final_board[i][j]) {
                        complete = false;
                    }
                }
            }
        }
        return complete;
    }

    @Override
    public boolean success() {
        return getCurrentState().equals(getFinalState());
    }
}
