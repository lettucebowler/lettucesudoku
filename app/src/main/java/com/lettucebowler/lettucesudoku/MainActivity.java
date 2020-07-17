package com.lettucebowler.lettucesudoku;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.gridlayout.widget.GridLayout;

import com.lettucebowler.lettucesudoku.domains.sudoku.Sudoku;
import com.lettucebowler.lettucesudoku.domains.sudoku.SudokuProblem;
import com.lettucebowler.lettucesudoku.domains.sudoku.SudokuState;
import com.lettucebowler.lettucesudoku.framework.problem.SolvingAssistant;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private int order;
    private int num_starter_hints;
    private boolean game_has_been_started;
    private boolean highlight_adjacent;
    private boolean highlight_all_num;
    private boolean color_text_on_correctness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToggleButton peer_cells = findViewById(R.id.peer_cell_toggle);
        ToggleButton peer_digits = findViewById(R.id.peer_digit_toggle);
        RadioGroup color_rule = findViewById(R.id.digit_color_radio);
        SeekBar difficulty_slider = findViewById(R.id.seekBar_difficulty);

        Button launch_button = findViewById(R.id.button_launch);
        launch_button.setOnClickListener(e -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("hint_offset", difficulty_slider.getProgress());
            boolean do_peer_cells = peer_cells.getText() == "On";
            intent.putExtra("highlight_peer_cells", do_peer_cells);
            boolean do_peer_digits = peer_digits.getText() == "On";
            intent.putExtra("highlight_peer_digits", do_peer_digits);
            startActivity(intent);
        });
    }
}