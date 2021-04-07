package com.thealer.telehealer.views.home.userPermission;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.OnAdapterListener;
import com.thealer.telehealer.apilayer.models.userPermission.UserPermissionApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;

import java.util.ArrayList;

/**
 * Created by Nimesh Patel
 * Created Date: 07,April,2021
 **/
public class UserPermissionAdapter extends RecyclerView.Adapter<UserPermissionAdapter.OnUserPermissionViewHolder> {
    private FragmentActivity activity;
    private ArrayList<UserPermissionApiResponseModel.Datum> adapterList;
    private OnAdapterListener onAdapterListener;
    UserPermissionAdapter subUserPermissionAdapter;
    int rootPosition=-1;

    public UserPermissionAdapter(FragmentActivity activity, OnAdapterListener onAdapterListener) {
        this.activity = activity;
        this.onAdapterListener = onAdapterListener;
    }

    @NonNull
    @Override
    public OnUserPermissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View permissionView = LayoutInflater.from(parent.getContext()).inflate(R.layout.permission_raw_item, parent, false);
        return new UserPermissionAdapter.OnUserPermissionViewHolder(permissionView);
    }

    @Override
    public void onBindViewHolder(@NonNull OnUserPermissionViewHolder holder, int position) {
        UserPermissionApiResponseModel.Datum currentPermission = adapterList.get(position);
        rootPosition=position;
        if (currentPermission != null) {
            holder.statSwitch.setChecked(currentPermission.getPermissionState());
            holder.tvName.setText(currentPermission.getPermissionName());
            if (currentPermission.getSubPermission() != null && currentPermission.getSubPermission().size() > 0 && currentPermission.getPermissionState()) {

                holder.rvSubSwitch.setVisibility(View.VISIBLE);

            } else {
                holder.rvSubSwitch.setVisibility(View.GONE);
            }
            holder.statSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(ArgumentKeys.ITEM_CLICK_POS, position);
                    onAdapterListener.onEventTrigger(bundle);
                }
            });


        }
    }

    @Override
    public int getItemCount() {
        return adapterList.size();
    }

    public void setAdapterData(ArrayList<UserPermissionApiResponseModel.Datum> adapterList) {
        this.adapterList = adapterList;
    }

    public class OnUserPermissionViewHolder extends RecyclerView.ViewHolder {
        private Switch statSwitch;
        private RecyclerView rvSubSwitch;
        private TextView tvName;

        public OnUserPermissionViewHolder(@NonNull View itemView) {
            super(itemView);
            statSwitch = itemView.findViewById(R.id.stat_switch);
            rvSubSwitch = itemView.findViewById(R.id.rv_sub_switch);
            tvName = itemView.findViewById(R.id.tv_name);
            rvSubSwitch.setLayoutManager(new LinearLayoutManager(activity));

        }
    }
}
