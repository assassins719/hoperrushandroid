package com.hoperrush.pojo;

/**
 * Created by user88 on 1/6/2016.
 */
public class Myprofile_Reviwes_Pojo {

    private String name;
    private String profilimg;
    private String rattingimage="";
    public String getRating_time() {
        return rating_time;
    }

    public void setRating_time(String rating_time) {
        this.rating_time = rating_time;
    }

    public String getReviwe_description() {
        return reviwe_description;
    }

    public void setReviwe_description(String reviwe_description) {
        this.reviwe_description = reviwe_description;
    }

    private String rating_time;
    private String reviwe_description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getProfilimg() {
        return profilimg;
    }

    public void setProfilimg(String profilimg) {
        this.profilimg = profilimg;
    }


    public String getratingimage() {
        return rattingimage;
    }

    public void setratingimage(String profilimg) {
        this.rattingimage = profilimg;
    }

    private String rating;
}
