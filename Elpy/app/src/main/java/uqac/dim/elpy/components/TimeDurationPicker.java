package uqac.dim.elpy.components;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import uqac.dim.elpy.R;

public class TimeDurationPicker extends Dialog {
    private ITimeDurationPickerListener listener;

    private NumberPicker hour_picker;
    private NumberPicker minute_picker;
    private NumberPicker second_picker;
    private Button button_ok;
    private Button button_cancel;

    public TimeDurationPicker(Context context) {
        super(context);

        setContentView(R.layout.time_duration_picker);

        hour_picker = findViewById(R.id.hour_picker);
        minute_picker = findViewById(R.id.minute_picker);
        second_picker = findViewById(R.id.second_picker);
        button_ok = findViewById(R.id.button_ok);
        button_cancel = findViewById(R.id.button_cancel);

        hour_picker.setMinValue(0);
        hour_picker.setMaxValue(23);
        minute_picker.setMinValue(0);
        minute_picker.setMaxValue(59);
        second_picker.setMinValue(0);
        second_picker.setMaxValue(59);

        button_ok.setOnClickListener(v -> {
            notifyTimePicker();
            dismiss();
        });
        button_cancel.setOnClickListener(v -> {
            notifyCancel();
            dismiss();
        });
    }

    public void setTimeDurationPickerListener(ITimeDurationPickerListener listener) {
        this.listener = listener;
    }

    private void notifyTimePicker() {
        if (listener != null) {
            listener.onTimePicked(hour_picker.getValue(), minute_picker.getValue(), second_picker.getValue());
        }
    }

    private void notifyCancel() {
        if (listener != null) {
            listener.onCancel();
        }
    }
}
