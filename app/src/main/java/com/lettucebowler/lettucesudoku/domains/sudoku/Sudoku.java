package com.lettucebowler.lettucesudoku.domains.sudoku;

import java.util.ArrayList;
import java.util.Random;

public class Sudoku {
    private int board_size;
    private int cell_size;
    private int hint_offset;
    private int[][] final_board;
    private int[][] board_temp;
    private int[][] initial_board;

    public Sudoku(int cell_size) {
        this.cell_size = cell_size;
        this.board_size = cell_size * cell_size;
        this.board_size = cell_size * cell_size;
        this.hint_offset = 0;
        this.final_board = new int[board_size][board_size];
        this.board_temp = new int[board_size][board_size];
        this.generateFilled();
        this.scrambleBoard();
        this.preparePuzzle();
    }

    public Sudoku(int cell_size, int hint_offset) {
        this.cell_size = cell_size;
        this.board_size = cell_size * cell_size;
        this.hint_offset = hint_offset;
        this.final_board = new int[board_size][board_size];
        this.board_temp = new int[board_size][board_size];
        this.generateFilled();
        this.scrambleBoard();
        this.preparePuzzle();
    }

    public Sudoku(int cell_size, int hint_offset, int[][] initial_board, int[][] final_board) {
        this.cell_size = cell_size;
        this.board_size = cell_size * cell_size;
        this.hint_offset = hint_offset;
        this.final_board = final_board;
        this.initial_board = initial_board;
    }

    public int[][] getBoardFilled() {
        return this.final_board;
    }

    public int[][] getBoardEmptied() {
        return this.initial_board;
    }

    public int getBoardSize() {
        return this.board_size;
    }

    public int getCellSize() { return this.cell_size; }

    public void generateFilled() {
        this.board_size = this.cell_size * this.cell_size;
        this.final_board = new int[this.board_size][this.board_size];
        int k ;
        int n = 1;

        for(int i = 0; i < this.board_size; i++) {
            k = n;

            for (int j = 0; j < this.board_size; j++) {
                if (k > this.board_size) {
                    k = 1;
                }

                this.final_board[i][j] = k;
                k++;
            }

            n = k + this.cell_size;
            if (k == this.board_size + 1) {
                n = 1 + cell_size;
            }

            if ( n > this.board_size) {
                n = (n % this.board_size) + 1;
            }
        }
    }

    private void scrambleBoard() {
        int max_iterations = 15;
        scrambleRows(getRandom(max_iterations));
        scrambleCols(getRandom(max_iterations));
        randomizeDigits();
    }

    private void scrambleRows(int iterations) {
        for(int i = 0; i < iterations; i++) {
            int[] positions = getPositionsToSwap();
            swapRows(positions[0], positions[1]);
        }
    }

    private void scrambleCols(int iterations) {
        for(int i = 0; i < iterations; i++) {
            int[] positions = getPositionsToSwap();
            swapCols(positions[0], positions[1]);
        }
    }

    private int[] getPositionsToSwap() {
        int pos1 = getRandom(this.board_size);
        int pos2;
        do {
            pos2 = getRandom(this.cell_size);
            pos2 = (pos1 / this.cell_size) + pos2;
        } while(pos1 == pos2);
        return new int[]{pos1, pos2};
    }

    // Swap two rows from a block of cells
    private void swapRows(int row1, int row2) {
        int temp;
        for(int i = 0; i < this.board_size; i++) {
            temp = this.final_board[row1][i];
            this.final_board[row1][i] = this.final_board[row2][i];
            this.final_board[row2][i] = temp;
        }
    }

    // Swap to columns from a block of cells
    private void swapCols(int col1, int col2) {
        int temp;
        for(int i = 0; i < this.board_size; i++) {
            temp = this.final_board[i][col1];
            this.final_board[i][col1] = this.final_board[i][col2];
            this.final_board[i][col2] = temp;
        }
    }

    private void randomizeDigits() {
        int[] digits = new int[this.board_size];
        for(int i = 0; i < this.board_size; i++) {
            digits[i] = i + 1;
        }
        digits = getRandomizedOrder(digits);
        for(int i = 0; i < this.board_size; i++) {
            for(int j = 0; j < this.board_size; j++) {
                int temp = this.final_board[i][j];
                this.final_board[i][j] = digits[temp - 1];
            }
        }
    }

    private int[] getRandomizedOrder(int[] arr) {
        Random r = new Random();
        int[] array = new int[this.board_size];
        System.arraycopy(arr, 0, array, 0, this.board_size);
        for(int i = 0; i < array.length; i++) {
            int random_pos = r.nextInt(array.length);
            int temp = array[i];
            array[i] = array[random_pos];
            array[random_pos] = temp;
        }
        return array;
    }

    // Returns input 2D array of integer as a string
    public String toString(int[][] board) {
        int width = this.board_size;
        String prefix = "";
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < width; i++) {
            builder.append(prefix);
            prefix = "\n";
            String spacer = "";
            for(int j = 0; j < width; j++) {
                builder.append(spacer);
                spacer = " ";
                builder.append(board[i][j]);
            }
        }
        return builder.toString();
    }

    // Takes a complete sudoku board and removes cells to create a sudoku puzzle
    private void preparePuzzle() {
        this.initial_board = new int[this.board_size][this.board_size];
        for (int row = 0; row < this.board_size; row++) {
            System.arraycopy(this.final_board[row], 0, this.board_temp[row], 0, this.board_size);
        }
        int num_removed = 0;
        int max_remove;
        if (this.cell_size == 2) {
            max_remove = 12;
        } else if (this.cell_size == 3) {
            max_remove = 51 - hint_offset;
        } else if (this.cell_size == 4) {
            max_remove = 150;
        } else {
            max_remove = 0;
        }
        int temp;
        int row;
        int col;
        int solutions;

        // create a list of valid spots to remove numbers from
        ArrayList<int[]> valid_positions = new ArrayList<>();
        for(int i = 0; i < this.board_size; i++) {
            for(int j = 0; j < this.board_size; j++) {
                int[] pos = new int[2];
                pos[0] = i;
                pos[1] = j;
                valid_positions.add(pos);
            }
        }

        int rand_pos;
        do {
            rand_pos = getRandom(valid_positions.size());
            int[] tmp_pos = valid_positions.get(rand_pos);
            row = tmp_pos[0];
            col = tmp_pos[1];
            temp = board_temp[row][col];
            board_temp[row][col] = 0;
            valid_positions.remove(rand_pos);
            solutions = countSolutions(0, 0, board_temp, 0);
            if(solutions > 1) {
                board_temp[row][col] = temp;
            }
            else {
                num_removed++;
            }
        } while (num_removed < max_remove && valid_positions.size() > 0);

        this.initial_board = new int[this.board_size][this.board_size];
        for (int i = 0; i < this.board_size; i++) {
            System.arraycopy(board_temp[i], 0, this.initial_board[i], 0, this.board_size);
        }
    }

    private int countSolutions(int i, int j, int[][] board, int count) {
        if (i == board.length) {
            i = 0;
            if (++j == board.length) {
                return 1 + count;
            }
        }
        if (board[i][j] != 0) { // Skip filled cells
            return countSolutions(i+1, j, board, count);
        }
        for (int val = 1; val <= board.length && count < 2; ++val) {
            if (checkSafety(board, i, j, val)) {
                board[i][j] = val;
                count = countSolutions(i + 1, j, board, count);
            }
        }
        board[i][j] = 0;
        return count;
    }

    // https://www.geeksforgeeks.org/sudoku-backtracking-7/
    public static boolean checkSafety(int[][] board, int row, int col, int num) {
        // Check row
        for (int d = 0; d < board.length; d++) {
            if (board[row][d] == num) {
                return false;
            }
        }
        // Check column
        for (int[] ints : board) {
            if (ints[col] == num) {
                return false;
            }
        }
        // Check box
        int order = (int)Math.sqrt(board.length);
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                boolean same_row_block = i / order == row / order;
                boolean same_col_block = j / order == col / order;
                if (same_row_block && same_col_block) {
                    if (board[i][j] == num) {
                        return false;
                    }
                }
            }
        }
        // Passed all tests
        return true;
    }

    public static int getRandom(int max) {
        Random random = new Random();
        return Math.abs(random.nextInt(max));
    }

    public static void main(String[] args) {
        int cell_size = 3;
        Sudoku sudoku = new Sudoku(cell_size);
        System.out.println(sudoku.toString(sudoku.getBoardFilled()));
        System.out.println();
        System.out.println(sudoku.toString(sudoku.getBoardEmptied()));
    }
}