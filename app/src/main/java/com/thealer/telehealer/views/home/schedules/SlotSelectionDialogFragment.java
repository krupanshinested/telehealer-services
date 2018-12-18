package com.thealer.telehealer.views.home.schedules;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseBottomSheetDialogFragment;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aswin on 20,December,2018
 */
public class SlotSelectionDialogFragment extends BaseBottomSheetDialogFragment {
    private TextView titleTv;
    private NumberPicker timeNp;
    private NumberPicker dateNp;
    private Button doneBtn;

    private List<String> dateList = new ArrayList<>();
    private Map<String, List<String>> dateMap = new HashMap<>();
    private Map<String, Map<String, String>> timeMaps = new HashMap<>();
    private String[] date = null;
    private String[] time = null;
    private String selectedDate = null, selectedTime = null;
    private int position;

    private CreateScheduleViewModel createScheduleViewModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        createScheduleViewModel = ViewModelProviders.of(getActivity()).get(CreateScheduleViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.cloneInContext(contextThemeWrapper).inflate(R.layout.dialog_slot_picker, container, false);
        initView(view);
        setBottomSheetHeight(view, 50);
        return view;
    }

    private void initView(View view) {
        titleTv = (TextView) view.findViewById(R.id.title_tv);
        timeNp = (NumberPicker) view.findViewById(R.id.time_np);
        dateNp = (NumberPicker) view.findViewById(R.id.date_np);
        doneBtn = (Button) view.findViewById(R.id.done_btn);

        timeNp.setMinValue(0);
        dateNp.setMinValue(0);

        if (getArguments() != null) {
            position = getArguments().getInt(ArgumentKeys.SELECTED_TIME_SLOT);
            selectedDate = Utils.getSelectedSlotDate(createScheduleViewModel.getTimeSlots().getValue().get(position));
            selectedTime = Utils.getSelectedSlotTime(createScheduleViewModel.getTimeSlots().getValue().get(position));
        }

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int endHour = 21;
        int timeDifference = createScheduleViewModel.getDoctorCommonModel().getAppt_length();
        int endMinute = 60 - timeDifference;

        for (int i = 0; i < 30; i++) {

            List<String> timeList = new ArrayList<>();
            Map<String, String> timeMap = new HashMap<>();

            int startHour = 8;
            int startMinute = 0;

            Calendar startCal = Calendar.getInstance();
            startCal.set(year, month, day + i, startHour, startMinute, 0);
            startCal.set(Calendar.MILLISECOND, 0);

            Calendar endCal = Calendar.getInstance();
            endCal.set(year, month, day + i, endHour, endMinute, 0);
            endCal.set(Calendar.MILLISECOND, 0);


            String timeStamp = new Timestamp(startCal.getTimeInMillis()).toString();
            String date = Utils.getSlotDate(timeStamp);

            while (startCal.getTime().compareTo(endCal.getTime()) <= 0) {

                if (calendar.getTime().compareTo(startCal.getTime()) < 0) {
                    timeStamp = new Timestamp(startCal.getTimeInMillis()).toString();
                    String time = Utils.getSlotTime(timeStamp);

                    if (!createScheduleViewModel.getUnAvaliableTimeSlots().contains(Utils.getUTCfromGMT(timeStamp))) {
                        if (!timeList.contains(time)) {
                            timeList.add(time);
                        }
                        timeMap.put(time, Utils.getUTCfromGMT(timeStamp));
                    }
                }

                startCal.add(Calendar.MINUTE, timeDifference);
            }

            if (timeList.size() > 0) {
                if (!dateList.contains(date)) {
                    dateList.add(date);
                }
                dateMap.put(date, timeList);
                timeMaps.put(date, timeMap);
            }
        }


        date = dateList.toArray(new String[0]);
        dateNp.setMaxValue(date.length - 1);
        dateNp.setDisplayedValues(date);

        setTimeValue(0);

        dateNp.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setTimeValue(newVal);
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> slotList = createScheduleViewModel.getTimeSlots().getValue();
                if (slotList == null) {
                    slotList = new ArrayList<>();
                }
                if (selectedDate != null) {
                    slotList.set(position, timeMaps.get(date[dateNp.getValue()]).get(time[timeNp.getValue()]));
                } else {
                    slotList.add(timeMaps.get(date[dateNp.getValue()]).get(time[timeNp.getValue()]));
                }
                createScheduleViewModel.getTimeSlots().setValue(slotList);
                getDialog().dismiss();
            }
        });

        if (selectedDate != null) {
            for (int i = 0; i < date.length; i++) {
                if (date[i].equals(selectedDate)) {
                    dateNp.setValue(i);
                    setTimeValue(i);
                    for (int j = 0; j < timeNp.getDisplayedValues().length; j++) {
                        if (timeNp.getDisplayedValues()[j].equals(selectedTime)) {
                            timeNp.setValue(j);
                            break;
                        }
                    }
                    break;
                }
            }
        }

    }

    private void setTimeValue(int position) {
        if (dateMap.containsKey(dateList.get(position))) {
            time = dateMap.get(dateList.get(position)).toArray(new String[0]);
            timeNp.setMaxValue(0);
            timeNp.setDisplayedValues(time);
            timeNp.setMaxValue(timeNp.getDisplayedValues().length - 1);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (getTargetFragment() != null) {
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
        }
        super.onDismiss(dialog);

    }

}
