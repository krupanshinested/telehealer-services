package com.thealer.telehealer.views.settings.medicalHistory;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.medicalHistory.MedicalHistoryViewModel;
import com.thealer.telehealer.apilayer.models.medicalHistory.MedicationModel;
import com.thealer.telehealer.common.CustomSpinnerView;
import com.thealer.telehealer.common.Util.TimerInterface;
import com.thealer.telehealer.common.Util.TimerRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 21,January,2019
 */
public class MedicationListAdapter extends RecyclerView.Adapter<MedicationListAdapter.ViewHolder> {
    private FragmentActivity activity;
    private MedicalHistoryViewModel medicalHistoryViewModel;
    private List<MedicationModel> medicationModelList = new ArrayList<>();
    private String[] metrics, directionOne, directionTwo;

    public MedicationListAdapter(FragmentActivity activity) {
        this.activity = activity;
        medicalHistoryViewModel = new ViewModelProvider(activity).get(MedicalHistoryViewModel.class);
        medicationModelList = medicalHistoryViewModel.getMedicationListMutableLiveData().getValue();
        medicalHistoryViewModel.getMedicationListMutableLiveData().observe(activity,
                new Observer<List<MedicationModel>>() {
                    @Override
                    public void onChanged(@Nullable List<MedicationModel> medicationModels) {
                        if (medicationModels != null) {
                            medicationModelList = medicationModels;
                            notifyDataSetChanged();
                        }
                    }
                });

        metrics = MedicalHistoryConstants.metrics;
        directionOne = MedicalHistoryConstants.directionOne;
        directionTwo = MedicalHistoryConstants.directionTwo;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_medication_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        int metricPosition = 0, directionOnePosition = 0, directionTwoPosition = 0;

        if (medicationModelList.get(i).getUnit() != null) {
            for (int j = 0; j < metrics.length; j++) {
                if (medicationModelList.get(i).getUnit().equals(metrics[j])) {
                    metricPosition = j;
                    break;
                }
            }
        }

        if (medicationModelList.get(i).getDirectionType1() != null) {
            for (int j = 0; j < directionOne.length; j++) {
                if (medicationModelList.get(i).getDirectionType1().toLowerCase().trim().equals(directionOne[j].toLowerCase().trim())) {
                    directionOnePosition = j;
                    break;
                }
            }
        }

        if (medicationModelList.get(i).getDirectionType2() != null) {
            for (int j = 0; j < directionTwo.length; j++) {
                if (medicationModelList.get(i).getDirectionType2().toLowerCase().trim().equals(directionTwo[j].toLowerCase().trim())) {
                    directionTwoPosition = j;
                    break;
                }
            }
        }

        ArrayAdapter metricsAdapter = new ArrayAdapter(activity, android.R.layout.simple_spinner_item, metrics);
        metricsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewHolder.metricCsv.setSpinnerAdapter(metricsAdapter);
        viewHolder.metricCsv.getSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                medicalHistoryViewModel.getMedicationListMutableLiveData().getValue().get(i).setUnit(metrics[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter directionOneAdapter = new ArrayAdapter(activity, android.R.layout.simple_spinner_item, directionOne);
        directionOneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewHolder.direction1Csv.setSpinnerAdapter(directionOneAdapter);
        viewHolder.direction1Csv.getSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                medicalHistoryViewModel.getMedicationListMutableLiveData().getValue().get(i).setDirectionType1(directionOne[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter directionTwoAdapter = new ArrayAdapter(activity, android.R.layout.simple_spinner_item, directionTwo);
        directionTwoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewHolder.direction2Csv.setSpinnerAdapter(directionTwoAdapter);
        viewHolder.direction2Csv.getSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                medicalHistoryViewModel.getMedicationListMutableLiveData().getValue().get(i).setDirectionType2(directionTwo[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        viewHolder.drugNameEt.setText(medicationModelList.get(i).getDrugName());
        viewHolder.strengthEt.setText(medicationModelList.get(i).getStrength());
        viewHolder.directionEt.setText(medicationModelList.get(i).getDirection());

        viewHolder.drugNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (i < 0) {
                    return;
                }

                List<MedicationModel> model = medicalHistoryViewModel.getMedicationListMutableLiveData().getValue();
                if (model != null && model.size() > i) {
                    model.get(i).setDrugName(s.toString());
                }

                if (medicationModelList.size() > i) {
                    medicationModelList.get(i).setDrugName(s.toString());
                }

            }
        });

        viewHolder.strengthEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (medicalHistoryViewModel.getMedicationListMutableLiveData().getValue() != null &&
                        i >= 0 && medicalHistoryViewModel.getMedicationListMutableLiveData().getValue().size() > i) {
                    medicalHistoryViewModel.getMedicationListMutableLiveData().getValue().get(i).setStrength(s.toString());
                    medicationModelList.get(i).setStrength(s.toString());
                }
            }
        });

        viewHolder.directionEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                medicalHistoryViewModel.getMedicationListMutableLiveData().getValue().get(i).setDirection(s.toString());
                medicationModelList.get(i).setDirection(s.toString());
            }
        });

        viewHolder.metricCsv.getSpinner().setSelection(metricPosition);
        viewHolder.direction1Csv.getSpinner().setSelection(directionOnePosition);
        viewHolder.direction2Csv.getSpinner().setSelection(directionTwoPosition);

        viewHolder.deleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medicationModelList.remove(i);
                medicalHistoryViewModel.getMedicationListMutableLiveData().setValue(medicationModelList);
                notifyItemRemoved(i);
            }
        });

        viewHolder.drugNameEt.requestFocus();
    }

    @Override
    public int getItemCount() {
        return medicationModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextInputLayout drugNameTil;
        private EditText drugNameEt;
        private TextInputLayout strengthTil;
        private EditText strengthEt;
        private CustomSpinnerView metricCsv;
        private TextInputLayout directionTil;
        private EditText directionEt;
        private CustomSpinnerView direction1Csv;
        private CustomSpinnerView direction2Csv;
        private ImageView deleteIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            drugNameTil = (TextInputLayout) itemView.findViewById(R.id.drug_name_til);
            drugNameEt = (EditText) itemView.findViewById(R.id.drug_name_et);
            strengthTil = (TextInputLayout) itemView.findViewById(R.id.strength_til);
            strengthEt = (EditText) itemView.findViewById(R.id.strength_et);
            metricCsv = (CustomSpinnerView) itemView.findViewById(R.id.metric_csv);
            directionTil = (TextInputLayout) itemView.findViewById(R.id.direction_til);
            directionEt = (EditText) itemView.findViewById(R.id.direction_et);
            direction1Csv = (CustomSpinnerView) itemView.findViewById(R.id.direction_1_csv);
            direction2Csv = (CustomSpinnerView) itemView.findViewById(R.id.direction_2_csv);
            deleteIv = (ImageView) itemView.findViewById(R.id.delete_iv);
        }
    }
}
