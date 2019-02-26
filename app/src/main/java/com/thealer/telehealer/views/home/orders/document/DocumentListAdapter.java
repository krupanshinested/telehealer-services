package com.thealer.telehealer.views.home.orders.document;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.orders.documents.DocumentsApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Aswin on 29,November,2018
 */
class DocumentListAdapter extends BaseExpandableListAdapter {
    private FragmentActivity activity;
    private List<String> headerList;
    private HashMap<String, List<DocumentsApiResponseModel.ResultBean>> childList;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private DocumentsApiResponseModel documentsApiResponseModel;
    private boolean isFromHome;

    public DocumentListAdapter(FragmentActivity activity, List<String> headerList, HashMap<String, List<DocumentsApiResponseModel.ResultBean>> childList, boolean isFromHome) {
        this.activity = activity;
        this.headerList = headerList;
        this.childList = childList;
        showSubFragmentInterface = (ShowSubFragmentInterface) activity;
        this.isFromHome = isFromHome;
    }

    @Override
    public int getGroupCount() {
        return headerList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childList.get(headerList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return headerList.get(groupPosition);
    }

    @Override
    public DocumentsApiResponseModel.ResultBean getChild(int groupPosition, int childPosition) {
        return childList.get(headerList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(activity).inflate(R.layout.adapter_expandable_list_header_view, parent, false);

        TextView recentHeaderTv = (TextView) convertView.findViewById(R.id.recent_header_tv);

        recentHeaderTv.setText(headerList.get(groupPosition));

        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(activity).inflate(R.layout.adapter_document_list, parent, false);
        CardView documentCv;
        ImageView documentIv;
        TextView titleTv;
        TextView sizeTv;

        documentCv = (CardView) convertView.findViewById(R.id.document_cv);
        documentIv = (ImageView) convertView.findViewById(R.id.document_iv);
        titleTv = (TextView) convertView.findViewById(R.id.title_tv);
        sizeTv = (TextView) convertView.findViewById(R.id.size_tv);

        Utils.setImageWithGlide(activity, documentIv, childList.get(headerList.get(groupPosition)).get(childPosition).getPath(), activity.getDrawable(R.drawable.document_placeholder_drawable), true);

        titleTv.setText(childList.get(headerList.get(groupPosition)).get(childPosition).getName());

        documentCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (documentsApiResponseModel != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.USER_DETAIL, documentsApiResponseModel);
                    bundle.putBoolean(Constants.IS_FROM_HOME, isFromHome);
                    bundle.putInt(Constants.SELECTED_ITEM, childList.get(headerList.get(groupPosition)).get(childPosition).getUser_file_id());

                    ViewDocumentFragment viewDocumentFragment = new ViewDocumentFragment();
                    viewDocumentFragment.setArguments(bundle);
                    showSubFragmentInterface.onShowFragment(viewDocumentFragment);
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setData(List<String> newHeaderList, HashMap<String, List<DocumentsApiResponseModel.ResultBean>> newChildList, DocumentsApiResponseModel documentsApiResponseModel, int page) {
        this.headerList = newHeaderList;
        this.childList = newChildList;
        this.documentsApiResponseModel = documentsApiResponseModel;
        notifyDataSetChanged();
    }

}
