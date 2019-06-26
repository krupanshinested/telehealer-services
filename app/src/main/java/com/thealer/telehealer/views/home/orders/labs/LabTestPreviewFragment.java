package com.thealer.telehealer.views.home.orders.labs;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.orders.lab.IcdCodeApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.lab.LabsBean;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.orders.OrdersBaseFragment;
import com.thealer.telehealer.views.home.orders.OrdersCustomView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Aswin on 01,December,2018
 */
public class LabTestPreviewFragment extends OrdersBaseFragment implements View.OnClickListener {
    private OrdersCustomView chooseTestOcv;
    private Switch statSwitch;
    private RecyclerView icdListRv;
    private Button doneBtn;

    private LabTestDataViewModel labTestDataViewModel;
    private IcdCodesDataViewModel icdCodesDataViewModel;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private OnCloseActionInterface onCloseActionInterface;

    private List<String> selectedIcdCodeList = new ArrayList<>();
    private HashMap<String, IcdCodeApiResponseModel.ResultsBean> icdCodeDetailHashMap = new HashMap<>();
    private ConstraintLayout listCl;
    private boolean isEditMode = false;
    private int position;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        labTestDataViewModel = ViewModelProviders.of(getActivity()).get(LabTestDataViewModel.class);
        icdCodesDataViewModel = ViewModelProviders.of(getActivity()).get(IcdCodesDataViewModel.class);

        icdCodesDataViewModel.selectedIcdCodeList.observe(this,
                new Observer<List<String>>() {
                    @Override
                    public void onChanged(@Nullable List<String> list) {
                        if (list != null) {
                            selectedIcdCodeList = list;
                        }
                    }
                });

        icdCodesDataViewModel.icdCodeDetailHashMap.observe(this,
                new Observer<HashMap<String, IcdCodeApiResponseModel.ResultsBean>>() {
                    @Override
                    public void onChanged(@Nullable HashMap<String, IcdCodeApiResponseModel.ResultsBean> resultsBeanHashMap) {
                        if (resultsBeanHashMap != null) {

                            icdCodeDetailHashMap = resultsBeanHashMap;

                            setLabTestAdapter();

                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lab_test_preview, container, false);
        setTitle(getString(R.string.lab_description));
        initView(view);
        return view;
    }

    private void initView(View view) {
        chooseTestOcv = (OrdersCustomView) view.findViewById(R.id.choose_test_ocv);
        statSwitch = (Switch) view.findViewById(R.id.stat_switch);
        icdListRv = (RecyclerView) view.findViewById(R.id.icd_list_rv);
        doneBtn = (Button) view.findViewById(R.id.done_btn);
        listCl = (ConstraintLayout) view.findViewById(R.id.list_cl);

        chooseTestOcv.setOnClickListener(this);
        doneBtn.setOnClickListener(this);
        listCl.setOnClickListener(this);

        if (getArguments() != null) {

            isEditMode = getArguments().getBoolean(ArgumentKeys.IS_EDIT_MODE);

            if (isEditMode) {
                position = getArguments().getInt(ArgumentKeys.LAB_TEST_SELECTED_POSITION);

                LabsBean labsBean = labTestDataViewModel.getLabsBeanLiveData().getValue().get(position);

                if (labTestDataViewModel.getTestTitle() != null) {
                    labTestDataViewModel.setTestTitle(labTestDataViewModel.getTestTitle());
                } else {
                    labTestDataViewModel.setTestTitle(labsBean.getTest_description());
                }

                icdCodesDataViewModel.getSelectedIcdCodeList().setValue(labsBean.getICD10_codes());

                statSwitch.setChecked(labsBean.isStat());
            }
        }

        chooseTestOcv.setTitleTv(labTestDataViewModel.getTestTitle());

        icdListRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        setLabTestAdapter();
    }

    private void setLabTestAdapter() {
        IcdCodeListAdapter icdCodeListAdapter = new IcdCodeListAdapter(getActivity(), selectedIcdCodeList, icdCodeDetailHashMap);
        icdListRv.setAdapter(icdCodeListAdapter);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }
        switch (v.getId()) {
            case R.id.choose_test_ocv:
                SelectLabTestFragment selectLabTestFragment = new SelectLabTestFragment();
                bundle.putString(ArgumentKeys.VIEW_TITLE, getString(R.string.choose_test));
                selectLabTestFragment.setArguments(bundle);
                showSubFragmentInterface.onShowFragment(selectLabTestFragment);
                break;
            case R.id.done_btn:

                LabsBean labsBean = new LabsBean();
                labsBean.setTest_description(labTestDataViewModel.getTestTitle());
                labsBean.setStat(statSwitch.isChecked());
                labsBean.setICD10_codes(selectedIcdCodeList);

                if (labTestDataViewModel.getLabsBeanLiveData().getValue() != null) {
                    if (!isEditMode) {
                        labTestDataViewModel.getLabsBeanLiveData().getValue().add(labsBean);
                    } else {
                        labTestDataViewModel.getLabsBeanLiveData().getValue().set(position, labsBean);
                    }

                } else {
                    List<LabsBean> labsBeanList = new ArrayList<>();
                    labsBeanList.add(labsBean);
                    labTestDataViewModel.getLabsBeanLiveData().setValue(labsBeanList);
                }
                onCloseActionInterface.onClose(false);

                break;
            case R.id.list_cl:
                SelectIcdCodeFragment selectIcdCodeFragment = new SelectIcdCodeFragment();
                selectIcdCodeFragment.setArguments(bundle);
                showSubFragmentInterface.onShowFragment(selectIcdCodeFragment);
                break;
        }
    }
}
