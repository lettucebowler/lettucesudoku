package com.lettucebowler.lettucesudoku;

import android.app.Activity;
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
import com.lettucebowler.lettucesudoku.domains.sudoku.SudokuProblem;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ToggleButton peer_cells;
    ToggleButton peer_digits;
    RadioGroup color_rule;
    SeekBar difficulty_slider;
    boolean do_legality;
    boolean do_peer_cells;
    boolean do_peer_digits;
    int hint_offset;


    boolean DEFAULT_DO_PEER_CELLS = true;
    boolean DEFAULT_DO_PEER_DIGITS = true;
    boolean DEFAULT_DO_LEGALITY = false;
    int DEFAULT_HINT_OFFSET = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        peer_cells = findViewById(R.id.peer_cell_toggle);
        peer_digits = findViewById(R.id.peer_digit_toggle);
        color_rule = findViewById(R.id.digit_color_radio);
        difficulty_slider = findViewById(R.id.seekBar_difficulty);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            if(intent.hasExtra("do_peer_cells")) {
                peer_cells.setChecked(bundle.getBoolean("do_peer_cells"));
            }

            if(intent.hasExtra("do_peer_digits")) {
                peer_digits.setChecked(bundle.getBoolean("do_peer_digits"));
            }

            if(intent.hasExtra("do_legality")) {
                int index = bundle.getBoolean("do_legality") ? 0 : 1;
                System.out.println(index);
                ((RadioButton) color_rule.getChildAt(index)).setChecked(true);
            }

            if(intent.hasExtra("hint_offset")) {
                difficulty_slider.setProgress(bundle.getInt("hint_offset"));
            }
        }
        configureGameButton();
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        if(intent.hasExtra("game_in_progress")) {
            resumeGame();
        }
        else {
            super.onBackPressed();
        }
    }

    private void configureGameButton() {
        Intent game_intent = getIntent();
        SharedPreferences mPrefs = getSharedPreferences("system", MODE_PRIVATE);
        String json = mPrefs.getString("current_board", "");
        System.out.println("game_save: " + json);
        if(!json.equals("")) {
            setLaunchButtonToResumeFromSave(json);
        }
        else if(game_intent.hasExtra("game_in_progress")) {
            setLaunchButtonToResume();
        }
        else {
            setLaunchButtonToStart();
        }
    }

    private void setLaunchButtonToResumeFromSave(String save_string) {
        Button button = findViewById(R.id.button_launch);
        button.setText("Resume From Save");
        button.setOnClickListener(e -> {
            resumeGameFromSave(save_string);
        });
    }

    private void setLaunchButtonToStart() {
        Button button = findViewById(R.id.button_launch);
        button.setOnClickListener(e -> {
            startGame();
        });
    }

    private void setLaunchButtonToResume() {
        Button button = findViewById(R.id.button_launch);
        button.setText("resume game");
        button.setOnClickListener(e -> {
            resumeGame();
        });
    }

    private void resumeGameFromSave(String save_string) {
        Intent intent = new Intent(this, GameActivity.class);
        intent = writeGameBundle(intent);
        intent.putExtra("current_board", save_string);
        startActivity(writeGameBundle(intent));

    }

    private void resumeGame() {
        setResult(Activity.RESULT_OK, writeGameBundle(new Intent()));
        finish();
    }

    private void startGame() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(writeGameBundle(intent));
    }

    private Intent writeGameBundle(Intent intent) {
        intent.putExtra("hint_offset", getHintOffset());
        intent.putExtra("do_peer_cells", getDoPeerCells());
        intent.putExtra("do_peer_digits", getDoPeerDigits());
        intent.putExtra("do_legality", getDoLegality());

        return intent;
    }

    private void writeSharedPrefs() {
        SharedPreferences mPrefs = getSharedPreferences("system", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        prefsEditor.putString("do_peer_cells", gson.toJson(getDoPeerCells()));
        prefsEditor.putString("do_peer_digits", gson.toJson(getDoPeerDigits()));
        prefsEditor.putString("do_legality", gson.toJson(getDoLegality()));
        prefsEditor.putString("hint_offset", gson.toJson(getHintOffset()));
    }

    private void readSharedPrefs() {
        SharedPreferences mPrefs = getSharedPreferences("system", MODE_PRIVATE);
//        SharedPreferences.Editor prefsEditor = mPrefs.edit();
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
        hint_offset = hint_offset_string.equals("") ? DEFAULT_HINT_OFFSET : gson.fromJson(do_legality_string, int.class);
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