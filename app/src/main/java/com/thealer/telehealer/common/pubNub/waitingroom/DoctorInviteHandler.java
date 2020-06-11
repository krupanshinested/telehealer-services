package com.thealer.telehealer.common.pubNub.waitingroom;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.pubNub.models.APNSPayload;
import com.thealer.telehealer.common.pubNub.models.Patientinfo;

import java.util.ArrayList;
import java.util.List;

public class DoctorInviteHandler extends InviteRoomHandler{

    private String  userGuid;
    private DoctorInviteRoomInterface doctorInviteRoomProtocol;
    private Context context;

    public DoctorInviteHandler(Context context,String userGuid, DoctorInviteRoomInterface doctorInviteRoomProtocol) {
        this.userGuid = userGuid;
        this.context = context;
        this.doctorInviteRoomProtocol = doctorInviteRoomProtocol;
        initalizePubnubhandler();
    }

    //InviteRoomHandler  methods
    @Override
    protected String getChannelName() {
        return userGuid+":waitingRoom";
    }

    @Override
    protected String getUUID() {
        return userGuid;
    }

    @Override
    protected String getDoctorGuuid() {
        return userGuid;
    }

    //Invite Room Handler override methods
    @Override
    void didSubscribersChanged(List<Patientinfo> guestinfo) {
        super.didSubscribersChanged(guestinfo);
        Log.d("DoctorInviteHandler","didSubscribersChanged");
        Log.d("SubscriberChanged",""+guestinfo.size());
        Intent i=new Intent(context.getString(R.string.patient_count_update));
        Bundle bundle = new Bundle();
        bundle.putString(ArgumentKeys.SIZE,""+guestinfo.size());
        bundle.putSerializable(ArgumentKeys.GUEST_INFO_LIST, (ArrayList<Patientinfo>) guestinfo);
        i.putExtras(bundle);
        LocalBroadcastManager.getInstance(context).sendBroadcast(i);
    }

    @Override
    public void didReceiveMessage(APNSPayload message) {
        super.didReceiveMessage(message);
    }

    @Override
    public void changeInState(PNPresenceEventResult state) {
        super.changeInState(state);
    }
}
