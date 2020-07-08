package com.thealer.telehealer.apilayer.models.recents;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.UserBean;
import com.thealer.telehealer.apilayer.models.procedure.ProcedureModel;

import java.util.List;

/**
 * Created by Aswin on 23,April,2019
 */
public class VisitsDetailApiResponseModel extends BaseApiResponseModel {

    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {

        private String order_id;
        private String start_time;
        private String end_time;
        private String status;
        private int caller_id;
        private int callee_id;
        private Object doctor_id;
        private String type;
        private String category;
        private Object cost;
        private String instructions;
        private Object diagnosis;
        private String visit_summary;
        private ProcedureModel procedure;
        private String created_at;
        private String updated_at;
        private int transcription_id;
        private String transcript;
        private String audio_stream;
        private String transcription_status;
        private String updated_transcript;
        private String archive_id;
        private String audio_stream_screenshot;
        private int schedule_id;
        private UserBean patient;
        private UserBean doctor;
        private UserBean callee;
        private UserBean caller;
        private UserBean medical_assistant;
        private List<Integer> user_vitals;
        private List<Integer> user_diets;
        private List<Integer> user_files;
        private List<Integer> user_forms;
        private List<Integer> labs;
        private List<Integer> specialists;
        private List<Integer> xrays;
        private List<Integer> prescriptions;
        private List<Integer> miscellaneous;
        private List<Integer> referrals;

        public String getOrder_id() {
            return order_id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getCaller_id() {
            return caller_id;
        }

        public void setCaller_id(int caller_id) {
            this.caller_id = caller_id;
        }

        public int getCallee_id() {
            return callee_id;
        }

        public void setCallee_id(int callee_id) {
            this.callee_id = callee_id;
        }

        public Object getDoctor_id() {
            return doctor_id;
        }

        public void setDoctor_id(Object doctor_id) {
            this.doctor_id = doctor_id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public Object getCost() {
            return cost;
        }

        public void setCost(Object cost) {
            this.cost = cost;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public int getTranscription_id() {
            return transcription_id;
        }

        public void setTranscription_id(int transcription_id) {
            this.transcription_id = transcription_id;
        }

        public String getTranscript() {
            return transcript;
        }

        public void setTranscript(String transcript) {
            this.transcript = transcript;
        }

        public String getAudio_stream() {
            return audio_stream;
        }

        public void setAudio_stream(String audio_stream) {
            this.audio_stream = audio_stream;
        }

        public String getArchive_id() {
            return archive_id;
        }

        public void setArchive_id(String archive_id) {
            this.archive_id = archive_id;
        }

        public String getAudio_stream_screenshot() {
            return audio_stream_screenshot;
        }

        public void setAudio_stream_screenshot(String audio_stream_screenshot) {
            this.audio_stream_screenshot = audio_stream_screenshot;
        }

        public int getSchedule_id() {
            return schedule_id;
        }

        public void setSchedule_id(int schedule_id) {
            this.schedule_id = schedule_id;
        }

        public UserBean getPatient() {
            return patient;
        }

        public void setPatient(UserBean patient) {
            this.patient = patient;
        }

        public UserBean getDoctor() {
            return doctor;
        }

        public void setDoctor(UserBean doctor) {
            this.doctor = doctor;
        }

        public UserBean getMedical_assistant() {
            return medical_assistant;
        }

        public void setMedical_assistant(UserBean medical_assistant) {
            this.medical_assistant = medical_assistant;
        }

        public List<Integer> getUser_vitals() {
            return user_vitals;
        }

        public void setUser_vitals(List<Integer> user_vitals) {
            this.user_vitals = user_vitals;
        }

        public List<Integer> getUser_diets() {
            return user_diets;
        }

        public void setUser_diets(List<Integer> user_diets) {
            this.user_diets = user_diets;
        }

        public List<Integer> getUser_files() {
            return user_files;
        }

        public void setUser_files(List<Integer> user_files) {
            this.user_files = user_files;
        }

        public List<Integer> getUser_forms() {
            return user_forms;
        }

        public void setUser_forms(List<Integer> user_forms) {
            this.user_forms = user_forms;
        }

        public List<Integer> getLabs() {
            return labs;
        }

        public void setLabs(List<Integer> labs) {
            this.labs = labs;
        }

        public List<Integer> getSpecialists() {
            return specialists;
        }

        public void setSpecialists(List<Integer> specialists) {
            this.specialists = specialists;
        }

        public List<Integer> getXrays() {
            return xrays;
        }

        public void setXrays(List<Integer> xrays) {
            this.xrays = xrays;
        }

        public List<Integer> getPrescriptions() {
            return prescriptions;
        }

        public void setPrescriptions(List<Integer> prescriptions) {
            this.prescriptions = prescriptions;
        }

        public List<Integer> getMiscellaneous() {
            return miscellaneous;
        }

        public void setMiscellaneous(List<Integer> miscellaneous) {
            this.miscellaneous = miscellaneous;
        }

        public List<Integer> getReferrals() {
            return referrals;
        }

        public void setReferrals(List<Integer> referrals) {
            this.referrals = referrals;
        }

        public String getInstructions() {
            return instructions;
        }

        public void setInstructions(String instructions) {
            this.instructions = instructions;
        }

        public Object getDiagnosis() {
            return diagnosis;
        }

        public void setDiagnosis(Object diagnosis) {
            this.diagnosis = diagnosis;
        }

        public ProcedureModel getProcedure() {
            return procedure;
        }

        public void setProcedure(ProcedureModel procedure) {
            this.procedure = procedure;
        }

        public String getVisit_summary() {
            return visit_summary;
        }

        public void setVisit_summary(String visit_summary) {
            this.visit_summary = visit_summary;
        }

        public String getTranscription_status() {
            return transcription_status;
        }

        public void setTranscription_status(String transcription_status) {
            this.transcription_status = transcription_status;
        }

        public String getUpdated_transcript() {
            return updated_transcript;
        }

        public void setUpdated_transcript(String updated_transcript) {
            this.updated_transcript = updated_transcript;
        }

        public UserBean getCallee() {
            return callee;
        }

        public void setCallee(UserBean callee) {
            this.callee = callee;
        }

        public UserBean getCaller() {
            return caller;
        }

        public void setCaller(UserBean caller) {
            this.caller = caller;
        }
    }
}
