package com.hoperrush.pojo;

import java.io.Serializable;

/**
 * Casperon Technology on 1/19/2016.
 */
public class CancelJobPojo implements Serializable
{
    private String reasonId, reason;

    public String getReasonId() {
        return reasonId;
    }

    public void setReasonId(String reasonId) {
        this.reasonId = reasonId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
