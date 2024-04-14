package service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import uqac.dim.elpy.MainActivity;
import uqac.dim.elpy.R;
import uqac.dim.elpy.database.RoomDB;
import uqac.dim.elpy.enums.TimerState;
import uqac.dim.elpy.enums.TimerType;
import uqac.dim.elpy.models.Chrono;
import uqac.dim.elpy.models.Timer;

public class TimerService extends Service {
    private static final long DEFAULT_POMODORO = 1500000;
    private static final long DEFAULT_SHORT_BREAK = 300000;
    private static final long DEFAULT_LONG_BREAK = 900000;
    private static final long TIMER_INTERVAL = 100;
    private static final String CHANNEL_ID = "timer_channel_id";
    private static final String CHANNEL_NAME = "timer_channel";
    private static final String CHANNEL_DESCRIPTION = "timer_channel_description";
    private static int NOTIFICATION_ID = 1111;

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
    private MediaPlayer mediaPlayer;
    private NotificationManager nm;
    private Notification.Builder builder;

    public TimerService(MainActivity mainActivity) {
        instance = this;
        this.mainActivity = mainActivity;
        database = RoomDB.getInstance(mainActivity);
        chrono = database.mainDAO().getChrono();
        mediaPlayer = MediaPlayer.create(mainActivity, R.raw.timer_end);

        if (chrono == null) {
            chrono = new Chrono(
                    new Timer(DEFAULT_POMODORO),
                    new Timer(DEFAULT_SHORT_BREAK),
                    new Timer(DEFAULT_LONG_BREAK)
            );
            UpdateDB();
        }
        createNotificationChannel();
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

    public void setListener(ITimerServiceListener listener) {
        this.listener = listener;
    }

    public TimerState getTimerState() {
        return timerState;
    }

    public long getCurrentMillis() {
        return currentMillis;
    }

    public void start() {
        if (currentMillis == 0) {
            if (timerType == TimerType.POMODORO) {
                currentMillis = chrono.getPomodoro().toMillis();
            } else if (timerType == TimerType.SHORT_BREAK) {
                currentMillis = chrono.getShortBreak().toMillis();
            } else if (timerType == TimerType.LONG_BREAK) {
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
                updateNotification();
            }

            @Override
            public void onFinish() {
                stop();
                mediaPlayer.start();
            }
        }.start();
        timerState = TimerState.STARTED;
        createNotification();
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
        cancelNotification();
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESCRIPTION);
            nm = mainActivity.getSystemService(NotificationManager.class);
            nm.createNotificationChannel(channel);
        }
    }

    private void createNotification() {
        Intent intent = new Intent(mainActivity, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mainActivity, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder = new Notification.Builder(mainActivity, CHANNEL_ID)
                .setContentTitle("Focus Timer")
                .setContentText(Timer.longToTimer(currentMillis).toString())
                .setSmallIcon(R.drawable.timer_notification_icon)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setFullScreenIntent(pendingIntent, true);
        try {
            nm.notify(NOTIFICATION_ID, builder.build());
        } catch (Exception e) {
            Log.i("FocusTimer", "createNotification: " + e.getMessage(), e);
        }
    }

    private void updateNotification() {
        builder.setContentText(Timer.longToTimer(currentMillis).toString());
        nm.notify(NOTIFICATION_ID, builder.build());
    }

    private void cancelNotification() {
        nm.cancel(NOTIFICATION_ID);
    }
}
