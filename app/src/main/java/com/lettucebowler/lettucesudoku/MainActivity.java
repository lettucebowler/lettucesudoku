package com.lettucebowler.lettucesudoku;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

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
            RadioButton button =  findViewById(color_rule.getCheckedRadioButtonId());
            boolean do_legality = button.getText() == "Legality";
            intent.putExtra("color_by_legality", do_legality);
            startActivity(intent);
        });
    }
}