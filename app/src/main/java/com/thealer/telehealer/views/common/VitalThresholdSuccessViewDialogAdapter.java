package com.thealer.telehealer.views.common;

import android.content.Context;
import android.text.Html;
import android.util.ArraySet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsDetailBean;
import com.thealer.telehealer.common.BaseAdapter;
import com.thealer.telehealer.common.GetUserDetails;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.home.VitalsOrdersListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

public class VitalThresholdSuccessViewDialogAdapter extends RecyclerView.Adapter<VitalThresholdSuccessViewDialogAdapter.ViewHolder> {

    ArrayList<VitalsDetailBean> detail;
    FragmentActivity activity;
    private HashMap<String, CommonUserApiResponseModel> doctorMap = new HashMap<>();

    public VitalThresholdSuccessViewDialogAdapter(ArrayList<VitalsDetailBean> detail, FragmentActivity activity) {
        this.detail=detail;
        this.activity=activity;
        Set<String> guids = new ArraySet<>();
        for (VitalsDetailBean bean:detail) {
            guids.add(bean.getDoctor_guid());
        }
        getDoc(guids);
    }

    @NonNull
    @Override
    public VitalThresholdSuccessViewDialogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_vitals_threshold, parent, false);
        return new VitalThresholdSuccessViewDialogAdapter.ViewHolder(view);
    }

    private void getDoc(Set<String> guids) {
        GetUserDetails
                .getInstance(activity)
                .getDetails(guids)
                .getHashMapMutableLiveData().observe(activity,
                new Observer<HashMap<String, CommonUserApiResponseModel>>() {
                    @Override
                    public void onChanged(@Nullable HashMap<String, CommonUserApiResponseModel> stringCommonUserApiResponseModelHashMap) {
                        doctorMap = stringCommonUserApiResponseModelHashMap;
                        notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onBindViewHolder(@NonNull VitalThresholdSuccessViewDialogAdapter.ViewHolder holder, int position) {
        ViewHolder myViewHolder = (ViewHolder) holder;

        String disclaimerHtml = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "<h5><font color=\"black\">%s</font><br>%s</h5>" +
                "<p><font color=\"black\">%s</font></p>\n" +
                "</body>\n" +
                "</html>\n";

        VitalsDetailBean vitalBean = detail.get(position);
        String doctor_name = "";
        CommonUserApiResponseModel doctor = doctorMap.get(vitalBean.getDoctor_guid());
        if (doctor != null) {
            doctor_name = doctorMap.get(vitalBean.getDoctor_guid()).getUserDisplay_name();
            Utils.setImageWithGlide(myViewHolder.userProfileIV.getContext(),myViewHolder.userProfileIV,doctor.getUser_avatar(),myViewHolder.userProfileIV.getContext().getDrawable(R.drawable.profile_placeholder), true, true);
        } else {
            myViewHolder.userProfileIV.setImageDrawable(myViewHolder.userProfileIV.getContext().getDrawable(R.drawable.profile_placeholder));
        }

        myViewHolder.descriptionTV.setText(Html.fromHtml(String.format(disclaimerHtml, doctor_name, "says", vitalBean.getMessage())));
    }

    @Override
    public int getItemCount() {
        return detail.size();
    }

    @Override
    public void onViewRecycled(VitalThresholdSuccessViewDialogAdapter.ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.scrollView.scrollTo(0,0);

    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView userProfileIV;
        private TextView descriptionTV;
        public NestedScrollView scrollView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileIV = (ImageView) itemView.findViewById(R.id.userImageProfile);
            descriptionTV = (TextView) itemView.findViewById(R.id.vitals_description_tv);
            scrollView = (NestedScrollView) itemView.findViewById(R.id.scroll);
        }
    }
}
