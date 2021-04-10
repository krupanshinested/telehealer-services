package com.thealer.telehealer.views.subscription;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.subscription.PlanInfo;

import java.util.ArrayList;

/**
 * Created by Nimesh Patel
 * Created Date: 10,April,2021
 **/
public class SubscriptionPlanAdapter extends RecyclerView.Adapter<SubscriptionPlanAdapter.OnSubscriptionViewHolder> {
    private  FragmentActivity fragmentActivity;
    private ArrayList<PlanInfo> adapterList;

    public SubscriptionPlanAdapter(FragmentActivity fragmentActivity) {
        this.fragmentActivity=fragmentActivity;
    }

    @NonNull
    @Override
    public OnSubscriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View subscriptionView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subpermission_raw_item, parent, false);
        return new SubscriptionPlanAdapter.OnSubscriptionViewHolder(subscriptionView);
    }

    @Override
    public void onBindViewHolder(@NonNull OnSubscriptionViewHolder holder, int position) {
        PlanInfo currentPlan = adapterList.get(position);
        if(currentPlan.getPlanActivated()){
            holder.llContainer.setForeground(new ColorDrawable(ContextCompat.getColor(fragmentActivity,R.color.colorWhite_25)));
        }else{
            holder.llContainer.setForeground(null);
        }
        holder.tvPlanName.setText(currentPlan.getPlanName());
        String planPrice=fragmentActivity.getString(R.string.symbol_dollar)+" "+" <font color='red'>"+currentPlan.getPlanPricing()+"</font>";
        holder.tvPlanPricing.setText(Html.fromHtml(planPrice));
        holder.tvExistingFeature.setText(currentPlan.getExistingFeatures());
        holder.tvPlanPricing.setText(currentPlan.getAdditionalFeatures());
        holder.tvFreeDesc.setText(Html.fromHtml(currentPlan.getFreeDesc()));
        holder.tvRpmDesc.setText(currentPlan.getRpmDesc());
        int btnColor;
        switch (position%4){
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
            default:

                break;
        }

    }

    @Override
    public int getItemCount() {
        return adapterList.size();
    }

    public void setAdapterData(ArrayList<PlanInfo> adapterList) {
        this.adapterList=adapterList;
    }

    public class OnSubscriptionViewHolder extends RecyclerView.ViewHolder{
        LinearLayout llContainer;
        TextView tvPlanName,tvPlanPricing,tvExistingFeature, tvAdditionalFeature,tvFreeDesc,tvRpmDesc;
        Button btnActivate;
        public OnSubscriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            llContainer=itemView.findViewById(R.id.ll_container);
            tvPlanName=itemView.findViewById(R.id.tv_plan_name);
            tvPlanPricing=itemView.findViewById(R.id.tv_plan_pricing);
            tvExistingFeature=itemView.findViewById(R.id.tv_existing_feature);
            tvAdditionalFeature =itemView.findViewById(R.id.tv_aditional_feature);
            tvFreeDesc =itemView.findViewById(R.id.tv_free_desc);
            tvRpmDesc =itemView.findViewById(R.id.tv_rpm_desc);
            btnActivate =itemView.findViewById(R.id.btn_activate);
        }
    }
}
