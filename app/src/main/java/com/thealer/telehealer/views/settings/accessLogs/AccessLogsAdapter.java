package com.thealer.telehealer.views.settings.accessLogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.accessLog.AccessLogApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aswin on 15,July,2019
 */
public class AccessLogsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_ITEM = 2;

    private Context context;
    private List<AccessLogApiResponseModel.ResultBean> resultBean;
    private List<AccessLogAdapterModel> adapterModelList;

    public AccessLogsAdapter(Context context) {
        this.context = context;
        adapterModelList = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_HEADER:
                view = LayoutInflater.from(context).inflate(R.layout.adapter_list_header_view, parent, false);
                return new DateHolder(view);
            case TYPE_ITEM:
                view = LayoutInflater.from(context).inflate(R.layout.adapter_access_log_view, parent, false);
                return new ItemHolder(view);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (adapterModelList.get(position).getType()) {
            case TYPE_HEADER:
                DateHolder dateHolder = (DateHolder) holder;
                dateHolder.headerTv.setText(adapterModelList.get(position).getDate());
                break;
            case TYPE_ITEM:
                ItemHolder itemHolder = (ItemHolder) holder;
                AccessLogApiResponseModel.ResultBean resultBean = adapterModelList.get(position).getItem();

                itemHolder.logTimeTv.setText(Utils.getFormatedTime(resultBean.getTimestamp()));
                itemHolder.logTypeTv.setText(resultBean.getType());

                String userName, userAvatar;

                if (resultBean.getAccessed_users().size() > 0) {
                    if (resultBean.getAccessed_users().get(0).getRole().equals(Constants.ROLE_DOCTOR)) {
                        userName = Utils.getDoctorDisplayName(resultBean.getAccessed_users().get(0).getFirst_name(), resultBean.getAccessed_users().get(0).getLast_name(), "");
                    } else {
                        userName = Utils.getPatientDisplayName(resultBean.getAccessed_users().get(0).getFirst_name(), resultBean.getAccessed_users().get(0).getLast_name());
                    }
                    userAvatar = resultBean.getAccessed_users().get(0).getUser_avatar();
                } else {
                    userName = UserDetailPreferenceManager.getUserDisplayName();
                    userAvatar = UserDetailPreferenceManager.getUser_avatar();
                }

                Utils.setImageWithGlide(context, itemHolder.userAvatarCiv, userAvatar, context.getDrawable(R.drawable.profile_placeholder), true, true);
                itemHolder.userNameTv.setText(userName);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return adapterModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return adapterModelList.get(position).getType();
    }

    public void setData(List<AccessLogApiResponseModel.ResultBean> result, int page) {
        if (page == 1) {
            resultBean = result;
        } else {
            resultBean.addAll(result);
        }

        Collections.sort(resultBean, new Comparator<AccessLogApiResponseModel.ResultBean>() {
            @Override
            public int compare(AccessLogApiResponseModel.ResultBean o1, AccessLogApiResponseModel.ResultBean o2) {
                return Utils.getDateFromString(o2.getTimestamp()).compareTo(Utils.getDateFromString(o1.getTimestamp()));
            }
        });

        adapterModelList.clear();

        for (int i = 0; i < resultBean.size(); i++) {
            if (i == 0 || !Utils.getDayMonthYear(resultBean.get(i).getTimestamp()).equals(Utils.getDayMonthYear(resultBean.get(i - 1).getTimestamp()))) {
                adapterModelList.add(new AccessLogAdapterModel(TYPE_HEADER, Utils.getDayMonthYear(resultBean.get(i).getTimestamp())));
            }

            adapterModelList.add(new AccessLogAdapterModel(TYPE_ITEM, resultBean.get(i)));
        }

        notifyDataSetChanged();
    }

    public class DateHolder extends RecyclerView.ViewHolder {
        private TextView headerTv;

        public DateHolder(@NonNull View itemView) {
            super(itemView);
            headerTv = (TextView) itemView.findViewById(R.id.header_tv);
            headerTv.setTextColor(context.getColor(R.color.colorBlack));
            headerTv.setBackgroundColor(context.getColor(R.color.color_grey_light_background));
            headerTv.setPadding(16, 16, 16, 16);
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private TextView logTimeTv;
        private CircleImageView userAvatarCiv;
        private TextView userNameTv;
        private TextView logTypeTv;
        private View bottomView;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            logTimeTv = (TextView) itemView.findViewById(R.id.log_time_tv);
            userAvatarCiv = (CircleImageView) itemView.findViewById(R.id.user_avatar_civ);
            userNameTv = (TextView) itemView.findViewById(R.id.user_name_tv);
            logTypeTv = (TextView) itemView.findViewById(R.id.log_type_tv);
            bottomView = (View) itemView.findViewById(R.id.bottom_view);
        }
    }

    private class AccessLogAdapterModel {
        private int type;
        private String date;
        private AccessLogApiResponseModel.ResultBean item;

        public AccessLogAdapterModel(int type, String date) {
            this.type = type;
            this.date = date;
        }

        public AccessLogAdapterModel(int type, AccessLogApiResponseModel.ResultBean item) {
            this.type = type;
            this.item = item;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public AccessLogApiResponseModel.ResultBean getItem() {
            return item;
        }

        public void setItem(AccessLogApiResponseModel.ResultBean item) {
            this.item = item;
        }
    }

}
