package uqac.dim.elpy.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import uqac.dim.elpy.R;

@Entity(tableName = "NOTE")
public class Note implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id = 0;
    @ColumnInfo(name = "Title")
    private String title;
    @ColumnInfo(name = "Content")
    private String content;
    @ColumnInfo(name = "Date")
    private String date;
    @ColumnInfo(name = "Color")
    private int color;
    @ColumnInfo(name = "IsPinned")
    private boolean isPinned;

    public Note(String date) {
        this.date = date;
        this.color = R.color.orange;
        this.isPinned = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }
}
