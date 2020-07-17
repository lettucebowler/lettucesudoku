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
            boolean do_peer_cells = peer_cells.isChecked();
            intent.putExtra("do_peer_cells", do_peer_cells);
            boolean do_peer_digits = peer_digits.isChecked();
            intent.putExtra("do_peer_digits", do_peer_digits);
            int id = color_rule.getCheckedRadioButtonId();
            RadioButton button =  findViewById(id);
            boolean do_legality = button != null && button.getText() == "Legality";
            intent.putExtra("do_legality", do_legality);
            startActivity(intent);
        });
    }
}