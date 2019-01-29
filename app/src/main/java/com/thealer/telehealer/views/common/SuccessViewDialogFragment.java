package com.thealer.telehealer.views.common;

import android.animation.Animator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.views.base.BaseDialogFragment;

/**
 * Created by Aswin on 01,November,2018
 */
public class SuccessViewDialogFragment extends BaseDialogFragment {
    private SuccessViewInterface successViewInterface;
    private ImageView loaderIv;
    private TextView titleTv;
    private TextView messageTv;
    private CustomButton doneBtn;
    private boolean status;
    private String title;
    private String message;
    private Animatable2 animatable2;
    private ImageView preloaderIv;
    private boolean isDataReceived = false;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onDataUpdated(intent.getExtras());
        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        successViewInterface = (SuccessViewInterface) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_success_view, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        loaderIv = (ImageView) view.findViewById(R.id.loader_iv);
        titleTv = (TextView) view.findViewById(R.id.title_tv);
        messageTv = (TextView) view.findViewById(R.id.message_tv);
        doneBtn = (CustomButton) view.findViewById(R.id.done_btn);
        preloaderIv = (ImageView) view.findViewById(R.id.preloader_iv);

        titleTv.setText("Loading");
        messageTv.setText("Please wait...");
        doneBtn.setVisibility(View.GONE);

        animatable2 = (Animatable2) loaderIv.getDrawable();

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                successViewInterface.onSuccessViewCompletion(status);
                getDialog().dismiss();
                if (getTargetFragment() != null) {
                    if (status)
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
                    else
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
                }
            }
        });

        if (getArguments() != null) {
            onDataUpdated(getArguments());
        }
    }

    public void onDataUpdated(Bundle bundle) {
        if (bundle != null) {
            status = bundle.getBoolean(Constants.SUCCESS_VIEW_STATUS);
            title = bundle.getString(Constants.SUCCESS_VIEW_TITLE);
            message = bundle.getString(Constants.SUCCESS_VIEW_DESCRIPTION);
            setData();
        }
    }

    private void setData() {
        doneBtn.setText(status ? "done" : "Retry");
        isDataReceived = true;
    }

    private void animatePreLoader() {
        Animation animation = new TranslateAnimation(0, 0, 500, 0);
        animation.setDuration(1250);
        preloaderIv.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startLoaderAnimation();
                preloaderIv.animate().alpha(0.0f).setDuration(1500).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        loaderIv.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    private void startLoaderAnimation() {
        titleTv.setAlpha(0f);
        messageTv.setAlpha(0f);
//        loaderIv.setAlpha(0f);

        titleTv.setVisibility(View.VISIBLE);
        messageTv.setVisibility(View.VISIBLE);

        titleTv.animate().alpha(1).setDuration(200);
        messageTv.animate().alpha(1).setDuration(500);

        animatable2.registerAnimationCallback(new Animatable2.AnimationCallback() {
            @Override
            public void onAnimationEnd(Drawable drawable) {
                super.onAnimationEnd(drawable);
                if (!isDataReceived) {
                    animatable2.start();
                } else {
                    stopLoaderAnimation(status);
                }
            }
        });
        animatable2.start();
    }

    private void stopLoaderAnimation(boolean status) {
        if (animatable2 != null && animatable2.isRunning()) {
            animatable2.stop();
        }
        if (status) {
            loaderIv.setImageDrawable(getActivity().getDrawable(R.drawable.success_animation_drawable));
        } else {
            loaderIv.setImageDrawable(getActivity().getDrawable(R.drawable.failure_animation_drawable));
        }
        ((Animatable) loaderIv.getDrawable()).start();
        titleTv.setText(title);
        messageTv.setText(message);

        doneBtn.setAlpha(0f);
        doneBtn.setVisibility(View.VISIBLE);
        doneBtn.animate().alpha(1).setDuration(500);

    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(getString(R.string.success_broadcast_receiver)));
        animatePreLoader();
    }
}
