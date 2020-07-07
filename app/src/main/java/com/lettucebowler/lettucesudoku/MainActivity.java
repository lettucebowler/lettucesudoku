package com.lettucebowler.lettucesudoku;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import com.lettucebowler.lettucesudoku.domains.sudoku.Sudoku;
import com.lettucebowler.lettucesudoku.domains.sudoku.SudokuProblem;
import com.lettucebowler.lettucesudoku.domains.sudoku.SudokuState;
import com.lettucebowler.lettucesudoku.framework.problem.SolvingAssistant;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private SudokuProblem problem;
    private SolvingAssistant assistant;
    private GridLayout sudoku_view;
    private Button hint_button;
    private int board_size;
    private int block_size;
    private int order;
    private int selected_row;
    private int selected_col;
    private int color_correct_bg_light;
    private int color_correct_bg_dark;
    private int board_bg;
    private int incorrect_bg_light;
    private int incorrect_bg_dark;
    private int success_bg_light;
    private int success_bg_dark;
    private int color_default_text;
    private int color_correct_text;
    private int color_incorrect_text;
    private int color_hint_text;
    private boolean cell_has_been_selected;
    private ArrayList<int[]> hints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = getIntent().getExtras();
        order = 3;
        if(bundle != null) {
            order = bundle.getInt("order");
        }
        initialize_members();
        configure_touchables();
    }

    private void initialize_members() {
        problem = new SudokuProblem(order);
        assistant = new SolvingAssistant(problem);
        sudoku_view = findViewById(R.id.number_grid);
        board_size = ((SudokuState)problem.getCurrentState()).getTiles().length;
        block_size = (int)Math.sqrt(board_size);
        hints = new ArrayList<>();
        cell_has_been_selected = false;

        // highlight colors
        color_correct_bg_light = getColor(R.color.colorCorrectBGLight);
        color_correct_bg_dark = getColor(R.color.colorCorrectBGDark);
        board_bg            = getColor(R.color.colorBoardBG);
        incorrect_bg_light  = getColor(R.color.colorIncorrectBGLight);
        incorrect_bg_dark   = getColor(R.color.colorIncorrectBGDark);
        success_bg_light    = getColor(R.color.colorSuccessBGLight);
        success_bg_dark     = getColor(R.color.colorSuccessBGDark);


        // text colors
        color_default_text = getColor(R.color.colorDefaultText);
        color_incorrect_text = getColor(R.color.colorIncorrectText);
        color_correct_text = getColor(R.color.colorCorrectText);
        color_hint_text = getColor(R.color.colorHintText);
    }

    private void configure_touchables() {
        initialize_board();
        configure_game_buttons();
        create_move_buttons();
    }

    private void configure_game_buttons() {
        configure_reset_button();
        configure_hint_button();
    }

    private void create_move_buttons() {
        TableLayout move_buttons = findViewById(R.id.move_buttons);
        int row_length = 5;
        int num_rows = (board_size + 1) / row_length;
        for (int i = 0; i <  num_rows; i++) {
            TableRow move_row = new TableRow(this);
            TableLayout.LayoutParams l = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT);
            l.weight = 1;
            move_row.setLayoutParams(l);
            move_buttons.addView(move_row);
        }
        for(int i = 1; i <= board_size + 1; i++) {
            SquareButton move_button = create_move_button(this, i % (board_size + 1));
            ((TableRow) move_buttons.getChildAt((i - 1) / row_length)).addView(move_button);
        }
    }

    private SquareButton create_move_button(Context context, int i)  {
        SquareButton move_button = new SquareButton(context);
        set_move_button_action(move_button, i);
        style_move_button(move_button);
        return move_button;
    }

    private void set_move_button_action(SquareButton move_button, int i) {
        move_button.setText(String.format(Locale.US, "%d", i));
        move_button.setOnClickListener(e -> {
            if (cell_has_been_selected) {
                int[][] initial_board = ((SudokuState) problem.getInitialState()).getTiles();
                if (initial_board[selected_row][selected_col] == 0) {
                    do_move(i, selected_row, selected_col);
                }
            }
        });
    }

    private void style_move_button(SquareButton move_button) {
        TableRow.LayoutParams p = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        p.weight = 1;
        move_button.setLayoutParams(p);
        move_button.setGravity(Gravity.CENTER);
        move_button.setTextSize(20);
        move_button.setTextColor(color_default_text);
        move_button.setIncludeFontPadding(false);
    }

    private void configure_reset_button() {
        Button reset_button = (Button) findViewById(R.id.button_reset);
        reset_button.setOnClickListener(View -> {
            problem = new SudokuProblem(order);
            reset_button.setEnabled(false);
            reset_board();
            reset_button.setEnabled(true);
        });
    }

    private void configure_hint_button() {
        hint_button = (Button) findViewById(R.id.button_hint);
        hint_button.setTag(5);
        String hint_text = String.format(Locale.US, "hint(%d)", (int) hint_button.getTag());
        hint_button.setText(hint_text);
        hint_button.setEnabled(true);
        hint_button.setOnClickListener(e -> {
            // Only run if problem is not yet solved
            if (!problem.success()) {
                int[][] final_board = ((SudokuState) problem.getFinalState()).getTiles();
                int[][] current_board = ((SudokuState) problem.getCurrentState()).getTiles();
                int i;
                int j;
                int move_num;
                do {
                    i = Sudoku.get_random(final_board.length);
                    j = Sudoku.get_random(final_board.length);
                    move_num = final_board[i][j];
                } while (current_board[i][j] != 0);
                do_move(move_num, i, j);
                int[] hint = {i, j};
                hints.add(hint);
                hint_button.setTag(((int) hint_button.getTag()) - 1);
                if ((int) hint_button.getTag() == 0) {
                    hint_button.setEnabled(false);
                }
                final String finalHint_text = String.format(Locale.US, "hint(%d)", (int)hint_button.getTag());
                hint_button.setText(finalHint_text);
                update_board();
            }
            else {
                Toast.makeText(this, "Puzzle already solved!", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void initialize_board() {
        sudoku_view.setColumnCount(board_size);
        for(int i = 0; i < board_size * board_size; i++) {
            SquareTextView gridButton = make_board_button(this, i / board_size, i % board_size);
            sudoku_view.addView(gridButton);
            int width = sudoku_view.getWidth() / board_size;
            gridButton.setWidth(width);
            gridButton.setHeight(width);

        }
        white_out_board();
        update_board();
    }

    private SquareTextView make_board_button(Context context, int i, int j) {
        SquareTextView board_button = new SquareTextView(context);
        set_board_button_margins(board_button, i, j);
        board_button.setTag(new TableData(i, j));
        set_board_button_actions(board_button, i, j);
        board_button.setTextSize(32);
        board_button.setIncludeFontPadding(false);
        board_button.setGravity(Gravity.CENTER);
        return board_button;
    }

    private void set_board_button_margins(SquareTextView board_button, int i, int j) {
        GridLayout.LayoutParams p = new GridLayout.LayoutParams();
        p.setGravity(Gravity.CENTER_VERTICAL);
        int end = board_size - 1;
        p.setMargins(1, 1, 1, 1);
        if (i % block_size == block_size - 1 && i != end) {
            p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, 4);
        }
        if (j % block_size == block_size - 1 && j != end) {
            p.setMargins(p.leftMargin, p.topMargin, 4, p.bottomMargin);
        }
        board_button.setLayoutParams(p);
    }

    private void set_board_button_actions(SquareTextView board_button, int i, int j) {
        board_button.setOnClickListener(e -> {
            if(!problem.success()) {
                cell_has_been_selected = true;
                selected_row = i;
                selected_col = j;
                highlight_on_click();
            }
        });
    }

    private void highlight_on_click() {
        white_out_board();
        highlight_num_row_col_block();
        highlight_num_row_col_block();
        highlight_all_of_num();

    }

    private void highlight_all_of_num() {
        ArrayList<View> layoutButtons = sudoku_view.getTouchables();
        int[][] current_board = ((SudokuState) problem.getCurrentState()).getTiles();
        int[][] final_board = ((SudokuState) problem.getFinalState()).getTiles();
        int cell_num = current_board[selected_row][selected_col];
        for (View view : layoutButtons) {
            TableData position = (TableData) view.getTag();
            int cur_cell = current_board[position.RowIndex][position.ColumnIndex];
            if ( cur_cell == cell_num && cur_cell != 0) {
                if (final_board[position.RowIndex][position.ColumnIndex] == cur_cell) {
                    view.setBackgroundColor(color_correct_bg_dark);
                }
                else {
                    view.setBackgroundColor(incorrect_bg_dark);
                }
            }
            // undo highlight of selected cell
            if (position.RowIndex == selected_row && position.ColumnIndex == selected_col) {
                view.setBackgroundColor(board_bg);
            }
        }
    }

    private void highlight_num_row_col_block() {
        ArrayList<View> layoutButtons = sudoku_view.getTouchables();
        for (View view : layoutButtons) {
            TableData position = (TableData) view.getTag();
            int pos_row = position.RowIndex;
            int pos_col = position.ColumnIndex;
            boolean same_row_block = pos_row / block_size == selected_row / block_size;
            boolean same_col_block = pos_col / block_size == selected_col / block_size;
            boolean same_row = pos_row == selected_row;
            boolean same_col = pos_col == selected_col;

            // highlight if same row or col
            if (same_row || same_col) {
                view.setBackgroundColor(color_correct_bg_light);
            }
            // highlight if same block
            else if (same_col_block && same_row_block) {
                view.setBackgroundColor(color_correct_bg_light);
            }
            // undo highlight of selected cell
            if (same_row && same_col) {
                view.setBackgroundColor(board_bg);
            }
        }
    }

    private void update_board() {
        int[][] current_tiles = ((SudokuState) problem.getCurrentState()).getTiles();

        if (cell_has_been_selected) {
            highlight_on_click();
        }
        ArrayList<View> layoutButtons = sudoku_view.getTouchables();
        for (View view : layoutButtons) {
            TableData pos = (TableData) view.getTag();
            int to_place = current_tiles[pos.RowIndex][pos.ColumnIndex];
            String buttonText = (to_place == 0) ? "" : String.format(Locale.US, "%d", to_place);
            ((SquareTextView) view).setText(buttonText);
            int row = ((TableData) view.getTag()).RowIndex;
            int col = ((TableData) view.getTag()).ColumnIndex;

            if(problem.is_correct(row, col)) {
                ((SquareTextView) view).setTextColor(color_correct_text);
            }
            else {
                ((SquareTextView) view).setTextColor(color_incorrect_text);
            }
            if(problem.is_initial_hint(row, col)) {
                ((SquareTextView) view).setTextColor(color_default_text);
            }
            if(given_as_hint(row, col, hints)) {
                ((SquareTextView) view).setTextColor(color_hint_text);
            }
        }
        if (problem.success()) {
            highlight_on_success();
            hint_button.setEnabled(false);
        }
    }

    private void highlight_on_success() {
        ArrayList<View> layoutButtons = sudoku_view.getTouchables();
        for (View view : layoutButtons) {
            view.setBackgroundColor(success_bg_dark);
        }
    }

    // Call to reset board for new game
    private void populate_board() {
        cell_has_been_selected = false;
        int[][] initial_tiles = ((SudokuState) problem.getInitialState()).getTiles();
        ArrayList<View> layoutButtons = sudoku_view.getTouchables();
        for (View view : layoutButtons) {
            TableData pos = (TableData) view.getTag();
            int to_place = initial_tiles[pos.RowIndex][pos.ColumnIndex];
            String buttonText = (to_place == 0) ? "" : String.format(Locale.US, "%d", to_place);
            ((SquareTextView) view).setText(buttonText);
            ((SquareTextView) view).setTextColor(color_default_text);
            view.setEnabled(true);
        }
    }

    private boolean given_as_hint(int row, int col, ArrayList<int[]> hints) {
        for (int[] hint : hints) {
            if (row == hint[0] && col == hint[1]) {
                return true;
            }
        }
        return false;
    }

    private void white_out_board() {
        ArrayList<View> cells = sudoku_view.getTouchables();
        for (View cell : cells) {
            cell.setBackgroundColor(board_bg);
        }
    }

    private void reset_board() {
        problem = new SudokuProblem(order);
        assistant = new SolvingAssistant(problem);
        hints.clear();
        white_out_board();
        configure_hint_button();
        populate_board();
    }

    private void do_move(int num, int row, int col) {
        if(!problem.success()) {
            String move = "Place " + num + " at " + row + " " + col;
            assistant.tryMove(move);
            if (assistant.isMoveLegal()) {
                update_board();
            } else {
                Toast.makeText(this, "Illegal move.", Toast.LENGTH_SHORT).show();
            }
            if (assistant.isProblemSolved()) {
                Toast.makeText(this, "Puzzle solved!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class SquareTextView extends androidx.appcompat.widget.AppCompatTextView{
        public SquareTextView(Context context) {
            super(context);
        }

        public SquareTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public SquareTextView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }
////
//        public SquareTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//            super(context, attrs, defStyleAttr, defStyleRes);
//        }

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
            int width = (int)((double) MeasureSpec.getSize(widthMeasureSpec) / 9.5);
            int height = (int)((double) MeasureSpec.getSize(widthMeasureSpec) / 9.5);
            int size = Math.min(width, height);
            setMeasuredDimension(size, size); // make it square
//            setMeasuredDimension(150, 150);
        }
    }

    public static class SquareButton extends androidx.appcompat.widget.AppCompatButton {
        public SquareButton(Context context) {
            super(context);
        }

        public SquareButton(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public SquareButton(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }
////
//        public SquareButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//            super(context, attrs, defStyleAttr, defStyleRes);
//        }

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            int size = Math.min(width, height);
            setMeasuredDimension(size, size); // make it square

        }
    }

    public static class TableData{
        public final int RowIndex;
        public final int ColumnIndex;

        public TableData(int rowIndex, int columnIndex) {
            RowIndex = rowIndex;
            ColumnIndex = columnIndex;
        }
    }
}