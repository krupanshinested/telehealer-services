package com.thealer.telehealer.views.signup;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;

/**
 * Created by Aswin on 26,March,2019
 */
public class RegistrationEmailMobileVerificationFragment extends BaseFragment implements DoCurrentTransactionInterface {

    private OnActionCompleteInterface onActionCompleteInterface;
    private CreateUserRequestModel createUserRequestModel;
    private OnViewChangeInterface onViewChangeInterface;
    private TextView titleTv;
    private TextView phoneLabel;
    private TextView phoneTv;
    private TextView emailLabel;
    private TextView emailTv;
    private TextView descriptionTv;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
        createUserRequestModel = ViewModelProviders.of(getActivity()).get(CreateUserRequestModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration_email_mobile_verification, container, false);
        initView(view);
        onViewChangeInterface.enableNext(true);
        return view;
    }

    @Override
    public void doCurrentTransaction() {
        onActionCompleteInterface.onCompletionResult(null, true, null);
    }

    private void initView(View view) {
        titleTv = (TextView) view.findViewById(R.id.title_tv);
        phoneLabel = (TextView) view.findViewById(R.id.phone_label);
        phoneTv = (TextView) view.findViewById(R.id.phone_tv);
        emailLabel = (TextView) view.findViewById(R.id.email_label);
        emailTv = (TextView) view.findViewById(R.id.email_tv);
        descriptionTv = (TextView) view.findViewById(R.id.description_tv);

        phoneTv.setText(createUserRequestModel.getUser_data().getPhone());
        emailTv.setText(createUserRequestModel.getUser_data().getEmail());
    }
}
