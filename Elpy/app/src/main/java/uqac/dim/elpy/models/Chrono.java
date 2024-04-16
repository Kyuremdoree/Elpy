package uqac.dim.elpy.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CHRONO")
public class Chrono {
    @PrimaryKey
    private int id = 5;
    @ColumnInfo(name = "Pomodoro")
    private Timer pomodoro;
    @ColumnInfo(name = "ShortBreak")
    private Timer shortBreak;
    @ColumnInfo(name = "LongBreak")
    private Timer longBreak;

    public Chrono(Timer pomodoro, Timer shortBreak, Timer longBreak) {
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

    public Timer getPomodoro() {
        return pomodoro;
    }

    public void setPomodoro(Timer pomodoro) {
        this.pomodoro = pomodoro;
    }

    public Timer getShortBreak() {
        return shortBreak;
    }

    public void setShortBreak(Timer shortBreak) {
        this.shortBreak = shortBreak;
    }

    public Timer getLongBreak() {
        return longBreak;
    }

    public void setLongBreak(Timer longBreak) {
        this.longBreak = longBreak;
    }
}
