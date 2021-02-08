package com.thealer.telehealer.views.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.DoctorGroupedAssociations;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.Animation.CustomUserListItemView;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.monitoring.diet.DietListingFragment;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aswin on 13,November,2018
 */
public class DoctorPatientListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_HEADER = 1;
    private final int TYPE_ITEM = 2;

    private FragmentActivity fragmentActivity;
    private OnActionCompleteInterface onActionCompleteInterface;
    private boolean isDietView;
    private Bundle bundle;
    private List<AssociationAdapterListModel> adapterListModels;

    public DoctorPatientListAdapter(FragmentActivity activity, boolean isDietView, Bundle bundle) {
        fragmentActivity = activity;
        adapterListModels = new ArrayList<>();
        onActionCompleteInterface = (OnActionCompleteInterface) activity;
        this.isDietView = isDietView;
        this.bundle = bundle;
        if (bundle == null) {
            this.bundle = new Bundle();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        switch (i) {
            case TYPE_HEADER:
                view = LayoutInflater.from(fragmentActivity).inflate(R.layout.adapter_list_header_view, viewGroup, false);
                return new HeaderHolder(view);
            case TYPE_ITEM:
                view = LayoutInflater.from(fragmentActivity).inflate(R.layout.adapter_doctor_patient_list, viewGroup, false);
                return new ItemViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {

        switch (adapterListModels.get(i).getType()) {
            case TYPE_HEADER:
                HeaderHolder headerHolder = (HeaderHolder) holder;
                headerHolder.headerTv.setText(adapterListModels.get(i).getTitle());
                break;
            case TYPE_ITEM:
                ItemViewHolder viewHolder = (ItemViewHolder) holder;
                CommonUserApiResponseModel userModel = adapterListModels.get(i).getCommonUserApiResponseModel();

                viewHolder.titleTv.setText(userModel.getUserDisplay_name());
                loadAvatar(viewHolder.avatarCiv, userModel.getUser_avatar());

                if (UserType.isUserDoctor()) {
                    viewHolder.actionIv.setVisibility(View.VISIBLE);
                    Utils.setGenderImage(fragmentActivity, viewHolder.actionIv, userModel.getGender());
                    viewHolder.userListIv.showCardStatus(userModel.getPayment_account_info());
                } else if (UserType.isUserAssistant()) {
                    viewHolder.actionIv.setVisibility(View.GONE);
                } else if (UserType.isUserPatient()) {
                    viewHolder.actionIv.setVisibility(View.GONE);
                }
                viewHolder.subTitleTv.setText(userModel.getDisplayInfo());

                viewHolder.patientTemplateCv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.hideKeyboard(fragmentActivity);
                        proceed(userModel);
                    }
                });

                viewHolder.userListIv.setStatus(userModel.getStatus(), userModel.getLast_active());
                break;
        }
    }

    private void proceed(CommonUserApiResponseModel resultBean) {
        bundle.putSerializable(Constants.USER_DETAIL, resultBean);
        if (!isDietView) {
            bundle.putBoolean(ArgumentKeys.SHOW_FAVORITES, true);
            onActionCompleteInterface.onCompletionResult(RequestID.REQ_SHOW_DETAIL_VIEW, true, bundle);
        } else {
            ShowSubFragmentInterface showSubFragmentInterface = (ShowSubFragmentInterface) fragmentActivity;
            DietListingFragment dietListingFragment = new DietListingFragment();
            dietListingFragment.setArguments(bundle);
            showSubFragmentInterface.onShowFragment(dietListingFragment);
        }
    }

    private void loadAvatar(ImageView imageView, String user_avatar) {
        Utils.setImageWithGlide(fragmentActivity.getApplicationContext(), imageView, user_avatar, fragmentActivity.getDrawable(R.drawable.profile_placeholder), true, true);
    }

    @Override
    public int getItemViewType(int position) {
        return adapterListModels.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return adapterListModels.size();
    }

    public void setData(List<CommonUserApiResponseModel> associationApiResponseModelResult, int page) {
        List<CommonUserApiResponseModel> favoriteList = new ArrayList<>();
        List<CommonUserApiResponseModel> otherList = new ArrayList<>();

        if (page == 1) {
            adapterListModels.clear();
        }

        for (int i = 0; i < associationApiResponseModelResult.size(); i++) {
            if (associationApiResponseModelResult.get(i).getFavorite() != null && associationApiResponseModelResult.get(i).getFavorite()) {
                favoriteList.add(associationApiResponseModelResult.get(i));
            } else {
                otherList.add(associationApiResponseModelResult.get(i));
            }
        }

        if (!favoriteList.isEmpty()) {

            adapterListModels.add(new AssociationAdapterListModel(TYPE_HEADER, fragmentActivity.getString(R.string.favorite)));

            for (int i = 0; i < favoriteList.size(); i++) {
                adapterListModels.add(new AssociationAdapterListModel(TYPE_ITEM, favoriteList.get(i)));
            }

            if (!otherList.isEmpty()) {
                adapterListModels.add(new AssociationAdapterListModel(TYPE_HEADER, fragmentActivity.getString(R.string.others)));
            }
        }
        for (int i = 0; i < otherList.size(); i++) {
            adapterListModels.add(new AssociationAdapterListModel(TYPE_ITEM, otherList.get(i)));
        }

        notifyDataSetChanged();
    }

    public void setData(List<DoctorGroupedAssociations> associationApiResponseModelResult) {
        adapterListModels.clear();

        boolean needHeader = true;
        if (associationApiResponseModelResult.size() == 1 && associationApiResponseModelResult.get(0).getGroup_name().equals("Others")) {
            needHeader = false;
        }

        for (int i = 0; i < associationApiResponseModelResult.size(); i++) {
            DoctorGroupedAssociations associations = associationApiResponseModelResult.get(i);
            if (needHeader) {
                adapterListModels.add(new AssociationAdapterListModel(TYPE_HEADER, associations.getGroup_name()));
            }

            for (int j = 0; j < associations.getDoctors().size(); j++) {
                adapterListModels.add(new AssociationAdapterListModel(TYPE_ITEM, associations.getDoctors().get(j)));
            }
        }

        notifyDataSetChanged();
    }


    public class HeaderHolder extends RecyclerView.ViewHolder {
        private TextView headerTv;

        public HeaderHolder(@NonNull View itemView) {
            super(itemView);
            headerTv = (TextView) itemView.findViewById(R.id.header_tv);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private CardView patientTemplateCv;
        private CircleImageView avatarCiv;
        private TextView titleTv;
        private TextView subTitleTv;
        private ImageView actionIv;
        private CustomUserListItemView userListIv;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            userListIv = (CustomUserListItemView) itemView.findViewById(R.id.user_list_iv);
            patientTemplateCv = userListIv.getListItemCv();
            avatarCiv = userListIv.getAvatarCiv();
            titleTv = userListIv.getListTitleTv();
            subTitleTv = userListIv.getListSubTitleTv();
            actionIv = userListIv.getActionIv();

        }
    }

    public class AssociationAdapterListModel {
        private int type;
        private String title;
        private CommonUserApiResponseModel commonUserApiResponseModel;

        public AssociationAdapterListModel(int type, String title) {
            this.type = type;
            this.title = title;
        }

        public AssociationAdapterListModel(int type, CommonUserApiResponseModel commonUserApiResponseModel) {
            this.type = type;
            this.commonUserApiResponseModel = commonUserApiResponseModel;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public CommonUserApiResponseModel getCommonUserApiResponseModel() {
            return commonUserApiResponseModel;
        }

        public void setCommonUserApiResponseModel(CommonUserApiResponseModel commonUserApiResponseModel) {
            this.commonUserApiResponseModel = commonUserApiResponseModel;
        }
    }
}
