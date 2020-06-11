package com.thealer.telehealer.common.pubNub.waitingroom;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNSetStateResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.pubsub.PNSignalResult;
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult;
import com.pubnub.api.models.consumer.pubsub.objects.PNMembershipResult;
import com.pubnub.api.models.consumer.pubsub.objects.PNSpaceResult;
import com.pubnub.api.models.consumer.pubsub.objects.PNUserResult;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.Pubnub.PubNubViewModel;
import com.thealer.telehealer.common.Config;
import com.thealer.telehealer.common.ResultFetcher;
import com.thealer.telehealer.common.pubNub.models.APNSPayload;
import com.thealer.telehealer.common.pubNub.pubinterface.PubNubDelagate;
import com.thealer.telehealer.common.pubNub.pubinterface.PubNubResultFetcher;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class PubnubHandler extends SubscribeCallback{

    private static final String TAG = "PubnubHandler";
    public PubNub pubnub;
    private String channel;
    private PubNubDelagate pubNubDelagate;
    private boolean isChannelpnprescalled=false;

    public PubnubHandler(String channel,String uuid,@Nullable PubNubDelagate pubNubDelagate) {
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setPublishKey(Config.getPubNubPublisherKey());
        pnConfiguration.setSubscribeKey(Config.getPubNubSubscriberKey());
        pnConfiguration.setUuid(uuid);
        pubnub = new PubNub(pnConfiguration);
        this.channel=channel;
        this.pubNubDelagate=pubNubDelagate;
        pubnub.addListener(this);
    }

    //Subscriber callback methods
    @Override
    public void status(@NotNull PubNub pubnub, @NotNull PNStatus pnStatus) {

    }

    @Override
    public void message(@NotNull PubNub pubnub, @NotNull PNMessageResult pnMessageResult) {
        Log.d("PubnubHandler","message"+pnMessageResult.getMessage().toString());
        pubNubDelagate.didReceiveMessage(getApnsPayloadfromMessage(pnMessageResult));
    }

    private APNSPayload getApnsPayloadfromMessage(PNMessageResult pnMessageResult){
        JsonObject root = pnMessageResult.getMessage().getAsJsonObject();
        APNSPayload pubnubChatModel;
        if (root.get("pn_apns") != null) {
            pubnubChatModel = new Gson().fromJson(root.get("pn_apns").getAsJsonObject().toString(), APNSPayload.class);
        } else {
            pubnubChatModel = new Gson().fromJson(root.toString(), APNSPayload.class);
        }
        return  pubnubChatModel;
    }

    private String parseJsonElement(String toString) {
        String msg="";
        try {
            JSONObject jsonObject=new JSONObject(toString);
            if (jsonObject.has("pn_apns")){
               JSONObject pn_apns=jsonObject.getJSONObject("pn_apns");
               if(pn_apns.has("content"))
               msg=pn_apns.getString("content");
                Log.d("PubnubHandler","message"+msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return msg;
    }

    @Override
    public void presence(@NotNull PubNub pubnub, @NotNull PNPresenceEventResult pnPresenceEventResult) {
        Log.d("PubnubHandler","changeInState");
        pubNubDelagate.changeInState(pnPresenceEventResult);
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

    public void subscribe() {
        Log.d("PubnubHandler","subscribe");
        PubNubViewModel pubNubViewModel = new PubNubViewModel(TeleHealerApplication.application);
        pubNubViewModel.grantPubNubAccess(channel, new ResultFetcher() {
            @Override
            public void didFetched(BaseApiResponseModel baseApiResponseModel) {
                if (!baseApiResponseModel.isSuccess()){
                    return;
                }
                pubNubViewModel.grantPubNubAccess(channel+"-pnpres", new ResultFetcher() {
                    @Override
                    public void didFetched(BaseApiResponseModel baseApiResponseModel) {
                        if (!baseApiResponseModel.isSuccess()){
                            return;
                        }
                        pubnub.subscribe()
                                .channels(new ArrayList<>(Collections.singletonList(channel)))
                                .withPresence()
                                .execute();
                        pubNubDelagate.didSubscribed();
                    }
                });

            }
        });

    }

    public void unsubscribe() {
        pubnub.unsubscribe()
                .channels(new ArrayList<>(Collections.singletonList(channel)))
                .execute();
    }

    public void setState(Object state) {
        setState(state,null);
    }

    public void setState(Object state,@Nullable PubNubResultFetcher resultFetcher) {
        pubnub.setPresenceState()
                .channels(Arrays.asList(channel))
                .state(state)
                .async(new PNCallback<PNSetStateResult>() {
                    @Override
                    public void onResponse(final PNSetStateResult result, PNStatus status) {

                        if (resultFetcher!=null)
                            resultFetcher.didresultFetched(result);

                        if (status.isError()) {
                            return;
                        }
                        Log.d("Success Set State Res:",""+result.getState());
                    }
                });
    }


    public void publishMessage(String channel, APNSPayload apnsPayload) {
        pubnub.publish().channel(channel).message(apnsPayload).async(
                new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        try {
                            if (!status.isError()) {
                                Log.v(TAG, "publish"+status);
                            } else {
                                Log.v(TAG, "publishError"+status);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }
}
