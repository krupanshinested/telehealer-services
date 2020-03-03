package com.thealer.telehealer.common.pubNub;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNPushType;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNGetStateResult;
import com.pubnub.api.models.consumer.presence.PNSetStateResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.pubsub.PNSignalResult;
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult;
import com.pubnub.api.models.consumer.pubsub.objects.PNMembershipResult;
import com.pubnub.api.models.consumer.pubsub.objects.PNSpaceResult;
import com.pubnub.api.models.consumer.pubsub.objects.PNUserResult;
import com.pubnub.api.models.consumer.push.PNPushAddChannelResult;
import com.pubnub.api.models.consumer.push.PNPushRemoveAllChannelsResult;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.models.Pubnub.PubNubMessage;
import com.thealer.telehealer.apilayer.models.Pubnub.PubNubViewModel;
import com.thealer.telehealer.common.Config;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Util.InternalLogging.TeleLogExternalAPI;
import com.thealer.telehealer.common.Util.InternalLogging.TeleLogger;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.pubNub.models.PushPayLoad;
import com.thealer.telehealer.views.common.SuccessViewInterface;
import com.thealer.telehealer.views.home.chat.GetStateInterface;
import com.thealer.telehealer.views.home.chat.PubnubUserStatusInterface;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rsekar on 12/25/18.
 */

public class PubnubUtil extends SubscribeCallback {
    private static final String TAG = "aswin";
    public static PubnubUtil shared = new PubnubUtil();
    private PubNub pubnub;
    private PubNub voipPubnub;
    public static final int CHAT_STATUS_ACTIVE = 0;
    public static final int CHAT_STATUS_INACTIVE = 1;
    public static final int CHAT_STATUS_BACKGROUND = 2;

    @Nullable
    private SubscribeCallback callback;

    private PubnubUtil() {
        //punnub configuration

        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setPublishKey(Config.getPubNubPublisherKey());
        pnConfiguration.setSubscribeKey(Config.getPubNubSubscriberKey());
        pubnub = new PubNub(pnConfiguration);
        pubnub.addListener(this);

        PNConfiguration voipConfiguration = new PNConfiguration();
        voipConfiguration.setPublishKey(Config.getVoipPublisherKey());
        voipConfiguration.setSubscribeKey(Config.getVoipSubscriberKey());
        voipPubnub = new PubNub(voipConfiguration);
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

                            System.out.println("pub failed : " +status.getErrorData().getInformation());
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

    public void createChatChannel(String toGuid, String userGuid, SubscribeCallback subscribeCallback) {
        subscribe(getChatChannel(toGuid, userGuid));
        this.callback = subscribeCallback;

        setState(CHAT_STATUS_ACTIVE, toGuid, userGuid);
    }

    public void subscribe(String channel) {
        pubnub.subscribe()
                .channels(new ArrayList<>(Collections.singletonList(channel)))
                .execute();
    }

    public void unSubscribe(String channel) {
        pubnub.unsubscribe()
                .channels(new ArrayList<>(Collections.singletonList(channel)))
                .execute();
    }


    private String getChatChannel(String toGuid, String userGuid) {
        List<String> guidList = new ArrayList<>(Arrays.asList(toGuid, userGuid));
        Collections.sort(guidList);
        String channel = guidList.toString().replace(", ", ":").replace('[', ' ').replace(']', ' ').trim();
        Log.e(TAG, "getChatChannel: " + channel);
        return channel;
    }

    public void unSubscribeChatChannel(String toGuid, String userGuid) {
        Log.d("Pubnubutil","unSubscribeChatChannel");
        unSubscribe(getChatChannel(toGuid, userGuid));
        setState(CHAT_STATUS_BACKGROUND, toGuid, userGuid);
    }

    public void setState(int state, String toGuid, String userGuid) {
        Log.d("Pubnubutil","setState");

        String channel = getChatChannel(toGuid, userGuid);
        getState(toGuid,channel,new GetStateInterface() {
            @Override
            public void getStateMap(Map<String, JsonElement> stateMap) {
                if (stateMap == null) {
                    stateMap = new HashMap<>();
                }

                stateMap.put(userGuid, new JsonPrimitive(state));

                Log.e("aswin", "getStateMap: " + stateMap.toString());

                pubnub.setPresenceState()
                        .channels(new ArrayList<>(Collections.singletonList(channel)))
                        .uuid(userGuid)
                        .state(stateMap)
                        .async(new PNCallback<PNSetStateResult>() {
                            @Override
                            public void onResponse(PNSetStateResult result, PNStatus status) {
                                Log.e("aswin", "onResponse: " + result.getState().toString());
                            }
                        });

            }
        });
    }

    private void getState(String uuid,String channel,
                          GetStateInterface getStateInterface) {
        pubnub.getPresenceState()
                .channels(new ArrayList<>(Collections.singletonList(channel)))
                .uuid(uuid)
                .async(new PNCallback<PNGetStateResult>() {
                    @Override
                    public void onResponse(PNGetStateResult result, PNStatus status) {
                        if (result != null) {
                            Log.e("aswin", "onResponse: ! " + result.getStateByUUID().get(channel));
                            getStateInterface.getStateMap(result.getStateByUUID());
                        }
                    }
                });
    }

    public void getUserStatus(String userGuid,
                              PubnubUserStatusInterface statusInterface) {
        String channel = getChatChannel(userGuid, UserDetailPreferenceManager.getUser_guid());
        pubnub.getPresenceState()
                .channels(new ArrayList<>(Collections.singletonList(channel)))
                .uuid(userGuid)
                .async(new PNCallback<PNGetStateResult>() {
                    @Override
                    public void onResponse(PNGetStateResult result, PNStatus status) {
                        if (result != null) {
                            try {
                                Log.e(TAG, "onResponse: " + result.getStateByUUID());
                               int userStatus = result.getStateByUUID().get(channel).getAsJsonObject().get(userGuid).getAsInt();
                                Log.e(TAG, "onResponse: userStatus " + userStatus);
                                statusInterface.userStatus(userStatus);
                            } catch (Exception e) {
                                Log.e(TAG, "onResponse: exception");
                                e.printStackTrace();
                                statusInterface.userStatus(PubnubUtil.CHAT_STATUS_INACTIVE);
                            }
                        }
                    }
                });
    }

    public void sendMessage(String channel,PubNubMessage message) {
        pubnub.publish()
                .message(message)
                .channel(channel)
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        Log.e(TAG, "onResponse: " + status.isError());
                    }
                });
    }

    public void sendPubnubMessage(String toGuid, String message) {
        String channel = getChatChannel(toGuid, UserDetailPreferenceManager.getUser_guid());
        Log.e(TAG, "getStateMap: 1");

        PushPayLoad pushPayLoad = PubNubNotificationPayload.getNewMessagePayload(toGuid,message,UserDetailPreferenceManager.getUser_guid(),Utils.getCurrentUtcDate());

        pubnub.publish()
                .message(pushPayLoad)
                .channel(channel)
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        Log.e(TAG, "onResponse: " + status.isError());
                    }
                });
    }


    //SubscribeCallback
    @Override
    public void status(@NotNull PubNub pubnub, @NotNull PNStatus status) {
        if (callback != null) {
            callback.status(pubnub,status);
        }
    }

    @Override
    public void message(@NotNull PubNub pubnub, @NotNull PNMessageResult message) {
        if (callback != null) {
            callback.message(pubnub,message);
        }
    }

    @Override
    public void presence(@NotNull PubNub pubnub, @NotNull PNPresenceEventResult presence) {
        if (callback != null) {
            callback.presence(pubnub,presence);
        }
    }

    @Override
    public void signal(@NotNull PubNub pubnub, @NotNull PNSignalResult pnSignalResult) {

    }

    @Override
    public void user(@NotNull PubNub pubnub, @NotNull PNUserResult pnUserResult) {

    }

    @Override
    public void space(@NotNull PubNub pubnub, @NotNull PNSpaceResult pnSpaceResult) {

    }

    @Override
    public void membership(@NotNull PubNub pubnub, @NotNull PNMembershipResult pnMembershipResult) {

    }

    @Override
    public void messageAction(@NotNull PubNub pubnub, @NotNull PNMessageActionResult pnMessageActionResult) {

    }
}
