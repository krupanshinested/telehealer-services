package com.thealer.telehealer.views.home.monitoring;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thealer.telehealer.R;
import com.thealer.telehealer.views.base.BaseFragment;

/**
 * Created by Aswin on 20,February,2019
 */
public class MonitoringFragment extends BaseFragment {
    private RecyclerView monitoringRv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitoring, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        monitoringRv = (RecyclerView) view.findViewById(R.id.monitoring_rv);

        monitoringRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        monitoringRv.setAdapter(new MonitoringListAdapter(getActivity(), getArguments()));
    }
}
