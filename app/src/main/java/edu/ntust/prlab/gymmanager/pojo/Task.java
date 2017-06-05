package edu.ntust.prlab.gymmanager.pojo;

/**
 * 用來代表提醒項目的類別。
 */
public class Task {

    /**
     * 用來代表提醒項目的編號。
     */
    private int id;
    /**
     * 用來代表提醒項目的標題。
     */
    private String title;
    /**
     * 用來代表提醒項目的時間。
     */
    private String time;
    /**
     * 用來代表提醒項目的啟用與否。
     */
    private boolean isEnabled;

    public Task() {
    }

    public int getId() {
        return id;
    }

    public Task setId(int id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Task setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getTime() {
        return time;
    }

    public Task setTime(String time) {
        this.time = time;
        return this;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public Task setEnabled(boolean enabled) {
        isEnabled = enabled;
        return this;
    }

}
