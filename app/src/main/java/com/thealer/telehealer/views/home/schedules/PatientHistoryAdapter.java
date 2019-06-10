package com.thealer.telehealer.views.home.schedules;

import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.HistoryBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Aswin on 19,December,2018
 */
public class PatientHistoryAdapter extends RecyclerView.Adapter<PatientHistoryAdapter.ViewHolder> {

    private FragmentActivity activity;
    private List<String> questions = new ArrayList<>(Arrays.asList("Has there been any changes or diagnosis in your medical health from any other physician?",
            "Have you had any new surgical procedures since your last visit?",
            "Have you developed any drug or food allergies since your last visit?",
            "Have you had started any new medications or supplements since your last visit?",
            "Has there been any new major medical illness in your genetically related family?",
            "Has there been any change in your tobacco or alcohol drug history since your last visit?",
            "Is there any change in your social status?",
            "Have you had any immunizations since your last visit?",
            "Are there any new medical providers since your last visit?"));
    private List<HistoryBean> patientHistory;
    private CreateScheduleViewModel createScheduleViewModel;
    private boolean isEditmode = true;

    public PatientHistoryAdapter(FragmentActivity activity) {
        this.activity = activity;
        createScheduleViewModel = ViewModelProviders.of(activity).get(CreateScheduleViewModel.class);
        if (createScheduleViewModel.getPatientHistory() == null) {
            createScheduleViewModel.setPatientHistory(new ArrayList<>());
            for (int i = 0; i < questions.size(); i++) {
                HistoryBean historyBean = new HistoryBean();
                historyBean.setQuestion(questions.get(i));
                historyBean.setIsYes(false);
                historyBean.setReason("");
                createScheduleViewModel.getPatientHistory().add(historyBean);
            }
        }
        patientHistory = createScheduleViewModel.getPatientHistory();
    }

    public PatientHistoryAdapter(FragmentActivity activity, boolean isEditMode, List<HistoryBean> historyList) {
        this.activity = activity;
        this.patientHistory = historyList;
        this.isEditmode = isEditMode;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_patient_history, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.itemSwitch.setText(questions.get(i));

        viewHolder.itemSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    viewHolder.commentsEt.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.commentsEt.setVisibility(View.GONE);
                    createScheduleViewModel.getPatientHistory().get(i).setReason("");
                    viewHolder.commentsEt.setText(null);
                }

                if (createScheduleViewModel != null) {
                    createScheduleViewModel.getPatientHistory().get(i).setIsYes(isChecked);
                }
            }
        });

        viewHolder.commentsEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (createScheduleViewModel != null) {
                    createScheduleViewModel.getPatientHistory().get(i).setReason(s.toString());
                }
            }
        });

        if (patientHistory != null && patientHistory.size() >= questions.size()) {
            viewHolder.itemSwitch.setChecked(patientHistory.get(i).isIsYes());
            if (patientHistory.get(i).getReason() != null && !patientHistory.get(i).getReason().isEmpty()) {
                viewHolder.commentsEt.setText(patientHistory.get(i).getReason());
            }
        }

        viewHolder.itemSwitch.setClickable(isEditmode);
        viewHolder.commentsEt.setEnabled(isEditmode);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox itemSwitch;
        private EditText commentsEt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemSwitch = (CheckBox) itemView.findViewById(R.id.item_switch);
            commentsEt = (EditText) itemView.findViewById(R.id.comments_et);
        }
    }
}
