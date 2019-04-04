package com.thealer.telehealer.views.home.recents;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.diet.DietApiResponseModel;
import com.thealer.telehealer.apilayer.models.diet.DietApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersIdListApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersPrescriptionApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersSpecialistApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.documents.DocumentsApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.forms.OrdersUserFormsApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.lab.OrdersLabApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.miscellaneous.MiscellaneousApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.radiology.GetRadiologyResponseModel;
import com.thealer.telehealer.apilayer.models.recents.DownloadTranscriptResponseModel;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiResponseModel;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiViewModel;
import com.thealer.telehealer.apilayer.models.recents.TranscriptionApiResponseModel;
import com.thealer.telehealer.apilayer.models.recents.VisitsDetailApiResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiViewModel;
import com.thealer.telehealer.apilayer.models.visits.UpdateVisitRequestModel;
import com.thealer.telehealer.apilayer.models.visits.VisitsApiViewModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.GetUserDetails;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnModeChangeListener;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Aswin on 23,April,2019
 */
public class VisitsDetailFragment extends BaseFragment implements View.OnClickListener {
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView cancelTv;
    private TextView toolbarTitle;
    private TextView nextTv;
    private ImageView printIv;
    private RecyclerView visitsDetailRv;

    private OnCloseActionInterface onCloseActionInterface;
    private AttachObserverInterface attachObserverInterface;
    private ShowSubFragmentInterface showSubFragmentInterface;

    private RecentsApiViewModel recentsApiViewModel;
    private VisitsApiViewModel visitsApiViewModel;
    private SchedulesApiViewModel schedulesApiViewModel;
    private VisitDetailViewModel visitDetailViewModel;
    private VitalsApiViewModel vitalsApiViewModel;
    private OrdersApiViewModel ordersApiViewModel;
    private DietApiViewModel dietApiViewModel;

    private RecentsApiResponseModel.ResultBean recentDetail;
    private TranscriptionApiResponseModel transcriptionApiResponseModel;
    private DownloadTranscriptResponseModel downloadTranscriptResponseModel;
    private VisitsDetailApiResponseModel visitsDetailApiResponseModel;
    private SchedulesApiResponseModel.ResultBean schedulesApiResponseModel;
    private ArrayList<VitalsApiResponseModel> vitalsApiResponseModel;
    private OrdersIdListApiResponseModel ordersIdListApiResponseModel;
    private ArrayList<DocumentsApiResponseModel.ResultBean> documentsApiResponseModels;
    private ArrayList<OrdersUserFormsApiResponseModel> formsApiResponseModels;
    private ArrayList<DietApiResponseModel> dietApiResponseModels;

    private VisitDetailListAdapter visitDetailListAdapter;
    private CommonUserApiResponseModel patientDetail, doctorDetail;
    private String patientGuid, doctorGuid;
    private int mode = Constants.VIEW_MODE;
    private boolean isSecondaryApisCalled = false, isSuccessViewShown = false, isVisitUpdated = false, isPrimaryApiCalled = false;
    private String currentUpdateType = null, updateType;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachObserverInterface = (AttachObserverInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();

        dietApiViewModel = ViewModelProviders.of(this).get(DietApiViewModel.class);
        dietApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                if (baseApiResponseModels != null) {
                    dietApiResponseModels = (ArrayList<DietApiResponseModel>) (Object) baseApiResponseModels;
                    visitDetailViewModel.setDietApiResponseModels(dietApiResponseModels);
                    visitDetailListAdapter.setData();
                }
            }
        });

        visitDetailViewModel = ViewModelProviders.of(this).get(VisitDetailViewModel.class);

        schedulesApiViewModel = ViewModelProviders.of(this).get(SchedulesApiViewModel.class);
        attachObserverInterface.attachObserver(schedulesApiViewModel);
        schedulesApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    schedulesApiResponseModel = (SchedulesApiResponseModel.ResultBean) baseApiResponseModel;
                    visitDetailViewModel.setSchedulesApiResponseModel(schedulesApiResponseModel);
                    visitDetailListAdapter.setData();
                    isPrimaryApiCalled = true;
                }
            }
        });

        visitsApiViewModel = ViewModelProviders.of(this).get(VisitsApiViewModel.class);
        attachObserverInterface.attachObserver(visitsApiViewModel);
        visitsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof VisitsDetailApiResponseModel) {
                        visitsDetailApiResponseModel = (VisitsDetailApiResponseModel) baseApiResponseModel;
                        visitDetailViewModel.setVisitsDetailApiResponseModel(visitsDetailApiResponseModel);
                        visitDetailListAdapter.setData();

                        String updatedTranscript = visitsDetailApiResponseModel.getResult().getUpdated_transcript();
                        if (updatedTranscript != null && !updatedTranscript.isEmpty()) {
                            downloadTranscript(updatedTranscript, true);
                        }

                        if (isVisitUpdated) {
                            switch (updateType) {
                                case VisitDetailConstants.VISIT_TYPE_VITALS:
                                    getVitalsDetail();
                                    break;
                                case VisitDetailConstants.VISIT_TYPE_DIETS:
                                    getDietDetails();
                                    break;
                                case VisitDetailConstants.VISIT_TYPE_FORMS:
                                    getFormDetails();
                                    break;
                                case VisitDetailConstants.VISIT_TYPE_FILES:
                                    getDocumentDetails();
                                    break;
                                case VisitDetailConstants.VISIT_TYPE_ORDERS:
                                case VisitDetailConstants.VISIT_TYPE_LABS:
                                case VisitDetailConstants.VISIT_TYPE_XRAYS:
                                case VisitDetailConstants.VISIT_TYPE_SPECIALIST:
                                case VisitDetailConstants.VISIT_TYPE_PRESCRIPTIONS:
                                case VisitDetailConstants.VISIT_TYPE_MISCELLANEOUS:
                                    getReferralOrderDetail();
                                    break;
                                default:
                                    getOrderDetail();
                            }
                        } else if ((Object) visitsDetailApiResponseModel.getResult().getSchedule_id() != null &&
                                visitsDetailApiResponseModel.getResult().getSchedule_id() != 0) {
                            schedulesApiViewModel.getScheduleDetail(visitsDetailApiResponseModel.getResult().getSchedule_id(), doctorGuid, true);
                        } else {
                            isPrimaryApiCalled = true;
                        }
                    } else {
                        if (baseApiResponseModel.isSuccess()) {
                            if (currentUpdateType != null) {
                                switch (currentUpdateType) {
                                    case VisitDetailConstants.VISIT_TYPE_VITALS:
                                        visitDetailViewModel.removeVital();
                                        break;
                                    case VisitDetailConstants.VISIT_TYPE_DIETS:
                                        visitDetailViewModel.removeDiets();
                                        break;
                                    case VisitDetailConstants.VISIT_TYPE_FORMS:
                                        visitDetailViewModel.removeForms();
                                        break;
                                    case VisitDetailConstants.VISIT_TYPE_FILES:
                                        visitDetailViewModel.removeFiles();
                                        break;
                                    case VisitDetailConstants.VISIT_TYPE_LABS:
                                        visitDetailViewModel.removeLabs();
                                        break;
                                    case VisitDetailConstants.VISIT_TYPE_XRAYS:
                                        visitDetailViewModel.removeXrays();
                                        break;
                                    case VisitDetailConstants.VISIT_TYPE_SPECIALIST:
                                        visitDetailViewModel.removeSpecialist();
                                        break;
                                    case VisitDetailConstants.VISIT_TYPE_PRESCRIPTIONS:
                                        visitDetailViewModel.removePrescription();
                                        break;
                                    case VisitDetailConstants.VISIT_TYPE_MISCELLANEOUS:
                                        visitDetailViewModel.removeMiscellaneous();
                                        break;
                                }
                            }
                            if (isHasNextRequest()) {
                                updateVisit();
                            } else {
                                if (visitDetailViewModel.isInstructionUpdated()) {
                                    visitDetailViewModel.setInstructionUpdated(false);
                                    visitsDetailApiResponseModel.getResult().setInstructions(visitDetailViewModel.getInstruction());
                                    visitDetailViewModel.setVisitsDetailApiResponseModel(visitsDetailApiResponseModel);
                                }

                                if (visitDetailViewModel.isTranscriptEdited()) {

                                    DownloadTranscriptResponseModel updatedTranscript = new DownloadTranscriptResponseModel();
                                    updatedTranscript.setTranscripts(visitDetailViewModel.getUpdatedTranscriptResponseModel().getTranscripts());
                                    List<DownloadTranscriptResponseModel.SpeakerLabelsBean> updatedLabels = new ArrayList<>();

                                    for (int i = 0; i < visitDetailViewModel.getUpdatedTranscriptResponseModel().getSpeakerLabels().size(); i++) {
                                        if (!visitDetailViewModel.getUpdatedTranscriptResponseModel().getSpeakerLabels().get(i).isRemoved()) {
                                            updatedLabels.add(visitDetailViewModel.getUpdatedTranscriptResponseModel().getSpeakerLabels().get(i));
                                        }
                                    }
                                    updatedTranscript.setSpeakerLabels(updatedLabels);

                                    downloadTranscriptResponseModel = updatedTranscript;
                                    visitDetailViewModel.setDownloadTranscriptResponseModel(updatedTranscript);

                                    Log.e(TAG, "onChanged: " + new Gson().toJson(updatedTranscript.getSpeakerLabels()));

                                } else
                                    Log.e(TAG, "onChanged: else");

                                isSuccessViewShown = false;
                                sendSuccessViewBroadCast(getActivity(), true, getString(R.string.success), getString(R.string.visit_updated_successfully));
                                setMode(Constants.VIEW_MODE);
                            }
                        }
                    }
                }
            }
        });

        visitsApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    isSuccessViewShown = false;
                    sendSuccessViewBroadCast(getActivity(), false, getString(R.string.failure), getString(R.string.visit_update_failed));
                }
            }
        });

        recentsApiViewModel = ViewModelProviders.of(this).get(RecentsApiViewModel.class);
        attachObserverInterface.attachObserver(recentsApiViewModel);

        recentsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof TranscriptionApiResponseModel) {

                        transcriptionApiResponseModel = (TranscriptionApiResponseModel) baseApiResponseModel;

                        visitDetailViewModel.setTranscriptionApiResponseModel(transcriptionApiResponseModel);
                        visitDetailListAdapter.setData();

                        getOrderDetail();

                        if (transcriptionApiResponseModel.getStatus() != null &&
                                transcriptionApiResponseModel.getStatus().equals(transcriptionApiResponseModel.STATUS_READY) &&
                                transcriptionApiResponseModel.getTranscript() != null) {

                            downloadTranscript(transcriptionApiResponseModel.getTranscript(), true);
                        }

                    } else if (baseApiResponseModel instanceof DownloadTranscriptResponseModel) {
                        downloadTranscriptResponseModel = (DownloadTranscriptResponseModel) baseApiResponseModel;
                        visitDetailViewModel.setDownloadTranscriptResponseModel(downloadTranscriptResponseModel);
                        visitDetailListAdapter.setData();
                    }
                }
            }
        });

        recentsApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    showToast(errorModel.getMessage());
                }
            }
        });

        vitalsApiViewModel = ViewModelProviders.of(this).get(VitalsApiViewModel.class);
        attachObserverInterface.attachObserver(visitsApiViewModel);

        vitalsApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                if (baseApiResponseModels != null) {
                    vitalsApiResponseModel = (ArrayList<VitalsApiResponseModel>) (Object) baseApiResponseModels;
                    visitDetailViewModel.setVitalsApiResponseModels(vitalsApiResponseModel);
                    visitDetailListAdapter.setData();
                }
            }
        });

        ordersApiViewModel = ViewModelProviders.of(this).get(OrdersApiViewModel.class);
        ordersApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    ordersIdListApiResponseModel = (OrdersIdListApiResponseModel) baseApiResponseModel;
                    visitDetailViewModel.setOrdersIdListApiResponseModel(ordersIdListApiResponseModel);
                    visitDetailListAdapter.setData();
                }
            }
        });
        ordersApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                if (baseApiResponseModels != null && !baseApiResponseModels.isEmpty()) {
                    if (baseApiResponseModels.get(0) instanceof DocumentsApiResponseModel.ResultBean) {
                        documentsApiResponseModels = (ArrayList<DocumentsApiResponseModel.ResultBean>) (Object) baseApiResponseModels;
                        visitDetailViewModel.setDocumentsApiResponseModels(documentsApiResponseModels);
                    } else if (baseApiResponseModels.get(0) instanceof OrdersUserFormsApiResponseModel) {
                        formsApiResponseModels = (ArrayList<OrdersUserFormsApiResponseModel>) (Object) baseApiResponseModels;
                        visitDetailViewModel.setFormsApiResponseModels(formsApiResponseModels);
                    }
                    visitDetailListAdapter.setData();
                }
            }
        });
    }

    private void computeHeight() {
        Log.e(TAG, "height: " + visitsDetailRv.getHeight() + " " + visitsDetailRv.computeVerticalScrollRange());
        if (!isSecondaryApisCalled) {
            if (visitsDetailRv.computeVerticalScrollRange() < visitsDetailRv.getHeight()) {
                callSecondaryApis();
            }
        }
    }

    private void downloadTranscript(String transcriptUrl, boolean isShowProgress) {
        recentsApiViewModel.downloadTranscriptDetail(transcriptUrl, isShowProgress);
    }

    private void getOrderDetail() {
        if (transcriptionApiResponseModel.getOrder_id() != null) {
            visitsApiViewModel.getOrderDetail(transcriptionApiResponseModel.getOrder_id(), (UserType.isUserAssistant() ? doctorGuid : null), true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visit_detail, container, false);
        initView(view);
        return view;
    }


    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        cancelTv = (TextView) view.findViewById(R.id.cancel_tv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        nextTv = (TextView) view.findViewById(R.id.next_tv);
        printIv = (ImageView) view.findViewById(R.id.close_iv);
        visitsDetailRv = (RecyclerView) view.findViewById(R.id.visits_detail_rv);

        printIv.setImageResource(R.drawable.ic_print_white_24dp);
        nextTv.setText(getString(R.string.edit));
        toolbarTitle.setText(getString(R.string.visit_detail));

        backIv.setOnClickListener(this);
        cancelTv.setOnClickListener(this);
        printIv.setOnClickListener(this);
        nextTv.setOnClickListener(this);

        if (UserType.isUserPatient()) {
            nextTv.setVisibility(View.GONE);
        }

        setDetailAdapter();

        setMode(mode);

        if (getArguments() != null) {
            recentDetail = (RecentsApiResponseModel.ResultBean) getArguments().getSerializable(ArgumentKeys.SELECTED_RECENT_DETAIL);
            visitDetailViewModel.setRecentResponseModel(recentDetail);
            if (recentDetail != null) {
                patientGuid = recentDetail.getPatient().getUser_guid();
                doctorGuid = recentDetail.getDoctor().getUser_guid();

                if (transcriptionApiResponseModel == null)
                    recentsApiViewModel.getTranscriptionDetail(recentDetail.getTranscription_id(), true);

                Set<String> guidSet = new HashSet<>();
                if (patientDetail == null)
                    guidSet.add(patientGuid);
                if (doctorDetail == null)
                    guidSet.add(doctorGuid);

                if (!guidSet.isEmpty()) {
                    GetUserDetails.getInstance(getActivity()).getDetails(guidSet).getHashMapMutableLiveData().observe(this,
                            new Observer<HashMap<String, CommonUserApiResponseModel>>() {
                                @Override
                                public void onChanged(@Nullable HashMap<String, CommonUserApiResponseModel> stringCommonUserApiResponseModelHashMap) {
                                    if (stringCommonUserApiResponseModelHashMap != null) {
                                        if (stringCommonUserApiResponseModelHashMap.containsKey(patientGuid)) {
                                            visitDetailViewModel.setPatientDetailModel(stringCommonUserApiResponseModelHashMap.get(patientGuid));
                                        }
                                        if (stringCommonUserApiResponseModelHashMap.containsKey(doctorGuid)) {
                                            visitDetailViewModel.setDoctorDetailModel(stringCommonUserApiResponseModelHashMap.get(doctorGuid));
                                        }
                                        visitDetailListAdapter.setData();
                                    }
                                }
                            });
                }
            }
        }

    }

    private void setDetailAdapter() {
        visitDetailListAdapter = new VisitDetailListAdapter(getActivity(), this, visitDetailViewModel, mode, new OnModeChangeListener() {
            @Override
            public void onModeChange(int mode) {
                setMode(mode);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        visitsDetailRv.setLayoutManager(linearLayoutManager);

        visitsDetailRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy != 0) {
                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == linearLayoutManager.getItemCount() - 1) {
                        if (!isSecondaryApisCalled) {
                            callSecondaryApis();
                        }
                    }
                }

                if (dy == 0 && isPrimaryApiCalled && !isSecondaryApisCalled && (recyclerView.computeVerticalScrollRange() < recyclerView.getHeight())) {
                    callSecondaryApis();
                }
            }
        });
        visitsDetailRv.setAdapter(visitDetailListAdapter);

    }

    private void callSecondaryApis() {
        isSecondaryApisCalled = true;

        getVitalsDetail();

        getDocumentDetails();

        getFormDetails();

        getDietDetails();

        getReferralOrderDetail();

    }

    private void getReferralOrderDetail() {
        String guidPatient = null, guidDoctor = null;
        if (!UserType.isUserPatient()) {
            guidPatient = patientGuid;
            if (UserType.isUserAssistant()) {
                guidDoctor = doctorGuid;
            }
        }

        List<Integer> otherOrderIdList = new ArrayList<>();
        /**
         * Labs
         */
        if (visitsDetailApiResponseModel.getResult().getLabs() != null) {
            otherOrderIdList.addAll(visitsDetailApiResponseModel.getResult().getLabs());
        }
        /**
         * Specialist
         */
        if (visitsDetailApiResponseModel.getResult().getSpecialists() != null) {
            otherOrderIdList.addAll(visitsDetailApiResponseModel.getResult().getSpecialists());
        }
        /**
         * Xrays
         */
        if (visitsDetailApiResponseModel.getResult().getXrays() != null) {
            otherOrderIdList.addAll(visitsDetailApiResponseModel.getResult().getXrays());
        }
        /**
         * Prescriptions
         */
        if (visitsDetailApiResponseModel.getResult().getPrescriptions() != null) {
            otherOrderIdList.addAll(visitsDetailApiResponseModel.getResult().getPrescriptions());
        }
        /**
         * Miscellaneous
         */
        if (visitsDetailApiResponseModel.getResult().getMiscellaneous() != null) {
            otherOrderIdList.addAll(visitsDetailApiResponseModel.getResult().getMiscellaneous());
        }

        Log.e(TAG, "getReferralOrderDetail: " + new Gson().toJson(otherOrderIdList));
        if (!otherOrderIdList.isEmpty()) {
            ordersApiViewModel.getOrderdsDetail(guidPatient, guidDoctor, otherOrderIdList, true);
        }

    }

    /**
     * Diets
     */
    private void getDietDetails() {

        if (visitsDetailApiResponseModel.getResult().getUser_diets() != null &&
                !visitsDetailApiResponseModel.getResult().getUser_diets().isEmpty()) {
            dietApiViewModel.getUserDietDetails(null, null, null, visitsDetailApiResponseModel.getResult().getUser_diets(), true);
        }

    }

    /**
     * Forms
     */
    private void getFormDetails() {
        String guidPatient = null, guidDoctor = null;
        if (!UserType.isUserPatient()) {
            guidPatient = patientGuid;
            if (UserType.isUserAssistant()) {
                guidDoctor = doctorGuid;
            }
        }
        if (visitsDetailApiResponseModel.getResult().getUser_forms() != null &&
                !visitsDetailApiResponseModel.getResult().getUser_forms().isEmpty()) {
            ordersApiViewModel.getFormsDetail(guidPatient, guidDoctor, visitsDetailApiResponseModel.getResult().getUser_forms(), true);
        }

    }

    /**
     * Documents
     */
    private void getDocumentDetails() {

        if (visitsDetailApiResponseModel.getResult().getUser_files() != null &&
                !visitsDetailApiResponseModel.getResult().getUser_files().isEmpty()) {
            ordersApiViewModel.getDocumentsDetail(null, null, visitsDetailApiResponseModel.getResult().getUser_files(), true);
        }

    }

    /**
     * Vitals
     */
    private void getVitalsDetail() {
        String guidPatient = null, guidDoctor = null;
        if (!UserType.isUserPatient()) {
            guidPatient = patientGuid;
            if (UserType.isUserAssistant()) {
                guidDoctor = doctorGuid;
            }
        }
        if (visitsDetailApiResponseModel.getResult().getUser_vitals() != null &&
                !visitsDetailApiResponseModel.getResult().getUser_vitals().isEmpty()) {
            vitalsApiViewModel.getVitalDetail(guidPatient, guidDoctor, visitsDetailApiResponseModel.getResult().getUser_vitals(), true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onCloseActionInterface.onClose(false);
                break;
            case R.id.cancel_tv:
                visitDetailViewModel.clearAddRemoveList();
                setMode(Constants.VIEW_MODE);
                break;
            case R.id.next_tv:
                switch (mode) {
                    case Constants.VIEW_MODE:
                        setMode(Constants.EDIT_MODE);
                        break;
                    case Constants.EDIT_MODE:
                        updateVisit();
                        break;
                }
                break;
            case R.id.print_iv:
                printPdf();
                break;
        }
    }

    public void setMode(int mode) {
        this.mode = mode;
        updateUI();
    }

    private void updateUI() {
        updateToolbarOptions();

        if (visitDetailListAdapter != null) {
            visitDetailListAdapter.setMode(mode);
        }
    }

    private void updateToolbarOptions() {
        switch (mode) {
            case Constants.VIEW_MODE:
                Utils.hideKeyboard(getActivity());
                backIv.setVisibility(View.VISIBLE);
                cancelTv.setVisibility(View.GONE);
                printIv.setVisibility(View.VISIBLE);
                nextTv.setText(getString(R.string.edit));
                break;
            case Constants.EDIT_MODE:
                backIv.setVisibility(View.GONE);
                cancelTv.setVisibility(View.VISIBLE);
                printIv.setVisibility(View.GONE);
                nextTv.setText(getString(R.string.Save));
                break;
        }
    }

    private void updateVisit() {
        List<Integer> removeList = new ArrayList<>();
        List<Integer> addList = new ArrayList<>();

        if (!visitDetailViewModel.getVitalsRemoveList().isEmpty() || !visitDetailViewModel.getVitalsAddList().isEmpty()) {
            currentUpdateType = VisitDetailConstants.VISIT_TYPE_VITALS;
            removeList = visitDetailViewModel.getVitalsRemoveList();
            addList = visitDetailViewModel.getVitalsAddList();
        } else if (!visitDetailViewModel.getPrescriptionRemoveList().isEmpty() || !visitDetailViewModel.getPrescriptionAddList().isEmpty()) {
            currentUpdateType = VisitDetailConstants.VISIT_TYPE_PRESCRIPTIONS;
            removeList = visitDetailViewModel.getPrescriptionRemoveList();
            addList = visitDetailViewModel.getPrescriptionAddList();
        } else if (!visitDetailViewModel.getSpecialistRemoveList().isEmpty() || !visitDetailViewModel.getSpecialistAddList().isEmpty()) {
            currentUpdateType = VisitDetailConstants.VISIT_TYPE_SPECIALIST;
            removeList = visitDetailViewModel.getSpecialistRemoveList();
            addList = visitDetailViewModel.getSpecialistAddList();
        } else if (!visitDetailViewModel.getLabRemoveList().isEmpty() || !visitDetailViewModel.getLabAddList().isEmpty()) {
            currentUpdateType = VisitDetailConstants.VISIT_TYPE_LABS;
            removeList = visitDetailViewModel.getLabRemoveList();
            addList = visitDetailViewModel.getLabAddList();
        } else if (!visitDetailViewModel.getXrayRemoveList().isEmpty() || !visitDetailViewModel.getXrayAddList().isEmpty()) {
            currentUpdateType = VisitDetailConstants.VISIT_TYPE_XRAYS;
            removeList = visitDetailViewModel.getXrayRemoveList();
            addList = visitDetailViewModel.getXrayAddList();
        } else if (!visitDetailViewModel.getFormsRemoveList().isEmpty() || !visitDetailViewModel.getFormsAddList().isEmpty()) {
            currentUpdateType = VisitDetailConstants.VISIT_TYPE_FORMS;
            removeList = visitDetailViewModel.getFormsRemoveList();
            addList = visitDetailViewModel.getFormsAddList();
        } else if (!visitDetailViewModel.getFilesRemoveList().isEmpty() || !visitDetailViewModel.getFilesAddList().isEmpty()) {
            currentUpdateType = VisitDetailConstants.VISIT_TYPE_FILES;
            removeList = visitDetailViewModel.getFilesRemoveList();
            addList = visitDetailViewModel.getFilesAddList();
        } else if (!visitDetailViewModel.getDietRemoveList().isEmpty() || !visitDetailViewModel.getDietAddList().isEmpty()) {
            currentUpdateType = VisitDetailConstants.VISIT_TYPE_DIETS;
            removeList = visitDetailViewModel.getDietRemoveList();
            addList = visitDetailViewModel.getDietAddList();
        } else if (!visitDetailViewModel.getMiscellaneousRemoveList().isEmpty() || !visitDetailViewModel.getMiscellaneousAddList().isEmpty()) {
            currentUpdateType = VisitDetailConstants.VISIT_TYPE_MISCELLANEOUS;
            removeList = visitDetailViewModel.getMiscellaneousRemoveList();
            addList = visitDetailViewModel.getMiscellaneousAddList();
        }

        if ((currentUpdateType != null && ((removeList != null && !removeList.isEmpty()) || (addList != null && !addList.isEmpty())))
                || visitDetailViewModel.isInstructionUpdated() || visitDetailViewModel.isTranscriptUpdated() ||
                visitDetailViewModel.isTranscriptEdited()) {
            if (!isSuccessViewShown) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.loading));
                bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.updating_your_visit_please_wait));
                isSuccessViewShown = true;
                showSuccessView(this, RequestID.REQ_SHOW_SUCCESS_VIEW, bundle);
            }

            UpdateVisitRequestModel visitRequestModel = new UpdateVisitRequestModel();

            if (currentUpdateType != null && ((removeList != null && !removeList.isEmpty()) || (addList != null && !addList.isEmpty()))) {
                visitRequestModel.setAssociation_type(currentUpdateType);
                visitRequestModel.setRemove_associations(removeList);
                if (removeList != null && !removeList.isEmpty()) {
                    for (int id : removeList) {
                        if (addList != null && addList.contains((Object) id)) {
                            addList.remove((Object) id);
                        }
                    }
                }

                visitRequestModel.setAdd_associations(addList);
            }
            if (visitDetailViewModel.isInstructionUpdated()) {
                visitRequestModel.setInstructions(visitDetailViewModel.getInstruction());
                visitRequestModel.setDiagnosis(visitDetailViewModel.getDiagnosis());
            }

            if (visitDetailViewModel.isTranscriptUpdated() || visitDetailViewModel.isTranscriptEdited()) {

                DownloadTranscriptResponseModel updatedTranscript = new DownloadTranscriptResponseModel();

                updatedTranscript.setTranscripts(visitDetailViewModel.getUpdatedTranscriptResponseModel().getTranscripts());

                List<DownloadTranscriptResponseModel.SpeakerLabelsBean> updatedLabels = new ArrayList<>();

                for (int i = 0; i < visitDetailViewModel.getUpdatedTranscriptResponseModel().getSpeakerLabels().size(); i++) {
                    if (!visitDetailViewModel.getUpdatedTranscriptResponseModel().getSpeakerLabels().get(i).isRemoved()) {
                        updatedLabels.add(visitDetailViewModel.getUpdatedTranscriptResponseModel().getSpeakerLabels().get(i));
                    }
                }
                updatedTranscript.setSpeakerLabels(updatedLabels);

                visitRequestModel.setUpdated_transcript(updatedTranscript);
                visitDetailViewModel.setUpdatedTranscriptResponseModel(updatedTranscript);
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    visitsApiViewModel.updateOrder(recentDetail.getOrder_id(), visitRequestModel, doctorGuid, true);
                }
            }, 2000);
        } else {
            setMode(Constants.VIEW_MODE);
        }
    }

    private boolean isHasNextRequest() {
        return !visitDetailViewModel.getVitalsRemoveList().isEmpty() ||
                !visitDetailViewModel.getVitalsAddList().isEmpty() ||
                !visitDetailViewModel.getPrescriptionRemoveList().isEmpty() ||
                !visitDetailViewModel.getPrescriptionAddList().isEmpty() ||
                !visitDetailViewModel.getSpecialistRemoveList().isEmpty() ||
                !visitDetailViewModel.getSpecialistAddList().isEmpty() ||
                !visitDetailViewModel.getLabRemoveList().isEmpty() ||
                !visitDetailViewModel.getLabAddList().isEmpty() ||
                !visitDetailViewModel.getXrayRemoveList().isEmpty() ||
                !visitDetailViewModel.getXrayAddList().isEmpty() ||
                !visitDetailViewModel.getFormsRemoveList().isEmpty() ||
                !visitDetailViewModel.getFormsAddList().isEmpty() ||
                !visitDetailViewModel.getFilesRemoveList().isEmpty() ||
                !visitDetailViewModel.getFilesAddList().isEmpty() ||
                !visitDetailViewModel.getDietRemoveList().isEmpty() ||
                !visitDetailViewModel.getDietAddList().isEmpty() ||
                !visitDetailViewModel.getMiscellaneousRemoveList().isEmpty() ||
                !visitDetailViewModel.getMiscellaneousAddList().isEmpty();
    }

    private void printPdf() {
        //TODO print pdf here
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RequestID.REQ_SHOW_SUCCESS_VIEW:
                if (resultCode == Activity.RESULT_OK) {
                    visitDetailListAdapter.setData();
                }
                break;
            case RequestID.REQ_VISIT_UPDATE:
                if (resultCode == Activity.RESULT_OK) {
//                    isVisitUpdated = true;
                    updateType = data.getStringExtra(ArgumentKeys.UPDATE_TYPE);
//                    getOrderDetail();
                    switch (updateType) {
                        case VisitDetailConstants.VISIT_TYPE_VITALS:
                            HashMap<Integer, VitalsApiResponseModel> selectedVitals = (HashMap<Integer, VitalsApiResponseModel>) data.getSerializableExtra(ArgumentKeys.SELECTED_VITALS);
                            ArrayList<VitalsApiResponseModel> vitalsList = new ArrayList<>();
                            if (visitDetailViewModel.getVitalsApiResponseModels() != null) {
                                vitalsList.addAll(visitDetailViewModel.getVitalsApiResponseModels());
                            }
                            vitalsList.addAll(selectedVitals.values());
                            visitDetailViewModel.setVitalsAddList(new ArrayList<>(selectedVitals.keySet()));
                            visitDetailViewModel.setVitalsApiResponseModels(vitalsList);
                            break;
                        case VisitDetailConstants.VISIT_TYPE_ORDERS:
                            HashMap<Integer, OrdersPrescriptionApiResponseModel.OrdersResultBean> selectedPrescription = (HashMap<Integer, OrdersPrescriptionApiResponseModel.OrdersResultBean>) data.getSerializableExtra(ArgumentKeys.SELECTED_PRESCRIPTION);
                            HashMap<Integer, OrdersSpecialistApiResponseModel.ResultBean> selectedSpecialist = (HashMap<Integer, OrdersSpecialistApiResponseModel.ResultBean>) data.getSerializableExtra(ArgumentKeys.SELECTED_SPECIALIST);
                            HashMap<Integer, OrdersLabApiResponseModel.LabsResponseBean> selectedLabs = (HashMap<Integer, OrdersLabApiResponseModel.LabsResponseBean>) data.getSerializableExtra(ArgumentKeys.SELECTED_LABS);
                            HashMap<Integer, GetRadiologyResponseModel.ResultBean> selectedXrays = (HashMap<Integer, GetRadiologyResponseModel.ResultBean>) data.getSerializableExtra(ArgumentKeys.SELECTED_XRAYS);
                            HashMap<Integer, MiscellaneousApiResponseModel.ResultBean> selectedMiscellaneous = (HashMap<Integer, MiscellaneousApiResponseModel.ResultBean>) data.getSerializableExtra(ArgumentKeys.SELECTED_MISCELLANEOUS);

                            ArrayList<OrdersLabApiResponseModel.LabsResponseBean> labs = new ArrayList<>();
                            ArrayList<GetRadiologyResponseModel.ResultBean> xrays = new ArrayList<>();
                            ArrayList<OrdersSpecialistApiResponseModel.ResultBean> specialists = new ArrayList<>();
                            ArrayList<OrdersPrescriptionApiResponseModel.OrdersResultBean> prescriptions = new ArrayList<>();
                            ArrayList<MiscellaneousApiResponseModel.ResultBean> miscellaneous = new ArrayList<>();

                            OrdersIdListApiResponseModel ordersIdListApiResponseModel = visitDetailViewModel.getOrdersIdListApiResponseModel();

                            if (ordersIdListApiResponseModel == null) {
                                ordersIdListApiResponseModel = new OrdersIdListApiResponseModel();
                                visitDetailViewModel.setOrdersIdListApiResponseModel(ordersIdListApiResponseModel);
                            }

                            if (visitDetailViewModel.getOrdersIdListApiResponseModel().getPrescriptions() != null &&
                                    !visitDetailViewModel.getOrdersIdListApiResponseModel().getPrescriptions().isEmpty()) {
                                prescriptions.addAll(visitDetailViewModel.getOrdersIdListApiResponseModel().getPrescriptions());
                            }
                            prescriptions.addAll(selectedPrescription.values());
                            visitDetailViewModel.setPrescriptionAddList(new ArrayList<>(selectedPrescription.keySet()));

                            if (visitDetailViewModel.getOrdersIdListApiResponseModel().getSpecialists() != null &&
                                    !visitDetailViewModel.getOrdersIdListApiResponseModel().getSpecialists().isEmpty()) {
                                specialists.addAll(visitDetailViewModel.getOrdersIdListApiResponseModel().getSpecialists());
                            }
                            specialists.addAll(selectedSpecialist.values());
                            visitDetailViewModel.setSpecialistAddList(new ArrayList<>(selectedSpecialist.keySet()));

                            if (visitDetailViewModel.getOrdersIdListApiResponseModel().getLabs() != null &&
                                    !visitDetailViewModel.getOrdersIdListApiResponseModel().getLabs().isEmpty()) {
                                labs.addAll(visitDetailViewModel.getOrdersIdListApiResponseModel().getLabs());
                            }
                            labs.addAll(selectedLabs.values());
                            visitDetailViewModel.setLabAddList(new ArrayList<>(selectedLabs.keySet()));

                            if (visitDetailViewModel.getOrdersIdListApiResponseModel().getXrays() != null &&
                                    !visitDetailViewModel.getOrdersIdListApiResponseModel().getXrays().isEmpty()) {
                                xrays.addAll(visitDetailViewModel.getOrdersIdListApiResponseModel().getXrays());
                            }
                            xrays.addAll(selectedXrays.values());
                            visitDetailViewModel.setXrayAddList(new ArrayList<>(selectedXrays.keySet()));

                            if (visitDetailViewModel.getOrdersIdListApiResponseModel().getMiscellaneous() != null &&
                                    !visitDetailViewModel.getOrdersIdListApiResponseModel().getMiscellaneous().isEmpty()) {
                                miscellaneous.addAll(visitDetailViewModel.getOrdersIdListApiResponseModel().getMiscellaneous());
                            }
                            miscellaneous.addAll(selectedMiscellaneous.values());
                            visitDetailViewModel.setMiscellaneousAddList(new ArrayList<>(selectedMiscellaneous.keySet()));

                            ordersIdListApiResponseModel.setPrescriptions(prescriptions);
                            ordersIdListApiResponseModel.setSpecialists(specialists);
                            ordersIdListApiResponseModel.setLabs(labs);
                            ordersIdListApiResponseModel.setXrays(xrays);
                            ordersIdListApiResponseModel.setMiscellaneous(miscellaneous);

                            visitDetailViewModel.setOrdersIdListApiResponseModel(ordersIdListApiResponseModel);

                            break;
                        case VisitDetailConstants.VISIT_TYPE_DIETS:
                            HashMap<Integer, DietApiResponseModel> selectedDiets = (HashMap<Integer, DietApiResponseModel>) data.getSerializableExtra(ArgumentKeys.SELECTED_DIET);
                            ArrayList<DietApiResponseModel> dietList = new ArrayList<>();
                            if (visitDetailViewModel.getDietApiResponseModels() != null) {
                                dietList.addAll(visitDetailViewModel.getDietApiResponseModels());
                            }
                            dietList.addAll(selectedDiets.values());

                            visitDetailViewModel.setDietApiResponseModels(dietList);
                            visitDetailViewModel.setDietAddList(new ArrayList<>(selectedDiets.keySet()));
                            break;
                        case VisitDetailConstants.VISIT_TYPE_FILES:
                            HashMap<Integer, DocumentsApiResponseModel.ResultBean> selectedFiles = (HashMap<Integer, DocumentsApiResponseModel.ResultBean>) data.getSerializableExtra(ArgumentKeys.SELECTED_FILES);
                            ArrayList<DocumentsApiResponseModel.ResultBean> filesList = new ArrayList<>();
                            if (visitDetailViewModel.getDocumentsApiResponseModels() != null) {
                                filesList.addAll(visitDetailViewModel.getDocumentsApiResponseModels());
                            }
                            filesList.addAll(selectedFiles.values());

                            visitDetailViewModel.setDocumentsApiResponseModels(filesList);
                            visitDetailViewModel.setFilesAddList(new ArrayList<>(selectedFiles.keySet()));
                            break;
                        case VisitDetailConstants.VISIT_TYPE_FORMS:
                            break;
                    }
                    visitDetailListAdapter.setData();
                }
                break;
        }
    }
}
