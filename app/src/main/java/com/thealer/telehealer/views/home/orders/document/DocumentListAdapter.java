package com.thealer.telehealer.views.home.orders.document;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.orders.documents.DocumentsApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Aswin on 06,March,2019
 */
public class DocumentListAdapter extends RecyclerView.Adapter<DocumentListAdapter.ViewHolder> {
    private List<DocumentsApiResponseModel.ResultBean> resultBeanList = new ArrayList<>();
    private FragmentActivity activity;
    private DocumentsApiResponseModel documentsApiResponseModel;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private boolean isFromHome;
    private int spanCount = 1;

    public DocumentListAdapter(FragmentActivity activity, boolean isFromHome, int spanCount) {
        this.activity = activity;
        showSubFragmentInterface = (ShowSubFragmentInterface) activity;
        this.isFromHome = isFromHome;
        this.spanCount = spanCount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(activity);

        if (spanCount == 2) {
            view = layoutInflater.inflate(R.layout.adapter_document_grid, viewGroup, false);
        } else {
            view = layoutInflater.inflate(R.layout.adapter_document_list, viewGroup, false);
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        boolean showHeader = false;

        if (i == 0 || (!Utils.getDayMonthYear(resultBeanList.get(i).getCreated_at()).equals(Utils.getDayMonthYear(resultBeanList.get(i - 1).getCreated_at())))) {
            showHeader = true;
        }

        if (showHeader) {
            viewHolder.recentHeaderTv.setVisibility(View.VISIBLE);
            viewHolder.recentHeaderTv.setText(Utils.getDayMonthYear(resultBeanList.get(i).getCreated_at()));
        } else {
            viewHolder.recentHeaderTv.setVisibility(View.GONE);
        }

        Utils.setImageWithGlide(activity.getApplicationContext(), viewHolder.documentIv, resultBeanList.get(i).getPath(), activity.getDrawable(R.drawable.profile_placeholder), true);

        GlideUrl glideUrl = Utils.getGlideUrlWithAuth(activity.getApplicationContext(), resultBeanList.get(i).getPath());

        Glide.with(activity.getApplicationContext()).asFile().load(glideUrl).addListener(new RequestListener<File>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {

                int kb = 1000;
                int mb = kb * 1000;

                String size;

                if (resource.length() >= mb) {
                    size = String.format("%.2f %s", ((float) resource.length() / mb), " MB");
                } else {
                    size = String.format("%.2f %s", ((float) resource.length() / kb), " KB");
                }
                viewHolder.sizeTv.setText(resultBeanList.get(i).getCreator().getUserName() + " - " + size);

                return false;
            }
        }).submit();

        viewHolder.titleTv.setText(resultBeanList.get(i).getName());

        viewHolder.sizeTv.setText(resultBeanList.get(i).getCreator().getUserName());

        viewHolder.documentCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                documentsApiResponseModel = new DocumentsApiResponseModel();
                documentsApiResponseModel.setResult(resultBeanList);

                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.USER_DETAIL, documentsApiResponseModel);
                bundle.putBoolean(Constants.IS_FROM_HOME, isFromHome);
                bundle.putInt(Constants.SELECTED_ITEM, resultBeanList.get(i).getUser_file_id());

                ViewDocumentFragment viewDocumentFragment = new ViewDocumentFragment();
                viewDocumentFragment.setArguments(bundle);
                showSubFragmentInterface.onShowFragment(viewDocumentFragment);

            }
        });
    }

    @Override
    public int getItemCount() {
        return resultBeanList.size();
    }

    public void setData(List<DocumentsApiResponseModel.ResultBean> beanList, int page) {
        if (page == 1) {
            this.resultBeanList = beanList;
        } else {
            this.resultBeanList.addAll(beanList);
        }

        Collections.sort(this.resultBeanList, new Comparator<DocumentsApiResponseModel.ResultBean>() {
            @Override
            public int compare(DocumentsApiResponseModel.ResultBean model1, DocumentsApiResponseModel.ResultBean model2) {
                Date date1 = Utils.getDateFromString(model1.getCreated_at());
                Date date2 = Utils.getDateFromString(model2.getCreated_at());
                return date2.compareTo(date1);
            }
        });

        notifyDataSetChanged();
    }

    public void setSpanCount(int spanCount) {
        this.spanCount = spanCount;
        notifyItemRangeChanged(0, resultBeanList.size());
    }

    public List<DocumentsApiResponseModel.ResultBean> getDataList() {
        return resultBeanList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView recentHeaderTv;
        private CardView documentCv;
        private ImageView documentIv;
        private TextView titleTv;
        private TextView sizeTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recentHeaderTv = (TextView) itemView.findViewById(R.id.recent_header_tv);
            documentCv = (CardView) itemView.findViewById(R.id.document_cv);
            documentIv = (ImageView) itemView.findViewById(R.id.document_iv);
            titleTv = (TextView) itemView.findViewById(R.id.title_tv);
            sizeTv = (TextView) itemView.findViewById(R.id.size_tv);
        }
    }
}
