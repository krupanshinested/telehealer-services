package com.thealer.telehealer.views.home;

import static com.thealer.telehealer.views.home.orders.radiology.RadiologyConstants.TYPE_ITEM;

import android.animation.Animator;
import android.app.Activity;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.AssociationAdapterListModel;
import com.thealer.telehealer.apilayer.models.DoctorGroupedAssociations;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.Utils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProviderListAdapter extends RecyclerView.Adapter<ProviderListAdapter.ViewHolder> {

    public ArrayList<AssociationAdapterListModel> adapterListModels = new ArrayList<>();
    public boolean isAnimationEnd = false;
    public boolean auto_dismiss = false;
    protected Integer successReplaceDrawableId = null;
    int defaultphysician = -1;
    Activity activity;
    private boolean isDataReceived = false;

    public ProviderListAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        return adapterListModels.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.view_provider, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        CommonUserApiResponseModel userModel = adapterListModels.get(position).getCommonUserApiResponseModel();

        viewHolder.titleTv.setText(userModel.getDisplayName());
        loadAvatar(viewHolder.avatarCiv, userModel.getUser_avatar());

        viewHolder.dftprovider.setVisibility(View.VISIBLE);
        viewHolder.dftprovider.setText("" + activity.getString(R.string.default_physician));
        viewHolder.dftprovider.setTextSize(12.0f);
        viewHolder.dftprovider.setHeight(24);
        viewHolder.subTitleTv.setText(userModel.getDisplayInfo());
        viewHolder.statusciv.setVisibility(View.GONE);

        if (defaultphysician == -1) {
            if (userModel.getisDefualtPhysician()) {
                isDataReceived = true;
                defaultphysician = position;
                viewHolder.loaderiv.setVisibility(View.VISIBLE);
                viewHolder.dftprovider.setVisibility(View.GONE);
                viewHolder.loaderiv.setImageDrawable(activity.getDrawable(R.drawable.success_animation_drawable));
                ((Animatable) viewHolder.loaderiv.getDrawable()).start();
            } else {
                viewHolder.loaderiv.setVisibility(View.GONE);
                viewHolder.dftprovider.setVisibility(View.VISIBLE);
            }
        } else {
            if (userModel.getisDefualtPhysician()) {
                isDataReceived = true;
                defaultphysician = position;
                viewHolder.loaderiv.setImageDrawable(activity.getDrawable(R.drawable.success_animation_drawable));
                ((Animatable) viewHolder.loaderiv.getDrawable()).start();
                viewHolder.loaderiv.setVisibility(View.VISIBLE);
                viewHolder.dftprovider.setVisibility(View.GONE);
            } else {
                viewHolder.loaderiv.setVisibility(View.GONE);
                viewHolder.dftprovider.setVisibility(View.VISIBLE);
            }
        }
        viewHolder.dftprovider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (defaultphysician == -1){
                    defaultphysician = position;
                }
                Animatable2 animatable2 = (Animatable2) viewHolder.loaderiv.getDrawable();
                proceed(viewHolder.loaderiv, viewHolder.preloaderiv, animatable2, viewHolder.dftprovider, userModel, true);
            }
        });

    }

    private void animatePreLoader(ImageView loaderiv, ImageView preloaderiv, Animatable2 animatable2, CustomButton dftprovider, CommonUserApiResponseModel userModel, boolean fromclick) {
        Animation animation = new TranslateAnimation(0, 0, 500, 0);
        animation.setDuration(1250);
        preloaderiv.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startLoaderAnimation(animatable2, loaderiv, userModel, fromclick);
                preloaderiv.animate().alpha(0.0f).setDuration(1500).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        loaderiv.setVisibility(View.VISIBLE);
                        dftprovider.setVisibility(View.GONE);
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

    private void startLoaderAnimation(Animatable2 animatable2, ImageView loaderiv, CommonUserApiResponseModel userModel, boolean fromclick) {

        animatable2.registerAnimationCallback(new Animatable2.AnimationCallback() {
            @Override
            public void onAnimationEnd(Drawable drawable) {
                super.onAnimationEnd(drawable);
                if (!isDataReceived) {
                    animatable2.start();
                } else {
                    stopLoaderAnimation(true, loaderiv);
                    if (auto_dismiss) {
//                        dismissScreen();
                    } else {
                        isAnimationEnd = true;
                        if (fromclick) {
                            adapterListModels.get(defaultphysician).getCommonUserApiResponseModel().setisDefualtPhysician(false);
                            userModel.setisDefualtPhysician(true);
                            notifyDataSetChanged();
                        }
                    }
                }
            }
        });
        animatable2.start();
    }

    private void stopLoaderAnimation(boolean status, ImageView loaderiv) {
        if (activity == null) {
            return;
        }

        if (status) {
            if (successReplaceDrawableId != null) {
                loaderiv.setImageResource(successReplaceDrawableId);
            } else {
                loaderiv.setImageDrawable(activity.getDrawable(R.drawable.success_animation_drawable));
                ((Animatable) loaderiv.getDrawable()).start();
            }
        } else {
            loaderiv.setImageDrawable(activity.getDrawable(R.drawable.failure_animation_drawable));
            ((Animatable) loaderiv.getDrawable()).start();
        }

    }

    private void proceed(ImageView loaderiv, ImageView preloaderiv, Animatable2 animatable2, CustomButton dftprovider, CommonUserApiResponseModel userModel, boolean fromclick) {
        animatePreLoader(loaderiv, preloaderiv, animatable2, dftprovider, userModel, fromclick);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                isDataReceived = true;
            }
        }, 3000);
    }

    private void loadAvatar(ImageView imageView, String user_avatar) {
        Utils.setImageWithGlide(activity.getApplicationContext(), imageView, user_avatar, activity.getDrawable(R.drawable.profile_placeholder), true, true);
    }

    public void setData(List<CommonUserApiResponseModel> associationApiResponseModelResult, int page) {
        List<CommonUserApiResponseModel> favoriteList = new ArrayList<>();
        List<CommonUserApiResponseModel> otherList = new ArrayList<>();

        if (page == 1) {
            adapterListModels.clear();
        }

        for (int i = 0; i < associationApiResponseModelResult.size(); i++) {
            if (associationApiResponseModelResult.get(i).getFavorite() != null && associationApiResponseModelResult.get(i).getFavorite()) {
                favoriteList.add(associationApiResponseModelResult.get(i));
            } else {
                otherList.add(associationApiResponseModelResult.get(i));
            }
        }

        if (!favoriteList.isEmpty()) {

            for (int i = 0; i < favoriteList.size(); i++) {
                adapterListModels.add(new AssociationAdapterListModel(TYPE_ITEM, favoriteList.get(i)));
            }
        }
        for (int i = 0; i < otherList.size(); i++) {
            adapterListModels.add(new AssociationAdapterListModel(TYPE_ITEM, otherList.get(i)));
        }

        notifyDataSetChanged();
    }

    public void setData(List<DoctorGroupedAssociations> associationApiResponseModelResult) {
        adapterListModels.clear();

        boolean needHeader = true;
        if (associationApiResponseModelResult.size() == 1 && associationApiResponseModelResult.get(0).getGroup_name().equals("Others")) {
            needHeader = false;
        }

        for (int i = 0; i < associationApiResponseModelResult.size(); i++) {
            DoctorGroupedAssociations associations = associationApiResponseModelResult.get(i);

            for (int j = 0; j < associations.getDoctors().size(); j++) {
                adapterListModels.add(new AssociationAdapterListModel(TYPE_ITEM, associations.getDoctors().get(j)));
            }
        }

        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ConstraintLayout animok;
        private final ImageView preloaderiv, loaderiv;
        private LinearLayout topView;
        private CircleImageView statusciv;
        private CustomButton dftprovider;
        private CircleImageView avatarCiv;
        private TextView titleTv;
        private TextView subTitleTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarCiv = (CircleImageView) itemView.findViewById(R.id.avatar_civ);
            statusciv = (CircleImageView) itemView.findViewById(R.id.status_civ);
            titleTv = (TextView) itemView.findViewById(R.id.list_title_tv);
            subTitleTv = (TextView) itemView.findViewById(R.id.list_sub_title_tv);
            dftprovider = (CustomButton) itemView.findViewById(R.id.accept_btn);
            topView = (LinearLayout) itemView.findViewById(R.id.topView);
            animok = (ConstraintLayout) itemView.findViewById(R.id.anim_ok);
            preloaderiv = (ImageView) itemView.findViewById(R.id.preloader_iv);
            loaderiv = (ImageView) itemView.findViewById(R.id.loader_iv);
        }

    }

}
