package service;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import uqac.dim.elpy.MainActivity;
import uqac.dim.elpy.database.RoomDB;
import uqac.dim.elpy.enums.TimerState;
import uqac.dim.elpy.enums.TimerType;
import uqac.dim.elpy.models.Chrono;
import uqac.dim.elpy.models.Timer;

public class TimerService extends Service{
    private static final long DEFAULT_POMODORO = 1500000;
    private static final long DEFAULT_SHORT_BREAK = 300000;
    private static final long DEFAULT_LONG_BREAK = 900000;
    private static final long TIMER_INTERVAL = 1000;

    private static TimerService instance;
    private MainActivity mainActivity;

    private RoomDB database;
    private Chrono chrono;
    private TimerType timerType = TimerType.POMODORO;
    private CountDownTimer countDownTimer;
    private long startMillis;
    private long currentMillis;
    private ITimerServiceListener listener;
    private TimerState timerState = TimerState.STOPPPED;

    public TimerService(MainActivity mainActivity) {
        instance = this;
        this.mainActivity = mainActivity;
        database = RoomDB.getInstance(mainActivity);
        chrono = database.mainDAO().getChrono();
        if (chrono == null) {
            chrono = new Chrono(
                    new Timer(DEFAULT_POMODORO),
                    new Timer(DEFAULT_SHORT_BREAK),
                    new Timer(DEFAULT_LONG_BREAK)
            );
            UpdateDB();
        }
    }

    public static TimerService getInstance() {
        return instance;
    }

    public void UpdateDB() {
        database.mainDAO().insertChrono(chrono);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public Chrono getChrono() {
        return chrono;
    }

    public TimerType getTimerType() {
        return timerType;
    }

    public void setTimerType(TimerType timerType) {
        this.timerType = timerType;
    }

    public void start() {
        if (currentMillis == 0) {
            if (timerType == TimerType.POMODORO) {
                currentMillis = chrono.getPomodoro().toMillis();
            }
            else if (timerType == TimerType.SHORT_BREAK) {
                currentMillis = chrono.getShortBreak().toMillis();
            }
            else if (timerType == TimerType.LONG_BREAK) {
                currentMillis = chrono.getLongBreak().toMillis();
            }
            startMillis = currentMillis;
        }

        countDownTimer = new CountDownTimer(currentMillis, TIMER_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (listener != null) {
                    listener.onTimerServiceTick(millisUntilFinished);
                }
                currentMillis = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                stop();
            }
        }.start();
        timerState = TimerState.STARTED;
    }
    public void pause() {
        countDownTimer.cancel();
        timerState = TimerState.PAUSED;
    }
    public void stop() {
        if (listener != null) {
            listener.onTimerServiceFinish(startMillis);
        }
        currentMillis = 0;
        countDownTimer.cancel();
        timerState = TimerState.STOPPPED;
    }

    public void setListener(ITimerServiceListener listener) {
        this.listener = listener;
    }

    public TimerState getTimerState() {
        return timerState;
    }

    public long getCurrentMillis() {
        return currentMillis;
    }
}
