package uqac.dim.elpy.component;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;

import uqac.dim.elpy.R;
import uqac.dim.elpy.models.Timer;

public class TimeDurationPicker extends Dialog {
    private NumberPicker hour;
    private NumberPicker minute;
    private NumberPicker second;
    private Button ok_button;
    private Button cancel_button;
    private ITimeDurationPickerListener listener;

    public TimeDurationPicker(@NonNull Context context, Timer currentTimer, ITimeDurationPickerListener listener) {
        super(context);
        this.listener = listener;

        setContentView(R.layout.time_duration_picker);
        hour = findViewById(R.id.hour_picker);
        minute = findViewById(R.id.minute_picker);
        second = findViewById(R.id.second_picker);
        ok_button = findViewById(R.id.button_ok);
        cancel_button = findViewById(R.id.button_cancel);

        hour.setMinValue(0);
        hour.setMaxValue(23);
        hour.setValue(currentTimer.getHour());

        minute.setMinValue(0);
        minute.setMaxValue(59);
        minute.setValue(currentTimer.getMinute());

        second.setMinValue(0);
        second.setMaxValue(59);
        second.setValue(currentTimer.getSecond());

        ok_button.setOnClickListener(v -> {
            listener.onTimeDurationPickerOk(new Timer(
                    hour.getValue(),
                    minute.getValue(),
                    second.getValue()
            ));
            dismiss();
        });

        cancel_button.setOnClickListener(v -> {
            listener.onTimeDurationPickerCancel();
            dismiss();
        });
    }

    public void setListener(ITimeDurationPickerListener listener) {
        this.listener = listener;
    }
}
