package com.lettucebowler.lettucesudoku;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    private ToggleButton peer_cells;
    private ToggleButton peer_digits;
    private RadioGroup color_rule;
    private SeekBar difficulty_slider;
    private boolean do_legality;
    private boolean do_peer_cells;
    private boolean do_peer_digits;
    private boolean game_in_progress;
    private int hint_offset;

    private boolean DEFAULT_DO_PEER_CELLS = true;
    private boolean DEFAULT_DO_PEER_DIGITS = true;
    private boolean DEFAULT_DO_LEGALITY = false;
    private boolean DEFAULT_GAME_IN_PROGRESS = false;
    private int DEFAULT_HINT_OFFSET = 10;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        peer_cells = findViewById(R.id.peer_cell_toggle);
        peer_digits = findViewById(R.id.peer_digit_toggle);
        color_rule = findViewById(R.id.digit_color_radio);
        difficulty_slider = findViewById(R.id.seekBar_difficulty);
        do_peer_cells = DEFAULT_DO_PEER_CELLS;
        do_peer_digits = DEFAULT_DO_PEER_DIGITS;
        do_legality = DEFAULT_DO_LEGALITY;
        hint_offset = DEFAULT_HINT_OFFSET;
    }

    @Override
    protected void onStop() {
        super.onStop();
        writeSharedPrefs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        readSharedPrefs();
        applySharedPrefs();
        configureLaunchButton();
    }

    @Override
    public void onBackPressed() {
        writeSharedPrefs();
        super.onBackPressed();
    }

    private void configureLaunchButton() {
        Button button = findViewById(R.id.button_launch);
        button.setOnClickListener(e -> startGame());
    }

    private void startGame() {
        Intent intent = new Intent(this, GameActivity.class);
        writeSharedPrefs();
        if(getIntent().hasExtra("from_game")) {
            finish();
        }
        else {
            startActivity(intent);
        }
    }

    private void writeSharedPrefs() {
        SharedPreferences mPrefs = getSharedPreferences("system", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        prefsEditor.putString("do_peer_cells", gson.toJson(getDoPeerCells()));
        prefsEditor.putString("do_peer_digits", gson.toJson(getDoPeerDigits()));
        prefsEditor.putString("do_legality", gson.toJson(getDoLegality()));
        prefsEditor.putString("hint_offset", gson.toJson(getHintOffset()));
        prefsEditor.apply();
    }

    private void readSharedPrefs() {
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

        String current_board_string = mPrefs.getString("current_board", "");
        assert current_board_string != null;
        game_in_progress = !current_board_string.equals("");
    }

    private void applySharedPrefs() {
        peer_cells.setChecked(do_peer_cells);
        peer_digits.setChecked(do_peer_digits);
        int index = do_legality ? 0 : 1;
        ((RadioButton) color_rule.getChildAt(index)).setChecked(true);
        difficulty_slider.setProgress(hint_offset);
    }

    private boolean getDoLegality() {
        int id = color_rule.getCheckedRadioButtonId();
        RadioButton button =  findViewById(id);
        return button != null && ((RadioButton)color_rule.getChildAt(0)).isChecked();
    }

    private boolean getDoPeerDigits() {
        return peer_digits.isChecked();
    }

    private boolean getDoPeerCells() {
        return peer_cells.isChecked();
    }

    private int getHintOffset() {
        return difficulty_slider.getProgress();
    }
}