package com.thealer.telehealer.views.settings.medicalHistory;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.PersonalHistoryModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.QuestionnaireBean;
import com.thealer.telehealer.apilayer.models.medicalHistory.FamilyHistoryModel;
import com.thealer.telehealer.apilayer.models.medicalHistory.HealthHabitModel;
import com.thealer.telehealer.apilayer.models.medicalHistory.MedicalHistoryApiViewModel;
import com.thealer.telehealer.apilayer.models.medicalHistory.MedicalHistoryCommonModel;
import com.thealer.telehealer.apilayer.models.medicalHistory.MedicalHistoryViewModel;
import com.thealer.telehealer.apilayer.models.medicalHistory.MedicationModel;
import com.thealer.telehealer.apilayer.models.medicalHistory.SexualHistoryModel;
import com.thealer.telehealer.apilayer.models.medicalHistory.UpdateQuestionaryBodyModel;
import com.thealer.telehealer.apilayer.models.settings.AppointmentSlotUpdate;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.CustomDialogClickListener;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.orders.CreateOrderActivity;
import com.thealer.telehealer.views.home.orders.OrderConstant;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import java.util.ArrayList;
import java.util.List;

import static com.thealer.telehealer.views.settings.medicalHistory.MedicalHistoryConstants.MH_FAMILY_HISTORY;
import static com.thealer.telehealer.views.settings.medicalHistory.MedicalHistoryConstants.MH_HEALTH_HABITS;
import static com.thealer.telehealer.views.settings.medicalHistory.MedicalHistoryConstants.MH_MEDICATION;
import static com.thealer.telehealer.views.settings.medicalHistory.MedicalHistoryConstants.MH_PAST_MEDICAL_HISTORY;
import static com.thealer.telehealer.views.settings.medicalHistory.MedicalHistoryConstants.MH_PERSONAL_HISTORY;
import static com.thealer.telehealer.views.settings.medicalHistory.MedicalHistoryConstants.MH_RECENT_IMMUNIZATION;
import static com.thealer.telehealer.views.settings.medicalHistory.MedicalHistoryConstants.MH_SEXUAL_HISTORY;
import static com.thealer.telehealer.views.settings.medicalHistory.MedicalHistoryConstants.MH_SURGERIES;

/**
 * Created by Aswin on 21,January,2019
 */
public class MedicalHistoryEditFragment extends BaseFragment implements DoCurrentTransactionInterface {
    private TextView viewTitle;
    private RecyclerView recyclerView;
    private TextView addItemTv;
    private Button updateBtn;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private LinearLayout othersLl;
    private TextView otherLabelTv;
    private EditText otherEt;

    private WhoAmIApiResponseModel whoAmIApiResponseModel;
    private CommonUserApiResponseModel commonUserApiResponseModel;
    private String type;
    private MedicalHistoryViewModel medicalHistoryViewModel;
    private OnCloseActionInterface onCloseActionInterface;
    private AttachObserverInterface attachObserverInterface;
    private MedicalHistoryCheckBoxListAdapter checkBoxListAdapter;
    private OnViewChangeInterface onViewChangeInterface;
    private ShowSubFragmentInterface showSubFragmentInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        medicalHistoryViewModel = ViewModelProviders.of(getActivity()).get(MedicalHistoryViewModel.class);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medical_history_edit, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        viewTitle = (TextView) view.findViewById(R.id.view_title);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        addItemTv = (TextView) view.findViewById(R.id.add_item_tv);
        updateBtn = (Button) view.findViewById(R.id.update_btn);
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        othersLl = (LinearLayout) view.findViewById(R.id.others_ll);
        otherLabelTv = (TextView) view.findViewById(R.id.other_label_tv);
        otherEt = (EditText) view.findViewById(R.id.other_et);

        otherEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                medicalHistoryViewModel.setOtherInformation(s.toString());
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserHistory();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (getArguments() != null) {
            type = getArguments().getString(ArgumentKeys.HISTORY_TYPE);

            if (UserType.isUserPatient()) {
                whoAmIApiResponseModel = UserDetailPreferenceManager.getWhoAmIResponse();
            } else {
                commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
            }

            if (commonUserApiResponseModel != null) {
                backIv = (ImageView) view.findViewById(R.id.back_iv);
                toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
                backIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCloseActionInterface.onClose(false);
                    }
                });

                toolbarTitle.setText(type);

                toolbar.setVisibility(View.VISIBLE);
            } else {
                toolbar.setVisibility(View.GONE);
            }

            if (type != null) {
                viewTitle.setText(MedicalHistoryConstants.getTitle(type));
                medicalHistoryViewModel.setOtherInformation(null);
                switch (type) {
                    case MH_MEDICATION:
                        showMedications();
                        break;
                    case MH_PAST_MEDICAL_HISTORY:
                        showPastMedicalHistory();
                        break;
                    case MH_SURGERIES:
                        showSurgeries();
                        break;
                    case MH_FAMILY_HISTORY:
                        showFamilyList();
                        break;
                    case MH_PERSONAL_HISTORY:
                        showPersonalHistory();
                        break;
                    case MH_HEALTH_HABITS:
                        showHealthHabits();
                        break;
                    case MH_SEXUAL_HISTORY:
                        showSexualHistory();
                        break;
                    case MH_RECENT_IMMUNIZATION:
                        showRecentImmunization();
                        break;
                }
            }

        }
    }

    private void showPersonalHistory() {

        List<PersonalHistoryModel> personalHistoryModelList = new ArrayList<>();

        if (UserType.isUserPatient()) {
            if (whoAmIApiResponseModel.getQuestionnaire() != null) {
                if (whoAmIApiResponseModel.getQuestionnaire().getPersonalHistoryBean() != null) {
                    if (whoAmIApiResponseModel.getQuestionnaire().getPersonalHistoryBean().getItems() != null) {
                        personalHistoryModelList = whoAmIApiResponseModel.getQuestionnaire().getPersonalHistoryBean().getItems();
                    }
                }
            }
        } else {
            if (commonUserApiResponseModel != null) {
                if (commonUserApiResponseModel.getQuestionnaire() != null &&
                        commonUserApiResponseModel.getQuestionnaire().getPersonalHistoryBean() != null) {
                    if (commonUserApiResponseModel.getQuestionnaire().getPersonalHistoryBean().getItems() != null) {
                        personalHistoryModelList = commonUserApiResponseModel.getQuestionnaire().getPersonalHistoryBean().getItems();
                    }
                }
            }
        }

        medicalHistoryViewModel.getPersonalHistoryMutableLiveData().setValue(personalHistoryModelList);

        setRecyclerViewWithCheckboxAdapter();

    }

    private void showHealthHabits() {
        List<HealthHabitModel> healthHabitModelList = new ArrayList<>();

        if (UserType.isUserPatient()) {
            if (whoAmIApiResponseModel.getQuestionnaire() != null) {
                if (whoAmIApiResponseModel.getQuestionnaire().getHealthHabitBean() != null) {
                    if (whoAmIApiResponseModel.getQuestionnaire().getHealthHabitBean().getItems() != null) {
                        healthHabitModelList = whoAmIApiResponseModel.getQuestionnaire().getHealthHabitBean().getItems();
                    }
                }
            }
        } else {
            if (commonUserApiResponseModel != null) {
                if (commonUserApiResponseModel.getQuestionnaire() != null &&
                        commonUserApiResponseModel.getQuestionnaire().getHealthHabitBean() != null) {
                    if (commonUserApiResponseModel.getQuestionnaire().getHealthHabitBean().getItems() != null) {
                        healthHabitModelList = commonUserApiResponseModel.getQuestionnaire().getHealthHabitBean().getItems();
                    }
                }
            }
        }

        medicalHistoryViewModel.getHealthHabitMutableLiveData().setValue(healthHabitModelList);

        setRecyclerViewWithCheckboxAdapter();
    }

    private void showSexualHistory() {
        List<SexualHistoryModel> sexualHistoryModelList = new ArrayList<>();

        if (UserType.isUserPatient()) {
            if (whoAmIApiResponseModel.getQuestionnaire() != null) {
                if (whoAmIApiResponseModel.getQuestionnaire().getSexualHistoryBean() != null) {
                    if (whoAmIApiResponseModel.getQuestionnaire().getSexualHistoryBean().getItems() != null) {
                        sexualHistoryModelList = whoAmIApiResponseModel.getQuestionnaire().getSexualHistoryBean().getItems();
                    }
                }
            }
        } else {
            if (commonUserApiResponseModel != null) {
                if (commonUserApiResponseModel.getQuestionnaire() != null &&
                        commonUserApiResponseModel.getQuestionnaire().getSexualHistoryBean() != null) {
                    if (commonUserApiResponseModel.getQuestionnaire().getSexualHistoryBean().getItems() != null) {
                        sexualHistoryModelList = commonUserApiResponseModel.getQuestionnaire().getSexualHistoryBean().getItems();
                    }
                }
            }
        }

        medicalHistoryViewModel.getSexualHistoryMutableLiveData().setValue(sexualHistoryModelList);

        setRecyclerViewWithCheckboxAdapter();

    }

    private void showRecentImmunization() {
        if (UserType.isUserPatient()) {
            onViewChangeInterface.hideOrShowNext(true);
            onViewChangeInterface.updateNextTitle(getString(R.string.add));
        }

        addItemTv.setText(getString(R.string.add_new_immunization));
        addItemTv.setVisibility(View.VISIBLE);
        addItemTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showUserInputDialog(getActivity(),
                        getString(R.string.new_immunization),
                        getString(R.string.please_provide_the_details),
                        getString(R.string.name).toLowerCase(),
                        getString(R.string.Done),
                        getString(R.string.Cancel),
                        new CustomDialogClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, String inputText) {
                                if (checkBoxListAdapter != null) {
                                    checkBoxListAdapter.addListItem(inputText);
                                }
                                dialog.dismiss();
                            }
                        },
                        new CustomDialogClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, String inputText) {
                                dialog.dismiss();
                            }
                        });
            }
        });

        List<MedicalHistoryCommonModel> medicalHistoryCommonModelList = new ArrayList<>();

        if (UserType.isUserPatient()) {
            if (whoAmIApiResponseModel.getQuestionnaire() != null) {
                if (whoAmIApiResponseModel.getQuestionnaire().getRecentImmunizationBean() != null) {
                    if (whoAmIApiResponseModel.getQuestionnaire().getRecentImmunizationBean().getItems() != null) {
                        medicalHistoryCommonModelList = whoAmIApiResponseModel.getQuestionnaire().getRecentImmunizationBean().getItems();
                    }
                }
            }
        } else {
            if (commonUserApiResponseModel != null) {
                if (commonUserApiResponseModel.getQuestionnaire() != null &&
                        commonUserApiResponseModel.getQuestionnaire().getRecentImmunizationBean() != null) {
                    if (commonUserApiResponseModel.getQuestionnaire().getRecentImmunizationBean().getItems() != null) {
                        medicalHistoryCommonModelList = commonUserApiResponseModel.getQuestionnaire().getRecentImmunizationBean().getItems();
                    }
                }
            }
        }

        medicalHistoryViewModel.getRecentImmunizationMutableLiveData().setValue(medicalHistoryCommonModelList);

        setRecyclerViewWithCheckboxAdapter();
    }

    private void showFamilyList() {
        addItemTv.setText(getString(R.string.add).toUpperCase());
        addItemTv.setVisibility(View.VISIBLE);
        addItemTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showUserInputDialog(getActivity(),
                        getString(R.string.new_family_history),
                        getString(R.string.please_provide_the_details),
                        getString(R.string.name_of_the_disease),
                        getString(R.string.Done),
                        getString(R.string.Cancel),
                        new CustomDialogClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, String inputText) {
                                if (checkBoxListAdapter != null) {
                                    checkBoxListAdapter.addListItem(inputText);
                                }
                                dialog.dismiss();
                            }
                        },
                        new CustomDialogClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, String inputText) {
                                dialog.dismiss();
                            }
                        });
            }
        });

        List<FamilyHistoryModel> familyHistoryModelList = new ArrayList<>();

        if (UserType.isUserPatient()) {
            if (whoAmIApiResponseModel.getQuestionnaire() != null) {
                if (whoAmIApiResponseModel.getQuestionnaire().getFamilyHistoryBean() != null) {
                    if (whoAmIApiResponseModel.getQuestionnaire().getFamilyHistoryBean().getItems() != null) {
                        familyHistoryModelList = whoAmIApiResponseModel.getQuestionnaire().getFamilyHistoryBean().getItems();
                    }
                }
            }
        } else {
            if (commonUserApiResponseModel != null) {
                if (commonUserApiResponseModel.getQuestionnaire() != null &&
                        commonUserApiResponseModel.getQuestionnaire().getFamilyHistoryBean() != null) {
                    if (commonUserApiResponseModel.getQuestionnaire().getFamilyHistoryBean().getItems() != null) {
                        familyHistoryModelList = commonUserApiResponseModel.getQuestionnaire().getFamilyHistoryBean().getItems();
                    }
                }
            }
        }

        medicalHistoryViewModel.getFamilyHistoryMutableLiveData().setValue(familyHistoryModelList);

        setRecyclerViewWithCheckboxAdapter();
    }

    private void showSurgeries() {
        othersLl.setVisibility(View.VISIBLE);
        otherLabelTv.setText(getString(R.string.any_other_surgeries));
        otherEt.setHint(getString(R.string.separate_with_comma));

        List<MedicalHistoryCommonModel> medicalHistoryCommonModelList = new ArrayList<>();

        if (UserType.isUserPatient()) {
            if (whoAmIApiResponseModel.getQuestionnaire() != null) {
                if (whoAmIApiResponseModel.getQuestionnaire().getSurgeries() != null) {
                    medicalHistoryViewModel.setOtherInformation(whoAmIApiResponseModel.getQuestionnaire().getSurgeries().getOtherInformation());
                    if (whoAmIApiResponseModel.getQuestionnaire().getSurgeries().getItems() != null) {
                        medicalHistoryCommonModelList = whoAmIApiResponseModel.getQuestionnaire().getSurgeries().getItems();
                    }
                }
            }
        } else {
            if (commonUserApiResponseModel != null) {

                if (commonUserApiResponseModel.getQuestionnaire() != null &&
                        commonUserApiResponseModel.getQuestionnaire().getSurgeries() != null) {

                    medicalHistoryViewModel.setOtherInformation(commonUserApiResponseModel.getQuestionnaire().getSurgeries().getOtherInformation());
                    if (commonUserApiResponseModel.getQuestionnaire().getSurgeries().getItems() != null) {
                        medicalHistoryCommonModelList = commonUserApiResponseModel.getQuestionnaire().getSurgeries().getItems();
                    }
                }
            }
        }

        medicalHistoryViewModel.getSurgeriesMutableLiveData().setValue(medicalHistoryCommonModelList);

        if (medicalHistoryViewModel.getOtherInformation() != null &&
                !medicalHistoryViewModel.getOtherInformation().isEmpty()) {
            otherEt.setText(medicalHistoryViewModel.getOtherInformation());
        }

        setRecyclerViewWithCheckboxAdapter();
    }

    private void showPastMedicalHistory() {
        othersLl.setVisibility(View.VISIBLE);
        otherLabelTv.setText(getString(R.string.other_medical_conditions));
        otherEt.setHint(getString(R.string.separate_with_comma));

        List<MedicalHistoryCommonModel> medicalHistoryCommonModelList = new ArrayList<>();

        if (UserType.isUserPatient()) {
            if (whoAmIApiResponseModel.getQuestionnaire() != null &&
                    whoAmIApiResponseModel.getQuestionnaire().getPastMedicalHistoryBean() != null) {

                medicalHistoryViewModel.setOtherInformation(whoAmIApiResponseModel.getQuestionnaire().getPastMedicalHistoryBean().getOtherInformation());

                if (whoAmIApiResponseModel.getQuestionnaire().getPastMedicalHistoryBean().getItems() != null) {
                    medicalHistoryCommonModelList = whoAmIApiResponseModel.getQuestionnaire().getPastMedicalHistoryBean().getItems();
                }
            }
        } else {
            if (commonUserApiResponseModel != null &&
                    commonUserApiResponseModel.getQuestionnaire() != null &&
                    commonUserApiResponseModel.getQuestionnaire().getPastMedicalHistoryBean() != null) {

                medicalHistoryViewModel.setOtherInformation(commonUserApiResponseModel.getQuestionnaire().getPastMedicalHistoryBean().getOtherInformation());

                if (commonUserApiResponseModel.getQuestionnaire().getPastMedicalHistoryBean().getItems() != null) {
                    medicalHistoryCommonModelList = commonUserApiResponseModel.getQuestionnaire().getPastMedicalHistoryBean().getItems();
                }
            }
        }

        medicalHistoryViewModel.getPastMedicalHistoryMutableLiveData().setValue(medicalHistoryCommonModelList);

        if (medicalHistoryViewModel.getOtherInformation() != null &&
                !medicalHistoryViewModel.getOtherInformation().isEmpty()) {
            otherEt.setText(medicalHistoryViewModel.getOtherInformation());
        }

        setRecyclerViewWithCheckboxAdapter();
    }

    private void setRecyclerViewWithCheckboxAdapter() {
        checkBoxListAdapter = new MedicalHistoryCheckBoxListAdapter(getActivity(), type);
        recyclerView.setAdapter(checkBoxListAdapter);
    }

    private void showMedications() {
        List<MedicationModel> medicationModelList = new ArrayList<>();

        if (UserType.isUserPatient()) {
            if (whoAmIApiResponseModel.getQuestionnaire() != null &&
                    whoAmIApiResponseModel.getQuestionnaire().getMedication() != null &&
                    whoAmIApiResponseModel.getQuestionnaire().getMedication().getItems() != null) {

                medicationModelList = whoAmIApiResponseModel.getQuestionnaire().getMedication().getItems();
            }
        } else {
            if (commonUserApiResponseModel != null) {
                if (commonUserApiResponseModel.getQuestionnaire() != null &&
                        commonUserApiResponseModel.getQuestionnaire().getMedication() != null &&
                        commonUserApiResponseModel.getQuestionnaire().getMedication().getItems() != null) {
                    medicationModelList = commonUserApiResponseModel.getQuestionnaire().getMedication().getItems();
                }
            }
        }

        medicalHistoryViewModel.getMedicationListMutableLiveData().setValue(medicationModelList);

        MedicationListAdapter medicationListAdapter = new MedicationListAdapter(getActivity());
        recyclerView.setAdapter(medicationListAdapter);

        addItemTv.setVisibility(View.VISIBLE);
        addItemTv.setText(getString(R.string.add_medication_caps));
        addItemTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medicalHistoryViewModel.getMedicationListMutableLiveData().getValue().add(new MedicationModel());
                recyclerView.setAdapter(medicationListAdapter);
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (medicalHistoryViewModel.getMedicationListMutableLiveData().getValue() != null) {
                    List<MedicationModel> medicationModelList = medicalHistoryViewModel.getMedicationListMutableLiveData().getValue();
                    boolean isAllFieldsEntered = true;

                    for (int i = 0; i < medicationModelList.size(); i++) {
                        if (medicationModelList.get(i).getDrugName() == null || medicationModelList.get(i).getDrugName().isEmpty()) {
                            isAllFieldsEntered = false;
                            break;
                        }
                    }

                    if (!isAllFieldsEntered) {
                        Utils.showAlertDialog(getActivity(), getString(R.string.error), getString(R.string.drug_name_error_msg), getString(R.string.ok), null,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }, null);
                    } else {
                        updateUserHistory();
                    }
                }
            }
        });
    }

    private void updateUserHistory() {

        UpdateQuestionaryBodyModel questionaryBodyModel = new UpdateQuestionaryBodyModel();

        QuestionnaireBean questionnaireBean;

        if (UserType.isUserPatient()) {
            questionnaireBean = whoAmIApiResponseModel.getQuestionnaire();
        } else {
            questionnaireBean = commonUserApiResponseModel.getQuestionnaire();
        }

        if (questionnaireBean == null) {
            questionnaireBean = new QuestionnaireBean();
        }

        switch (type) {
            case MH_MEDICATION:
                if (medicalHistoryViewModel.getMedicationListMutableLiveData().getValue() != null &&
                        medicalHistoryViewModel.getMedicationListMutableLiveData().getValue().size() > 0) {
                    questionnaireBean.setMedication(new QuestionnaireBean.MedicationBean(medicalHistoryViewModel.getMedicationListMutableLiveData().getValue()));
                } else {
                    questionnaireBean.setMedication(null);
                }
                break;
            case MH_PAST_MEDICAL_HISTORY:
                if (medicalHistoryViewModel.getPastMedicalHistoryMutableLiveData().getValue() != null &&
                        medicalHistoryViewModel.getPastMedicalHistoryMutableLiveData().getValue().size() > 0) {
                    questionnaireBean.setPastMedicalHistoryBean(new QuestionnaireBean.PastMedicalHistoryBean(medicalHistoryViewModel.getOtherInformation(), medicalHistoryViewModel.getPastMedicalHistoryMutableLiveData().getValue()));
                } else {
                    questionnaireBean.setPastMedicalHistoryBean(null);
                }
                break;
            case MH_SURGERIES:
                if (medicalHistoryViewModel.getSurgeriesMutableLiveData().getValue() != null &&
                        medicalHistoryViewModel.getSurgeriesMutableLiveData().getValue().size() > 0) {
                    questionnaireBean.setSurgeries(new QuestionnaireBean.SurgeriesBean(medicalHistoryViewModel.getOtherInformation(), medicalHistoryViewModel.getSurgeriesMutableLiveData().getValue()));
                } else {
                    questionnaireBean.setSurgeries(null);
                }
                break;
            case MH_FAMILY_HISTORY:
                if (medicalHistoryViewModel.getFamilyHistoryMutableLiveData().getValue() != null &&
                        medicalHistoryViewModel.getFamilyHistoryMutableLiveData().getValue().size() > 0) {
                    questionnaireBean.setFamilyHistoryBean(new QuestionnaireBean.FamilyHistoryBean(medicalHistoryViewModel.getFamilyHistoryMutableLiveData().getValue()));
                } else {
                    questionnaireBean.setFamilyHistoryBean(null);
                }
                break;
            case MH_PERSONAL_HISTORY:
                if (medicalHistoryViewModel.getPersonalHistoryMutableLiveData().getValue() != null &&
                        medicalHistoryViewModel.getPersonalHistoryMutableLiveData().getValue().size() > 0) {
                    questionnaireBean.setPersonalHistoryBean(new QuestionnaireBean.PersonalHistoryBean(medicalHistoryViewModel.getPersonalHistoryMutableLiveData().getValue()));
                } else {
                    questionnaireBean.setPersonalHistoryBean(null);
                }
                break;
            case MH_HEALTH_HABITS:
                if (medicalHistoryViewModel.getHealthHabitMutableLiveData().getValue() != null &&
                        medicalHistoryViewModel.getHealthHabitMutableLiveData().getValue().size() > 0) {
                    questionnaireBean.setHealthHabitBean(new QuestionnaireBean.HealthHabitBean(medicalHistoryViewModel.getHealthHabitMutableLiveData().getValue()));
                } else {
                    questionnaireBean.setHealthHabitBean(null);
                }
                break;
            case MH_SEXUAL_HISTORY:
                if (medicalHistoryViewModel.getSexualHistoryMutableLiveData().getValue() != null &&
                        medicalHistoryViewModel.getSexualHistoryMutableLiveData().getValue().size() > 0) {
                    questionnaireBean.setSexualHistoryBean(new QuestionnaireBean.SexualHistoryBean(medicalHistoryViewModel.getSexualHistoryMutableLiveData().getValue()));
                } else {
                    questionnaireBean.setSexualHistoryBean(null);
                }
                break;
            case MH_RECENT_IMMUNIZATION:
                if (medicalHistoryViewModel.getRecentImmunizationMutableLiveData().getValue() != null &&
                        medicalHistoryViewModel.getRecentImmunizationMutableLiveData().getValue().size() > 0) {
                    questionnaireBean.setRecentImmunizationBean(new QuestionnaireBean.CommonItemBean(medicalHistoryViewModel.getRecentImmunizationMutableLiveData().getValue()));
                } else {
                    questionnaireBean.setRecentImmunizationBean(null);
                }
                break;
        }

        questionaryBodyModel.setQuestionnaire(questionnaireBean);

        if (UserType.isUserPatient()) {
            AppointmentSlotUpdate appointmentSlotUpdate = ViewModelProviders.of(this).get(AppointmentSlotUpdate.class);
            attachObserverInterface.attachObserver(appointmentSlotUpdate);
            appointmentSlotUpdate.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
                @Override
                public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                    if (baseApiResponseModel != null) {
                        if (baseApiResponseModel.isSuccess()) {
                            updateWhoAmI(questionaryBodyModel);
                            showSuccessAlert(getString(R.string.success), baseApiResponseModel.getMessage());
                        }
                    }
                }
            });

            appointmentSlotUpdate.updateUserQuestionary(questionaryBodyModel, true);

        } else {
            MedicalHistoryApiViewModel medicalHistoryApiViewModel = ViewModelProviders.of(this).get(MedicalHistoryApiViewModel.class);
            attachObserverInterface.attachObserver(medicalHistoryApiViewModel);
            medicalHistoryApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
                @Override
                public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                    if (baseApiResponseModel != null) {
                        if (baseApiResponseModel.isSuccess()) {
                            if (getTargetFragment() != null) {
                                Bundle bundle = new Bundle();
                                if (commonUserApiResponseModel.getQuestionnaire() == null){
                                    commonUserApiResponseModel.setQuestionnaire(new QuestionnaireBean());
                                }
                                switch (type) {
                                    case MH_MEDICATION:
                                        commonUserApiResponseModel.getQuestionnaire().setMedication(questionaryBodyModel.getQuestionnaire().getMedication());
                                        break;
                                    case MH_PAST_MEDICAL_HISTORY:
                                        commonUserApiResponseModel.getQuestionnaire().setPastMedicalHistoryBean(questionaryBodyModel.getQuestionnaire().getPastMedicalHistoryBean());
                                        break;
                                    case MH_SURGERIES:
                                        commonUserApiResponseModel.getQuestionnaire().setSurgeries(questionaryBodyModel.getQuestionnaire().getSurgeries());
                                        break;
                                    case MH_FAMILY_HISTORY:
                                        commonUserApiResponseModel.getQuestionnaire().setFamilyHistoryBean(questionaryBodyModel.getQuestionnaire().getFamilyHistoryBean());
                                        break;
                                    case MH_PERSONAL_HISTORY:
                                        commonUserApiResponseModel.getQuestionnaire().setPersonalHistoryBean(questionaryBodyModel.getQuestionnaire().getPersonalHistoryBean());
                                        break;
                                    case MH_HEALTH_HABITS:
                                        commonUserApiResponseModel.getQuestionnaire().setHealthHabitBean(questionaryBodyModel.getQuestionnaire().getHealthHabitBean());
                                        break;
                                    case MH_SEXUAL_HISTORY:
                                        commonUserApiResponseModel.getQuestionnaire().setSexualHistoryBean(questionaryBodyModel.getQuestionnaire().getSexualHistoryBean());
                                        break;
                                    case MH_RECENT_IMMUNIZATION:
                                        commonUserApiResponseModel.getQuestionnaire().setRecentImmunizationBean(questionaryBodyModel.getQuestionnaire().getRecentImmunizationBean());
                                        break;
                                }

                                bundle.putSerializable(Constants.USER_DETAIL, commonUserApiResponseModel);
                                Intent intent = new Intent();
                                intent.putExtras(bundle);
                                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                            }
                            showSuccessAlert(getString(R.string.success), baseApiResponseModel.getMessage());
                        }
                    }
                }
            });

            if (commonUserApiResponseModel != null) {
                medicalHistoryApiViewModel.updateQuestionary(commonUserApiResponseModel.getUser_guid(), questionaryBodyModel, true);
            }
        }
    }

    private void updateWhoAmI(UpdateQuestionaryBodyModel questionaryBodyModel) {
        WhoAmIApiResponseModel whoAmIApiResponseModel = UserDetailPreferenceManager.getWhoAmIResponse();
        whoAmIApiResponseModel.setQuestionnaire(questionaryBodyModel.getQuestionnaire());
        UserDetailPreferenceManager.insertUserDetail(whoAmIApiResponseModel);
    }

    private void showSuccessAlert(String title, String message) {
        Utils.showAlertDialog(getActivity(), title, getString(R.string.medical_questionaries_updated), getString(R.string.ok), null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onCloseActionInterface.onClose(false);
            }
        }, null);
    }

    @Override
    public void doCurrentTransaction() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SELECTED_ITEM, OrderConstant.ORDER_DOCUMENTS);
        startActivity(new Intent(getActivity(), CreateOrderActivity.class).putExtras(bundle));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.hideKeyboard(getActivity());
        onViewChangeInterface.hideOrShowNext(false);
    }
}
