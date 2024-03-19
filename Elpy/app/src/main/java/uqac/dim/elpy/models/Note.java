package uqac.dim.elpy.models;

import java.io.Serializable;

public class Note implements Serializable {
    private int id;
    private String title;
    private String content;
    private String date;
    private int color;
    private boolean isPinned;

    public Note(int id, String title, String content, String date, int color, boolean isPinned) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.color = color;
        this.isPinned = isPinned;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public int getColor() {
        return color;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }
}
