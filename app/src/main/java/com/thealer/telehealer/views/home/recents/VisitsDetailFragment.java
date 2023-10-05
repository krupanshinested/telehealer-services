package com.thealer.telehealer.views.home.recents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.HistoryBean;
import com.thealer.telehealer.apilayer.models.diet.DietApiResponseModel;
import com.thealer.telehealer.apilayer.models.diet.DietApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersIdListApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersPrescriptionApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersSpecialistApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.documents.DocumentsApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.forms.OrdersUserFormsApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.lab.IcdCodeApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.lab.OrdersLabApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.miscellaneous.MiscellaneousApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.radiology.GetRadiologyResponseModel;
import com.thealer.telehealer.apilayer.models.procedure.ProcedureModel;
import com.thealer.telehealer.apilayer.models.recents.DownloadTranscriptResponseModel;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiResponseModel;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiViewModel;
import com.thealer.telehealer.apilayer.models.recents.VisitDiagnosisModel;
import com.thealer.telehealer.apilayer.models.recents.VisitSummaryApiResponseModel;
import com.thealer.telehealer.apilayer.models.recents.VisitsDetailApiResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiViewModel;
import com.thealer.telehealer.apilayer.models.visits.UpdateVisitRequestModel;
import com.thealer.telehealer.apilayer.models.visits.VisitsApiViewModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiViewModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.GetUserDetails;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.Procedure.ProcedureConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.PdfViewerFragment;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.orders.labs.IcdCodesDataViewModel;

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
    private IcdCodesDataViewModel icdCodesDataViewModel;

    private RecentsApiResponseModel.ResultBean recentDetail;
    private DownloadTranscriptResponseModel downloadTranscriptResponseModel;
    private VisitsDetailApiResponseModel visitsDetailApiResponseModel;
    private SchedulesApiResponseModel.ResultBean schedulesApiResponseModel;
    private ArrayList<VitalsApiResponseModel> vitalsApiResponseModel;
    private OrdersIdListApiResponseModel ordersIdListApiResponseModel;
    private ArrayList<DocumentsApiResponseModel.ResultBean> documentsApiResponseModels;
    private ArrayList<OrdersUserFormsApiResponseModel> formsApiResponseModels;
    private ArrayList<DietApiResponseModel> dietApiResponseModels;

    private VisitDetailListAdapter visitDetailListAdapter;
    private String patientGuid, doctorGuid;
    private int mode = Constants.VIEW_MODE;
    private boolean isSecondaryApisCalled = false, isSuccessViewShown = false, isVisitUpdated = false, isPrimaryApiCalled = false;
    private String currentUpdateType = null, updateType;
    List<Integer> removeList = new ArrayList<>();
    List<Integer> addList = new ArrayList<>();
    private boolean isdataUpdate = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachObserverInterface = (AttachObserverInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();

        icdCodesDataViewModel = new ViewModelProvider(getActivity()).get(IcdCodesDataViewModel.class);
        icdCodesDataViewModel.setSelectedIcdCodeList(new MediatorLiveData<>());
        icdCodesDataViewModel.setIcdCodeDetailHashMap(new MediatorLiveData<>());

        visitDetailViewModel = new ViewModelProvider(this).get(VisitDetailViewModel.class);

        dietApiViewModel = new ViewModelProvider(this).get(DietApiViewModel.class);
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

        schedulesApiViewModel = new ViewModelProvider(this).get(SchedulesApiViewModel.class);
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

        visitsApiViewModel = new ViewModelProvider(this).get(VisitsApiViewModel.class);
        attachObserverInterface.attachObserver(visitsApiViewModel);
        visitsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof VisitSummaryApiResponseModel) {

                        VisitSummaryApiResponseModel visitSummaryApiResponseModel = (VisitSummaryApiResponseModel) baseApiResponseModel;
                        showPdfViewFragment(visitSummaryApiResponseModel.getResult().getVisit_summary());

                    } else if (baseApiResponseModel instanceof VisitsDetailApiResponseModel) {
                        visitsDetailApiResponseModel = (VisitsDetailApiResponseModel) baseApiResponseModel;
                        visitDetailViewModel.setVisitsDetailApiResponseModel(visitsDetailApiResponseModel);

                        visitDetailViewModel.setDiagnosisData();
                        if (visitDetailViewModel.getDiagnosis() != null &&
                                visitDetailViewModel.getDiagnosis().getICD10_codes() != null &&
                                !visitDetailViewModel.getDiagnosis().getICD10_codes().isEmpty()) {

                            List<String> selectedIcdCodes = new ArrayList<>();
                            HashMap<String, IcdCodeApiResponseModel.ResultsBean> map = new HashMap<>();

                            for (int i = 0; i < visitDetailViewModel.getDiagnosis().getICD10_codes().size(); i++) {
                                selectedIcdCodes.add(visitDetailViewModel.getDiagnosis().getICD10_codes().get(i).getCode());
                                map.put(visitDetailViewModel.getDiagnosis().getICD10_codes().get(i).getCode(), visitDetailViewModel.getDiagnosis().getICD10_codes().get(i));
                            }

                            icdCodesDataViewModel.getSelectedIcdCodeList().setValue(selectedIcdCodes);
                            icdCodesDataViewModel.getIcdCodeDetailHashMap().setValue(map);
                        }


                        if (visitsDetailApiResponseModel.getResult().getProcedure() != null)
                            visitDetailViewModel.setSelectedCptCodeList(visitsDetailApiResponseModel.getResult().getProcedure().getCPT_codes());

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
                                    getVisitDetail();
                                   break;
                            }
                        } else if ((Object) visitsDetailApiResponseModel.getResult().getSchedule_id() != null &&
                                visitsDetailApiResponseModel.getResult().getSchedule_id() != 0) {
                            schedulesApiViewModel.getScheduleDetail(visitsDetailApiResponseModel.getResult().getSchedule_id(), doctorGuid, true);
                        } else {
                            isPrimaryApiCalled = true;
                        }

                        if (isdataUpdate){
                            if (visitDetailListAdapter!=null){
                                visitDetailListAdapter.notifyDataSetChanged();
                                isdataUpdate = false;
                            }
                        }

                    } else {
                        if (baseApiResponseModel.isSuccess()) {
                            updatePrescription();
                        }
                    }
                }
            }
        });

        visitsApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    if (errorModel.geterrorCode() == null){
                        isSuccessViewShown = false;
                        sendSuccessViewBroadCast(getActivity(), false, getString(R.string.failure), getString(R.string.visit_update_failed));
                    }else if (!errorModel.geterrorCode().isEmpty() && !errorModel.geterrorCode().equals("SUBSCRIPTION")) {
                        isSuccessViewShown = false;
                        sendSuccessViewBroadCast(getActivity(), false, getString(R.string.failure), getString(R.string.visit_update_failed));
                    }
                }
            }
        });

        recentsApiViewModel = new ViewModelProvider(this).get(RecentsApiViewModel.class);
        attachObserverInterface.attachObserver(recentsApiViewModel);

        recentsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof VisitsDetailApiResponseModel) {

                        visitsDetailApiResponseModel = (VisitsDetailApiResponseModel) baseApiResponseModel;

                        visitDetailViewModel.setVisitsDetailApiResponseModel(visitsDetailApiResponseModel);
                        visitDetailListAdapter.setData();

                        if (visitsDetailApiResponseModel.getResult().getStatus() != null &&
                                visitsDetailApiResponseModel.getResult().getTranscript() != null) {

                            downloadTranscript(visitsDetailApiResponseModel.getResult().getTranscript(), true);
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
                    if (errorModel.geterrorCode() == null){
                        showToast(errorModel.getMessage());
                    }else if (!errorModel.geterrorCode().isEmpty() && !errorModel.geterrorCode().equals("SUBSCRIPTION")) {
                        showToast(errorModel.getMessage());
                    }
                }
            }
        });

        vitalsApiViewModel = new ViewModelProvider(this).get(VitalsApiViewModel.class);
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

        ordersApiViewModel = new ViewModelProvider(this).get(OrdersApiViewModel.class);
        ordersApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    ordersIdListApiResponseModel = (OrdersIdListApiResponseModel) baseApiResponseModel;
                    visitDetailViewModel.setOrdersIdListApiResponseModel((OrdersIdListApiResponseModel) baseApiResponseModel);
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
                        visitDetailViewModel.setFormsApiResponseModels((ArrayList<OrdersUserFormsApiResponseModel>) (Object) baseApiResponseModels);
                    }
                    visitDetailListAdapter.setData();
                }
            }
        });
    }

    private void downloadTranscript(String transcriptUrl, boolean isShowProgress) {
        if (!TextUtils.isEmpty(transcriptUrl)) {
            recentsApiViewModel.downloadTranscriptDetail(transcriptUrl, isShowProgress);
        }
    }

    private void getVisitDetail() {
        if (visitDetailViewModel.getVisitsDetailApiResponseModel() == null)
            visitsApiViewModel.getVisitApiDetail(recentDetail.getOrder_id(), (UserType.isUserAssistant() ? doctorGuid : null), true);
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

        if (getArguments() != null) {
            recentDetail = (RecentsApiResponseModel.ResultBean) getArguments().getSerializable(ArgumentKeys.SELECTED_RECENT_DETAIL);
            visitDetailViewModel.setRecentResponseModel(recentDetail);
            if (recentDetail != null) {
                patientGuid = recentDetail.getPatient().getUser_guid();
                doctorGuid = recentDetail.getDoctor().getUser_guid();

                getVisitDetail();

                checkForPatientDetail();

                checkForDoctorDetail();
            }
        }

        setDetailAdapter();

        setMode(mode);

    }

    private void checkForDoctorDetail() {
        if (visitDetailViewModel.getDoctorDetailModel() == null) {
            Set<String> guidSet = new HashSet<>();
            guidSet.add(doctorGuid);
            if (!guidSet.isEmpty()) {
                GetUserDetails
                        .getInstance(getActivity())
                        .getDetails(guidSet)
                        .getHashMapMutableLiveData().observe(this,
                        new Observer<HashMap<String, CommonUserApiResponseModel>>() {
                            @Override
                            public void onChanged(@Nullable HashMap<String, CommonUserApiResponseModel> stringCommonUserApiResponseModelHashMap) {
                                if (stringCommonUserApiResponseModelHashMap != null) {
                                    if (stringCommonUserApiResponseModelHashMap.containsKey(doctorGuid)) {
                                        visitDetailViewModel.setDoctorDetailModel(stringCommonUserApiResponseModelHashMap.get(doctorGuid));
                                        visitDetailListAdapter.setData();
                                    }
                                }
                            }
                        });
            }

        }
    }

    private void checkForPatientDetail() {
        if (visitDetailViewModel.getPatientDetailModel() == null) {

            if (UserType.isUserPatient()) {
                WhoAmIApiResponseModel whoAmIApiResponseModel = UserDetailPreferenceManager.getWhoAmIResponse();
                visitDetailViewModel.setPatientDetailModel(whoAmIApiResponseModel);
                visitDetailViewModel.setHistoryList(visitDetailViewModel.getUpdatedHistoryModelList(whoAmIApiResponseModel.getHistory()));
                visitDetailListAdapter.setData();
            } else {
                Set<String> guidSet = new HashSet<>();
                guidSet.add(patientGuid);

                if (!guidSet.isEmpty()) {
                    GetUserDetails
                            .getInstance(getActivity())
                            .getAssociationDetail(guidSet, (UserType.isUserAssistant() ? doctorGuid : null))
                            .getHashMapMutableLiveData().observe(this,
                            new Observer<HashMap<String, CommonUserApiResponseModel>>() {
                                @Override
                                public void onChanged(@Nullable HashMap<String, CommonUserApiResponseModel> stringCommonUserApiResponseModelHashMap) {
                                    if (stringCommonUserApiResponseModelHashMap != null) {
                                        if (stringCommonUserApiResponseModelHashMap.containsKey(patientGuid)) {

                                            CommonUserApiResponseModel userModel = stringCommonUserApiResponseModelHashMap.get(patientGuid);

                                            visitDetailViewModel.setPatientDetailModel(userModel);

                                            visitDetailViewModel.setHistoryList(visitDetailViewModel.getUpdatedHistoryModelList(userModel.getHistory()));
                                            visitDetailListAdapter.setData();
                                        }
                                    }
                                }
                            });
                }
            }
        }
    }

    private void setDetailAdapter() {
        visitDetailListAdapter = new VisitDetailListAdapter(getActivity(), this, visitDetailViewModel, mode);
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

        if (visitsDetailApiResponseModel != null && visitsDetailApiResponseModel.getResult() != null) {
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
    }

    /**
     * Diets
     */
    private void getDietDetails() {

        if (visitsDetailApiResponseModel != null && visitsDetailApiResponseModel.getResult() != null && visitsDetailApiResponseModel.getResult().getUser_diets() != null &&
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
        if (visitsDetailApiResponseModel != null && visitsDetailApiResponseModel.getResult() != null && visitsDetailApiResponseModel.getResult().getUser_forms() != null &&
                !visitsDetailApiResponseModel.getResult().getUser_forms().isEmpty()) {
            ordersApiViewModel.getFormsDetail(guidPatient, guidDoctor, visitsDetailApiResponseModel.getResult().getUser_forms(), true);
        }

    }

    /**
     * Documents
     */
    private void getDocumentDetails() {

        if (visitsDetailApiResponseModel != null && visitsDetailApiResponseModel.getResult() != null && visitsDetailApiResponseModel.getResult().getUser_files() != null &&
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
        if (visitsDetailApiResponseModel != null && visitsDetailApiResponseModel.getResult() != null && visitsDetailApiResponseModel.getResult().getUser_vitals() != null &&
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
//                        updateVisit();
                        updateVitals();
                        break;
                }
                break;
            case R.id.close_iv:
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

    private void updateVitals(){

        removeList.clear();
        addList.clear();
        currentUpdateType = null;

        if (!visitDetailViewModel.getVitalsRemoveList().isEmpty() || !visitDetailViewModel.getVitalsAddList().isEmpty()) {
            currentUpdateType = VisitDetailConstants.VISIT_TYPE_VITALS;
            removeList = visitDetailViewModel.getVitalsRemoveList();
            addList = visitDetailViewModel.getVitalsAddList();
            updateVisit();
        }else {
            updatePrescription();
        }
    }

    private void updatePrescription(){

        removeList.clear();
        addList.clear();
        currentUpdateType = null;

        if (!visitDetailViewModel.getPrescriptionRemoveList().isEmpty() || !visitDetailViewModel.getPrescriptionAddList().isEmpty()) {
            currentUpdateType = VisitDetailConstants.VISIT_TYPE_PRESCRIPTIONS;
            removeList = visitDetailViewModel.getPrescriptionRemoveList();
            addList = visitDetailViewModel.getPrescriptionAddList();
            updateVisit();
        }else {
            updateSpecialist();
        }
    }

    private void updateSpecialist(){

        removeList.clear();
        addList.clear();
        currentUpdateType = null;

        if (!visitDetailViewModel.getSpecialistRemoveList().isEmpty() || !visitDetailViewModel.getSpecialistAddList().isEmpty()) {
            currentUpdateType = VisitDetailConstants.VISIT_TYPE_SPECIALIST;
            removeList = visitDetailViewModel.getSpecialistRemoveList();
            addList = visitDetailViewModel.getSpecialistAddList();
            updateVisit();
        }else {
            updateLab();
        }
    }

    private void updateLab(){

        removeList.clear();
        addList.clear();
        currentUpdateType = null;

        if (!visitDetailViewModel.getLabRemoveList().isEmpty() || !visitDetailViewModel.getLabAddList().isEmpty()) {
            currentUpdateType = VisitDetailConstants.VISIT_TYPE_LABS;
            removeList = visitDetailViewModel.getLabRemoveList();
            addList = visitDetailViewModel.getLabAddList();
            updateVisit();
        }else {
            updateXray();
        }
    }

    private void updateXray(){

        removeList.clear();
        addList.clear();
        currentUpdateType = null;

        if (!visitDetailViewModel.getXrayRemoveList().isEmpty() || !visitDetailViewModel.getXrayAddList().isEmpty()) {
            currentUpdateType = VisitDetailConstants.VISIT_TYPE_XRAYS;
            removeList = visitDetailViewModel.getXrayRemoveList();
            addList = visitDetailViewModel.getXrayAddList();
            updateVisit();
        }else {
            updateForms();
        }
    }

    private void updateForms(){

        removeList.clear();
        addList.clear();
        currentUpdateType = null;

        if (!visitDetailViewModel.getFormsRemoveList().isEmpty() || !visitDetailViewModel.getFormsAddList().isEmpty()) {
            currentUpdateType = VisitDetailConstants.VISIT_TYPE_FORMS;
            removeList = visitDetailViewModel.getFormsRemoveList();
            addList = visitDetailViewModel.getFormsAddList();
            updateVisit();
        }else {
            updateFiles();
        }
    }

    private void updateFiles(){

        removeList.clear();
        addList.clear();
        currentUpdateType = null;

        if (!visitDetailViewModel.getFilesRemoveList().isEmpty() || !visitDetailViewModel.getFilesAddList().isEmpty()) {
            currentUpdateType = VisitDetailConstants.VISIT_TYPE_FILES;
            removeList = visitDetailViewModel.getFilesRemoveList();
            addList = visitDetailViewModel.getFilesAddList();
            updateVisit();
        }else {
            updateDiet();
        }
    }

    private void updateDiet(){

        removeList.clear();
        addList.clear();
        currentUpdateType = null;

        if (!visitDetailViewModel.getDietRemoveList().isEmpty() || !visitDetailViewModel.getDietAddList().isEmpty()) {
            currentUpdateType = VisitDetailConstants.VISIT_TYPE_DIETS;
            removeList = visitDetailViewModel.getDietRemoveList();
            addList = visitDetailViewModel.getDietAddList();
            updateVisit();
        }else {
            updateMiscellaneous();
        }
    }

    private void updateMiscellaneous(){

        removeList.clear();
        addList.clear();
        currentUpdateType = null;

        if (!visitDetailViewModel.getMiscellaneousRemoveList().isEmpty() || ! ) {
            currentUpdateType = VisitDetailConstants.VISIT_TYPE_MISCELLANEOUS;
            removeList = visitDetailViewModel.getMiscellaneousRemoveList();
            addList = visitDetailViewModel.getMiscellaneousAddList();
            updateVisit();
        }else {
            if (isSuccessViewShown){
                updateFinalStatus();
            }else {
                setMode(Constants.VIEW_MODE);
            }
        }
    }

    private void updateFinalStatus(){
        if (currentUpdateType != null && ordersIdListApiResponseModel != null) {
            switch (currentUpdateType) {
                case VisitDetailConstants.VISIT_TYPE_VITALS:
                    visitDetailViewModel.removeVital();
                    break;
                case VisitDetailConstants.VISIT_TYPE_DIETS:
                    visitDetailViewModel.removeDiets();
                    break;
                case VisitDetailConstants.VISIT_TYPE_FORMS:
                    visitDetailViewModel.removeForms();
                    formsApiResponseModels.clear();
                    formsApiResponseModels.addAll(visitDetailViewModel.getFormsApiResponseModels());
                    break;
                case VisitDetailConstants.VISIT_TYPE_FILES:
                    visitDetailViewModel.removeFiles();
                    break;
                case VisitDetailConstants.VISIT_TYPE_LABS:
                    visitDetailViewModel.removeLabs();
                    ordersIdListApiResponseModel.setLabs(visitDetailViewModel.getOrdersIdListApiResponseModel().getLabs());
                    break;
                case VisitDetailConstants.VISIT_TYPE_XRAYS:
                    visitDetailViewModel.removeXrays();
                    ordersIdListApiResponseModel.setXrays(visitDetailViewModel.getOrdersIdListApiResponseModel().getXrays());
                    break;
                case VisitDetailConstants.VISIT_TYPE_SPECIALIST:
                    visitDetailViewModel.removeSpecialist();
                    ordersIdListApiResponseModel.setSpecialists(visitDetailViewModel.getOrdersIdListApiResponseModel().getSpecialists());
                    break;
                case VisitDetailConstants.VISIT_TYPE_PRESCRIPTIONS:
                    visitDetailViewModel.removePrescription();
                    ordersIdListApiResponseModel.setPrescriptions(visitDetailViewModel.getOrdersIdListApiResponseModel().getPrescriptions());
                    break;
                case VisitDetailConstants.VISIT_TYPE_MISCELLANEOUS:
                    visitDetailViewModel.removeMiscellaneous();
                    ordersIdListApiResponseModel.setMiscellaneous(visitDetailViewModel.getOrdersIdListApiResponseModel().getMiscellaneous());
                    break;
            }
        }
        if (isHasNextRequest()) {
//                                updateVisit();
            getVisitDetail();
            isSuccessViewShown = false;
            sendSuccessViewBroadCast(getActivity(), true, getString(R.string.success), getString(R.string.visit_updated_successfully));
            setMode(Constants.VIEW_MODE);
        } else {
            if (visitDetailViewModel.isInstructionUpdated()) {
                visitDetailViewModel.setInstructionUpdated(false);
                visitsDetailApiResponseModel.getResult().setInstructions(visitDetailViewModel.getInstruction());
                visitDetailViewModel.setVisitsDetailApiResponseModel(visitsDetailApiResponseModel);
            }

            if (visitDetailViewModel.isDiagnosisUpdated()) {
                visitDetailViewModel.setDiagnosisUpdated(false);
                visitsDetailApiResponseModel.getResult().setDiagnosis(visitDetailViewModel.getDiagnosis());
                visitDetailViewModel.setVisitsDetailApiResponseModel(visitsDetailApiResponseModel);
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

                downloadTranscriptResponseModel = updatedTranscript;
                visitDetailViewModel.setDownloadTranscriptResponseModel(updatedTranscript);
                visitDetailViewModel.setUpdatedTranscriptResponseModel(updatedTranscript);

                Log.e(TAG, "onChanged: " + new Gson().toJson(updatedTranscript.getSpeakerLabels()));

            }

            if (visitDetailViewModel.isHistoryUpdate()) {
                List<HistoryBean> historyBeanList = new ArrayList<>();
                for (int i = 0; i < visitDetailViewModel.getHistoryList().size(); i++) {
                    historyBeanList.add(new HistoryBean(visitDetailViewModel.getHistoryList().get(i).isIsYes(),
                            visitDetailViewModel.getHistoryList().get(i).getQuestion(),
                            visitDetailViewModel.getHistoryList().get(i).getReason()));
                }
                visitDetailViewModel.getPatientDetailModel().setHistory(historyBeanList);
            }


            if (visitDetailViewModel.isProcedureUpdated()) {
                visitsDetailApiResponseModel.getResult().setProcedure(new ProcedureModel(visitDetailViewModel.getSelectedCptCodeList()));
            }

            isSuccessViewShown = false;
            sendSuccessViewBroadCast(getActivity(), true, getString(R.string.success), getString(R.string.visit_updated_successfully));
            setMode(Constants.VIEW_MODE);
        }
    }

    private void updateVisit() {
//        List<Integer> removeList = new ArrayList<>();
//        List<Integer> addList = new ArrayList<>();
//
//        if (!visitDetailViewModel.getVitalsRemoveList().isEmpty() || !visitDetailViewModel.getVitalsAddList().isEmpty()) {
//            currentUpdateType = VisitDetailConstants.VISIT_TYPE_VITALS;
//            removeList = visitDetailViewModel.getVitalsRemoveList();
//            addList = visitDetailViewModel.getVitalsAddList();
//        } else if (!visitDetailViewModel.getPrescriptionRemoveList().isEmpty() || !visitDetailViewModel.getPrescriptionAddList().isEmpty()) {
//            currentUpdateType = VisitDetailConstants.VISIT_TYPE_PRESCRIPTIONS;
//            removeList = visitDetailViewModel.getPrescriptionRemoveList();
//            addList = visitDetailViewModel.getPrescriptionAddList();
//        } else if (!visitDetailViewModel.getSpecialistRemoveList().isEmpty() || !visitDetailViewModel.getSpecialistAddList().isEmpty()) {
//            currentUpdateType = VisitDetailConstants.VISIT_TYPE_SPECIALIST;
//            removeList = visitDetailViewModel.getSpecialistRemoveList();
//            addList = visitDetailViewModel.getSpecialistAddList();
//        } else if (!visitDetailViewModel.getLabRemoveList().isEmpty() || !visitDetailViewModel.getLabAddList().isEmpty()) {
//            currentUpdateType = VisitDetailConstants.VISIT_TYPE_LABS;
//            removeList = visitDetailViewModel.getLabRemoveList();
//            addList = visitDetailViewModel.getLabAddList();
//        } else if (!visitDetailViewModel.getXrayRemoveList().isEmpty() || !visitDetailViewModel.getXrayAddList().isEmpty()) {
//            currentUpdateType = VisitDetailConstants.VISIT_TYPE_XRAYS;
//            removeList = visitDetailViewModel.getXrayRemoveList();
//            addList = visitDetailViewModel.getXrayAddList();
//        } else if (!visitDetailViewModel.getFormsRemoveList().isEmpty() || !visitDetailViewModel.getFormsAddList().isEmpty()) {
//            currentUpdateType = VisitDetailConstants.VISIT_TYPE_FORMS;
//            removeList = visitDetailViewModel.getFormsRemoveList();
//            addList = visitDetailViewModel.getFormsAddList();
//        } else if (!visitDetailViewModel.getFilesRemoveList().isEmpty() || !visitDetailViewModel.getFilesAddList().isEmpty()) {
//            currentUpdateType = VisitDetailConstants.VISIT_TYPE_FILES;
//            removeList = visitDetailViewModel.getFilesRemoveList();
//            addList = visitDetailViewModel.getFilesAddList();
//        } else if (!visitDetailViewModel.getDietRemoveList().isEmpty() || !visitDetailViewModel.getDietAddList().isEmpty()) {
//            currentUpdateType = VisitDetailConstants.VISIT_TYPE_DIETS;
//            removeList = visitDetailViewModel.getDietRemoveList();
//            addList = visitDetailViewModel.getDietAddList();
//        } else if (!visitDetailViewModel.getMiscellaneousRemoveList().isEmpty() || !visitDetailViewModel.getMiscellaneousAddList().isEmpty()) {
//            currentUpdateType = VisitDetailConstants.VISIT_TYPE_MISCELLANEOUS;
//            removeList = visitDetailViewModel.getMiscellaneousRemoveList();
//            addList = visitDetailViewModel.getMiscellaneousAddList();
//        }

        if ((currentUpdateType != null && ((removeList != null && !removeList.isEmpty()) || (addList != null && !addList.isEmpty())))
                || visitDetailViewModel.isInstructionUpdated() || visitDetailViewModel.isDiagnosisUpdated() || visitDetailViewModel.isTranscriptUpdated() ||
                visitDetailViewModel.isTranscriptEdited() || visitDetailViewModel.isHistoryUpdate() || visitDetailViewModel.isProcedureUpdated()) {

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
            }

            if (visitDetailViewModel.isDiagnosisUpdated()) {
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
            }

            if (visitDetailViewModel.isHistoryUpdate()) {
                visitRequestModel.setPatient_history(visitDetailViewModel.getHistoryList());
            }

            if (visitDetailViewModel.isProcedureUpdated()) {
                visitRequestModel.setProcedure(new ProcedureModel(visitDetailViewModel.getSelectedCptCodeList()));
            }

            Log.e(TAG, "visitRequestModel: " + new Gson().toJson(visitRequestModel));
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
        visitsApiViewModel.getVisitSummary(recentDetail.getOrder_id(), true);
    }

    private void showPdfViewFragment(String pdfUrl) {
        PdfViewerFragment pdfViewerFragment = new PdfViewerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ArgumentKeys.PDF_TITLE, getString(R.string.visit));
        bundle.putString(ArgumentKeys.PDF_URL, pdfUrl);
        bundle.putBoolean(ArgumentKeys.IS_PDF_DECRYPT, true);

        pdfViewerFragment.setArguments(bundle);
        showSubFragmentInterface.onShowFragment(pdfViewerFragment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RequestID.REQ_SHOW_SUCCESS_VIEW:
                if (resultCode == Activity.RESULT_OK) {
//                    visitDetailListAdapter.setData();
                    isdataUpdate = true;
                    visitsApiViewModel.getVisitApiDetail(recentDetail.getOrder_id(), (UserType.isUserAssistant() ? doctorGuid : null), true);
                    visitDetailListAdapter.notifyDataSetChanged();
                }
                break;
            case RequestID.REQ_SELECT_CPT_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<String> selectedCptCode = data.getStringArrayListExtra(ArgumentKeys.SELECTED_ITEMS);

                    List<ProcedureModel.CPTCodesBean> codesBeanList = new ArrayList<>();

                    for (int i = 0; i < selectedCptCode.size(); i++) {
                        codesBeanList.add(new ProcedureModel.CPTCodesBean(selectedCptCode.get(i), ProcedureConstants.getDescription(getActivity(), selectedCptCode.get(i))));
                    }
                    visitDetailViewModel.setSelectedCptCodeList(codesBeanList);
                    visitDetailListAdapter.setData();
                }
                break;
            case RequestID.REQ_SELECT_ICD_CODE:
                VisitDiagnosisModel visitDiagnosisModel = new VisitDiagnosisModel();
                List<IcdCodeApiResponseModel.ResultsBean> list = new ArrayList<>();

                if (icdCodesDataViewModel.getSelectedIcdCodeList().getValue() != null) {
                    for (int i = 0; i < icdCodesDataViewModel.getSelectedIcdCodeList().getValue().size(); i++) {
                        list.add(icdCodesDataViewModel.getIcdCodeDetailHashMap().getValue().get(icdCodesDataViewModel.getSelectedIcdCodeList().getValue().get(i)));
                    }
                }

                visitDiagnosisModel.setICD10_codes(list);
                visitDetailViewModel.setDiagnosis(visitDiagnosisModel);

                if (visitsDetailApiResponseModel.getResult().getDiagnosis() == null && visitDetailViewModel.getDiagnosis() != null) {
                    visitDetailViewModel.setDiagnosisUpdated(true);

                } else if (visitsDetailApiResponseModel.getResult().getDiagnosis() != null) {

                    if (visitsDetailApiResponseModel.getResult().getDiagnosis() instanceof VisitDiagnosisModel) {
                        VisitDiagnosisModel diagnosisModel = (VisitDiagnosisModel) visitsDetailApiResponseModel.getResult().getDiagnosis();
                        List<String> newICD = icdCodesDataViewModel.getSelectedIcdCodeList().getValue();
                        List<String> oldICD = new ArrayList<>();
                        for (int i = 0; i < diagnosisModel.getICD10_codes().size(); i++) {
                            oldICD.add(diagnosisModel.getICD10_codes().get(i).getCode());
                        }

                        for (int i = 0; i < newICD.size(); i++) {
                            if (!oldICD.contains(newICD.get(i))) {
                                visitDetailViewModel.setDiagnosisUpdated(true);
                                break;
                            }
                        }
                    } else {
                        visitDetailViewModel.setDiagnosisUpdated(true);
                    }
                }

                visitDetailListAdapter.setData();

                break;
            case RequestID.REQ_VISIT_UPDATE:
                if (resultCode == Activity.RESULT_OK) {
                    updateType = data.getStringExtra(ArgumentKeys.UPDATE_TYPE);
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

                            visitDetailViewModel.setPrescriptionsMap(selectedPrescription);
                            visitDetailViewModel.setSpecialistsMap(selectedSpecialist);
                            visitDetailViewModel.setLabsMap(selectedLabs);
                            visitDetailViewModel.setXraysMap(selectedXrays);
                            visitDetailViewModel.setMiscellaneousMap(selectedMiscellaneous);

                            //Selected orders
                            ArrayList<OrdersLabApiResponseModel.LabsResponseBean> labs = new ArrayList<>();
                            ArrayList<GetRadiologyResponseModel.ResultBean> xrays = new ArrayList<>();
                            ArrayList<OrdersSpecialistApiResponseModel.ResultBean> specialists = new ArrayList<>();
                            ArrayList<OrdersPrescriptionApiResponseModel.OrdersResultBean> prescriptions = new ArrayList<>();
                            ArrayList<MiscellaneousApiResponseModel.ResultBean> miscellaneous = new ArrayList<>();

                            OrdersIdListApiResponseModel ordersIdListApiResponseModel = new OrdersIdListApiResponseModel();

                            if (this.ordersIdListApiResponseModel != null) {
                                if (this.ordersIdListApiResponseModel.getLabs() != null)
                                    ordersIdListApiResponseModel.setLabs(this.ordersIdListApiResponseModel.getLabs());
                                if (this.ordersIdListApiResponseModel.getPrescriptions() != null)
                                    ordersIdListApiResponseModel.setPrescriptions(this.ordersIdListApiResponseModel.getPrescriptions());
                                if (this.ordersIdListApiResponseModel.getSpecialists() != null)
                                    ordersIdListApiResponseModel.setSpecialists(this.ordersIdListApiResponseModel.getSpecialists());
                                if (this.ordersIdListApiResponseModel.getMiscellaneous() != null)
                                    ordersIdListApiResponseModel.setMiscellaneous(this.ordersIdListApiResponseModel.getMiscellaneous());
                                if (this.ordersIdListApiResponseModel.getXrays() != null)
                                    ordersIdListApiResponseModel.setXrays(this.ordersIdListApiResponseModel.getXrays());
                            }
                            if (ordersIdListApiResponseModel.getPrescriptions() != null &&
                                    !ordersIdListApiResponseModel.getPrescriptions().isEmpty()) {
                                prescriptions.addAll(ordersIdListApiResponseModel.getPrescriptions());
                            }
                            prescriptions.addAll(selectedPrescription.values());
                            visitDetailViewModel.setPrescriptionAddList(new ArrayList<>(selectedPrescription.keySet()));

                            if (ordersIdListApiResponseModel.getSpecialists() != null &&
                                    !ordersIdListApiResponseModel.getSpecialists().isEmpty()) {
                                specialists.addAll(ordersIdListApiResponseModel.getSpecialists());
                            }
                            specialists.addAll(selectedSpecialist.values());
                            visitDetailViewModel.setSpecialistAddList(new ArrayList<>(selectedSpecialist.keySet()));

                            if (ordersIdListApiResponseModel.getLabs() != null &&
                                    !ordersIdListApiResponseModel.getLabs().isEmpty()) {
                                labs.addAll(ordersIdListApiResponseModel.getLabs());
                            }
                            labs.addAll(selectedLabs.values());
                            visitDetailViewModel.setLabAddList(new ArrayList<>(selectedLabs.keySet()));

                            if (ordersIdListApiResponseModel.getXrays() != null &&
                                    !ordersIdListApiResponseModel.getXrays().isEmpty()) {
                                xrays.addAll(ordersIdListApiResponseModel.getXrays());
                            }
                            xrays.addAll(selectedXrays.values());
                            visitDetailViewModel.setXrayAddList(new ArrayList<>(selectedXrays.keySet()));

                            if (ordersIdListApiResponseModel.getMiscellaneous() != null &&
                                    !ordersIdListApiResponseModel.getMiscellaneous().isEmpty()) {
                                miscellaneous.addAll(ordersIdListApiResponseModel.getMiscellaneous());
                            }
                            miscellaneous.addAll(selectedMiscellaneous.values());
                            visitDetailViewModel.setMiscellaneousAddList(new ArrayList<>(selectedMiscellaneous.keySet()));

                            ordersIdListApiResponseModel.setPrescriptions(prescriptions);
                            ordersIdListApiResponseModel.setSpecialists(specialists);
                            ordersIdListApiResponseModel.setLabs(labs);
                            ordersIdListApiResponseModel.setXrays(xrays);
                            ordersIdListApiResponseModel.setMiscellaneous(miscellaneous);

                            visitDetailViewModel.setOrdersIdListApiResponseModel(ordersIdListApiResponseModel);

                            //selected forms
                            if (data.getSerializableExtra(ArgumentKeys.SELECTED_FORMS) != null) {
                                HashMap<Integer, OrdersUserFormsApiResponseModel> selectedForms = (HashMap<Integer, OrdersUserFormsApiResponseModel>) data.getSerializableExtra(ArgumentKeys.SELECTED_FORMS);

                                visitDetailViewModel.setFormMap(selectedForms);

                                ArrayList<OrdersUserFormsApiResponseModel> forms = new ArrayList<>();

                                if (formsApiResponseModels != null && !formsApiResponseModels.isEmpty()) {
                                    forms.addAll(visitDetailViewModel.getFormsApiResponseModels());
                                }
                                forms.addAll(selectedForms.values());
                                visitDetailViewModel.setFormsAddList(new ArrayList<>(selectedForms.keySet()));
                                visitDetailViewModel.setFormsApiResponseModels(forms);
                            }

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
