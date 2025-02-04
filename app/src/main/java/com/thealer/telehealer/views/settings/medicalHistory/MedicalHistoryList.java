package com.thealer.telehealer.views.settings.medicalHistory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.OnCloseActionInterface;

/**
 * Created by Aswin on 21,January,2019
 */
public class MedicalHistoryList extends BaseFragment {
    private RecyclerView medicalHistoryRv;

    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;

    private OnCloseActionInterface onCloseActionInterface;
    private MedicalHistoryListAdapter medicalHistoryListAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medical_history_list, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        medicalHistoryRv = (RecyclerView) view.findViewById(R.id.medical_history_rv);
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.health_summary));
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseActionInterface.onClose(false);
            }
        });
        toolbar.setVisibility(View.VISIBLE);

        medicalHistoryRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        medicalHistoryListAdapter = new MedicalHistoryListAdapter(getActivity(), getArguments(), this);
        medicalHistoryRv.setAdapter(medicalHistoryListAdapter);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RequestID.REQ_HISTORY_UPDATE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                if (getArguments() != null) {
                    getArguments().putSerializable(Constants.USER_DETAIL, data.getSerializableExtra(Constants.USER_DETAIL));
                    if (medicalHistoryListAdapter != null) {
                        medicalHistoryListAdapter.updateBundle(getArguments());
                    }
                }
            }
        }
    }
}
