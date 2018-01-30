package com.hoperrush.pojo;

/**
 * Created by user145 on 5/3/2017.
 */
public class SendMessageEvent {
    private String eventName;

    private Object messageObject;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }


    public Object getMessageObject() {
        return messageObject;
    }

    public void setMessageObject(Object messageObject) {
        this.messageObject = messageObject;
    }
}
