package com.thealer.telehealer.views.inviteUser;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.inviteUser.InviteByEmailPhoneApiResponseModel;
import com.thealer.telehealer.apilayer.models.inviteUser.InviteUserApiViewModel;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.pubNub.PubNubNotificationPayload;
import com.thealer.telehealer.common.pubNub.PubnubUtil;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;

/**
 * Created by Aswin on 17,December,2018
 */
public class InviteUserBaseFragment extends BaseFragment {
    public ChangeTitleInterface changeTitleInterface;
    public AttachObserverInterface attachObserverInterface;
    public InviteUserApiViewModel inviteUserApiViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeTitleInterface = (ChangeTitleInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        inviteUserApiViewModel = ViewModelProviders.of(this).get(InviteUserApiViewModel.class);
        attachObserverInterface.attachObserver(inviteUserApiViewModel);

        inviteUserApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof InviteByEmailPhoneApiResponseModel) {
                        InviteByEmailPhoneApiResponseModel inviteByEmailPhoneApiResponseModel = (InviteByEmailPhoneApiResponseModel) baseApiResponseModel;
                        if (inviteByEmailPhoneApiResponseModel.isSuccess()) {
                            sendSuccessMessage();
                            if (inviteByEmailPhoneApiResponseModel.getResultData() != null) {
                                for (int i = 0; i < inviteByEmailPhoneApiResponseModel.getResultData().size(); i++) {
                                    if (inviteByEmailPhoneApiResponseModel.getResultData().get(i).getUser_guid() != null &&
                                            !inviteByEmailPhoneApiResponseModel.getResultData().get(i).getUser_guid().isEmpty()) {
                                        PubnubUtil.shared.publishPushMessage(PubNubNotificationPayload.getConnectionPayload(inviteByEmailPhoneApiResponseModel.getResultData().get(i).getUser_guid()), null);
                                    }
                                }
                            }
                        }
                    } else if (baseApiResponseModel.isSuccess()) {
                        sendSuccessMessage();
                    }
                }
            }
        });

        inviteUserApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    sendFailureMessage(errorModel.getMessage());
                }
            }
        });
    }

    public void showSuccessFragment() {
        showSuccessView(this, RequestID.REQ_SHOW_SUCCESS_VIEW);
    }

    private void sendSuccessMessage() {
        sendSuccessViewBroadCast(getActivity(), true, getString(R.string.success), getString(R.string.invite_user_success_message));

    }

    private void sendFailureMessage(String message) {
        sendSuccessViewBroadCast(getActivity(), false, getString(R.string.failure), message);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == RequestID.REQ_SHOW_SUCCESS_VIEW) {
            getActivity().finish();
        }
    }
}
