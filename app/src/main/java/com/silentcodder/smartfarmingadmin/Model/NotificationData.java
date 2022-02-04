package com.silentcodder.smartfarmingadmin.Model;

public class NotificationData {
    String Msg;
    String Title;
    String NotificationReceiver;
    String NotificationSender;
    String Status;
    Long TimeStamp;

    public NotificationData() {
    }

    public NotificationData(String msg, String title, String notificationReceiver, String notificationSender, String status, Long timeStamp) {
        Msg = msg;
        Title = title;
        NotificationReceiver = notificationReceiver;
        NotificationSender = notificationSender;
        Status = status;
        TimeStamp = timeStamp;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getNotificationReceiver() {
        return NotificationReceiver;
    }

    public void setNotificationReceiver(String notificationReceiver) {
        NotificationReceiver = notificationReceiver;
    }

    public String getNotificationSender() {
        return NotificationSender;
    }

    public void setNotificationSender(String notificationSender) {
        NotificationSender = notificationSender;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public Long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        TimeStamp = timeStamp;
    }
}
