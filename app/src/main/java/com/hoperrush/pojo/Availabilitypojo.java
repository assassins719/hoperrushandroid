package com.hoperrush.pojo;

/**
 * Created by user145 on 11/7/2016.
 */
public class Availabilitypojo {
    private String days,morning,afternoon,evening;
    private String url;

    private String messageid;
    public String getAfternoon() {
        return afternoon;
    }

    public String getEvening() {
       return evening;
    }

    public String getMorning() {
        return morning;
    }

    public void setEvening(String type) {
        this.evening = type;
    }



    public void setAfternoon(String time) {
        this.afternoon = time;
    }



    public void setMorning(String url) {
        this.morning = url;
    }
    public void setDays(String messageid){
        this.days=messageid;


    }
    public String getDays() {
        return days;
    }


}
