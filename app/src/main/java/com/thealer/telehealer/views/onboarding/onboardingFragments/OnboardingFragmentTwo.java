package com.thealer.telehealer.views.onboarding.onboardingFragments;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.views.base.BaseFragment;

/**
 * Created by Aswin on 04,February,2019
 */
public class OnboardingFragmentTwo extends BaseFragment {

    private ImageView doctorIv;
    private ImageView patientIv;
    private ImageView thermometerIv;
    private TextView temperatureTv;
    private ImageView doctorWaveIv;
    private ImageView patientWaveIv;

    private boolean isResumed = false;
    private CountDownTimer waveTimer;
    private CountDownTimer temperatureTimer;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_two, container, false);
        this.view = view;
        return view;
    }

    private void initView(View view) {
        doctorIv = (ImageView) view.findViewById(R.id.doctor_iv);
        patientIv = (ImageView) view.findViewById(R.id.patient_iv);
        thermometerIv = (ImageView) view.findViewById(R.id.thermometer_iv);
        temperatureTv = (TextView) view.findViewById(R.id.temperature_tv);
        doctorWaveIv = (ImageView) view.findViewById(R.id.doctor_wave_iv);
        patientWaveIv = (ImageView) view.findViewById(R.id.patient_wave_iv);

        doctorIv.clearAnimation();
        patientIv.clearAnimation();
        thermometerIv.clearAnimation();
        doctorWaveIv.clearAnimation();
        patientWaveIv.clearAnimation();

        doctorIv.setVisibility(View.INVISIBLE);
        patientIv.setVisibility(View.INVISIBLE);
        thermometerIv.setVisibility(View.INVISIBLE);
        temperatureTv.setVisibility(View.INVISIBLE);
        doctorWaveIv.setVisibility(View.INVISIBLE);
        patientWaveIv.setVisibility(View.INVISIBLE);

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
            clearTimers();
            if (isVisibleToUser) {
                initView(view);
            }
        }
    }

    private void clearTimers() {
        if (waveTimer != null) {
            waveTimer.onFinish();
        }
        if (temperatureTimer != null) {
            temperatureTimer.cancel();
        }
    }

    private void animateDoctorPatient() {
        Animation doctorAnimation = new TranslateAnimation(200, 0, 0, 0);
        doctorAnimation.setDuration(750);
        doctorAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                doctorIv.setVisibility(View.VISIBLE);
                patientIv.setVisibility(View.VISIBLE);
                animateThermometer();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        Animation patientAnimation = new TranslateAnimation(-200, 0, 0, 0);
        patientAnimation.setDuration(750);

        doctorIv.startAnimation(doctorAnimation);
        patientIv.startAnimation(patientAnimation);
    }

    private void animateThermometer() {
        Animation animation = new TranslateAnimation(0, 0, 1000, 0);
        animation.setDuration(750);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                thermometerIv.setVisibility(View.VISIBLE);
                animateWifi();
                animateTemperature();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        thermometerIv.startAnimation(animation);
    }

    private int count;

    private void animateWifi() {

        AnimationDrawable patientWaveDrawable = (AnimationDrawable) patientWaveIv.getBackground();
        AnimationDrawable doctorWaveDrawable = (AnimationDrawable) doctorWaveIv.getBackground();

        count = 0;

        waveTimer = new CountDownTimer(3000, 750) {
            @Override
            public void onTick(long millisUntilFinished) {

                if (count == 0 || count == 2) {
                    patientWaveIv.setVisibility(View.VISIBLE);
                    doctorWaveIv.setVisibility(View.INVISIBLE);
                    patientWaveDrawable.start();
                } else {
                    patientWaveIv.setVisibility(View.INVISIBLE);
                    doctorWaveIv.setVisibility(View.VISIBLE);
                    doctorWaveDrawable.start();
                }

                count++;
            }

            @Override
            public void onFinish() {
                patientWaveIv.clearAnimation();
                doctorWaveIv.clearAnimation();

                patientWaveIv.setVisibility(View.INVISIBLE);
                doctorWaveIv.setVisibility(View.INVISIBLE);
            }
        }.start();

    }

    private int i;

    private void animateTemperature() {
        temperatureTv.setVisibility(View.VISIBLE);

        i = 100;
        setTemperature(i);

        temperatureTimer = new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                i = i + 1;
                setTemperature(i);
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    private void setTemperature(int degree) {
        temperatureTv.setText(degree + "\u2109");
    }
}
