package com.thealer.telehealer.views.home.monitoring;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import flavor.GoogleFit.VitalsListWithGoogleFitFragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.monitoring.diet.DietDetailFragment;
import com.thealer.telehealer.views.home.monitoring.diet.DietUserListingFragment;
import com.thealer.telehealer.views.home.vitals.vitalReport.VitalReportFragment;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Aswin on 20,February,2019
 */
class MonitoringListAdapter extends RecyclerView.Adapter<MonitoringListAdapter.ViewHolder> {

    private FragmentActivity activity;
    private ShowSubFragmentInterface showSubFragmentInterface;

    private List<String> titleList;
    private List<Drawable> imageList;
    private Bundle bundle;
    private CommonUserApiResponseModel doctorModel;

    public MonitoringListAdapter(FragmentActivity activity,@Nullable Bundle arguments) {
        this.activity = activity;
        showSubFragmentInterface = (ShowSubFragmentInterface) activity;
        titleList = Arrays.asList(activity.getString(R.string.vitals), activity.getString(R.string.diet));
        imageList = Arrays.asList(activity.getDrawable(R.drawable.ic_vitals_heart), activity.getDrawable(R.drawable.ic_diet));
        this.bundle = arguments;

        if (arguments != null) {
            String openAutomaticType = arguments.getString(ArgumentKeys.OPEN_AUTOMATICALLY);
            doctorModel = (CommonUserApiResponseModel) arguments.getSerializable(Constants.DOCTOR_DETAIL);

            if (openAutomaticType != null) {
                switch (openAutomaticType) {
                    case MonitoringFragment.DIET_OPEN_TYPE:
                        openFragment(1);
                        break;
                    case MonitoringFragment.VITAL_OPEN_TYPE:
                        openFragment(0);
                        break;
                }
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_vitals_orders_list_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.listTv.setText(titleList.get(i));
        viewHolder.listIv.setImageDrawable(imageList.get(i));
//        ManageSAPermission(viewHolder,i);
        viewHolder.listCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView listIv;
        private TextView listTv;
        private CardView listCv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            listIv = (ImageView) itemView.findViewById(R.id.list_iv);
            listTv = (TextView) itemView.findViewById(R.id.list_tv);
            listCv = (CardView) itemView.findViewById(R.id.list_cv);
        }
    }

    private void openFragment(int i) {
        Fragment fragment = null;
        if (bundle == null) {
            bundle = new Bundle();
        }
        if(i==0) {
            if (UserType.isUserAssistant() && doctorModel != null && doctorModel.getPermissions() != null && doctorModel.getPermissions().size() > 0) {
                boolean isPermissionAllowed = Utils.checkPermissionStatus(doctorModel.getPermissions(), ArgumentKeys.VIEW_VITALS_CODE);
                Constants.isVitalsAddEnable = Utils.checkPermissionStatus(doctorModel.getPermissions(), ArgumentKeys.ADD_VITALS_CODE);
                Constants.isVitalsViewEnable = Utils.checkPermissionStatus(doctorModel.getPermissions(), ArgumentKeys.VIEW_VITALS_CODE);

                bundle.putBoolean(ArgumentKeys.isPermissionAllowed,isPermissionAllowed);
                /*if (!isPermissionAllowed) {
                    Utils.displayPermissionMsg(activity);
                    return;
                }*/

            }
        }

        bundle.putBoolean(ArgumentKeys.SHOW_TOOLBAR, true);

        if (titleList.get(i).equals(activity.getString(R.string.vitals))) {
            if (UserType.isUserPatient()) {

                fragment = new VitalsListWithGoogleFitFragment();
                bundle.putBoolean(Constants.IS_FROM_HOME, true);
                fragment.setArguments(bundle);

            } else {
                fragment = new VitalReportFragment();
                bundle.putBoolean(ArgumentKeys.SHOW_PRINT_FILTER, false);
                fragment.setArguments(bundle);
            }
        } else if (titleList.get(i).equals(activity.getString(R.string.medication))) {

            fragment = null;

        } else if (titleList.get(i).equals(activity.getString(R.string.diet))) {

            if (UserType.isUserPatient()) {
                fragment = new DietDetailFragment();
            } else {
                fragment = new DietUserListingFragment();
                bundle.putBoolean(ArgumentKeys.SHOW_PRINT_FILTER, false);
                fragment.setArguments(bundle);
            }
        }

        if (fragment != null) {
            showSubFragmentInterface.onShowFragment(fragment);
        }
    }

    private void ManageSAPermission(ViewHolder viewHolder, int i) {
        if(i==0) {
            if (UserType.isUserAssistant() && doctorModel != null && doctorModel.getPermissions() != null && doctorModel.getPermissions().size() > 0) {
                boolean isPermissionAllowed = Utils.checkPermissionStatus(doctorModel.getPermissions(), ArgumentKeys.ADD_VITALS_CODE);
                Constants.isVitalsAddEnable = Utils.checkPermissionStatus(doctorModel.getPermissions(), ArgumentKeys.ADD_VITALS_CODE);
                Constants.isVitalsViewEnable = Utils.checkPermissionStatus(doctorModel.getPermissions(), ArgumentKeys.VIEW_VITALS_CODE);

                if(!isPermissionAllowed){
                    viewHolder.listIv.setColorFilter(ContextCompat.getColor(activity,R.color.colorGrey), PorterDuff.Mode.SRC_IN);
                    viewHolder.listTv.setTextColor(ContextCompat.getColor(activity,R.color.colorGrey));
                }else{
                    viewHolder.listTv.setTextColor(ContextCompat.getColor(activity,R.color.colorBlack));
                    viewHolder.listIv.setColorFilter(ContextCompat.getColor(activity,R.color.app_gradient_start), PorterDuff.Mode.SRC_IN);
                }

            }
        }
    }
}
