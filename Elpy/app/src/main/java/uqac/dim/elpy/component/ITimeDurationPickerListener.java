package uqac.dim.elpy.component;

import uqac.dim.elpy.models.Timer;

public interface ITimeDurationPickerListener {
    void onTimeDurationPickerOk(Timer timer);
    void onTimeDurationPickerCancel();
}
