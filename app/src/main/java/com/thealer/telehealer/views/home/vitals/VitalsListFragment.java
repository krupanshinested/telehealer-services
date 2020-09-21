package com.thealer.telehealer.views.home.vitals;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.home.VitalsOrdersListAdapter;
import iHealth.pairing.VitalCreationActivity;

/**
 * Created by Aswin on 21,November,2018
 */
public class VitalsListFragment extends BaseFragment {
    private RecyclerView listRv;
    private FloatingActionButton fab;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_vitals_orders_list, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        fab = view.findViewById(R.id.add_fab);

        if (UserType.isUserPatient()) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (UserType.isUserPatient()) {
                        Intent intent = new Intent(getActivity(), VitalCreationActivity.class);
                        getActivity().startActivity(intent);
                    }
                }
            });
        } else {
            fab.setVisibility(View.GONE);
        }

        listRv = (RecyclerView) view.findViewById(R.id.vitals_orders_list_rv);
        listRv.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (getArguments() != null) {

            VitalsOrdersListAdapter vitalsOrdersListAdapter = new VitalsOrdersListAdapter(getActivity(), SupportedMeasurementType.items, Constants.VIEW_VITALS, getArguments());
            listRv.setAdapter(vitalsOrdersListAdapter);
        }
    }
}
