package com.thealer.telehealer.views.common;

import android.animation.Animator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.views.base.BaseDialogFragment;

import java.util.ArrayList;

/**
 * Created by Aswin on 01,November,2018
 */
public class SuccessViewDialogFragment extends BaseDialogFragment {
    protected SuccessViewInterface successViewInterface;
    protected ImageView loaderIv;
    protected TextView titleTv;
    protected TextView messageTv;
    protected CustomButton doneBtn;
    protected boolean status;
    protected String title;
    protected String message;
    protected Animatable2 animatable2;
    protected ImageView preloaderIv;
    protected boolean isDataReceived = false;
    protected boolean auto_dismiss = false;
    protected boolean isAnimationEnd = false;
    protected boolean needToShowDoneButtonOnResultFetched = true;

    @Nullable
    protected Integer successReplaceDrawableId = null;
    @Nullable
    protected Integer successReplaceTintColor = null;

    protected BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("aswin", "onReceive: ");
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
    protected void initView(View view) {
        loaderIv = (ImageView) view.findViewById(R.id.loader_iv);
        titleTv = (TextView) view.findViewById(R.id.title_tv);
        messageTv = (TextView) view.findViewById(R.id.message_tv);
        doneBtn = (CustomButton) view.findViewById(R.id.done_btn);
        preloaderIv = (ImageView) view.findViewById(R.id.preloader_iv);

        if (getArguments() != null) {
            onDataUpdated(getArguments());
        } else {
            titleTv.setText(getString(R.string.loading));
            messageTv.setText(getString(R.string.please_wait));
        }

        doneBtn.setVisibility(View.GONE);

        animatable2 = (Animatable2) loaderIv.getDrawable();

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissScreen();
            }
        });
    }

    protected void onDataUpdated(Bundle bundle) {
        if (bundle != null) {
            title = bundle.getString(Constants.SUCCESS_VIEW_TITLE);
            message = bundle.getString(Constants.SUCCESS_VIEW_DESCRIPTION);
            int drawableId = bundle.getInt(Constants.SUCCESS_VIEW_SUCCESS_IMAGE,0);
            if (drawableId != 0) {
                successReplaceDrawableId = drawableId;
            } else {
                successReplaceDrawableId = null;
            }

            int colorId = bundle.getInt(Constants.SUCCESS_VIEW_SUCCESS_IMAGE_TINT,0);
            if (drawableId != 0) {
                successReplaceTintColor = colorId;
            } else {
                successReplaceTintColor = null;
            }

            if (bundle.containsKey(Constants.SUCCESS_VIEW_STATUS)) {
                status = bundle.getBoolean(Constants.SUCCESS_VIEW_STATUS);
                setData();
            } else {
                titleTv.setText(title);
                messageTv.setText(message);
            }

            needToShowDoneButtonOnResultFetched = !bundle.getBoolean(Constants.SUCCESS_VIEW_DONE_BUTTON,false);

            if (bundle.getBoolean(Constants.SUCCESS_VIEW_AUTO_DISMISS,false)) {
                auto_dismiss = true;
                if (isAnimationEnd) {
                    dismissScreen();
                }
            }
        }
    }

    private void setData() {
        doneBtn.setText(status ? getString(R.string.done) : getString(R.string.retry));
        isDataReceived = true;
    }

    protected void dismissScreen() {
        successViewInterface.onSuccessViewCompletion(status);
        getDialog().dismiss();
        if (getTargetFragment() != null) {
            if (status)
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
            else
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
        }
    }

    protected void animatePreLoader() {
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
                    if (auto_dismiss) {
                        dismissScreen();
                    } else {
                        isAnimationEnd = true;
                    }
                }
            }
        });
        animatable2.start();
    }

    protected void stopLoaderAnimation(boolean status) {
        if (getActivity() == null) {
            return;
        }

        if (status) {
            if (successReplaceDrawableId != null) {
                loaderIv.setImageResource(successReplaceDrawableId);
                if (successReplaceTintColor != null) {
                    loaderIv.setImageTintList(ColorStateList.valueOf(getResources().getColor(successReplaceTintColor, null)));
                }
            } else {
                loaderIv.setImageDrawable(getActivity().getDrawable(R.drawable.success_animation_drawable));
                ((Animatable) loaderIv.getDrawable()).start();
            }
        } else {
            loaderIv.setImageDrawable(getActivity().getDrawable(R.drawable.failure_animation_drawable));
            ((Animatable) loaderIv.getDrawable()).start();
        }

        titleTv.setText(title);
        messageTv.setText(message);

        if (needToShowDoneButtonOnResultFetched) {
            doneBtn.setAlpha(0f);
            doneBtn.setVisibility(View.VISIBLE);
            doneBtn.animate().alpha(1).setDuration(500);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,getIntentFilterKey());
        Log.e("aswin", "onResume: ");
        animatePreLoader();
    }

    protected IntentFilter getIntentFilterKey() {
        return new IntentFilter(getString(R.string.success_broadcast_receiver));
    }
}
