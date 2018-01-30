package com.hoperrush.pojo;

/**
 * Created by CAS61 on 12/30/2016.
 */
public class MessageChatPojo {

    private String MessageTaskId = "";
    private String MessageBookingId = "";
    private String MessageTaskerNameId = "";
    private String MessageTaskerId = "";
    private String MessageTaskerImageId = "";
    private String Category;
    private String tasker_status="";
    private String date="";


    public String getMessageTaskerImageId() {
        return MessageTaskerImageId;
    }

    public void setMessageTaskerImageId(String messageTaskerImageId) {
        MessageTaskerImageId = messageTaskerImageId;
    }

    public String getMessageTaskerNameId() {
        return MessageTaskerNameId;
    }

    public void setMessageTaskerNameId(String messageTaskerNameId) {
        MessageTaskerNameId = messageTaskerNameId;
    }

    public String getMessageTaskerId() {
        return MessageTaskerId;
    }

    public void setMessageTaskerId(String messageTaskerId) {
        MessageTaskerId = messageTaskerId;
    }

    public String getMessageTaskId() {
        return MessageTaskId;
    }

    public void setMessageTaskId(String messageTaskId) {
        MessageTaskId = messageTaskId;
    }

    public String getMessageBookingId() {
        return MessageBookingId;
    }

    public void setMessageBookingId(String messageBookingId) {
        MessageBookingId = messageBookingId;
    }
    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }
    public String getstatus() {
        return tasker_status;
    }

    public void setstatus(String taskerstatus) {
        tasker_status = taskerstatus;
    }

    public String getdate() {
        return date;
    }

    public void setdate(String Date) {
        date = Date;
    }

}
