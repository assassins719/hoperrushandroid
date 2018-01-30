package com.hoperrush.pojo;

/**
 * Created by user146 on 10/4/2016.
 */
public class PaymentFareSummeryPojo {
    private String payment_title;
    private String minimumcost="",totalhours="",freehours="",remaininghours="",totalamouint="",servicetax="",grandtotal="";
    private String jobid="";
    private String currencycode="";

    public String getPayment_amount() {
        return payment_amount;
    }

    public void setPayment_amount(String payment_amount) {
        this.payment_amount = payment_amount;
    }

    public String getPayment_title() {
        return payment_title;
    }

    public void setPayment_title(String payment_title) {
        this.payment_title = payment_title;
    }

    private String payment_amount;



    public void setMinimumcost(String minimumcost) {
        this.minimumcost = minimumcost;
    }

public void setCurrencycode(String currencycode){
    this.currencycode=currencycode;

}

    public String getCurrencycode() {
        return currencycode;
    }



    public void setTotalhours(String totalhours) {
        this.totalhours = totalhours;
    }



    public void setFreehours(String freehours) {
        this.freehours = freehours;
    }


    public void setTotalamouint(String totalamouint) {
        this.totalamouint = totalamouint;
    }


    public void setServicetax(String servicetax) {
        this.servicetax = servicetax;
    }




    public void setGrandtotal(String grandtotal) {
        this.grandtotal = grandtotal;
    }




    public void setJobid(String jobid) {
        this.jobid = jobid;
    }


}
