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

        String disclaimerHtml = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "<h3><font color=\"red\">%s</font></h3>\n" +
                "<P><font color=\"red\">%s</font></p>\n" +
                "<p><font color=\"black\">%s</font>" +
                "<font color=\"red\">%s</font>" +
                "<font color=\"black\"> %s<br> %s</font>" +
                "<font color=\"red\">%s</font>" +
                "<font color=\"black\"> %s <b>%s</b>.<br>%s</font></p>\n" +
                "<p><b><font color=\"black\">%s</font></b></p>\n" +
                "</body>\n" +
                "</html>\n";

        disclaimerTv.setText(Html.fromHtml(String.format(disclaimerHtml, getString(R.string.html_disclaimer),
                getString(R.string.html_do_not_use),
                getString(R.string.html_experiencing_chest_pain),
                getString(R.string.html_911),
                getString(R.string.html_go_to_nearest_emergency_room),
                getString(R.string.html_feelings),
                getString(R.string.html_911),
                getString(R.string.html_suicide_crisis),
                getString(R.string.html_helpline_number),
                getString(R.string.html_by_scheduling),
                getString(R.string.html_by_clicking))));
    }
}
