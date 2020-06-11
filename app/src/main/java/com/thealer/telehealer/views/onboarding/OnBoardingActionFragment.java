package com.thealer.telehealer.views.onboarding;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.guestlogin.GuestLoginActivity;
import com.thealer.telehealer.views.signin.SigninActivity;
import com.thealer.telehealer.views.signup.SignUpActivity;

/**
 * Created by Aswin on 10,October,2018
 */
public class OnBoardingActionFragment extends BaseFragment implements View.OnClickListener {

    private TextView onboardingTv,tv_guestlogin;
    private Button signupBtn;
    private Button signinBtn;
    private String[] textList;
    private ConstraintLayout parentView,lay_guestlogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_action, container, false);
        initView(view);
        return view;
    }

    /**
     * This method is used to initialize all the variables with the view param.
     *
     * @param view
     */
    private void initView(View view) {
        onboardingTv = (TextView) view.findViewById(R.id.onboarding_tv);
        tv_guestlogin = (TextView) view.findViewById(R.id.tv_guestlogin);
        signupBtn = (Button) view.findViewById(R.id.signup_btn);
        signinBtn = (Button) view.findViewById(R.id.signin_btn);

        textList = this.getResources().getStringArray(R.array.onboarding_list);
        signinBtn.setOnClickListener(this);
        signupBtn.setOnClickListener(this);
        tv_guestlogin.setOnClickListener(this);
        parentView = (ConstraintLayout) view.findViewById(R.id.parent_view);
        lay_guestlogin = (ConstraintLayout) view.findViewById(R.id.lay_guestlogin);

        if (BuildConfig.FLAVOR_TYPE.equals(Constants.BUILD_PATIENT)) {
            lay_guestlogin.setVisibility(View.VISIBLE);
        }else
            lay_guestlogin.setVisibility(View.GONE);
    }

    /**
     * This method is used to update the textView text with current position of the view pager
     *
     * @param position
     */
    public void setCurrentText(int position) {
        onboardingTv.setText(textList[position]);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signin_btn:
                startActivity(new Intent(getActivity(), SigninActivity.class));
                getActivity().finish();
                break;
            case R.id.signup_btn:
                UserDetailPreferenceManager.deleteAllPreference();
                startActivity(new Intent(getActivity(), SignUpActivity.class));
                getActivity().finish();
                break;
            case R.id.tv_guestlogin:
                UserDetailPreferenceManager.deleteAllPreference();
                startActivity(new Intent(getActivity(), GuestLoginActivity.class));
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
