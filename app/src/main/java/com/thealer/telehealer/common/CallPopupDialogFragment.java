package com.thealer.telehealer.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.OpenTok.CallRequest;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiResponseModel;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;
import com.thealer.telehealer.views.common.CallPlacingActivity;

import java.util.UUID;

/**
 * Created by Aswin on 04,April,2019
 */
public class CallPopupDialogFragment extends DialogFragment {
    private android.widget.TextView titleTv;
    private CustomButton joinBtn;
    private CustomButton dismissBtn;
    private SchedulesApiResponseModel.ResultBean scheduleDetail;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setCancelable(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_call_popup, null, true);
        titleTv = (TextView) view.findViewById(R.id.title_tv);
        joinBtn = (CustomButton) view.findViewById(R.id.join_btn);
        dismissBtn = (CustomButton) view.findViewById(R.id.dismiss_btn);

        if (getArguments() != null) {
            scheduleDetail = (SchedulesApiResponseModel.ResultBean) getArguments().getSerializable(ArgumentKeys.SCHEDULE_DETAIL);

            if (scheduleDetail != null) {

                String user = String.format("%s ( %s )", scheduleDetail.getPatient().getFirst_name(), scheduleDetail.getPatient().getAge());

                titleTv.setText(String.format(getString(R.string.call_pop_up_title), user));
            }
        }

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String doctorGuid = null, doctorName = null;

                if (UserType.isUserAssistant()) {
                    doctorGuid = scheduleDetail.getDoctor().getUser_guid();
                    doctorName = scheduleDetail.getDoctor().getUserDisplay_name();
                }

                CallRequest callRequest = new CallRequest(UUID.randomUUID().toString(),scheduleDetail.getPatient().getUser_guid(), scheduleDetail.getPatient(), doctorGuid, doctorName, String.valueOf(scheduleDetail.getSchedule_id()), OpenTokConstants.video,true,scheduleDetail.getSchedule_id()+"");

                Intent intent = new Intent(getActivity(), CallPlacingActivity.class);
                intent.putExtra(ArgumentKeys.CALL_INITIATE_MODEL, callRequest);
                startActivity(intent);

                dismiss();
            }
        });

        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        builder.setView(view);

        Dialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        return dialog;
    }
}
