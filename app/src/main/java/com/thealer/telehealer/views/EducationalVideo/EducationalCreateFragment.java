package com.thealer.telehealer.views.EducationalVideo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.AppBarLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalFetchModel;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalVideo;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalVideoOrder;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalVideoRequest;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalVideoViewModel;
import com.thealer.telehealer.apilayer.models.OpenTok.CallRequest;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.OpenTok.CallManager;
import com.thealer.telehealer.common.OpenTok.OpenTok;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.call.CallActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;

import java.util.UUID;

import static com.thealer.telehealer.TeleHealerApplication.application;

public class EducationalCreateFragment extends BaseFragment {

    private Button record_bt;
    private EditText title_et,description_et;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle,next_tv;

    @Nullable
    private EducationalVideo educationalVideo;
    @Nullable
    private EducationalVideoViewModel educationalVideoViewModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        educationalVideoViewModel = new ViewModelProvider(this).get(EducationalVideoViewModel.class);
        addObservers();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_educational_create, container, false);

        if (getActivity() instanceof AttachObserverInterface) {
            ((AttachObserverInterface)getActivity()).attachObserver(educationalVideoViewModel);
        }

        if (getArguments() != null) {
            Object educationalVideoObject =  getArguments().getSerializable(ArgumentKeys.EDUCATIONAL_VIDEO);
            if (educationalVideoObject != null) {
                if (educationalVideoObject instanceof EducationalVideo) {
                    educationalVideo = (EducationalVideo) educationalVideoObject;
                } else if (educationalVideoObject instanceof EducationalVideoOrder) {
                    educationalVideo = ((EducationalVideoOrder) educationalVideoObject).getVideo();
                }
            }
        }

        initView(view);
        return view;
    }

    private void initView(View view) {
        record_bt = view.findViewById(R.id.record_bt);
        title_et = view.findViewById(R.id.title_et);
        description_et = view.findViewById(R.id.description_et);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        next_tv = view.findViewById(R.id.next_tv);

        initListeners();

        if (educationalVideo != null) {
            record_bt.setText(getString(R.string.update));
            title_et.setText(educationalVideo.getTitle());
            description_et.setText(educationalVideo.getDescription());
            toolbarTitle.setText(getString(R.string.update_educational_video));
        } else {
            toolbarTitle.setText(getString(R.string.new_educational_video));
        }

        next_tv.setVisibility(View.GONE);

        updateRecordButton();
    }

    private void addObservers() {
        educationalVideoViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel instanceof EducationalFetchModel) {
                    EducationalFetchModel model = (EducationalFetchModel) baseApiResponseModel;
                    CallRequest callRequest = new CallRequest(UUID.randomUUID().toString(),
                            UserDetailPreferenceManager.getUser_guid(),null,null,null,model.getVideoId()+"", OpenTokConstants.education,true,model.getVideoId()+"");
                    callRequest.setEducationTitle(title_et.getText().toString());
                    callRequest.setEducationDescription(description_et.getText().toString());
                    model.recording_enabled = true;
                    model.transcription_enabled = true;
                    callRequest.update(model);

                    OpenTok tokBox =new OpenTok(callRequest);
                    tokBox.connectToSession();
                    CallManager.shared.addCall(tokBox);

                    Intent intent = CallActivity.getIntent(application, callRequest);
                    application.startActivity(intent);

                    getActivity().onBackPressed();
                } else {

                    if (getArguments() != null) {
                        Object educationalVideoObject =  getArguments().getSerializable(ArgumentKeys.EDUCATIONAL_VIDEO);

                        if (educationalVideoObject != null) {
                            if (educationalVideoObject instanceof EducationalVideo) {
                                EducationalVideo educationalVideo = (EducationalVideo) educationalVideoObject;
                                educationalVideo.setDescription(description_et.getText().toString());
                                educationalVideo.setTitle(title_et.getText().toString());
                                getArguments().putSerializable(ArgumentKeys.EDUCATIONAL_VIDEO,educationalVideo);
                            } else if (educationalVideoObject instanceof EducationalVideoOrder) {
                                EducationalVideoOrder educationalVideo = ((EducationalVideoOrder) educationalVideoObject);
                                educationalVideo.getVideo().setTitle(title_et.getText().toString());
                                educationalVideo.getVideo().setDescription(description_et.getText().toString());
                                getArguments().putSerializable(ArgumentKeys.EDUCATIONAL_VIDEO,educationalVideo);
                            }
                        }

                    }

                    if (getActivity() != null)
                        getActivity().onBackPressed();
                }
            }
        });
    }

    private void initListeners() {
        title_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateRecordButton();
            }
        });

        description_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateRecordButton();
            }
        });


        record_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentUserGuid=UserDetailPreferenceManager.getUser_guid()!=null?UserDetailPreferenceManager.getUser_guid():"";
                if(!UserType.isUserAssistant()){
                    currentUserGuid="";
                }
                if (educationalVideo != null) {
                    educationalVideoViewModel.updateEducationalVideo(currentUserGuid,title_et.getText().toString(),description_et.getText().toString(),educationalVideo.getVideo_id());
                } else {
                   educationalVideoViewModel.postEducationalVideo(currentUserGuid,new EducationalVideoRequest(title_et.getText().toString(),description_et.getText().toString()));
                }
            }
        });

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }


    private void updateRecordButton() {
        if (TextUtils.isEmpty(title_et.getText().toString().trim()) || TextUtils.isEmpty(description_et.getText().toString().trim())) {
            record_bt.setEnabled(false);
        } else {
            record_bt.setEnabled(true);
        }
    }

}
