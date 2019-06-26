package com.thealer.telehealer.views.settings.medicalHistory;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.medicalHistory.MedicalHistoryViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.views.base.BaseBottomSheetDialogFragment;

/**
 * Created by Aswin on 24,January,2019
 */
public class MedicalHistoryOptionsSelecteDialogFragment extends BaseBottomSheetDialogFragment {

    private TextView titleTv;
    private NumberPicker picker;
    private String[] pickerList;
    private Button doneBtn;

    private MedicalHistoryViewModel medicalHistoryViewModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        medicalHistoryViewModel = ViewModelProviders.of(getActivity()).get(MedicalHistoryViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.cloneInContext(contextThemeWrapper).inflate(R.layout.dialog_mh_option_select, container, false);
        initView(view);
        setCancelable(false);
        return view;
    }

    private void initView(View view) {
        titleTv = (TextView) view.findViewById(R.id.title_tv);
        picker = (NumberPicker) view.findViewById(R.id.picker);
        doneBtn = (Button) view.findViewById(R.id.done_btn);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medicalHistoryViewModel.setSelectedOption(pickerList[picker.getValue()]);
                medicalHistoryViewModel.getOnActionLiveData().setValue(true);
                dismiss();
            }
        });

        if (getArguments() != null) {
            titleTv.setText(getArguments().getString(ArgumentKeys.TITLE));
            int listType = getArguments().getInt(ArgumentKeys.LIST);

            switch (listType) {
                case MedicalHistoryConstants.LIST_YES_OR_NO:
                    pickerList = MedicalHistoryConstants.yesOrNoList;
                    break;
                case MedicalHistoryConstants.LIST_SEXUALLY_ACTIVE_WITH:
                    pickerList = MedicalHistoryConstants.sexWithList;
                    break;
                case MedicalHistoryConstants.LIST_YES_NO_BOTH:
                    pickerList = MedicalHistoryConstants.yesNoBoth;
                    break;
                case MedicalHistoryConstants.LIST_NOT_WORKING_REASON:
                    pickerList = MedicalHistoryConstants.notWorkingReasonList;
                    break;
                case MedicalHistoryConstants.LIST_EDUCATION_LEVEL:
                    pickerList = MedicalHistoryConstants.educationLevelsList;
                    break;
                case MedicalHistoryConstants.LIST_MARITAL_STATUS:
                    pickerList = MedicalHistoryConstants.maritalStatusList;
                    break;
            }

            picker.setDisplayedValues(pickerList);
            picker.setMinValue(0);
            picker.setMaxValue(pickerList.length - 1);

        }
    }
}
