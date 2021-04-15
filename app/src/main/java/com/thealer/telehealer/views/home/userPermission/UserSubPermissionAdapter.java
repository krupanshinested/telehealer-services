package com.thealer.telehealer.views.home.userPermission;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.OnAdapterListener;
import com.thealer.telehealer.apilayer.models.commonResponseModel.PermissionBean;
import com.thealer.telehealer.apilayer.models.commonResponseModel.PermissionDetails;
import com.thealer.telehealer.apilayer.models.userPermission.UserPermissionApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nimesh Patel
 * Created Date: 08,April,2021
 **/
public class UserSubPermissionAdapter extends RecyclerView.Adapter<UserSubPermissionAdapter.OnUserSubPermissionViewHolder> {
    private FragmentActivity activity;
    private List<PermissionBean> adapterList;
    private OnAdapterListener onAdapterListener;
    private int rootPosition;
    public UserSubPermissionAdapter(FragmentActivity activity,int rootPosition, List<PermissionBean> adapterList, OnAdapterListener onAdapterListener) {
        this.activity = activity;
        this.adapterList = adapterList;
        this.onAdapterListener = onAdapterListener;
        this.rootPosition=rootPosition;
    }

    @NonNull
    @Override
    public OnUserSubPermissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View permissionView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subpermission_raw_item, parent, false);
        return new OnUserSubPermissionViewHolder(permissionView);
    }

    @Override
    public void onBindViewHolder(@NonNull OnUserSubPermissionViewHolder holder, int position) {
        PermissionBean currentPermission = adapterList.get(position);
        if (currentPermission != null) {
            holder.permissionSubSwitch.setChecked(currentPermission.getValue());
            PermissionDetails childPermissionInfo = currentPermission.getPermission();
            holder.title.setText(childPermissionInfo.getName());
            holder.permissionSubSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(ArgumentKeys.ITEM_CLICK_CHILD_POS, position);
                    bundle.putInt(ArgumentKeys.ITEM_CLICK_PARENT_POS, rootPosition);
                    bundle.putBoolean(ArgumentKeys.IS_FROM_PARENT, false);
                    onAdapterListener.onEventTrigger(bundle);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return adapterList.size();
    }

    public class OnUserSubPermissionViewHolder extends RecyclerView.ViewHolder {
        private Switch permissionSubSwitch;
        private TextView title;

        public OnUserSubPermissionViewHolder(@NonNull View itemView) {
            super(itemView);
            permissionSubSwitch = itemView.findViewById(R.id.permission_sub_switch);
            title = itemView.findViewById(R.id.title);

        }
    }
}
