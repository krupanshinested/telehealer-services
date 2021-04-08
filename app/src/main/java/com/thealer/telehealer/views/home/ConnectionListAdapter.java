package com.thealer.telehealer.views.home;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.addConnection.AddConnectionApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.createuser.SpecialtiesBean;
import com.thealer.telehealer.common.Animation.CustomUserListItemView;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aswin on 19,November,2018
 */
public class ConnectionListAdapter extends RecyclerView.Adapter<ConnectionListAdapter.ViewHolder> {
    private Context context;
    private FragmentActivity fragmentActivity;
    private List<CommonUserApiResponseModel> apiResponseModelList = new ArrayList<>();
    private OnListItemSelectInterface onListItemSelectInterface;
    private OnActionCompleteInterface onActionCompleteInterface;
    private AddConnectionApiViewModel addConnectionApiViewModel;
    private int selected_position = -1;

    public ConnectionListAdapter(Context context) {
        this.context = context;
        this.fragmentActivity = (FragmentActivity) context;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        addConnectionApiViewModel = new ViewModelProvider(fragmentActivity).get(AddConnectionApiViewModel.class);
        onListItemSelectInterface = (OnListItemSelectInterface) fragmentActivity;
        onActionCompleteInterface = (OnActionCompleteInterface) fragmentActivity;

        addConnectionApiViewModel.baseApiResponseModelMutableLiveData.observe(fragmentActivity, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel.isSuccess()) {
                        Log.e("aswin", "onChanged: " + selected_position);
                        if (selected_position >= 0) {
                            apiResponseModelList.get(selected_position).setConnection_status(Constants.CONNECTION_STATUS_OPEN);
                            notifyItemChanged(selected_position);
                        }
                    }
                }
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_add_connection_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        CommonUserApiResponseModel user = apiResponseModelList.get(i);

        if (user.getRole().equals(Constants.ROLE_DOCTOR) && !user.getConnection_requests()) {
            viewHolder.actionIv.setImageDrawable(context.getDrawable(R.drawable.ic_info_32dp));
            viewHolder.actionIv.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.app_gradient_start)));
        } else if (user.getConnection_status() == null ||
                user.getConnection_status().equals(Constants.CONNECTION_STATUS_REJECTED)) {
            viewHolder.actionIv.setImageDrawable(context.getDrawable(R.drawable.ic_connect_user));
            viewHolder.actionIv.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.app_gradient_start)));
        } else if (user.getConnection_status().equals(Constants.CONNECTION_STATUS_OPEN) ||
                user.getConnection_status().equals(Constants.CONNECTION_STATUS_PENDING)) {
            viewHolder.actionIv.setImageDrawable(context.getDrawable(R.drawable.ic_status_pending));
            viewHolder.actionIv.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.color_green_light)));
        } else {
            viewHolder.actionIv.setVisibility(View.GONE);
        }

        Utils.setImageWithGlide(context, viewHolder.avatarCiv, user.getUser_avatar(), context.getDrawable(R.drawable.profile_placeholder), true, true);

        viewHolder.titleTv.setText(apiResponseModelList.get(i).getDisplayName());
        viewHolder.subTitleTv.setText(apiResponseModelList.get(i).getDisplayInfo());

        viewHolder.itemCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_position = i;
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.USER_DETAIL, apiResponseModelList.get(i));
                onListItemSelectInterface.onListItemSelected(i, bundle);
            }
        });

        viewHolder.actionIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getRole().equals(Constants.ROLE_DOCTOR) && !user.getConnection_requests()) {
                    selected_position = i;
                    selectDesignation(v,i,user);
                } else if (apiResponseModelList.get(i).getConnection_status() == null ||
                        apiResponseModelList.get(i).getConnection_status().equals(Constants.CONNECTION_STATUS_REJECTED)) {
                    Utils.vibrate(fragmentActivity);
                    selected_position = i;
                    onListItemSelectInterface.onListItemSelected(i, null);
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.ADD_CONNECTION_ID, apiResponseModelList.get(i).getUser_id());
                    bundle.putSerializable(Constants.USER_DETAIL, apiResponseModelList.get(i));
                    onActionCompleteInterface.onCompletionResult(null, true, bundle);
                }
            }
        });

        Utils.setImageWithGlide(context, viewHolder.avatarCiv, apiResponseModelList.get(i).getUser_avatar(), context.getDrawable(R.drawable.profile_placeholder), true, true);

        viewHolder.titleTv.setText(apiResponseModelList.get(i).getDisplayName());
        viewHolder.subTitleTv.setText(apiResponseModelList.get(i).getDisplayInfo());

    }

    //Allow physician to view list of support staff. Also physician can request to add them.
    private void selectDesignation(View v, int rootPos, CommonUserApiResponseModel user) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View layoutInflateView=layoutInflater.inflate
                (R.layout.designation_alert,(ViewGroup)v.findViewById(R.id.cl_root));
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setView(layoutInflateView);
        alertDialog.setCancelable(false);
        AlertDialog dialog = alertDialog.create();
        TextView headerTitle=layoutInflateView.findViewById(R.id.header_title);
        RecyclerView rvDesignation=layoutInflateView.findViewById(R.id.rv_designation);
        rvDesignation.setLayoutManager(new LinearLayoutManager(fragmentActivity));
        Button btnYes=layoutInflateView.findViewById(R.id.btn_yes);
        TextView noRecordFound=layoutInflateView.findViewById(R.id.no_record_found);
        Button btnCancel=layoutInflateView.findViewById(R.id.btn_cancel);
        View viewDevider=layoutInflateView.findViewById(R.id.view_devider);

        headerTitle.setText(String.format(fragmentActivity.getString(R.string.str_select_designation_for),user.getDisplayName()));

        List<SpecialtiesBean> tempList = user.getSupportStaffTypeList();
        tempList=new ArrayList<>();
        SpecialtiesBean spb1=new SpecialtiesBean();
        SpecialtiesBean spb2=new SpecialtiesBean();
        SpecialtiesBean spb3=new SpecialtiesBean();
        SpecialtiesBean spb4=new SpecialtiesBean();
        SpecialtiesBean spb5=new SpecialtiesBean();
        SpecialtiesBean spb6=new SpecialtiesBean();
        SpecialtiesBean spb7=new SpecialtiesBean();
        spb1.setName("Office Staff Assistant");
        spb2.setName("Medical Assistant");
        spb3.setName("Scribe");
        spb4.setName("Receptionist");
        spb5.setName("Biller");
        spb6.setName("Scheduler");
        spb7.setName("Service Provider");
        tempList.add(spb1);
        tempList.add(spb2);
        tempList.add(spb3);
        tempList.add(spb4);
        tempList.add(spb5);
        tempList.add(spb6);
        tempList.add(spb7);
        if(tempList.size()==0) {
            rvDesignation.setVisibility(View.GONE);
            noRecordFound.setVisibility(View.VISIBLE);
            btnYes.setVisibility(View.GONE);
            viewDevider.setVisibility(View.GONE);
        } else{
            rvDesignation.setVisibility(View.VISIBLE);
            noRecordFound.setVisibility(View.GONE);
            btnYes.setVisibility(View.VISIBLE);
            viewDevider.setVisibility(View.VISIBLE);
        }
        DesignationListAdapter designationListAdapter=new DesignationListAdapter(fragmentActivity,tempList);
        rvDesignation.setAdapter(designationListAdapter);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(designationListAdapter!=null) {
                    SpecialtiesBean specialist = designationListAdapter.getSpecialistInfo();
                    Toast.makeText(fragmentActivity, "You have Selected : "+specialist.getName(), Toast.LENGTH_LONG).show();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.USER_DETAIL, user);
                    bundle.putBoolean(ArgumentKeys.SHOW_CONNECTION_REQUEST_ALERT, true);
                    onListItemSelectInterface.onListItemSelected(rootPos, bundle);
                }
                dialog.dismiss();

            }
        });


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    public int getItemCount() {
        if (apiResponseModelList != null) {
            return apiResponseModelList.size();
        } else {
            return 0;
        }
    }

    public void setData(List<CommonUserApiResponseModel> commonUserApiResponseModelList, int selectedPosition) {
        apiResponseModelList = commonUserApiResponseModelList;
        if (selectedPosition == -1)
            notifyDataSetChanged();
        else {
            apiResponseModelList.get(selectedPosition).setConnection_status(Constants.CONNECTION_STATUS_OPEN);
            notifyItemChanged(selected_position);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatarCiv;
        private TextView titleTv;
        private TextView subTitleTv;
        private ImageView actionIv;
        private CardView itemCv;
        private CustomUserListItemView userListIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userListIv = (CustomUserListItemView) itemView.findViewById(R.id.user_list_iv);
            itemCv = userListIv.getListItemCv();
            avatarCiv = userListIv.getAvatarCiv();
            titleTv = userListIv.getListTitleTv();
            subTitleTv = userListIv.getListSubTitleTv();
            actionIv = userListIv.getActionIv();
        }
    }
}
