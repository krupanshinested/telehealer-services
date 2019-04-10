package com.thealer.telehealer.views.home.recents;

import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.HistoryBean;
import com.thealer.telehealer.apilayer.models.diet.DietApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.documents.DocumentsApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.forms.OrdersFormsApiResponseModel;
import com.thealer.telehealer.apilayer.models.recents.DownloadTranscriptResponseModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiResponseModel;
import com.thealer.telehealer.views.home.recents.adapterModels.AddNewModel;
import com.thealer.telehealer.views.home.recents.adapterModels.CallSummaryModel;
import com.thealer.telehealer.views.home.recents.adapterModels.LabelValueModel;
import com.thealer.telehealer.views.home.recents.adapterModels.VisitOrdersAdapterModel;

import java.util.List;


/**
 * Created by Aswin on 25,April,2019
 */
public class VisitDetailAdapterModel {
    private int viewType;
    private String categoryTitle;
    private boolean isShow = true;
    private CallSummaryModel callSummaryModel;
    private CommonUserApiResponseModel userInfoModel;
    private LabelValueModel labelValueModel;
    private HistoryBean historyBean;
    private DownloadTranscriptResponseModel.SpeakerLabelsBean transcriptModel;
    private VitalsApiResponseModel vitalsApiResponseModel;
    private AddNewModel addNewModel;
    private String date;
    private VisitOrdersAdapterModel visitOrdersAdapterModel;
    private DocumentsApiResponseModel.ResultBean documentModel;
    private OrdersFormsApiResponseModel formsApiResponseModel;
    private List<DietApiResponseModel> dietApiResponseModel;

    public VisitDetailAdapterModel(int viewType, String categoryTitle) {
        this.viewType = viewType;
        this.categoryTitle = categoryTitle;
    }
    public VisitDetailAdapterModel(int viewType, String categoryTitle, boolean isShow) {
        this.viewType = viewType;
        this.categoryTitle = categoryTitle;
        this.isShow = isShow;
    }

    public VisitDetailAdapterModel(int viewType, CallSummaryModel callSummaryModel) {
        this.viewType = viewType;
        this.callSummaryModel = callSummaryModel;
    }

    public VisitDetailAdapterModel(int viewType, CommonUserApiResponseModel userInfoModel) {
        this.viewType = viewType;
        this.userInfoModel = userInfoModel;
    }

    public VisitDetailAdapterModel(int viewType, LabelValueModel labelValueModel) {
        this.viewType = viewType;
        this.labelValueModel = labelValueModel;
    }

    public VisitDetailAdapterModel(int viewType, HistoryBean historyBean) {
        this.viewType = viewType;
        this.historyBean = historyBean;
    }

    public VisitDetailAdapterModel(int viewType, DownloadTranscriptResponseModel.SpeakerLabelsBean transcriptModel) {
        this.viewType = viewType;
        this.transcriptModel = transcriptModel;
    }

    public VisitDetailAdapterModel(int viewType, DocumentsApiResponseModel.ResultBean documentModel) {
        this.viewType = viewType;
        this.documentModel = documentModel;
    }

    public VisitDetailAdapterModel(int viewType, OrdersFormsApiResponseModel formsApiResponseModel) {
        this.viewType = viewType;
        this.formsApiResponseModel = formsApiResponseModel;
    }

    public VisitDetailAdapterModel(int viewType) {
        this.viewType = viewType;
    }

    public VisitDetailAdapterModel(int viewType, VitalsApiResponseModel vitalsApiResponseModel) {
        this.viewType = viewType;
        this.vitalsApiResponseModel = vitalsApiResponseModel;
    }

    public VisitDetailAdapterModel(int viewType, AddNewModel addNewModel) {
        this.viewType = viewType;
        this.addNewModel = addNewModel;
    }

    public VisitDetailAdapterModel(int viewType, VisitOrdersAdapterModel visitOrdersAdapterModel) {
        this.viewType = viewType;
        this.visitOrdersAdapterModel = visitOrdersAdapterModel;
    }

    public VisitDetailAdapterModel(int viewType, List<DietApiResponseModel> dietApiResponseModel) {
        this.viewType = viewType;
        this.dietApiResponseModel = dietApiResponseModel;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public CallSummaryModel getCallSummaryModel() {
        return callSummaryModel;
    }

    public void setCallSummaryModel(CallSummaryModel callSummaryModel) {
        this.callSummaryModel = callSummaryModel;
    }

    public CommonUserApiResponseModel getUserInfoModel() {
        return userInfoModel;
    }

    public void setUserInfoModel(CommonUserApiResponseModel userInfoModel) {
        this.userInfoModel = userInfoModel;
    }

    public LabelValueModel getLabelValueModel() {
        return labelValueModel;
    }

    public void setLabelValueModel(LabelValueModel labelValueModel) {
        this.labelValueModel = labelValueModel;
    }

    public HistoryBean getHistoryBean() {
        return historyBean;
    }

    public void setHistoryBean(HistoryBean historyBean) {
        this.historyBean = historyBean;
    }

    public DownloadTranscriptResponseModel.SpeakerLabelsBean getTranscriptModel() {
        return transcriptModel;
    }

    public void setTranscriptModel(DownloadTranscriptResponseModel.SpeakerLabelsBean transcriptModel) {
        this.transcriptModel = transcriptModel;
    }

    public VitalsApiResponseModel getVitalsApiResponseModel() {
        return vitalsApiResponseModel;
    }

    public void setVitalsApiResponseModel(VitalsApiResponseModel vitalsApiResponseModel) {
        this.vitalsApiResponseModel = vitalsApiResponseModel;
    }

    public AddNewModel getAddNewModel() {
        return addNewModel;
    }

    public void setAddNewModel(AddNewModel addNewModel) {
        this.addNewModel = addNewModel;
    }

    public VisitOrdersAdapterModel getVisitOrdersAdapterModel() {
        return visitOrdersAdapterModel;
    }

    public void setVisitOrdersAdapterModel(VisitOrdersAdapterModel visitOrdersAdapterModel) {
        this.visitOrdersAdapterModel = visitOrdersAdapterModel;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public DocumentsApiResponseModel.ResultBean getDocumentModel() {
        return documentModel;
    }

    public void setDocumentModel(DocumentsApiResponseModel.ResultBean documentModel) {
        this.documentModel = documentModel;
    }

    public OrdersFormsApiResponseModel getFormsApiResponseModel() {
        return formsApiResponseModel;
    }

    public void setFormsApiResponseModel(OrdersFormsApiResponseModel formsApiResponseModel) {
        this.formsApiResponseModel = formsApiResponseModel;
    }

    public List<DietApiResponseModel> getDietApiResponseModel() {
        return dietApiResponseModel;
    }

    public void setDietApiResponseModel(List<DietApiResponseModel> dietApiResponseModel) {
        this.dietApiResponseModel = dietApiResponseModel;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
