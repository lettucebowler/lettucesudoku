package com.lettucebowler.lettucesudoku;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import com.google.gson.Gson;
import com.lettucebowler.lettucesudoku.domains.sudoku.Sudoku;
import com.lettucebowler.lettucesudoku.domains.sudoku.SudokuProblem;
import com.lettucebowler.lettucesudoku.domains.sudoku.SudokuState;
import com.lettucebowler.lettucesudoku.framework.problem.SolvingAssistant;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class GameActivity extends AppCompatActivity {

    private SudokuProblem problem;
    private SolvingAssistant solving_assistant;
    private CustomViews.SquareGridLayout sudoku_view;
    private Button hint_button;
    private int board_size;
    private int block_size;
    private int hint_offset;
    private int num_extra_hints;
    private int order;
    private int selected_row;
    private int selected_col;
    private int color_correct_bg_light;
    private int color_correct_bg_dark;
    private int board_bg;
    private int success_bg_dark;
    private int color_default_text;
    private int color_correct_text;
    private int color_incorrect_text;
    private int color_hint_text;
    private int[][] initial_board;
    private int[][] current_board;
    private int[][] final_board;
    private boolean cell_has_been_selected;
    private boolean do_peer_cells;
    private boolean do_peer_digits;
    private boolean do_legality;
    private boolean do_game_save;
    private ArrayList<int[]> hints_given;
    private CustomViews.SquareTextView[][] button_grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        readSharedPrefs();
        order = 3;
        initializeMembers();
        configureTouchables();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(do_game_save) {
            saveGame();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        readSharedPrefs();
        highlightOnClick(color_correct_bg_light, color_correct_bg_dark, board_bg);
    }

    private void readSharedPrefs() {
        boolean DEFAULT_DO_PEER_CELLS = true;
        boolean DEFAULT_DO_PEER_DIGITS = true;
        boolean DEFAULT_DO_LEGALITY = false;
        int DEFAULT_HINT_OFFSET = 10;

        SharedPreferences mPrefs = getSharedPreferences("system", MODE_PRIVATE);
        Gson gson = new Gson();

        String do_peer_cells_string = mPrefs.getString("do_peer_cells", "");
        assert do_peer_cells_string != null;
        do_peer_cells = do_peer_cells_string.equals("") ? DEFAULT_DO_PEER_CELLS : gson.fromJson(do_peer_cells_string, boolean.class);

        String do_peer_digits_string = mPrefs.getString("do_peer_digits", "");
        assert do_peer_digits_string != null;
        do_peer_digits = do_peer_digits_string.equals("") ? DEFAULT_DO_PEER_DIGITS : gson.fromJson(do_peer_digits_string, boolean.class);


        String do_legality_string = mPrefs.getString("do_legality", "");
        assert do_legality_string != null;
        do_legality = do_legality_string.equals("") ? DEFAULT_DO_LEGALITY : gson.fromJson(do_legality_string, boolean.class);

        String hint_offset_string = mPrefs.getString("hint_offset", "");
        assert hint_offset_string != null;
        hint_offset = hint_offset_string.equals("") ? DEFAULT_HINT_OFFSET : gson.fromJson(hint_offset_string, int.class);
    }

    private void initializeMembers() {
        sudoku_view = findViewById(R.id.board);
        board_size = order * order;
        block_size = order;
        num_extra_hints = 5;
        cell_has_been_selected = false;
        button_grid = new CustomViews.SquareTextView[board_size][board_size];
        hints_given = new ArrayList<>();

        // highlight colors
        color_correct_bg_light = getColor(R.color.colorCorrectBGLight);
        color_correct_bg_dark = getColor(R.color.colorCorrectBGDark);
        success_bg_dark  = getColor(R.color.colorSuccessBGDark);
        board_bg = getColor(R.color.colorBoardBG);


        // text colors
        color_default_text = getColor(R.color.colorDefaultText);
        color_incorrect_text = getColor(R.color.colorIncorrectText);
        color_correct_text = getColor(R.color.colorCorrectText);
        color_hint_text = getColor(R.color.colorHintText);
    }

    private int[][] getInitialBoard() {
        return ((SudokuState) problem.getInitialState()).getTiles();
    }

    private int[][] getCurrentBoard() {
        return ((SudokuState) problem.getCurrentState()).getTiles();
    }

    private int[][] getFinalBoard() {
        return ((SudokuState) problem.getFinalState()).getTiles();
    }

    private void configureTouchables() {
        initializeBoard();
        configureGameButtons();
        createMoveButtons();
        configureSolveButton();
    }

    private void configureSolveButton() {
        Button button = findViewById(R.id.button_solve);
        button.setOnClickListener(e -> solveGame());
    }

    private void configureMenuButton() {
        ImageButton button = findViewById(R.id.button_menu);
        button.setOnClickListener(e -> openMenu());
    }

    private void openMenu() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("from_game", true);
        startActivity(intent);
    }


    private void configureGameButtons() {
        configureResetButton();
        configureHintButton();
        configureMenuButton();
    }

    private void createMoveButtons() {
        TableLayout move_buttons = findViewById(R.id.move_buttons);
        int row_length = 5;
        int num_rows = (int) Math.ceil((double)(board_size + 1) / (double)row_length);

        // Add empty rows to the table
        for (int i = 0; i <  num_rows; i++) {
            TableRow move_row = new TableRow(this);
            TableLayout.LayoutParams l = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT);
            l.weight = 1;
            move_row.setLayoutParams(l);
            move_buttons.addView(move_row, 0);
        }

        // Create buttons and add them to the correct row in the layout
        for(int i = 1; i <= board_size + 1; i++) {
            CustomViews.SquareButton move_button = CreateMoveButton(this, i % (board_size + 1));
            ((TableRow) move_buttons.getChildAt((i - 1) / row_length)).addView(move_button);
        }
    }

    @org.jetbrains.annotations.NotNull
    private CustomViews.SquareButton CreateMoveButton(Context context, int i)  {
        CustomViews.SquareButton move_button = new CustomViews.SquareButton(context);
        setMoveButtonAction(move_button, i);
        styleMoveButton(move_button, i);
        return move_button;
    }

    private void setMoveButtonAction(@NotNull CustomViews.SquareButton move_button, int i) {
        move_button.setOnClickListener(e -> {
            if (cell_has_been_selected) {
                if (initial_board[selected_row][selected_col] == 0 && !givenAsHint(selected_row, selected_col)) {
                    doMove(i, selected_row, selected_col);
                }
            }
        });
    }

    private void styleMoveButton(@NotNull CustomViews.SquareButton move_button, int i) {
        TableRow.LayoutParams p = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        p.weight = 1;
        move_button.setLayoutParams(p);
        move_button.setGravity(Gravity.CENTER);
        move_button.setTextSize(28);
        move_button.setTextColor(color_default_text);
        move_button.setIncludeFontPadding(false);
        TypedValue outValue = new TypedValue();
        this.getTheme().resolveAttribute(R.attr.selectableItemBackground, outValue, true);
        move_button.setBackgroundResource(outValue.resourceId);
        String buttonText = (i == 0) ? "X" : String.format(Locale.US, "%d", i);
        move_button.setText(buttonText);
    }

    private void configureResetButton() {
        Button reset_button = findViewById(R.id.button_reset);
        reset_button.setOnClickListener(View -> {
            reset_button.setEnabled(false);
            resetBoard(true);
            reset_button.setEnabled(true);
        });
    }

    private void configureHintButton() {
        hint_button = findViewById(R.id.button_hint);
        hint_button.setTag(num_extra_hints);
        String hint_text = String.format(Locale.US, "hint(%d)", num_extra_hints);
        hint_button.setText(hint_text);
        hint_button.setEnabled(true);
        hint_button.setOnClickListener(e -> giveHint());
    }

    private void giveHint() {
        // Only run if problem is not yet solved
        if (!problem.success()) {
            current_board = getCurrentBoard();
            int i;
            int j;
            int move_num;
            do {
                i = Sudoku.getRandom(final_board.length);
                j = Sudoku.getRandom(final_board.length);
                move_num = final_board[i][j];
            } while (current_board[i][j] != 0);
            doMove(move_num, i, j);
            int[] hint = {i, j};
            hints_given.add(hint);
            hint_button.setTag(((int) hint_button.getTag()) - 1);
            if ((int) hint_button.getTag() == 0) {
                hint_button.setEnabled(false);
            }
            final String finalHint_text = String.format(Locale.US, "hint(%d)", (int) hint_button.getTag());
            hint_button.setText(finalHint_text);
            updateCell(i, j);
        }
        else {
            Toast.makeText(this, "Puzzle already solved!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeBoard() {
        sudoku_view.setColumnCount(board_size);
        for(int i = 0; i < board_size * board_size; i++) {
            CustomViews.SquareTextView gridButton = makeBoardButton(this, i / board_size, i % board_size);
            sudoku_view.addView(gridButton);
            button_grid[i / board_size][i % board_size] = gridButton;
        }
        resetBoard(false);
    }

    private CustomViews.SquareTextView makeBoardButton(Context context, int i, int j) {
        CustomViews.SquareTextView board_button = new CustomViews.SquareTextView(context);
        styleBoardButton(board_button, i, j);
        setBoardButtonActions(board_button, i, j);
        return board_button;
    }

    private void styleBoardButton(CustomViews.SquareTextView board_button, int i, int j) {
        GridLayout.LayoutParams p = new GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f),      GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f));
        p.setMargins(1, 1, 1, 1);
        if(i == 0) {
            p.setMargins(p.leftMargin, 4, p.rightMargin, p.bottomMargin);
        }
        if(j == 0) {
            p.setMargins(4, p.topMargin, p.rightMargin, p.bottomMargin);
        }
        if(i % block_size == block_size - 1) {
            p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, 4);
        }
        if(j % block_size == block_size - 1) {
            p.setMargins(p.leftMargin, p.topMargin, 4, p.bottomMargin);
        }
        board_button.setLayoutParams(p);
        int text_size = order == 3 ? 34 : 76;
        board_button.setTextSize(text_size);
        board_button.setIncludeFontPadding(false);
        board_button.setGravity(Gravity.CENTER);
    }

    private void setBoardButtonActions(CustomViews.SquareTextView board_button, int i, int j) {
        board_button.setOnClickListener(e -> {
            cell_has_been_selected = true;
            selected_row = i;
            selected_col = j;
            highlightOnClick(color_correct_bg_light, color_correct_bg_dark, board_bg);
        });
    }

    private boolean cellGood(int row, int col) {
        boolean good = true;
        if(getCurrentBoard()[row][col] != 0) {
            if(do_legality) {
                good = problem.isLegal(row, col);
            }
            else {
                good = problem.isCorrect(row, col);
            }
        }
        return good;
    }

    private void highlightOnClick(int light_color, int dark_color, int selected_color) {
        if(cell_has_been_selected) {
            if(!problem.success()) {
                highlightBoard(selected_color);
                button_grid[selected_row][selected_col].setBackgroundColor(color_correct_bg_dark);
                if(do_peer_cells) {
                    highlightPeerCells(light_color, selected_color);
                }
                if(do_peer_digits) {
                    highlightPeerDigits(dark_color, light_color);
                    if(do_peer_cells) {
                        button_grid[selected_row][selected_col].setBackgroundColor(selected_color);
                    }
                }
            }
        }
    }

    private void highlightPeerDigits(int peer_color, int selected_color) {
        current_board = getCurrentBoard();
        int cell_num = current_board[selected_row][selected_col];
        if(cell_num != 0) {
            for(int i = 0; i < board_size; i++) {
                for(int j = 0; j < board_size; j++) {
                    int cur_cell = current_board[i][j];
                    if(cur_cell == cell_num) {
                        button_grid[i][j].setBackgroundColor(peer_color);
                    }
                }
            }
            button_grid[selected_row][selected_col].setBackgroundColor(selected_color);
        }
    }

    private void highlightPeerCells(int peer_color, int selected_color) {
        current_board = getCurrentBoard();
        // Highlight cells in came row or column
        for(int i = 0; i < board_size; i++) {
            button_grid[selected_row][i].setBackgroundColor(peer_color);
            button_grid[i][selected_col].setBackgroundColor(peer_color);
        }

        // Highlight cells in same block
        int start_row = selected_row / block_size * block_size;
        int start_col = selected_col / block_size * block_size;
        for(int i = 0; i < block_size; i++) {
            for(int j = 0; j < block_size; j++) {
                button_grid[start_row + i][start_col + j].setBackgroundColor(peer_color);
            }
        }
        button_grid[selected_row][selected_col].setBackgroundColor(selected_color);
    }

    private void updateBoard() {
        for(int i = 0; i < board_size; i++) {
            for(int j = 0; j < board_size; j++) {
                updateCell(i, j);
            }
        }
    }

    private void updateCell(int row, int col) {
        current_board = getCurrentBoard();
        int to_place = current_board[row][col];
        String buttonText = (to_place == 0) ? "" : String.format(Locale.US, "%d", to_place);
        CustomViews.SquareTextView grid_cell = button_grid[row][col];
        grid_cell.setText(buttonText);
        highlightOnClick(color_correct_bg_light, color_correct_bg_dark, board_bg);

        if(do_legality) {
            recolorDigits();
        }
        else {
            recolorDigit(row, col);
        }
    }

    private void recolorDigits() {
        for(int i = 0; i < board_size; i++) {
            for(int j = 0; j < board_size; j++) {
                recolorDigit(i, j);
            }
        }
    }

    private void recolorDigit(int row, int col) {
        CustomViews.SquareTextView view = button_grid[row][col];
        if(cellGood(row, col)) {
            view.setTextColor(color_correct_text);
        }
        else {
            view.setTextColor(color_incorrect_text);
        }

        if(problem.isInitialHint(row, col)) {
            view.setTextColor(color_default_text);
        }
        if(givenAsHint(row, col)) {
            view.setTextColor(color_hint_text);
        }
    }

    // Highlight all cells on board in success_color
    private void highlightBoard(int color) {
        ArrayList<View> layoutButtons = sudoku_view.getTouchables();
        for (View view : layoutButtons) {
            view.setBackgroundColor(color);
        }
    }

    // Call to reset board for new game
    // Sets text of each cell in board to the corresponding cell in the sudoku board.
    // Resets text color to default.
    private void populateBoard() {
        cell_has_been_selected = false;
        for(int i = 0; i < board_size; i++) {
            for(int j = 0; j < board_size; j++) {
                int to_place = getCurrentBoard()[i][j];
                String buttonText = (to_place == 0) ? "" : String.format(Locale.US, "%d", to_place);
                button_grid[i][j].setText(buttonText);
                button_grid[i][j].setTextColor(color_default_text);
            }
        }
    }

    private boolean givenAsHint(int row, int col) {
        for (int[] hint : hints_given) {
            if (row == hint[0] && col == hint[1]) {
                return true;
            }
        }
        return false;
    }

    // Generate new sudoku board and reset game state
    private void newGame() {
        problem = new SudokuProblem(order, hint_offset);
        solving_assistant = new SolvingAssistant(problem);
        initial_board = getInitialBoard();
        final_board = getFinalBoard();
        hints_given.clear();
    }

    // Read game state from sharedPrefs and apply it to current game state
    private void resumeSavedGame() {
        SharedPreferences  mPrefs = getSharedPreferences("system", MODE_PRIVATE);
        String initial = mPrefs.getString("initial_board", "");
        String current = mPrefs.getString("current_board", "");
        String complete = mPrefs.getString("final_board", "");
        String hints = mPrefs.getString("hints_given", "");
        Gson gson = new Gson();

        initial_board = gson.fromJson(initial, int[][].class);
        current_board = gson.fromJson(current, int[][].class);
        final_board = gson.fromJson(complete, int[][].class);
        if(hints.equals("")) {
            hints_given.clear();
        }
        else {
            int[][] hints_arr = gson.fromJson(hints, int[][].class);
            hints_given = new ArrayList<int[]>(Arrays.asList(hints_arr));
        }

        problem = new SudokuProblem(order, hint_offset, initial_board, current_board, final_board);
        solving_assistant = new SolvingAssistant(problem);
    }

    private void saveGame() {
        SharedPreferences mPrefs = getSharedPreferences("system", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String initial = gson.toJson(getInitialBoard());
        String current = gson.toJson(getCurrentBoard());
        String complete = gson.toJson(getFinalBoard());
        int[][] hints_arr = new int[hints_given.size()][2];
        hints_arr = hints_given.toArray(hints_arr);
        String hints = gson.toJson(hints_arr);
        prefsEditor.putString("initial_board", initial);
        prefsEditor.putString("current_board", current);
        prefsEditor.putString("final_board", complete);
        prefsEditor.putString("hints_given", hints);
        prefsEditor.apply();
    }

    private void deleteGame() {
        SharedPreferences mPrefs = getSharedPreferences("system", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.remove("initial_board");
        prefsEditor.remove("current_board");
        prefsEditor.remove("final_board");
        prefsEditor.remove("hints_given");
        prefsEditor.apply();
    }

    private void resetBoard(boolean button_pressed) {
        SharedPreferences  mPrefs = getSharedPreferences("system", MODE_PRIVATE);
        String current = mPrefs.getString("current_board", "");
        assert current != null;
        if(current.equals("") || button_pressed) {
            newGame();
        }
        else {
            resumeSavedGame();
        }
        do_game_save = true;
        highlightBoard(board_bg);
        configureHintButton();
        populateBoard();
        recolorDigits();
    }

    private void doMove(int num, int row, int col) {
        if(!problem.success()) {
            String move = "Place " + num + " at " + row + " " + col;
            solving_assistant.tryMove(move);
            if (solving_assistant.isMoveLegal()) {
                updateCell(row, col);
            }
            else {
                Toast.makeText(this, "Illegal move.", Toast.LENGTH_SHORT).show();
            }
            if (solving_assistant.isProblemSolved()) {
                Toast.makeText(this, "Puzzle solved!", Toast.LENGTH_SHORT).show();
                highlightBoard(success_bg_dark);
                hint_button.setEnabled(false);
                deleteGame();
                do_game_save = false;
            }
        }
    }

    private void solveGame() {
        for(int i = 0; i < board_size; i++) {
            for(int j = 0; j < board_size; j++) {
                if(getCurrentBoard()[i][j] == 0) {
                    for(int k = 1; k <= board_size; k++) {
                        doMove(k, i, j);
                        if(problem.isCorrect(i, j)) {
                            break;
                        }
                    }
                }
            }
        }
    }
}