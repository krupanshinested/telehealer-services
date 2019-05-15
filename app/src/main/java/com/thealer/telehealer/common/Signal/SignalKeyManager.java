package com.thealer.telehealer.common.Signal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.Gson;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.chat.ChatApiViewModel;
import com.thealer.telehealer.apilayer.models.chat.UserKeysApiResponseModel;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.Signal.SignalModels.SignalKey;
import com.thealer.telehealer.common.Signal.SignalUtil.SignalManager;

import org.whispersystems.libsignal.InvalidKeyException;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 04,July,2019
 */
public class SignalKeyManager {
    private static SignalKeyManager signalKeyManager;
    private static ChatApiViewModel chatApiViewModel;
    public MutableLiveData<UserKeysApiResponseModel> userKeysApiResponseModel = new MutableLiveData<>();
    private boolean isOwn, isUpdatePreference, isPostingKey;
    private SignalKey signalKey;

    public MutableLiveData<UserKeysApiResponseModel> getUserKeysApiResponseModel() {
        return userKeysApiResponseModel;
    }

    public void setUserKeysApiResponseModel(MutableLiveData<UserKeysApiResponseModel> userKeysApiResponseModel) {
        this.userKeysApiResponseModel = userKeysApiResponseModel;
    }

    public static SignalKeyManager getInstance(FragmentActivity fragmentActivity) {
        chatApiViewModel = ViewModelProviders.of(fragmentActivity).get(ChatApiViewModel.class);
        chatApiViewModel.baseApiResponseModelMutableLiveData.observe(fragmentActivity, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof UserKeysApiResponseModel) {
                        onDataReceived((UserKeysApiResponseModel) baseApiResponseModel);
                    } else if (signalKeyManager.isPostingKey) {
                        signalKeyManager.isPostingKey = false;
                        UserKeysApiResponseModel userKeysApiResponseModel = new UserKeysApiResponseModel();
                        userKeysApiResponseModel.setData(signalKeyManager.signalKey);
                        onDataReceived(userKeysApiResponseModel);
                    }
                }
            }
        });
        if (signalKeyManager == null)
            signalKeyManager = new SignalKeyManager();

        return signalKeyManager;
    }

    private static void onDataReceived(UserKeysApiResponseModel userKeysApiResponseModel) {
        if (signalKeyManager.isOwn && userKeysApiResponseModel == null) {
            try {
                signalKeyManager.signalKey = SignalManager.generateNewKeys();
                signalKeyManager.isPostingKey = true;
                chatApiViewModel.postUserKeys(signalKeyManager.signalKey, true);
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        } else {
            signalKeyManager.userKeysApiResponseModel.setValue(userKeysApiResponseModel);

            if (signalKeyManager.isUpdatePreference) {
                appPreference.setString(PreferenceConstants.USER_KEYS, new Gson().toJson(userKeysApiResponseModel));
            }
        }
    }

    public SignalKeyManager getUserKey(@NonNull String userGuid, boolean isOwn, boolean isUpdatePreference, boolean isShowProgress) {
        this.isOwn = isOwn;
        this.isUpdatePreference = isUpdatePreference;
        chatApiViewModel.getUserKeys(userGuid, isShowProgress);
        return signalKeyManager;
    }


}
