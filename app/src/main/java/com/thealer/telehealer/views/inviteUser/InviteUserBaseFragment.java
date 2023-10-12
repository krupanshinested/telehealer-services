package com.thealer.telehealer.views.inviteUser;

import android.app.Activity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

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

import java.util.HashSet;
import java.util.Set;

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
        inviteUserApiViewModel = new ViewModelProvider(this).get(InviteUserApiViewModel.class);
        attachObserverInterface.attachObserver(inviteUserApiViewModel);

        inviteUserApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    boolean status = true;
                    String title = getString(R.string.success);
                    String message = getString(R.string.invite_user_success_message);

                    if (baseApiResponseModel instanceof InviteByEmailPhoneApiResponseModel) {
                        InviteByEmailPhoneApiResponseModel inviteByEmailPhoneApiResponseModel = (InviteByEmailPhoneApiResponseModel) baseApiResponseModel;

                        if (inviteByEmailPhoneApiResponseModel.getSuccessCount() == 0 && inviteByEmailPhoneApiResponseModel.getFailureCount() == 1) {
                            status = false;
                            title = getString(R.string.failure);
                            message = inviteByEmailPhoneApiResponseModel.getResultData().get(0).getMessage();

                        } else if (inviteByEmailPhoneApiResponseModel.getSuccessCount() > 0 && inviteByEmailPhoneApiResponseModel.getFailureCount() > 0) {

                            title = getString(R.string.partially_success);

                            Set<String> stringSet = new HashSet<>();
                            if (inviteByEmailPhoneApiResponseModel.getFailureCount() > 0) {
                                for (int i = 0; i < inviteByEmailPhoneApiResponseModel.getResultData().size(); i++) {
                                    if (!inviteByEmailPhoneApiResponseModel.getResultData().get(i).isSuccess()) {
                                        stringSet.add(inviteByEmailPhoneApiResponseModel.getResultData().get(i).getMessage());
                                    }
                                }

                                message = String.format(getString(R.string.invite_contact_error), inviteByEmailPhoneApiResponseModel.getResultData().size(),
                                        inviteByEmailPhoneApiResponseModel.getFailureCount(), stringSet.toString().substring(1, stringSet.toString().length() - 1));
                            }
                        }

                        sendSuccessMessage(status, title, message);
                    } else if (baseApiResponseModel.isSuccess()) {
                        sendSuccessMessage(status, title, message);
                    }
                }
            }
        });

        inviteUserApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    if (errorModel.geterrorCode() == null){
                        if (errorModel.getMessage().contains("User with provided attributes not found")){
                            sendFailureMessage(String.format(getActivity().getString(R.string.demographic_invite_error_not_found),getActivity().getString(R.string.app_name),getActivity().getString(R.string.app_name)));
                        }else if (errorModel.getMessage().contains("User with provided attributes exists")){
                            sendFailureMessage(String.format(getActivity().getString(R.string.demographic_invite_error),errorModel.getMessage()));
                        }else {
                            sendFailureMessage(errorModel.getMessage());
                        }
                    }else if (!errorModel.geterrorCode().isEmpty() && !errorModel.geterrorCode().equals("SUBSCRIPTION")) {
                        sendFailureMessage(errorModel.getMessage());
                    }
                }
            }
        });
    }

    public void showSuccessFragment() {
        showSuccessView(this, RequestID.REQ_SHOW_SUCCESS_VIEW, null);
    }

    private void sendSuccessMessage(boolean status, String title, String message) {
        sendSuccessViewBroadCast(getActivity(), status, title, message);

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
