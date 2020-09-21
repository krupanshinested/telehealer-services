package com.thealer.telehealer.views.signup.doctor;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.createuser.PracticesBean;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.GetDoctorsApiResponseModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.GetDoctorsApiViewModel;
import com.thealer.telehealer.common.Constants;
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
    private GetDoctorsApiViewModel getDoctorsApiViewModel;
    private GetDoctorsApiResponseModel getDoctorsApiResponseModel;
    private int position;
    private List<PracticesBean> practicesBeanList = new ArrayList<>();
    private DoctorPracticeSelectAdapter doctorPracticeSelectAdapter;

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
                searchList(text.toString());
            }
        });

        getDoctorsApiViewModel = ViewModelProviders.of(getActivity()).get(GetDoctorsApiViewModel.class);

        getDoctorsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {

                    getDoctorsApiResponseModel = (GetDoctorsApiResponseModel) baseApiResponseModel;

                    setData();
                }
            }
        });

    }

    private void setData() {
        if (getArguments() != null) {

            position = getArguments().getInt(Constants.SELECTE_POSITION);

            doctorNameTv.setText(getDoctorsApiResponseModel.getData().get(position).getProfile().getFirst_name() + ", " +
                    getDoctorsApiResponseModel.getData().get(position).getProfile().getTitle());

            practiceRv.setLayoutManager(new LinearLayoutManager(getActivity()));

            doctorPracticeSelectAdapter = new DoctorPracticeSelectAdapter(getActivity(), practicesBeanList, getDialog(), position);

            practiceRv.setAdapter(doctorPracticeSelectAdapter);

            searchList("");
        }
    }

    private void searchList(String search) {
        practicesBeanList.clear();

        if (search.isEmpty()) {

            if ( getDoctorsApiResponseModel != null && getDoctorsApiResponseModel.getData() != null) {
                practicesBeanList.addAll(getDoctorsApiResponseModel.getData().get(position).getPractices());

                doctorPracticeSelectAdapter.notifyDataSetChanged();
            }

        } else {

            GetDoctorsApiResponseModel.DataBean dataBean = getDoctorsApiResponseModel.getData().get(position);

            for (int i = 0; i < dataBean.getPractices().size(); i++) {
                if (dataBean.getPractices().get(i).getName().toLowerCase().contains(search.trim().toLowerCase()))
                    practicesBeanList.add(dataBean.getPractices().get(i));
            }

            doctorPracticeSelectAdapter.notifyDataSetChanged();
        }
    }
}
