package uqac.dim.elpy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import service.ITimerServiceListener;
import service.TimerService;
import uqac.dim.elpy.component.ITimeDurationPickerListener;
import uqac.dim.elpy.component.TimeDurationPicker;
import uqac.dim.elpy.enums.TimerState;
import uqac.dim.elpy.enums.TimerType;
import uqac.dim.elpy.models.Timer;

public class FocusTimer extends Fragment implements ITimeDurationPickerListener, ITimerServiceListener {
    private TimerService timerService;

    private TextView time_text;
    private RadioGroup pomodoro_selector;
    private RadioButton pomodoro_button;
    private RadioButton short_break_button;
    private RadioButton long_break_button;

    private Button pomodoro_picker;
    private Button short_break_picker;
    private Button long_break_picker;
    private Button selectedPicker;

    private ImageView play_button;
    private ImageView pause_button;
    private ImageView reset_button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_focus_timer, container, false);

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

        timerService = TimerService.getInstance();
        timerService.setListener(this);
        if (timerService.getTimerState() == TimerState.STARTED) {
            setEnables(false);
            time_text.setText(Timer.longToTimer(timerService.getCurrentMillis()).toString());
        }
        else if (timerService.getTimerState() == TimerState.PAUSED) {
            setEnables(false);
            play_button.setEnabled(true);
            reset_button.setEnabled(true);
            time_text.setText(Timer.longToTimer(timerService.getCurrentMillis()).toString());
        }
        else if (timerService.getTimerState() == TimerState.STOPPPED) {
            setEnables(true);
        }

        pomodoro_picker.setText(timerService.getChrono().getPomodoro().toString());
        short_break_picker.setText(timerService.getChrono().getShortBreak().toString());
        long_break_picker.setText(timerService.getChrono().getLongBreak().toString());

        pomodoro_picker.setOnClickListener(v -> {
            selectedPicker = pomodoro_picker;
            openPicker(timerService.getChrono().getPomodoro());
        });
        short_break_picker.setOnClickListener(v -> {
            selectedPicker = short_break_picker;
            openPicker(timerService.getChrono().getShortBreak());
        });
        long_break_picker.setOnClickListener(v -> {
            selectedPicker = long_break_picker;
            openPicker(timerService.getChrono().getLongBreak());
        });

        pomodoro_selector.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.pomodoro_button) {
                timerService.setTimerType(TimerType.POMODORO);
                time_text.setText(timerService.getChrono().getPomodoro().toString());
            }
            else if (checkedId == R.id.short_break_button) {
                timerService.setTimerType(TimerType.SHORT_BREAK);
                time_text.setText(timerService.getChrono().getShortBreak().toString());
            }
            else if (checkedId == R.id.long_break_button) {
                timerService.setTimerType(TimerType.LONG_BREAK);
                time_text.setText(timerService.getChrono().getLongBreak().toString());
            }
        });

        play_button.setOnClickListener(v -> {
            timerService.start();
            setEnables(false);
        });
        pause_button.setOnClickListener(v -> {
            timerService.pause();
            play_button.setEnabled(true);
            reset_button.setEnabled(true);
        });
        reset_button.setOnClickListener(v -> {
            timerService.stop();
            setEnables(true);
        });

        if (timerService.getTimerType() == TimerType.POMODORO) {
            pomodoro_button.setChecked(true);
        }
        else if (timerService.getTimerType() == TimerType.SHORT_BREAK) {
            short_break_button.setChecked(true);
        }
        else if (timerService.getTimerType() == TimerType.LONG_BREAK) {
            long_break_button.setChecked(true);
        }

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        timerService.setListener(null);
    }

    private void openPicker(Timer selectedTimer) {
        new TimeDurationPicker(getContext(), selectedTimer, this).show();
    }

    @Override
    public void onTimeDurationPickerOk(Timer timer) {
        selectedPicker.setText(timer.toString());

        if (selectedPicker == pomodoro_picker) {
            timerService.getChrono().setPomodoro(timer);
            if (timerService.getTimerType() == TimerType.POMODORO) {
                time_text.setText(timer.toString());
            }
        }
        else if (selectedPicker == short_break_picker) {
            timerService.getChrono().setShortBreak(timer);
            if (timerService.getTimerType() == TimerType.SHORT_BREAK) {
                time_text.setText(timer.toString());
            }
        }
        else if (selectedPicker == long_break_picker){
            timerService.getChrono().setLongBreak(timer);
            if (timerService.getTimerType() == TimerType.LONG_BREAK) {
                time_text.setText(timer.toString());
            }
        }
        selectedPicker = null;
        timerService.UpdateDB();
    }

    @Override
    public void onTimeDurationPickerCancel() {
        selectedPicker = null;
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
    }

    @Override
    public void onTimerServiceTick(long currentMillis) {
        time_text.setText(Timer.longToTimer(currentMillis).toString());
    }

    @Override
    public void onTimerServiceFinish(long startMillis) {
        time_text.setText(Timer.longToTimer(startMillis).toString());
        setEnables(true);
    }
}
