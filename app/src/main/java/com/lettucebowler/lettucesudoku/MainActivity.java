package com.lettucebowler.lettucesudoku;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ToggleButton peer_cells;
    ToggleButton peer_digits;
    RadioGroup color_rule;
    SeekBar difficulty_slider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        peer_cells = findViewById(R.id.peer_cell_toggle);
        peer_digits = findViewById(R.id.peer_digit_toggle);
        color_rule = findViewById(R.id.digit_color_radio);
        difficulty_slider = findViewById(R.id.seekBar_difficulty);

        Button launch_button = findViewById(R.id.button_launch);
        launch_button.setOnClickListener(e -> {
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(write_game_bundle(intent));
            finish();
        });
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
            if(intent.hasExtra("game_in_progress")) {
                change_launch_button_to_resume();
            }
        }
    }

    private void change_launch_button_to_resume() {
        Button button = findViewById(R.id.button_launch);
        button.setText("resume game");
        button.setOnClickListener(e -> {
//            Intent intent = getIntent();
            setResult(Activity.RESULT_OK, write_game_bundle(new Intent()));
            finish();
        });
    }

    private Intent write_game_bundle(Intent intent) {
//        Intent intent = new Intent();
        int hint_offset = difficulty_slider.getProgress();
        intent.putExtra("hint_offset", hint_offset);

        boolean do_peer_cells = peer_cells.isChecked();
        intent.putExtra("do_peer_cells", do_peer_cells);

        boolean do_peer_digits = peer_digits.isChecked();
        intent.putExtra("do_peer_digits", do_peer_digits);

        int id = color_rule.getCheckedRadioButtonId();
        RadioButton button =  findViewById(id);
        boolean do_legality = button != null && ((RadioButton)color_rule.getChildAt(0)).isChecked();
        intent.putExtra("do_legality", do_legality);

//        System.out.println("write_game_bundle()");
//        System.out.println("hint offset: " + hint_offset);
//        System.out.println("do_peer_cells: " + do_peer_cells);
//        System.out.println("do_peer_digits: " + do_peer_digits);
//        System.out.println("do_legality: " + do_legality);

        return intent;
    }
}