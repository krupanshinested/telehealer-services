package com.thealer.telehealer.common;

import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.TeleHealerApplication;

import config.AppConfig;

/**
 * Created by rsekar on 12/25/18.
 */

public class Config {

    public static String getPubNubPublisherKey() {
        if (BuildConfig.PARENT_APP.equals(AppConfig.PEPTALK_PARENT)) {
            if (BuildConfig.BUILD_TYPE.equals(Constants.BUILD_TYPE_DEBUG)) {
                return "pub-c-5ba71b22-4f5d-45f8-a186-16d6f69b8d35";
            } else {
                return "pub-c-aec8144a-e60e-4963-bbf9-4aa7ced68280";
            }
        } else {
            if (BuildConfig.BUILD_TYPE.equals(Constants.BUILD_TYPE_DEBUG)) {
                return "pub-c-0040dcb7-ad4d-4fbf-97f3-2b6bd6b6e864";
            } else {
                return "pub-c-1de6787a-02d9-40cb-88d5-fd09144d46b6";
            }
        }
    }

    public static String getPubNubSubscriberKey() {
        if (BuildConfig.PARENT_APP.equals(AppConfig.PEPTALK_PARENT)) {
            if (BuildConfig.BUILD_TYPE.equals(Constants.BUILD_TYPE_DEBUG)) {
                return "sub-c-7f774c8c-7227-11ea-bbe3-3ec3e5ef3302";
            } else {
                return "sub-c-a27e7c90-7228-11ea-a7c4-5e95b827fd71";
            }
        } else {
            if (BuildConfig.BUILD_TYPE.equals(Constants.BUILD_TYPE_DEBUG)) {
                return "sub-c-4e2c9826-c201-11e8-91cd-c6d399a24064";
            } else {
                return "sub-c-621f10be-c221-11e8-aa55-5e2faf3b3bb1";
            }
        }
    }

    public static String getVoipPublisherKey() {
        if (BuildConfig.PARENT_APP.equals(AppConfig.PEPTALK_PARENT)) {
            if (BuildConfig.BUILD_TYPE.equals(Constants.BUILD_TYPE_DEBUG)) {
                return "pub-c-55a09178-1e76-4fa5-9511-68172eda23ce";
            } else {
                return "pub-c-025433d9-210f-4b27-919e-1057aaef4281";
            }
        } else {
            if (BuildConfig.BUILD_TYPE.equals(Constants.BUILD_TYPE_DEBUG)) {
                return "pub-c-44175757-ac32-4f0d-8084-584e366e9bb8";
            } else {
                return "pub-c-fcaae6eb-5eba-4d72-a119-3513eda312bd";
            }
        }
    }

    public static String getVoipSubscriberKey() {
        if (BuildConfig.PARENT_APP.equals(AppConfig.PEPTALK_PARENT)) {
            if (BuildConfig.BUILD_TYPE.equals(Constants.BUILD_TYPE_DEBUG)) {
                return "sub-c-e3a69658-7228-11ea-a44c-76a98e4db888";
            } else {
                return "sub-c-722f369a-722a-11ea-a7c4-5e95b827fd71";
            }
        } else {
            if (BuildConfig.BUILD_TYPE.equals(Constants.BUILD_TYPE_DEBUG)) {
                return "sub-c-9588e6e8-e4ce-11e9-9f1b-ce77373a3518";
            } else {
                return "sub-c-e160986a-eb75-11e9-ad72-8e6732c0d56b";
            }
        }
    }


    public static boolean isDev() {
       return TeleHealerApplication.application.getResources().getString(R.string.api_base_url).equals("https://api.dev.telehealer.com/");
    }

}
