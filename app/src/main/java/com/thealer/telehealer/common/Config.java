package com.thealer.telehealer.common;

import android.util.Log;

import com.thealer.telehealer.BuildConfig;

/**
 * Created by rsekar on 12/25/18.
 */

public class Config {

    public static String getPubNubPublisherKey() {
        if (BuildConfig.BUILD_TYPE.equals(Constants.BUILD_TYPE_DEBUG)) {
            return "pub-c-0040dcb7-ad4d-4fbf-97f3-2b6bd6b6e864";
        } else {
            return "pub-c-1de6787a-02d9-40cb-88d5-fd09144d46b6";
        }
    }

    public static String getPubNubSubscriberKey() {
        if (BuildConfig.BUILD_TYPE.equals(Constants.BUILD_TYPE_DEBUG)) {
            return "sub-c-4e2c9826-c201-11e8-91cd-c6d399a24064";
        } else {
            return "sub-c-621f10be-c221-11e8-aa55-5e2faf3b3bb1";
        }
    }

    public static String getVoipPublisherKey() {
        if (BuildConfig.BUILD_TYPE.equals(Constants.BUILD_TYPE_DEBUG)) {
            return "pub-c-d72c3071-a868-4bcc-892e-582937842a38";
        } else {
            return "pub-c-a2f1b7c1-77e3-4c82-a819-696c80bec3d4";
        }
    }

    public static String getVoipSubscriberKey() {
        if (BuildConfig.BUILD_TYPE.equals(Constants.BUILD_TYPE_DEBUG)) {
            return "sub-c-eddb7d5a-c220-11e8-a085-86a86c9890cf";
        } else {
            return "sub-c-bad221e2-c221-11e8-a6e8-e682e8ece688";
        }
    }

}
