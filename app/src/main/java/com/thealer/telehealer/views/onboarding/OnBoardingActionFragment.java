package com.thealer.telehealer.views.onboarding;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.signin.SigninActivity;
import com.thealer.telehealer.views.signup.SignUpActivity;

/**
 * Created by Aswin on 10,October,2018
 */
public class OnBoardingActionFragment extends BaseFragment implements View.OnClickListener {

    private TextView onboardingTv;
    private Button signupBtn;
    private Button signinBtn;
    private String[] textList;
    private ConstraintLayout parentView;

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
        signupBtn = (Button) view.findViewById(R.id.signup_btn);
        signinBtn = (Button) view.findViewById(R.id.signin_btn);

        textList = this.getResources().getStringArray(R.array.onboarding_list);
        signinBtn.setOnClickListener(this);
        signupBtn.setOnClickListener(this);
        parentView = (ConstraintLayout) view.findViewById(R.id.parent_view);

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
                startActivity(new Intent(getActivity(), SignUpActivity.class));
                getActivity().finish();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
