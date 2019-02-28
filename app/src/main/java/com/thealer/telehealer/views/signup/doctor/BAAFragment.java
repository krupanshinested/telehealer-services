package com.thealer.telehealer.views.signup.doctor;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
        createUserRequestModel = ViewModelProviders.of(getActivity()).get(CreateUserRequestModel.class);
        createUserApiViewModel = ViewModelProviders.of(this).get(CreateUserApiViewModel.class);

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
                showAlertDialog(getActivity(), getString(R.string.signature_capture), getString(R.string.baa_info),
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
        String baaHtmlContent = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "    <body>\n" +
                "        <font face=\"Helvetica Neue\" color = \"black\" size=\"5\">\n" +
                "    <p><strong>ARTICLE I</strong></p>\n" +
                "<p><strong>Preamble and</strong><strong> Definitions</strong></p>\n" +
                "<p><u>Business Associate</u>. &ldquo;Business Associate&rdquo; shall generally have the same meaning as the term &ldquo;Business Associate&rdquo; at 45 CFR 160.103, and in reference to the party to this agreement, shall mean Telehealer, Inc.</p>\n" +
                "<p><u>Covered Entity</u>. &ldquo;Covered Entity&rdquo; shall generally have the same meaning as the term &ldquo;Covered Entity&rdquo; at 45 CFR 160.103, and in reference to the party to this agreement, shall mean #CLINIC_NAME#.</p>\n" +
                "<p><u>HIPAA Rules</u>. &ldquo;HIPAA Rules&rdquo; shall mean the Privacy, Security, Breach Notification, and Enforcement Rules at 45 CFR Part 160 and Part 164.</p>\n" +
                "<p>Pursuant to the Health Insurance Portability and Accountability Act of 1996 (as amended, and including its promulgating regulations, &ldquo;<strong>HIPAA</strong>&rdquo;), #CLINIC_NAME#, (&ldquo;<strong>Covered Entity</strong>&rdquo;), a <strong>#STATE#</strong> Corporation, and Telehealer, Inc. (&ldquo;<strong>Business Associate</strong>&rdquo;), a California corporation, enter into this Business Associate Agreement (&ldquo;<strong>BAA</strong>&rdquo;) on <strong>#DATE#</strong>, which addresses the HIPAA requirements with respect to &ldquo;Business Associates,&rdquo; as defined under the privacy, security, breach notification, and enforcement rules at 45 C.F.R. Part 160 and Part 164 (&ldquo;<strong>HIPAA Rules</strong>&rdquo;). A reference in this BAA to a section in the HIPAA Rules means the section as in effect or as amended. This BAA shall become effective as of the date on which Business Associate receives, becomes knowledgeable or aware of, maintains, uses, discloses, or otherwise processes any PHI (as defined below) for or on behalf of Covered Entity or any of its contractors (the &ldquo;<strong>Effective Date</strong>&rdquo;).</p>\n" +
                "<p>This BAA is intended to ensure that Business Associate will establish and implement appropriate safeguards for the Protected Health Information (as defined under the HIPAA Rules, &ldquo;<strong>PHI</strong>&rdquo;) that Business Associate may receive, create, maintain, use, or disclose in connection with the functions, activities, and services that Business Associate performs for or on behalf of Covered Entity or any of its contractors.</p>\n" +
                "<p>Pursuant to changes required under the Health Information Technology for Economic and Clinical Health Act of 2009 (the &ldquo;<strong>HITECH Act</strong>&rdquo;) and the American Recovery and Reinvestment Act of 2009 (&ldquo;<strong>ARRA</strong>&rdquo;), this BAA also reflects federal breach notification requirements imposed on Business Associate when any &ldquo;Unsecured PHI&rdquo; (as defined under the HIPAA Rules) is acquired by an unauthorized party, as well as the expanded privacy and security provisions generally imposed on Business Associates.</p>\n" +
                "<p>Unless the context clearly indicates otherwise, the following terms in this BAA shall have the same meaning as those terms in the HIPAA Rules: Breach, Data Aggregation, Designated Record Set, Disclosure, Health Care Operations, Individual, Minimum Necessary, Notice of Privacy Practices, Protected Health Information, Required By Law, Secretary, Security Incident, Subcontractor, Unsecured Protected Health Information, and Use.</p>\n" +
                "<p>A reference in this BAA to the Privacy Rule means the Privacy Rule, in conformity with the regulations at 45 C.F.R. Parts 160-164 (the &ldquo;<strong>Privacy Rule</strong>&rdquo;) as interpreted under applicable regulations and guidance of general application published by the U.S. Department of Health and Human Services (&ldquo;<strong>HHS</strong>&rdquo;), including all amendments thereto for which compliance is required, as amended or supplemented by the HITECH Act, ARRA, and the HIPAA Rules.</p>\n" +
                "<p><strong>ARTICLE II</strong></p>\n" +
                "<p><strong>General Obligations and Activities of Business Associate</strong></p>\n" +
                "<p>Business Associate agrees to:</p>\n" +
                "<p>(a) Not use or disclose protected health information other than as permitted or required by the Agreement or as required by law;</p>\n" +
                "<p>(b) Use appropriate safeguards, and comply with Subpart C of 45 CFR Part 164 with respect to electronic protected health information, to prevent use or disclosure of protected health information other than as provided for by the Agreement;</p>\n" +
                "<p>(c) Report to Covered Entity any use or disclosure of protected health information not provided for by the Agreement of which it becomes aware, including breaches of unsecured protected health information as required at 45 CFR 164.410, and any security incident of which it becomes aware;</p>\n" +
                "<p>(d) In accordance with 45 CFR 164.502(e)(1)(ii) and 164.308(b)(2), if applicable, ensure that any subcontractors that create, receive, maintain, or transmit protected health information on behalf of the Business Associate agree to the same restrictions, conditions, and requirements that apply to the Business Associate with respect to such information;</p>\n" +
                "<p>(e) If required by law, make available protected health information in a designated record set to the &ldquo;Covered Entity&rdquo; as necessary to satisfy Covered Entity&rsquo;s obligations under 45 CFR 164.524;</p>\n" +
                "<p>(f) Make any amendment(s) to protected health information in a designated record set as directed or agreed to by the Covered Entity pursuant to 45 CFR 164.526, or take other measures as necessary to satisfy Covered Entity&rsquo;s obligations under 45 CFR 164.526;</p>\n" +
                "<p>(g) Maintain and make available the information required to provide an accounting of disclosures to the [Choose either &ldquo;Covered Entity&rdquo; or &ldquo;individual&rdquo;] as necessary to satisfy Covered Entity&rsquo;s obligations under 45 CFR 164.528;</p>\n" +
                "<p>(h) To the extent the Business Associate is to carry out one or more of Covered Entity's obligation(s) under Subpart E of 45 CFR Part 164, comply with the requirements of Subpart E that apply to the Covered Entity in the performance of such obligation(s); and</p>\n" +
                "<p>(i) Make its internal practices, books, and records available to the Secretary for purposes of determining compliance with the HIPAA Rules.</p>\n" +
                "<p>&nbsp;<strong>ARTICLE III</strong></p>\n" +
                "<p><strong>Permitted Uses and Disclosures by Business Associate</strong></p>\n" +
                "<p>(a) Business Associate agrees to receive, create, use, or disclose PHI only in a manner that is consistent with this BAA, the Privacy Rule, or Security Rule and only in connection with providing services to Covered Entity; <u>provided</u> that the use or disclosure would not violate the Privacy Rule, including 45 C.F.R. 164.504(e), if the use or disclosure would be done by Covered Entity. For example, the use and disclosure of PHI will be permitted for &ldquo;treatment, payment, and health care operations,&rdquo; in accordance with the Privacy Rule.</p>\n" +
                "<p>(b) Business Associate is authorized to use protected health information to de-identify the information in accordance with 45 CFR 164.514(a)-(c).</p>\n" +
                "<p>(c) Business Associate may use or disclose protected health information as required by law.</p>\n" +
                "<p>(d) Business Associate agrees to make uses and disclosures and requests for protected health information consistent with Covered Entity&rsquo;s minimum necessary policies and procedures.</p>\n" +
                "<p>(e) Business Associate may not use or disclose protected health information in a manner that would violate Subpart E of 45 CFR Part 164 if done by Covered Entity except for the specific uses and disclosures set forth below -</p>\n" +
                "<p>(i) Business Associate may use protected health information for the proper management and administration of the Business Associate or to carry out the legal responsibilities of the Business Associate.</p>\n" +
                "<p>(ii) Business Associate may disclose protected health information for the proper management and administration of Business Associate or to carry out the legal responsibilities of the Business Associate, provided the disclosures are required by law, or Business Associate obtains reasonable assurances from the person to whom the information is disclosed that the information will remain confidential and used or further disclosed only as required by law or for the purposes for which it was disclosed to the person, and the person notifies Business Associate of any instances of which it is aware in which the confidentiality of the information has been breached.</p>\n" +
                "<p>(iii) Business Associate may provide data aggregation services relating to the health care operations of the Covered Entity.</p>\n" +
                "<p>&nbsp;<strong>ARTICLE III</strong></p>\n" +
                "<p><strong>Obligations of Covered Entity</strong></p>\n" +
                "<p>&nbsp;(a) Covered Entity shall notify Business Associate of any limitation(s) in the notice of privacy practices of Covered Entity under 45 CFR 164.520, to the extent that such limitation may affect Business Associate&rsquo;s use or disclosure of protected health information.</p>\n" +
                "<p>(b) Covered Entity shall notify Business Associate of any changes in, or revocation of, the permission by an individual to use or disclose his or her protected health information, to the extent that such changes may affect Business Associate&rsquo;s use or disclosure of protected health information.</p>\n" +
                "<p>(c) Covered Entity shall notify Business Associate of any restriction on the use or disclosure of protected health information that Covered Entity has agreed to or is required to abide by under 45 CFR 164.522, to the extent that such restriction may affect Business Associate&rsquo;s use or disclosure of protected health information.</p>\n" +
                "<p>(d) Covered Entity shall not request Business Associate to use or disclose protected health information in any manner that would not be permissible under Subpart E of 45 CFR Part 164 if done by Covered Entity, except as provided under Article III of this BAA</p>\n" +
                "<p>&nbsp;<strong>ARTICLE IV</strong></p>\n" +
                "<p><strong>Term and Termination</strong></p>\n" +
                "<ul>\n" +
                "<li><u>Term</u>. The Term of this Agreement shall be effective as of the effective date, and shall terminate on the earlier of the date that:\n" +
                "<ol>\n" +
                "<li>Either party terminates this BAA for cause as authorized under Article IV Section (b)</li>\n" +
                "<li>All of the PHI received from Covered Entity or any of its contractors or End Users (as defined in the Underlying Agreement), or created or received by Business Associate on behalf of Covered Entity, is destroyed or returned to Covered Entity</li>\n" +
                "</ol>\n" +
                "</li>\n" +
                "</ul>\n" +
                "<p>(b)&nbsp;<u>Termination for Cause</u>. Business Associate authorizes termination of this Agreement by Covered Entity, if Covered Entity determines Business Associate has violated a material term of the Agreement and Business Associate has not cured the breach or ended the violation within a reasonable time frame not to exceed sixty (60) days from the notification of the breach.</p>\n" +
                "<p>(c)&nbsp;<u>Obligations of Business Associate Upon Termination</u>.</p>\n" +
                "<p>Upon termination of this Agreement for any reason, Business Associate shall return to Covered Entity, or if expressly agreed to by Covered Entity, destroy all protected health information received from Covered Entity, or created, maintained, or received by Business Associate on behalf of Covered Entity, that the Business Associate still maintains in any form. Business Associate shall retain no copies of the protected health information.&nbsp;</p>\n" +
                "<p>&nbsp;<strong>ARTICLE V</strong></p>\n" +
                "<p><strong>Miscellaneous</strong></p>\n" +
                "<ul>\n" +
                "<li><u>Amendment</u>. The Parties agree to take such action as is necessary to amend this Agreement from time to time as is necessary for compliance with the requirements of the HIPAA Rules and any other applicable law.</li>\n" +
                "<li>This BAA will be binding on the permitted successors and permitted assigns of Customer and the Business Associate. However, this BAA may not be assigned, in whole or in part, without the written consent of the other party. Any attempted assignment in violation of this provision shall be null and void.</li>\n" +
                "<li>This BAA may be executed in two or more counterparts, each of which shall be deemed an original.</li>\n" +
                "</ul>\n" +
                "</font>\n" +
                "    </body>\n" +
                "</html>\n";

        String CLINIC_NAME = "#CLINIC_NAME#";
        String STATE = "#STATE#";
        String DATE = "#DATE#";

        baaHtmlContent = baaHtmlContent.replace(CLINIC_NAME, createUserRequestModel.getUser_detail().getData().getClinic().getName())
                .replace(STATE, createUserRequestModel.getUser_detail().getData().getClinic().getState())
                .replace(DATE, Utils.getCurrentFomatedDate());

        return baaHtmlContent;
    }

    @Override
    public void doCurrentTransaction() {
        createUser();
    }
}
