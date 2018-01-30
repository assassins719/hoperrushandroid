package com.hoperrush.pojo;

/**
 * Created by user145 on 5/3/2017.
 */
public class ReceiveMessageEvent {
    private String eventName;

    private Object[] objectsArray;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Object[] getObjectsArray() {
        return objectsArray;
    }

    public void setObjectsArray(Object[] objectsArray) {
        this.objectsArray = objectsArray;
    }
}
