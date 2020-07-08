package com.thealer.telehealer.views.home.recents;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.HistoryBean;
import com.thealer.telehealer.apilayer.models.diet.DietApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersIdListApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersPrescriptionApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersSpecialistApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.documents.DocumentsApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.forms.OrdersUserFormsApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.lab.OrdersLabApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.miscellaneous.MiscellaneousApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.radiology.GetRadiologyResponseModel;
import com.thealer.telehealer.apilayer.models.procedure.ProcedureModel;
import com.thealer.telehealer.apilayer.models.recents.DownloadTranscriptResponseModel;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiResponseModel;
import com.thealer.telehealer.apilayer.models.recents.VisitDiagnosisModel;
import com.thealer.telehealer.apilayer.models.recents.VisitsDetailApiResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiResponseModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aswin on 25,April,2019
 */
public class VisitDetailViewModel extends ViewModel {
    private RecentsApiResponseModel.ResultBean recentResponseModel;
    private DownloadTranscriptResponseModel downloadTranscriptResponseModel;
    private DownloadTranscriptResponseModel updatedTranscriptResponseModel;
    private VisitsDetailApiResponseModel visitsDetailApiResponseModel;
    private SchedulesApiResponseModel.ResultBean schedulesApiResponseModel;
    private CommonUserApiResponseModel doctorDetailModel;
    private CommonUserApiResponseModel patientDetailModel;

    private ArrayList<VitalsApiResponseModel> vitalsApiResponseModels;
    private OrdersIdListApiResponseModel ordersIdListApiResponseModel;
    private ArrayList<DocumentsApiResponseModel.ResultBean> documentsApiResponseModels;
    private ArrayList<OrdersUserFormsApiResponseModel> formsApiResponseModels;
    private ArrayList<DietApiResponseModel> dietApiResponseModels;

    private List<Integer> vitalsRemoveList = new ArrayList<>();
    private List<Integer> DietRemoveList = new ArrayList<>();
    private List<Integer> FormsRemoveList = new ArrayList<>();
    private List<Integer> FilesRemoveList = new ArrayList<>();
    private List<Integer> LabRemoveList = new ArrayList<>();
    private List<Integer> XrayRemoveList = new ArrayList<>();
    private List<Integer> SpecialistRemoveList = new ArrayList<>();
    private List<Integer> PrescriptionRemoveList = new ArrayList<>();
    private List<Integer> MiscellaneousRemoveList = new ArrayList<>();

    private List<Integer> vitalsAddList = new ArrayList<>();
    private List<Integer> DietAddList = new ArrayList<>();
    private List<Integer> FormsAddList = new ArrayList<>();
    private List<Integer> FilesAddList = new ArrayList<>();
    private List<Integer> LabAddList = new ArrayList<>();
    private List<Integer> XrayAddList = new ArrayList<>();
    private List<Integer> SpecialistAddList = new ArrayList<>();
    private List<Integer> PrescriptionAddList = new ArrayList<>();
    private List<Integer> MiscellaneousAddList = new ArrayList<>();

    private HashMap<Integer, OrdersLabApiResponseModel.LabsResponseBean> labsMap = new HashMap<>();
    private HashMap<Integer, GetRadiologyResponseModel.ResultBean> xraysMap = new HashMap<>();
    private HashMap<Integer, OrdersSpecialistApiResponseModel.ResultBean> specialistsMap = new HashMap<>();
    private HashMap<Integer, OrdersPrescriptionApiResponseModel.OrdersResultBean> prescriptionsMap = new HashMap<>();
    private HashMap<Integer, MiscellaneousApiResponseModel.ResultBean> miscellaneousMap = new HashMap<>();
    private HashMap<Integer, OrdersUserFormsApiResponseModel> formMap = new HashMap<>();

    private Map<String, List<DietApiResponseModel>> dietListModelMap = new HashMap<>();
    private List<UpdatedHistoryBean> historyList = new ArrayList<>();

    private String instruction;
    private VisitDiagnosisModel diagnosis;
    private boolean isInstructionUpdated, isDiagnosisUpdated;


    public List<UpdatedHistoryBean> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<UpdatedHistoryBean> historyList) {
        this.historyList = historyList;
    }

    public List<UpdatedHistoryBean> getUpdatedHistoryModelList(List<HistoryBean> historyBeanList) {
        List<UpdatedHistoryBean> updatedHistoryBeanList = new ArrayList<>();

        if (historyBeanList != null)
            for (int i = 0; i < historyBeanList.size(); i++) {
                updatedHistoryBeanList.add(new UpdatedHistoryBean(historyBeanList.get(i).isIsYes(),
                        historyBeanList.get(i).getQuestion(),
                        historyBeanList.get(i).getReason()));
            }

        return updatedHistoryBeanList;
    }

    public boolean isHistoryUpdate() {
        for (int i = 0; i < getHistoryList().size(); i++) {

            int compare = Boolean.compare(getHistoryList().get(i).isIsYes(), getPatientDetailModel().getHistory().get(i).isIsYes());
            Log.e("aswin", "" + i);
            Log.e("aswin", getPatientDetailModel().getHistory().get(i).isIsYes() + "\t" + getPatientDetailModel().getHistory().get(i).getReason());
            Log.e("aswin", getHistoryList().get(i).isIsYes() + "\t" + getHistoryList().get(i).getReason());
            if ((compare != 0) ||
                    !getHistoryList().get(i).getReason().equals(getPatientDetailModel().getHistory().get(i).getReason())) {
                return true;
            }
        }
        return false;
    }

    public void resetHistory() {
        if (getPatientDetailModel() != null)
            setHistoryList(getUpdatedHistoryModelList(getPatientDetailModel().getHistory()));
    }

    public static class UpdatedHistoryBean extends HistoryBean {
        public UpdatedHistoryBean(boolean isYes, String question, String reason) {
            super(isYes, question, reason);
        }
    }

    public void setDiagnosisData() {
        if (visitsDetailApiResponseModel != null && visitsDetailApiResponseModel.getResult().getDiagnosis() != null &&
                !(visitsDetailApiResponseModel.getResult().getDiagnosis() instanceof String)) {
            VisitDiagnosisModel visitDiagnosisModel = new Gson().fromJson(new Gson().toJson(visitsDetailApiResponseModel.getResult().getDiagnosis()), VisitDiagnosisModel.class);
            setDiagnosis(visitDiagnosisModel);
        } else {
            setDiagnosis(null);
        }
    }

    public VisitDiagnosisModel getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(VisitDiagnosisModel diagnosis) {
        this.diagnosis = diagnosis;
    }


    private List<ProcedureModel.CPTCodesBean> selectedCptCodeList = new ArrayList<>();

    public boolean isProcedureUpdated() {
        boolean isUpdated = false;

        if (getVisitsDetailApiResponseModel().getResult().getProcedure() == null) {
            isUpdated = !selectedCptCodeList.isEmpty();
        } else {
            if (selectedCptCodeList.size() != getVisitsDetailApiResponseModel().getResult().getProcedure().getCPT_codes().size()) {
                isUpdated = true;
            } else {
                for (int i = 0; i < getVisitsDetailApiResponseModel().getResult().getProcedure().getCPT_codes().size(); i++) {

                    boolean isAvailable = false;

                    for (int j = 0; j < selectedCptCodeList.size(); j++) {
                        if (selectedCptCodeList.get(j).getCode().trim().equals(getVisitsDetailApiResponseModel().getResult().getProcedure().getCPT_codes().get(i).getCode().trim())) {
                            isAvailable = true;
                            break;
                        }
                    }

                    if (!isAvailable) {
                        isUpdated = true;
                        break;
                    }
                }
            }
        }
        return isUpdated;
    }

    public List<ProcedureModel.CPTCodesBean> getSelectedCptCodeList() {
        return selectedCptCodeList;
    }

    public void setSelectedCptCodeList(List<ProcedureModel.CPTCodesBean> selectedCptCodeList) {
        this.selectedCptCodeList = selectedCptCodeList;
    }

    public boolean isTranscriptUpdated() {
        if (updatedTranscriptResponseModel != null && updatedTranscriptResponseModel.getSpeakerLabels() != null) {
            for (int i = 0; i < updatedTranscriptResponseModel.getSpeakerLabels().size(); i++) {
                if (updatedTranscriptResponseModel.getSpeakerLabels().get(i).isRemoved()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isTranscriptEdited() {
        if (downloadTranscriptResponseModel != null && updatedTranscriptResponseModel != null) {
            if (downloadTranscriptResponseModel.getSpeakerLabels().size() != updatedTranscriptResponseModel.getSpeakerLabels().size())
                return true;

            for (int i = 0; i < downloadTranscriptResponseModel.getSpeakerLabels().size(); i++) {
                if (!downloadTranscriptResponseModel.getSpeakerLabels().get(i).isModelEqual(updatedTranscriptResponseModel.getSpeakerLabels().get(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public boolean isInstructionUpdated() {
        return isInstructionUpdated;
    }

    public void setInstructionUpdated(boolean instructionUpdated) {
        isInstructionUpdated = instructionUpdated;
    }

    public boolean isDiagnosisUpdated() {
        return isDiagnosisUpdated;
    }

    public void setDiagnosisUpdated(boolean diagnosisUpdated) {
        isDiagnosisUpdated = diagnosisUpdated;
    }

    public RecentsApiResponseModel.ResultBean getRecentResponseModel() {
        return recentResponseModel;
    }

    public void setRecentResponseModel(RecentsApiResponseModel.ResultBean recentResponseModel) {
        this.recentResponseModel = recentResponseModel;
    }

    public DownloadTranscriptResponseModel getDownloadTranscriptResponseModel() {
        return downloadTranscriptResponseModel;
    }

    public void setDownloadTranscriptResponseModel(DownloadTranscriptResponseModel downloadTranscriptResponseModel) {
        this.downloadTranscriptResponseModel = downloadTranscriptResponseModel;
    }

    public DownloadTranscriptResponseModel getUpdatedTranscriptResponseModel() {
        return updatedTranscriptResponseModel;
    }

    public void setUpdatedTranscriptResponseModel(DownloadTranscriptResponseModel updatedTranscriptResponseModel) {
        this.updatedTranscriptResponseModel = updatedTranscriptResponseModel;
    }

    public VisitsDetailApiResponseModel getVisitsDetailApiResponseModel() {
        return visitsDetailApiResponseModel;
    }

    public void setVisitsDetailApiResponseModel(VisitsDetailApiResponseModel visitsDetailApiResponseModel) {
        this.visitsDetailApiResponseModel = visitsDetailApiResponseModel;
    }

    public SchedulesApiResponseModel.ResultBean getSchedulesApiResponseModel() {
        return schedulesApiResponseModel;
    }

    public void setSchedulesApiResponseModel(SchedulesApiResponseModel.ResultBean schedulesApiResponseModel) {
        this.schedulesApiResponseModel = schedulesApiResponseModel;
    }

    public CommonUserApiResponseModel getDoctorDetailModel() {
        return doctorDetailModel;
    }

    public void setDoctorDetailModel(CommonUserApiResponseModel doctorDetailModel) {
        this.doctorDetailModel = doctorDetailModel;
    }

    public CommonUserApiResponseModel getPatientDetailModel() {
        return patientDetailModel;
    }

    public void setPatientDetailModel(CommonUserApiResponseModel patientDetailModel) {
        this.patientDetailModel = patientDetailModel;
    }

    public ArrayList<VitalsApiResponseModel> getVitalsApiResponseModels() {
        return vitalsApiResponseModels;
    }

    public void setVitalsApiResponseModels(ArrayList<VitalsApiResponseModel> vitalsApiResponseModels) {
        this.vitalsApiResponseModels = vitalsApiResponseModels;
    }

    public OrdersIdListApiResponseModel getOrdersIdListApiResponseModel() {
        return ordersIdListApiResponseModel;
    }

    public void setOrdersIdListApiResponseModel(OrdersIdListApiResponseModel ordersIdListApiResponseModel) {
        this.ordersIdListApiResponseModel = ordersIdListApiResponseModel;
    }

    public ArrayList<DocumentsApiResponseModel.ResultBean> getDocumentsApiResponseModels() {
        return documentsApiResponseModels;
    }

    public void setDocumentsApiResponseModels(ArrayList<DocumentsApiResponseModel.ResultBean> documentsApiResponseModels) {
        this.documentsApiResponseModels = documentsApiResponseModels;
    }

    public ArrayList<OrdersUserFormsApiResponseModel> getFormsApiResponseModels() {
        return formsApiResponseModels;
    }

    public void setFormsApiResponseModels(ArrayList<OrdersUserFormsApiResponseModel> formsApiResponseModels) {
        this.formsApiResponseModels = formsApiResponseModels;
    }

    public ArrayList<DietApiResponseModel> getDietApiResponseModels() {
        return dietApiResponseModels;
    }

    public void setDietApiResponseModels(ArrayList<DietApiResponseModel> dietApiResponseModels) {
        this.dietApiResponseModels = dietApiResponseModels;
    }

    public List<Integer> getVitalsRemoveList() {
        return vitalsRemoveList;
    }

    public void setVitalsRemoveList(List<Integer> vitalsRemoveList) {
        this.vitalsRemoveList = vitalsRemoveList;
    }

    public List<Integer> getDietRemoveList() {
        return DietRemoveList;
    }

    public void setDietRemoveList(List<Integer> dietRemoveList) {
        DietRemoveList = dietRemoveList;
    }

    public List<Integer> getFormsRemoveList() {
        return FormsRemoveList;
    }

    public void setFormsRemoveList(List<Integer> formsRemoveList) {
        FormsRemoveList = formsRemoveList;
    }

    public List<Integer> getFilesRemoveList() {
        return FilesRemoveList;
    }

    public void setFilesRemoveList(List<Integer> filesRemoveList) {
        FilesRemoveList = filesRemoveList;
    }

    public List<Integer> getLabRemoveList() {
        return LabRemoveList;
    }

    public void setLabRemoveList(List<Integer> labRemoveList) {
        LabRemoveList = labRemoveList;
    }

    public List<Integer> getXrayRemoveList() {
        return XrayRemoveList;
    }

    public void setXrayRemoveList(List<Integer> xrayRemoveList) {
        XrayRemoveList = xrayRemoveList;
    }

    public List<Integer> getSpecialistRemoveList() {
        return SpecialistRemoveList;
    }

    public void setSpecialistRemoveList(List<Integer> specialistRemoveList) {
        SpecialistRemoveList = specialistRemoveList;
    }

    public List<Integer> getPrescriptionRemoveList() {
        return PrescriptionRemoveList;
    }

    public void setPrescriptionRemoveList(List<Integer> prescriptionRemoveList) {
        PrescriptionRemoveList = prescriptionRemoveList;
    }

    public List<Integer> getMiscellaneousRemoveList() {
        return MiscellaneousRemoveList;
    }

    public void setMiscellaneousRemoveList(List<Integer> miscellaneousRemoveList) {
        MiscellaneousRemoveList = miscellaneousRemoveList;
    }

    public Map<String, List<DietApiResponseModel>> getDietListModelMap() {
        return dietListModelMap;
    }

    public void setDietListModelMap(Map<String, List<DietApiResponseModel>> dietListModelMap) {
        this.dietListModelMap = dietListModelMap;
    }

    public List<Integer> getVitalsAddList() {
        return vitalsAddList;
    }

    public void setVitalsAddList(List<Integer> vitalsAddList) {
        this.vitalsAddList = vitalsAddList;
    }

    public List<Integer> getDietAddList() {
        return DietAddList;
    }

    public void setDietAddList(List<Integer> dietAddList) {
        DietAddList = dietAddList;
    }

    public List<Integer> getFormsAddList() {
        return FormsAddList;
    }

    public void setFormsAddList(List<Integer> formsAddList) {
        FormsAddList = formsAddList;
    }

    public List<Integer> getFilesAddList() {
        return FilesAddList;
    }

    public void setFilesAddList(List<Integer> filesAddList) {
        FilesAddList = filesAddList;
    }

    public List<Integer> getLabAddList() {
        return LabAddList;
    }

    public void setLabAddList(List<Integer> labAddList) {
        LabAddList = labAddList;
    }

    public List<Integer> getXrayAddList() {
        return XrayAddList;
    }

    public void setXrayAddList(List<Integer> xrayAddList) {
        XrayAddList = xrayAddList;
    }

    public List<Integer> getSpecialistAddList() {
        return SpecialistAddList;
    }

    public void setSpecialistAddList(List<Integer> specialistAddList) {
        SpecialistAddList = specialistAddList;
    }

    public List<Integer> getPrescriptionAddList() {
        return PrescriptionAddList;
    }

    public void setPrescriptionAddList(List<Integer> prescriptionAddList) {
        PrescriptionAddList = prescriptionAddList;
    }

    public List<Integer> getMiscellaneousAddList() {
        return MiscellaneousAddList;
    }

    public void setMiscellaneousAddList(List<Integer> miscellaneousAddList) {
        MiscellaneousAddList = miscellaneousAddList;
    }

    public HashMap<Integer, OrdersLabApiResponseModel.LabsResponseBean> getLabsMap() {
        return labsMap;
    }

    public void setLabsMap(HashMap<Integer, OrdersLabApiResponseModel.LabsResponseBean> labsMap) {
        this.labsMap = labsMap;
    }

    public HashMap<Integer, GetRadiologyResponseModel.ResultBean> getXraysMap() {
        return xraysMap;
    }

    public void setXraysMap(HashMap<Integer, GetRadiologyResponseModel.ResultBean> xraysMap) {
        this.xraysMap = xraysMap;
    }

    public HashMap<Integer, OrdersSpecialistApiResponseModel.ResultBean> getSpecialistsMap() {
        return specialistsMap;
    }

    public void setSpecialistsMap(HashMap<Integer, OrdersSpecialistApiResponseModel.ResultBean> specialistsMap) {
        this.specialistsMap = specialistsMap;
    }

    public HashMap<Integer, OrdersPrescriptionApiResponseModel.OrdersResultBean> getPrescriptionsMap() {
        return prescriptionsMap;
    }

    public void setPrescriptionsMap(HashMap<Integer, OrdersPrescriptionApiResponseModel.OrdersResultBean> prescriptionsMap) {
        this.prescriptionsMap = prescriptionsMap;
    }

    public HashMap<Integer, MiscellaneousApiResponseModel.ResultBean> getMiscellaneousMap() {
        return miscellaneousMap;
    }

    public void setMiscellaneousMap(HashMap<Integer, MiscellaneousApiResponseModel.ResultBean> miscellaneousMap) {
        this.miscellaneousMap = miscellaneousMap;
    }

    public HashMap<Integer, OrdersUserFormsApiResponseModel> getFormMap() {
        return formMap;
    }

    public void setFormMap(HashMap<Integer, OrdersUserFormsApiResponseModel> formMap) {
        this.formMap = formMap;
    }

    public void removeVital() {
        getVisitsDetailApiResponseModel().getResult().getUser_vitals().removeAll(getVitalsRemoveList());
        ArrayList<VitalsApiResponseModel> vitalsList = new ArrayList<>();
        for (int i = 0; i < getVitalsApiResponseModels().size(); i++) {
            if (!getVitalsRemoveList().contains(getVitalsApiResponseModels().get(i).getUser_vital_id())) {
                vitalsList.add(getVitalsApiResponseModels().get(i));
            }
        }
        setVitalsApiResponseModels(vitalsList);
        getVitalsRemoveList().clear();
        getVitalsAddList().clear();
    }

    public void removeDiets() {
        getVisitsDetailApiResponseModel().getResult().getUser_diets().removeAll(getDietRemoveList());
        ArrayList<DietApiResponseModel> dietModels = new ArrayList<>();
        for (int i = 0; i < getDietApiResponseModels().size(); i++) {
            if (!getDietRemoveList().contains(getDietApiResponseModels().get(i).getUser_diet_id())) {
                dietModels.add(getDietApiResponseModels().get(i));
            }
        }
        setDietApiResponseModels(dietModels);
        getDietRemoveList().clear();
        getDietAddList().clear();

    }

    public void removeForms() {
        getVisitsDetailApiResponseModel().getResult().getUser_forms().removeAll(getFormsRemoveList());
        ArrayList<OrdersUserFormsApiResponseModel> formsList = new ArrayList<>();
        for (int i = 0; i < getFormsApiResponseModels().size(); i++) {
            if (!getFormsRemoveList().contains(getFormsApiResponseModels().get(i).getUser_form_id())) {
                formsList.add(getFormsApiResponseModels().get(i));
            }
        }
        setFormsApiResponseModels(formsList);
        getFormsRemoveList().clear();
        getFormsAddList().clear();
        getFormMap().clear();
    }

    public void removeFiles() {
        getVisitsDetailApiResponseModel().getResult().getUser_files().removeAll(getFilesRemoveList());
        ArrayList<DocumentsApiResponseModel.ResultBean> documentList = new ArrayList<>();
        for (int i = 0; i < getDocumentsApiResponseModels().size(); i++) {
            if (!getFilesRemoveList().contains(getDocumentsApiResponseModels().get(i).getUser_file_id())) {
                documentList.add(getDocumentsApiResponseModels().get(i));
            }
        }
        setDocumentsApiResponseModels(documentList);
        getFilesRemoveList().clear();
        getFilesAddList().clear();

    }

    public void removeLabs() {
        getVisitsDetailApiResponseModel().getResult().getLabs().removeAll(getLabRemoveList());
        List<OrdersLabApiResponseModel.LabsResponseBean> labsResponseBeanList = new ArrayList<>();

        for (int i = 0; i < getOrdersIdListApiResponseModel().getLabs().size(); i++) {
            if (!getLabRemoveList().contains(getOrdersIdListApiResponseModel().getLabs().get(i).getReferral_id())) {
                labsResponseBeanList.add(getOrdersIdListApiResponseModel().getLabs().get(i));
            }
        }
        getOrdersIdListApiResponseModel().setLabs(labsResponseBeanList);
        setOrdersIdListApiResponseModel(getOrdersIdListApiResponseModel());
        getLabRemoveList().clear();
        getLabAddList().clear();
        getLabsMap().clear();
    }

    public void removeXrays() {
        getVisitsDetailApiResponseModel().getResult().getXrays().removeAll(getXrayRemoveList());
        List<GetRadiologyResponseModel.ResultBean> xrayList = new ArrayList<>();

        for (int i = 0; i < getOrdersIdListApiResponseModel().getXrays().size(); i++) {
            if (!getXrayRemoveList().contains(getOrdersIdListApiResponseModel().getXrays().get(i).getReferral_id())) {
                xrayList.add(getOrdersIdListApiResponseModel().getXrays().get(i));
            }
        }
        getOrdersIdListApiResponseModel().setXrays(xrayList);
        setOrdersIdListApiResponseModel(getOrdersIdListApiResponseModel());
        getXrayRemoveList().clear();
        getXrayAddList().clear();
        getXraysMap().clear();
    }

    public void removeSpecialist() {
        getVisitsDetailApiResponseModel().getResult().getSpecialists().removeAll(getSpecialistRemoveList());
        List<OrdersSpecialistApiResponseModel.ResultBean> list = new ArrayList<>();

        for (int i = 0; i < getOrdersIdListApiResponseModel().getSpecialists().size(); i++) {
            if (!getSpecialistRemoveList().contains(getOrdersIdListApiResponseModel().getSpecialists().get(i).getReferral_id())) {
                list.add(getOrdersIdListApiResponseModel().getSpecialists().get(i));
            }
        }
        getOrdersIdListApiResponseModel().setSpecialists(list);
        setOrdersIdListApiResponseModel(getOrdersIdListApiResponseModel());
        getSpecialistRemoveList().clear();
        getSpecialistAddList().clear();
        getSpecialistsMap().clear();
    }

    public void removePrescription() {
        getVisitsDetailApiResponseModel().getResult().getPrescriptions().removeAll(getPrescriptionRemoveList());

        List<OrdersPrescriptionApiResponseModel.OrdersResultBean> list = new ArrayList<>();

        for (int i = 0; i < getOrdersIdListApiResponseModel().getPrescriptions().size(); i++) {
            if (!getPrescriptionRemoveList().contains(getOrdersIdListApiResponseModel().getPrescriptions().get(i).getReferral_id())) {
                list.add(getOrdersIdListApiResponseModel().getPrescriptions().get(i));
            }
        }
        getOrdersIdListApiResponseModel().setPrescriptions(list);
        setOrdersIdListApiResponseModel(getOrdersIdListApiResponseModel());
        getPrescriptionRemoveList().clear();
        getPrescriptionAddList().clear();
        getPrescriptionsMap().clear();

    }

    public void removeMiscellaneous() {
        getVisitsDetailApiResponseModel().getResult().getMiscellaneous().removeAll(getMiscellaneousRemoveList());
        List<MiscellaneousApiResponseModel.ResultBean> list = new ArrayList<>();

        for (int i = 0; i < getOrdersIdListApiResponseModel().getMiscellaneous().size(); i++) {
            if (!getMiscellaneousRemoveList().contains(getOrdersIdListApiResponseModel().getMiscellaneous().get(i).getReferral_id())) {
                list.add(getOrdersIdListApiResponseModel().getMiscellaneous().get(i));
            }
        }
        getOrdersIdListApiResponseModel().setMiscellaneous(list);
        setOrdersIdListApiResponseModel(getOrdersIdListApiResponseModel());
        getMiscellaneousRemoveList().clear();
        getMiscellaneousAddList().clear();
        getMiscellaneousMap().clear();
    }

    public void clearAddRemoveList() {
        getVitalsRemoveList().clear();
        getDietRemoveList().clear();
        getFormsRemoveList().clear();
        getFilesRemoveList().clear();
        getLabRemoveList().clear();
        getXrayRemoveList().clear();
        getSpecialistRemoveList().clear();
        getPrescriptionRemoveList().clear();
        getMiscellaneousRemoveList().clear();

        getLabsMap().clear();
        getXraysMap().clear();
        getSpecialistsMap().clear();
        getPrescriptionsMap().clear();
        getMiscellaneousMap().clear();
        getFormMap().clear();

        removeAddedVitals();
        removeAddedDiets();
        removeAddedForms();
        removeAddedFiles();
        removeAddedOrders();

        resetHistory();
        setDiagnosisData();

    }

    private void removeAddedVitals() {
        if (!getVitalsAddList().isEmpty()) {
            ArrayList<VitalsApiResponseModel> vitalsApiResponseModelArrayList = new ArrayList<>();
            for (int i = 0; i < getVitalsApiResponseModels().size(); i++) {
                if (!getVitalsAddList().contains(getVitalsApiResponseModels().get(i).getUser_vital_id())) {
                    vitalsApiResponseModelArrayList.add(getVitalsApiResponseModels().get(i));
                }
            }
            setVitalsApiResponseModels(vitalsApiResponseModelArrayList);
            getVitalsAddList().clear();
        }
    }

    private void removeAddedDiets() {
        if (!getDietAddList().isEmpty()) {
            ArrayList<DietApiResponseModel> dietApiResponseModels = new ArrayList<>();
            for (int i = 0; i < getDietApiResponseModels().size(); i++) {
                if (!getDietAddList().contains(getDietApiResponseModels().get(i).getUser_diet_id())) {
                    dietApiResponseModels.add(getDietApiResponseModels().get(i));
                }
            }
            setDietApiResponseModels(dietApiResponseModels);
            getDietAddList().clear();
        }
    }

    private void removeAddedForms() {
        if (!getFormsAddList().isEmpty()) {

            ArrayList<OrdersUserFormsApiResponseModel> formsList = new ArrayList<>();
            for (int i = 0; i < getFormsApiResponseModels().size(); i++) {
                if (!getFormsAddList().contains(getFormsApiResponseModels().get(i).getUser_form_id())) {
                    formsList.add(getFormsApiResponseModels().get(i));
                }
            }
            setFormsApiResponseModels(formsList);
            getFormsAddList().clear();
        }
    }

    private void removeAddedFiles() {
        if (!getFilesAddList().isEmpty()) {
            ArrayList<DocumentsApiResponseModel.ResultBean> arrayList = new ArrayList<>();

            for (int i = 0; i < getDocumentsApiResponseModels().size(); i++) {
                if (!getFilesAddList().contains(getDocumentsApiResponseModels().get(i).getUser_file_id())) {
                    arrayList.add(getDocumentsApiResponseModels().get(i));
                }
            }
            setDocumentsApiResponseModels(arrayList);
            getFilesAddList().clear();
        }
    }

    private void removeAddedOrders() {
        if (getOrdersIdListApiResponseModel() != null) {
            if (!getPrescriptionAddList().isEmpty()) {
                List<OrdersPrescriptionApiResponseModel.OrdersResultBean> ordersResultBeanList = new ArrayList<>();

                for (int i = 0; i < getOrdersIdListApiResponseModel().getPrescriptions().size(); i++) {
                    if (!getPrescriptionAddList().contains(getOrdersIdListApiResponseModel().getPrescriptions().get(i).getReferral_id())) {
                        ordersResultBeanList.add(getOrdersIdListApiResponseModel().getPrescriptions().get(i));
                    }
                }
                getOrdersIdListApiResponseModel().setPrescriptions(ordersResultBeanList);
                getPrescriptionAddList().clear();
            }

            if (!getSpecialistAddList().isEmpty()) {
                List<OrdersSpecialistApiResponseModel.ResultBean> ordersResultBeanList = new ArrayList<>();

                for (int i = 0; i < getOrdersIdListApiResponseModel().getSpecialists().size(); i++) {
                    if (!getSpecialistAddList().contains(getOrdersIdListApiResponseModel().getSpecialists().get(i).getReferral_id())) {
                        ordersResultBeanList.add(getOrdersIdListApiResponseModel().getSpecialists().get(i));
                    }
                }
                getOrdersIdListApiResponseModel().setSpecialists(ordersResultBeanList);
                getSpecialistAddList().clear();

            }
            if (!getLabAddList().isEmpty()) {
                List<OrdersLabApiResponseModel.LabsResponseBean> ordersResultBeanList = new ArrayList<>();

                for (int i = 0; i < getOrdersIdListApiResponseModel().getLabs().size(); i++) {
                    if (!getLabAddList().contains(getOrdersIdListApiResponseModel().getLabs().get(i).getReferral_id())) {
                        ordersResultBeanList.add(getOrdersIdListApiResponseModel().getLabs().get(i));
                    }
                }
                getOrdersIdListApiResponseModel().setLabs(ordersResultBeanList);
                getLabAddList().clear();

            }
            if (!getXrayAddList().isEmpty()) {
                List<GetRadiologyResponseModel.ResultBean> ordersResultBeanList = new ArrayList<>();

                for (int i = 0; i < getOrdersIdListApiResponseModel().getXrays().size(); i++) {
                    if (!getXrayAddList().contains(getOrdersIdListApiResponseModel().getXrays().get(i).getReferral_id())) {
                        ordersResultBeanList.add(getOrdersIdListApiResponseModel().getXrays().get(i));
                    }
                }
                getOrdersIdListApiResponseModel().setXrays(ordersResultBeanList);
                getXrayAddList().clear();

            }

            if (!getMiscellaneousAddList().isEmpty()) {
                List<MiscellaneousApiResponseModel.ResultBean> ordersResultBeanList = new ArrayList<>();

                for (int i = 0; i < getOrdersIdListApiResponseModel().getMiscellaneous().size(); i++) {
                    if (!getMiscellaneousAddList().contains(getOrdersIdListApiResponseModel().getMiscellaneous().get(i).getReferral_id())) {
                        ordersResultBeanList.add(getOrdersIdListApiResponseModel().getMiscellaneous().get(i));
                    }
                }
                getOrdersIdListApiResponseModel().setMiscellaneous(ordersResultBeanList);
                getMiscellaneousAddList().clear();
            }
        }
    }
}
