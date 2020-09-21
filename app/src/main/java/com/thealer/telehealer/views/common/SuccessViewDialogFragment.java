package com.thealer.telehealer.views.common;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.views.base.BaseDialogFragment;

/**
 * Created by Aswin on 01,November,2018
 */
public class SuccessViewDialogFragment extends BaseDialogFragment {
    private SuccessViewInterface successViewInterface;
    private ImageView loaderIv;
    private TextView titleTv;
    private TextView messageTv;
    private CustomButton doneBtn;
    private boolean status;
    private String title;
    private String message;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onDataUpdated(intent.getExtras());
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_success_view, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        successViewInterface = (SuccessViewInterface) getActivity();
    }

    public void onDataUpdated(Bundle bundle) {
        if (bundle != null) {
            status = bundle.getBoolean(Constants.SUCCESS_VIEW_STATUS);
            title = bundle.getString(Constants.SUCCESS_VIEW_TITLE);
            message = bundle.getString(Constants.SUCCESS_VIEW_DESCRIPTION);

            setData();
        }
    }

    private void setData() {
        titleTv.setVisibility(View.VISIBLE);
        messageTv.setVisibility(View.VISIBLE);
        doneBtn.setVisibility(View.VISIBLE);

        doneBtn.setText(status ? "done" : "Retry");

        if (status) {
            loaderIv.setImageDrawable(getActivity().getDrawable(R.drawable.ic_tick_inside_a_circle));
        } else {
            loaderIv.setImageDrawable(getActivity().getDrawable(R.drawable.ic_cross_inside_a_circle));
        }
        loaderIv.setVisibility(View.VISIBLE);

        titleTv.setText(title);
        messageTv.setText(message);
    }

    private void initView(View view) {
        loaderIv = (ImageView) view.findViewById(R.id.loader_iv);
        titleTv = (TextView) view.findViewById(R.id.title_tv);
        messageTv = (TextView) view.findViewById(R.id.message_tv);
        doneBtn = (CustomButton) view.findViewById(R.id.done_btn);

        loaderIv.setVisibility(View.GONE);
        titleTv.setText("Loading");
        messageTv.setText("Please wait...");
//        titleTv.setVisibility(View.GONE);
//        messageTv.setVisibility(View.GONE);
        doneBtn.setVisibility(View.GONE);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                successViewInterface.onSuccessViewCompletion(status);
                getDialog().dismiss();

                if (getTargetFragment()!= null){
                    if (status)
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
                    else
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
                }
            }
        });

        if (getArguments() != null) {
            onDataUpdated(getArguments());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(getString(R.string.success_broadcast_receiver)));
    }
}
