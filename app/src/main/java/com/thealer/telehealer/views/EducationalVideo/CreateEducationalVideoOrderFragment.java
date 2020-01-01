package com.thealer.telehealer.views.EducationalVideo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalVideo;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalVideoResponse;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalVideoViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.EducationalVideo.Adapter.EducationListAdapter;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;
import com.thealer.telehealer.views.home.SelectAssociationFragment;
import com.thealer.telehealer.views.home.orders.OrdersBaseFragment;
import com.thealer.telehealer.views.home.orders.OrdersCustomView;

import java.util.ArrayList;


public class CreateEducationalVideoOrderFragment extends OrdersBaseFragment {

    private OrdersCustomView patient_ocv,visit_ocv;
    private CustomRecyclerView video_rv;
    private Button send_btn;

    @Nullable
    private EducationListAdapter educationListAdapter;
    private EducationalVideoViewModel educationalVideoViewModel;
    private int page = 1;
    private ArrayList<String> selectedList = new ArrayList<>();
    private CommonUserApiResponseModel selectedPatientDetail;
    @Nullable
    private String doctorGuid = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        educationalVideoViewModel = new ViewModelProvider(this).get(EducationalVideoViewModel.class);
        addObservers();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_educational_order_create, container, false);
        setTitle(getString(R.string.new_educational_video));

        if (getActivity() instanceof AttachObserverInterface) {
            ((AttachObserverInterface)getActivity()).attachObserver(educationalVideoViewModel);
        }

        initView(view);
        return view;
    }

    private void initView(View view) {
        patient_ocv = (OrdersCustomView) view.findViewById(R.id.patient_ocv);
        visit_ocv = (OrdersCustomView) view.findViewById(R.id.visit_ocv);
        video_rv = view.findViewById(R.id.video_rv);
        send_btn = view.findViewById(R.id.send_btn);

        initListener();
        assignRecyclerViewObservers();

        if (selectedPatientDetail != null) {
            patient_ocv.setTitleTv(selectedPatientDetail.getUserDisplay_name());
            patient_ocv.setSubtitleTv(selectedPatientDetail.getDob());
            patient_ocv.setSub_title_visible(true);
        }

        if (getArguments() != null) {
            CommonUserApiResponseModel doctorModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL);
            if (doctorModel != null) {
                doctorGuid = doctorModel.getUser_guid();
            }
        }

        updateButtonEnable();
    }

    private void initListener() {
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showQuickLogin();
            }
        });

        patient_ocv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = getArguments();
                if (bundle == null) {
                    bundle = new Bundle();
                }
                bundle.putString(ArgumentKeys.SEARCH_TYPE, ArgumentKeys.SEARCH_ASSOCIATION);
                bundle.putString(ArgumentKeys.DOCTOR_GUID, doctorGuid);
                SelectAssociationFragment selectAssociationFragment = new SelectAssociationFragment();
                selectAssociationFragment.setArguments(bundle);
                selectAssociationFragment.setTargetFragment(CreateEducationalVideoOrderFragment.this, 1000);
                ((ShowSubFragmentInterface) getActivity()).onShowFragment(selectAssociationFragment);
            }
        });
    }

    private void assignRecyclerViewObservers() {
        video_rv.setEmptyState(EmptyViewConstants.EMPTY_EDUCATIONAL_VIDEO_ORDER_NEW);
        video_rv.showOrhideEmptyState(false);
        video_rv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                page = page + 1;
                video_rv.setScrollable(false);
                video_rv.showProgressBar();
                getEducationalVideos();
            }
        });
        video_rv.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEducationalVideos();
            }
        });
        video_rv.setErrorModel(this, educationalVideoViewModel.getErrorModelLiveData());
        video_rv.getSwipeLayout().setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getEducationalVideos();
            }
        });

        educationListAdapter = new EducationListAdapter(getActivity(), new ArrayList<>(), new EducationListAdapter.EducationalListSelector() {
            @Override
            public void didSelectEducationalVideo(EducationalVideo video) {
                String item = video.getVideo_id()+"";
                if (selectedList.contains(item)) {
                    selectedList.remove(item);
                } else {
                    selectedList.add(item);
                }

                if (educationListAdapter != null) {
                    educationListAdapter.updateSelectionList(selectedList);
                }

                updateButtonEnable();
            }
        },selectedList);
        video_rv.getRecyclerView().setAdapter(educationListAdapter);

        getEducationalVideos();
    }

    private void addObservers() {
        educationalVideoViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel instanceof EducationalVideoResponse) {
                    EducationalVideoResponse response = (EducationalVideoResponse) baseApiResponseModel;

                    if (educationListAdapter == null) {
                        return;
                    }

                    video_rv.getSwipeLayout().setRefreshing(false);

                    if (page == 1) {
                        educationListAdapter.reset();
                    }

                    if (response.getResult().size() == 0) {
                        video_rv.showOrhideEmptyState(true);
                    }

                    video_rv.setNextPage(response.getNext());

                    if (response.getResult().size() > 0) {
                        video_rv.showOrhideEmptyState(false);
                    }

                    educationListAdapter.appedItems(response.getResult());

                    video_rv.setScrollable(true);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, true);
                    bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.success));
                    bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.selected_form_submitted_successfully));
                    LocalBroadcastManager
                            .getInstance(getActivity())
                            .sendBroadcast(new Intent(getString(R.string.success_broadcast_receiver))
                                    .putExtras(bundle));
                }
            }
        });
    }

    private void getEducationalVideos() {
        educationalVideoViewModel.getEducationalVideo(doctorGuid,page);
    }

    private void updateButtonEnable() {
        if (selectedList.size() > 0 && selectedPatientDetail != null) {
            send_btn.setEnabled(true);
        } else {
            send_btn.setEnabled(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                selectedPatientDetail = (CommonUserApiResponseModel) bundle.getSerializable(ArgumentKeys.SELECTED_ASSOCIATION_DETAIL);
            }
        }
    }

    @Override
    public void onAuthenticated() {
        SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
        successViewDialogFragment.setTargetFragment(this, RequestID.REQ_SHOW_SUCCESS_VIEW);
        successViewDialogFragment.show(getActivity().getSupportFragmentManager(), successViewDialogFragment.getClass().getSimpleName());
        assignEducationalOrder();
    }

    private void assignEducationalOrder() {
        if (selectedPatientDetail == null) {
            return;
        }

        educationalVideoViewModel.postEducationalVideoOrder(doctorGuid,selectedPatientDetail.getUser_guid(),selectedList);
    }

}
