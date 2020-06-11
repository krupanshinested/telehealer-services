package com.thealer.telehealer.common.pubNub.waitingroom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.pubnub.api.models.consumer.presence.PNSetStateResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Util.Array.ArrayListFilter;
import com.thealer.telehealer.common.Util.Array.ArrayListUtil;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.pubNub.PubNubNotificationPayload;
import com.thealer.telehealer.common.pubNub.PubnubUtil;
import com.thealer.telehealer.common.pubNub.models.APNSPayload;
import com.thealer.telehealer.common.pubNub.models.PatientInvite;
import com.thealer.telehealer.common.pubNub.models.Patientinfo;
import com.thealer.telehealer.common.pubNub.models.PushPayLoad;
import com.thealer.telehealer.common.pubNub.pubinterface.PubNubResultFetcher;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PatientInviteHandler extends InviteRoomHandler{

    private PatientInvite patientInvite;
    private PatientInviteRoomInterface patientInviteRoomInterface;
    private Context context;
    private boolean isKickoutcalled=false;


    public PatientInviteHandler(Context context,PatientInvite patientInvite, PatientInviteRoomInterface patientInviteRoomInterface) {
        this.patientInvite = patientInvite;
        this.patientInviteRoomInterface = patientInviteRoomInterface;
        LocalBroadcastManager.getInstance(context).registerReceiver(user_Kickout, new IntentFilter(ArgumentKeys.USER_KIKCOUT));
        initalizePubnubhandler();
    }


    public void willAppTerminate() {
        pubnubHandler.unsubscribe();
    }

    public void didAppBecameActive() {
        Patientinfo guestinfo= patientInvite.patientinfo;
        guestinfo.setAvailable(true);
        setState(guestinfo,null);
    }

    public void didAppBecameInActive() {
        Patientinfo guestinfo= patientInvite.patientinfo;
        guestinfo.setAvailable(false);
        setState(guestinfo,null);
    }



    //Pubnub delgate methods
    @Override
    public void didReceiveMessage(APNSPayload message) {
        super.didReceiveMessage(message);
        Log.d("PatientInviteHandler","didReceiveMessage"+message);
        patientInviteRoomInterface.didReceiveMessage(message);
    }

    @Override
    public void changeInState(PNPresenceEventResult state) {
        if (state.getEvent().equalsIgnoreCase("join")) {
            Log.d("changeInState_Join", "PatientInviteHandler");
            if (state.getJoin().contains(getUUID())) {
                Patientinfo guestinfo = patientInvite.patientinfo;
                guestinfo.setAvailable(true);
                setState(guestinfo, null);
            }
            checkLastPushDate();
        }
        patientInviteRoomInterface.changeInState(state);
        super.changeInState(state);
    }

    private void checkLastPushDate() {
        String date= UserDetailPreferenceManager.getJoinedNotficationPushTime(patientInvite.doctorDetails.getUser_guid());
        if (date==null || date.equalsIgnoreCase("")){
            Log.d("checkLastPushDate","nullcase");
            sendNotficationToDoctor();
        }else if (Utils.getDateDifferceinHours(Long.parseLong(date),new Date().getTime())>=1) {
            Log.d("checkLastPushDate", "isOneHourBefore");
            sendNotficationToDoctor();
        }

    }

    private void sendNotficationToDoctor() {
        Log.d("PatientInviteHandler", "sendNotficationToDoctor" + new Date().getTime());
        UserDetailPreferenceManager.setJoinedNotficationPushTime(String.valueOf(new Date().getTime()),patientInvite.doctorDetails.getUser_guid());
        PushPayLoad pushPayLoad = PubNubNotificationPayload.getPatientAdmittedPayload(patientInvite);
        PubnubUtil.shared.publishPushMessage(pushPayLoad, null);
    }

    @Override
    public void didSubscribed() {
        Log.d("SetStateafterSubscribe",""+patientInvite.patientinfo);
        setState(patientInvite.patientinfo, new PubNubResultFetcher() {
            @Override
            public void didresultFetched(PNSetStateResult isresultFetched) {
                fetchAllSubscribedUsers();
                patientInviteRoomInterface.didPostStateVeryFirstTime();
            }
        });

    }

    //InviteRoomHandler  methods
    @Override
    protected String getChannelName(){
        return patientInvite.getwaitingRoomChannel();
    }

    @Override
    protected String getUUID(){
        return patientInvite.patientinfo.getUserGuid();
    }

    @Override
    protected String getDoctorGuuid() {
        return patientInvite.doctorDetails.getUser_guid();
    }

    @Override
    void didSubscribersChanged(List<Patientinfo> guestinfoList) {
        super.didSubscribersChanged(guestinfoList);
        Log.d("PatientInviteHandler","didSubscribersChanged");
        Log.d("guestinfoList",""+guestinfoList.size());

        for (Patientinfo patientinfo:guestinfoList){
            Log.d("didSubscribersChanged",""+patientinfo.getDisplayName());
        }

        int i=1;
        for (Patientinfo guest:guestinfoList){
            if (guest.getUserGuid().equalsIgnoreCase(patientInvite.patientinfo.getUserGuid())){
                patientInviteRoomInterface.didUpdateCurrentPosition(i);
                Log.d("PatientInviteHandler","didUpdateCurrentPosition");
                return;
            }
            i++;
        }

    }

    private BroadcastReceiver user_Kickout = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("WaitingScreenFragment","callKickOut");

            if (!isKickoutcalled)
            patientInviteRoomInterface.kickOut();

            isKickoutcalled=true;
        }
    };

    public  void unRegisterKickout(){
        LocalBroadcastManager.getInstance(context).unregisterReceiver(user_Kickout);
    }

    @Override
    protected ArrayList<Patientinfo> doAdditionalModification(ArrayList<Patientinfo> currentlySubscribedPatients) {
        ArrayListUtil<Patientinfo, Patientinfo> util = new ArrayListUtil<>();
        ArrayList<Patientinfo> filterList = util.filterList(currentlySubscribedPatients, new ArrayListFilter<Patientinfo>() {
            @Override
            public Boolean needToAddInFilter(Patientinfo model) {
                return !patientInvite.getPatientinfo().getUserGuid().equals(model.getUserGuid());
            }
        });

        for (Patientinfo p:currentlySubscribedPatients){
            Log.d("doAdditionalModi",""+p.getDisplayName());
        }

        filterList.add(patientInvite.patientinfo);

        for (Patientinfo p:currentlySubscribedPatients){
            Log.d("doAdditionalModi_af",""+p.getDisplayName());
        }

        return filterList;
    }
}
