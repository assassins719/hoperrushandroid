package com.hoperrush.pojo;

/**
 * Created by user145 on 1/31/2017.
 */
public class MarkerData {

    private String latitude="";
    private String logintude="";
    private String title="";
    private String id="";
    private String imageurl="";
    private String mini_cost="";
    private String hourly_cost="";
    private String TaskerId="";
    private String rating="";
    private String category_imageurl="";
    private String setAddress="";

    public String getSetAddress() {
        return setAddress;
    }

    public void setSetAddress(String setAddress) {
        this.setAddress = setAddress;
    }
    public void setlatitude(String latitude){
        this.latitude=latitude;
    }
    public void setLogintude(String logintude){
        this.logintude=logintude;
    }
    public void settiltle(String title){
        this.title=title;
    }

    public String getLatitude(){

        return latitude;
    }
    public String getLogintude(){
        return logintude;
    }
    public String getTitle(){
        return title;
    }
    public void setId(String id){
        this.id=id;

    }
    public String getId(){
        return id;
    }

    public void setimageurl(String imageurl){
        this.imageurl=imageurl;
    }

    public String getimageurl(){

        return imageurl;
    }
    public void setMini_cost(String mini_cost){
        this.mini_cost=mini_cost;

    }
    public String getMini_cost(){
        return mini_cost;
    }
    public void setHourly_cost(String hourly_cost){
       this.hourly_cost=hourly_cost;
    }
    public String getHourly_cost(){
        return hourly_cost;
    }

    public String getTaskerId() {
        return TaskerId;
    }

    public void setTaskerId(String taskerId) {
        TaskerId = taskerId;
    }

    public void setRating(String rating){
        this.rating=rating;

    }

    public String getRating(){
        return rating;
    }

    public void setCategory_imageurl(String category_imageurl){
        this.category_imageurl=category_imageurl;

    }

    public String getCategory_imageurl(){
        return category_imageurl;
    }
}
