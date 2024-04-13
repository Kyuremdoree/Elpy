package uqac.dim.elpy.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Timer {
    @ColumnInfo(name = "Hour")
    private int hour;
    @ColumnInfo(name = "Minute")
    private int minute;
    @ColumnInfo(name = "Second")
    private int second;

    public Timer(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public Timer(long millis) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        this.hour = (int) (seconds / 3600);
        this.minute = (int) ((seconds % 3600) / 60);
        this.second = (int) (seconds % 60);
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    @TypeConverter
    public static long timerToLong(Timer timer) {
        return timer.toMillis();
    }

    public long toMillis() {
        return TimeUnit.HOURS.toMillis(hour)
        + TimeUnit.MINUTES.toMillis(minute)
        + TimeUnit.SECONDS.toMillis(second);
    }

    @TypeConverter
    public static Timer longToTimer(long millis) {
        return new Timer(millis);
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }
}
