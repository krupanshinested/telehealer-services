package com.thealer.telehealer.views.onboarding.onboardingFragments;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.views.base.BaseFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jaygoo.widget.wlv.WaveLineView;

/**
 * Created by Aswin on 04,February,2019
 */
public class OnboardingFragmentThree extends BaseFragment {
    private WaveLineView waveline;
    private RecyclerView speechRv;
    private ImageView doctorIv;
    private ImageView doctorWaveIv;
    private ImageView patientWaveIv;
    private ImageView patientIv;

    private CountDownTimer waveTimer;
    private boolean isResumed;
    private OnboardingConversationAdapter onboardingConversationAdapter;
    private List<String> conversations;
    private int count = 0;

    private View view;
    private List<CountDownTimer> countDownTimerList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        conversations = Arrays.asList(getString(R.string.onboarding_conversation_one),
                getString(R.string.onboarding_conversation_two),
                getString(R.string.onboarding_conversation_three),
                getString(R.string.onboarding_conversation_four),
                getString(R.string.onboarding_conversation_five),
                getString(R.string.onboarding_conversation_six));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_three, container, false);
        this.view = view;
        return view;
    }

    private void initView(View view) {
        waveline = (WaveLineView) view.findViewById(R.id.waveline);
        speechRv = (RecyclerView) view.findViewById(R.id.speech_rv);
        doctorIv = (ImageView) view.findViewById(R.id.doctor_iv);
        doctorWaveIv = (ImageView) view.findViewById(R.id.doctor_wave_iv);
        patientWaveIv = (ImageView) view.findViewById(R.id.patient_wave_iv);
        patientIv = (ImageView) view.findViewById(R.id.patient_iv);

        waveline.stopAnim();
        doctorIv.clearAnimation();
        patientIv.clearAnimation();
        doctorWaveIv.clearAnimation();
        patientWaveIv.clearAnimation();

        doctorIv.setVisibility(View.INVISIBLE);
        patientIv.setVisibility(View.INVISIBLE);
        doctorWaveIv.setVisibility(View.INVISIBLE);
        patientWaveIv.setVisibility(View.INVISIBLE);
        speechRv.setVisibility(View.INVISIBLE);

        count = 1;

        onboardingConversationAdapter = new OnboardingConversationAdapter(getActivity());
        speechRv.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        speechRv.setAdapter(onboardingConversationAdapter);
        speechRv.setVisibility(View.VISIBLE);

        animateDoctorPatient();

    }

    @Override
    public void onResume() {
        super.onResume();
        isResumed = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed) {

            resetView();

            if (isVisibleToUser) {
                initView(view);
            }
        }
    }

    private void resetView() {
        if (waveTimer != null) {
            waveTimer.onFinish();
        }

        if (countDownTimerList != null) {
            for (int i = 0; i < countDownTimerList.size(); i++) {
                countDownTimerList.get(i).cancel();
            }
        }
    }

    private void animateDoctorPatient() {

        Animation doctorAnimation = new TranslateAnimation(0, 0, 500, 0);
        doctorAnimation.setDuration(500);
        Animation patientAnimation = new TranslateAnimation(0, 0, 500, 0);
        patientAnimation.setDuration(500);

        patientAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                patientIv.setVisibility(View.VISIBLE);
                doctorIv.setVisibility(View.VISIBLE);

                animateWaves();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        doctorIv.startAnimation(doctorAnimation);
        patientIv.startAnimation(patientAnimation);
    }

    private void animateWaves() {

        AnimationDrawable patientWaveDrawable = (AnimationDrawable) patientWaveIv.getBackground();
        AnimationDrawable doctorWaveDrawable = (AnimationDrawable) doctorWaveIv.getBackground();

        waveline.setVolume(100);
        waveline.startAnim();

        count = 1;

        countDownTimerList = new ArrayList<>();

        waveTimer = new CountDownTimer(4500, 750) {
            @Override
            public void onTick(long millisUntilFinished) {

                if (count % 2 == 0) {
                    doctorWaveIv.setVisibility(View.VISIBLE);
                    doctorWaveDrawable.start();
                    patientWaveIv.setVisibility(View.INVISIBLE);
                } else {
                    patientWaveIv.setVisibility(View.VISIBLE);
                    patientWaveDrawable.start();
                    doctorWaveIv.setVisibility(View.INVISIBLE);
                }

                countDownTimerList.add(new CountDownTimer(750, 750) {
                    int position = count - 1;

                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        if (onboardingConversationAdapter != null && position < conversations.size()) {
                            onboardingConversationAdapter.addConversation(conversations.get(position));
                            speechRv.scrollToPosition(position);
                        }
                    }
                }.start());

                count++;
            }

            @Override
            public void onFinish() {
                waveline.setVolume(10);
                patientWaveIv.setVisibility(View.INVISIBLE);
                doctorWaveIv.setVisibility(View.INVISIBLE);
            }
        }.start();
    }
}
