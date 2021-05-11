package com.thealer.telehealer.views.inviteUser;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnOrientationChangeInterface;

public class SendInvitationFragment extends BaseFragment implements View.OnClickListener {

    private OnCloseActionInterface onCloseActionInterface;
    private AttachObserverInterface attachObserverInterface;
    private OnOrientationChangeInterface onOrientationChangeInterface;
    private ChangeTitleInterface changeTitleInterface;
    private CustomButton patientCb, saCb, providerCb;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;

    public SendInvitationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        onOrientationChangeInterface = (OnOrientationChangeInterface) getActivity();
        changeTitleInterface = (ChangeTitleInterface) context;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_invitation, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        patientCb = view.findViewById(R.id.patient_cb);
        saCb = view.findViewById(R.id.sa_cb);
        providerCb = view.findViewById(R.id.provider_cb);

        patientCb.setOnClickListener(this);
        saCb.setOnClickListener(this);
        providerCb.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Bundle inviteBundle=new Bundle();
        switch (v.getId()) {
            case R.id.patient_cb:
                inviteBundle.putString(ArgumentKeys.ROLE,Constants.ROLE_PATIENT);
                Utils.showInviteAlert(getActivity(), inviteBundle);
                break;
            case R.id.sa_cb:
                inviteBundle.putString(ArgumentKeys.ROLE, Constants.ROLE_ASSISTANT);
                Utils.showInviteAlert(getActivity(), inviteBundle);
                break;
            case R.id.provider_cb:
                inviteBundle.putString(ArgumentKeys.ROLE,Constants.ROLE_DOCTOR);
                Utils.showInviteAlert(getActivity(), inviteBundle);
                break;
        }
    }
}