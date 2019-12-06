package com.thealer.telehealer.views.signup.doctor;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.GetDoctorsApiViewModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.TypeAHeadResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 23,October,2018
 */
public class DoctorSearchNameFragment extends BaseFragment implements DoCurrentTransactionInterface {
    private EditText searchEt;
    private ImageView clearIv;
    private RecyclerView searchListRv;
    private ImageView progressbar;

    private OnActionCompleteInterface onActionCompleteInterface;
    private OnViewChangeInterface onViewChangeInterface;
    private GetDoctorsApiViewModel getDoctorsApiViewModel;
    private TypeAHeadResponseModel typeAHeadResponseModel;

    private SearchListAdapter searchListAdapter;
    private int page = 1;
    private boolean isApiRequested = false, isSearch = true;
    private List<String> doctorsNameList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private ImageView throbber;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
        getDoctorsApiViewModel = new ViewModelProvider(this).get(GetDoctorsApiViewModel.class);
        onViewChangeInterface.attachObserver(getDoctorsApiViewModel);
        getDoctorsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                throbber.setVisibility(View.GONE);
                isApiRequested = false;
                progressbar.setVisibility(View.GONE);
                if (!searchEt.getText().toString().isEmpty())
                    clearIv.setVisibility(View.VISIBLE);

                if (baseApiResponseModel != null) {
                    typeAHeadResponseModel = (TypeAHeadResponseModel) baseApiResponseModel;

                    if (page == 1) {
                        doctorsNameList.clear();
                        doctorsNameList.add(" ");
                    }
                    doctorsNameList.addAll(typeAHeadResponseModel.getData());
                    setSearchList();

                    EventRecorder.recordRegistration("doctor_lookup_success", null);
                }
            }
        });

        getDoctorsApiViewModel.getErrorModelLiveData().observe(this,
                new Observer<ErrorModel>() {
                    @Override
                    public void onChanged(@Nullable ErrorModel errorModel) {
                        isApiRequested = false;
                        progressbar.setVisibility(View.GONE);
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_search_name, container, false);

        onViewChangeInterface.hideOrShowNext(true);
        onViewChangeInterface.enableNext(false);

        initView(view);
        return view;
    }

    private void initView(View view) {
        searchEt = (EditText) view.findViewById(R.id.search_et);
        clearIv = (ImageView) view.findViewById(R.id.clear_iv);
        searchListRv = (RecyclerView) view.findViewById(R.id.search_list_rv);
        progressbar = (ImageView) view.findViewById(R.id.progressbar);
        throbber = (ImageView) view.findViewById(R.id.throbber);

        Glide.with(getActivity().getApplicationContext()).load(R.raw.throbber).into(throbber);
        Glide.with(getActivity().getApplicationContext()).load(R.raw.throbber).into(progressbar);

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isSearch) {
                    clearIv.setVisibility(View.GONE);
                    throbber.setVisibility(View.VISIBLE);
                    page = 1;
                    if (searchListAdapter != null) {
                        searchListAdapter.setData(new ArrayList<>());
                    }
                    if (!s.toString().isEmpty()) {
                        makeApiCall(s.toString());
                        onViewChangeInterface.enableNext(true);
                    } else {
                        onViewChangeInterface.enableNext(false);
                        clearIv.setVisibility(View.GONE);
                    }
                }
            }
        });

        clearIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEt.setText(null);
                doctorsNameList.clear();
            }
        });

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        searchListRv.setLayoutManager(linearLayoutManager);

        searchListRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:

                        if (linearLayoutManager.getItemCount() > 0 && linearLayoutManager.findFirstVisibleItemPosition() < doctorsNameList.size()) {
                            if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() > 0) {
                                isSearch = false;
                                linearLayoutManager.setSmoothScrollbarEnabled(true);
                                linearLayoutManager.scrollToPositionWithOffset(linearLayoutManager.findFirstCompletelyVisibleItemPosition(), 0);
                                searchEt.setText(doctorsNameList.get(linearLayoutManager.findFirstCompletelyVisibleItemPosition()));
                                searchEt.setSelection(searchEt.getText().toString().length());
                                isSearch = true;
                            }
                        }
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        break;

                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (linearLayoutManager.getItemCount() > 0 && linearLayoutManager.getItemCount() < typeAHeadResponseModel.getTotal_count()) {
                    if (linearLayoutManager.findLastVisibleItemPosition() == linearLayoutManager.getItemCount() - 1) {
                        page = page + 1;
                        makeApiCall(searchEt.getText().toString());
                        progressbar.setVisibility(View.VISIBLE);
                    } else {
                        progressbar.setVisibility(View.GONE);
                    }
                } else {
                    progressbar.setVisibility(View.GONE);
                }
            }
        });

    }

    private void setSearchList() {
        searchListAdapter = new SearchListAdapter(getActivity(), doctorsNameList, new OnListItemSelectInterface() {
            @Override
            public void onListItemSelected(int position, Bundle bundle) {
                isSearch = false;
                searchEt.setText(doctorsNameList.get(position));
                searchEt.setSelection(searchEt.getText().toString().length());
                linearLayoutManager.scrollToPositionWithOffset(position, 0);
                isSearch = true;
            }
        });
        searchListRv.setAdapter(searchListAdapter);

    }

    private void makeApiCall(String name) {
        if (!isApiRequested) {
            isApiRequested = true;
            getDoctorsApiViewModel.getTypeAHeadResult(page, name, false);
        }
    }

    @Override
    public void doCurrentTransaction() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SEARCH_KEY, searchEt.getText().toString());

        onActionCompleteInterface.onCompletionResult(null, true, bundle);
    }
}
