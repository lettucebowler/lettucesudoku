package com.lettucebowler.lettucesudoku;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
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

import com.lettucebowler.lettucesudoku.domains.sudoku.Sudoku;
import com.lettucebowler.lettucesudoku.domains.sudoku.SudokuProblem;
import com.lettucebowler.lettucesudoku.domains.sudoku.SudokuState;
import com.lettucebowler.lettucesudoku.framework.problem.SolvingAssistant;

import java.util.ArrayList;
import java.util.Locale;

public class GameActivity extends AppCompatActivity {

    private SudokuProblem problem;
    private SolvingAssistant solving_assistant;
    private SquareGridLayout sudoku_view;
    private Button hint_button;
    private int board_size;
    private int block_size;
    private int hint_offset;
    private int order;
    private int selected_row;
    private int selected_col;
    private int color_correct_bg_light;
    private int color_correct_bg_dark;
    private int board_bg;
    private int incorrect_bg_dark;
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
    private ArrayList<int[]> hints_given;
    private SquareTextView[][] button_grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        read_bundle(getIntent());
        order = 3;
        initialize_members();
        configure_touchables();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        read_bundle(getIntent());
        highlight_on_click();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0) {
            System.out.println("onActivityResult()");
            read_bundle(data);
            System.out.println("hint offset: " + hint_offset);
            System.out.println("do_peer_cells: " + do_peer_cells);
            System.out.println("do_peer_digits: " + do_peer_digits);
            System.out.println("do_legality: " + do_legality);
        }
    }

    private void read_bundle(Intent intent) {
//        Intent intent = getIntent();
        System.out.println("read_bundle()");
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            if(intent.hasExtra("order")) {
                int bundle_order = bundle.getInt("order");
                order = (bundle_order == 2 || bundle_order == 3) ? bundle_order : 3;
            }
            if(intent.hasExtra("hint_offset")) {
                int hints = bundle.getInt("hint_offset");
                hint_offset = 0 <= hints && hints <= 20 ? hints : 10;
            }
            if(intent.hasExtra("do_peer_cells")) {
                do_peer_cells = bundle.getBoolean("do_peer_cells");
            }
            if(intent.hasExtra("do_peer_digits")) {
                do_peer_digits = bundle.getBoolean("do_peer_digits");
            }
            if(intent.hasExtra("do_legality")) {
                do_legality = bundle.getBoolean("do_legality");
            }
        }
    }

    private void initialize_members() {
        problem = new SudokuProblem(order);
        solving_assistant = new SolvingAssistant(problem);
        sudoku_view = findViewById(R.id.board);
        board_size = ((SudokuState)problem.getCurrentState()).getTiles().length;
        block_size = (int)Math.sqrt(board_size);
        hints_given = new ArrayList<>();
        cell_has_been_selected = false;
        button_grid = new SquareTextView[board_size][board_size];
        initial_board = ((SudokuState) problem.getInitialState()).getTiles();
        current_board = get_current_board();
        final_board = ((SudokuState) problem.getFinalState()).getTiles();

        // highlight colors
        color_correct_bg_light = getColor(R.color.colorCorrectBGLight);
        color_correct_bg_dark = getColor(R.color.colorCorrectBGDark);
        board_bg            = getColor(R.color.colorBoardBG);
        incorrect_bg_dark   = getColor(R.color.colorIncorrectBGDark);
        success_bg_dark     = getColor(R.color.colorSuccessBGDark);


        // text colors
        color_default_text = getColor(R.color.colorDefaultText);
        color_incorrect_text = getColor(R.color.colorIncorrectText);
        color_correct_text = getColor(R.color.colorCorrectText);
        color_hint_text = getColor(R.color.colorHintText);
    }

    private int[][] get_initial_board() {
        return ((SudokuState) problem.getInitialState()).getTiles();
    }

    private int[][] get_current_board() {
        return ((SudokuState) problem.getCurrentState()).getTiles();
    }

    private int[][] get_final_board() {
        return ((SudokuState) problem.getFinalState()).getTiles();
    }

    private void configure_touchables() {
        initialize_board();
        configure_game_buttons();
        create_move_buttons();
    }

    private void configure_menu_button() {
        ImageButton button = findViewById(R.id.button_menu);
        button.setOnClickListener(e -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("hint_offset", hint_offset);
            intent.putExtra("do_peer_cells", do_peer_cells);
            intent.putExtra("do_peer_digits", do_peer_digits);
            intent.putExtra("do_legality", do_legality);
            intent.putExtra("game_in_progress", true);
            startActivityForResult(intent, 0);
        });
    }


    private void configure_game_buttons() {
        configure_reset_button();
        configure_hint_button();
        configure_menu_button();
    }

    private void create_move_buttons() {
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
            SquareButton move_button = create_move_button(this, i % (board_size + 1));
            ((TableRow) move_buttons.getChildAt((i - 1) / row_length)).addView(move_button);
        }
    }

    private SquareButton create_move_button(Context context, int i)  {
        SquareButton move_button = new SquareButton(this);
        set_move_button_action(move_button, i);
        style_move_button(move_button, i);
        return move_button;
    }

    private void set_move_button_action(SquareButton move_button, int i) {
        move_button.setOnClickListener(e -> {
            if (cell_has_been_selected) {
                if (initial_board[selected_row][selected_col] == 0) {
                    do_move(i, selected_row, selected_col);
                }
            }
        });
    }

    private void style_move_button(SquareButton move_button, int i) {
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

    private void configure_reset_button() {
        Button reset_button = findViewById(R.id.button_reset);
        reset_button.setOnClickListener(View -> {
            problem = new SudokuProblem(order);
            reset_button.setEnabled(false);
            reset_board();
            reset_button.setEnabled(true);
        });
    }

    private void configure_hint_button() {
        hint_button = findViewById(R.id.button_hint);
        hint_button.setTag(5);
        String hint_text = String.format(Locale.US, "hint(%d)", (int) hint_button.getTag());
        hint_button.setText(hint_text);
        hint_button.setEnabled(true);
        hint_button.setOnClickListener(e -> {
            give_hint();
        });
    }

    private void give_hint() {
        // Only run if problem is not yet solved
        if (!problem.success()) {
            current_board = get_current_board();
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
            hints_given.add(hint);
            hint_button.setTag(((int) hint_button.getTag()) - 1);
            if ((int) hint_button.getTag() == 0) {
                hint_button.setEnabled(false);
            }
            final String finalHint_text = String.format(Locale.US, "hint(%d)", (int)hint_button.getTag());
            hint_button.setText(finalHint_text);
            update_cell(i, j);
        }
        else {
            Toast.makeText(this, "Puzzle already solved!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initialize_board() {
        sudoku_view.setColumnCount(board_size);
        for(int i = 0; i < board_size * board_size; i++) {
            SquareTextView gridButton = make_board_button(this, i / board_size, i % board_size);
            sudoku_view.addView(gridButton);
            button_grid[i / board_size][i % board_size] = gridButton;
        }
        white_out_board();
        populate_board();
    }

    private SquareTextView make_board_button(Context context, int i, int j) {
        SquareTextView board_button = new SquareTextView(context);
        style_board_button(board_button, i, j);
//        board_button.setTag(new TableData(i, j));
        set_board_button_actions(board_button, i, j);
        return board_button;
    }

    private void style_board_button(SquareTextView board_button, int i, int j) {
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
        if(cell_has_been_selected) {
            white_out_board();
            if(do_peer_cells) {
                highlight_num_row_col_block();
            }
            if(do_peer_digits) {
                highlight_all_of_num();
            }
            button_grid[selected_row][selected_col].setBackgroundColor(color_correct_bg_dark);
        }
    }

    private void highlight_all_of_num() {
        current_board = get_current_board();
        int cell_num = current_board[selected_row][selected_col];
        for(int i = 0; i < board_size; i++) {
            for(int j = 0; j < board_size; j++) {
                int cur_cell = current_board[i][j];
                if(cur_cell == cell_num && cell_num != 0) {
                    if (final_board[i][j] == cell_num) {
                        button_grid[i][j].setBackgroundColor(color_correct_bg_dark);
                    }
                    else {
                        button_grid[i][j].setBackgroundColor(incorrect_bg_dark);
                    }
                }
            }
        }
//        button_grid[selected_row][selected_col].setBackgroundColor(board_bg);
    }

    private void highlight_num_row_col_block() {
        current_board = get_current_board();
        // Highlight cells in came row or column
        for(int i = 0; i < board_size; i++) {
            button_grid[selected_row][i].setBackgroundColor(color_correct_bg_light);
            button_grid[i][selected_col].setBackgroundColor(color_correct_bg_light);
        }

        // Highlight cells in same block
        int start_row = selected_row / block_size * block_size;
        int start_col = selected_col / block_size * block_size;
        for(int i = 0; i < block_size; i++) {
            for(int j = 0; j < block_size; j++) {
                button_grid[start_row + i][start_col + j].setBackgroundColor(color_correct_bg_light);
            }
        }
    }

    private void update_cell(int row, int col) {
        current_board = get_current_board();
        int to_place = current_board[row][col];
        String buttonText = (to_place == 0) ? "" : String.format(Locale.US, "%d", to_place);
        SquareTextView grid_cell = button_grid[row][col];
        grid_cell.setText(String.format(buttonText));
        set_board_text_color(grid_cell, row, col);
        highlight_on_click();

        if (problem.success()) {
            highlight_on_success();
            hint_button.setEnabled(false);
        }
    }

    private void set_board_text_color(SquareTextView view, int row, int col) {
        if(problem.is_correct(row, col)) {
            view.setTextColor(color_correct_text);
        }
        else {
            view.setTextColor(color_incorrect_text);
        }
        if(problem.is_initial_hint(row, col)) {
            view.setTextColor(color_default_text);
        }
        if(given_as_hint(row, col, hints_given)) {
            view.setTextColor(color_hint_text);
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
        for(int i = 0; i < board_size; i++) {
            for(int j = 0; j < board_size; j++) {
                int to_place = initial_board[i][j];
                String buttonText = (to_place == 0) ? "" : String.format(Locale.US, "%d", to_place);
                button_grid[i][j].setText(buttonText);
                button_grid[i][j].setTextColor(color_default_text);
            }
        }
    }

    private boolean given_as_hint(int row, int col, ArrayList<int[]> hints_given) {
        for (int[] hint : hints_given) {
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
        solving_assistant = new SolvingAssistant(problem);
        initial_board = get_initial_board();
        final_board = get_final_board();
        hints_given.clear();
        white_out_board();
        configure_hint_button();
        populate_board();
    }

    private void do_move(int num, int row, int col) {
        if(!problem.success()) {
            String move = "Place " + num + " at " + row + " " + col;
            solving_assistant.tryMove(move);
            if (solving_assistant.isMoveLegal()) {
                update_cell(row, col);
            }
            else {
                Toast.makeText(this, "Illegal move.", Toast.LENGTH_SHORT).show();
            }
            if (solving_assistant.isProblemSolved()) {
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

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
            int width = (int)((double) MeasureSpec.getSize(widthMeasureSpec) / 9.5);
            int height = (int)((double) MeasureSpec.getSize(widthMeasureSpec) / 9.5);
            int size = Math.min(width, height);
            setMeasuredDimension(size, size); // make it square
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

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            int size = Math.min(width, height);
            setMeasuredDimension(size, size); // make it square
        }
    }

    private static class SquareGridLayout extends GridLayout {

        public SquareGridLayout(Context context) {
            super(context);
        }

        public SquareGridLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public SquareGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            int size = Math.min(width, height);
            setMeasuredDimension(size, size); // make it square
        }
    }
}