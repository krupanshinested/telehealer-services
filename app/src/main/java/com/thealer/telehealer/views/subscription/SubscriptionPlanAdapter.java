package com.thealer.telehealer.views.subscription;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.OnAdapterListener;
import com.thealer.telehealer.apilayer.models.subscription.PlanInfoBean;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nimesh Patel
 * Created Date: 10,April,2021
 **/
public class SubscriptionPlanAdapter extends RecyclerView.Adapter<SubscriptionPlanAdapter.OnSubscriptionViewHolder> {
    private FragmentActivity fragmentActivity;
    private List<PlanInfoBean.Result> adapterList = new ArrayList<>();
    private OnAdapterListener adapterListener;
    private int isCancelPos = -1;

    public SubscriptionPlanAdapter(FragmentActivity fragmentActivity, OnAdapterListener adapterListener) {
        this.fragmentActivity = fragmentActivity;
        this.adapterListener = adapterListener;
    }

    @NonNull
    @Override
    public OnSubscriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View subscriptionView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscription_raw_item, parent, false);
        return new OnSubscriptionViewHolder(subscriptionView);
    }

    @Override
    public void onBindViewHolder(@NonNull OnSubscriptionViewHolder holder, int position) {
        PlanInfoBean.Result currentPlan = adapterList.get(position);
        if (currentPlan.getIs_active()) {

            if (currentPlan.isPurchased())
                Constants.activatedPlan = position;

            int llContainerBGColor, tvTxtColor, tvHighlightColor, tvFeatureColor, tvDescColor, btnTextColor, btnBGColor;
            String btnStr = fragmentActivity.getString(R.string.str_start_with_ideal);
            switch ((position) % 4) {
                case 0:
                    llContainerBGColor = R.color.bt_white;
                    tvTxtColor = R.color.colorBlack;
                    tvHighlightColor = R.color.bt_theme_orange;
                    tvFeatureColor = R.color.color_blue;
                    tvDescColor = R.color.colorBlack_85;
                    btnTextColor = R.color.colorWhite;
                    btnBGColor = R.color.bt_theme_blue;
                    if (isCancelPos == (position) % 4) {
                        btnStr = fragmentActivity.getString(R.string.str_start_with_current);
                    } else {
                        btnStr = fragmentActivity.getString(R.string.str_start_with_limited);
                    }

                    break;
                case 1:
                    llContainerBGColor = R.color.bt_theme_orange;
                    tvTxtColor = R.color.colorWhite;
                    tvHighlightColor = R.color.colorWhite;
                    tvFeatureColor = R.color.colorWhite_85;
                    tvDescColor = R.color.colorWhite_85;
                    btnTextColor = R.color.bt_theme_orange;
                    btnBGColor = R.color.colorWhite;
                    if (isCancelPos == (position) % 4) {
                        if (SubscriptionPlanFragment.isContinuePlan) {
                            btnStr = fragmentActivity.getString(R.string.str_start_with_current);
                        } else {
                            btnStr = fragmentActivity.getString(R.string.str_start_with_basic);
                        }
                    } else {
                        btnStr = fragmentActivity.getString(R.string.str_start_with_basic);
                    }
                    break;
                case 2:
                    llContainerBGColor = R.color.bt_theme_blue;
                    tvTxtColor = R.color.colorWhite;
                    tvHighlightColor = R.color.colorWhite;
                    tvFeatureColor = R.color.colorWhite_85;
                    tvDescColor = R.color.colorWhite_85;
                    btnTextColor = R.color.bt_theme_blue;
                    btnBGColor = R.color.colorWhite;
                    if (isCancelPos == (position) % 4) {
                        if (SubscriptionPlanFragment.isContinuePlan) {
                            btnStr = fragmentActivity.getString(R.string.str_start_with_current);
                        } else {
                            btnStr = fragmentActivity.getString(R.string.str_start_with_better);
                        }
                    } else {
                        btnStr = fragmentActivity.getString(R.string.str_start_with_better);
                    }
                    break;
                case 3:
                default:
                    llContainerBGColor = R.color.bt_theme_parot;
                    tvTxtColor = R.color.colorWhite;
                    tvHighlightColor = R.color.colorWhite;
                    tvFeatureColor = R.color.colorWhite_85;
                    tvDescColor = R.color.colorWhite_85;
                    btnTextColor = R.color.bt_theme_green;
                    btnBGColor = R.color.colorWhite;

                    if (isCancelPos == (position) % 4) {
                        if (SubscriptionPlanFragment.isContinuePlan) {
                            btnStr = fragmentActivity.getString(R.string.str_start_with_current);
                        } else {
                            btnStr = fragmentActivity.getString(R.string.str_start_with_ideal);
                        }
                    } else {
                        btnStr = fragmentActivity.getString(R.string.str_start_with_ideal);
                    }
                    break;
            }
            if (!SubscriptionPlanFragment.isChangePlan) {
                if (!SubscriptionPlanFragment.isContinuePlan) {
                    if (!SubscriptionPlanFragment.isResubscriptPlan) {
                        if (currentPlan.isPurchased() || currentPlan.isCanReshedule()) {
                            holder.llContainer.setForeground(new ColorDrawable(ContextCompat.getColor(fragmentActivity, R.color.colorWhite_50)));
                        } else {
                            holder.llContainer.setForeground(new ColorDrawable(Color.TRANSPARENT));
                        }
                    }
                }
            }

            String[] name = currentPlan.getName().split("Practice");

            if (SubscriptionPlanFragment.isChangePlan) {
                if (SubscriptionPlanFragment.isResubscriptPlan) {
                    btnStr = name[0].isEmpty() ? getTest(position) : "Start with " + name[0];
                } else {
                    if (currentPlan.isPurchased() || currentPlan.isCancelled()) {
                        btnStr = "Continue with current plan";
                    } else {
                        btnStr = name[0].isEmpty() ? getTest(position) : "Start with " + name[0];
                    }
                }
            } else {
                btnStr = name[0].isEmpty() ? getTest(position) : "Start with " + name[0];
            }

            holder.btnStartWith.setText(btnStr);

            holder.llContainer.setBackgroundTintList(ContextCompat.getColorStateList(fragmentActivity, llContainerBGColor));

            holder.tvPlanName.setTextColor(ContextCompat.getColor(fragmentActivity, tvTxtColor));
            holder.tvPlanName.setText(currentPlan.getName());

            String haxcolor = "#" + Integer.toHexString(ContextCompat.getColor(fragmentActivity, tvHighlightColor)).substring(2);
            String planPrice = fragmentActivity.getString(R.string.symbol_dollar) + " " + " <font color=" + haxcolor + ">" + currentPlan.getPrice() + "</font> " + currentPlan.getBilling_cycle();
            holder.tvPlanPricing.setTextColor(ContextCompat.getColor(fragmentActivity, tvTxtColor));
            holder.tvPlanPricing.setText(Html.fromHtml(planPrice));

            holder.tvExistingFeature.setTextColor(ContextCompat.getColor(fragmentActivity, tvDescColor));
            holder.tvExistingFeature.setText(currentPlan.getDescription());

            holder.tvAdditionalFeature.setTextColor(ContextCompat.getColor(fragmentActivity, tvFeatureColor));
            holder.tvAdditionalFeature.setText("See Feature List");

            String rpmDesc = "Perform <big><font color=" + haxcolor + ">" + currentPlan.getRpm_count() + "</font></big> monthly to get this plan <big><font color=" + haxcolor + "> Free.</font></big>";
            holder.tvRpmDesc.setText(Html.fromHtml(rpmDesc));
            holder.tvRpmDesc.setTextColor(ContextCompat.getColor(fragmentActivity, tvTxtColor));

//            if (position == 0) {
            holder.tvAdditionalFeature.setCompoundDrawables(null, null, null, null);
            setMargins(holder.cvRoot, 0, 15, 0, 0);
//            } else if (position == (adapterList.size() - 1)) {
//                setMargins(holder.cvRoot, 0, 15, 0, 75);
//            } else {
//                setMargins(holder.cvRoot, 0, 15, 0, 0);
//            }
            holder.btnStartWith.setBackgroundColor(ContextCompat.getColor(fragmentActivity, btnBGColor));
            holder.btnStartWith.setTextColor(ContextCompat.getColor(fragmentActivity, btnTextColor));
            holder.clRoot.setVisibility(View.VISIBLE);
        } else {
            holder.clRoot.setVisibility(View.GONE);
        }
        holder.tvAdditionalFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (position == 0) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://telehealer.coruscate.work/doctors/#Features"));
                fragmentActivity.startActivity(browserIntent);
//                }
            }
        });
        holder.btnStartWith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentPlan.isPurchased()) {
                    if (SubscriptionPlanFragment.isContinuePlan || SubscriptionPlanFragment.isResubscriptPlan) {
                        Bundle bundle = new Bundle();
                        bundle.putString(ArgumentKeys.PlanID, currentPlan.getPlan_id());
                        bundle.putString(ArgumentKeys.BillingCycle, currentPlan.getBilling_cycle());
                        adapterListener.onEventTrigger(bundle);
                    } else {
                        if (Constants.activatedPlan != position) {

                            Bundle bundle = new Bundle();
                            bundle.putString(ArgumentKeys.PlanID, currentPlan.getPlan_id());
                            bundle.putString(ArgumentKeys.BillingCycle, currentPlan.getBilling_cycle());
                            adapterListener.onEventTrigger(bundle);
                        } else {
                            fragmentActivity.onBackPressed();
                            Toast.makeText(fragmentActivity, fragmentActivity.getString(R.string.str_plan_already_activate), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (Constants.activatedPlan == position) {
                        fragmentActivity.onBackPressed();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return adapterList.size();
    }

    public String getTest(int position) {
        switch (position % 4) {
            case 0:
                return "Start with Limited";
            case 1:
                return "Start with Basic";
            case 2:
                return "Start with Better";
            default:
                return "Start with Ideal";
        }
    }

    public void setAdapterData(List<PlanInfoBean.Result> adapterList) {
        this.adapterList = adapterList;

        if (SubscriptionPlanFragment.isContinuePlan) {
            for (int i = 0; i < adapterList.size(); i++) {
                if (adapterList.get(i).isCancelled()) {
                    isCancelPos = i;
                }
            }
        }

    }

    public class OnSubscriptionViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llPlan, llContainer;
        TextView tvPlanName, tvPlanPricing, tvExistingFeature, tvAdditionalFeature, tvRpmDesc;
        Button btnStartWith;
        CardView cvRoot;
        ConstraintLayout clRoot;

        public OnSubscriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            cvRoot = itemView.findViewById(R.id.cv_root);
            clRoot = itemView.findViewById(R.id.cl_root);
            llPlan = itemView.findViewById(R.id.ll_plan);
            btnStartWith = itemView.findViewById(R.id.btn_start_with);
            llContainer = itemView.findViewById(R.id.ll_container);
            tvPlanName = itemView.findViewById(R.id.tv_plan_name);
            tvPlanPricing = itemView.findViewById(R.id.tv_plan_pricing);
            tvExistingFeature = itemView.findViewById(R.id.tv_existing_feature);
            tvAdditionalFeature = itemView.findViewById(R.id.tv_aditional_feature);
            tvRpmDesc = itemView.findViewById(R.id.tv_rpm_desc);
        }
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

            final float scale = fragmentActivity.getResources().getDisplayMetrics().density;
            // convert the DP into pixel
            int l = (int) (left * scale + 0.5f);
            int r = (int) (right * scale + 0.5f);
            int t = (int) (top * scale + 0.5f);
            int b = (int) (bottom * scale + 0.5f);

            p.setMargins(l, t, r, b);
            view.requestLayout();
        }
    }
}
