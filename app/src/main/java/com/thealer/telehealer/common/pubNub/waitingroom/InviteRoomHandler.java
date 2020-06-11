package com.thealer.telehealer.common.pubNub.waitingroom;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNHereNowChannelData;
import com.pubnub.api.models.consumer.presence.PNHereNowOccupantData;
import com.pubnub.api.models.consumer.presence.PNHereNowResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.thealer.telehealer.common.Util.Array.ArrayListFilter;
import com.thealer.telehealer.common.Util.Array.ArrayListUtil;
import com.thealer.telehealer.common.pubNub.models.APNSPayload;
import com.thealer.telehealer.common.pubNub.models.Patientinfo;
import com.thealer.telehealer.common.pubNub.pubinterface.PubNubDelagate;
import com.thealer.telehealer.common.pubNub.pubinterface.PubNubResultFetcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InviteRoomHandler implements PubNubDelagate {

    private boolean isFirstFetchForAllSubscribersCompleted=false;
    public PubnubHandler pubnubHandler;
    public ArrayList<Patientinfo> currentlySubscribedPatients=new ArrayList<>();

    public void initalizePubnubhandler(){
        pubnubHandler=new PubnubHandler(getChannelName(),getUUID(),this);
    }

    protected String getChannelName() {
        return "";
    }

    protected String getUUID() {
        return "";
    }

    protected String getDoctorGuuid() {
        return "";
    }


    void didSubscribersChanged(List<Patientinfo> guestinfo) {
        Log.d("InviteRoomHandler","didSubscribersChanged");
    }


    //start subscribe
    public void startToSubscribe() {
        pubnubHandler.subscribe();
    }

    //assign state
    public void setState(Patientinfo guestinfo, @Nullable PubNubResultFetcher resultFetcher){
        pubnubHandler.setState(guestinfo,resultFetcher);
    }

    //get all subscriber list
    public void fetchAllSubscribedUsers(){
        Log.d("InviteRoomHandler","fetchAllSubscribedUsers");
        Log.d("InviteRoomHandler",""+getChannelName());
        pubnubHandler.pubnub.hereNow()
                .channels(new ArrayList<>(Collections.singletonList(getChannelName())))
                .includeUUIDs(true)
                .includeState(true)
                .async(new PNCallback<PNHereNowResult>() {
                    @Override
                    public void onResponse(PNHereNowResult result, PNStatus status) {
                        if (status.isError()) {
                            Log.d("fetchAllSubscribedUsers","Error");
                            currentlySubscribedPatients.clear();
                            didSubscribersChanged(currentlySubscribedPatients);
                            return;
                        }

                        currentlySubscribedPatients.clear();
                        for (PNHereNowChannelData channelData : result.getChannels().values()) {
                            System.out.println("channel:" + channelData.getChannelName());
                            System.out.println("occupancy: " + channelData.getOccupancy());
                            for (PNHereNowOccupantData occupant : channelData.getOccupants()) {
                                System.out.println("uuid: " + occupant.getUuid() + " state: " + occupant.getState());

                                if (hasJoinDate(occupant))
                                currentlySubscribedPatients.add(getInfo(occupant));

                            }
                        }
                        isFirstFetchForAllSubscribersCompleted=true;

                        sortPatientList();

                        for (Patientinfo patientinfo: currentlySubscribedPatients){
                            Log.d("fetchAllSubscribedUsers",""+patientinfo.getDisplayName());
                        }

                        Log.d("currentlySubscribedPat","Size"+currentlySubscribedPatients.size());
                        didSubscribersChanged(currentlySubscribedPatients);
                    }
                });
    }


    //get guestinfo from PNHereNowOccupantData
    private Patientinfo getInfo(@Nullable PNHereNowOccupantData occupant) {
        Patientinfo guestinfo = null;
        Gson gson = new Gson();
        guestinfo = gson.fromJson(occupant.getState(), Patientinfo.class);
       return guestinfo;
    }

    private boolean hasJoinDate(@Nullable PNHereNowOccupantData occupant) {
        Patientinfo guestinfo = null;
        Gson gson = new Gson();
        guestinfo = gson.fromJson(occupant.getState(), Patientinfo.class);

        if(guestinfo!=null && guestinfo.getJoinedDate()!=null)
            return true;
        else
            return false;
    }




    //PubNubDelagate override methods
    @Override
    public void didSubscribed() {
        Log.d("InviteRoomHandler","didSubscribed");
        fetchAllSubscribedUsers();
    }

    @Override
    public void didReceiveMessage(APNSPayload message) {
        Log.d("InviteRoomHandler","didReceiveMessage");
    }

    @Override
    public void changeInState(PNPresenceEventResult state) {
        Log.d("InviteRoomHandler","changeInState  "+state.getEvent());
        switch (state.getEvent()){
            case "join":
                Log.d("changeInState","Join");
                break;

            case "leave":
            case "state-change":
                Log.d("changeInState","state-change");

                //get pateitn info
                Patientinfo guestinfo=getInfoChangeState(state);

                //filter patent list
                ArrayListUtil<Patientinfo, Patientinfo> util = new ArrayListUtil<>();
                ArrayList<Patientinfo> filterList = util.filterList(currentlySubscribedPatients, new ArrayListFilter<Patientinfo>() {
                    @Override
                    public Boolean needToAddInFilter(Patientinfo model) {
                        if (guestinfo != null) {
                            Log.d("guestinfo!=null","true");
                            return !guestinfo.getUserGuid().equals(model.getUserGuid());
                        } else {
                            Log.d("guestinfo!=null","false");
                            return true;
                        }
                    }
                });
                Log.d("filterList",""+filterList.size());

                //if state change remove user from list and add user again
                if (state.getEvent().equalsIgnoreCase("state-change")) {
                    Log.d("Useradded","state-change");
                    if (guestinfo != null)
                        filterList.add(guestinfo);
                }
                Log.d("filterList_after",""+filterList.size());
                Log.d("currentlySubscri_before",""+currentlySubscribedPatients.size());
                currentlySubscribedPatients=new ArrayList<>();
                currentlySubscribedPatients.addAll(filterList);
                Log.d("currentlySubscri_after",""+currentlySubscribedPatients.size());

                for (Patientinfo patientinfo:currentlySubscribedPatients){
                    Log.d("before_sort",""+patientinfo.getDisplayName());
                }

                //sort pateitn list based on date
                sortPatientList();

                for (Patientinfo patientinfo:currentlySubscribedPatients){
                    Log.d("after_sort",""+patientinfo.getDisplayName());
                }

                didSubscribersChanged(currentlySubscribedPatients);
                break;

            case "interval":
                fetchAllSubscribedUsers();
                break;

            case "timeout":
                fetchAllSubscribedUsers();
                break;
        }
    }



    //get guestinfo from PNPresenceEventResult (Change State response)
    private Patientinfo getInfoChangeState(@Nullable PNPresenceEventResult state) {
        Patientinfo guestinfo = null;
        Gson gson = new Gson();
        guestinfo = gson.fromJson(state.getState(), Patientinfo.class);

        if (guestinfo!=null)
        Log.d("getInfoChangeState",""+guestinfo.toString());
        return guestinfo;
    }

    private boolean hasJoindateState(@Nullable PNPresenceEventResult state) {
        Patientinfo guestinfo = null;
        Gson gson = new Gson();
        guestinfo = gson.fromJson(state.getState(), Patientinfo.class);
        if(guestinfo!=null && guestinfo.getJoinedDate()!=null)
            return true;
        else
            return false;
    }


    //unsubscribe
    public void unsubscribe() {
        pubnubHandler.unsubscribe();
    }

    //sort patient using joined date
    public void sortPatientList(){

        currentlySubscribedPatients=doAdditionalModification(currentlySubscribedPatients);

        Collections.sort(currentlySubscribedPatients, new Comparator<Patientinfo>() {
            @Override
            public int compare(Patientinfo lhs, Patientinfo rhs) {
                return Long.compare(lhs.getJoinedDate(),rhs.getJoinedDate());
            }
        });
    }

    protected ArrayList<Patientinfo> doAdditionalModification(ArrayList<Patientinfo> currentlySubscribedPatients) {
        return currentlySubscribedPatients;
    }

}
