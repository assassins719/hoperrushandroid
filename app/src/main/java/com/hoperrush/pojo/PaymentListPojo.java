package com.hoperrush.pojo;

import java.io.Serializable;

/**
 * Casperon Technology on 1/23/2016.
 */
public class PaymentListPojo implements Serializable {
    private String paymentName, paymentCode;

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }
}
