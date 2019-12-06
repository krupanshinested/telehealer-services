package com.thealer.telehealer.views.settings.medicalHistory;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.medicalHistory.MedicalHistoryViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.views.base.BaseBottomSheetDialogFragment;

/**
 * Created by Aswin on 23,January,2019
 */
public class SelectRelationBottomSheetDialogFragment extends BaseBottomSheetDialogFragment implements View.OnClickListener {
    private TextView titleTv;
    private RecyclerView relationRv;
    private Button doneBtn;
    private Button deselectBtn;

    private MedicalHistoryViewModel medicalHistoryViewModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        medicalHistoryViewModel = new ViewModelProvider(getActivity()).get(MedicalHistoryViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.cloneInContext(contextThemeWrapper).inflate(R.layout.bottomsheet_relation_selection, container, false);
        setBottomSheetHeight(view, 75);
        initView(view);
        getDialog().getWindow().setBackgroundDrawable(getActivity().getDrawable(android.R.drawable.screen_background_dark_transparent));
        return view;
    }

    private void initView(View view) {
        titleTv = (TextView) view.findViewById(R.id.title_tv);
        relationRv = (RecyclerView) view.findViewById(R.id.relation_rv);
        doneBtn = (Button) view.findViewById(R.id.done_btn);
        deselectBtn = (Button) view.findViewById(R.id.deselect_btn);

        if (getArguments() != null) {
            String title = getArguments().getString(ArgumentKeys.TITLE);
            titleTv.setText(title);
        }

        relationRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        relationRv.setAdapter(new RelationListAdapter(getActivity()));

        doneBtn.setOnClickListener(this);
        deselectBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.done_btn:
                medicalHistoryViewModel.setDeselected(false);
                break;
            case R.id.deselect_btn:
                medicalHistoryViewModel.setDeselected(true);
                break;
        }

        medicalHistoryViewModel.getOnActionLiveData().setValue(true);

        dismiss();
    }
}
