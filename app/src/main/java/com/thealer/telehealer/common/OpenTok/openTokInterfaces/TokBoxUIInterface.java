package com.thealer.telehealer.common.OpenTok.openTokInterfaces;

import com.thealer.telehealer.apilayer.models.OpenTok.OpenTokViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;

/**
 * Created by rsekar on 12/25/18.
 */

public interface TokBoxUIInterface {
    void startedCall();
    void didEndCall(String callRejectionReason);
    void updateCallInfo(String message);
    void didUpdatedPatientLocation(String state);
    void didReceivedOtherUserDetails(CommonUserApiResponseModel commonUserApiResponseModel);
    void receivedRequestForVideoSwap();
    void receivedResponseForVideoSwap(Boolean isAccepted );
    void updateCallQuality(String  callQuality);
    void updateVideoQuality(String qualityString,Boolean isMuted);
    void didSubscribeVideoDisabled();
    void didSubscribeAudioDisabled();
    void didSubscribeVideoEnabled();
    void didSubscribeAudioEnabled();
    void didReceiveVitalData(String data,String type);
    void didChangedAudioInput(int type);
    void assignTokBoxApiViewModel(OpenTokViewModel openTokViewModel);

    void bluetoothMediaAction(Boolean forEnd);

    String getCurrentCallQuality();
}
