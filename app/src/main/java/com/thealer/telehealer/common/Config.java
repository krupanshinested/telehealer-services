package com.thealer.telehealer.common;

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
            return "pub-c-44175757-ac32-4f0d-8084-584e366e9bb8";
        } else {
            return "pub-c-fcaae6eb-5eba-4d72-a119-3513eda312bd";
        }
    }

    public static String getVoipSubscriberKey() {
        if (BuildConfig.BUILD_TYPE.equals(Constants.BUILD_TYPE_DEBUG)) {
            return "sub-c-9588e6e8-e4ce-11e9-9f1b-ce77373a3518";
        } else {
            return "sub-c-e160986a-eb75-11e9-ad72-8e6732c0d56b";
        }
    }

}
