package com.thealer.telehealer.apilayer.models.recents;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

/**
 * Created by Aswin on 25,December,2018
 */
public class TranscriptionApiResponseModel extends BaseApiResponseModel {

    public final String STATUS_READY = "READY";

    private int transcription_id;
    private String status;
    private String order_id;
    private String order_start_time;
    private String order_end_time;
    private int caller_id;
    private int callee_id;
    private Object doctor_id;
    private String type;
    private String category;
    private String transcript;
    private String audio_stream;
    private String archive_id;
    private Object tuser;
    private RecentsUserDetailModel patient;
    private RecentsUserDetailModel doctor;
    private RecentsUserDetailModel medical_assistant;
    private RecentsUserDetailModel callee;
    private RecentsUserDetailModel caller;

    public int getTranscription_id() {
        return transcription_id;
    }

    public void setTranscription_id(int transcription_id) {
        this.transcription_id = transcription_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_start_time() {
        return order_start_time;
    }

    public void setOrder_start_time(String order_start_time) {
        this.order_start_time = order_start_time;
    }

    public String getOrder_end_time() {
        return order_end_time;
    }

    public void setOrder_end_time(String order_end_time) {
        this.order_end_time = order_end_time;
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

    public Object getTuser() {
        return tuser;
    }

    public void setTuser(Object tuser) {
        this.tuser = tuser;
    }

    public RecentsUserDetailModel getPatient() {
        return patient;
    }

    public void setPatient(RecentsUserDetailModel patient) {
        this.patient = patient;
    }

    public RecentsUserDetailModel getDoctor() {
        return doctor;
    }

    public void setDoctor(RecentsUserDetailModel doctor) {
        this.doctor = doctor;
    }

    public RecentsUserDetailModel getMedical_assistant() {
        return medical_assistant;
    }

    public void setMedical_assistant(RecentsUserDetailModel medical_assistant) {
        this.medical_assistant = medical_assistant;
    }

    public RecentsUserDetailModel getCallee() {
        return callee;
    }

    public void setCallee(RecentsUserDetailModel callee) {
        this.callee = callee;
    }

    public RecentsUserDetailModel getCaller() {
        return caller;
    }

    public void setCaller(RecentsUserDetailModel caller) {
        this.caller = caller;
    }
}
