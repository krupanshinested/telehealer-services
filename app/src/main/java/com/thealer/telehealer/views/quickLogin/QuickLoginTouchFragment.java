package com.thealer.telehealer.views.quickLogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.views.base.BaseFragment;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 31,October,2018
 */
public class QuickLoginTouchFragment extends BaseFragment implements View.OnClickListener {
    private ImageView closeIv;
    private TextView touchIdAgreementTv;
    private CustomButton enableBtn;
    private TextView infoTv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quicklogin_touch, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        closeIv = (ImageView) view.findViewById(R.id.close_iv);
        touchIdAgreementTv = (TextView) view.findViewById(R.id.touch_id_agreement_tv);
        enableBtn = (CustomButton) view.findViewById(R.id.enable_btn);
        infoTv = (TextView) view.findViewById(R.id.info_tv);

        closeIv.setOnClickListener(this);
        enableBtn.setOnClickListener(this);
        touchIdAgreementTv.setOnClickListener(this);

        infoTv.setText(String.format(getString(R.string.touch_info, getString(R.string.app_name))));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.close_iv:
                sendBroadCast();
                EventRecorder.recordRegistration("QuickLogin_skipped", UserDetailPreferenceManager.getUser_guid());
                appPreference.setInt(Constants.QUICK_LOGIN_TYPE, Constants.QUICK_LOGIN_TYPE_NONE);
                break;
            case R.id.enable_btn:
                sendBroadCast();
                EventRecorder.recordRegistration("TouchID_enabled", UserDetailPreferenceManager.getUser_guid());
                appPreference.setInt(Constants.QUICK_LOGIN_TYPE, Constants.QUICK_LOGIN_TYPE_TOUCH);
                break;
            case R.id.touch_id_agreement_tv:
                startActivity(new Intent(getActivity(), QuickLoginAgreementActivity.class));
                break;
        }
    }

    private void sendBroadCast() {
        Bundle bundle = new Bundle();
        bundle.putInt(ArgumentKeys.QUICK_LOGIN_STATUS, ArgumentKeys.QUICK_LOGIN_CREATED);

        LocalBroadcastManager.getInstance(getActivity())
                .sendBroadcast(new Intent(getString(R.string.quick_login_broadcast_receiver))
                        .putExtras(bundle));
    }

}
