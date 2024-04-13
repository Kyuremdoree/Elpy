package uqac.dim.elpy;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import uqac.dim.elpy.components.ITimeDurationPickerListener;
import uqac.dim.elpy.components.TimeDurationPicker;
import uqac.dim.elpy.database.RoomDB;
import uqac.dim.elpy.models.Chrono;
import uqac.dim.elpy.models.Timer;

public class FocusTimer extends Fragment implements ITimeDurationPickerListener {
    private final static long DEFAULT_POMODORO = 1500000;
    private final static long DEFAULT_SHORT_BREAK = 300000;
    private final static long DEFAULT_LONG_BREAK = 900000;
    private final static long TIMER_INTERVAL = 1000;

    private MainActivity mainActivity;
    private RoomDB database;
    private Chrono chrono;
    private Button currentButton;
    private CountDownTimer countDownTimer;
    private Timer selectedTimer;
    private long remainingMillis;

    private TextView time_text;
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
            chrono = new Chrono(
                    new Timer(DEFAULT_POMODORO),
                    new Timer(DEFAULT_SHORT_BREAK),
                    new Timer(DEFAULT_LONG_BREAK)
            );
            updateDB();
        }

        time_text = view.findViewById(R.id.time_text);
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

        pomodoro_picker.setText(chrono.getPomodoro().toString());
        short_break_picker.setText(chrono.getShortBreak().toString());
        long_break_picker.setText(chrono.getLongBreak().toString());

        pomodoro_picker.setOnClickListener(v -> {
            openPicker(pomodoro_picker);
        });
        short_break_picker.setOnClickListener(v -> {
            openPicker(short_break_picker);
        });
        long_break_picker.setOnClickListener(v -> {
            openPicker(long_break_picker);
        });

        pomodoro_selector.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.pomodoro_button) {
                selectedTimer = chrono.getPomodoro();
            }
            else if (checkedId == R.id.short_break_button) {
                selectedTimer = chrono.getShortBreak();
            }
            else if (checkedId == R.id.long_break_button) {
                selectedTimer = chrono.getLongBreak();
            }

            time_text.setText(selectedTimer.toString());
        });

        play_button.setOnClickListener(v -> {
            startTimer(remainingMillis == 0 ? selectedTimer.toMillis() : remainingMillis);
            setEnables(false);
        });
        pause_button.setOnClickListener(v -> {
            pauseTimer();
        });
        reset_button.setOnClickListener(v -> {
            endTimer();
            setEnables(true);
        });

        pause_button.setEnabled(false);
        reset_button.setEnabled(false);
        pomodoro_selector.check(R.id.pomodoro_button);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mainActivity = (MainActivity) context;
        }
    }

    private void startTimer(long millis) {
        countDownTimer = new CountDownTimer(millis, TIMER_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                time_text.setText(Timer.longToTimer(millisUntilFinished).toString());
                remainingMillis = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                endTimer();
            }
        }.start();
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        play_button.setEnabled(true);
        pause_button.setEnabled(false);
        reset_button.setEnabled(true);
    }

    private void endTimer() {
        countDownTimer.cancel();
        setEnables(true);
        time_text.setText(selectedTimer.toString());
        remainingMillis = 0;
    }

    private void setEnables(boolean state) {
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
        mainActivity.setDrawerEnabled(state);
    }

    private void updateDB() {
        database.mainDAO().insertChrono(chrono);
    }

    private void openPicker(Button button) {
        currentButton = button;
        TimeDurationPicker picker = new TimeDurationPicker(getContext());
        picker.setTimeDurationPickerListener(this);
        picker.show();
    }

    @Override
    public void onTimePicked(int hour, int minute, int second) {
        currentButton.setText(hour+":"+minute+":"+second);

        if (currentButton == pomodoro_picker) {
            chrono.setPomodoro(new Timer(hour, minute, second));
        }
        else if (currentButton == short_break_picker) {
            chrono.setShortBreak(new Timer(hour, minute, second));
        }
        else {
            chrono.setLongBreak(new Timer(hour, minute, second));
        }
        updateDB();
    }

    @Override
    public void onCancel() {
        currentButton = null;
    }
}
