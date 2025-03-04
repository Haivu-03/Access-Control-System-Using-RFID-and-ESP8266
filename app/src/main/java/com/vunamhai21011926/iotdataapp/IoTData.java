package com.vunamhai21011926.iotdataapp;

public class IoTData {
    private String tag;
    private String time;

    public IoTData(String tag, String time) {
        this.tag = tag;
        this.time = time;
    }

    public String getTag() {
        return tag;
    }

    public String getTime() {
        return time;
    }
}
