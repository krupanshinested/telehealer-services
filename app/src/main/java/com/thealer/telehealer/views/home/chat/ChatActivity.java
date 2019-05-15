package com.thealer.telehealer.views.home.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.OpenTok.CallInitiateModel;
import com.thealer.telehealer.apilayer.models.Pubnub.PubnubChatModel;
import com.thealer.telehealer.apilayer.models.chat.ChatApiResponseModel;
import com.thealer.telehealer.apilayer.models.chat.ChatApiViewModel;
import com.thealer.telehealer.apilayer.models.chat.ChatMessageRequestModel;
import com.thealer.telehealer.apilayer.models.chat.PrecannedMessageApiResponse;
import com.thealer.telehealer.apilayer.models.chat.UserKeysApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.GetUserDetails;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.Signal.SignalKeyManager;
import com.thealer.telehealer.common.Signal.SignalModels.SignalKey;
import com.thealer.telehealer.common.Signal.SignalUtil.SignalManager;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.common.pubNub.PubnubUtil;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.CallPlacingActivity;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 15,May,2019
 */
public class ChatActivity extends BaseActivity implements View.OnClickListener, OnActionCompleteInterface {

    private String userGuid = null, doctorGuid = null;
    private CommonUserApiResponseModel userModel, doctorModel;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private TextView nextTv;
    private ImageView closeIv;
    private ConstraintLayout bottomCl;
    private View viewGrey;
    private EditText messageEt;
    private ImageView infoIv;
    private ImageView sendIv;
    private CustomRecyclerView chatMessageCrv;

    private int page = 1;
    private ChatListAdapter chatListAdapter;

    private ChatApiViewModel chatApiViewModel;
    private ChatApiResponseModel chatApiResponseModel;
    private PrecannedMessageApiResponse precannedMessageApiResponse;
    private ArrayList<String> precannedMessages = new ArrayList<>();

    private SignalKey mySignalKey, userSignalKey;
    boolean isScrollable, isApiRequested, isNextPageAvailable;

    private void initViewModel() {

        chatApiViewModel = ViewModelProviders.of(this).get(ChatApiViewModel.class);
        attachObserver(chatApiViewModel);

        chatApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof ChatApiResponseModel) {
                        chatApiResponseModel = (ChatApiResponseModel) baseApiResponseModel;

                        if (chatApiResponseModel.getCount() > 0) {

                            chatListAdapter.setData(chatApiResponseModel.getResult(), page);

                            if (page == 1 && chatMessageCrv.getLayoutManager().getItemCount() > 2)
                                chatMessageCrv.getRecyclerView().smoothScrollToPosition(chatMessageCrv.getLayoutManager().getItemCount() - 1);

                            chatMessageCrv.showOrhideEmptyState(false);
                        } else {
                            chatMessageCrv.showOrhideEmptyState(true);
                        }

                        isNextPageAvailable = chatApiResponseModel.getNext() != null;
                        chatMessageCrv.hideProgressBar();
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

    private void enablePagination() {
        isApiRequested = false;
        isScrollable = true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initViewModel();
        initView();
    }

    private void initView() {
        appbarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backIv = (ImageView) findViewById(R.id.back_iv);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        nextTv = (TextView) findViewById(R.id.next_tv);
        closeIv = (ImageView) findViewById(R.id.close_iv);
        bottomCl = (ConstraintLayout) findViewById(R.id.bottom_cl);
        viewGrey = (View) findViewById(R.id.view_grey);
        messageEt = (EditText) findViewById(R.id.message_et);
        infoIv = (ImageView) findViewById(R.id.info_iv);
        sendIv = (ImageView) findViewById(R.id.send_iv);
        chatMessageCrv = (CustomRecyclerView) findViewById(R.id.chat_message_crv);

        if (UserType.isUserPatient()) {
            infoIv.setVisibility(View.GONE);
            messageEt.setFocusable(false);
            messageEt.setClickable(true);
            messageEt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPrecannedMessage();
                }
            });
        }
        nextTv.setVisibility(View.GONE);
        setSupportActionBar(toolbar);

        backIv.setOnClickListener(this);
        sendIv.setOnClickListener(this);
        infoIv.setOnClickListener(this);

        chatMessageCrv.setEmptyState(EmptyViewConstants.EMPTY_CHATS);


        LinearLayoutManager linearLayoutManager = chatMessageCrv.getLayoutManager();

        chatMessageCrv.getRecyclerView().setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (isScrollable && isNextPageAvailable) {
                    if (linearLayoutManager.findFirstVisibleItemPosition() == 0) {
                        isScrollable = false;
                        page = page + 1;
                        getPreviousMessages(false);
                        chatMessageCrv.showProgressBar();
                    }
                } else {
                    chatMessageCrv.hideProgressBar();
                }

                chatMessageCrv.updateView();
            }
        });

        chatMessageCrv.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        if (isApiRequested)
                            enablePagination();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });


        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString(ArgumentKeys.USER_GUID) != null) {
                userGuid = getIntent().getExtras().getString(ArgumentKeys.USER_GUID);

                if (userModel == null) {
                    Set<String> set = new HashSet<>();
                    set.add(userGuid);
                    GetUserDetails.getInstance(this)
                            .getDetails(set)
                            .getHashMapMutableLiveData()
                            .observe(this, new Observer<HashMap<String, CommonUserApiResponseModel>>() {
                                @Override
                                public void onChanged(@Nullable HashMap<String, CommonUserApiResponseModel> stringCommonUserApiResponseModelHashMap) {
                                    if (stringCommonUserApiResponseModelHashMap != null) {
                                        if (stringCommonUserApiResponseModelHashMap.containsKey(userGuid)) {
                                            userModel = stringCommonUserApiResponseModelHashMap.get(userGuid);
                                            setToolbarTitle();
                                            getData();
                                            createChannel();
                                        }
                                    }
                                }
                            });
                }
            } else {
                userModel = (CommonUserApiResponseModel) getIntent().getExtras().getSerializable(Constants.USER_DETAIL);
                doctorModel = (CommonUserApiResponseModel) getIntent().getExtras().getSerializable(Constants.DOCTOR_DETAIL);

                if (UserType.isUserAssistant() && doctorModel != null) {
                    doctorGuid = doctorModel.getUser_guid();
                }

                if (userModel != null) {
                    setToolbarTitle();
                    userGuid = userModel.getUser_guid();
                }
                getData();
            }

        }

        getPrecannedMessages();
    }

    private void getPreviousMessages(boolean isShowProgress) {
        if (!isApiRequested) {
            isApiRequested = true;
            chatApiViewModel.getPreviousChat(userGuid, page, isShowProgress);
        }
    }

    private void getPrecannedMessages() {
        precannedMessages.addAll(appPreference.getStringSet(PreferenceConstants.PRECANNED_MESSAGES));
        if (precannedMessages.isEmpty()) {
            chatApiViewModel.getPrecannedMessages();
        }
    }

    private void getData() {
        setupChatAdapter();

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


        getUserKey(userGuid, false, false, true);

    }

    private void getUserKey(String guid, boolean isOwn, boolean isUpdatePreference, boolean isShowProgress) {
        SignalKeyManager.getInstance(this)
                .getUserKey(guid, isOwn, isUpdatePreference, isShowProgress)
                .getUserKeysApiResponseModel().observe(this, new Observer<UserKeysApiResponseModel>() {
            @Override
            public void onChanged(@Nullable UserKeysApiResponseModel userKeysApiResponseModel) {
                if (userKeysApiResponseModel != null) {
                    if (isOwn) {
                        mySignalKey = userKeysApiResponseModel.getData();
                    } else {
                        userSignalKey = userKeysApiResponseModel.getData();
                        chatListAdapter.setSignalKeys(mySignalKey, userSignalKey);

                        getPreviousMessages(true);
                    }
                }
            }
        });
    }

    private void setToolbarTitle() {
        toolbarTitle.setText(userModel.getUserDisplay_name());
    }

    private void setupChatAdapter() {
        chatListAdapter = new ChatListAdapter(this, userModel);
        chatMessageCrv.getRecyclerView().setAdapter(chatListAdapter);
        chatMessageCrv.getSwipeLayout().setEnabled(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!UserType.isUserPatient())
            getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String callType = null;
        switch (item.getItemId()) {
            case R.id.menu_video_call:
                callType = OpenTokConstants.video;
                break;
            case R.id.menu_audio_call:
                callType = OpenTokConstants.audio;
                break;
        }

        String doctorName = UserDetailPreferenceManager.getUserDisplayName();

        if (doctorModel != null) {
            doctorName = doctorModel.getDoctorDisplayName();
        }

        CallInitiateModel callInitiateModel = new CallInitiateModel(userGuid, userModel, doctorGuid, doctorName, null, callType);
        Intent intent = new Intent(this, CallPlacingActivity.class);
        intent.putExtra(ArgumentKeys.CALL_INITIATE_MODEL, callInitiateModel);
        startActivity(intent);


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onBackPressed();
                break;
            case R.id.send_iv:
                if (!messageEt.getText().toString().isEmpty()) {
                    sendMessage();
                }
                break;
            case R.id.info_iv:
                openPrecannedMessage();
                break;
        }
    }

    private void openPrecannedMessage() {
        ChatMessageSelectionBottomSheet chatMessageSelectionBottomSheet = new ChatMessageSelectionBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(ArgumentKeys.PRECANNED_MESSAGES, precannedMessages);
        chatMessageSelectionBottomSheet.setArguments(bundle);
        chatMessageSelectionBottomSheet.show(getSupportFragmentManager(), chatMessageSelectionBottomSheet.getClass().getSimpleName());
    }

    private void sendMessage() {
        ChatMessageRequestModel chatMessageRequestModel = new ChatMessageRequestModel();

        chatMessageRequestModel.setTo(userGuid);

        List<ChatMessageRequestModel.MessagesBean> messagesBeanList = new ArrayList<>();

        ChatMessageRequestModel.MessagesBean messagesBean = new ChatMessageRequestModel.MessagesBean();

        String myEncryptedMessage = SignalManager.encryptMessage(messageEt.getText().toString(), mySignalKey, mySignalKey);
        String receiverEncryptedMessage = SignalManager.encryptMessage(messageEt.getText().toString(), mySignalKey, userSignalKey);

        messagesBean.setReceiver_one_message(receiverEncryptedMessage);
        messagesBean.setSender_message(myEncryptedMessage);

        messagesBeanList.add(messagesBean);

        chatMessageRequestModel.setMessages(messagesBeanList);

        chatMessageCrv.getRecyclerView().smoothScrollToPosition(chatMessageCrv.getLayoutManager().getItemCount());

        chatApiViewModel.sendMessage(chatMessageRequestModel);

        ChatApiResponseModel.ResultBean messageBean = new ChatApiResponseModel.ResultBean(myEncryptedMessage, new ChatApiResponseModel.ResultBean.UserBean(UserDetailPreferenceManager.getUser_guid()));

        chatListAdapter.addMessage(messageBean);

        messageEt.setText("");

    }

    @Override
    public void onCompletionResult(String string, Boolean success, Bundle bundle) {
        if (success) {
            String selectedMessage = bundle.getString(Constants.SELECTED_ITEM);
            messageEt.setText(selectedMessage);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        createChannel();
    }

    private void createChannel() {
        if (userGuid != null)
            PubnubUtil.shared.createChatChannel(userGuid, UserDetailPreferenceManager.getUser_guid(), new SubscribeCallback() {
                @Override
                public void status(PubNub pubnub, PNStatus status) {

                }

                @Override
                public void message(PubNub pubnub, PNMessageResult message) {
                    PubnubChatModel pubnubChatModel = new Gson().fromJson(message.getMessage().toString(), PubnubChatModel.class);

                    if (!pubnubChatModel.getSender_uuid().equals(UserDetailPreferenceManager.getUser_guid())) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addMessage(pubnubChatModel);
                            }
                        });
                    }
                }

                @Override
                public void presence(PubNub pubnub, PNPresenceEventResult presence) {

                }
            });
    }

    private void addMessage(PubnubChatModel pubnubChatModel) {
        ChatApiResponseModel.ResultBean.UserBean userBean = new ChatApiResponseModel.ResultBean.UserBean(pubnubChatModel.getSender_uuid());
        ChatApiResponseModel.ResultBean resultBean = new ChatApiResponseModel.ResultBean(pubnubChatModel.getContent(), pubnubChatModel.getDate(), userBean);
        chatListAdapter.addMessage(resultBean);
        chatMessageCrv.getRecyclerView().smoothScrollToPosition(chatMessageCrv.getLayoutManager().getItemCount());
    }

    @Override
    protected void onStop() {
        super.onStop();
        unSubscribeChannel();
    }

    private void unSubscribeChannel() {
        if (userGuid != null)
            PubnubUtil.shared.unSubscribeChatChannel(userGuid, UserDetailPreferenceManager.getUser_guid());
    }

    @Override
    public void onBackPressed() {
        if (isPreviousActivityAvailable())
            super.onBackPressed();
        else {
            goToHomeActivity();
        }
    }
}
