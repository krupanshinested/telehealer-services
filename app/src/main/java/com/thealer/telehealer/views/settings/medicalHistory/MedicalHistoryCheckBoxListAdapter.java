package com.thealer.telehealer.views.settings.medicalHistory;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.PersonalHistoryModel;
import com.thealer.telehealer.apilayer.models.medicalHistory.FamilyHistoryModel;
import com.thealer.telehealer.apilayer.models.medicalHistory.HealthHabitModel;
import com.thealer.telehealer.apilayer.models.medicalHistory.MedicalHistoryCommonModel;
import com.thealer.telehealer.apilayer.models.medicalHistory.MedicalHistoryViewModel;
import com.thealer.telehealer.apilayer.models.medicalHistory.SexualHistoryModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.DatePickerDialogFragment;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.CustomDialogClickListener;
import com.thealer.telehealer.views.common.DateBroadcastReceiver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thealer.telehealer.views.settings.medicalHistory.MedicalHistoryConstants.MH_FAMILY_HISTORY;
import static com.thealer.telehealer.views.settings.medicalHistory.MedicalHistoryConstants.MH_HEALTH_HABITS;
import static com.thealer.telehealer.views.settings.medicalHistory.MedicalHistoryConstants.MH_PAST_MEDICAL_HISTORY;
import static com.thealer.telehealer.views.settings.medicalHistory.MedicalHistoryConstants.MH_PERSONAL_HISTORY;
import static com.thealer.telehealer.views.settings.medicalHistory.MedicalHistoryConstants.MH_RECENT_IMMUNIZATION;
import static com.thealer.telehealer.views.settings.medicalHistory.MedicalHistoryConstants.MH_SEXUAL_HISTORY;
import static com.thealer.telehealer.views.settings.medicalHistory.MedicalHistoryConstants.MH_SURGERIES;

/**
 * Created by Aswin on 23,January,2019
 */
public class MedicalHistoryCheckBoxListAdapter extends RecyclerView.Adapter<MedicalHistoryCheckBoxListAdapter.ViewHolder> {
    private FragmentActivity activity;
    private MedicalHistoryViewModel medicalHistoryViewModel;
    private List<String> stringList = new ArrayList<>();
    private Map<String, String> selectedItem = new HashMap<>();
    private String type;
    private DateBroadcastReceiver dateBroadcastReceiver = null;

    public MedicalHistoryCheckBoxListAdapter(FragmentActivity activity, String type) {
        this.activity = activity;
        medicalHistoryViewModel = ViewModelProviders.of(activity).get(MedicalHistoryViewModel.class);
        this.type = type;
        stringList.clear();
        switch (type) {
            case MH_PAST_MEDICAL_HISTORY:
                stringList = MedicalHistoryConstants.pastMedicalHistoryList;
                medicalHistoryViewModel.getPastMedicalHistoryMutableLiveData().observe(activity, new Observer<List<MedicalHistoryCommonModel>>() {
                    @Override
                    public void onChanged(@Nullable List<MedicalHistoryCommonModel> medicalHistoryCommonModels) {
                        updateSelectedItems(medicalHistoryCommonModels);
                        notifyDataSetChanged();
                    }
                });
                if (medicalHistoryViewModel.getPastMedicalHistoryMutableLiveData().getValue() != null) {
                    updateSelectedItems(medicalHistoryViewModel.getPastMedicalHistoryMutableLiveData().getValue());
                }
                break;
            case MH_SURGERIES:
                stringList = MedicalHistoryConstants.surgeriesList;
                medicalHistoryViewModel.getSurgeriesMutableLiveData().observe(activity, new Observer<List<MedicalHistoryCommonModel>>() {
                    @Override
                    public void onChanged(@Nullable List<MedicalHistoryCommonModel> medicalHistoryCommonModels) {
                        updateSelectedItems(medicalHistoryCommonModels);
                        notifyDataSetChanged();
                    }
                });
                break;
            case MH_FAMILY_HISTORY:
                stringList.addAll(MedicalHistoryConstants.familyHistoryList);
                medicalHistoryViewModel.getFamilyHistoryMutableLiveData().observe(activity, new Observer<List<FamilyHistoryModel>>() {
                    @Override
                    public void onChanged(@Nullable List<FamilyHistoryModel> familyHistoryModels) {
                        updateSelectedFamilyItems(familyHistoryModels);
                    }
                });
                break;
            case MH_PERSONAL_HISTORY:
                stringList.addAll(MedicalHistoryConstants.personalHistoryList);
                medicalHistoryViewModel.getPersonalHistoryMutableLiveData().observe(activity, new Observer<List<PersonalHistoryModel>>() {
                    @Override
                    public void onChanged(@Nullable List<PersonalHistoryModel> personalHistoryModels) {
                        updatePersonalHistoryList(personalHistoryModels);
                    }
                });
                break;
            case MH_HEALTH_HABITS:
                stringList.addAll(MedicalHistoryConstants.healthHabitList);
                medicalHistoryViewModel.getHealthHabitMutableLiveData().observe(activity, new Observer<List<HealthHabitModel>>() {
                    @Override
                    public void onChanged(@Nullable List<HealthHabitModel> healthHabitModels) {
                        updateHealthHabitList(healthHabitModels);
                    }
                });
                break;
            case MH_SEXUAL_HISTORY:
                stringList.addAll(MedicalHistoryConstants.sexualHistoryList);
                medicalHistoryViewModel.getSexualHistoryMutableLiveData().observe(activity, new Observer<List<SexualHistoryModel>>() {
                    @Override
                    public void onChanged(@Nullable List<SexualHistoryModel> sexualHistoryModels) {
                        updateSexualHistoryItems(sexualHistoryModels);
                    }
                });
                break;
            case MH_RECENT_IMMUNIZATION:
                stringList.addAll(MedicalHistoryConstants.recentImmunizationList);
                medicalHistoryViewModel.getRecentImmunizationMutableLiveData().observe(activity, new Observer<List<MedicalHistoryCommonModel>>() {
                    @Override
                    public void onChanged(@Nullable List<MedicalHistoryCommonModel> medicalHistoryCommonModels) {
                        updateSelectedItems(medicalHistoryCommonModels);
                    }
                });
                break;
        }
        Collections.sort(stringList);
    }

    private void updatePersonalHistoryList(List<PersonalHistoryModel> personalHistoryModels) {
        for (int i = 0; i < personalHistoryModels.size(); i++) {
            selectedItem.put(personalHistoryModels.get(i).getTitle(), personalHistoryModels.get(i).getDetailString());
        }
    }

    private void updateHealthHabitList(List<HealthHabitModel> healthHabitModels) {
        for (int i = 0; i < healthHabitModels.size(); i++) {
            selectedItem.put(healthHabitModels.get(i).getTitle(), healthHabitModels.get(i).getDetailString(activity));
        }
    }

    private void updateSexualHistoryItems(List<SexualHistoryModel> sexualHistoryModels) {
        for (int i = 0; i < sexualHistoryModels.size(); i++) {
            selectedItem.put(sexualHistoryModels.get(i).getTitle(), sexualHistoryModels.get(i).getDetailString(activity));
        }
    }

    private void updateSelectedFamilyItems(List<FamilyHistoryModel> familyHistoryModels) {
        for (int i = 0; i < familyHistoryModels.size(); i++) {
            String relations = "";

            if (!familyHistoryModels.get(i).getSelectedRelations().isEmpty()) {
                relations = familyHistoryModels.get(i).getSelectedRelationsString();
            }

            selectedItem.put(familyHistoryModels.get(i).getTitle(), relations);

            if (!stringList.contains(familyHistoryModels.get(i).getTitle())) {
                addListItem(familyHistoryModels.get(i).getTitle());
            }
        }
    }

    private void updateSelectedItems(List<MedicalHistoryCommonModel> medicalHistoryCommonModels) {
        for (int i = 0; i < medicalHistoryCommonModels.size(); i++) {
            selectedItem.put(medicalHistoryCommonModels.get(i).getTitle(), medicalHistoryCommonModels.get(i).getAdditionalInformation());

            if (!stringList.contains(medicalHistoryCommonModels.get(i).getTitle())) {
                addListItem(medicalHistoryCommonModels.get(i).getTitle());
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_medical_history_cb_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.itemCb.setText(stringList.get(i));

        viewHolder.itemCb.setOnCheckedChangeListener(null);

        if (selectedItem.containsKey(stringList.get(i))) {

            viewHolder.itemCb.setChecked(true);

            if (selectedItem.get(stringList.get(i)) != null && !selectedItem.get(stringList.get(i)).isEmpty()) {
                viewHolder.itemUserInputTv.setText(selectedItem.get(stringList.get(i)));
                viewHolder.itemUserInputTv.setVisibility(View.VISIBLE);
            } else {
                if (viewHolder.itemUserInputTv.getVisibility() == View.VISIBLE) {
                    viewHolder.itemUserInputTv.setVisibility(View.GONE);
                }
            }
        } else {
            viewHolder.itemCb.setChecked(false);
            if (viewHolder.itemUserInputTv.getVisibility() == View.VISIBLE) {
                viewHolder.itemUserInputTv.setVisibility(View.GONE);
            }
        }

        viewHolder.itemCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    addItem(i);
                } else {
                    removeItem(i);
                }
            }
        });
    }

    private void removeItem(int position) {
        if (selectedItem.containsKey(stringList.get(position))) {
            switch (type) {
                case MH_PAST_MEDICAL_HISTORY:
                    if (medicalHistoryViewModel.getPastMedicalHistoryMutableLiveData().getValue() != null) {
                        for (int i = 0; i < medicalHistoryViewModel.getPastMedicalHistoryMutableLiveData().getValue().size(); i++) {
                            if (medicalHistoryViewModel.getPastMedicalHistoryMutableLiveData().getValue().get(i).getTitle().equals(stringList.get(position))) {
                                medicalHistoryViewModel.getPastMedicalHistoryMutableLiveData().getValue().remove(i);
                                break;
                            }
                        }
                    }
                    break;
                case MH_SURGERIES:
                    if (medicalHistoryViewModel.getSurgeriesMutableLiveData().getValue() != null) {
                        for (int i = 0; i < medicalHistoryViewModel.getSurgeriesMutableLiveData().getValue().size(); i++) {
                            if (medicalHistoryViewModel.getSurgeriesMutableLiveData().getValue().get(i).getTitle().equals(stringList.get(position))) {
                                medicalHistoryViewModel.getSurgeriesMutableLiveData().getValue().remove(i);
                                break;
                            }
                        }
                    }
                    break;
                case MH_FAMILY_HISTORY:
                    if (medicalHistoryViewModel.getFamilyHistoryMutableLiveData().getValue() != null) {
                        for (int i = 0; i < medicalHistoryViewModel.getFamilyHistoryMutableLiveData().getValue().size(); i++) {
                            if (medicalHistoryViewModel.getFamilyHistoryMutableLiveData().getValue().get(i).getTitle().equals(stringList.get(position))) {
                                medicalHistoryViewModel.getFamilyHistoryMutableLiveData().getValue().remove(i);
                                break;
                            }
                        }
                    }
                    break;
                case MH_PERSONAL_HISTORY:
                    if (medicalHistoryViewModel.getPersonalHistoryMutableLiveData().getValue() != null) {
                        for (int i = 0; i < medicalHistoryViewModel.getPersonalHistoryMutableLiveData().getValue().size(); i++) {
                            if (medicalHistoryViewModel.getPersonalHistoryMutableLiveData().getValue().get(i).getTitle().equals(stringList.get(position))) {
                                medicalHistoryViewModel.getPersonalHistoryMutableLiveData().getValue().remove(i);
                                break;
                            }
                        }
                    }
                    break;
                case MH_HEALTH_HABITS:
                    if (medicalHistoryViewModel.getHealthHabitMutableLiveData().getValue() != null) {
                        for (int i = 0; i < medicalHistoryViewModel.getHealthHabitMutableLiveData().getValue().size(); i++) {
                            if (medicalHistoryViewModel.getHealthHabitMutableLiveData().getValue().get(i).getTitle().equals(stringList.get(position))) {
                                medicalHistoryViewModel.getHealthHabitMutableLiveData().getValue().remove(i);
                                break;
                            }
                        }
                    }

                    break;
                case MH_SEXUAL_HISTORY:
                    if (medicalHistoryViewModel.getSexualHistoryMutableLiveData().getValue() != null) {
                        for (int i = 0; i < medicalHistoryViewModel.getSexualHistoryMutableLiveData().getValue().size(); i++) {
                            if (medicalHistoryViewModel.getSexualHistoryMutableLiveData().getValue().get(i).getTitle().equals(stringList.get(position))) {
                                medicalHistoryViewModel.getSexualHistoryMutableLiveData().getValue().remove(i);
                                break;
                            }
                        }
                    }
                    break;
                case MH_RECENT_IMMUNIZATION:
                    if (medicalHistoryViewModel.getRecentImmunizationMutableLiveData().getValue() != null) {
                        for (int i = 0; i < medicalHistoryViewModel.getRecentImmunizationMutableLiveData().getValue().size(); i++) {
                            if (medicalHistoryViewModel.getRecentImmunizationMutableLiveData().getValue().get(i).getTitle().equals(stringList.get(position))) {
                                medicalHistoryViewModel.getRecentImmunizationMutableLiveData().getValue().remove(i);
                                break;
                            }
                        }
                    }
                    break;
            }

            selectedItem.remove(stringList.get(position));
            notifyItemChanged(position);

        }
    }

    private void addItem(int position) {
        switch (type) {
            case MH_PAST_MEDICAL_HISTORY:
                if (MedicalHistoryConstants.userInputRequiredList.contains(stringList.get(position))) {
                    Utils.showUserInputDialog(activity,
                            stringList.get(position),
                            activity.getString(R.string.please_provide_the_details),
                            activity.getString(R.string.specify),
                            activity.getString(R.string.Done),
                            activity.getString(R.string.Cancel),
                            new CustomDialogClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, String inputText) {

                                    addData(stringList.get(position), inputText);
                                    notifyItemChanged(position);
                                    dialog.dismiss();
                                }
                            },
                            new CustomDialogClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, String inputText) {
                                    notifyItemChanged(position);
                                    dialog.dismiss();
                                }
                            });
                } else {
                    addData(stringList.get(position), "");
                }
                break;
            case MH_SURGERIES:
                addData(stringList.get(position), "");
                break;
            case MH_FAMILY_HISTORY:

                if (!medicalHistoryViewModel.getOnActionLiveData().hasActiveObservers()) {

                    medicalHistoryViewModel.getOnActionLiveData().observe(activity, new Observer<Boolean>() {
                        @Override
                        public void onChanged(@Nullable Boolean isDone) {

                            if (isDone) {

                                if (!medicalHistoryViewModel.isDeselected()) {
                                    String relations = "";

                                    if (medicalHistoryViewModel.getSelectedRelationsLiveData().getValue() != null &&
                                            !medicalHistoryViewModel.getSelectedRelationsLiveData().getValue().isEmpty()) {
                                        relations = medicalHistoryViewModel.getSelectedRelationsLiveData().getValue().toString().replace("[", "").replace("]", "");
                                    }

                                    addFamilyHistoryData(stringList.get(position), medicalHistoryViewModel.getSelectedRelationsLiveData().getValue(), relations);

                                } else {
                                    medicalHistoryViewModel.getOnActionLiveData().setValue(false);
                                    medicalHistoryViewModel.getOnActionLiveData().removeObservers(activity);
                                }
                                notifyItemChanged(position);
                            }
                        }
                    });
                }

                SelectRelationBottomSheetDialogFragment selectRelationBottomSheetDialogFragment = new SelectRelationBottomSheetDialogFragment();

                selectRelationBottomSheetDialogFragment.setCancelable(false);

                Bundle bundle = new Bundle();
                bundle.putString(ArgumentKeys.TITLE, stringList.get(position));

                selectRelationBottomSheetDialogFragment.setArguments(bundle);

                medicalHistoryViewModel.getSelectedRelationsLiveData().setValue(new ArrayList<>());

                selectRelationBottomSheetDialogFragment.show(activity.getSupportFragmentManager(), selectRelationBottomSheetDialogFragment.getClass().getSimpleName());

                break;
            case MH_PERSONAL_HISTORY:

                if (MedicalHistoryConstants.optionsRequired.contains(stringList.get(position))) {

                    if (!medicalHistoryViewModel.getOnActionLiveData().hasActiveObservers()) {

                        medicalHistoryViewModel.getOnActionLiveData().observe(activity, new Observer<Boolean>() {
                            @Override
                            public void onChanged(@Nullable Boolean isDone) {
                                if (isDone) {

                                    if (medicalHistoryViewModel.getSelectedOption() != null) {
                                        if (MedicalHistoryConstants.userInputRequiredList.contains(stringList.get(position))) {

                                            getUserInput(stringList.get(position), medicalHistoryViewModel.getSelectedOption(), position);

                                        } else {

                                            addPersonalHistoryData(stringList.get(position), null, medicalHistoryViewModel.getSelectedOption());

                                            notifyItemChanged(position);
                                        }
                                    }
                                }
                            }
                        });
                    }
                    showMHOptionSelectionDialog(stringList.get(position), MedicalHistoryConstants.getListType(stringList.get(position)));

                } else {

                    getUserInput(stringList.get(position), null, position);

                }

                break;
            case MH_HEALTH_HABITS:

                if (!medicalHistoryViewModel.getOnActionLiveData().hasActiveObservers()) {
                    medicalHistoryViewModel.getOnActionLiveData().observe(activity, new Observer<Boolean>() {
                        @Override
                        public void onChanged(@Nullable Boolean isDone) {

                            if (isDone) {

                                if (medicalHistoryViewModel.getSelectedOption() != null) {

                                    if (medicalHistoryViewModel.getSelectedOption().equals(activity.getString(R.string.no))) {
                                        addHealthHabitData(stringList.get(position), medicalHistoryViewModel.getSelectedOption(), null);
                                        notifyItemChanged(position);
                                    } else {
                                        CustomDialogClickListener cancelListener = new CustomDialogClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, String inputText) {
                                                medicalHistoryViewModel.setSelectedOption(null);
                                                medicalHistoryViewModel.getOnActionLiveData().removeObservers(activity);
                                                notifyItemChanged(position);
                                                dialog.dismiss();
                                            }
                                        };

                                        if (stringList.get(position).equals(MedicalHistoryConstants.DO_YOU_DRINK)) {

                                            Utils.showUserMultiInputDialog(activity, MedicalHistoryConstants.MH_HEALTH_HABITS, activity.getString(R.string.please_provide_the_details), MedicalHistoryConstants.drink_questions, activity.getString(R.string.Done), activity.getString(R.string.Cancel),
                                                    new CustomDialogClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, String inputText) {
                                                            String[] answers = inputText.split(",");

                                                            HealthHabitModel.DetailBean detailBean = null;

                                                            if (!Arrays.toString(answers).isEmpty()) {
                                                                detailBean = new HealthHabitModel.DetailBean();


                                                                if (answers[0] != null && !answers[0].trim().isEmpty()) {
                                                                    detailBean.setAlcohol_Often(answers[0]);
                                                                }
                                                                if (answers[1] != null && !answers[1].trim().isEmpty()) {
                                                                    detailBean.setAlcohol_Quantity(answers[1]);
                                                                }
                                                                if (answers[2] != null && !answers[2].trim().isEmpty()) {
                                                                    detailBean.setAlcohol_Felt_Cut_Down_Drinking(answers[2]);
                                                                }
                                                            }
                                                            addHealthHabitData(stringList.get(position), medicalHistoryViewModel.getSelectedOption(), detailBean);
                                                            notifyItemChanged(position);
                                                            dialog.dismiss();

                                                        }
                                                    }, cancelListener);
                                        } else if (stringList.get(position).equals(MedicalHistoryConstants.DO_YOU_SMOKE)) {

                                            Utils.showUserMultiInputDialog(activity, MedicalHistoryConstants.MH_HEALTH_HABITS, activity.getString(R.string.please_provide_the_details), MedicalHistoryConstants.smoke_questions, activity.getString(R.string.Done), activity.getString(R.string.Cancel),
                                                    new CustomDialogClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, String inputText) {
                                                            String[] answers = inputText.split(",");

                                                            HealthHabitModel.DetailBean detailBean = null;

                                                            if (!Arrays.toString(answers).isEmpty()) {
                                                                detailBean = new HealthHabitModel.DetailBean();


                                                                if (answers[0] != null && !answers[0].trim().isEmpty()) {
                                                                    detailBean.setCigarettes_Per_Day(answers[0]);
                                                                }
                                                                if (answers[1] != null && !answers[1].trim().isEmpty()) {
                                                                    detailBean.setCigarettes_For_How_Many_Years(answers[1]);
                                                                }
                                                                if (answers[2] != null && !answers[2].trim().isEmpty()) {
                                                                    detailBean.setOtherFormsOfTobacco(answers[2]);
                                                                }
                                                            }
                                                            addHealthHabitData(stringList.get(position), medicalHistoryViewModel.getSelectedOption(), detailBean);
                                                            notifyItemChanged(position);
                                                            dialog.dismiss();

                                                        }
                                                    }, cancelListener);
                                        } else if (stringList.get(position).equals(MedicalHistoryConstants.USED_OTHER_DRUGS)) {

                                            Utils.showUserMultiInputDialog(activity, MedicalHistoryConstants.MH_HEALTH_HABITS, activity.getString(R.string.please_provide_the_details), MedicalHistoryConstants.drugs_questions, activity.getString(R.string.Done), activity.getString(R.string.Cancel),
                                                    new CustomDialogClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, String inputText) {
                                                            String[] answers = inputText.split(",");

                                                            HealthHabitModel.DetailBean detailBean = null;

                                                            if (!Arrays.toString(answers).isEmpty()) {
                                                                detailBean = new HealthHabitModel.DetailBean();

                                                                if (answers[0] != null && !answers[0].trim().isEmpty()) {
                                                                    detailBean.setStill_Using_Drug(answers[0]);
                                                                }
                                                            }
                                                            addHealthHabitData(stringList.get(position), medicalHistoryViewModel.getSelectedOption(), detailBean);
                                                            notifyItemChanged(position);
                                                            dialog.dismiss();

                                                        }
                                                    }, cancelListener);
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
                showMHOptionSelectionDialog(stringList.get(position), MedicalHistoryConstants.LIST_YES_NO_BOTH);

                break;
            case MH_SEXUAL_HISTORY:

                if (!medicalHistoryViewModel.getOnActionLiveData().hasActiveObservers()) {

                    medicalHistoryViewModel.getOnActionLiveData().observe(activity, new Observer<Boolean>() {
                        @Override
                        public void onChanged(@Nullable Boolean isDone) {

                            SexualHistoryModel.DetailBean detailBean = null;

                            if (isDone) {
                                if (medicalHistoryViewModel.getSelectedOption() != null) {

                                    if (medicalHistoryViewModel.getSelectedOption().equals(activity.getString(R.string.yes))) {

                                        if (stringList.get(position).equals(MedicalHistoryConstants.SEXUALLY_ACTIVE)) {

                                            showMHOptionSelectionDialog(activity.getString(R.string.sexually_active_with), MedicalHistoryConstants.LIST_SEXUALLY_ACTIVE_WITH);

                                        } else if (stringList.get(position).equals(MedicalHistoryConstants.MENSURAL_PERIODS)) {

                                            Utils.showUserMultiInputDialog(activity, MedicalHistoryConstants.MH_SEXUAL_HISTORY, activity.getString(R.string.please_provide_the_details), MedicalHistoryConstants.mensural_questions, activity.getString(R.string.Done), activity.getString(R.string.Cancel),
                                                    new CustomDialogClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, String inputText) {
                                                            String[] answers = inputText.split(",");

                                                            SexualHistoryModel.DetailBean detailBean = null;
                                                            if (!Arrays.toString(answers).isEmpty()) {

                                                                detailBean = new SexualHistoryModel.DetailBean();

                                                                if (answers[0] != null && !answers[0].trim().isEmpty()) {
                                                                    detailBean.setPeriodsStoppedAt(answers[0]);
                                                                }
                                                                if (answers[1] != null && !answers[1].trim().isEmpty()) {
                                                                    detailBean.setPeriodsRegular(answers[1]);
                                                                }
                                                            }
                                                            addSexualHistory(stringList.get(position), activity.getString(R.string.yes), detailBean);
                                                            notifyItemChanged(position);
                                                            dialog.dismiss();
                                                        }
                                                    },
                                                    new CustomDialogClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, String inputText) {
                                                            dialog.dismiss();
                                                            medicalHistoryViewModel.setSelectedOption(null);
                                                            medicalHistoryViewModel.getOnActionLiveData().removeObservers(activity);
                                                        }
                                                    });

                                        } else if (stringList.get(position).equals(MedicalHistoryConstants.EVER_BEEN_PREGNANT)) {

                                            Utils.showUserMultiInputDialog(activity, MedicalHistoryConstants.MH_SEXUAL_HISTORY, activity.getString(R.string.please_provide_the_details), MedicalHistoryConstants.pregnant_questions, activity.getString(R.string.Done), activity.getString(R.string.Cancel),
                                                    new CustomDialogClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, String inputText) {

                                                            String[] answers = inputText.split(",");

                                                            SexualHistoryModel.DetailBean detailBean = null;

                                                            if (!Arrays.toString(answers).isEmpty()) {

                                                                detailBean = new SexualHistoryModel.DetailBean();

                                                                if (answers[0] != null && !answers[0].trim().isEmpty()) {
                                                                    detailBean.setPregnant_Times(answers[0]);
                                                                }
                                                                if (answers[1] != null && !answers[1].trim().isEmpty()) {
                                                                    detailBean.setMiscarriages_Count(answers[1]);
                                                                }
                                                                if (answers[2] != null && !answers[2].trim().isEmpty()) {
                                                                    detailBean.setAbortions_Count(answers[2]);
                                                                }
                                                                if (answers[3] != null && !answers[3].trim().isEmpty()) {
                                                                    detailBean.setChildren_Count(answers[3]);
                                                                }

                                                            }
                                                            addSexualHistory(stringList.get(position), activity.getString(R.string.yes), detailBean);
                                                            notifyItemChanged(position);
                                                            dialog.dismiss();
                                                        }
                                                    },
                                                    new CustomDialogClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, String inputText) {
                                                            medicalHistoryViewModel.setSelectedOption(null);
                                                            medicalHistoryViewModel.getOnActionLiveData().removeObservers(activity);
                                                            dialog.dismiss();
                                                        }
                                                    });

                                        }
                                    } else if (medicalHistoryViewModel.getSelectedOption().equals(activity.getString(R.string.no))) {

                                        addSexualHistory(stringList.get(position), activity.getString(R.string.no), detailBean);

                                    } else {

                                        if (stringList.get(position).equals(MedicalHistoryConstants.SEXUALLY_ACTIVE)) {

                                            detailBean = new SexualHistoryModel.DetailBean(medicalHistoryViewModel.getSelectedOption());

                                            addSexualHistory(stringList.get(position), activity.getString(R.string.yes), detailBean);

                                        }

                                    }

                                    notifyItemChanged(position);
                                }
                            }
                        }
                    });
                }
                showMHOptionSelectionDialog(stringList.get(position), MedicalHistoryConstants.LIST_YES_OR_NO);

                break;
            case MH_RECENT_IMMUNIZATION:
                dateBroadcastReceiver = new DateBroadcastReceiver() {
                    @Override
                    public void onDateReceived(String formatedDate) {
                        if (dateBroadcastReceiver != null) {
                            addData(stringList.get(position), formatedDate);
                            notifyItemChanged(position);
                            LocalBroadcastManager.getInstance(activity).unregisterReceiver(dateBroadcastReceiver);
                        }
                    }

                    @Override
                    public void onCancelled() {
                        notifyItemChanged(position);
                        LocalBroadcastManager.getInstance(activity).unregisterReceiver(dateBroadcastReceiver);
                    }
                };

                LocalBroadcastManager.getInstance(activity).registerReceiver(dateBroadcastReceiver, new IntentFilter(Constants.DATE_PICKER_INTENT));

                DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();

                Bundle datePickerBundle = new Bundle();
                datePickerBundle.putInt(Constants.DATE_PICKER_TYPE, Constants.TILL_CURRENT_DAY);
                datePickerDialogFragment.setArguments(datePickerBundle);

                datePickerDialogFragment.show(activity.getSupportFragmentManager(), datePickerDialogFragment.getClass().getSimpleName());
                break;
        }
    }

    private void getUserInput(@NonNull String title, @Nullable String selectedOption, int position) {

        CustomDialogClickListener cancelListener = new CustomDialogClickListener() {
            @Override
            public void onClick(DialogInterface dialog, String inputText) {
                medicalHistoryViewModel.setSelectedOption(null);
                medicalHistoryViewModel.getOnActionLiveData().removeObservers(activity);
                notifyItemChanged(position);
                dialog.dismiss();
            }
        };

        if (title.equals(MedicalHistoryConstants.LEVEL_OF_EDUCATION)) {
            Utils.showUserMultiInputDialog(activity, title, activity.getString(R.string.please_provide_the_details), Collections.singletonList(MedicalHistoryConstants.getPersonalHistoryInputHint(title)), activity.getString(R.string.Done), activity.getString(R.string.Cancel),
                    new CustomDialogClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, String inputText) {
                            addPersonalHistoryData(title, inputText, selectedOption);
                            notifyItemChanged(position);
                            dialog.dismiss();
                        }
                    }, cancelListener);
        } else {
            Utils.showUserInputDialog(activity, title, activity.getString(R.string.please_provide_the_details), MedicalHistoryConstants.getPersonalHistoryInputHint(title), activity.getString(R.string.Done), activity.getString(R.string.Cancel),
                    new CustomDialogClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, String inputText) {
                            addPersonalHistoryData(title, inputText, selectedOption);
                            notifyItemChanged(position);
                            dialog.dismiss();
                        }
                    }, cancelListener);
        }
    }

    private void addPersonalHistoryData(@NonNull String title, @Nullable String additionalInfo, @Nullable String selectedOption) {
        PersonalHistoryModel personalHistoryModel = new PersonalHistoryModel();
        personalHistoryModel.setTitle(title);
        personalHistoryModel.setAdditionalInformation(additionalInfo);
        personalHistoryModel.setSelectedOption(selectedOption);

        selectedItem.put(title, personalHistoryModel.getDetailString());

        medicalHistoryViewModel.getPersonalHistoryMutableLiveData().getValue().add(personalHistoryModel);

        medicalHistoryViewModel.setSelectedOption(null);
        medicalHistoryViewModel.getOnActionLiveData().removeObservers(activity);
    }

    private void addHealthHabitData(@NonNull String title, @NonNull String additionalInfo, @Nullable HealthHabitModel.DetailBean detailBean) {

        HealthHabitModel healthHabitModel = new HealthHabitModel();
        healthHabitModel.setTitle(title);
        healthHabitModel.setAdditionalInformation(additionalInfo);
        healthHabitModel.setDetail(detailBean);

        selectedItem.put(title, healthHabitModel.getDetailString(activity));

        medicalHistoryViewModel.getHealthHabitMutableLiveData().getValue().add(healthHabitModel);

        medicalHistoryViewModel.setSelectedOption(null);
        medicalHistoryViewModel.getOnActionLiveData().removeObservers(activity);
    }

    private void addSexualHistory(@NonNull String title, @NonNull String additionalInfo, @Nullable SexualHistoryModel.DetailBean detailBean) {
        SexualHistoryModel sexualHistoryModel = new SexualHistoryModel();
        sexualHistoryModel.setTitle(title);
        sexualHistoryModel.setAdditionalInformation(additionalInfo);
        sexualHistoryModel.setDetail(detailBean);

        selectedItem.put(title, sexualHistoryModel.getDetailString(activity));

        medicalHistoryViewModel.getSexualHistoryMutableLiveData().getValue().add(sexualHistoryModel);

        medicalHistoryViewModel.setSelectedOption(null);
        medicalHistoryViewModel.getOnActionLiveData().removeObservers(activity);
    }

    private void showMHOptionSelectionDialog(String title, int type) {
        MedicalHistoryOptionsSelecteDialogFragment optionsSelecteDialogFragment = new MedicalHistoryOptionsSelecteDialogFragment();
        Bundle optionsBundle = new Bundle();
        optionsBundle.putString(ArgumentKeys.TITLE, title);
        optionsBundle.putInt(ArgumentKeys.LIST, type);
        optionsSelecteDialogFragment.setArguments(optionsBundle);
        optionsSelecteDialogFragment.show(activity.getSupportFragmentManager(), optionsSelecteDialogFragment.getClass().getSimpleName());

    }

    private void addFamilyHistoryData(String title, List<String> selectedRelations, String relations) {

        selectedItem.put(title, relations);

        List<String> relationsList = new ArrayList<>(selectedRelations);

        FamilyHistoryModel familyHistoryModel = new FamilyHistoryModel();
        familyHistoryModel.setTitle(title);
        familyHistoryModel.setSelectedRelations(relationsList);

        medicalHistoryViewModel.getFamilyHistoryMutableLiveData().getValue().add(familyHistoryModel);

        medicalHistoryViewModel.getOnActionLiveData().setValue(false);

        medicalHistoryViewModel.getOnActionLiveData().removeObservers(activity);

    }

    private void addData(@NonNull String title, @NonNull String additionalInfo) {
        selectedItem.put(title, additionalInfo);
        MedicalHistoryCommonModel medicalHistoryCommonModel = new MedicalHistoryCommonModel();
        medicalHistoryCommonModel.setTitle(title);
        medicalHistoryCommonModel.setAdditionalInformation(additionalInfo);

        switch (type) {
            case MH_PAST_MEDICAL_HISTORY:
                medicalHistoryViewModel.getPastMedicalHistoryMutableLiveData().getValue().add(medicalHistoryCommonModel);
                break;
            case MH_SURGERIES:
                medicalHistoryViewModel.getSurgeriesMutableLiveData().getValue().add(medicalHistoryCommonModel);
                break;
            case MH_RECENT_IMMUNIZATION:
                medicalHistoryViewModel.getRecentImmunizationMutableLiveData().getValue().add(medicalHistoryCommonModel);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public void addListItem(String item) {
        stringList.add(item);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox itemCb;
        private TextView itemUserInputTv;
        private View bottomView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCb = (CheckBox) itemView.findViewById(R.id.item_cb);
            itemUserInputTv = (TextView) itemView.findViewById(R.id.item_user_input_tv);
            bottomView = (View) itemView.findViewById(R.id.bottom_view);
        }
    }

}
