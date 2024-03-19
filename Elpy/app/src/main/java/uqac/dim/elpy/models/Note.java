package uqac.dim.elpy.models;

import java.io.Serializable;

public class Note implements Serializable {
    private int id;
    private String title;
    private String content;
    private String date;
    private boolean isPinned;

    public Note(String title, String content, String date, boolean isPinned) {
        this.title = title;
        this.content = content;
        this.date = date;
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

    public boolean isPinned() {
        return isPinned;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }
}
