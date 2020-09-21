package com.thealer.telehealer.views.home.orders.labs;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.base.OrdersBaseFragment;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.orders.OrderConstant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Aswin on 01,December,2018
 */
public class SelectLabTestFragment extends OrdersBaseFragment implements View.OnClickListener {
    private EditText searchEt;
    private RecyclerView testListRv;
    private Button nextBtn;

    private String selectedTitle;
    private List<String> testList = Arrays.asList(OrderConstant.labTestList);
    boolean isEditMode = false;

    private SelectLabTestAdapter selectLabTestAdapter;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private OnCloseActionInterface onCloseActionInterface;
    private LabTestDataViewModel labTestDataViewModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        labTestDataViewModel = ViewModelProviders.of(getActivity()).get(LabTestDataViewModel.class);
        selectedTitle = labTestDataViewModel.getTestTitle();
        if (selectedTitle != null){
            isEditMode = true;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_lab_test, container, false);
        setTitle(getString(R.string.choose_test));
        initView(view);
        return view;
    }

    private void initView(View view) {
        searchEt = (EditText) view.findViewById(R.id.search_et);
        testListRv = (RecyclerView) view.findViewById(R.id.test_list_rv);
        nextBtn = (Button) view.findViewById(R.id.next_btn);

        nextBtn.setOnClickListener(this);

        searchEt.setHint(getString(R.string.choose_test));

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()){
                    selectLabTestAdapter.setTestList(testList);
                }else {
                    selectLabTestAdapter.setTestList(getFilteredList(s.toString()));
                }
            }
        });

        testListRv.setLayoutManager(new LinearLayoutManager(getActivity()));

        selectLabTestAdapter = new SelectLabTestAdapter(getActivity(),
                testList,
                selectedTitle,
                new OnListItemSelectInterface() {
            @Override
            public void onListItemSelected(int position, Bundle bundle) {
                selectedTitle = testList.get(position);
                enableOrDisableNext();
            }
        });

        testListRv.setAdapter(selectLabTestAdapter);

        enableOrDisableNext();
    }

    private List<String> getFilteredList(String s) {
        List<String > resultList = new ArrayList<>();

        for (String test: testList) {
            if (test.contains(s)){
                resultList.add(test);
            }
        }
        return resultList;
    }

    private void enableOrDisableNext() {
        boolean enable = false;

        if (selectedTitle != null){
            enable = true;
        }

        nextBtn.setEnabled(enable);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.next_btn:
                labTestDataViewModel.setTestTitle(selectedTitle);
                if (isEditMode){
                    onCloseActionInterface.onClose(false);
                }else {
                    SelectIcdCodeFragment selectIcdCodeFragment = new SelectIcdCodeFragment();
                    selectIcdCodeFragment.setTargetFragment(this, RequestID.REQ_SELECT_ICD_CODE);
                    showSubFragmentInterface.onShowFragment(selectIcdCodeFragment);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == RequestID.REQ_SELECT_ICD_CODE){
            onCloseActionInterface.onClose(false);
            showLabOrderPreview();
        }
    }


    private void showLabOrderPreview() {
        LabTestPreviewFragment labTestPreviewFragment = new LabTestPreviewFragment();
        labTestPreviewFragment.setArguments(getArguments());
        showSubFragmentInterface.onShowFragment(labTestPreviewFragment);
    }
}
