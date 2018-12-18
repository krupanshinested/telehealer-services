package com.thealer.telehealer.views.home.schedules;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;

/**
 * Created by Aswin on 19,December,2018
 */
public class PatientDisclaimerFragment extends BaseFragment {
    private TextView disclaimerTv;
    private Button continueBtn;

    private ShowSubFragmentInterface showSubFragmentInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_disclaimer, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        disclaimerTv = (TextView) view.findViewById(R.id.disclaimer_tv);
        continueBtn = (Button) view.findViewById(R.id.continue_btn);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAppointmentFragment createAppointmentFragment = new CreateAppointmentFragment();
                createAppointmentFragment.setArguments(getArguments());
                showSubFragmentInterface.onShowFragment(createAppointmentFragment);
            }
        });

        disclaimerTv.setText(Html.fromHtml("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "<h3><font color=\"red\">DISCLAIMER:</font></h3>\n" +
                "<P><font color=\"red\">DO NOT USE THIS FORM IF YOU ARE SEEKING URGENT OR EMERGENCY MEDICAL ATTENTION.</font></p>\n" +
                "<p><font color=\"black\">If you are experiencing chest pain, sudden or serious pain, bleeding or other serious complications, call</font> <font color=\"red\">911</font><font color=\"black\"> or go to the nearest emergency room.<br> If you have feelings of wanting to harm yourself or someone else, call</font> <font color=\"red\">911</font><font color=\"black\"> or the Suicide Prevention Crisis Line at <b>1-800-784-2433</b>.<br>By scheduling this appointment you acknowledge that you are not experiencing an emergency.</font></p>\n" +
                "<p><b><font color=\"black\">By clicking the Continue button I acknowledge that I have read and understand the above disclaimer</font></b></p>\n" +
                "</body>\n" +
                "</html>\n"));
    }
}
