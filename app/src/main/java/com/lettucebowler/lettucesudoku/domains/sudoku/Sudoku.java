package com.lettucebowler.lettucesudoku.domains.sudoku;

import java.util.ArrayList;
import java.util.Random;

public class Sudoku {
    private int board_size;
    private int cell_size;
    private int hint_offset;
    private int[][] board_filled;
    private int[][] board_temp;
    private int[][] board_emptied;

    public Sudoku(int cell_size) {
        this.cell_size = cell_size;
        this.board_size = cell_size * cell_size;
        this.board_size = cell_size * cell_size;
        this.hint_offset = 0;
        this.board_filled = new int[board_size][board_size];
        this.board_temp = new int[board_size][board_size];
        this.generateFilled();
        this.scrambleBoard();
        this.preparePuzzle();
    }

    public Sudoku(int cell_size, int hint_offset) {
        this.cell_size = cell_size;
        this.board_size = cell_size * cell_size;
        this.hint_offset = hint_offset;
        this.board_filled = new int[board_size][board_size];
        this.board_temp = new int[board_size][board_size];
        this.generateFilled();
        this.scrambleBoard();
        this.preparePuzzle();
    }

    public int[][] getBoardFilled() {
        return this.board_filled;
    }

    public int[][] getBoardEmptied() {
        return this.board_emptied;
    }

    public int getBoardSize() {
        return this.board_size;
    }

    public int getCellSize() { return this.cell_size; }

    public void generateFilled() {
        this.board_size = this.cell_size * this.cell_size;
        this.board_filled = new int[this.board_size][this.board_size];
        int k ;
        int n = 1;

        for(int i = 0; i < this.board_size; i++) {
            k = n;

            for (int j = 0; j < this.board_size; j++) {
                if (k > this.board_size) {
                    k = 1;
                }

                this.board_filled[i][j] = k;
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
        int max_iterations = 10;
        this.scrambleRows(getRandom(max_iterations));
        this.scrambleCols(getRandom(max_iterations));
    }

    private void scrambleRows(int iterations) {
        for (int i = 0; i < iterations; i++) {
            for(int j = 0; j < this.cell_size; j++) {
                int row1 = getRandom(this.cell_size);
                int row2;
                do {
                    row2 = getRandom(this.cell_size);
                } while (row1 == row2);
                int base = j * this.cell_size;
                swapRows(base + row1, base + row2);
            }
        }
    }

    private void scrambleCols(int iterations) {
        for (int i = 0; i < iterations; i++) {
            for(int j = 0; j < this.cell_size; j++) {
                int col1 = getRandom(this.cell_size);
                int col2;
                do {
                    col2 = getRandom(this.cell_size);
                } while (col1 == col2);
                int base = j * this.cell_size;
                swapCols(base + col1, base + col2);
            }
        }
    }

    // Swap two rows from a block of cells
    private void swapRows(int row1, int row2) {
        int temp;
        for(int i = 0; i < this.board_size; i++) {
            temp = this.board_filled[row1][i];
            this.board_filled[row1][i] = this.board_filled[row2][i];
            this.board_filled[row2][i] = temp;
        }
    }

    // Swap to columns from a block of cells
    private void swapCols(int col1, int col2) {
        int temp;
        for(int i = 0; i < this.board_size; i++) {
            temp = this.board_filled[i][col1];
            this.board_filled[i][col1] = this.board_filled[i][col2];
            this.board_filled[i][col2] = temp;
        }
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
                // int cell = board[i][j];
                // String to_place = cell != 0 ? String.format("%d", cell) : " ";
                builder.append(spacer);
                spacer = " ";
                // builder.append(to_place);
                builder.append(board[i][j]);
            }
        }
        return builder.toString();
    }

    // Takes a complete sudoku board and removes cells to create a sudoku puzzle
    private void preparePuzzle() {
        this.board_emptied = new int[this.board_size][this.board_size];
        for (int row = 0; row < this.board_size; row++) {
            System.arraycopy(this.board_filled[row], 0, this.board_temp[row], 0, this.board_size);
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
//        int[] num_count = new int[board_size];

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

//        // list of count for each number on board
//        for (int num = 0; num < this.board_size; num++) {
//            num_count[num] = this.board_size;
//        }

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
//                num_count[temp - 1]--;
                num_removed++;
            }
        } while (num_removed < max_remove && valid_positions.size() > 0);
        // System.out.println(String.format(Locale.US, "Prep iterations: %d", iterations));

        this.board_emptied = new int[this.board_size][this.board_size];
        for (int i = 0; i < this.board_size; i++) {
            System.arraycopy(board_temp[i], 0, this.board_emptied[i], 0, this.board_size);
        }
    }

    // Returns number of filled cells in same block as input row and col;
    private int countNumInBlock(int[][] board, int row, int col) {
        int count = 0;
        for(int i = 0; i < this.board_size; i++) {
            for(int j = 0; j < this.board_size; j++) {
                if(i / 3 == row / 3 && j / 3 == col / 3 && board[i][j] != 0) {
                    count++;
                }
            }
        }
        return count;
    }

    private int countNum(int num, int[][] board) {
        int count = 0;
        for (int[] row : board) {
            for (int cell : row) {
                if (cell == num) {
                    count++;
                }
            }
        }
        return count;
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

//    public static boolean solve(int[][] board, int n) {
//        int row = -1;
//        int col = -1;
//        boolean isEmpty = true;
//        for (int i = 0; i < n; i++) {
//            for (int j = 0; j < n; j++) {
//                if (board[i][j] == 0) {
//                    row = i;
//                    col = j;
//                    // we still have some remaining
//                    // missing values in Sudoku
//                    isEmpty = false;
//                    break;
//                }
//            }
//            if (!isEmpty) {
//                break;
//            }
//        }
//
//        // no empty space left
//        if (isEmpty) {
//            return true;
//        }
//
//        // else for each-row backtrack
//        for (int num = 1; num <= n; num++) {
//            if (checkSafety(board, row, col, num)) {
//                board[row][col] = num;
//                if (solve(board, n)) {
//                    // print(board, n);
//                    return true;
//                }
//                else {
//                    // replace it
//                    board[row][col] = 0;
//                }
//            }
//        }
//        return false;
//    }

    public static int getRandom(int max) {
        Random random = new Random();
        // System.out.println(String.format("max: %d", max));
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