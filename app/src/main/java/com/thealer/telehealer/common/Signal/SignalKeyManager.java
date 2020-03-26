package com.thealer.telehealer.common.Signal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.Gson;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.chat.ChatApiViewModel;
import com.thealer.telehealer.apilayer.models.chat.UserKeysApiResponseModel;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.Signal.SignalModels.SignalKey;
import com.thealer.telehealer.common.Signal.SignalModels.SignalKeyPostModel;
import com.thealer.telehealer.common.Signal.SignalUtil.SignalManager;

import org.whispersystems.libsignal.InvalidKeyException;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 04,July,2019
 */
public class SignalKeyManager {
    private static SignalKeyManager signalKeyManager;
    private static ChatApiViewModel chatApiViewModel;
    private boolean isOwn, isUpdatePreference, isPostingKey;
    private SignalKeyPostModel signalKey;
    private OnUserKeyReceivedListener onUserKeyReceivedListener;

    public static SignalKeyManager getInstance(FragmentActivity fragmentActivity, OnUserKeyReceivedListener onUserKeyReceivedListener) {
        chatApiViewModel = new ViewModelProvider(fragmentActivity).get(ChatApiViewModel.class);
        chatApiViewModel.baseApiResponseModelMutableLiveData.observe(fragmentActivity, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof UserKeysApiResponseModel) {
                        onDataReceived((UserKeysApiResponseModel) baseApiResponseModel);
                    } else if (signalKeyManager != null && signalKeyManager.isPostingKey) {
                        signalKeyManager.isPostingKey = false;
                        UserKeysApiResponseModel userKeysApiResponseModel = new UserKeysApiResponseModel();
                        SignalKey signalKey = new SignalKey(signalKeyManager.signalKey.getDevice_id(),
                                signalKeyManager.signalKey.getUser_id(),
                                signalKeyManager.signalKey.getRegistrationId(),
                                signalKeyManager.signalKey.getPreKey(),
                                signalKeyManager.signalKey.getSignedPreKey(),
                                signalKeyManager.signalKey.getIdentityKey(),
                                signalKeyManager.signalKey.getUser_guid(),
                                signalKeyManager.signalKey.getEncryption_key_id());

                        userKeysApiResponseModel.setData(signalKey);
                        onDataReceived(userKeysApiResponseModel);
                    }
                }
            }
        });

        chatApiViewModel.getErrorModelLiveData().observe(fragmentActivity, new Observer<ErrorModel>() {
            @Override
            public void onChanged(ErrorModel errorModel) {
                if (errorModel.getStatusCode() == 404) {
                    postNewKey();
                }
            }
        });

        if (signalKeyManager == null)
            signalKeyManager = new SignalKeyManager();

        signalKeyManager.onUserKeyReceivedListener = onUserKeyReceivedListener;

        return signalKeyManager;
    }

    private static void onDataReceived(UserKeysApiResponseModel userKeysApiResponseModel) {
        if (signalKeyManager.isOwn && userKeysApiResponseModel == null) {
            postNewKey();
        } else {
            signalKeyManager.onUserKeyReceivedListener.onKeyReceived(userKeysApiResponseModel);

            if (signalKeyManager.isUpdatePreference) {
                appPreference.setString(PreferenceConstants.USER_KEYS, new Gson().toJson(userKeysApiResponseModel));
            }
        }
    }

    private static void postNewKey() {
        try {
            signalKeyManager.signalKey = SignalManager.generateNewKeys();
            signalKeyManager.isPostingKey = true;
            chatApiViewModel.postUserKeys(signalKeyManager.signalKey, true);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public SignalKeyManager getUserKey(@NonNull String userGuid, boolean isOwn, boolean isUpdatePreference, boolean isShowProgress) {
        this.isOwn = isOwn;
        this.isUpdatePreference = isUpdatePreference;
        chatApiViewModel.getUserKeys(userGuid, isShowProgress);
        return signalKeyManager;
    }

    public interface OnUserKeyReceivedListener{
        void onKeyReceived(UserKeysApiResponseModel userKeysApiResponseModel);
    }

}
