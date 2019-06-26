package com.thealer.telehealer.common.pubNub;

import androidx.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNPushType;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.push.PNPushAddChannelResult;
import com.pubnub.api.models.consumer.push.PNPushRemoveAllChannelsResult;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.models.Pubnub.PubNubViewModel;
import com.thealer.telehealer.common.Config;
import com.thealer.telehealer.common.Util.InternalLogging.TeleLogExternalAPI;
import com.thealer.telehealer.common.Util.InternalLogging.TeleLogger;
import com.thealer.telehealer.common.pubNub.models.PushPayLoad;
import com.thealer.telehealer.views.common.SuccessViewInterface;

import java.util.Collections;
import java.util.HashMap;

/**
 * Created by rsekar on 12/25/18.
 */

public class PubnubUtil {
    public static PubnubUtil shared = new PubnubUtil();
    private PubNub pubnub;
    private PubNub voipPubnub;

    private PubnubUtil() {
        //punnub configuration

        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setPublishKey(Config.getPubNubPublisherKey());
        pnConfiguration.setSubscribeKey(Config.getPubNubSubscriberKey());
        pubnub = new PubNub(pnConfiguration);
        pubnub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {

            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });

        PNConfiguration voipConfiguration = new PNConfiguration();
        voipConfiguration.setPublishKey(Config.getVoipPublisherKey());
        voipConfiguration.setSubscribeKey(Config.getVoipSubscriberKey());
        voipPubnub = new PubNub(voipConfiguration);
        voipPubnub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {

            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });
    }

    void grantPubNub(String token, String channelName) {
        PubNubViewModel pubNubViewModel = new PubNubViewModel(TeleHealerApplication.application);
        pubNubViewModel.grantPubNub(channelName, token);
    }

    public void enablePushOnChannel(String token, String channelName) {
        unsubscribePush(new SuccessViewInterface() {
            @Override
            public void onSuccessViewCompletion(boolean success) {
                Log.d("PubnubUtil", "enablePushOnChannel " + channelName);
                pubnub.addPushNotificationsOnChannels()
                        .pushType(PNPushType.GCM)
                        .channels(Collections.singletonList(channelName))
                        .deviceId(token)
                        .async(new PNCallback<PNPushAddChannelResult>() {
                            @Override
                            public void onResponse(PNPushAddChannelResult result, PNStatus status) {
                                if (status.isError()) {
                                    Log.d("PubnubUtil", "Error on push notification" + status.getErrorData());

                                    HashMap<String, String> detail = new HashMap<>();
                                    detail.put("status", "fail");
                                    detail.put("reason", status.getErrorData().getInformation());
                                    detail.put("event", "registerForPushOn");

                                    TeleLogger.shared.log(TeleLogExternalAPI.pubnub, detail);

                                } else {
                                    Log.d("PubnubUtil", "Push notification added ");

                                    HashMap<String, String> detail = new HashMap<>();
                                    detail.put("status", "success");
                                    detail.put("event", "registerForPushOn");

                                    TeleLogger.shared.log(TeleLogExternalAPI.pubnub, detail);
                                }
                            }
                        });
            }
        });
    }

    public void enableVoipOnChannel(String token, String channelName) {
        unsubscribeVoip(new SuccessViewInterface() {
            @Override
            public void onSuccessViewCompletion(boolean success) {
                Log.d("PubnubUtil", "enableVoipOnChannel " + channelName);
                voipPubnub.addPushNotificationsOnChannels()
                        .pushType(PNPushType.GCM)
                        .channels(Collections.singletonList(channelName))
                        .deviceId(token)
                        .async(new PNCallback<PNPushAddChannelResult>() {
                            @Override
                            public void onResponse(PNPushAddChannelResult result, PNStatus status) {
                                if (status.isError()) {
                                    Log.d("PubnubUtil", "Error on voip notification" + status.getErrorData());

                                    HashMap<String, String> detail = new HashMap<>();
                                    detail.put("status", "fail");
                                    detail.put("reason", status.getErrorData().getInformation());
                                    detail.put("event", "registerForVoip");

                                    TeleLogger.shared.log(TeleLogExternalAPI.pubnub, detail);

                                } else {
                                    Log.d("PubnubUtil", "voip notification added ");

                                    HashMap<String, String> detail = new HashMap<>();
                                    detail.put("status", "success");
                                    detail.put("event", "registerForVoip");

                                    TeleLogger.shared.log(TeleLogExternalAPI.pubnub, detail);
                                }
                            }
                        });
            }
        });
    }

    public void publishPushMessage(PushPayLoad pushPayLoad, @Nullable PubNubResult resultPNCallback) {
        Log.d("Pubnub Util", "publishPushMessage: " + new Gson().toJson(pushPayLoad));
        pubnub.publish()
                .message(pushPayLoad)
                .channel(pushPayLoad.getPn_apns().getTo())
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {

                        if (!status.isError()) {
                            System.out.println("pub timetoken: " + result.getTimetoken());
                        } else {
                            HashMap<String, String> detail = new HashMap<>();
                            detail.put("status", "fail");
                            detail.put("reason", status.getErrorData().getInformation());
                            detail.put("event", "publishPushMessage");

                            TeleLogger.shared.log(TeleLogExternalAPI.pubnub, detail);
                        }

                        if (resultPNCallback != null) {
                            resultPNCallback.didSend(!status.isError());
                        }
                    }
                });
    }

    public void publishVoipMessage(PushPayLoad pushPayLoad, @Nullable PubNubResult resultPNCallback) {
        Gson gson = new Gson();
        Log.d("PubnubUtil", gson.toJson(pushPayLoad));

        voipPubnub.publish()
                .message(pushPayLoad)
                .channel(pushPayLoad.getPn_apns().getTo())
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {

                        if (!status.isError()) {
                            System.out.println("pub timetoken: " + result.getTimetoken());
                        } else {
                            HashMap<String, String> detail = new HashMap<>();
                            detail.put("status", "fail");
                            detail.put("reason", status.getErrorData().getInformation());
                            detail.put("event", "publishVoipMessage");

                            TeleLogger.shared.log(TeleLogExternalAPI.pubnub, detail);
                        }

                        if (resultPNCallback != null) {
                            resultPNCallback.didSend(!status.isError());
                        }
                    }
                });
    }

    public void unsubscribe() {
        unsubscribeVoip(null);
        unsubscribePush(null);
    }

    private void unsubscribeVoip(@Nullable SuccessViewInterface successViewInterface) {
        Log.d("PubnubUtil", "unsubscribeVoip");

        voipPubnub.removeAllPushNotificationsFromDeviceWithPushToken()
                .deviceId(TelehealerFirebaseMessagingService.getCurrentToken())
                .pushType(PNPushType.GCM).async(new PNCallback<PNPushRemoveAllChannelsResult>() {
            @Override
            public void onResponse(PNPushRemoveAllChannelsResult result, PNStatus status) {
                if (!status.isError()) {
                    System.out.println("Voip Successfully unscubscribed");
                }
                if (successViewInterface != null)
                    successViewInterface.onSuccessViewCompletion(true);
            }
        });

    }

    private void unsubscribePush(@Nullable SuccessViewInterface successViewInterface) {
        Log.d("PubnubUtil", "unsubscribePush");

        pubnub.removeAllPushNotificationsFromDeviceWithPushToken()
                .deviceId(TelehealerFirebaseMessagingService.getCurrentToken())
                .pushType(PNPushType.GCM).async(new PNCallback<PNPushRemoveAllChannelsResult>() {
            @Override
            public void onResponse(PNPushRemoveAllChannelsResult result, PNStatus status) {
                if (!status.isError()) {
                    System.out.println("push Successfully unscubscribed");
                }

                if (successViewInterface != null)
                    successViewInterface.onSuccessViewCompletion(true);
            }
        });

    }


}
