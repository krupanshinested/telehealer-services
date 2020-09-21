package com.thealer.telehealer.views.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.QuickLoginBroadcastReceiver;
import com.thealer.telehealer.views.quickLogin.QuickLoginActivity;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 04,December,2018
 */
public class OrdersBaseFragment extends BaseFragment {
    public int authResponse;
    private ChangeTitleInterface changeTitleInterface;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeTitleInterface = (ChangeTitleInterface) getActivity();
    }

    public void setTitle(String title) {
        changeTitleInterface.onTitleChange(title);
    }

    private QuickLoginBroadcastReceiver quickLoginBroadcastReceiver = new QuickLoginBroadcastReceiver() {
        @Override
        public void onQuickLogin(int status) {
            if (status == ArgumentKeys.AUTH_FAILED) {
                showAlertDialog(getActivity(), "Validation Failed", "User validation failed cannot process the current order", "OK", null,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, null);
            } else {
                authResponse = status;
            }
        }
    };

    public void showQuickLogin() {
        startActivity(new Intent(getActivity(), QuickLoginActivity.class));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(quickLoginBroadcastReceiver, new IntentFilter(getString(R.string.quick_login_broadcast_receiver)));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(quickLoginBroadcastReceiver);
    }

}
