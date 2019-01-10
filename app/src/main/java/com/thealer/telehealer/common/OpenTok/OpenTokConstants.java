package com.thealer.telehealer.common.OpenTok;

/**
 * Created by rsekar on 12/26/18.
 */

public class OpenTokConstants {

    //Call Types
    public static final String audio = "audio";
    public static final String video = "video";

    //Call Rejection Reason
    public static final String busyInAnotherLine = "busyInAnotherLine";
    public static final String notPickedUp = "notPickedUp";
    public static final String endCallPressed = "endCallPressed";
    public static final String timedOut = "timedOut";
    public static final String badNetwork = "badNetwork";
    public static final String other = "other";

    //Message Type
    public static final String patient_location = "patient_location";
    public static final String requestForVideoSwap = "requestForVideoSwap";
    public static final String responseForVideoSwap = "responseForVideoSwap";
    public static final String isVideoSwapAccepted = "isVideoSwapAccepted";
    public static final String videoSwapAccepted = "videoSwapAccepted";
    public static final String videoSwapRejected = "videoSwapRejected";
    public static final String audioMuteStatus = "audioMuteStatus";
    public static final String qualityStat = "qualityStat";
    public static final String audioScore = "audioScore";
    public static final String videoScore = "videoScore";
    public static final String totalScore = "totalScore";
    public static final String videoDisabled = "videoDisabled";
    public static final String videoEnabled = "videoEnabled";
    public static final String otherEndBandwidth = "otherEndBandwidth";

    //Call Quality
    public static final String hd = "HD";
    public static final String sd = "SD";
    public static final String poorConnection = "POOR CONNECTION";
    public static final String none = "none";

    //Call State
    public static final int waitingForUserAction = 1; //incoming call
    public static final int incomingCallGoingOn = 2;
    public static final int outGoingCallGoingOn = 3;
    public static final int idle = 4;

    //Audio State
    public static final int headPhones = 1;
    public static final int inEarSpeaker = 2;
    public static final int bluetooth = 3;
    public static final int speaker = 4;

    //Call Feedback
    public static final String ccm = "CCM";
    public static final String rpm = "RPM";
    public static final String bhi = "BHI";

}
