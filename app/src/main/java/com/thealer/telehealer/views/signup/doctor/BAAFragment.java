package com.thealer.telehealer.views.signup.doctor;

import android.app.Activity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserApiViewModel;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.settings.SignatureActivity;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 28,February,2019
 */
public class BAAFragment extends BaseFragment implements DoCurrentTransactionInterface {

    private TextView titleTv;
    private CustomButton agreeBtn;
    private TextView baaInfoTv;

    private OnActionCompleteInterface onActionCompleteInterface;
    private CreateUserRequestModel createUserRequestModel;
    private CreateUserApiViewModel createUserApiViewModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        createUserRequestModel = new ViewModelProvider(getActivity()).get(CreateUserRequestModel.class);
        createUserApiViewModel = new ViewModelProvider(this).get(CreateUserApiViewModel.class);

        AttachObserverInterface attachObserverInterface = (AttachObserverInterface) getActivity();
        attachObserverInterface.attachObserver(createUserApiViewModel);

        OnViewChangeInterface onViewChangeInterface = (OnViewChangeInterface) getActivity();
        onViewChangeInterface.updateNextTitle(getString(R.string.skip));
        onViewChangeInterface.enableNext(true);

        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();

        createUserApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    CreateUserApiResponseModel createUserApiResponseModel = (CreateUserApiResponseModel) baseApiResponseModel;

                    if (createUserApiResponseModel.isSuccess()) {

                        appPreference.setString(PreferenceConstants.USER_GUID, createUserApiResponseModel.getData().getUser_guid());
                        appPreference.setString(PreferenceConstants.USER_AUTH_TOKEN, createUserApiResponseModel.getData().getToken());
                        appPreference.setString(PreferenceConstants.USER_REFRESH_TOKEN, createUserApiResponseModel.getData().getRefresh_token());
                        appPreference.setString(PreferenceConstants.USER_NAME, createUserApiResponseModel.getData().getName());

                        createUserApiViewModel.baseApiResponseModelMutableLiveData.setValue(null);

                        if (getActivity() != null) {
                            EventRecorder.recordRegistration("SIGNUP_DOCTOR_NOT_VERIFIED", createUserApiResponseModel.getData().getUser_guid());
                        }

                        Bundle bundle = new Bundle();
                        bundle.putString(ArgumentKeys.ROLE, createUserRequestModel.getUser_data().getRole());

                        onActionCompleteInterface.onCompletionResult(null, true, null);
                    }
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_baa, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        titleTv = (TextView) view.findViewById(R.id.title_tv);
        agreeBtn = (CustomButton) view.findViewById(R.id.agree_btn);
        baaInfoTv = (TextView) view.findViewById(R.id.baa_info);

        baaInfoTv.setText(Html.fromHtml(getHtml()));

        agreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showAlertDialog(getActivity(), getString(R.string.signature_capture), getString(R.string.baa_info),
                        getString(R.string.ok), null,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(ArgumentKeys.IS_CREATE_USER, true);

                                startActivityForResult(new Intent(getActivity(), SignatureActivity.class).putExtras(bundle), RequestID.REQ_SIGNATURE);
                                dialog.dismiss();
                            }
                        }, null);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestID.REQ_SIGNATURE && resultCode == Activity.RESULT_OK) {
            createUserRequestModel.setDoctor_signature_path(data.getStringExtra(ArgumentKeys.SIGNATURE_PATH));
            createUser();
        }
    }

    private void createUser() {
        createUserRequestModel.getUser_data().setRole(Constants.ROLE_DOCTOR);
        createUserRequestModel.getUser_data().setUser_name(Constants.BUILD_MEDICAL);

        createUserApiViewModel.createDoctor(createUserRequestModel);
    }

    private String getHtml() {
        String baaHtmlContent = getString(R.string.baa_content);

        String CLINIC_NAME = "#CLINIC_NAME#";
        String STATE = "#STATE#";
        String DATE = "#DATE#";
        String Company_NAME = "#COMPANY_NAME#";

        baaHtmlContent = baaHtmlContent.replace(CLINIC_NAME, createUserRequestModel.getUser_detail().getData().getClinic().getName())
                .replace(STATE, createUserRequestModel.getUser_detail().getData().getClinic().getState())
                .replace(DATE, Utils.getCurrentFomatedDate())
        .replace(Company_NAME, getString(R.string.organization_name));

        return baaHtmlContent;
    }

    @Override
    public void doCurrentTransaction() {
        agreeBtn.setEnabled(false);
        createUser();
    }
}
