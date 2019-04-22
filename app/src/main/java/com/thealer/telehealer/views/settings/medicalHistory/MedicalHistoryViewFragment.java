package com.thealer.telehealer.views.settings.medicalHistory;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.PersonalHistoryModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.QuestionnaireBean;
import com.thealer.telehealer.apilayer.models.medicalHistory.FamilyHistoryModel;
import com.thealer.telehealer.apilayer.models.medicalHistory.HealthHabitModel;
import com.thealer.telehealer.apilayer.models.medicalHistory.MedicalHistoryCommonModel;
import com.thealer.telehealer.apilayer.models.medicalHistory.MedicationModel;
import com.thealer.telehealer.apilayer.models.medicalHistory.SexualHistoryModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyStateUtil;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.PdfViewerFragment;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

/**
 * Created by Aswin on 22,January,2019
 */
public class MedicalHistoryViewFragment extends BaseFragment implements DoCurrentTransactionInterface {
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;

    private OnCloseActionInterface onCloseActionInterface;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private ChangeTitleInterface changeTitleInterface;
    private LinearLayout medicalHistoryContainer;
    private OnViewChangeInterface onViewChangeInterface;
    private CommonUserApiResponseModel commonUserApiResponseModel;
    private LinearLayout emptyLl;
    private ConstraintLayout recyclerEmptyStateView;
    private ImageView emptyIv;
    private TextView emptyTitleTv;
    private TextView emptyMessageTv;
    private CustomButton emptyActionBtn;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
        changeTitleInterface = (ChangeTitleInterface) getActivity();
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medical_history_view, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        emptyLl = (LinearLayout) view.findViewById(R.id.empty_ll);
        emptyIv = (ImageView) view.findViewById(R.id.empty_iv);
        emptyTitleTv = (TextView) view.findViewById(R.id.empty_title_tv);
        emptyMessageTv = (TextView) view.findViewById(R.id.empty_message_tv);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        medicalHistoryContainer = (LinearLayout) view.findViewById(R.id.medical_history_container);

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseActionInterface.onClose(false);
            }
        });

        toolbar.inflateMenu(R.menu.menu_history);
        if (UserType.isUserAssistant()) {
            toolbar.getMenu().findItem(R.id.menu_edit).setVisible(false);
        }

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_edit:
                        showHistoryList();
                        break;
                    case R.id.menu_print:
                        generatePdf();
                        break;
                }
                return true;
            }
        });

    }

    private void generatePdf() {
        MedicalHistoryPdfGenerator pdfGenerator = new MedicalHistoryPdfGenerator(getActivity());
        String pdfHtml = pdfGenerator.getPdfHtml(commonUserApiResponseModel);

        PdfViewerFragment pdfViewerFragment = new PdfViewerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ArgumentKeys.HTML_FILE, pdfHtml);
        bundle.putString(ArgumentKeys.PDF_TITLE, getString(R.string.health_profile));
        pdfViewerFragment.setArguments(bundle);
        showSubFragmentInterface.onShowFragment(pdfViewerFragment);
    }

    private void showHistoryList() {
        MedicalHistoryList medicalHistoryList = new MedicalHistoryList();
        medicalHistoryList.setArguments(getArguments());
        showSubFragmentInterface.onShowFragment(medicalHistoryList);
        onViewChangeInterface.hideOrShowNext(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        Utils.hideKeyboard(getActivity());

        if (UserType.isUserPatient()) {
            changeTitleInterface.onTitleChange(getString(R.string.health_profile));
            onViewChangeInterface.hideOrShowNext(true);
            onViewChangeInterface.updateNextTitle(getString(R.string.edit));

        } else if (UserType.isUserAssistant()) {
            onViewChangeInterface.hideOrShowNext(false);
        }
        toolbarTitle.setText(getString(R.string.health_profile));

        showMedicalHistory();
    }

    private void showOrhideEmptyState(boolean show) {
        if (show) {
            emptyLl.setVisibility(View.VISIBLE);
            emptyIv.setImageDrawable(getActivity().getDrawable(EmptyStateUtil.getImage(EmptyViewConstants.EMPTY_PATIENT_HISTORY)));
            emptyTitleTv.setText(EmptyStateUtil.getTitle(EmptyViewConstants.EMPTY_PATIENT_HISTORY));
            emptyMessageTv.setText(EmptyStateUtil.getMessage(EmptyViewConstants.EMPTY_PATIENT_HISTORY));
            toolbar.getMenu().findItem(R.id.menu_print).setVisible(false);
        } else {
            emptyLl.setVisibility(View.GONE);
            toolbar.getMenu().findItem(R.id.menu_print).setVisible(true);
        }
    }

    private void showMedicalHistory() {
        QuestionnaireBean questionnaireBean = null;

        if (UserType.isUserPatient()) {
            WhoAmIApiResponseModel whoAmIApiResponseModel = UserDetailPreferenceManager.getWhoAmIResponse();
            questionnaireBean = whoAmIApiResponseModel.getQuestionnaire();
            commonUserApiResponseModel = whoAmIApiResponseModel;
        } else {
            if (getArguments() != null) {
                commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
                if (commonUserApiResponseModel != null) {
                    questionnaireBean = commonUserApiResponseModel.getQuestionnaire();

                    if (questionnaireBean == null || questionnaireBean.isHistoryEmpty()) {
                        showOrhideEmptyState(true);
                    } else {
                        showOrhideEmptyState(false);
                    }
                }
            }
        }

        medicalHistoryContainer.removeAllViews();

        if (questionnaireBean != null && questionnaireBean.isQuestionariesEmpty()) {

            //category items
            TextView categoryTitle;
            LinearLayout categoryDetailContainer = null;

            //category detail items
            TextView categoryDetailTitle;
            TextView categoryDetail;
            View bottomView;

            /**
             * Medication details added here
             */
            if (questionnaireBean.getMedication() != null && questionnaireBean.getMedication().getItems().size() > 0) {
                View category = getLayoutInflater().inflate(R.layout.view_medical_history_category, null);


                categoryTitle = (TextView) category.findViewById(R.id.category_title);
                categoryDetailContainer = (LinearLayout) category.findViewById(R.id.category_detail_container);

                categoryTitle.setText(MedicalHistoryConstants.MH_MEDICATION);

                for (int i = 0; i < questionnaireBean.getMedication().getItems().size(); i++) {
                    View detailView = getLayoutInflater().inflate(R.layout.view_medical_history_category_detail, null);

                    categoryDetailTitle = (TextView) detailView.findViewById(R.id.category_detail_title);
                    categoryDetail = (TextView) detailView.findViewById(R.id.category_detail);
                    bottomView = (View) detailView.findViewById(R.id.bottom_view);

                    if (i == questionnaireBean.getMedication().getItems().size() - 1) {
                        bottomView.setVisibility(View.GONE);
                    }

                    MedicationModel medicationModel = questionnaireBean.getMedication().getItems().get(i);
                    categoryDetailTitle.setText(medicationModel.getDrugName());

                    String detail = ((medicationModel.getDirection() == null || medicationModel.getDirection().isEmpty()) ? 0 : medicationModel.getDirection()) + " tablet " + medicationModel.getDirectionType1() + " " + medicationModel.getDirectionType2();
                    categoryDetail.setText(detail);

                    categoryDetailContainer.addView(detailView);
                }

                medicalHistoryContainer.addView(category);
            }

            /**
             * Past medical history
             */

            if (questionnaireBean.getPastMedicalHistoryBean() != null) {
                View category = getLayoutInflater().inflate(R.layout.view_medical_history_category, null);

                categoryTitle = (TextView) category.findViewById(R.id.category_title);

                categoryTitle.setText(MedicalHistoryConstants.MH_PAST_MEDICAL_HISTORY);

                if (questionnaireBean.getPastMedicalHistoryBean().getItems().size() > 0) {

                    categoryDetailContainer = (LinearLayout) category.findViewById(R.id.category_detail_container);

                    for (int i = 0; i < questionnaireBean.getPastMedicalHistoryBean().getItems().size(); i++) {
                        View detailView = getLayoutInflater().inflate(R.layout.view_medical_history_category_detail, null);

                        categoryDetailTitle = (TextView) detailView.findViewById(R.id.category_detail_title);
                        categoryDetail = (TextView) detailView.findViewById(R.id.category_detail);
                        bottomView = (View) detailView.findViewById(R.id.bottom_view);

                        if (i == questionnaireBean.getPastMedicalHistoryBean().getItems().size() - 1) {
                            if (questionnaireBean.getPastMedicalHistoryBean().getOtherInformation() == null ||
                                    questionnaireBean.getPastMedicalHistoryBean().getOtherInformation().isEmpty()) {
                                bottomView.setVisibility(View.GONE);
                            }
                        }

                        MedicalHistoryCommonModel medicalHistoryCommonModel = questionnaireBean.getPastMedicalHistoryBean().getItems().get(i);

                        categoryDetailTitle.setText(medicalHistoryCommonModel.getTitle());

                        if (medicalHistoryCommonModel.getAdditionalInformation() != null &&
                                !medicalHistoryCommonModel.getAdditionalInformation().isEmpty()) {
                            categoryDetail.setText(medicalHistoryCommonModel.getAdditionalInformation());
                        } else {
                            categoryDetail.setVisibility(View.GONE);
                        }


                        categoryDetailContainer.addView(detailView);
                    }
                }

                if (questionnaireBean.getPastMedicalHistoryBean().getOtherInformation() != null &&
                        !questionnaireBean.getPastMedicalHistoryBean().getOtherInformation().isEmpty()) {

                    View detailView = getLayoutInflater().inflate(R.layout.view_medical_history_category_detail, null);

                    categoryDetailTitle = (TextView) detailView.findViewById(R.id.category_detail_title);
                    categoryDetail = (TextView) detailView.findViewById(R.id.category_detail);
                    bottomView = (View) detailView.findViewById(R.id.bottom_view);

                    categoryDetailTitle.setText(getString(R.string.other_medical_history));
                    categoryDetail.setText(questionnaireBean.getPastMedicalHistoryBean().getOtherInformation());
                    bottomView.setVisibility(View.GONE);

                    if (categoryDetailContainer == null) {
                        categoryDetailContainer = (LinearLayout) category.findViewById(R.id.category_detail_container);
                    }
                    categoryDetailContainer.addView(detailView);
                }

                if (categoryDetailContainer != null) {
                    medicalHistoryContainer.addView(category);
                }
            }

            /**
             * For Surgeries
             */

            if (questionnaireBean.getSurgeries() != null) {

                View category = getLayoutInflater().inflate(R.layout.view_medical_history_category, null);

                categoryTitle = (TextView) category.findViewById(R.id.category_title);
                categoryDetailContainer = (LinearLayout) category.findViewById(R.id.category_detail_container);

                categoryTitle.setText(MedicalHistoryConstants.MH_SURGERIES);

                if (questionnaireBean.getSurgeries().getItems().size() > 0) {

                    categoryDetailContainer = (LinearLayout) category.findViewById(R.id.category_detail_container);

                    for (int i = 0; i < questionnaireBean.getSurgeries().getItems().size(); i++) {

                        View detailView = getLayoutInflater().inflate(R.layout.view_medical_history_category_detail, null);

                        categoryDetailTitle = (TextView) detailView.findViewById(R.id.category_detail_title);
                        categoryDetail = (TextView) detailView.findViewById(R.id.category_detail);
                        bottomView = (View) detailView.findViewById(R.id.bottom_view);

                        if (i == questionnaireBean.getSurgeries().getItems().size() - 1) {
                            if (questionnaireBean.getSurgeries().getOtherInformation() == null ||
                                    questionnaireBean.getSurgeries().getOtherInformation().isEmpty()) {
                                bottomView.setVisibility(View.GONE);
                            }
                        }

                        MedicalHistoryCommonModel medicalHistoryCommonModel = questionnaireBean.getSurgeries().getItems().get(i);

                        categoryDetailTitle.setText(medicalHistoryCommonModel.getTitle());

                        categoryDetail.setVisibility(View.GONE);

                        categoryDetailContainer.addView(detailView);
                    }
                }


                if (questionnaireBean.getSurgeries().getOtherInformation() != null &&
                        !questionnaireBean.getSurgeries().getOtherInformation().isEmpty()) {

                    View detailView = getLayoutInflater().inflate(R.layout.view_medical_history_category_detail, null);

                    categoryDetailTitle = (TextView) detailView.findViewById(R.id.category_detail_title);
                    categoryDetail = (TextView) detailView.findViewById(R.id.category_detail);
                    bottomView = (View) detailView.findViewById(R.id.bottom_view);

                    categoryDetailTitle.setText(getString(R.string.other_medical_history));
                    categoryDetail.setText(questionnaireBean.getSurgeries().getOtherInformation());
                    bottomView.setVisibility(View.GONE);

                    if (categoryDetailContainer == null) {
                        categoryDetailContainer = (LinearLayout) category.findViewById(R.id.category_detail_container);
                    }
                    categoryDetailContainer.addView(detailView);
                }

                if (categoryDetailContainer != null) {
                    medicalHistoryContainer.addView(category);
                }
            }

            /**
             * For Family history
             */

            if (questionnaireBean.getFamilyHistoryBean() != null &&
                    questionnaireBean.getFamilyHistoryBean().getItems() != null &&
                    questionnaireBean.getFamilyHistoryBean().getItems().size() > 0) {

                View category = getLayoutInflater().inflate(R.layout.view_medical_history_category, null);

                categoryTitle = (TextView) category.findViewById(R.id.category_title);
                categoryDetailContainer = (LinearLayout) category.findViewById(R.id.category_detail_container);

                categoryTitle.setText(MedicalHistoryConstants.MH_FAMILY_HISTORY);

                if (questionnaireBean.getFamilyHistoryBean().getItems().size() > 0) {

                    categoryDetailContainer = (LinearLayout) category.findViewById(R.id.category_detail_container);

                    for (int i = 0; i < questionnaireBean.getFamilyHistoryBean().getItems().size(); i++) {

                        View detailView = getLayoutInflater().inflate(R.layout.view_medical_history_category_detail, null);

                        categoryDetailTitle = (TextView) detailView.findViewById(R.id.category_detail_title);
                        categoryDetail = (TextView) detailView.findViewById(R.id.category_detail);
                        bottomView = (View) detailView.findViewById(R.id.bottom_view);

                        if (i == questionnaireBean.getFamilyHistoryBean().getItems().size() - 1) {
                            bottomView.setVisibility(View.GONE);
                        }

                        FamilyHistoryModel familyHistoryModel = questionnaireBean.getFamilyHistoryBean().getItems().get(i);

                        categoryDetailTitle.setText(familyHistoryModel.getTitle());

                        if (!familyHistoryModel.getSelectedRelations().isEmpty()) {
                            categoryDetail.setVisibility(View.VISIBLE);
                            categoryDetail.setText(familyHistoryModel.getSelectedRelationsString());

                        } else {
                            categoryDetail.setVisibility(View.GONE);
                        }

                        categoryDetailContainer.addView(detailView);
                    }
                }

                if (categoryDetailContainer != null) {
                    medicalHistoryContainer.addView(category);
                }
            }


            /**
             * For recent immunization
             */


            if (questionnaireBean.getRecentImmunizationBean() != null &&
                    questionnaireBean.getRecentImmunizationBean().getItems() != null &&
                    questionnaireBean.getRecentImmunizationBean().getItems().size() > 0) {

                View category = getLayoutInflater().inflate(R.layout.view_medical_history_category, null);

                categoryTitle = (TextView) category.findViewById(R.id.category_title);
                categoryDetailContainer = (LinearLayout) category.findViewById(R.id.category_detail_container);

                categoryTitle.setText(MedicalHistoryConstants.MH_RECENT_IMMUNIZATION);

                if (questionnaireBean.getRecentImmunizationBean().getItems().size() > 0) {

                    categoryDetailContainer = (LinearLayout) category.findViewById(R.id.category_detail_container);

                    for (int i = 0; i < questionnaireBean.getRecentImmunizationBean().getItems().size(); i++) {

                        View detailView = getLayoutInflater().inflate(R.layout.view_medical_history_category_detail, null);

                        categoryDetailTitle = (TextView) detailView.findViewById(R.id.category_detail_title);
                        categoryDetail = (TextView) detailView.findViewById(R.id.category_detail);
                        bottomView = (View) detailView.findViewById(R.id.bottom_view);

                        if (i == questionnaireBean.getRecentImmunizationBean().getItems().size() - 1) {
                            bottomView.setVisibility(View.GONE);
                        }

                        MedicalHistoryCommonModel medicalHistoryCommonModel = questionnaireBean.getRecentImmunizationBean().getItems().get(i);

                        categoryDetailTitle.setText(medicalHistoryCommonModel.getTitle());

                        if (!medicalHistoryCommonModel.getAdditionalInformation().isEmpty()) {
                            categoryDetail.setVisibility(View.VISIBLE);
                            categoryDetail.setText(medicalHistoryCommonModel.getAdditionalInformation());

                        } else {
                            categoryDetail.setVisibility(View.GONE);
                        }

                        categoryDetailContainer.addView(detailView);
                    }
                }

                if (categoryDetailContainer != null) {
                    medicalHistoryContainer.addView(category);
                }
            }

            /**
             * For sexual history
             */

            if (questionnaireBean.getSexualHistoryBean() != null &&
                    questionnaireBean.getSexualHistoryBean().getItems() != null &&
                    questionnaireBean.getSexualHistoryBean().getItems().size() > 0) {

                View category = getLayoutInflater().inflate(R.layout.view_medical_history_category, null);

                categoryTitle = (TextView) category.findViewById(R.id.category_title);
                categoryDetailContainer = (LinearLayout) category.findViewById(R.id.category_detail_container);

                categoryTitle.setText(MedicalHistoryConstants.MH_SEXUAL_HISTORY);

                if (questionnaireBean.getSexualHistoryBean().getItems().size() > 0) {

                    categoryDetailContainer = (LinearLayout) category.findViewById(R.id.category_detail_container);

                    for (int i = 0; i < questionnaireBean.getSexualHistoryBean().getItems().size(); i++) {

                        View detailView = getLayoutInflater().inflate(R.layout.view_medical_history_category_detail, null);

                        categoryDetailTitle = (TextView) detailView.findViewById(R.id.category_detail_title);
                        categoryDetail = (TextView) detailView.findViewById(R.id.category_detail);
                        bottomView = (View) detailView.findViewById(R.id.bottom_view);

                        if (i == questionnaireBean.getSexualHistoryBean().getItems().size() - 1) {
                            bottomView.setVisibility(View.GONE);
                        }

                        SexualHistoryModel sexualHistoryModel = questionnaireBean.getSexualHistoryBean().getItems().get(i);

                        categoryDetailTitle.setText(sexualHistoryModel.getTitle());
                        categoryDetail.setText(sexualHistoryModel.getDetailString(getActivity()));

                        categoryDetailContainer.addView(detailView);
                    }
                }

                if (categoryDetailContainer != null) {
                    medicalHistoryContainer.addView(category);
                }
            }


            /**
             * For health habit
             */

            if (questionnaireBean.getHealthHabitBean() != null &&
                    questionnaireBean.getHealthHabitBean().getItems() != null &&
                    questionnaireBean.getHealthHabitBean().getItems().size() > 0) {

                View category = getLayoutInflater().inflate(R.layout.view_medical_history_category, null);

                categoryTitle = (TextView) category.findViewById(R.id.category_title);
                categoryDetailContainer = (LinearLayout) category.findViewById(R.id.category_detail_container);

                categoryTitle.setText(MedicalHistoryConstants.MH_HEALTH_HABITS);

                if (questionnaireBean.getHealthHabitBean().getItems().size() > 0) {

                    categoryDetailContainer = (LinearLayout) category.findViewById(R.id.category_detail_container);

                    for (int i = 0; i < questionnaireBean.getHealthHabitBean().getItems().size(); i++) {

                        View detailView = getLayoutInflater().inflate(R.layout.view_medical_history_category_detail, null);

                        categoryDetailTitle = (TextView) detailView.findViewById(R.id.category_detail_title);
                        categoryDetail = (TextView) detailView.findViewById(R.id.category_detail);
                        bottomView = (View) detailView.findViewById(R.id.bottom_view);

                        if (i == questionnaireBean.getHealthHabitBean().getItems().size() - 1) {
                            bottomView.setVisibility(View.GONE);
                        }

                        HealthHabitModel healthHabitModel = questionnaireBean.getHealthHabitBean().getItems().get(i);

                        categoryDetailTitle.setText(healthHabitModel.getTitle());
                        categoryDetail.setText(healthHabitModel.getDetailString(getActivity()));

                        categoryDetailContainer.addView(detailView);
                    }
                }

                if (categoryDetailContainer != null) {
                    medicalHistoryContainer.addView(category);
                }
            }


            /**
             * For Personal history
             */

            if (questionnaireBean.getPersonalHistoryBean() != null &&
                    questionnaireBean.getPersonalHistoryBean().getItems() != null &&
                    questionnaireBean.getPersonalHistoryBean().getItems().size() > 0) {

                View category = getLayoutInflater().inflate(R.layout.view_medical_history_category, null);

                categoryTitle = (TextView) category.findViewById(R.id.category_title);
                categoryDetailContainer = (LinearLayout) category.findViewById(R.id.category_detail_container);

                categoryTitle.setText(MedicalHistoryConstants.MH_PERSONAL_HISTORY);

                if (questionnaireBean.getPersonalHistoryBean().getItems().size() > 0) {

                    categoryDetailContainer = (LinearLayout) category.findViewById(R.id.category_detail_container);

                    for (int i = 0; i < questionnaireBean.getPersonalHistoryBean().getItems().size(); i++) {

                        View detailView = getLayoutInflater().inflate(R.layout.view_medical_history_category_detail, null);

                        categoryDetailTitle = (TextView) detailView.findViewById(R.id.category_detail_title);
                        categoryDetail = (TextView) detailView.findViewById(R.id.category_detail);
                        bottomView = (View) detailView.findViewById(R.id.bottom_view);

                        if (i == questionnaireBean.getPersonalHistoryBean().getItems().size() - 1) {
                            bottomView.setVisibility(View.GONE);
                        }

                        PersonalHistoryModel personalHistoryModel = questionnaireBean.getPersonalHistoryBean().getItems().get(i);

                        categoryDetailTitle.setText(personalHistoryModel.getTitle());
                        categoryDetail.setText(personalHistoryModel.getDetailString());

                        categoryDetailContainer.addView(detailView);
                    }
                }

                if (categoryDetailContainer != null) {
                    medicalHistoryContainer.addView(category);
                }
            }

        } else {
            if (!UserType.isUserAssistant())
                onCloseActionInterface.onClose(false);
        }
    }

    @Override
    public void doCurrentTransaction() {
        showHistoryList();
    }
}
