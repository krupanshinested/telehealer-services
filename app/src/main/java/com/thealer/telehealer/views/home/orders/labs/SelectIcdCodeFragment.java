package com.thealer.telehealer.views.home.orders.labs;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
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
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.lab.IcdCodeApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.lab.IcdCodeApiViewModel;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.orders.OrdersBaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 01,December,2018
 */
public class SelectIcdCodeFragment extends OrdersBaseFragment implements View.OnClickListener {

    private EditText searchEt;
    private CustomRecyclerView icdListCrv;
    private Button doneBtn;
    private CardView selectedIcdCv;
    private RecyclerView selectedIcdRv;

    private IcdCodeApiViewModel icdCodeApiViewModel;
    private IcdCodeApiResponseModel icdCodeApiResponseModel;
    private AttachObserverInterface attachObserverInterface;
    private OnCloseActionInterface onCloseActionInterface;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private IcdCodesDataViewModel icdCodesDataViewModel;

    private int startKey = 1;
    private SelectIcdCodeAdapter selectIcdCodeAdapter;
    private SelectedIcdCodeListAdapter selectedIcdCodeListAdapter;
    private List<String> selectedIcdCodeList = new ArrayList<>();
    private boolean isApiRequested;
    private String labTestTitle;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachObserverInterface = (AttachObserverInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();

        icdCodesDataViewModel = ViewModelProviders.of(getActivity()).get(IcdCodesDataViewModel.class);
        icdCodeApiViewModel = ViewModelProviders.of(this).get(IcdCodeApiViewModel.class);

        attachObserverInterface.attachObserver(icdCodeApiViewModel);

        icdCodeApiViewModel.baseApiResponseModelMutableLiveData.observe(this,
                new Observer<BaseApiResponseModel>() {
                    @Override
                    public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                        if (baseApiResponseModel != null) {
                            icdCodeApiResponseModel = (IcdCodeApiResponseModel) baseApiResponseModel;
                            selectIcdCodeAdapter.setIcdCodeApiResponseModel(icdCodeApiResponseModel.getResults(), startKey);
                            icdListCrv.setTotalCount(icdCodeApiResponseModel.getTotal_count());
                            icdListCrv.setScrollable(true);
                            icdListCrv.hideProgressBar();
                            isApiRequested = false;
                        }
                    }
                });

        icdCodesDataViewModel.selectedIcdCodeList.observe(this,
                new Observer<List<String>>() {
                    @Override
                    public void onChanged(@Nullable List<String> list) {
                        if (list != null) {
                            if (list.size() > 0) {
                                selectedIcdCv.setVisibility(View.VISIBLE);
                            } else {
                                selectedIcdCv.setVisibility(View.GONE);
                            }
                            selectedIcdCodeList = list;
                            enableOrDisableDone();
                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_icd_code, container, false);
        setTitle(getString(R.string.icd_10));
        initView(view);
        return view;
    }

    private void initView(View view) {
        searchEt = (EditText) view.findViewById(R.id.search_et);
        icdListCrv = (CustomRecyclerView) view.findViewById(R.id.icd_list_crv);
        doneBtn = (Button) view.findViewById(R.id.done_btn);
        selectedIcdCv = (CardView) view.findViewById(R.id.selected_icd_cv);
        selectedIcdRv = (RecyclerView) view.findViewById(R.id.selected_icd_rv);

        searchEt.setHint(getString(R.string.search_icd10_codes));

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                startKey = 1;

                String key = null;
                if (!s.toString().isEmpty()) {
                    key = s.toString();
                }
                getIcdCodes(key, true);
            }
        });

        doneBtn.setOnClickListener(this);

        selectedIcdCv.setVisibility(View.GONE);

        icdListCrv.setEmptyState(EmptyViewConstants.EMPTY_SEARCH);

        icdListCrv.getSwipeLayout().setEnabled(false);

        icdListCrv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                startKey = startKey + 1;
                icdListCrv.setScrollable(false);
                icdListCrv.showProgressBar();
                getIcdCodes(searchEt.getText().toString(), false);
                isApiRequested = true;
            }
        });

        selectIcdCodeAdapter = new SelectIcdCodeAdapter(getActivity(), new ArrayList<>());

        icdListCrv.getRecyclerView().setAdapter(selectIcdCodeAdapter);

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        selectedIcdRv.setLayoutManager(horizontalLayoutManager);
        selectedIcdCodeListAdapter = new SelectedIcdCodeListAdapter(getActivity());
        selectedIcdRv.setAdapter(selectedIcdCodeListAdapter);

        getIcdCodes(null, true);

        enableOrDisableDone();
    }

    private void getIcdCodes(String key, boolean showProgress) {
        if (!isApiRequested) {
            icdCodeApiViewModel.getAllIcdCodes(startKey, key, showProgress);
        }
    }

    private void enableOrDisableDone() {
        boolean enable = false;

        if (selectedIcdCodeList.size() != 0) {
            enable = true;
        }

        doneBtn.setEnabled(enable);

        if (enable) {
            doneBtn.setAlpha(1);
        } else {
            doneBtn.setAlpha((float) 0.5);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.done_btn:
                onCloseActionInterface.onClose(false);
                if (getTargetFragment() != null) {
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
                }
                break;
        }
    }
}
