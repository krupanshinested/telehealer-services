package com.thealer.telehealer.views.signup.doctor;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.views.base.BaseBottomSheetDialogFragment;

/**
 * Created by Aswin on 25,October,2018
 */
public class DoctorBioBottomSheetFragment extends BaseBottomSheetDialogFragment {
    private TextView doneTv;
    private EditText bioBottomsheetEt;
    private CreateUserRequestModel createUserRequestModel;
    private boolean isNewBio;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.cloneInContext(contextThemeWrapper).inflate(R.layout.bottomsheet_doctor_bio, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        createUserRequestModel = ViewModelProviders.of(getActivity()).get(CreateUserRequestModel.class);

        isNewBio = createUserRequestModel.getUser_detail().getData().getBio() == null;

        if (createUserRequestModel.getUser_detail().getData().getBio() != null) {
            bioBottomsheetEt.setText(createUserRequestModel.getUser_detail().getData().getBio());
        }

    }

    private void initView(View view) {
        doneTv = (TextView) view.findViewById(R.id.done_tv);
        bioBottomsheetEt = (EditText) view.findViewById(R.id.bio_bottomsheet_et);

        doneTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUserRequestModel.getUser_detail().getData().setBio(bioBottomsheetEt.getText().toString());
                getTargetFragment().onActivityResult(getTargetRequestCode(), RequestID.REQ_BIO, null);
                getDialog().dismiss();
            }
        });


        View v = view.findViewById(R.id.parent_view);
        setBottomSheetHeight(v);

    }
}
