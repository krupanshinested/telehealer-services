package com.thealer.telehealer.views.signup.doctor;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.GetDoctorsApiResponseModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.GetDoctorsApiViewModel;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 25,October,2018
 */
class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {
    private FragmentActivity fragmentActivity;
    private List<String> doctorsNameList = new ArrayList<>();
    private OnActionCompleteInterface onActionCompleteInterface;
    private GetDoctorsApiResponseModel getDoctorsApiResponseModel;

    public SearchListAdapter(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
        onActionCompleteInterface = (OnActionCompleteInterface) fragmentActivity;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        GetDoctorsApiViewModel doctorsApiViewModel = ViewModelProviders.of(fragmentActivity).get(GetDoctorsApiViewModel.class);

        doctorsApiViewModel.getDoctorsApiResponseModelMutableLiveData.observe(fragmentActivity, new Observer<GetDoctorsApiResponseModel>() {
            @Override
            public void onChanged(@Nullable GetDoctorsApiResponseModel doctorsApiResponseModel) {
                if (doctorsApiResponseModel != null) {

                    if (doctorsApiResponseModel.getCurrent_page() == 1) {
                        resetAdapter();
                    }

                    if (doctorsApiResponseModel.getData().size() > 0) {

                        for (GetDoctorsApiResponseModel.DataBean dataBean :
                                doctorsApiResponseModel.getData()) {
                            doctorsNameList.add(dataBean.getProfile().getFirst_name());
                        }

                        notifyDataSetChanged();
                    } else {
                        resetAdapter();
                    }
                }else {
                    resetAdapter();
                }
            }
        });
    }

    private void resetAdapter() {
        doctorsNameList.clear();
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(fragmentActivity).inflate(R.layout.adapter_doctor_search_list, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.nameTv.setText(doctorsNameList.get(i));
        viewHolder.nameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doctorsNameList.clear();
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorsNameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.name_tv);
        }
    }
}
