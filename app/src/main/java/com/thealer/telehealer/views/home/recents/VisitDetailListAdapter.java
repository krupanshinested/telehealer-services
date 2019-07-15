package com.thealer.telehealer.views.home.recents;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.HistoryBean;
import com.thealer.telehealer.apilayer.models.diet.DietApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.documents.DocumentsApiResponseModel;
import com.thealer.telehealer.apilayer.models.procedure.ProcedureModel;
import com.thealer.telehealer.apilayer.models.recents.DownloadTranscriptResponseModel;
import com.thealer.telehealer.apilayer.models.recents.VisitDiagnosisModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitalReport.VitalReportApiViewModel;
import com.thealer.telehealer.apilayer.models.vitals.StethBean;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiResponseModel;
import com.thealer.telehealer.common.Animation.CustomUserListItemView;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomEditText;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.views.Procedure.SelectProceduresFragment;
import com.thealer.telehealer.views.common.LabelValueCustomView;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.DoctorPatientDetailViewFragment;
import com.thealer.telehealer.views.home.monitoring.diet.DietDetailFragment;
import com.thealer.telehealer.views.home.monitoring.diet.DietDetailModel;
import com.thealer.telehealer.views.home.monitoring.diet.DietListingFragment;
import com.thealer.telehealer.views.home.monitoring.diet.FoodConstant;
import com.thealer.telehealer.views.home.orders.OrderConstant;
import com.thealer.telehealer.views.home.orders.OrderSelectionListFragment;
import com.thealer.telehealer.views.home.orders.OrderStatus;
import com.thealer.telehealer.views.home.orders.document.DocumentListFragment;
import com.thealer.telehealer.views.home.orders.document.ViewDocumentFragment;
import com.thealer.telehealer.views.home.orders.labs.SelectIcdCodeFragment;
import com.thealer.telehealer.views.home.recents.adapterModels.AddNewModel;
import com.thealer.telehealer.views.home.recents.adapterModels.CallSummaryModel;
import com.thealer.telehealer.views.home.recents.adapterModels.LabelValueModel;
import com.thealer.telehealer.views.home.recents.adapterModels.VisitOrdersAdapterModel;
import com.thealer.telehealer.views.home.vitals.StethoscopeDetailViewFragment;
import com.thealer.telehealer.views.home.vitals.vitalReport.VitalUserReportListFragment;
import com.thealer.telehealer.views.settings.medicalHistory.MedicalHistoryList;
import com.thealer.telehealer.views.settings.medicalHistory.MedicalHistoryViewFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Aswin on 25,April,2019
 */
class VisitDetailListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private FragmentActivity activity;
    private VisitDetailViewModel detailViewModel;
    private List<VisitDetailAdapterModel> adapterModelList = new ArrayList<>();
    private ShowSubFragmentInterface showSubFragmentInterface;
    private int mode;
    private Fragment targetFragment;

    public VisitDetailListAdapter(FragmentActivity activity, Fragment targetFragment,
                                  VisitDetailViewModel visitDetailViewModel, int mode) {
        this.activity = activity;
        showSubFragmentInterface = (ShowSubFragmentInterface) activity;
        this.detailViewModel = visitDetailViewModel;
        this.mode = mode;
        this.targetFragment = targetFragment;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view;
        switch (viewType) {
            case VisitDetailConstants.TYPE_HEADER:
                view = inflater.inflate(R.layout.visit_list_adapter_category_view, viewGroup, false);
                return new HeaderViewHolder(view);
            case VisitDetailConstants.TYPE_CALL_SUMMARY:
                view = inflater.inflate(R.layout.visit_list_adapter_call_summary_view, viewGroup, false);
                return new CallSummaryViewHolder(view);
            case VisitDetailConstants.TYPE_USER_INFO:
                view = inflater.inflate(R.layout.adapter_doctor_patient_list, viewGroup, false);
                return new UserInfoViewHolder(view);
            case VisitDetailConstants.TYPE_HEALTH_SUMMARY:
                view = inflater.inflate(R.layout.adapter_health_summary_button_view, viewGroup, false);
                return new HealthSummaryViewHolder(view);
            case VisitDetailConstants.TYPE_LABEL_VALUE:
                view = inflater.inflate(R.layout.visit_list_adapter_label_value_view, viewGroup, false);
                return new LabelValueViewHolder(view);
            case VisitDetailConstants.TYPE_HISTORY_ITEM:
                view = inflater.inflate(R.layout.visit_list_adapter_patient_history_view, viewGroup, false);
                return new HistoryItemViewHolder(view);
            case VisitDetailConstants.TYPE_TRANSCRIPT:
                view = inflater.inflate(R.layout.visit_list_adapter_transcript_view, viewGroup, false);
                return new TranscriptViewHolder(view);
            case VisitDetailConstants.TYPE_INSTRUCTION:
                view = inflater.inflate(R.layout.visit_list_adapter_input_view, viewGroup, false);
                return new InstructionViewHolder(view);
            case VisitDetailConstants.TYPE_DIAGNOSIS:
                view = inflater.inflate(R.layout.visit_list_diagnosis_view, viewGroup, false);
                return new DiagnosisViewHolder(view);
            case VisitDetailConstants.TYPE_ADD:
                view = inflater.inflate(R.layout.adapter_item_add_view, viewGroup, false);
                return new AddViewHolder(view);
            case VisitDetailConstants.TYPE_DATE:
                view = inflater.inflate(R.layout.adapter_list_header_view, viewGroup, false);
                return new DateViewHolder(view);
            case VisitDetailConstants.TYPE_VITALS:
                view = inflater.inflate(R.layout.adapter_vitals_list_view, viewGroup, false);
                return new VitalsViewHolder(view);
            case VisitDetailConstants.TYPE_DIET:
                view = inflater.inflate(R.layout.adapter_diet_selection_list, viewGroup, false);
                return new DietViewHolder(view);
            case VisitDetailConstants.TYPE_ORDER:
                view = inflater.inflate(R.layout.adapter_editable_order_list_view, viewGroup, false);
                return new OrderViewHolder(view);
            case VisitDetailConstants.TYPE_DOCUMENT:
                view = inflater.inflate(R.layout.adapter_document_list, viewGroup, false);
                return new DocumentViewHolder(view);
            case VisitDetailConstants.TYPE_INFO:
                view = inflater.inflate(R.layout.visit_list_adapter_info_view, viewGroup, false);
                return new InfoViewHolder(view);
            case VisitDetailConstants.TYPE_PROCEDURE:
                view = inflater.inflate(R.layout.adapter_procedure_view, viewGroup, false);
                return new ProcedureViewHolder(view);
        }
        return null;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof TranscriptViewHolder) {
            TranscriptViewHolder viewHolder = (TranscriptViewHolder) holder;
            if (viewHolder.transcriptInfoEt != null)
                viewHolder.transcriptInfoEt.clearAllTextChangedListener();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {

        VisitDetailAdapterModel visitDetailAdapterModel = adapterModelList.get(i);
        switch (visitDetailAdapterModel.getViewType()) {
            case VisitDetailConstants.TYPE_HEADER:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.categoryTv.setText(visitDetailAdapterModel.getCategoryTitle());
                switch (mode) {
                    case Constants.VIEW_MODE:
                        if (visitDetailAdapterModel.isShow()) {
                            headerViewHolder.categoryTv.setVisibility(View.VISIBLE);
                        } else {
                            headerViewHolder.categoryTv.setVisibility(View.GONE);
                        }
                        break;
                    case Constants.EDIT_MODE:
                        headerViewHolder.categoryTv.setVisibility(View.VISIBLE);
                        break;
                }
                break;
            case VisitDetailConstants.TYPE_CALL_SUMMARY:
                CallSummaryViewHolder callSummaryViewHolder = (CallSummaryViewHolder) holder;

                CallSummaryModel callSummaryModel = visitDetailAdapterModel.getCallSummaryModel();
                callSummaryViewHolder.dateTv.setText(Utils.getDayMonthYear(callSummaryModel.getCallStartTime()));
                if (callSummaryModel.getCallCategory() != null && !UserType.isUserPatient()) {
                    callSummaryViewHolder.callTypeTv.setText(callSummaryModel.getCallCategory());
                } else {
                    callSummaryViewHolder.callTypeTv.setVisibility(View.GONE);
                }
                String timeDisplayFormat = "%s to %s ( %s )";
                callSummaryViewHolder.callTimeTv.setText(String.format(timeDisplayFormat, Utils.getFormatedTime(callSummaryModel.getCallStartTime()),
                        Utils.getFormatedTime(callSummaryModel.getCallEndTime()), Utils.getDisplayDuration(callSummaryModel.getDurationInSec())));
                callSummaryViewHolder.transcriptVideoIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecentDetailView recentDetailView = new RecentDetailView();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(ArgumentKeys.SELECTED_RECENT_DETAIL, detailViewModel.getRecentResponseModel());
                        recentDetailView.setArguments(bundle);
                        showSubFragmentInterface.onShowFragment(recentDetailView);
                    }
                });
                if (visitDetailAdapterModel.getCallSummaryModel().getScreenShot() != null) {
                    Utils.setImageWithGlide(activity, callSummaryViewHolder.transcriptVideoIv, visitDetailAdapterModel.getCallSummaryModel().getScreenShot(), null, true, false);
                }
                break;
            case VisitDetailConstants.TYPE_USER_INFO:
                UserInfoViewHolder userInfoViewHolder = (UserInfoViewHolder) holder;

                userInfoViewHolder.userListIv.setListTitleTv(visitDetailAdapterModel.getUserInfoModel().getDisplayName());
                userInfoViewHolder.userListIv.setListSubTitleTv(visitDetailAdapterModel.getUserInfoModel().getDisplayInfo());
                Utils.setImageWithGlide(activity.getApplicationContext(), userInfoViewHolder.userListIv.getAvatarCiv(),
                        visitDetailAdapterModel.getUserInfoModel().getUser_avatar(), activity.getDrawable(R.drawable.profile_placeholder), true, true);

                if (visitDetailAdapterModel.getUserInfoModel().getRole().equals(Constants.ROLE_PATIENT)) {
                    Utils.setGenderImage(activity, userInfoViewHolder.userListIv.getActionIv(), visitDetailAdapterModel.getUserInfoModel().getGender());
                    userInfoViewHolder.userListIv.getActionIv().setVisibility(View.VISIBLE);
                }

                userInfoViewHolder.userListIv.showStatus(false);
                userInfoViewHolder.userListIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DoctorPatientDetailViewFragment doctorPatientDetailViewFragment = new DoctorPatientDetailViewFragment();
                        Bundle detailBundle = new Bundle();
                        detailBundle.putBoolean(ArgumentKeys.CHECK_CONNECTION_STATUS, true);

                        detailBundle.putSerializable(Constants.USER_DETAIL, visitDetailAdapterModel.getUserInfoModel());
                        if (UserType.isUserAssistant()) {
                            if (visitDetailAdapterModel.getUserInfoModel().getRole().equals(Constants.ROLE_PATIENT)) {
                                detailBundle.putSerializable(Constants.DOCTOR_DETAIL, detailViewModel.getDoctorDetailModel());
                            }
                        }

                        detailBundle.putSerializable(Constants.VIEW_TYPE, Constants.VIEW_ASSOCIATION_DETAIL);
                        doctorPatientDetailViewFragment.setArguments(detailBundle);
                        showSubFragmentInterface.onShowFragment(doctorPatientDetailViewFragment);
                    }
                });
                break;
            case VisitDetailConstants.TYPE_HEALTH_SUMMARY:
                HealthSummaryViewHolder healthSummaryViewHolder = (HealthSummaryViewHolder) holder;

                healthSummaryViewHolder.medicalHistoryBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUserApiResponseModel userDetail = detailViewModel.getPatientDetailModel();

                        Fragment fragment;

                        Bundle bundle = new Bundle();
                        bundle.putBoolean(ArgumentKeys.IS_UPDATE_VISIT, true);
                        bundle.putSerializable(Constants.USER_DETAIL, userDetail);
                        bundle.putString(ArgumentKeys.ORDER_ID, String.valueOf(detailViewModel.getRecentResponseModel().getOrder_id()));

                        if (UserType.isUserAssistant() || (userDetail.getQuestionnaire() != null && userDetail.getQuestionnaire().isQuestionariesEmpty())) {
                            fragment = new MedicalHistoryViewFragment();
                        } else {
                            fragment = new MedicalHistoryList();
                        }
                        fragment.setArguments(bundle);

                        showSubFragmentInterface.onShowFragment(fragment);
                    }
                });
                break;
            case VisitDetailConstants.TYPE_LABEL_VALUE:

                LabelValueViewHolder labelValueViewHolder = (LabelValueViewHolder) holder;

                LabelValueModel labelValueModel = adapterModelList.get(i).getLabelValueModel();
                labelValueViewHolder.labelValueCv.setLabelText(labelValueModel.getLabel());
                labelValueViewHolder.labelValueCv.setValueText(labelValueModel.getValue());
                labelValueViewHolder.labelValueCv.setBottomViewVisible(labelValueModel.isShowBottomView());
                break;
            case VisitDetailConstants.TYPE_HISTORY_ITEM:

                HistoryItemViewHolder historyItemViewHolder = (HistoryItemViewHolder) holder;

                HistoryBean historyBean = adapterModelList.get(i).getHistoryBean();

                TextWatcher historyTextWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        for (int j = 0; j < detailViewModel.getHistoryList().size(); j++) {
                            if (detailViewModel.getHistoryList().get(j).getQuestion().equals(historyBean.getQuestion())) {
                                detailViewModel.getHistoryList().get(j).setReason(s.toString());
                                break;
                            }
                        }
                    }
                };
                historyItemViewHolder.commentsEt.clearAllTextChangedListener();

                if (mode == Constants.VIEW_MODE) {
                    historyItemViewHolder.viewModeCl.setVisibility(View.VISIBLE);
                    historyItemViewHolder.editModeCl.setVisibility(View.GONE);

                    if (historyBean.isIsYes()) {
                        historyItemViewHolder.historyQuestionTv.setText(historyBean.getQuestion());
                        historyItemViewHolder.historyAnswerTv.setText(historyBean.isIsYes() ? activity.getString(R.string.yes) : activity.getString(R.string.no));


                        if (historyBean.getReason() != null && !historyBean.getReason().trim().isEmpty()) {
                            historyItemViewHolder.historyReasonTv.setVisibility(View.VISIBLE);
                            historyItemViewHolder.historyReasonTv.setText(String.format("( %s )", historyBean.getReason()));
                        } else {
                            historyItemViewHolder.historyReasonTv.setText("");
                        }
                    }

                } else {
                    historyItemViewHolder.viewModeCl.setVisibility(View.GONE);
                    historyItemViewHolder.editModeCl.setVisibility(View.VISIBLE);

                    historyItemViewHolder.itemSwitch.setText(historyBean.getQuestion());
                    historyItemViewHolder.itemSwitch.setChecked(historyBean.isIsYes());
                    historyItemViewHolder.commentsEt.setText(historyBean.getReason());

                    if (historyBean.isIsYes()) {
                        historyItemViewHolder.commentsEt.setVisibility(View.VISIBLE);
                    } else {
                        historyItemViewHolder.commentsEt.setVisibility(View.GONE);
                    }

                    historyItemViewHolder.itemSwitch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (historyItemViewHolder.itemSwitch.isChecked()) {
                                historyItemViewHolder.commentsEt.setVisibility(View.VISIBLE);
                            } else {
                                historyItemViewHolder.commentsEt.setVisibility(View.GONE);
                                historyItemViewHolder.commentsEt.setText(null);
                            }

                            for (int j = 0; j < detailViewModel.getHistoryList().size(); j++) {
                                if (detailViewModel.getHistoryList().get(j).getQuestion().equals(historyBean.getQuestion())) {
                                    detailViewModel.getHistoryList().get(j).setIsYes(historyItemViewHolder.itemSwitch.isChecked());
                                    break;
                                }
                            }

                        }
                    });

                    historyItemViewHolder.commentsEt.addTextChangedListener(historyTextWatcher);
                }
                break;
            case VisitDetailConstants.TYPE_TRANSCRIPT:

                TranscriptViewHolder transcriptViewHolder = (TranscriptViewHolder) holder;

                DownloadTranscriptResponseModel.SpeakerLabelsBean speakerLabelsBean = adapterModelList.get(i).getTranscriptModel();
                transcriptViewHolder.callerNameTv.setText(speakerLabelsBean.getSpeakerName(detailViewModel.getTranscriptionApiResponseModel()));
                transcriptViewHolder.visitCb.setChecked(!speakerLabelsBean.isRemoved());

                TextWatcher transcriptTextWatcher;

                transcriptTextWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        for (int j = 0; j < detailViewModel.getUpdatedTranscriptResponseModel().getSpeakerLabels().size(); j++) {
                            DownloadTranscriptResponseModel.SpeakerLabelsBean labelsBean = detailViewModel.getUpdatedTranscriptResponseModel().getSpeakerLabels().get(j);
                            if (speakerLabelsBean.getSpeaker_label().equals(labelsBean.getSpeaker_label()) &&
                                    speakerLabelsBean.getStart_time().equals(labelsBean.getStart_time()) &&
                                    speakerLabelsBean.getEnd_time().equals(labelsBean.getEnd_time())) {

                                labelsBean.setTranscript(s.toString());
                                break;
                            }
                        }
                    }
                };

                transcriptViewHolder.transcriptInfoEt.addTextChangedListener(transcriptTextWatcher);

                transcriptViewHolder.transcriptInfoEt.setText(speakerLabelsBean.getTranscript());

                switch (mode) {
                    case Constants.VIEW_MODE:
                        transcriptViewHolder.transcriptRootCl.setOnClickListener(null);
                        transcriptViewHolder.visitCb.setVisibility(View.GONE);
                        transcriptViewHolder.transcriptInfoEt.setEnabled(false);
                        transcriptViewHolder.transcriptInfoEt.setBackground(null);
                        break;
                    case Constants.EDIT_MODE:
                        transcriptViewHolder.visitCb.setVisibility(View.VISIBLE);
                        transcriptViewHolder.transcriptInfoEt.setEnabled(true);
                        transcriptViewHolder.transcriptRootCl.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                transcriptViewHolder.visitCb.setChecked(!transcriptViewHolder.visitCb.isChecked());
                                for (int j = 0; j < detailViewModel.getUpdatedTranscriptResponseModel().getSpeakerLabels().size(); j++) {
                                    DownloadTranscriptResponseModel.SpeakerLabelsBean labelsBean = detailViewModel.getUpdatedTranscriptResponseModel().getSpeakerLabels().get(j);
                                    if (speakerLabelsBean.isModelEqual(labelsBean)) {
                                        labelsBean.setRemoved(!labelsBean.isRemoved());
                                        break;
                                    }
                                    detailViewModel.getUpdatedTranscriptResponseModel().getSpeakerLabels().set(j, labelsBean);
                                }
                            }
                        });
                        transcriptViewHolder.transcriptInfoEt.setBackgroundColor(activity.getColor(R.color.card_view_background));
                        break;
                }
                break;
            case VisitDetailConstants.TYPE_INSTRUCTION:

                InstructionViewHolder instructionViewHolder = (InstructionViewHolder) holder;

                TextWatcher textWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        boolean isUpdate = false;

                        if (visitDetailAdapterModel.getCategoryTitle() == null && !s.toString().isEmpty()) {
                            isUpdate = true;
                        } else if (visitDetailAdapterModel.getCategoryTitle() != null) {
                            if (!visitDetailAdapterModel.getCategoryTitle().equals(s.toString())) {
                                isUpdate = true;
                            }
                        }

                        detailViewModel.setInstructionUpdated(isUpdate);
                        detailViewModel.setInstruction(s.toString().trim());
                    }
                };

                switch (mode) {
                    case Constants.VIEW_MODE:
                        instructionViewHolder.instructionEt.setEnabled(false);
                        instructionViewHolder.instructionEt.removeTextChangedListener(textWatcher);
                        instructionViewHolder.instructionEt.setBackground(null);
                        break;
                    case Constants.EDIT_MODE:
                        instructionViewHolder.instructionEt.setEnabled(true);
                        instructionViewHolder.instructionEt.setVisibility(View.VISIBLE);
                        instructionViewHolder.instructionEt.addTextChangedListener(textWatcher);
                        instructionViewHolder.instructionEt.setBackgroundColor(activity.getColor(R.color.card_view_background));
                        break;
                }

                String text;

                if (detailViewModel.isInstructionUpdated()) {
                    text = detailViewModel.getInstruction();
                } else {
                    text = visitDetailAdapterModel.getCategoryTitle();
                }

                instructionViewHolder.instructionEt.setText(text);

                break;
            case VisitDetailConstants.TYPE_DIAGNOSIS:

                DiagnosisViewHolder diagnosisViewHolder = (DiagnosisViewHolder) holder;

                diagnosisViewHolder.icdListLl.removeAllViews();

                for (int j = 0; j < visitDetailAdapterModel.getVisitDiagnosisModel().getICD10_codes().size(); j++) {
                    View view = LayoutInflater.from(activity).inflate(R.layout.adapter_lab_icd_code_list, null);
                    TextView icdCodeTv;
                    TextView icdDetailTv;

                    icdCodeTv = (TextView) view.findViewById(R.id.icd_code_tv);
                    icdDetailTv = (TextView) view.findViewById(R.id.icd_detail_tv);

                    icdCodeTv.setText(visitDetailAdapterModel.getVisitDiagnosisModel().getICD10_codes().get(j).getCode());
                    icdDetailTv.setText(visitDetailAdapterModel.getVisitDiagnosisModel().getICD10_codes().get(j).getDescription());

                    diagnosisViewHolder.icdListLl.addView(view);
                }

                if (mode == Constants.EDIT_MODE) {
                    diagnosisViewHolder.icdListLl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showIcdSelection();
                        }
                    });
                    diagnosisViewHolder.arrowIv.setVisibility(View.VISIBLE);
                } else {
                    diagnosisViewHolder.arrowIv.setVisibility(View.GONE);
                }
                break;
            case VisitDetailConstants.TYPE_ADD:
                AddViewHolder addViewHolder = (AddViewHolder) holder;
                switch (mode) {
                    case Constants.VIEW_MODE:
                        addViewHolder.addItemTv.setVisibility(View.GONE);
                        break;
                    case Constants.EDIT_MODE:
                        addViewHolder.addItemTv.setVisibility(View.VISIBLE);
                        break;
                }

                AddNewModel addNewModel = adapterModelList.get(i).getAddNewModel();
                addViewHolder.addItemTv.setText(addNewModel.getTitle());
                addViewHolder.btAddCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants.USER_DETAIL, detailViewModel.getPatientDetailModel());
                        bundle.putSerializable(Constants.DOCTOR_DETAIL, detailViewModel.getDoctorDetailModel());
                        bundle.putString(ArgumentKeys.SEARCH_TYPE, VitalReportApiViewModel.LAST_WEEK);
                        bundle.putString(ArgumentKeys.DOCTOR_GUID, detailViewModel.getDoctorDetailModel().getUser_guid());
                        bundle.putString(ArgumentKeys.ORDER_ID, detailViewModel.getTranscriptionApiResponseModel().getOrder_id());
                        bundle.putBoolean(ArgumentKeys.DISABLE_CANCEL, true);

                        switch (addNewModel.getType()) {
                            case VisitDetailConstants.ADD_VITAL:
                                VitalUserReportListFragment vitalUserReportListFragment = new VitalUserReportListFragment();
                                vitalUserReportListFragment.setTargetFragment(targetFragment, RequestID.REQ_VISIT_UPDATE);
                                bundle.putBoolean(ArgumentKeys.IS_SHOW_FILTER, true);
                                vitalUserReportListFragment.setArguments(bundle);
                                showSubFragmentInterface.onShowFragment(vitalUserReportListFragment);
                                break;
                            case VisitDetailConstants.ADD_ORDER:
                                OrderSelectionListFragment orderSelectionListFragment = new OrderSelectionListFragment();
                                orderSelectionListFragment.setArguments(bundle);
                                orderSelectionListFragment.setTargetFragment(targetFragment, RequestID.REQ_VISIT_UPDATE);
                                showSubFragmentInterface.onShowFragment(orderSelectionListFragment);
                                break;
                            case VisitDetailConstants.ADD_DIET:
                                DietListingFragment dietListingFragment = new DietListingFragment();
                                bundle.putBoolean(ArgumentKeys.IS_SHOW_FILTER, true);
                                dietListingFragment.setArguments(bundle);
                                dietListingFragment.setTargetFragment(targetFragment, RequestID.REQ_VISIT_UPDATE);
                                showSubFragmentInterface.onShowFragment(dietListingFragment);
                                break;
                            case VisitDetailConstants.ADD_DOCUMENTS:
                                DocumentListFragment documentListFragment = new DocumentListFragment();
                                documentListFragment.setArguments(bundle);
                                documentListFragment.setTargetFragment(targetFragment, RequestID.REQ_VISIT_UPDATE);
                                showSubFragmentInterface.onShowFragment(documentListFragment);
                                break;
                            case VisitDetailConstants.ADD_DIAGNOSIS:
                                showIcdSelection();
                                break;
                            case VisitDetailConstants.ADD_PROCEDURE:
                                showProcedureSelectionView();
                                break;
                        }
                    }
                });
                break;
            case VisitDetailConstants.TYPE_DATE:
                DateViewHolder dateViewHolder = (DateViewHolder) holder;
                dateViewHolder.dateTv.setText(adapterModelList.get(i).getCategoryTitle());
                break;
            case VisitDetailConstants.TYPE_VITALS:
                VitalsViewHolder vitalsViewHolder = (VitalsViewHolder) holder;

                VitalsApiResponseModel vitalsApiResponseModel = adapterModelList.get(i).getVitalsApiResponseModel();

                vitalsViewHolder.timeTv.setText(Utils.getFormatedTime(vitalsApiResponseModel.getCreated_at()));
                vitalsViewHolder.descriptionTv.setText(vitalsApiResponseModel.getCapturedBy(activity));

                if (!vitalsApiResponseModel.getType().equals(SupportedMeasurementType.stethoscope)) {
                    vitalsViewHolder.valueTv.setText(vitalsApiResponseModel.getValue().toString());
                    vitalsViewHolder.unitTv.setText(SupportedMeasurementType.getVitalUnit(vitalsApiResponseModel.getType()));
                } else {
                    StethBean stethBean = vitalsApiResponseModel.getStethBean();

                    vitalsViewHolder.unitTv.setText(stethBean.getSegments().size() + " - Segment");

                    vitalsViewHolder.vitalIv.setImageDrawable(activity.getDrawable(vitalsApiResponseModel.getStethIoImage()));
                    vitalsViewHolder.vitalIv.setVisibility(View.VISIBLE);

                    if (mode == Constants.VIEW_MODE) {
                        vitalsViewHolder.itemCv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                StethoscopeDetailViewFragment stethoscopeDetailViewFragment = new StethoscopeDetailViewFragment();

                                Bundle bundle = new Bundle();
                                bundle.putSerializable(ArgumentKeys.VITAL_DETAIL, vitalsApiResponseModel);
                                stethoscopeDetailViewFragment.setArguments(bundle);

                                showSubFragmentInterface.onShowFragment(stethoscopeDetailViewFragment);
                            }
                        });
                    }
                }

                if (vitalsApiResponseModel.isAbnormal()) {
                    vitalsViewHolder.abnormalIndicatorCl.setVisibility(View.VISIBLE);
                }

                vitalsViewHolder.visitCb.setChecked(true);
                vitalsViewHolder.visitCb.setClickable(false);

                switch (mode) {
                    case Constants.VIEW_MODE:
                        vitalsViewHolder.visitCb.setVisibility(View.GONE);
                        break;
                    case Constants.EDIT_MODE:
                        vitalsViewHolder.visitCb.setVisibility(View.VISIBLE);
                        break;
                }
                vitalsViewHolder.vitalRootCl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = vitalsApiResponseModel.getUser_vital_id();
                        if (detailViewModel.getVitalsRemoveList().contains(id)) {
                            detailViewModel.getVitalsRemoveList().remove((Object) id);
                            vitalsViewHolder.visitCb.setChecked(true);
                        } else {
                            detailViewModel.getVitalsRemoveList().add(id);
                            vitalsViewHolder.visitCb.setChecked(false);
                        }
                    }
                });

                if (!vitalsApiResponseModel.getType().equals(SupportedMeasurementType.stethoscope)) {
                    vitalsViewHolder.itemCv.setCardElevation(0);
                    vitalsViewHolder.itemCv.setRadius(0);
                }
                break;
            case VisitDetailConstants.TYPE_ORDER:

                OrderViewHolder orderViewHolder = (OrderViewHolder) holder;

                VisitOrdersAdapterModel visitOrdersAdapterModel = adapterModelList.get(i).getVisitOrdersAdapterModel();
                orderViewHolder.orderListIv.setImageResource(visitOrdersAdapterModel.getDisplayImage());
                orderViewHolder.orderListTitleTv.setText(visitOrdersAdapterModel.getDisplayTitle());
                orderViewHolder.orderListSubTitleTv.setText(UserType.isUserPatient() ? detailViewModel.getDoctorDetailModel().getUserDisplay_name() : detailViewModel.getPatientDetailModel().getUserDisplay_name());
                int statusImage = 0;
                switch (visitOrdersAdapterModel.getOrderType()) {
                    case OrderConstant.ORDER_PRESCRIPTIONS:
                        statusImage = OrderStatus.getStatusImage(visitOrdersAdapterModel.getPrescriptions().getStatus());
                        break;
                    case OrderConstant.ORDER_REFERRALS:
                        statusImage = OrderStatus.getStatusImage(visitOrdersAdapterModel.getSpecialists().getStatus());
                        break;
                    case OrderConstant.ORDER_LABS:
                        statusImage = OrderStatus.getStatusImage(visitOrdersAdapterModel.getLabs().getStatus());
                        break;
                    case OrderConstant.ORDER_RADIOLOGY:
                        statusImage = OrderStatus.getStatusImage(visitOrdersAdapterModel.getXrays().getStatus());
                        break;
                    case OrderConstant.ORDER_MISC:
                        statusImage = OrderStatus.getStatusImage(visitOrdersAdapterModel.getMiscellaneous().getStatus());
                        break;
                    case OrderConstant.ORDER_FORM:
                        statusImage = OrderStatus.getStatusImage(visitOrdersAdapterModel.getForms().getStatus());
                        if (visitOrdersAdapterModel.getForms().isCompleted()) {
                            orderViewHolder.orderListOptionTitleTv.setVisibility(View.VISIBLE);
                            orderViewHolder.orderListOptionSubTitleTv.setVisibility(View.VISIBLE);

                            orderViewHolder.orderListOptionTitleTv.setText(visitOrdersAdapterModel.getForms().getData().getDisplayScore());
                            orderViewHolder.orderListOptionSubTitleTv.setText(activity.getString(R.string.score));

                        }
                        break;
                }
                if (statusImage != 0) {
                    orderViewHolder.orderStatusIv.setImageDrawable(activity.getDrawable(statusImage));
                    if (statusImage == R.drawable.ic_status_pending) {
                        orderViewHolder.orderStatusIv.setImageTintList(ColorStateList.valueOf(activity.getColor(R.color.app_gradient_start)));
                    }
                    orderViewHolder.orderStatusIv.setVisibility(View.VISIBLE);
                }

                orderViewHolder.visitCb.setChecked(true);
                orderViewHolder.visitCb.setClickable(false);
                switch (mode) {
                    case Constants.VIEW_MODE:
                        orderViewHolder.visitCb.setVisibility(View.GONE);
                        orderViewHolder.orderListItemCv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Fragment fragment = visitOrdersAdapterModel.getDetailViewFragment();
                                Bundle bundle = visitOrdersAdapterModel.getDetailViewBundle();
                                bundle.putSerializable(Constants.USER_DETAIL, detailViewModel.getPatientDetailModel());
                                bundle.putSerializable(Constants.DOCTOR_DETAIL, detailViewModel.getDoctorDetailModel());

                                if (fragment != null) {
                                    fragment.setArguments(bundle);
                                    showSubFragmentInterface.onShowFragment(fragment);
                                }
                            }
                        });
                        break;
                    case Constants.EDIT_MODE:
                        orderViewHolder.visitCb.setVisibility(View.VISIBLE);
                        orderViewHolder.orderListItemCv.setOnClickListener(null);
                        orderViewHolder.orderListItemCv.setClickable(false);
                        orderViewHolder.orderListItemCv.setFocusable(false);
                        break;
                }
                orderViewHolder.orderRootCl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id;
                        boolean checked = false;
                        switch (visitOrdersAdapterModel.getOrderType()) {
                            case OrderConstant.ORDER_PRESCRIPTIONS:
                                id = visitOrdersAdapterModel.getPrescriptions().getReferral_id();

                                if (detailViewModel.getPrescriptionRemoveList().contains(id)) {
                                    detailViewModel.getPrescriptionRemoveList().remove((Object) id);
                                    checked = true;
                                } else {
                                    checked = false;
                                    detailViewModel.getPrescriptionRemoveList().add(id);
                                }
                                break;
                            case OrderConstant.ORDER_REFERRALS:
                                id = visitOrdersAdapterModel.getSpecialists().getReferral_id();

                                if (detailViewModel.getSpecialistRemoveList().contains(id)) {
                                    detailViewModel.getSpecialistRemoveList().remove((Object) id);
                                    checked = true;
                                } else {
                                    checked = false;
                                    detailViewModel.getSpecialistRemoveList().add(id);
                                }
                                break;
                            case OrderConstant.ORDER_LABS:
                                id = visitOrdersAdapterModel.getLabs().getReferral_id();

                                if (detailViewModel.getLabRemoveList().contains(id)) {
                                    detailViewModel.getLabRemoveList().remove((Object) id);
                                    checked = true;
                                } else {
                                    checked = false;
                                    detailViewModel.getLabRemoveList().add(id);
                                }
                                break;
                            case OrderConstant.ORDER_RADIOLOGY:
                                id = visitOrdersAdapterModel.getXrays().getReferral_id();

                                if (detailViewModel.getXrayRemoveList().contains(id)) {
                                    detailViewModel.getXrayRemoveList().remove((Object) id);
                                    checked = true;
                                } else {
                                    checked = false;
                                    detailViewModel.getXrayRemoveList().add(id);
                                }
                                break;
                            case OrderConstant.ORDER_FORM:
                                id = visitOrdersAdapterModel.getForms().getUser_form_id();

                                if (detailViewModel.getFormsRemoveList().contains(id)) {
                                    detailViewModel.getFormsRemoveList().remove((Object) id);
                                    checked = true;
                                } else {
                                    checked = false;
                                    detailViewModel.getFormsRemoveList().add(id);
                                }
                                break;
                            case OrderConstant.ORDER_MISC:
                                id = visitOrdersAdapterModel.getMiscellaneous().getReferral_id();

                                if (detailViewModel.getMiscellaneousRemoveList().contains(id)) {
                                    detailViewModel.getMiscellaneousRemoveList().remove((Object) id);
                                    checked = true;
                                } else {
                                    checked = false;
                                    detailViewModel.getMiscellaneousRemoveList().add(id);
                                }
                                break;
                        }
                        orderViewHolder.visitCb.setChecked(checked);
                    }
                });
                break;
            case VisitDetailConstants.TYPE_DOCUMENT:

                DocumentViewHolder documentViewHolder = (DocumentViewHolder) holder;

                documentViewHolder.recentHeaderTv.setVisibility(View.GONE);

                DocumentsApiResponseModel.ResultBean documentsApiResponseModel = adapterModelList.get(i).getDocumentModel();

                Utils.setImageWithGlide(activity.getApplicationContext(), documentViewHolder.documentIv, documentsApiResponseModel.getPath(), activity.getDrawable(R.drawable.profile_placeholder), true, true);

                GlideUrl glideUrl = Utils.getGlideUrlWithAuth(activity.getApplicationContext(), documentsApiResponseModel.getPath(), true);

                Glide.with(activity.getApplicationContext()).asFile().load(glideUrl).addListener(new RequestListener<File>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {

                        int kb = 1000;
                        int mb = kb * 1000;

                        String size;

                        if (resource.length() >= mb) {
                            size = String.format("%.2f %s", ((float) resource.length() / mb), " MB");
                        } else {
                            size = String.format("%.2f %s", ((float) resource.length() / kb), " KB");
                        }
                        documentViewHolder.sizeTv.setText(documentsApiResponseModel.getCreator().getUserName(activity) + " - " + size);

                        return false;
                    }
                }).submit();

                documentViewHolder.sizeTv.setText(documentsApiResponseModel.getName());

                documentViewHolder.sizeTv.setText(documentsApiResponseModel.getCreator().getUserName(activity));

                documentViewHolder.visitCb.setChecked(true);
                documentViewHolder.visitCb.setClickable(false);
                switch (mode) {
                    case Constants.VIEW_MODE:
                        documentViewHolder.visitCb.setVisibility(View.GONE);
                        break;
                    case Constants.EDIT_MODE:
                        documentViewHolder.visitCb.setVisibility(View.VISIBLE);
                        break;
                }
                documentViewHolder.documentRootCl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (mode) {
                            case Constants.EDIT_MODE:
                                int id = visitDetailAdapterModel.getDocumentModel().getUser_file_id();

                                if (detailViewModel.getFilesRemoveList().contains(id)) {
                                    detailViewModel.getFilesRemoveList().remove((Object) id);
                                    documentViewHolder.visitCb.setChecked(true);
                                } else {
                                    detailViewModel.getFilesRemoveList().add(id);
                                    documentViewHolder.visitCb.setChecked(false);
                                }
                                break;
                            case Constants.VIEW_MODE:
                                DocumentsApiResponseModel documents = new DocumentsApiResponseModel();
                                List<DocumentsApiResponseModel.ResultBean> resultBeanList = new ArrayList<>();
                                resultBeanList.add(documentsApiResponseModel);
                                documents.setResult(resultBeanList);

                                Bundle bundle = new Bundle();
                                bundle.putSerializable(ArgumentKeys.DOCUMENT_DETAIL, documents);
                                bundle.putInt(Constants.SELECTED_ITEM, documentsApiResponseModel.getUser_file_id());

                                ViewDocumentFragment viewDocumentFragment = new ViewDocumentFragment();
                                viewDocumentFragment.setArguments(bundle);
                                showSubFragmentInterface.onShowFragment(viewDocumentFragment);

                                break;
                        }
                    }
                });
                break;
            case VisitDetailConstants.TYPE_DIET:

                DietViewHolder dietViewHolder = (DietViewHolder) holder;

                List<DietApiResponseModel> dietApiResponseModelList = adapterModelList.get(i).getDietApiResponseModel();
                double calories = 0, carbs = 0, fat = 0, protien = 0;
                if (dietApiResponseModelList != null) {
                    Map<String, Double> dietMap = DietApiResponseModel.getDisplayDiets(dietApiResponseModelList);
                    calories = dietMap.get(FoodConstant.FOOD_ENERGY);
                    carbs = dietMap.get(FoodConstant.FOOD_CARBS);
                    fat = dietMap.get(FoodConstant.FOOD_FAT);
                    protien = dietMap.get(FoodConstant.FOOD_PROTEIN);

                }

                if (calories > 0) {
                    dietViewHolder.energyUnitTv.setVisibility(View.VISIBLE);
                } else {
                    dietViewHolder.energyUnitTv.setVisibility(View.GONE);
                }
                dietViewHolder.energyCountTv.setText(DietApiResponseModel.getCalorieValue(calories));
                dietViewHolder.energyUnitTv.setText(DietApiResponseModel.getCalorieUnit(activity));

                if (carbs == 0) {
                    dietViewHolder.carbsUnitTv.setVisibility(View.GONE);
                } else {
                    dietViewHolder.carbsUnitTv.setVisibility(View.VISIBLE);
                }
                dietViewHolder.carbsCountTv.setText(DietApiResponseModel.getDisplayValue(carbs));
                dietViewHolder.carbsUnitTv.setText(DietApiResponseModel.getDisplayUnit(activity, carbs));

                if (fat == 0) {
                    dietViewHolder.fatUnitTv.setVisibility(View.GONE);
                } else {
                    dietViewHolder.fatUnitTv.setVisibility(View.VISIBLE);
                }
                dietViewHolder.fatCountTv.setText(DietApiResponseModel.getDisplayValue(fat));
                dietViewHolder.fatUnitTv.setText(DietApiResponseModel.getDisplayUnit(activity, fat));

                if (protien == 0) {
                    dietViewHolder.proteinUnitTv.setVisibility(View.GONE);
                } else {
                    dietViewHolder.proteinUnitTv.setVisibility(View.VISIBLE);
                }
                dietViewHolder.proteinCountTv.setText(DietApiResponseModel.getDisplayValue(protien));
                dietViewHolder.proteinUnitTv.setText(DietApiResponseModel.getDisplayUnit(activity, protien));

                dietViewHolder.visitCb.setChecked(true);
                dietViewHolder.visitCb.setClickable(false);
                switch (mode) {
                    case Constants.VIEW_MODE:
                        dietViewHolder.visitCb.setVisibility(View.GONE);
                        break;
                    case Constants.EDIT_MODE:
                        dietViewHolder.visitCb.setVisibility(View.VISIBLE);
                        break;
                }
                dietViewHolder.dietRootLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (mode) {
                            case Constants.EDIT_MODE:
                                boolean checked = false;
                                for (int j = 0; j < dietApiResponseModelList.size(); j++) {

                                    int id = dietApiResponseModelList.get(j).getUser_diet_id();

                                    if (detailViewModel.getDietRemoveList().contains(id)) {
                                        detailViewModel.getDietRemoveList().remove((Object) id);
                                        checked = true;
                                    } else {
                                        detailViewModel.getDietRemoveList().add(id);
                                        checked = false;
                                    }
                                }
                                dietViewHolder.visitCb.setChecked(checked);
                                break;
                            case Constants.VIEW_MODE:
                                if (dietApiResponseModelList != null) {
                                    String date = Utils.getDayMonthYear(dietApiResponseModelList.get(0).getDate());
                                    DietDetailFragment dietDetailFragment = new DietDetailFragment();
                                    Bundle bundle = new Bundle();
                                    DietDetailModel dietDetailModel = new DietDetailModel(date, detailViewModel.getDietListModelMap().get(date));
                                    bundle.putSerializable(ArgumentKeys.DIET_ITEM, dietDetailModel);
                                    dietDetailFragment.setArguments(bundle);
                                    showSubFragmentInterface.onShowFragment(dietDetailFragment);
                                }
                                break;
                        }
                    }
                });
                break;
            case VisitDetailConstants.TYPE_INFO:
                InfoViewHolder infoViewHolder = (InfoViewHolder) holder;

                infoViewHolder.infoTv.setText(visitDetailAdapterModel.getCategoryTitle());
                if (!visitDetailAdapterModel.isShow()) {
                    infoViewHolder.infoBottomView.setVisibility(View.GONE);
                }
                break;
            case VisitDetailConstants.TYPE_PROCEDURE:
                ProcedureViewHolder procedureViewHolder = (ProcedureViewHolder) holder;

                procedureViewHolder.itemHolderLl.removeAllViews();

                for (int j = 0; j < adapterModelList.get(i).getCptCodes().size(); j++) {

                    View view = LayoutInflater.from(activity).inflate(R.layout.adapter_procedure_item_view, null);

                    TextView procedureLabelTv = (TextView) view.findViewById(R.id.procedure_label_tv);
                    TextView procedureValueTv = (TextView) view.findViewById(R.id.procedure_value_tv);

                    procedureLabelTv.setText(adapterModelList.get(i).getCptCodes().get(j).getCode());
                    procedureValueTv.setText(adapterModelList.get(i).getCptCodes().get(j).getDescription());

                    procedureViewHolder.itemHolderLl.addView(view);
                }

                if (mode == Constants.EDIT_MODE) {
                    procedureViewHolder.arrowIv.setVisibility(View.VISIBLE);
                    procedureViewHolder.procedureCl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showProcedureSelectionView();
                        }
                    });
                }

                break;
        }
    }

    private void showProcedureSelectionView() {
        SelectProceduresFragment selectProceduresFragment = new SelectProceduresFragment();
        Bundle bundle = new Bundle();
        ArrayList<String> selectedList = new ArrayList<>();

        for (int i = 0; i < detailViewModel.getSelectedCptCodeList().size(); i++) {
            selectedList.add(detailViewModel.getSelectedCptCodeList().get(i).getCode());
        }

        bundle.putStringArrayList(ArgumentKeys.SELECTED_ITEMS, selectedList);

        selectProceduresFragment.setArguments(bundle);
        selectProceduresFragment.setTargetFragment(targetFragment, RequestID.REQ_SELECT_CPT_CODE);
        showSubFragmentInterface.onShowFragment(selectProceduresFragment);
    }

    private void showIcdSelection() {
        SelectIcdCodeFragment selectIcdCodeFragment = new SelectIcdCodeFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ArgumentKeys.SHOW_TOOLBAR, true);
        bundle.putBoolean(ArgumentKeys.IS_DONE_ENABLE, true);

        selectIcdCodeFragment.setArguments(bundle);
        selectIcdCodeFragment.setTargetFragment(targetFragment, RequestID.REQ_SELECT_ICD_CODE);
        showSubFragmentInterface.onShowFragment(selectIcdCodeFragment);
    }

    @Override
    public int getItemCount() {
        return adapterModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return adapterModelList.get(position).getViewType();
    }

    public void setData() {
        adapterModelList = generateAdapterModelList();
        notifyDataSetChanged();
    }

    public void setMode(int mode) {
        this.mode = mode;
        setData();
    }

    public class ProcedureViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout procedureCl;
        private LinearLayout itemHolderLl;
        private ImageView arrowIv;
        private View bottomView;

        public ProcedureViewHolder(@NonNull View itemView) {
            super(itemView);
            procedureCl = (ConstraintLayout) itemView.findViewById(R.id.procedure_cl);
            itemHolderLl = (LinearLayout) itemView.findViewById(R.id.item_holder_ll);
            arrowIv = (ImageView) itemView.findViewById(R.id.arrow_iv);
            bottomView = (View) itemView.findViewById(R.id.bottom_view);
        }
    }

    public class InfoViewHolder extends RecyclerView.ViewHolder {
        private TextView infoTv;
        private View infoBottomView;

        public InfoViewHolder(@NonNull View itemView) {
            super(itemView);

            infoTv = (TextView) itemView.findViewById(R.id.info_tv);
            infoBottomView = (View) itemView.findViewById(R.id.info_bottom_view);

        }
    }

    public class DocumentViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout documentRootCl;
        private TextView recentHeaderTv;
        private TextView icdCodeTv;
        private TextView icdDetailTv;
        private LinearLayout checkboxLl;
        private CheckBox visitCb;
        private CardView documentCv;
        private ImageView documentIv;
        private TextView titleTv;
        private TextView sizeTv;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            documentRootCl = (ConstraintLayout) itemView.findViewById(R.id.document_root_cl);
            recentHeaderTv = (TextView) itemView.findViewById(R.id.recent_header_tv);
            icdCodeTv = (TextView) itemView.findViewById(R.id.icd_code_tv);
            icdDetailTv = (TextView) itemView.findViewById(R.id.icd_detail_tv);
            checkboxLl = (LinearLayout) itemView.findViewById(R.id.checkbox_ll);
            visitCb = (CheckBox) itemView.findViewById(R.id.visit_cb);
            documentCv = (CardView) itemView.findViewById(R.id.document_cv);
            documentIv = (ImageView) itemView.findViewById(R.id.document_iv);
            titleTv = (TextView) itemView.findViewById(R.id.title_tv);
            sizeTv = (TextView) itemView.findViewById(R.id.size_tv);
        }
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout orderRootCl;
        private TextView headerTv;
        private LinearLayout checkboxLl;
        private CheckBox visitCb;
        private CardView orderListItemCv;
        private ImageView orderListIv;
        private TextView orderListTitleTv;
        private TextView orderListSubTitleTv;
        private ConstraintLayout optionsCl;
        private TextView orderListOptionTitleTv;
        private TextView orderListOptionSubTitleTv;
        private ImageView orderStatusIv;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderRootCl = (ConstraintLayout) itemView.findViewById(R.id.order_root_cl);
            headerTv = (TextView) itemView.findViewById(R.id.header_tv);
            checkboxLl = (LinearLayout) itemView.findViewById(R.id.checkbox_ll);
            visitCb = (CheckBox) itemView.findViewById(R.id.visit_cb);
            orderListItemCv = (CardView) itemView.findViewById(R.id.order_list_item_cv);
            orderListIv = (ImageView) itemView.findViewById(R.id.order_list_iv);
            orderListTitleTv = (TextView) itemView.findViewById(R.id.order_list_title_tv);
            orderListSubTitleTv = (TextView) itemView.findViewById(R.id.order_list_sub_title_tv);
            optionsCl = (ConstraintLayout) itemView.findViewById(R.id.options_cl);
            orderListOptionTitleTv = (TextView) itemView.findViewById(R.id.order_list_option_title_tv);
            orderListOptionSubTitleTv = (TextView) itemView.findViewById(R.id.order_list_option_sub_title_tv);
            orderStatusIv = (ImageView) itemView.findViewById(R.id.order_status_iv);
        }
    }

    public class DietViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout dietRootLl;
        private TextView headerTv;
        private LinearLayout checkboxLl;
        private CheckBox visitCb;
        private CardView itemCv;
        private ConstraintLayout dietDetailCl;
        private TextView energyLabel;
        private TextView carbsLabel;
        private TextView fatLabel;
        private TextView proteinLabel;
        private TextView energyCountTv;
        private TextView carbsCountTv;
        private TextView fatCountTv;
        private TextView proteinCountTv;
        private TextView energyUnitTv;
        private TextView carbsUnitTv;
        private TextView fatUnitTv;
        private TextView proteinUnitTv;

        public DietViewHolder(@NonNull View itemView) {
            super(itemView);
            dietRootLl = (LinearLayout) itemView.findViewById(R.id.diet_root_ll);
            headerTv = (TextView) itemView.findViewById(R.id.header_tv);
            checkboxLl = (LinearLayout) itemView.findViewById(R.id.checkbox_ll);
            visitCb = (CheckBox) itemView.findViewById(R.id.visit_cb);
            itemCv = (CardView) itemView.findViewById(R.id.item_cv);
            dietDetailCl = (ConstraintLayout) itemView.findViewById(R.id.diet_detail_cl);
            energyLabel = (TextView) itemView.findViewById(R.id.energy_label);
            carbsLabel = (TextView) itemView.findViewById(R.id.carbs_label);
            fatLabel = (TextView) itemView.findViewById(R.id.fat_label);
            proteinLabel = (TextView) itemView.findViewById(R.id.protein_label);
            energyCountTv = (TextView) itemView.findViewById(R.id.energy_count_tv);
            carbsCountTv = (TextView) itemView.findViewById(R.id.carbs_count_tv);
            fatCountTv = (TextView) itemView.findViewById(R.id.fat_count_tv);
            proteinCountTv = (TextView) itemView.findViewById(R.id.protein_count_tv);
            energyUnitTv = (TextView) itemView.findViewById(R.id.energy_unit_tv);
            carbsUnitTv = (TextView) itemView.findViewById(R.id.carbs_unit_tv);
            fatUnitTv = (TextView) itemView.findViewById(R.id.fat_unit_tv);
            proteinUnitTv = (TextView) itemView.findViewById(R.id.protein_unit_tv);
        }
    }

    public class VitalsViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout vitalRootCl;
        private LinearLayout checkboxLl;
        private CheckBox visitCb;
        private CardView itemCv;
        private ImageView vitalIv;
        private TextView valueTv;
        private TextView unitTv;
        private TextView descriptionTv;
        private ConstraintLayout abnormalIndicatorCl;
        private TextView timeTv;
        private View bottomView;

        public VitalsViewHolder(@NonNull View itemView) {
            super(itemView);
            vitalRootCl = (ConstraintLayout) itemView.findViewById(R.id.vital_root_cl);
            checkboxLl = (LinearLayout) itemView.findViewById(R.id.checkbox_ll);
            visitCb = (CheckBox) itemView.findViewById(R.id.visit_cb);
            itemCv = (CardView) itemView.findViewById(R.id.item_cv);
            vitalIv = (ImageView) itemView.findViewById(R.id.vital_iv);
            valueTv = (TextView) itemView.findViewById(R.id.value_tv);
            unitTv = (TextView) itemView.findViewById(R.id.unit_tv);
            descriptionTv = (TextView) itemView.findViewById(R.id.description_tv);
            abnormalIndicatorCl = (ConstraintLayout) itemView.findViewById(R.id.abnormal_indicator_cl);
            timeTv = (TextView) itemView.findViewById(R.id.time_tv);
            bottomView = (View) itemView.findViewById(R.id.bottom_view);
        }
    }

    public class DateViewHolder extends RecyclerView.ViewHolder {
        private TextView dateTv;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTv = (TextView) itemView.findViewById(R.id.header_tv);
        }
    }

    public class AddViewHolder extends RecyclerView.ViewHolder {
        private CardView btAddCardView;
        private TextView addItemTv;

        public AddViewHolder(@NonNull View itemView) {
            super(itemView);
            btAddCardView = (CardView) itemView.findViewById(R.id.bt_add_card_view);
            addItemTv = (TextView) itemView.findViewById(R.id.add_item_tv);
        }
    }

    public class DiagnosisViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout icdListLl;
        private ImageView arrowIv;

        public DiagnosisViewHolder(@NonNull View itemView) {
            super(itemView);
            icdListLl = (LinearLayout) itemView.findViewById(R.id.icd_list_ll);
            arrowIv = (ImageView) itemView.findViewById(R.id.arrow_iv);
        }
    }

    public class InstructionViewHolder extends RecyclerView.ViewHolder {
        private EditText instructionEt;

        public InstructionViewHolder(@NonNull View itemView) {
            super(itemView);
            instructionEt = (EditText) itemView.findViewById(R.id.input_et);
        }
    }

    public class TranscriptViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout transcriptRootCl;
        private LinearLayout checkboxLl;
        private CheckBox visitCb;
        private TextView callerNameTv;
        private CustomEditText transcriptInfoEt;

        public TranscriptViewHolder(@NonNull View itemView) {
            super(itemView);

            transcriptRootCl = (ConstraintLayout) itemView.findViewById(R.id.transcript_root_cl);
            checkboxLl = (LinearLayout) itemView.findViewById(R.id.checkbox_ll);
            visitCb = (CheckBox) itemView.findViewById(R.id.visit_cb);
            callerNameTv = (TextView) itemView.findViewById(R.id.caller_name_tv);
            transcriptInfoEt = (CustomEditText) itemView.findViewById(R.id.transcript_info_et);
        }
    }

    public class HistoryItemViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout viewModeCl;
        private TextView historyQuestionTv;
        private TextView historyAnswerTv;
        private TextView historyReasonTv;
        private View bottomView;
        private ConstraintLayout editModeCl;
        private CheckBox itemSwitch;
        private TextView yesOrNoTv;
        private CustomEditText commentsEt;

        public HistoryItemViewHolder(@NonNull View itemView) {
            super(itemView);
            viewModeCl = (ConstraintLayout) itemView.findViewById(R.id.view_mode_cl);

            historyQuestionTv = (TextView) itemView.findViewById(R.id.history_question_tv);
            historyAnswerTv = (TextView) itemView.findViewById(R.id.history_answer_tv);
            historyReasonTv = (TextView) itemView.findViewById(R.id.history_reason_tv);
            bottomView = (View) itemView.findViewById(R.id.bottom_view);

            editModeCl = (ConstraintLayout) itemView.findViewById(R.id.edit_mode_cl);

            itemSwitch = (CheckBox) itemView.findViewById(R.id.item_switch);
            yesOrNoTv = (TextView) itemView.findViewById(R.id.yesOrNo_tv);
            commentsEt = (CustomEditText) itemView.findViewById(R.id.comments_et);

            itemSwitch.setClickable(false);

        }
    }

    public class LabelValueViewHolder extends RecyclerView.ViewHolder {
        private LabelValueCustomView labelValueCv;

        public LabelValueViewHolder(@NonNull View itemView) {
            super(itemView);
            labelValueCv = (LabelValueCustomView) itemView.findViewById(R.id.label_value_cv);
        }
    }

    public class HealthSummaryViewHolder extends RecyclerView.ViewHolder {
        private CardView medicalHistoryBtn;

        public HealthSummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            medicalHistoryBtn = (CardView) itemView.findViewById(R.id.medical_history_btn);
        }
    }

    public class UserInfoViewHolder extends RecyclerView.ViewHolder {
        private CustomUserListItemView userListIv;

        public UserInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            userListIv = (CustomUserListItemView) itemView.findViewById(R.id.user_list_iv);
        }
    }

    public class CallSummaryViewHolder extends RecyclerView.ViewHolder {
        private TextView dateTv;
        private CardView callTypeCv;
        private TextView callTypeTv;
        private TextView callTimeTv;
        private ImageView transcriptVideoIv;

        public CallSummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTv = (TextView) itemView.findViewById(R.id.date_tv);
            callTypeCv = (CardView) itemView.findViewById(R.id.call_type_cv);
            callTypeTv = (TextView) itemView.findViewById(R.id.call_type_tv);
            callTimeTv = (TextView) itemView.findViewById(R.id.call_time_tv);
            transcriptVideoIv = (ImageView) itemView.findViewById(R.id.transcript_video_iv);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryTv;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTv = (TextView) itemView.findViewById(R.id.category_tv);
        }
    }

    private List<VisitDetailAdapterModel> generateAdapterModelList() {
        List<VisitDetailAdapterModel> modelList = new ArrayList<>();

        /**
         * call summary
         */
        if (detailViewModel.getTranscriptionApiResponseModel() != null) {
            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_HEADER, activity.getString(R.string.call_summary)));
            String screenShot = null;
            if (detailViewModel.getVisitsDetailApiResponseModel() != null && detailViewModel.getVisitsDetailApiResponseModel().getResult() != null) {
                screenShot = detailViewModel.getVisitsDetailApiResponseModel().getResult().getAudio_stream_screenshot();
            }

            CallSummaryModel callSummaryModel = new CallSummaryModel(detailViewModel.getTranscriptionApiResponseModel().getOrder_start_time(),
                    detailViewModel.getTranscriptionApiResponseModel().getOrder_end_time(),
                    detailViewModel.getTranscriptionApiResponseModel().getType(),
                    detailViewModel.getTranscriptionApiResponseModel().getCategory(),
                    detailViewModel.getTranscriptionApiResponseModel().getAudio_stream(),
                    detailViewModel.getRecentResponseModel().getDurationInSecs(),
                    screenShot);
            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_CALL_SUMMARY, callSummaryModel));
        }

        /**
         * Doctor patient info
         */

        String categoryTitle = activity.getString(R.string.user_info);
        if (UserType.isUserDoctor()) {
            categoryTitle = activity.getString(R.string.patient_info);
        } else if (UserType.isUserPatient()) {
            categoryTitle = activity.getString(R.string.doctor_info);
        }
        modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_HEADER, categoryTitle));

        if (UserType.isUserAssistant()) {
            if (detailViewModel.getPatientDetailModel() != null) {
                modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_USER_INFO, detailViewModel.getPatientDetailModel()));
            }
            if (detailViewModel.getDoctorDetailModel() != null) {
                modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_USER_INFO, detailViewModel.getDoctorDetailModel()));
            }
        } else if (UserType.isUserDoctor()) {
            if (detailViewModel.getPatientDetailModel() != null) {
                modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_USER_INFO, detailViewModel.getPatientDetailModel()));
            }
        } else if (UserType.isUserPatient()) {
            if (detailViewModel.getDoctorDetailModel() != null) {
                modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_USER_INFO, detailViewModel.getDoctorDetailModel()));
            }
        }

        /**
         * Health summary view
         *
         * Show only for doctor
         */

        if (UserType.isUserDoctor()) {
            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_HEALTH_SUMMARY));
        }

        /**
         * History detail
         */
        modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_HEADER, activity.getString(R.string.details)));

        if (detailViewModel.getSchedulesApiResponseModel() != null) {
            SchedulesApiResponseModel.ResultBean scheduleModel = detailViewModel.getSchedulesApiResponseModel();


            LabelValueModel labelValueModel;

            labelValueModel = new LabelValueModel(activity.getString(R.string.patient_history_has_been_updated), (scheduleModel.getDetail().isChange_medical_info() ? activity.getString(R.string.yes) : activity.getString(R.string.no)), false);
            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_LABEL_VALUE, labelValueModel));


            labelValueModel = new LabelValueModel(activity.getString(R.string.patient_demographic_changed), (scheduleModel.getDetail().isChange_demographic() ? activity.getString(R.string.yes) : activity.getString(R.string.no)), false);
            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_LABEL_VALUE, labelValueModel));


            labelValueModel = new LabelValueModel(activity.getString(R.string.patient_insurance_changed), (scheduleModel.getDetail().isInsurance_to_date() ? activity.getString(R.string.yes) : activity.getString(R.string.no)), false);
            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_LABEL_VALUE, labelValueModel));

            labelValueModel = new LabelValueModel(activity.getString(R.string.reason_for_visit), "", false);
            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_LABEL_VALUE, labelValueModel));
            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_INFO, scheduleModel.getDetail().getReason(), true));

        }
        List<VisitDetailViewModel.UpdatedHistoryBean> historyBeanList = detailViewModel.getHistoryList();

        for (int i = 0; i < historyBeanList.size(); i++) {
            if (mode == Constants.EDIT_MODE || historyBeanList.get(i).isIsYes()) {
                modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_HISTORY_ITEM, historyBeanList.get(i)));
            }
        }

        /**
         * Transcription detail
         */

        if (detailViewModel.getDownloadTranscriptResponseModel() != null && !detailViewModel.getDownloadTranscriptResponseModel().getSpeakerLabels().isEmpty()) {
            DownloadTranscriptResponseModel downloadTranscriptResponseModel = detailViewModel.getDownloadTranscriptResponseModel();

            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_HEADER, activity.getString(R.string.call_transcript)));

            DownloadTranscriptResponseModel updateModel = new DownloadTranscriptResponseModel();
            updateModel.setTranscripts(downloadTranscriptResponseModel.getTranscripts());

            List<DownloadTranscriptResponseModel.SpeakerLabelsBean> speakerLabelsBeans = new ArrayList<>();

            if (downloadTranscriptResponseModel.getSpeakerLabels() != null &&
                    !downloadTranscriptResponseModel.getSpeakerLabels().isEmpty()) {

                for (int i = 0; i < downloadTranscriptResponseModel.getSpeakerLabels().size(); i++) {
                    if (downloadTranscriptResponseModel.getSpeakerLabels().get(i).getTranscript() != null &&
                            !downloadTranscriptResponseModel.getSpeakerLabels().get(i).getTranscript().isEmpty()) {
                        DownloadTranscriptResponseModel.SpeakerLabelsBean speakerLabelsBean = new DownloadTranscriptResponseModel.SpeakerLabelsBean(downloadTranscriptResponseModel.getSpeakerLabels().get(i).getStart_time(),
                                downloadTranscriptResponseModel.getSpeakerLabels().get(i).getSpeaker_label(),
                                downloadTranscriptResponseModel.getSpeakerLabels().get(i).getEnd_time(),
                                downloadTranscriptResponseModel.getSpeakerLabels().get(i).getTranscript(),
                                downloadTranscriptResponseModel.getSpeakerLabels().get(i).isRemoved());
                        speakerLabelsBeans.add(speakerLabelsBean);

                        modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_TRANSCRIPT, downloadTranscriptResponseModel.getSpeakerLabels().get(i)));
                    }
                }
            }
            updateModel.setSpeakerLabels(speakerLabelsBeans);
            detailViewModel.setUpdatedTranscriptResponseModel(updateModel);
        }

        /**
         * Patient instructions
         */


        String category = activity.getString(R.string.patient_instructions);
        String data = null;
        boolean isAdd = false;

        if (detailViewModel.getVisitsDetailApiResponseModel() != null
                && detailViewModel.getVisitsDetailApiResponseModel().getResult() != null
                && detailViewModel.getVisitsDetailApiResponseModel().getResult().getInstructions() != null
                && !detailViewModel.getVisitsDetailApiResponseModel().getResult().getInstructions().isEmpty()) {
            data = detailViewModel.getVisitsDetailApiResponseModel().getResult().getInstructions();
        }

        switch (mode) {
            case Constants.EDIT_MODE:
                isAdd = true;
                break;
            case Constants.VIEW_MODE:
                if (data != null)
                    isAdd = true;
                break;
        }

        if (isAdd) {
            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_HEADER, category));
            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_INSTRUCTION, data, category));
        }

        /**
         * Patient Diagnosis
         */
        category = activity.getString(R.string.patient_diagnosis);

        if (detailViewModel.getDiagnosis() != null && detailViewModel.getDiagnosis().getICD10_codes() != null &&
                !detailViewModel.getDiagnosis().getICD10_codes().isEmpty()) {

            VisitDiagnosisModel visitDiagnosisModel = detailViewModel.getDiagnosis();

            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_HEADER, category));
            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_DIAGNOSIS, visitDiagnosisModel));
        } else {
            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_ADD, new AddNewModel(activity.getString(R.string.add_diagnosis), VisitDetailConstants.ADD_DIAGNOSIS)));
        }


        /**
         * Vitals Detail
         */

        boolean isVitalAvailable = detailViewModel.getVitalsApiResponseModels() != null && !detailViewModel.getVitalsApiResponseModels().isEmpty();


        if (isVitalAvailable) {
            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_HEADER, activity.getString(R.string.vitals), isVitalAvailable));

            Collections.sort(detailViewModel.getVitalsApiResponseModels(), new Comparator<VitalsApiResponseModel>() {
                @Override
                public int compare(VitalsApiResponseModel o1, VitalsApiResponseModel o2) {
                    return Utils.getDateFromString(o2.getCreated_at()).compareTo(Utils.getDateFromString(o1.getCreated_at()));
                }
            });

            for (int i = 0; i < detailViewModel.getVitalsApiResponseModels().size(); i++) {
                if (i == 0) {
                    modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_DATE, Utils.getDayMonthYear(detailViewModel.getVitalsApiResponseModels().get(i).getCreated_at())));
                } else {
                    if (!Utils.getDayMonthYear(detailViewModel.getVitalsApiResponseModels().get(i - 1).getCreated_at()).equals(Utils.getDayMonthYear(detailViewModel.getVitalsApiResponseModels().get(i).getCreated_at()))) {
                        modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_DATE, Utils.getDayMonthYear(detailViewModel.getVitalsApiResponseModels().get(i).getCreated_at())));
                    }
                }
                modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_VITALS, detailViewModel.getVitalsApiResponseModels().get(i)));
            }


        }
        modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_ADD, new AddNewModel(activity.getString(R.string.add_vitals), VisitDetailConstants.ADD_VITAL)));

        /**
         * Orders listing
         */

        boolean isOrdersAvailable = (detailViewModel.getOrdersIdListApiResponseModel() != null && !detailViewModel.getOrdersIdListApiResponseModel().isEmpty())
                || (detailViewModel.getFormsApiResponseModels() != null && !detailViewModel.getFormsApiResponseModels().isEmpty());


        if (isOrdersAvailable) {
            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_HEADER, activity.getString(R.string.orders), isOrdersAvailable));

            List<VisitOrdersAdapterModel> visitOrdersAdapterModels = new ArrayList<>();

            if (detailViewModel.getOrdersIdListApiResponseModel() != null) {

                if (detailViewModel.getOrdersIdListApiResponseModel().getPrescriptions() != null &&
                        !detailViewModel.getOrdersIdListApiResponseModel().getPrescriptions().isEmpty()) {
                    for (int i = 0; i < detailViewModel.getOrdersIdListApiResponseModel().getPrescriptions().size(); i++) {
                        visitOrdersAdapterModels.add(new VisitOrdersAdapterModel(detailViewModel.getOrdersIdListApiResponseModel().getPrescriptions().get(i).getCreated_at(),
                                detailViewModel.getOrdersIdListApiResponseModel().getPrescriptions().get(i)));
                    }
                }
                if (detailViewModel.getOrdersIdListApiResponseModel().getSpecialists() != null &&
                        !detailViewModel.getOrdersIdListApiResponseModel().getSpecialists().isEmpty()) {
                    for (int i = 0; i < detailViewModel.getOrdersIdListApiResponseModel().getSpecialists().size(); i++) {
                        visitOrdersAdapterModels.add(new VisitOrdersAdapterModel(detailViewModel.getOrdersIdListApiResponseModel().getSpecialists().get(i).getCreated_at(),
                                detailViewModel.getOrdersIdListApiResponseModel().getSpecialists().get(i)));
                    }
                }
                if (detailViewModel.getOrdersIdListApiResponseModel().getLabs() != null &&
                        !detailViewModel.getOrdersIdListApiResponseModel().getLabs().isEmpty()) {
                    for (int i = 0; i < detailViewModel.getOrdersIdListApiResponseModel().getLabs().size(); i++) {
                        visitOrdersAdapterModels.add(new VisitOrdersAdapterModel(detailViewModel.getOrdersIdListApiResponseModel().getLabs().get(i).getCreated_at(),
                                detailViewModel.getOrdersIdListApiResponseModel().getLabs().get(i)));
                    }
                }
                if (detailViewModel.getOrdersIdListApiResponseModel().getXrays() != null &&
                        !detailViewModel.getOrdersIdListApiResponseModel().getXrays().isEmpty()) {
                    for (int i = 0; i < detailViewModel.getOrdersIdListApiResponseModel().getXrays().size(); i++) {
                        visitOrdersAdapterModels.add(new VisitOrdersAdapterModel(detailViewModel.getOrdersIdListApiResponseModel().getXrays().get(i).getCreated_at(),
                                detailViewModel.getOrdersIdListApiResponseModel().getXrays().get(i)));
                    }
                }
                if (detailViewModel.getOrdersIdListApiResponseModel().getMiscellaneous() != null &&
                        !detailViewModel.getOrdersIdListApiResponseModel().getMiscellaneous().isEmpty()) {
                    for (int i = 0; i < detailViewModel.getOrdersIdListApiResponseModel().getMiscellaneous().size(); i++) {
                        visitOrdersAdapterModels.add(new VisitOrdersAdapterModel(detailViewModel.getOrdersIdListApiResponseModel().getMiscellaneous().get(i).getCreated_at(),
                                detailViewModel.getOrdersIdListApiResponseModel().getMiscellaneous().get(i)));
                    }
                }

            }

            if (detailViewModel.getFormsApiResponseModels() != null &&
                    !detailViewModel.getFormsApiResponseModels().isEmpty()) {
                for (int i = 0; i < detailViewModel.getFormsApiResponseModels().size(); i++) {
                    visitOrdersAdapterModels.add(new VisitOrdersAdapterModel(detailViewModel.getFormsApiResponseModels().get(i).getCreated_at(),
                            detailViewModel.getFormsApiResponseModels().get(i)));
                }
            }


            if (!visitOrdersAdapterModels.isEmpty()) {
                Collections.sort(visitOrdersAdapterModels, new Comparator<VisitOrdersAdapterModel>() {
                    @Override
                    public int compare(VisitOrdersAdapterModel o1, VisitOrdersAdapterModel o2) {
                        return Utils.getDateFromString(o2.getDate()).compareTo(Utils.getDateFromString(o1.getDate()));
                    }
                });

                for (int i = 0; i < visitOrdersAdapterModels.size(); i++) {
                    if (i == 0) {
                        modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_DATE, Utils.getDayMonthYear(visitOrdersAdapterModels.get(i).getDate())));
                    } else {
                        if (!Utils.getDayMonthYear(visitOrdersAdapterModels.get(i).getDate()).equals(Utils.getDayMonthYear(visitOrdersAdapterModels.get(i - 1).getDate()))) {
                            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_DATE, Utils.getDayMonthYear(visitOrdersAdapterModels.get(i).getDate())));
                        }
                    }
                    modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_ORDER, visitOrdersAdapterModels.get(i)));
                }


            }

        }
        modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_ADD, new AddNewModel(activity.getString(R.string.add_order), VisitDetailConstants.ADD_ORDER)));

        /**
         * Documents listing
         */
        boolean isDocumentsAvailable = detailViewModel.getDocumentsApiResponseModels() != null && !detailViewModel.getDocumentsApiResponseModels().isEmpty();


        if (isDocumentsAvailable) {
            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_HEADER, activity.getString(R.string.documents), isDocumentsAvailable));

            Collections.sort(detailViewModel.getDocumentsApiResponseModels(), new Comparator<DocumentsApiResponseModel.ResultBean>() {
                @Override
                public int compare(DocumentsApiResponseModel.ResultBean o1, DocumentsApiResponseModel.ResultBean o2) {
                    return Utils.getDateFromString(o2.getCreated_at()).compareTo(Utils.getDateFromString(o1.getCreated_at()));
                }
            });

            for (int i = 0; i < detailViewModel.getDocumentsApiResponseModels().size(); i++) {
                if (i == 0) {
                    modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_DATE, Utils.getDayMonthYear(detailViewModel.getDocumentsApiResponseModels().get(i).getCreated_at())));
                } else {
                    if (!Utils.getDayMonthYear(detailViewModel.getDocumentsApiResponseModels().get(i).getCreated_at()).equals(Utils.getDayMonthYear(detailViewModel.getDocumentsApiResponseModels().get(i - 1).getCreated_at()))) {
                        modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_DATE, Utils.getDayMonthYear(detailViewModel.getDocumentsApiResponseModels().get(i).getCreated_at())));
                    }
                }

                modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_DOCUMENT, detailViewModel.getDocumentsApiResponseModels().get(i)));

            }

        }
        modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_ADD, new AddNewModel(activity.getString(R.string.add_document), VisitDetailConstants.ADD_DOCUMENTS)));

        /**
         * Diet listing
         */

        boolean isDietAvailable = detailViewModel.getDietApiResponseModels() != null && !detailViewModel.getDietApiResponseModels().isEmpty();


        if (isDietAvailable) {
            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_HEADER, activity.getString(R.string.diets), isDietAvailable));

            Map<String, List<DietApiResponseModel>> modelMap = new HashMap<>();

            for (int i = 0; i < detailViewModel.getDietApiResponseModels().size(); i++) {
                DietApiResponseModel dietApiResponseModel = detailViewModel.getDietApiResponseModels().get(i);
                String date = Utils.getDayMonthYear(dietApiResponseModel.getCreated_at());

                List<DietApiResponseModel> responseModelList = new ArrayList<>();

                if (modelMap.containsKey(date) && modelMap.get(date) != null) {
                    responseModelList.addAll(modelMap.get(date));
                }

                responseModelList.add(dietApiResponseModel);

                modelMap.put(date, responseModelList);
            }

            detailViewModel.setDietListModelMap(modelMap);

            Set<String> keySet = modelMap.keySet();
            for (String date : keySet) {
                modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_DATE, date));
                modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_DIET, modelMap.get(date)));

            }

        } else {
            detailViewModel.getDietListModelMap().clear();
        }
        modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_ADD, new AddNewModel(activity.getString(R.string.add_diet), VisitDetailConstants.ADD_DIET)));

        /**
         * Procedure
         */

        if (!detailViewModel.getSelectedCptCodeList().isEmpty()) {

            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_HEADER, activity.getString(R.string.procedure)));

            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_PROCEDURE, new ProcedureModel(detailViewModel.getSelectedCptCodeList())));
        } else {
            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_ADD, new AddNewModel(activity.getString(R.string.add_procedure), VisitDetailConstants.ADD_PROCEDURE)));
        }

        return modelList;
    }

}