package com.thealer.telehealer.views.home.recents;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.recents.DownloadTranscriptResponseModel;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;

/**
 * Created by Aswin on 26,December,2018
 */
public class ExperimentalFeatureFragment extends BaseFragment {
    private ImageView backIv;
    private TextView toolbarTitle;
    private RecyclerView assessmentRv;
    private RecyclerView symptomsRv;
    private RecyclerView treatmentRv;
    private TextView speaker2Tv;
    private TextView speaker1Tv;

    private RecentsApiViewModel recentsApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private OnCloseActionInterface onCloseActionInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        recentsApiViewModel = ViewModelProviders.of(this).get(RecentsApiViewModel.class);
        attachObserverInterface.attachObserver(recentsApiViewModel);
        recentsApiViewModel.baseApiResponseModelMutableLiveData.observe(this,
                new Observer<BaseApiResponseModel>() {
                    @Override
                    public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                        if (baseApiResponseModel != null) {

                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_experimental_feature, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        assessmentRv = (RecyclerView) view.findViewById(R.id.assessment_rv);
        symptomsRv = (RecyclerView) view.findViewById(R.id.symptoms_rv);
        treatmentRv = (RecyclerView) view.findViewById(R.id.treatment_rv);
        speaker2Tv = (TextView) view.findViewById(R.id.speaker2_tv);
        speaker1Tv = (TextView) view.findViewById(R.id.speaker1_tv);

        toolbarTitle.setText(getString(R.string.experimental_feature));
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseActionInterface.onClose(false);
            }
        });

        if (getArguments() != null) {
            String orderId = getArguments().getString(ArgumentKeys.ORDER_ID);
            DownloadTranscriptResponseModel downloadTranscriptResponseModel = (DownloadTranscriptResponseModel) getArguments().getSerializable(ArgumentKeys.TRANSCRIPTION_DETAIL);
            String speaker1Text = "";
            String speaker2Text = "";

            if (downloadTranscriptResponseModel != null) {
                for (int i = 0; i < downloadTranscriptResponseModel.getSpeakerLabels().size(); i++) {
                    if (downloadTranscriptResponseModel.getSpeakerLabels().get(i).getSpeaker_label().equals("spk_0") ||
                            downloadTranscriptResponseModel.getSpeakerLabels().get(i).getSpeaker_label().equals("caller")) {
                        speaker1Text = speaker1Text.concat(downloadTranscriptResponseModel.getSpeakerLabels().get(i).getTranscript()).concat("\n");
                    } else {
                        speaker2Text = speaker2Text.concat(downloadTranscriptResponseModel.getSpeakerLabels().get(i).getTranscript()).concat("\n");
                    }
                }
            }

            speaker1Tv.setText(speaker1Text);
            speaker2Tv.setText(speaker2Text);
            recentsApiViewModel.getExperimentalFeatureDetail(orderId, true);
        }
    }
}
