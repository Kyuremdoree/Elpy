package service;

public interface ITimerServiceListener {
    void onTimerServiceTick(long currentMillis);
    void onTimerServiceFinish(long startMillis);
}
