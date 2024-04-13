package uqac.dim.elpy.components;

public interface ITimeDurationPickerListener {
    void onTimePicked(int hour, int minute, int second);
    void onCancel();
}
