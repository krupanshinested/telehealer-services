package com.thealer.telehealer.views.home.recents;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.HistoryBean;
import com.thealer.telehealer.apilayer.models.diet.DietApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.documents.DocumentsApiResponseModel;
import com.thealer.telehealer.apilayer.models.recents.DownloadTranscriptResponseModel;
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
import com.thealer.telehealer.views.common.LabelValueCustomView;
import com.thealer.telehealer.views.common.OnModeChangeListener;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.DoctorPatientDetailViewFragment;
import com.thealer.telehealer.views.home.monitoring.diet.DietDetailFragment;
import com.thealer.telehealer.views.home.monitoring.diet.DietDetailModel;
import com.thealer.telehealer.views.home.monitoring.diet.DietListingFragment;
import com.thealer.telehealer.views.home.monitoring.diet.FoodConstant;
import com.thealer.telehealer.views.home.orders.OrderConstant;
import com.thealer.telehealer.views.home.orders.OrderSelectionListFragment;
import com.thealer.telehealer.views.home.orders.document.DocumentListFragment;
import com.thealer.telehealer.views.home.orders.document.ViewDocumentFragment;
import com.thealer.telehealer.views.home.recents.adapterModels.AddNewModel;
import com.thealer.telehealer.views.home.recents.adapterModels.CallSummaryModel;
import com.thealer.telehealer.views.home.recents.adapterModels.LabelValueModel;
import com.thealer.telehealer.views.home.recents.adapterModels.VisitOrdersAdapterModel;
import com.thealer.telehealer.views.home.vitals.StethoscopeDetailViewFragment;
import com.thealer.telehealer.views.home.vitals.vitalReport.VitalUserReportListFragment;

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
class VisitDetailListAdapter extends RecyclerView.Adapter<VisitDetailListAdapter.ViewHolder> {
    private FragmentActivity activity;
    private VisitDetailViewModel detailViewModel;
    private List<VisitDetailAdapterModel> adapterModelList = new ArrayList<>();
    private ShowSubFragmentInterface showSubFragmentInterface;
    private int mode;
    private OnModeChangeListener onModeChangeListener;
    private Fragment targetFragment;

    public VisitDetailListAdapter(FragmentActivity activity, Fragment targetFragment,
                                  VisitDetailViewModel visitDetailViewModel, int mode, OnModeChangeListener onModeChangeListener) {
        this.activity = activity;
        showSubFragmentInterface = (ShowSubFragmentInterface) activity;
        this.detailViewModel = visitDetailViewModel;
        this.mode = mode;
        this.onModeChangeListener = onModeChangeListener;
        this.targetFragment = targetFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = null;
        switch (viewType) {
            case VisitDetailConstants.TYPE_HEADER:
                view = inflater.inflate(R.layout.visit_list_adapter_category_view, viewGroup, false);
                break;
            case VisitDetailConstants.TYPE_CALL_SUMMARY:
                view = inflater.inflate(R.layout.visit_list_adapter_call_summary_view, viewGroup, false);
                break;
            case VisitDetailConstants.TYPE_USER_INFO:
                view = inflater.inflate(R.layout.adapter_doctor_patient_list, viewGroup, false);
                break;
            case VisitDetailConstants.TYPE_LABEL_VALUE:
                view = inflater.inflate(R.layout.visit_list_adapter_label_value_view, viewGroup, false);
                break;
            case VisitDetailConstants.TYPE_HISTORY_ITEM:
                view = inflater.inflate(R.layout.visit_list_adapter_patient_history_view, viewGroup, false);
                break;
            case VisitDetailConstants.TYPE_TRANSCRIPT:
                view = inflater.inflate(R.layout.visit_list_adapter_transcript_view, viewGroup, false);
                break;
            case VisitDetailConstants.TYPE_INPUT:
                view = inflater.inflate(R.layout.visit_list_adapter_input_view, viewGroup, false);
                break;
            case VisitDetailConstants.TYPE_ADD:
                view = inflater.inflate(R.layout.adapter_item_add_view, viewGroup, false);
                break;
            case VisitDetailConstants.TYPE_DATE:
                view = inflater.inflate(R.layout.adapter_list_header_view, viewGroup, false);
                break;
            case VisitDetailConstants.TYPE_VITALS:
                view = inflater.inflate(R.layout.adapter_vitals_list_view, viewGroup, false);
                break;
            case VisitDetailConstants.TYPE_DIET:
                view = inflater.inflate(R.layout.adapter_diet_selection_list, viewGroup, false);
                break;
            case VisitDetailConstants.TYPE_ORDER:
                view = inflater.inflate(R.layout.adapter_editable_order_list_view, viewGroup, false);
                break;
            case VisitDetailConstants.TYPE_DOCUMENT:
                view = inflater.inflate(R.layout.adapter_document_list, viewGroup, false);
                break;
        }
        return new ViewHolder(view);
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.transcriptInfoEt != null)
            holder.transcriptInfoEt.clearAllTextChangedListener();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        VisitDetailAdapterModel visitDetailAdapterModel = adapterModelList.get(i);
        switch (visitDetailAdapterModel.getViewType()) {
            case VisitDetailConstants.TYPE_HEADER:
                viewHolder.categoryTv.setText(visitDetailAdapterModel.getCategoryTitle());
                switch (mode) {
                    case Constants.VIEW_MODE:
                        if (visitDetailAdapterModel.isShow()) {
                            viewHolder.categoryTv.setVisibility(View.VISIBLE);
                        } else {
                            viewHolder.categoryTv.setVisibility(View.GONE);
                        }
                        break;
                    case Constants.EDIT_MODE:
                        viewHolder.categoryTv.setVisibility(View.VISIBLE);
                        break;
                }
                break;
            case VisitDetailConstants.TYPE_CALL_SUMMARY:
                CallSummaryModel callSummaryModel = visitDetailAdapterModel.getCallSummaryModel();
                viewHolder.callDateTv.setText(Utils.getDayMonthYear(callSummaryModel.getCallStartTime()));
                if (callSummaryModel.getCallCategory() != null) {
                    viewHolder.callTypeTv.setText(callSummaryModel.getCallCategory());
                } else {
                    viewHolder.callTypeTv.setVisibility(View.GONE);
                }
                String timeDisplayFormat = "%s to %s ( %s )";
                viewHolder.timeTv.setText(String.format(timeDisplayFormat, Utils.getFormatedTime(callSummaryModel.getCallStartTime()),
                        Utils.getFormatedTime(callSummaryModel.getCallEndTime()), Utils.getDisplayDuration(callSummaryModel.getDurationInSec())));
                viewHolder.transcriptVideoIv.setOnClickListener(new View.OnClickListener() {
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
                    Utils.setImageWithGlide(activity, viewHolder.transcriptVideoIv, visitDetailAdapterModel.getCallSummaryModel().getScreenShot(), null, true);
                }
                break;
            case VisitDetailConstants.TYPE_USER_INFO:
                viewHolder.userListIv.setListTitleTv(visitDetailAdapterModel.getUserInfoModel().getDisplayName());
                viewHolder.userListIv.setListSubTitleTv(visitDetailAdapterModel.getUserInfoModel().getDisplayInfo());
                Utils.setImageWithGlide(activity.getApplicationContext(), viewHolder.userListIv.getAvatarCiv(),
                        visitDetailAdapterModel.getUserInfoModel().getUser_avatar(), activity.getDrawable(R.drawable.profile_placeholder), true);

                if (visitDetailAdapterModel.getUserInfoModel().getRole().equals(Constants.ROLE_PATIENT)) {
                    Utils.setGenderImage(activity, viewHolder.userListIv.getActionIv(), visitDetailAdapterModel.getUserInfoModel().getGender());
                    viewHolder.userListIv.getActionIv().setVisibility(View.VISIBLE);
                }

                viewHolder.userListIv.showStatus(false);
                viewHolder.userListIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DoctorPatientDetailViewFragment doctorPatientDetailViewFragment = new DoctorPatientDetailViewFragment();
                        Bundle detailBundle = new Bundle();

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
            case VisitDetailConstants.TYPE_LABEL_VALUE:
                LabelValueModel labelValueModel = adapterModelList.get(i).getLabelValueModel();
                viewHolder.labelValueCv.setLabelText(labelValueModel.getLabel());
                viewHolder.labelValueCv.setValueText(labelValueModel.getValue());
                viewHolder.labelValueCv.setBottomViewVisible(labelValueModel.isShowBottomView());
                break;
            case VisitDetailConstants.TYPE_HISTORY_ITEM:
                HistoryBean historyBean = adapterModelList.get(i).getHistoryBean();
                if (historyBean.isIsYes()) {
                    viewHolder.historyQuestionTv.setText(historyBean.getQuestion());
                    viewHolder.historyAnswerTv.setText(historyBean.isIsYes() ? activity.getString(R.string.yes) : activity.getString(R.string.no));
                    if (historyBean.getReason() != null && !historyBean.getReason().isEmpty()) {
                        viewHolder.historyReasonTv.setVisibility(View.VISIBLE);
                        viewHolder.historyReasonTv.setText(String.format("( %s )", historyBean.getReason()));
                    }
                }
                break;
            case VisitDetailConstants.TYPE_TRANSCRIPT:
                DownloadTranscriptResponseModel.SpeakerLabelsBean speakerLabelsBean = adapterModelList.get(i).getTranscriptModel();
                viewHolder.callerNameTv.setText(speakerLabelsBean.getSpeakerName(detailViewModel.getTranscriptionApiResponseModel()));
                viewHolder.visitCb.setChecked(!speakerLabelsBean.isRemoved());

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

                viewHolder.transcriptInfoEt.addTextChangedListener(transcriptTextWatcher);

                Log.e("aswin", "onBindViewHolder: " + speakerLabelsBean.getTranscript());
                viewHolder.transcriptInfoEt.setText(speakerLabelsBean.getTranscript());

                switch (mode) {
                    case Constants.VIEW_MODE:
                        viewHolder.transcriptRootCl.setOnClickListener(null);
                        viewHolder.visitCb.setVisibility(View.GONE);
                        viewHolder.transcriptInfoEt.setEnabled(false);
                        break;
                    case Constants.EDIT_MODE:
                        viewHolder.visitCb.setVisibility(View.VISIBLE);
                        viewHolder.transcriptInfoEt.setEnabled(true);
                        viewHolder.transcriptRootCl.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                viewHolder.visitCb.setChecked(!viewHolder.visitCb.isChecked());
                                for (int j = 0; j < detailViewModel.getUpdatedTranscriptResponseModel().getSpeakerLabels().size(); j++) {
                                    DownloadTranscriptResponseModel.SpeakerLabelsBean labelsBean = detailViewModel.getUpdatedTranscriptResponseModel().getSpeakerLabels().get(j);
                                    if (speakerLabelsBean.isModelEqual(labelsBean)) {
                                        labelsBean.setRemoved(!labelsBean.isRemoved());
                                        break;
                                    }
                                }
                                detailViewModel.setTranscriptUpdated(detailViewModel.getUpdatedTranscriptResponseModel().isUpdated());
                            }
                        });
                        break;
                }
                break;
            case VisitDetailConstants.TYPE_INPUT:
                TextWatcher textWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (visitDetailAdapterModel.getCategoryTitle() == null) {
                            if (s.toString().isEmpty())
                                detailViewModel.setInstructionUpdated(false);
                            else
                                detailViewModel.setInstructionUpdated(true);
                        } else {
                            if (s.toString().isEmpty())
                                detailViewModel.setInstructionUpdated(true);
                            else {
                                if (s.toString().trim().equals(visitDetailAdapterModel.getCategoryTitle()))
                                    detailViewModel.setInstructionUpdated(false);
                                else
                                    detailViewModel.setInstructionUpdated(true);
                            }
                        }
                        detailViewModel.setInstruction(s.toString().trim());
                    }
                };

                switch (mode) {
                    case Constants.VIEW_MODE:
                        viewHolder.inputEt.setEnabled(false);
                        viewHolder.inputEt.setClickable(false);
                        viewHolder.inputEt.removeTextChangedListener(textWatcher);
                        break;
                    case Constants.EDIT_MODE:
                        viewHolder.inputEt.setEnabled(true);
                        viewHolder.inputEt.setClickable(true);
                        viewHolder.inputEt.addTextChangedListener(textWatcher);
                        break;
                }

                if (visitDetailAdapterModel.getCategoryTitle() != null)
                    viewHolder.inputEt.setText(visitDetailAdapterModel.getCategoryTitle());


                break;
            case VisitDetailConstants.TYPE_ADD:
                switch (mode) {
                    case Constants.VIEW_MODE:
                        viewHolder.addItemTv.setVisibility(View.GONE);
                        break;
                    case Constants.EDIT_MODE:
                        viewHolder.addItemTv.setVisibility(View.VISIBLE);
                        break;
                }

                AddNewModel addNewModel = adapterModelList.get(i).getAddNewModel();
                viewHolder.addItemTv.setText(addNewModel.getTitle());
                viewHolder.btAddCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        mode = Constants.VIEW_MODE;
//                        onModeChangeListener.onModeChange(mode);
//                        notifyDataSetChanged();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants.USER_DETAIL, detailViewModel.getPatientDetailModel());
                        bundle.putSerializable(Constants.DOCTOR_DETAIL, detailViewModel.getDoctorDetailModel());
                        bundle.putString(ArgumentKeys.SEARCH_TYPE, VitalReportApiViewModel.LAST_WEEK);
                        bundle.putString(ArgumentKeys.DOCTOR_GUID, detailViewModel.getDoctorDetailModel().getUser_guid());
                        bundle.putString(ArgumentKeys.ORDER_ID, detailViewModel.getTranscriptionApiResponseModel().getOrder_id());

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
                        }
                    }
                });
                break;
            case VisitDetailConstants.TYPE_DATE:
                viewHolder.dateTv.setText(adapterModelList.get(i).getCategoryTitle());
                break;
            case VisitDetailConstants.TYPE_VITALS:
                VitalsApiResponseModel vitalsApiResponseModel = adapterModelList.get(i).getVitalsApiResponseModel();

                viewHolder.vitalTimeTv.setText(Utils.getFormatedTime(vitalsApiResponseModel.getCreated_at()));
                viewHolder.vitalDescriptionTv.setText(vitalsApiResponseModel.getCapturedBy());

                if (!vitalsApiResponseModel.getType().equals(SupportedMeasurementType.stethoscope)) {
                    viewHolder.vitalValueTv.setText(vitalsApiResponseModel.getValue().toString());
                    viewHolder.vitalUnitTv.setText(SupportedMeasurementType.getVitalUnit(vitalsApiResponseModel.getType()));
                } else {
                    StethBean stethBean = vitalsApiResponseModel.getStethBean();

                    viewHolder.vitalUnitTv.setText(stethBean.getSegments().size() + " - Segment");

                    viewHolder.vitalIv.setImageDrawable(activity.getDrawable(vitalsApiResponseModel.getStethIoImage()));
                    viewHolder.vitalIv.setVisibility(View.VISIBLE);

                    if (mode == Constants.VIEW_MODE) {
                        viewHolder.vitalItemCv.setOnClickListener(new View.OnClickListener() {
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
                    viewHolder.vitalAbnormalIndicatorCl.setVisibility(View.VISIBLE);
                }

                viewHolder.visitCb.setChecked(true);
                viewHolder.visitCb.setClickable(false);

                switch (mode) {
                    case Constants.VIEW_MODE:
                        viewHolder.visitCb.setVisibility(View.GONE);
                        break;
                    case Constants.EDIT_MODE:
                        viewHolder.visitCb.setVisibility(View.VISIBLE);
                        break;
                }
                viewHolder.vitalRootCl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = vitalsApiResponseModel.getUser_vital_id();
                        if (detailViewModel.getVitalsRemoveList().contains(id)) {
                            detailViewModel.getVitalsRemoveList().remove((Object) id);
                            viewHolder.visitCb.setChecked(true);
                        } else {
                            detailViewModel.getVitalsRemoveList().add(id);
                            viewHolder.visitCb.setChecked(false);
                        }
                    }
                });
                break;
            case VisitDetailConstants.TYPE_ORDER:
                VisitOrdersAdapterModel visitOrdersAdapterModel = adapterModelList.get(i).getVisitOrdersAdapterModel();
                viewHolder.orderListIv.setImageResource(visitOrdersAdapterModel.getDisplayImage());
                viewHolder.orderListTitleTv.setText(visitOrdersAdapterModel.getDisplayTitle());
                viewHolder.orderListSubTitleTv.setText(UserType.isUserPatient() ? detailViewModel.getDoctorDetailModel().getUserDisplay_name() : detailViewModel.getPatientDetailModel().getUserDisplay_name());

                viewHolder.visitCb.setChecked(true);
                viewHolder.visitCb.setClickable(false);
                switch (mode) {
                    case Constants.VIEW_MODE:
                        viewHolder.visitCb.setVisibility(View.GONE);
                        viewHolder.orderListItemCv.setOnClickListener(new View.OnClickListener() {
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
                        viewHolder.visitCb.setVisibility(View.VISIBLE);
                        viewHolder.orderListItemCv.setOnClickListener(null);
                        viewHolder.orderListItemCv.setClickable(false);
                        viewHolder.orderListItemCv.setFocusable(false);
                        break;
                }
                viewHolder.orderRootCl.setOnClickListener(new View.OnClickListener() {
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
                        viewHolder.visitCb.setChecked(checked);
                    }
                });
                break;
            case VisitDetailConstants.TYPE_DOCUMENT:
                viewHolder.recentHeaderTv.setVisibility(View.GONE);

                DocumentsApiResponseModel.ResultBean documentsApiResponseModel = adapterModelList.get(i).getDocumentModel();

                Utils.setImageWithGlide(activity.getApplicationContext(), viewHolder.documentIv, documentsApiResponseModel.getPath(), activity.getDrawable(R.drawable.profile_placeholder), true);

                GlideUrl glideUrl = Utils.getGlideUrlWithAuth(activity.getApplicationContext(), documentsApiResponseModel.getPath());

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
                        viewHolder.documentSizeTv.setText(documentsApiResponseModel.getCreator().getUserName() + " - " + size);

                        return false;
                    }
                }).submit();

                viewHolder.documentTitleTv.setText(documentsApiResponseModel.getName());

                viewHolder.documentSizeTv.setText(documentsApiResponseModel.getCreator().getUserName());

                viewHolder.visitCb.setChecked(true);
                viewHolder.visitCb.setClickable(false);
                switch (mode) {
                    case Constants.VIEW_MODE:
                        viewHolder.visitCb.setVisibility(View.GONE);
                        break;
                    case Constants.EDIT_MODE:
                        viewHolder.visitCb.setVisibility(View.VISIBLE);
                        break;
                }
                viewHolder.documentRootCl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (mode) {
                            case Constants.EDIT_MODE:
                                int id = visitDetailAdapterModel.getDocumentModel().getUser_file_id();

                                if (detailViewModel.getFilesRemoveList().contains(id)) {
                                    detailViewModel.getFilesRemoveList().remove((Object) id);
                                    viewHolder.visitCb.setChecked(true);
                                } else {
                                    detailViewModel.getFilesRemoveList().add(id);
                                    viewHolder.visitCb.setChecked(false);
                                }
                                break;
                            case Constants.VIEW_MODE:
                                DocumentsApiResponseModel documents = new DocumentsApiResponseModel();
                                List<DocumentsApiResponseModel.ResultBean> resultBeanList = new ArrayList<>();
                                resultBeanList.add(documentsApiResponseModel);
                                documents.setResult(resultBeanList);

                                Bundle bundle = new Bundle();
                                bundle.putSerializable(Constants.USER_DETAIL, documents);
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
                    viewHolder.energyUnitTv.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.energyUnitTv.setVisibility(View.GONE);
                }
                viewHolder.energyCountTv.setText(DietApiResponseModel.getCalorieValue(calories));
                viewHolder.energyUnitTv.setText(DietApiResponseModel.getCalorieUnit(activity));

                if (carbs == 0) {
                    viewHolder.carbsUnitTv.setVisibility(View.GONE);
                } else {
                    viewHolder.carbsUnitTv.setVisibility(View.VISIBLE);
                }
                viewHolder.carbsCountTv.setText(DietApiResponseModel.getDisplayValue(carbs));
                viewHolder.carbsUnitTv.setText(DietApiResponseModel.getDisplayUnit(activity, carbs));

                if (fat == 0) {
                    viewHolder.fatUnitTv.setVisibility(View.GONE);
                } else {
                    viewHolder.fatUnitTv.setVisibility(View.VISIBLE);
                }
                viewHolder.fatCountTv.setText(DietApiResponseModel.getDisplayValue(fat));
                viewHolder.fatUnitTv.setText(DietApiResponseModel.getDisplayUnit(activity, fat));

                if (protien == 0) {
                    viewHolder.proteinUnitTv.setVisibility(View.GONE);
                } else {
                    viewHolder.proteinUnitTv.setVisibility(View.VISIBLE);
                }
                viewHolder.proteinCountTv.setText(DietApiResponseModel.getDisplayValue(protien));
                viewHolder.proteinUnitTv.setText(DietApiResponseModel.getDisplayUnit(activity, protien));

                viewHolder.visitCb.setChecked(true);
                viewHolder.visitCb.setClickable(false);
                switch (mode) {
                    case Constants.VIEW_MODE:
                        viewHolder.visitCb.setVisibility(View.GONE);
                        break;
                    case Constants.EDIT_MODE:
                        viewHolder.visitCb.setVisibility(View.VISIBLE);
                        break;
                }
                viewHolder.dietRootLl.setOnClickListener(new View.OnClickListener() {
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
                                viewHolder.visitCb.setChecked(checked);
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
        }
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryTv, callDateTv, callTypeTv, timeTv, historyQuestionTv, historyAnswerTv, historyReasonTv,
                callerNameTv, addItemTv, dateTv;
        private CardView callTypeCv;
        private CardView btAddCardView;
        private ImageView transcriptVideoIv;
        private CustomUserListItemView userListIv;
        private LabelValueCustomView labelValueCv;
        private EditText inputEt;
        private CustomEditText transcriptInfoEt;

        private ConstraintLayout vitalRootCl;
        private CardView vitalItemCv;
        private ImageView vitalIv;
        private TextView vitalValueTv;
        private TextView vitalUnitTv;
        private TextView vitalDescriptionTv;
        private ConstraintLayout vitalAbnormalIndicatorCl;
        private TextView vitalTimeTv;
        private ConstraintLayout transcriptRootCl;
        private CheckBox visitCb;

        private ConstraintLayout orderRootCl;
        private TextView recentHeaderTv;
        private CardView orderListItemCv;
        private ImageView orderListIv;
        private TextView orderListTitleTv;
        private TextView orderListSubTitleTv;

        private ConstraintLayout documentRootCl;
        private CardView documentCv;
        private ImageView documentIv;
        private TextView documentTitleTv;
        private TextView documentSizeTv;

        private LinearLayout dietRootLl;
        private TextView dietHeaderTv;
        private CardView dietItemCv;
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryTv = (TextView) itemView.findViewById(R.id.category_tv);
            callDateTv = (TextView) itemView.findViewById(R.id.date_tv);
            callTypeCv = (CardView) itemView.findViewById(R.id.call_type_cv);
            callTypeTv = (TextView) itemView.findViewById(R.id.call_type_tv);
            timeTv = (TextView) itemView.findViewById(R.id.call_time_tv);
            transcriptVideoIv = (ImageView) itemView.findViewById(R.id.transcript_video_iv);
            userListIv = (CustomUserListItemView) itemView.findViewById(R.id.user_list_iv);
            labelValueCv = (LabelValueCustomView) itemView.findViewById(R.id.label_value_cv);
            historyQuestionTv = (TextView) itemView.findViewById(R.id.history_question_tv);
            historyAnswerTv = (TextView) itemView.findViewById(R.id.history_answer_tv);
            historyReasonTv = (TextView) itemView.findViewById(R.id.history_reason_tv);
            callerNameTv = (TextView) itemView.findViewById(R.id.caller_name_tv);
            transcriptInfoEt = (CustomEditText) itemView.findViewById(R.id.transcript_info_et);
            inputEt = (EditText) itemView.findViewById(R.id.input_et);
            btAddCardView = (CardView) itemView.findViewById(R.id.bt_add_card_view);
            addItemTv = (TextView) itemView.findViewById(R.id.add_item_tv);
            dateTv = (TextView) itemView.findViewById(R.id.header_tv);

            transcriptRootCl = (ConstraintLayout) itemView.findViewById(R.id.transcript_root_cl);
            visitCb = (CheckBox) itemView.findViewById(R.id.visit_cb);

            vitalRootCl = (ConstraintLayout) itemView.findViewById(R.id.vital_root_cl);
            vitalItemCv = (CardView) itemView.findViewById(R.id.item_cv);
            vitalIv = (ImageView) itemView.findViewById(R.id.vital_iv);
            vitalValueTv = (TextView) itemView.findViewById(R.id.value_tv);
            vitalUnitTv = (TextView) itemView.findViewById(R.id.unit_tv);
            vitalDescriptionTv = (TextView) itemView.findViewById(R.id.description_tv);
            vitalAbnormalIndicatorCl = (ConstraintLayout) itemView.findViewById(R.id.abnormal_indicator_cl);
            vitalTimeTv = (TextView) itemView.findViewById(R.id.time_tv);

            orderRootCl = (ConstraintLayout) itemView.findViewById(R.id.order_root_cl);
            orderListItemCv = (CardView) itemView.findViewById(R.id.order_list_item_cv);
            orderListIv = (ImageView) itemView.findViewById(R.id.order_list_iv);
            orderListTitleTv = (TextView) itemView.findViewById(R.id.order_list_title_tv);
            orderListSubTitleTv = (TextView) itemView.findViewById(R.id.order_list_sub_title_tv);

            documentRootCl = (ConstraintLayout) itemView.findViewById(R.id.document_root_cl);
            recentHeaderTv = (TextView) itemView.findViewById(R.id.recent_header_tv);
            documentCv = (CardView) itemView.findViewById(R.id.document_cv);
            documentIv = (ImageView) itemView.findViewById(R.id.document_iv);
            documentTitleTv = (TextView) itemView.findViewById(R.id.title_tv);
            documentSizeTv = (TextView) itemView.findViewById(R.id.size_tv);

            dietRootLl = (LinearLayout) itemView.findViewById(R.id.diet_root_ll);
            dietHeaderTv = (TextView) itemView.findViewById(R.id.header_tv);
            dietItemCv = (CardView) itemView.findViewById(R.id.item_cv);
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
         * History detail
         */
        if (detailViewModel.getSchedulesApiResponseModel() != null) {
            SchedulesApiResponseModel.ResultBean scheduleModel = detailViewModel.getSchedulesApiResponseModel();

            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_HEADER, activity.getString(R.string.details)));

            LabelValueModel labelValueModel;

            labelValueModel = new LabelValueModel(activity.getString(R.string.patient_history_has_been_updated), (scheduleModel.getDetail().isChange_medical_info() ? activity.getString(R.string.yes) : activity.getString(R.string.no)), false);
            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_LABEL_VALUE, labelValueModel));


            labelValueModel = new LabelValueModel(activity.getString(R.string.patient_demographic_changed), (scheduleModel.getDetail().isChange_demographic() ? activity.getString(R.string.yes) : activity.getString(R.string.no)), false);
            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_LABEL_VALUE, labelValueModel));


            labelValueModel = new LabelValueModel(activity.getString(R.string.patient_insurance_changed), (scheduleModel.getDetail().isInsurance_to_date() ? activity.getString(R.string.yes) : activity.getString(R.string.no)), false);
            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_LABEL_VALUE, labelValueModel));

            labelValueModel = new LabelValueModel(activity.getString(R.string.reason_for_visit), scheduleModel.getDetail().getReason(), true);
            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_LABEL_VALUE, labelValueModel));

            List<HistoryBean> historyBeanList = null;
            if (scheduleModel.getPatient() != null) {
                historyBeanList = new ArrayList<>(scheduleModel.getPatient().getHistory());
            }

            if (scheduleModel.getPatient_history() != null) {
                historyBeanList = new ArrayList<>(scheduleModel.getPatient_history());
            }
            if (historyBeanList != null && !historyBeanList.isEmpty()) {
                for (int i = 0; i < historyBeanList.size(); i++) {
                    if (historyBeanList.get(i).isIsYes())
                        modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_HISTORY_ITEM, historyBeanList.get(i)));
                }
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

                    DownloadTranscriptResponseModel.SpeakerLabelsBean speakerLabelsBean = new DownloadTranscriptResponseModel.SpeakerLabelsBean(downloadTranscriptResponseModel.getSpeakerLabels().get(i).getStart_time(),
                            downloadTranscriptResponseModel.getSpeakerLabels().get(i).getSpeaker_label(),
                            downloadTranscriptResponseModel.getSpeakerLabels().get(i).getEnd_time(),
                            downloadTranscriptResponseModel.getSpeakerLabels().get(i).getTranscript(),
                            downloadTranscriptResponseModel.getSpeakerLabels().get(i).isRemoved());
                    speakerLabelsBeans.add(speakerLabelsBean);

                    modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_TRANSCRIPT, downloadTranscriptResponseModel.getSpeakerLabels().get(i)));
                }
            }
            updateModel.setSpeakerLabels(speakerLabelsBeans);
            detailViewModel.setUpdatedTranscriptResponseModel(updateModel);
        }

        /**
         * Patient instructions
         */

        modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_HEADER, activity.getString(R.string.patient_instructions)));

        if (detailViewModel.getVisitsDetailApiResponseModel() != null && detailViewModel.getVisitsDetailApiResponseModel().getResult() != null) {
            modelList.add(new VisitDetailAdapterModel(VisitDetailConstants.TYPE_INPUT, detailViewModel.getVisitsDetailApiResponseModel().getResult().getInstructions()));
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


        return modelList;
    }

}
