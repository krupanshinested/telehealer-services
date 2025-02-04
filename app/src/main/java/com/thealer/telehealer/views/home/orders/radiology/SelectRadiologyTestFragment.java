package com.thealer.telehealer.views.home.orders.radiology;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Util.TimerInterface;
import com.thealer.telehealer.common.Util.TimerRunnable;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.home.orders.OrdersBaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 11,December,2018
 */
public class SelectRadiologyTestFragment extends OrdersBaseFragment implements View.OnClickListener {
    private EditText searchEt;
    private RecyclerView radiologyListRv;
    private Button nextBtn;
    private LinearLayout selectedTestLl;
    private RecyclerView selectedRadiologyListRv;

    private RadiologyListViewModel radiologyListViewModel;
    private OnCloseActionInterface onCloseActionInterface;
    private RadiologyListAdapter radiologyListAdapter;
    private RadiologySelectedListAdapter radiologySelectedListAdapter;

    private List<RadiologyListModel> radiologyModelList = new ArrayList<>();
    private List<String> selectedIdList = new ArrayList<>();

    @Nullable
    private TimerRunnable uiToggleTimer;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        radiologyListViewModel = new ViewModelProvider(getActivity()).get(RadiologyListViewModel.class);
        radiologyListViewModel.getSelectedRadiologyListLiveData().observe(this,
                new Observer<List<RadiologyListModel>>() {
                    @Override
                    public void onChanged(@Nullable List<RadiologyListModel> radiologyListModels) {
                        if (radiologyListModels != null && radiologyListModels.size() > 0) {
                            enableOrDisableNext(true);
                        } else {
                            enableOrDisableNext(false);
                        }

                        radiologyModelList = radiologyListModels;
                    }
                });

        radiologyListViewModel.getSelectedIdList().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> list) {

                selectedIdList = list;

                radiologyListAdapter.setData(radiologyModelList, selectedIdList);
                radiologySelectedListAdapter.setData(radiologyModelList, selectedIdList);

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_radiology_test, container, false);
        initView(view);
        setTitle(getString(R.string.choose_test));
        return view;
    }

    private void initView(View view) {
        searchEt = (EditText) view.findViewById(R.id.search_et);
        radiologyListRv = (RecyclerView) view.findViewById(R.id.radiology_list_rv);
        nextBtn = (Button) view.findViewById(R.id.next_btn);
        selectedTestLl = (LinearLayout) view.findViewById(R.id.selected_test_ll);
        selectedRadiologyListRv = (RecyclerView) view.findViewById(R.id.selected_radiology_list_rv);

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    if (uiToggleTimer != null) {
                        uiToggleTimer.setStopped(true);
                        uiToggleTimer = null;
                    }

                    Handler handler = new Handler();
                    TimerRunnable runnable = new TimerRunnable(new TimerInterface() {
                        @Override
                        public void run() {
                            radiologyModelList = new RadiologyConstants().getRadiologyListModel(searchEt.getText().toString().toLowerCase());
                            radiologyListAdapter.setData(radiologyModelList);
                            radiologyListAdapter.notifyDataSetChanged();
                        }
                    });
                    uiToggleTimer = runnable;
                    handler.postDelayed(runnable, ArgumentKeys.SEARCH_INTERVAL);
                } else {
                    radiologyModelList = new RadiologyConstants().getRadiologyListModel();
                }
                radiologyListAdapter.setData(radiologyModelList);
                radiologyListAdapter.notifyDataSetChanged();
            }
        });

        searchEt.setHint(getString(R.string.choose_test));

        nextBtn.setOnClickListener(this);

        enableOrDisableNext(false);

        radiologyListRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        selectedRadiologyListRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        radiologyModelList = new RadiologyConstants().getRadiologyListModel();
        radiologyListAdapter = new RadiologyListAdapter(getActivity(), radiologyModelList);
        radiologyListRv.setAdapter(radiologyListAdapter);

        radiologySelectedListAdapter = new RadiologySelectedListAdapter(getActivity());
        selectedRadiologyListRv.setAdapter(radiologySelectedListAdapter);
    }

    private void enableOrDisableNext(boolean enable) {
        nextBtn.setEnabled(enable);
        if (enable) {
            selectedTestLl.setVisibility(View.VISIBLE);
        } else {
            selectedTestLl.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_btn:
                onCloseActionInterface.onClose(false);
                break;
        }
    }
}
