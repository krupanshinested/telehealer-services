package com.thealer.telehealer.views.home.orders;

import com.thealer.telehealer.R;

/**
 * Created by Aswin on 23,November,2018
 */
public class OrderStatus {

    public static final String STATUS_ISSUED = "issued";
    public static final String STATUS_CANCELLED = "canceled";
    public static final String STATUS_FAILED = "failed";
    public static final String STATUS_BUSY = "busy";
    public static final String STATUS_QUEUED = "queued";
    public static final String STATUS_DELIVERED = "delivered";

    public static int getStatusImage(String status) {
        switch (status) {
            case STATUS_DELIVERED:
                return R.drawable.ic_status_success;
            case STATUS_CANCELLED:
            case STATUS_FAILED:
                return R.drawable.ic_status_failed;
            case STATUS_QUEUED:
            case STATUS_ISSUED:
            case STATUS_BUSY:
                return R.drawable.ic_status_pending;
            default:
                return 0;
        }
    }
}
