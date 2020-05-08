package com.thealer.telehealer.views.signup.doctor;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.createuser.PracticesBean;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.GetDoctorsApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Util.TimerInterface;
import com.thealer.telehealer.common.Util.TimerRunnable;
import com.thealer.telehealer.views.base.BaseBottomSheetDialogFragment;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 25,October,2018
 */
public class DoctorSelectPracticeBottomSheetFragment extends BaseBottomSheetDialogFragment {
    private ImageView closeIv;
    private TextView doctorNameTv;
    private EditText searchEt;
    private RecyclerView practiceRv;
    private TextView addNewTv;
    private OnActionCompleteInterface onActionCompleteInterface;
    private GetDoctorsApiResponseModel.DataBean doctorDetail;
    private int position;
    private List<PracticesBean> practicesBeanList = new ArrayList<>();
    private DoctorPracticeSelectAdapter doctorPracticeSelectAdapter;

    @Nullable
    private TimerRunnable uiToggleTimer;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.cloneInContext(contextThemeWrapper).inflate(R.layout.bottomsheet_doctor_select_practise, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        closeIv = (ImageView) view.findViewById(R.id.close_iv);
        doctorNameTv = (TextView) view.findViewById(R.id.doctor_name_tv);
        searchEt = (EditText) view.findViewById(R.id.search_et);
        practiceRv = (RecyclerView) view.findViewById(R.id.practice_rv);
        addNewTv = (TextView) view.findViewById(R.id.add_new_tv);

        setBottomSheetHeight(view.findViewById(R.id.parent_view), 80);

        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        addNewTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.IS_CREATE_MANUALLY, false);
                bundle.putBoolean(Constants.IS_NEW_PRACTICE, true);
                bundle.putSerializable(Constants.DOCTOR_ID, position);
                bundle.putSerializable(Constants.USER_DETAIL, doctorDetail);

                onActionCompleteInterface.onCompletionResult(null, true, bundle);
            }
        });

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable text) {
                if (uiToggleTimer != null) {
                    uiToggleTimer.setStopped(true);
                    uiToggleTimer = null;
                }

                Handler handler = new Handler();
                TimerRunnable runnable = new TimerRunnable(new TimerInterface() {
                    @Override
                    public void run() {
                        searchList(searchEt.getText().toString());
                    }
                });
                uiToggleTimer = runnable;
                handler.postDelayed(runnable, ArgumentKeys.SEARCH_INTERVAL);
            }
        });


        if (getArguments() != null) {
            doctorDetail = (GetDoctorsApiResponseModel.DataBean) getArguments().getSerializable(Constants.USER_DETAIL);
            setData();
        }

    }

    private void setData() {
        if (getArguments() != null) {

            position = getArguments().getInt(Constants.SELECTE_POSITION);

            doctorNameTv.setText(doctorDetail.getProfile().getFirst_name() + ", " + doctorDetail.getProfile().getTitle());

            practiceRv.setLayoutManager(new LinearLayoutManager(getActivity()));

            doctorPracticeSelectAdapter = new DoctorPracticeSelectAdapter(getActivity(), practicesBeanList, getDialog(), position, doctorDetail);

            practiceRv.setAdapter(doctorPracticeSelectAdapter);

            searchList("");
        }
    }

    private void searchList(String search) {
        practicesBeanList.clear();

        if (search.isEmpty()) {

            if (doctorDetail != null) {
                for (int i = 0; i < doctorDetail.getPractices().size(); i++) {
                    practicesBeanList.add(new PracticesBean(doctorDetail.getPractices().get(i).getName(),
                            doctorDetail.getPractices().get(i).getWebsite(),
                            doctorDetail.getPractices().get(i).getVisit_address(),
                            doctorDetail.getPractices().get(i).getPhones()));
                }

                doctorPracticeSelectAdapter.notifyDataSetChanged();
            }

        } else {


            for (int i = 0; i < doctorDetail.getPractices().size(); i++) {
                if (doctorDetail.getPractices().get(i).getName().toLowerCase().contains(search.trim().toLowerCase()))
                    practicesBeanList.add(new PracticesBean(doctorDetail.getPractices().get(i).getName(),
                            doctorDetail.getPractices().get(i).getWebsite(),
                            doctorDetail.getPractices().get(i).getVisit_address(),
                            doctorDetail.getPractices().get(i).getPhones()));
            }

            doctorPracticeSelectAdapter.notifyDataSetChanged();
        }
    }
}
