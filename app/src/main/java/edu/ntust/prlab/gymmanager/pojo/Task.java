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

    /**
     * 建構子
     */
    public Task() {
    }

    /**
     * 取得編號
     */
    public int getId() {
        return id;
    }

    /**
     * 修改編號
     */
    public Task setId(int id) {
        this.id = id;
        return this;
    }

    /**
     * 取得標題
     */
    public String getTitle() {
        return title;
    }

    /**
     * 修改標題
     */
    public Task setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * 取得時間
     */
    public String getTime() {
        return time;
    }

    /**
     * 修改時間
     */
    public Task setTime(String time) {
        this.time = time;
        return this;
    }

    /**
     * 取得啟用與否
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * 修改啟用與否
     */
    public Task setEnabled(boolean enabled) {
        isEnabled = enabled;
        return this;
    }

}
