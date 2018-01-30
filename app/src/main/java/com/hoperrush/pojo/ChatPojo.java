package com.hoperrush.pojo;

/**
 * Casperon Technology on 1/29/2016.
 */
public class ChatPojo {
    private String message;
    private String time;
    private String type;
    private String date;
    private String status="";
    private String warnings="";
    private String seentype="";
    private String SeenStatus = "";

    public String getSeenStatus() {
        return SeenStatus;
    }

    public void setSeenStatus(String seenStatus) {
        SeenStatus = seenStatus;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String url;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(String type) {
        this.status = type;
    }

    public String getStatus() {
        return status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getURL() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getwarnings() {
        return warnings;
    }

    public void setwarnings(String warnings) {
        this.warnings = warnings;
    }

    public void setSeentype(String seentype){
        this.seentype=seentype;

    }

    public String getSeentype(){
        return seentype;
    }
}
