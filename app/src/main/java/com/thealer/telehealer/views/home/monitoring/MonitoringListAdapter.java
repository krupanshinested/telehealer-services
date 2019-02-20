package com.thealer.telehealer.views.home.monitoring;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.DoctorPatientListingFragment;
import com.thealer.telehealer.views.home.monitoring.diet.DietDetailFragment;
import com.thealer.telehealer.views.home.vitals.VitalsListFragment;
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

    public MonitoringListAdapter(FragmentActivity activity) {
        this.activity = activity;
        showSubFragmentInterface = (ShowSubFragmentInterface) activity;
        titleList = Arrays.asList(activity.getString(R.string.vitals), activity.getString(R.string.medication), activity.getString(R.string.diet));
        imageList = Arrays.asList(activity.getDrawable(R.drawable.ic_vitals_heart), activity.getDrawable(R.drawable.ic_medication), activity.getDrawable(R.drawable.ic_diet));
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
        viewHolder.listCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = null;
                Bundle bundle = new Bundle();
                bundle.putBoolean(ArgumentKeys.SHOW_TOOLBAR, true);

                if (titleList.get(i).equals(activity.getString(R.string.vitals))) {
                    if (UserType.isUserPatient()) {

                        fragment = new VitalsListFragment();
                        bundle.putBoolean(Constants.IS_FROM_HOME, true);
                        fragment.setArguments(bundle);

                    } else {
                        fragment = new VitalReportFragment();
                        fragment.setArguments(bundle);
                    }
                } else if (titleList.get(i).equals(activity.getString(R.string.medication))) {

                    fragment = null;

                } else if (titleList.get(i).equals(activity.getString(R.string.diet))) {

                    if (UserType.isUserPatient()) {
                        fragment = new DietDetailFragment();
                    } else if (UserType.isUserDoctor()) {
                        bundle.putBoolean(ArgumentKeys.IS_DIET_VIEW, true);
                        fragment = new DoctorPatientListingFragment();
                        fragment.setArguments(bundle);
                    }
                }

                if (fragment != null) {
                    showSubFragmentInterface.onShowFragment(fragment);
                }
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
}
