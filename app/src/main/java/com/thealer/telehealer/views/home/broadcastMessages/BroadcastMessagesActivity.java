package com.thealer.telehealer.views.home.broadcastMessages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiResponseModel;
import com.thealer.telehealer.apilayer.models.chat.BroadCastUserKeyApiResponseModel;
import com.thealer.telehealer.apilayer.models.chat.BroadcastMessageRequestModel;
import com.thealer.telehealer.apilayer.models.chat.ChatApiResponseModel;
import com.thealer.telehealer.apilayer.models.chat.ChatApiViewModel;
import com.thealer.telehealer.apilayer.models.chat.ChatMessageRequestModel;
import com.thealer.telehealer.apilayer.models.chat.PrecannedMessageApiResponse;
import com.thealer.telehealer.apilayer.models.chat.UserKeysApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.Signal.SignalKeyManager;
import com.thealer.telehealer.common.Signal.SignalModels.SignalKey;
import com.thealer.telehealer.common.Signal.SignalUtil.SignalManager;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.home.chat.ChatListAdapter;
import com.thealer.telehealer.views.home.chat.ChatMessageSelectionBottomSheet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;
import static com.thealer.telehealer.apilayer.api.ApiInterface.FILTER_USER_GUID_IN;
import static com.thealer.telehealer.apilayer.api.ApiInterface.NAME;

public class BroadcastMessagesActivity extends BaseActivity implements View.OnClickListener, OnActionCompleteInterface {

    private ChatApiViewModel chatApiViewModel;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private String guidList="";
    private String title="";
    private EditText messageEt;
    private ImageView infoIv,sendIv;
    private  BroadCastUserKeyApiResponseModel broadCastUserKeyApiResponseModel;
    private CustomRecyclerView chatMessageCrv;
    private SignalKey mySignalKey;
    private ArrayList<SignalKey> selectedUserSignalKey=new ArrayList<>();
    private ChatListAdapter chatListAdapter;
    private CommonUserApiResponseModel userModel=new CommonUserApiResponseModel();
    private ArrayList<String> precannedMessages = new ArrayList<>();
    private PrecannedMessageApiResponse precannedMessageApiResponse;
    private List<CommonUserApiResponseModel> selectedUserList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_messages);
        if(getIntent()!=null) {

            selectedUserList =(List<CommonUserApiResponseModel>) getIntent().getSerializableExtra(FILTER_USER_GUID_IN);
            for (int i = 0; i < selectedUserList.size(); i++) {
                CommonUserApiResponseModel currentUser = selectedUserList.get(i);
                if (i == 0) {
                    guidList = currentUser.getUser_guid();
                    title = currentUser.getFirst_name();
                } else if (i == 1)
                    title = title + ", " + currentUser.getFirst_name();
                else if (i == 2)
                    title = title + ".. more";

                guidList = guidList+","+currentUser.getUser_guid();
            }

        }
        getData();
        initViewModel();
        initView();
    }

    private void getData() {
        String mykeys = appPreference.getString(PreferenceConstants.USER_KEYS);
        if (mykeys != null) {
            UserKeysApiResponseModel userKeysApiResponseModel = new Gson().fromJson(mykeys, UserKeysApiResponseModel.class);
            if (userKeysApiResponseModel != null && userKeysApiResponseModel.getData() != null) {
                mySignalKey = userKeysApiResponseModel.getData();
            } else {
                getUserKey(UserDetailPreferenceManager.getWhoAmIResponse().getUser_guid(), true, true, true);
                return;
            }
        }

    }
    private void getUserKey(String guid, boolean isOwn, boolean isUpdatePreference, boolean isShowProgress) {
        SignalKeyManager
                .getInstance(this, new SignalKeyManager.OnUserKeyReceivedListener() {
                    @Override
                    public void onKeyReceived(UserKeysApiResponseModel userKeysApiResponseModel) {
                        if (userKeysApiResponseModel != null) {
                            if (isOwn) {
                                mySignalKey = userKeysApiResponseModel.getData();
                            }
                        }

                    }
                })
                .getUserKey(guid, isOwn, isUpdatePreference, isShowProgress);
    }
    private void initViewModel() {
        chatApiViewModel = new ViewModelProvider(this).get(ChatApiViewModel.class);
        attachObserver(chatApiViewModel);

        chatApiViewModel.getBroadcastUserKeys(guidList,true);
        getPrecannedMessages();

        chatApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                   if(baseApiResponseModel instanceof BroadCastUserKeyApiResponseModel){
                       broadCastUserKeyApiResponseModel = (BroadCastUserKeyApiResponseModel) baseApiResponseModel;
                       selectedUserSignalKey= broadCastUserKeyApiResponseModel.getData();
                       
                   } else if (baseApiResponseModel instanceof PrecannedMessageApiResponse) {
                        precannedMessageApiResponse = (PrecannedMessageApiResponse) baseApiResponseModel;

                        precannedMessages.clear();
                        precannedMessages.addAll(precannedMessageApiResponse.getMessages());

                        Set<String> messages = new HashSet<>(precannedMessages);
                        appPreference.setStringSet(PreferenceConstants.PRECANNED_MESSAGES, messages);
                    }
                }
            }
        });


    }

    private void initView() {
        appbarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backIv = (ImageView) findViewById(R.id.back_iv);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        messageEt = (EditText) findViewById(R.id.message_et);
        infoIv = (ImageView) findViewById(R.id.info_iv);
        sendIv = (ImageView) findViewById(R.id.send_iv);
        chatMessageCrv = (CustomRecyclerView) findViewById(R.id.chat_message_crv);

        toolbarTitle.setText(title);

        backIv.setOnClickListener(this);
        sendIv.setOnClickListener(this);
        infoIv.setOnClickListener(this);

        chatMessageCrv.setEmptyState(EmptyViewConstants.EMPTY_CHATS);

        LinearLayoutManager linearLayoutManager = chatMessageCrv.getLayoutManager();


        setupChatAdapter();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onBackPressed();
                break;
            case R.id.send_iv:
                if (!messageEt.getText().toString().isEmpty()) {
                    sendBroadcastMessage();
                }
                break;
            case R.id.info_iv:
                openPrecannedMessage();
                break;
        }
    }

    private void setupChatAdapter() {
        chatListAdapter = new ChatListAdapter(this, userModel);
        chatMessageCrv.getRecyclerView().setAdapter(chatListAdapter);
        chatMessageCrv.getSwipeLayout().setEnabled(false);
    }

    private void sendBroadcastMessage() {
        List<BroadcastMessageRequestModel.MessagesBean> messagesBeanList = new ArrayList<>();
        BroadcastMessageRequestModel broadcastMessageRequestModel = new BroadcastMessageRequestModel();
        for(SignalKey userSignalKey:selectedUserSignalKey){
            String myEncryptedMessage = SignalManager.encryptMessage(messageEt.getText().toString(), mySignalKey, mySignalKey);
            String receiverEncryptedMessage = SignalManager.encryptMessage(messageEt.getText().toString(), mySignalKey, userSignalKey);
            BroadcastMessageRequestModel.MessagesBean messagesBean=new BroadcastMessageRequestModel.MessagesBean();
            messagesBean.setReceiver_one_message(receiverEncryptedMessage);
            messagesBean.setSender_message(myEncryptedMessage);
            messagesBean.setTo(userSignalKey.getUser_guid());
            messagesBeanList.add(messagesBean);
        }

        broadcastMessageRequestModel.setMessages(messagesBeanList);
        chatApiViewModel.sendBroadcastMessage(broadcastMessageRequestModel);

        messageEt.setText("");
    }

    private void openPrecannedMessage() {
        ChatMessageSelectionBottomSheet chatMessageSelectionBottomSheet = new ChatMessageSelectionBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(ArgumentKeys.PRECANNED_MESSAGES, precannedMessages);
        chatMessageSelectionBottomSheet.setArguments(bundle);
        chatMessageSelectionBottomSheet.show(getSupportFragmentManager(), chatMessageSelectionBottomSheet.getClass().getSimpleName());
    }

    private void getPrecannedMessages() {
        precannedMessages.addAll(appPreference.getStringSet(PreferenceConstants.PRECANNED_MESSAGES));
        if (precannedMessages.isEmpty()) {
            chatApiViewModel.getPrecannedMessages();
        }
    }

    @Override
    public void onCompletionResult(String string, Boolean success, Bundle bundle) {
        if (success) {
            String selectedMessage = bundle.getString(Constants.SELECTED_ITEM);
            messageEt.setText(selectedMessage);
        }
    }
}