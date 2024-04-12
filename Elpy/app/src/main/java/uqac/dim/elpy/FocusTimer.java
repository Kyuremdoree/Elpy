package uqac.dim.elpy;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import java.util.List;

import uqac.dim.elpy.database.RoomDB;
import uqac.dim.elpy.models.Chrono;

public class FocusTimer extends Fragment {
    private final static long DEFAULT_POMODORO = 1500000;
    private final static long DEFAULT_SHORT_BREAK = 300000;
    private final static long DEFAULT_LONG_BREAK = 900000;

    private RoomDB database;
    private Chrono chrono;

    private Chronometer chronometer;
    private RadioGroup pomodoro_selector;
    private Button pomodoro_button;
    private Button short_break_button;
    private Button long_break_button;
    private Button pomodoro_picker;
    private Button short_break_picker;
    private Button long_break_picker;
    private ImageView play_button;
    private ImageView pause_button;
    private ImageView reset_button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_focus_timer, container, false);
        database = RoomDB.getInstance(getContext());
        chrono = database.mainDAO().getChrono();

        if (chrono == null) {
            resetChrono();
        }

        chronometer = view.findViewById(R.id.chronometer);
        pomodoro_selector = view.findViewById(R.id.pomodoro_selector);
        pomodoro_button = view.findViewById(R.id.pomodoro_button);
        short_break_button = view.findViewById(R.id.short_break_button);
        long_break_button = view.findViewById(R.id.long_break_button);
        pomodoro_picker = view.findViewById(R.id.pomodoro_picker);
        short_break_picker = view.findViewById(R.id.short_break_picker);
        long_break_picker = view.findViewById(R.id.long_break_picker);
        play_button = view.findViewById(R.id.play_button);
        pause_button = view.findViewById(R.id.pause_button);
        reset_button = view.findViewById(R.id.reset_button);

        pomodoro_picker.setText("00:25:00");
        short_break_picker.setText("00:05:00");
        long_break_picker.setText("00:15:00");

        chronometer.setOnChronometerTickListener(chronometer -> {
            if (chronometer.getText().equals("00:00")) {
                manageChronometer(true);
            }
        });

        pomodoro_selector.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.pomodoro_button) {

            }
            else if (checkedId == R.id.short_break_button) {

            }
            else if (checkedId == R.id.long_break_button) {

            }
        });

        play_button.setOnClickListener(v -> {
            manageChronometer(false);
        });

        pause_button.setOnClickListener(v -> {
            pauseChronometer();
        });

        reset_button.setOnClickListener(v -> {
            manageChronometer(true);
        });

        pause_button.setEnabled(false);
        reset_button.setEnabled(false);
        pomodoro_selector.check(R.id.pomodoro_button);

        return view;
    }

    private void manageChronometer(boolean state) {
        pomodoro_picker.setEnabled(state);
        short_break_picker.setEnabled(state);
        long_break_picker.setEnabled(state);
        pomodoro_selector.setEnabled(state);
        pomodoro_button.setEnabled(state);
        short_break_button.setEnabled(state);
        long_break_button.setEnabled(state);
        play_button.setEnabled(state);
        pause_button.setEnabled(!state);
        reset_button.setEnabled(state);

        if (state) {
            chronometer.stop();
        }
        else {
            chronometer.start();
        }
    }

    private void pauseChronometer() {
        play_button.setEnabled(true);
        reset_button.setEnabled(true);
        chronometer.stop();
    }

    private void resetChrono() {
        chrono = new Chrono(DEFAULT_POMODORO, DEFAULT_SHORT_BREAK, DEFAULT_LONG_BREAK);
    }
}
