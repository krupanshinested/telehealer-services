package com.thealer.telehealer.views.signup.doctor;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.createuser.PracticesBean;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;

import java.util.List;

/**
 * Created by Aswin on 25,October,2018
 */
class DoctorPracticeSelectAdapter extends RecyclerView.Adapter<DoctorPracticeSelectAdapter.ViewHolder> {
    private FragmentActivity fragmentActivity;
    private OnActionCompleteInterface onActionCompleteInterface;
    private List<PracticesBean> practicesBeanList;
    private Dialog dialog;
    private int position;

    public DoctorPracticeSelectAdapter(FragmentActivity activity, List<PracticesBean> practicesBeanList, Dialog dialog, int position) {
        this.fragmentActivity = activity;
        this.practicesBeanList = practicesBeanList;
        onActionCompleteInterface = (OnActionCompleteInterface) activity;
        this.dialog = dialog;
        this.position = position;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(fragmentActivity).inflate(R.layout.adapter_doctor_practice_selection, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.practiceNameTv.setText(practicesBeanList.get(i).getName());

        viewHolder.practiceAddressTv.setText(practicesBeanList.get(i).getName() + " " +
                practicesBeanList.get(i).getVisit_address().getStreet() + " " +
                practicesBeanList.get(i).getVisit_address().getStreet2() + " " +
                practicesBeanList.get(i).getVisit_address().getCity() + " " +
                practicesBeanList.get(i).getVisit_address().getState() + " " +
                practicesBeanList.get(i).getVisit_address().getZip() + ".");

        viewHolder.practiceCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.IS_CREATE_MANUALLY, false);
                bundle.putBoolean(Constants.IS_NEW_PRACTICE, false);
                bundle.putInt(Constants.PRACTICE_ID, i);
                bundle.putInt(Constants.DOCTOR_ID, position);

                dialog.dismiss();

                onActionCompleteInterface.onCompletionResult(null, true, bundle);

            }
        });
    }

    @Override
    public int getItemCount() {
        return practicesBeanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView practiceNameTv;
        private TextView practiceAddressTv;
        private ConstraintLayout practiceCl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            practiceNameTv = (TextView) itemView.findViewById(R.id.practice_name_tv);
            practiceAddressTv = (TextView) itemView.findViewById(R.id.practice_address_tv);
            practiceCl = (ConstraintLayout) itemView.findViewById(R.id.practice_cl);
        }
    }
}
