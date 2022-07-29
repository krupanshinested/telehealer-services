package com.thealer.telehealer.views.home.userPermission;

import android.content.DialogInterface;
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
import com.thealer.telehealer.apilayer.models.commonResponseModel.PermissionBean;
import com.thealer.telehealer.apilayer.models.commonResponseModel.PermissionDetails;
import com.thealer.telehealer.apilayer.models.userPermission.UserPermissionApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nimesh Patel
 * Created Date: 07,April,2021
 **/
public class UserPermissionAdapter extends RecyclerView.Adapter<UserPermissionAdapter.OnUserPermissionViewHolder> implements OnAdapterListener {
    private FragmentActivity activity;
    private List<PermissionBean> adapterList;
    private OnAdapterListener onAdapterListener;


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
        PermissionBean currentPermission = adapterList.get(position);
        if (currentPermission != null) {
//            holder.permissionSwitch.setChecked(currentPermission.getValue());
            PermissionDetails rootPermissionInfo = currentPermission.getPermission();
            holder.title.setText(rootPermissionInfo.getName());
            if (currentPermission.getValue() && currentPermission.getChildren() != null && currentPermission.getChildren().size() > 0) {
                holder.rvSubSwitch.setVisibility(View.VISIBLE);
                UserSubPermissionAdapter userSubPermissionAdapter = new UserSubPermissionAdapter(activity, position, currentPermission.getChildren(), this);
                holder.rvSubSwitch.setAdapter(userSubPermissionAdapter);
            } else {
                holder.rvSubSwitch.setVisibility(View.GONE);
            }
            holder.permissionSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (!currentPermission.getValue()) {
//                        Utils.showAlertDialog(activity, activity.getString(R.string.notes), activity.getString(R.string.hippa_msg), activity.getString(R.string.ok), null, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Bundle bundle = new Bundle();
//                                bundle.putInt(ArgumentKeys.ITEM_CLICK_PARENT_POS, position);
//                                bundle.putBoolean(ArgumentKeys.IS_FROM_PARENT, true);
//                                onAdapterListener.onEventTrigger(bundle);
//                                dialog.dismiss();
//                            }
//                        }, null);
//                    }else{
//                        Bundle bundle = new Bundle();
//                        bundle.putInt(ArgumentKeys.ITEM_CLICK_PARENT_POS, position);
//                        bundle.putBoolean(ArgumentKeys.IS_FROM_PARENT, true);
//                        onAdapterListener.onEventTrigger(bundle);
//                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return adapterList.size();
    }

    public void setAdapterData(List<PermissionBean> adapterList) {
        this.adapterList = adapterList;
    }

    @Override
    public void onEventTrigger(Bundle bundle) {
        onAdapterListener.onEventTrigger(bundle);
    }

    public class OnUserPermissionViewHolder extends RecyclerView.ViewHolder {
        private Switch permissionSwitch;
        private RecyclerView rvSubSwitch;
        private TextView title;

        public OnUserPermissionViewHolder(@NonNull View itemView) {
            super(itemView);
            permissionSwitch = itemView.findViewById(R.id.permission_switch);
            rvSubSwitch = itemView.findViewById(R.id.rv_sub_switch);
            title = itemView.findViewById(R.id.title);
            rvSubSwitch.setLayoutManager(new LinearLayoutManager(activity));

        }
    }
}
