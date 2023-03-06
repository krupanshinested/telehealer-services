package com.thealer.telehealer.views.home.orders.forms;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.AppBarLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.orders.forms.DynamicFormDataBean;
import com.thealer.telehealer.apilayer.models.orders.forms.FormsApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.forms.OrdersUserFormsApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomSpinnerView;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.PdfViewerFragment;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.orders.OrderStatus;
import com.thealer.telehealer.views.home.orders.OrdersBaseFragment;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Aswin on 07,May,2019
 */
public class EditableFormFragment extends OrdersBaseFragment implements View.OnClickListener {

    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private LinearLayout editableFormRootLl;
    private Button submitBtn;
    private TextView scoreTv;
    private TextView learnAboutScoreTv;
    private ConstraintLayout scoreViewCl;

    private boolean isEditable = true;
    private boolean hideToolbar, isUpdated;

    private OrdersUserFormsApiResponseModel formsApiResponseModel;
    private DynamicFormDataBean dynamicFormDataBean;
    private OnCloseActionInterface onCloseActionInterface;
    private AttachObserverInterface attachObserverInterface;
    private FormsApiViewModel formsApiViewModel;
    private ShowSubFragmentInterface showSubFragmentInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();

        dynamicFormDataBean = new ViewModelProvider(this).get(DynamicFormDataBean.class);

        formsApiViewModel = new ViewModelProvider(this).get(FormsApiViewModel.class);
        attachObserverInterface.attachObserver(formsApiViewModel);

        formsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel.isSuccess()) {
                        if (submitBtn.getText().equals(getString(R.string.submit))){
                            showToast(getString(R.string.form_updated_successfully));
                            onCloseActionInterface.onClose(false);
                        }else {
                            if (baseApiResponseModel.getPath().isEmpty()){
                                showToast("Failed generate Form");
                                onCloseActionInterface.onClose(false);
                            }else {
                                PdfViewerFragment pdfViewerFragment = new PdfViewerFragment();
                                Bundle bundle =new Bundle();
                                bundle.putString(ArgumentKeys.PDF_TITLE, formsApiResponseModel.getName());
                                bundle.putString(ArgumentKeys.PDF_URL, baseApiResponseModel.getPath());
                                bundle.putBoolean(ArgumentKeys.IS_PDF_DECRYPT, true);
                                pdfViewerFragment.setArguments(bundle);
                                showSubFragmentInterface.onShowFragment(pdfViewerFragment);
                            }
                        }
                    }
                }
            }
        });

        formsApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    if (errorModel.geterrorCode() == null) {
                        showToast(errorModel.getMessage());
                    } else if (!errorModel.geterrorCode().isEmpty() && !errorModel.geterrorCode().equals("SUBSCRIPTION")) {
                        showToast(errorModel.getMessage());
                    }
                }
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editable_form, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        editableFormRootLl = (LinearLayout) view.findViewById(R.id.editable_form_root_ll);
        submitBtn = (Button) view.findViewById(R.id.submit_btn);
        scoreTv = (TextView) view.findViewById(R.id.score_tv);
        learnAboutScoreTv = (TextView) view.findViewById(R.id.learn_about_score_tv);
        scoreViewCl = (ConstraintLayout) view.findViewById(R.id.score_view_cl);

        if (getArguments() != null) {
            formsApiResponseModel = (OrdersUserFormsApiResponseModel) getArguments().getSerializable(ArgumentKeys.FORM_DETAIL);
            hideToolbar = getArguments().getBoolean(ArgumentKeys.IS_HIDE_TOOLBAR, false);

            if (UserDetailPreferenceManager.getWhoAmIResponse().getRole().equals(Constants.ROLE_DOCTOR)) {
                toolbar.inflateMenu(R.menu.orders_detail_menu);
                toolbar.getMenu().removeItem(R.id.send_fax_menu);
//                toolbar.getMenu().removeItem(R.id.print_menu);
                toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.print_menu:
                                formsApiViewModel.printForm(formsApiResponseModel.getUser_form_id(),true);
                                Log.d(TAG, "onMenuItemClick: print"+formsApiResponseModel.getUser_form_id());
                        }
                        return true;
                    }
                });
            }

            if (formsApiResponseModel != null) {
                setData(formsApiResponseModel);
            } else {
                int formid = getArguments().getInt(ArgumentKeys.ORDER_ID);
                String userGuid = getArguments().getString(ArgumentKeys.USER_GUID);
                String doctorGuid = getArguments().getString(ArgumentKeys.DOCTOR_GUID);

                getFormsDetail(userGuid, doctorGuid, new ArrayList<>(Arrays.asList(formid)), true);
            }
        }

        submitBtn.setOnClickListener(this);
        backIv.setOnClickListener(this);
        learnAboutScoreTv.setOnClickListener(this);
    }

    @Override
    public void onDetailReceived(ArrayList<BaseApiResponseModel> baseApiResponseModels) {
        if (baseApiResponseModels != null) {
            ArrayList<OrdersUserFormsApiResponseModel> arrayList = (ArrayList<OrdersUserFormsApiResponseModel>) (Object) baseApiResponseModels;
            if (!arrayList.isEmpty()) {
                formsApiResponseModel = arrayList.get(0);
                setData(formsApiResponseModel);
            }
        }
    }

    private void setData(OrdersUserFormsApiResponseModel formsApiResponseModel) {
        if (hideToolbar) {
            toolbar.setVisibility(View.GONE);
            ((ChangeTitleInterface) getActivity()).onTitleChange(Utils.fromHtml(getString(R.string.str_with_htmltag, formsApiResponseModel.getName())).toString());
        } else {
            toolbarTitle.setText(Utils.fromHtml(getString(R.string.str_with_htmltag, formsApiResponseModel.getName())));
        }

        if (formsApiResponseModel.isCompleted())
            setScore(formsApiResponseModel.getData().getDisplayScore());
        else
            scoreViewCl.setVisibility(View.GONE);

        if (formsApiResponseModel.getStatus() == null || formsApiResponseModel.getStatus().equals(OrderStatus.STATUS_COMPLETED) ||
                !UserType.isUserPatient()) {
            isEditable = false;

            if (formsApiResponseModel.getFilled_form_url() == null) {
                submitBtn.setVisibility(View.GONE);
            } else {
                submitBtn.setVisibility(View.GONE);
                submitBtn.setText(getString(R.string.print));
            }
        }

        if (formsApiResponseModel.getData() != null && formsApiResponseModel.getData().getData() != null &&
                !formsApiResponseModel.getData().getData().isEmpty()) {
            dynamicFormDataBean.getData().addAll(formsApiResponseModel.getData().getData());
            dynamicFormDataBean.setScore_details(formsApiResponseModel.getData().getScore_details());
            dynamicFormDataBean.setTotal_score(formsApiResponseModel.getData().getTotal_score());

            addDynamicFields();
        }

    }

    private void setScore(String score) {
        scoreTv.setText(score);
    }

    private void addDynamicFields() {
        for (int k = 0; k < formsApiResponseModel.getData().getData().size(); k++) {
            DynamicFormDataBean.DataBean dataBean = formsApiResponseModel.getData().getData().get(k);

            View titleView = getLayoutInflater().inflate(R.layout.form_title_view, null);
            TextView titleTv = (TextView) titleView.findViewById(R.id.title_tv);
            if (dataBean.getTitle().equalsIgnoreCase("others")) {
                titleTv.setVisibility(View.GONE);
            } else {
                titleTv.setText(dataBean.getTitle());
            }
            editableFormRootLl.addView(titleView);

            for (int i = 0; i < dataBean.getItems().size(); i++) {
                DynamicFormDataBean.DataBean.ItemsBean itemsBean = dataBean.getItems().get(i);

                View questionView = getLayoutInflater().inflate(R.layout.form_question_view, null);
                TextView formQuestionView = (TextView) questionView.findViewById(R.id.form_question_view);
                formQuestionView.setText(i + 1 + ". " + itemsBean.getQuestion() + ((itemsBean.getProperties().isIs_required()) ? " * " : ""));

                editableFormRootLl.addView(questionView);

                if (itemsBean.getProperties().getType() != null) {
                    switch (itemsBean.getProperties().getType()) {
                        case FormsConstant.TYPE_LIST:
                            View spinnerView = getLayoutInflater().inflate(R.layout.form_list_view, null);
                            CustomSpinnerView formCsv = (CustomSpinnerView) spinnerView.findViewById(R.id.form_csv);
                            List<String> optionList = new ArrayList<>();
                            List<Float> scoreList = new ArrayList<>();
                            for (int j = 0; j < itemsBean.getProperties().getOptions().size(); j++) {
                                optionList.add(itemsBean.getProperties().getOptions().get(j).getValue());
                                scoreList.add(itemsBean.getProperties().getOptions().get(j).getScore());
                            }

                            int finalI = i;
                            int finalK = k;
                            if (formsApiResponseModel.isCompleted() || UserType.isUserPatient()) {
                                formCsv.setSpinnerData(getContext(), optionList, new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        dynamicFormDataBean.getData().get(finalK).getItems().get(finalI).setValue(optionList.get(position));
                                        if (scoreList.get(position) != null) {
                                            dynamicFormDataBean.getData().get(finalK).getItems().get(finalI).setScore(scoreList.get(position));
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            }
                            try {
                                if (itemsBean.getScore() != null) {
                                    for (int j = 0; j < itemsBean.getProperties().getOptions().size(); j++) {
                                        if (itemsBean.getProperties().getOptions().get(j).getScore() == itemsBean.getScore()) {
                                            formCsv.getSpinner().setSelection(j, true);
                                            j = itemsBean.getProperties().getOptions().size() + 1;
                                        }
                                    }
                                    int pos = (int) ((itemsBean.getProperties().getOptions().size() - 1) - itemsBean.getScore());

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            formCsv.getSpinner().setEnabled(isEditable);
                            editableFormRootLl.addView(spinnerView);

                            break;
                        case FormsConstant.TYPE_TEXT_AREA:
                            View textAreaView = getLayoutInflater().inflate(R.layout.form_text_area_view, null);
                            EditText formMultilineEt = (EditText) textAreaView.findViewById(R.id.form_multiline_et);
                            if (itemsBean.getValue() != null) {
                                formMultilineEt.setText(itemsBean.getValue());
                            }
                            int finalI1 = i;
                            int finalK1 = k;
                            formMultilineEt.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    dynamicFormDataBean.getData().get(finalK1).getItems().get(finalI1).setValue(s.toString());
                                }
                            });

                            if (itemsBean.getValue() != null) {
                                formMultilineEt.setText(itemsBean.getValue());
                            }
                            formMultilineEt.setEnabled(isEditable);
                            editableFormRootLl.addView(textAreaView);

                            break;
                        case FormsConstant.TYPE_TEXT_FIELD:
                            View textFieldView = getLayoutInflater().inflate(R.layout.form_edit_text_view, null);
                            EditText formInputEt = (EditText) textFieldView.findViewById(R.id.form_input_et);
                            if (itemsBean.getValue() != null) {
                                formInputEt.setText(itemsBean.getValue());
                            }
                            int finalI2 = i;
                            int finalK2 = k;
                            formInputEt.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    dynamicFormDataBean.getData().get(finalK2).getItems().get(finalI2).setValue(s.toString());
                                }
                            });
                            if (itemsBean.getValue() != null) {
                                formInputEt.setText(itemsBean.getValue());
                            }
                            formInputEt.setEnabled(isEditable);
                            editableFormRootLl.addView(textFieldView);
                            break;
                        case FormsConstant.TYPE_DATE:
                            View dateView = getLayoutInflater().inflate(R.layout.form_date_time_view, null);
                            EditText dateEt = (EditText) dateView.findViewById(R.id.form_date_et);
                            dateEt.setVisibility(View.VISIBLE);

                            if (isEditable) {
                                int finalI3 = i;
                                int finalK3 = k;
                                dateEt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Utils.showDatePickerDialog(getActivity(), null, Constants.TYPE_DOB, new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                                dateEt.setText(Utils.getFormatedDate(year, month, dayOfMonth));
                                                dynamicFormDataBean.getData().get(finalK3).getItems().get(finalI3).setValue(getUtcFromDayMonthYear(year, month, dayOfMonth, 0, 0));
                                            }
                                        });
                                    }
                                });
                            } else {
                                dateEt.setEnabled(false);
                            }
                            if (itemsBean.getValue() != null) {
                                dateEt.setText(Utils.getDayMonthYear(itemsBean.getValue()));
                            }
                            editableFormRootLl.addView(dateView);
                            break;
                        case FormsConstant.TYPE_TIME:
                            View timeView = getLayoutInflater().inflate(R.layout.form_date_time_view, null);
                            EditText timeEt = (EditText) timeView.findViewById(R.id.form_time_et);
                            timeEt.setVisibility(View.VISIBLE);

                            if (isEditable) {
                                int finalI4 = i;
                                int finalK4 = k;
                                timeEt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Utils.showTimePickerDialog(null, getActivity(), null, new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                timeEt.setText(getDisplayTime(hourOfDay, minute));
                                                dynamicFormDataBean.getData().get(finalK4).getItems().get(finalI4).setValue(getUtcFromDayMonthYear(0, 0, 0, hourOfDay, minute));
                                            }
                                        });
                                    }
                                });
                            } else {
                                timeEt.setEnabled(false);
                            }
                            if (itemsBean.getValue() != null) {
                                timeEt.setText(Utils.getFormatedTime(itemsBean.getValue()));
                            }
                            editableFormRootLl.addView(timeView);
                            break;
                        case FormsConstant.TYPE_DATE_TIME:
                            View dateTimeView = getLayoutInflater().inflate(R.layout.form_date_time_view, null);
                            EditText formDateEt = (EditText) dateTimeView.findViewById(R.id.form_date_et);
                            EditText formTimeEt = (EditText) dateTimeView.findViewById(R.id.form_time_et);
                            formDateEt.setVisibility(View.VISIBLE);
                            formTimeEt.setVisibility(View.VISIBLE);
                            formTimeEt.setEnabled(false);

                            if (isEditable) {
                                final int[] selectedYear = new int[1];
                                final int[] selectedMonth = new int[1];
                                final int[] day = new int[1];

                                formDateEt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Utils.showDatePickerDialog(getActivity(), null, Constants.TYPE_DOB, new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                                selectedYear[0] = year;
                                                selectedMonth[0] = month;
                                                day[0] = dayOfMonth;

                                                formTimeEt.setEnabled(true);
                                                formDateEt.setText(Utils.getFormatedDate(year, month, dayOfMonth));
                                            }
                                        });
                                    }
                                });

                                int finalI5 = i;
                                int finalK5 = k;
                                formTimeEt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Utils.showTimePickerDialog(null, getActivity(), null, new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                formTimeEt.setText(getDisplayTime(hourOfDay, minute));
                                                dynamicFormDataBean.getData().get(finalK5).getItems().get(finalI5).setValue(getUtcFromDayMonthYear(selectedYear[0], selectedMonth[0], day[0], hourOfDay, minute));
                                            }
                                        });
                                    }
                                });
                                if (itemsBean.getValue() != null) {
                                    formDateEt.setText(Utils.getDayMonthYear(itemsBean.getValue()));
                                    formTimeEt.setText(Utils.getFormatedTime(itemsBean.getValue()));
                                }
                            } else {
                                formDateEt.setEnabled(false);
                                formTimeEt.setEnabled(false);
                            }
                            editableFormRootLl.addView(dateTimeView);
                            break;
                    }
                }
            }
        }
        TextView requiredInfo = new TextView(getActivity());
        int paddingDefault = getResources().getDimensionPixelSize(R.dimen.padding_default);
        requiredInfo.setPadding(paddingDefault, 32, paddingDefault, paddingDefault);
        requiredInfo.setText(getString(R.string.mandatory_questions_info));
        editableFormRootLl.addView(requiredInfo);
    }

    private String getDisplayTime(int hourOfDay, int minute) {
        int hr = (hourOfDay > 12) ? hourOfDay - 12 : hourOfDay;
        String amPm = (hourOfDay > 12) ? "PM" : "AM";
        return hr + " : " + minute + " " + amPm;
    }

    public static String getUtcFromDayMonthYear(int year, int month, int day, int hour, int min) {
        String utcDate;
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, min, 0);
        utcDate = Utils.getUTCfromGMT(new Timestamp(calendar.getTimeInMillis()).toString());
        return utcDate;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onCloseActionInterface.onClose(false);
                break;
            case R.id.submit_btn:
                if (submitBtn.getText().equals(getString(R.string.submit))) {

                    if (vaidateUserInputs()) {

                        if (dynamicFormDataBean.getData() != null && !dynamicFormDataBean.getData().isEmpty()) {
                            Double score = null;

                            for (int i = 0; i < dynamicFormDataBean.getData().size(); i++) {
                                for (int j = 0; j < dynamicFormDataBean.getData().get(i).getItems().size(); j++) {
                                    if (dynamicFormDataBean.getData().get(i).getItems().get(j).getScore() != null) {
                                        if (score == null) {
                                            score = 0.0;
                                        }
                                        score = score + dynamicFormDataBean.getData().get(i).getItems().get(j).getScore();
                                    }
                                }
                            }
                            if (score != null) {
                                dynamicFormDataBean.setTotal_score(String.valueOf(score));
                            }
                        }
                        formsApiViewModel.updateForm(formsApiResponseModel.getUser_form_id(), dynamicFormDataBean, true);
                    } else {
                        showToast(getString(R.string.enter_all_mandatory_fields));
                    }
                } else if (submitBtn.getText().equals(getString(R.string.print))) {

                    PdfViewerFragment fragment = new PdfViewerFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(ArgumentKeys.IS_FROM_PRESCRIPTION_DETAIL, true);
                    bundle.putBoolean(ArgumentKeys.IS_PDF_DECRYPT, false);

                    if (formsApiResponseModel.getFilled_form_url() != null) {
                        bundle.putString(ArgumentKeys.PDF_TITLE, formsApiResponseModel.getName());
                        bundle.putString(ArgumentKeys.PDF_URL, formsApiResponseModel.getFilled_form_url());
                    } else {
                        bundle.putString(ArgumentKeys.PDF_TITLE, formsApiResponseModel.getName());
                        bundle.putString(ArgumentKeys.PDF_URL, formsApiResponseModel.getUrl());
                    }
                    ((ShowSubFragmentInterface) getActivity()).onShowFragment(fragment);
                }
                break;
            case R.id.learn_about_score_tv:
                AboutScoreBottomSheet aboutScoreBottomSheet = new AboutScoreBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putSerializable(ArgumentKeys.FORM_DETAIL, formsApiResponseModel);
                aboutScoreBottomSheet.setArguments(bundle);
                aboutScoreBottomSheet.show(getChildFragmentManager(), aboutScoreBottomSheet.getClass().getSimpleName());
                break;
        }
    }

    private boolean vaidateUserInputs() {
        boolean isValid = false;
        for (int i = 0; i < dynamicFormDataBean.getData().size(); i++) {
            for (int j = 0; j < dynamicFormDataBean.getData().get(i).getItems().size(); j++) {
                isValid = dynamicFormDataBean.getData().get(i).getItems().get(j).getProperties().isIs_required() &&
                        dynamicFormDataBean.getData().get(i).getItems().get(j).getValue() != null
                        && !dynamicFormDataBean.getData().get(i).getItems().get(j).getValue().isEmpty();

                if (!isValid) {
                    return isValid;
                }
            }
        }
        return isValid;
    }
}
