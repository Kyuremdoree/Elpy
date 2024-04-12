package uqac.dim.elpy.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CHRONO")
public class Chrono {
    @PrimaryKey
    private int id = 5;
    @ColumnInfo(name = "Pomodoro")
    private long pomodoro;
    @ColumnInfo(name = "ShortBreak")
    private long shortBreak;
    @ColumnInfo(name = "LongBreak")
    private long longBreak;

    public Chrono(long pomodoro, long shortBreak, long longBreak) {
        this.pomodoro = pomodoro;
        this.shortBreak = shortBreak;
        this.longBreak = longBreak;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getPomodoro() {
        return pomodoro;
    }

    public long getShortBreak() {
        return shortBreak;
    }

    public long getLongBreak() {
        return longBreak;
    }

    public void setPomodoro(long pomodoro) {
        this.pomodoro = pomodoro;
    }

    public void setShortBreak(long shortBreak) {
        this.shortBreak = shortBreak;
    }

    public void setLongBreak(long longBreak) {
        this.longBreak = longBreak;
    }
}
