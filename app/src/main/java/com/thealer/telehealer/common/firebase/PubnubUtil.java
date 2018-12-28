package com.thealer.telehealer.common.firebase;

import android.util.Log;

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
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.models.Pubnub.PubNubViewModel;
import com.thealer.telehealer.common.Config;
import com.thealer.telehealer.common.firebase.models.PushPayLoad;

import java.util.Collections;

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

    void grantPubNub(String token,String channelName) {
        PubNubViewModel pubNubViewModel = new PubNubViewModel(TeleHealerApplication.application);
        pubNubViewModel.grantPubNub(channelName,token);
    }

    public void enablePushOnChannel(String token,String channelName) {
        Log.d("PubnubUtil","enablePushOnChannel " +channelName);
        pubnub.addPushNotificationsOnChannels()
                .pushType(PNPushType.GCM)
                .channels(Collections.singletonList(channelName))
                .deviceId(token)
                .async(new PNCallback<PNPushAddChannelResult>() {
                    @Override
                    public void onResponse(PNPushAddChannelResult result, PNStatus status) {
                        if (status.isError()) {
                            Log.d("PubnubUtil","Error on push notification" + status.getErrorData());
                        } else {
                            Log.d("PubnubUtil","Push notification added ");
                        }
                    }
                });
    }

    public void enableVoipOnChannel(String token,String channelName) {
        Log.d("PubnubUtil","enableVoipOnChannel " +channelName);
        voipPubnub.addPushNotificationsOnChannels()
                .pushType(PNPushType.GCM)
                .channels(Collections.singletonList(channelName))
                .deviceId(token)
                .async(new PNCallback<PNPushAddChannelResult>() {
                    @Override
                    public void onResponse(PNPushAddChannelResult result, PNStatus status) {
                        if (status.isError()) {
                            Log.d("PubnubUtil","Error on voip notification" + status.getErrorData());
                        } else {
                            Log.d("PubnubUtil","voip notification added ");
                        }
                    }
                });
    }

    public void publishPushMessage(PushPayLoad pushPayLoad) {
        pubnub.publish()
                .message(pushPayLoad)
                .channel(pushPayLoad.getPn_apns().getTo())
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        if(!status.isError()) {
                            System.out.println("pub timetoken: " + result.getTimetoken());
                        }
                        System.out.println("pub status code: " + status.getStatusCode());
                    }
                });
    }

    public void publishVoipMessage(PushPayLoad pushPayLoad) {
       voipPubnub.publish()
                .message(pushPayLoad)
                .channel(pushPayLoad.getPn_apns().getTo())
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        if(!status.isError()) {
                            System.out.println("pub timetoken: " + result.getTimetoken());
                        }
                        System.out.println("pub status code: " + status.getStatusCode());
                    }
                });
    }


}
