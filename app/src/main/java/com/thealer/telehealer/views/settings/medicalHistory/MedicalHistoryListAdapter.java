package com.thealer.telehealer.views.settings.medicalHistory;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;

/**
 * Created by Aswin on 21,January,2019
 */
public class MedicalHistoryListAdapter extends RecyclerView.Adapter<MedicalHistoryListAdapter.ViewHolder> {
    private FragmentActivity fragmentActivity;
    private String[] listItem;
    private Bundle bundle = new Bundle();
    private Fragment fragment;

    private ShowSubFragmentInterface showSubFragmentInterface;
    private ChangeTitleInterface changeTitleInterface;

    public MedicalHistoryListAdapter(FragmentActivity activity, Bundle arguments, MedicalHistoryList medicalHistoryList) {
        this.fragmentActivity = activity;
        changeTitleInterface = (ChangeTitleInterface) activity;
        showSubFragmentInterface = (ShowSubFragmentInterface) activity;
        listItem = MedicalHistoryConstants.itemList;
        this.fragment = medicalHistoryList;
        if (arguments != null) {
            this.bundle = arguments;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(fragmentActivity).inflate(R.layout.adapter_medical_history_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.itemTitleTv.setText(listItem[i]);
        viewHolder.leftIv.setImageDrawable(fragmentActivity.getDrawable(MedicalHistoryConstants.getIcon(listItem[i])));
        viewHolder.itemCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                changeTitleInterface.onTitleChange(listItem[i]);

                MedicalHistoryEditFragment medicalHistoryEditFragment = new MedicalHistoryEditFragment();
                bundle.putString(ArgumentKeys.HISTORY_TYPE, listItem[i]);
                medicalHistoryEditFragment.setArguments(bundle);
                medicalHistoryEditFragment.setTargetFragment(fragment, RequestID.REQ_HISTORY_UPDATE);
                showSubFragmentInterface.onShowFragment(medicalHistoryEditFragment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItem.length;
    }

    public void updateBundle(Bundle arguments) {
        this.bundle = arguments;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout itemCl;
        private ImageView leftIv;
        private TextView itemTitleTv;
        private ImageView rightIv;
        private View bottomView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCl = (ConstraintLayout) itemView.findViewById(R.id.item_cl);
            leftIv = (ImageView) itemView.findViewById(R.id.left_iv);
            itemTitleTv = (TextView) itemView.findViewById(R.id.item_title_tv);
            rightIv = (ImageView) itemView.findViewById(R.id.right_iv);
            bottomView = (View) itemView.findViewById(R.id.bottom_view);
        }
    }
}
