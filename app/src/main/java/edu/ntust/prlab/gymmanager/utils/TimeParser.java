package edu.ntust.prlab.gymmanager.utils;

public class TimeParser {

    private TimeParser() {
    }

    public static final String SEPARATOR = ":";

    /**
     * 把時間字串的小時給取出來
     */
    public static int parseHour(String time) {
        int index = time.indexOf(SEPARATOR);
        String hour = time.substring(0, index);
        return Integer.parseInt(hour);
    }

    /**
     * 把時間字串的分鐘給取出來
     */
    public static int parseMinute(String time) {
        int index = time.indexOf(SEPARATOR);
        String minute = time.substring(index + SEPARATOR.length(), time.length());
        return Integer.parseInt(minute);
    }

}
