package com.thealer.telehealer.views.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by rsekar on 11/19/18.
 */

public class ResetPasswordFragment extends BaseFragment implements View.OnClickListener {

    private CircleImageView profileView;
    private TextView profileTitle, description;
    private CustomButton okButton;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;

    private OnActionCompleteInterface actionCompleteInterface;
    private OnCloseActionInterface onCloseActionInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        actionCompleteInterface = (OnActionCompleteInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        profileView = view.findViewById(R.id.profile_iv);
        profileTitle = view.findViewById(R.id.profile_title);
        description = view.findViewById(R.id.description_tv);
        okButton = view.findViewById(R.id.ok_btn);
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);

        toolbarTitle.setText(getString(R.string.reset_password));

        Utils.setImageWithGlide(getActivity().getApplicationContext(), profileView, UserDetailPreferenceManager.getWhoAmIResponse().getUser_avatar(), getActivity().getDrawable(R.drawable.profile_placeholder), true, true);

        profileTitle.setText(getString(R.string.hi) + " " + UserDetailPreferenceManager.getUserDisplayName());

        description.setText(getString(R.string.reset_password_string) + " ( " + UserDetailPreferenceManager.getWhoAmIResponse().getPhone() + " )");

        okButton.setOnClickListener(this);
        backIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ok_btn:
                if (actionCompleteInterface != null)
                    actionCompleteInterface.onCompletionResult(RequestID.REQ_PASSWORD_RESET_OTP, true, null);
                break;
            case R.id.back_iv:
                onCloseActionInterface.onClose(false);
                break;
        }
    }

}
