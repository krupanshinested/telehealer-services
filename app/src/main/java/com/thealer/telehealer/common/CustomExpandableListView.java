package com.thealer.telehealer.common;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.emptyState.EmptyStateUtil;

/**
 * Created by Aswin on 20,November,2018
 */
public class CustomExpandableListView extends ConstraintLayout {

    private ExpandableListView expandableListView;
    private ConstraintLayout recyclerEmptyStateView;
    private ImageView emptyIv;
    private TextView emptyTitleTv;
    private TextView emptyMessageTv;
    private CustomButton emptyActionBtn;
    private ProgressBar recyclerLoader;
    private Context context;
    private String emptyState;
    private boolean isScrollable = false;
    private int totalCount = 0;
    private OnPaginateInterface onPaginateInterface;


    public CustomExpandableListView(Context context) {
        super(context);
    }

    public CustomExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_custom_expandable_list_view, this, true);
        initView(view);
    }


    private void initView(View view) {
        expandableListView = (ExpandableListView) view.findViewById(R.id.expandable_lv);
        recyclerEmptyStateView = (ConstraintLayout) view.findViewById(R.id.recycler_empty_state_view);
        emptyIv = (ImageView) view.findViewById(R.id.empty_iv);
        emptyTitleTv = (TextView) view.findViewById(R.id.empty_title_tv);
        emptyMessageTv = (TextView) view.findViewById(R.id.empty_message_tv);
        emptyActionBtn = (CustomButton) view.findViewById(R.id.empty_action_btn);
        recyclerLoader = (ProgressBar) view.findViewById(R.id.recycler_loader);

        expandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:
                        updateView();
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (isScrollable) {
                    if (firstVisibleItem < totalItemCount -1){
                        if ((firstVisibleItem + visibleItemCount == totalItemCount)) {
                            isScrollable = false;
                            onPaginateInterface.onPaginate();
                            showProgressBar();
                        } else {
                            hideProgressBar();
                        }
                    }else {
                        hideProgressBar();
                        isScrollable = false;
                    }
                } else {
                    hideProgressBar();
                }
                Log.e("aswin", "onScroll: " + firstVisibleItem + " " + visibleItemCount + " " + totalItemCount);
            }
        });
    }

    public void setEmptyState(String emptyState) {
        this.emptyState = emptyState;
        showEmptyState(emptyState);
    }

    public void setScrollable(boolean scrollable) {
        isScrollable = scrollable;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }


    public void setOnPaginateInterface(OnPaginateInterface onPaginateInterface) {
        this.onPaginateInterface = onPaginateInterface;
    }

    public ExpandableListView getExpandableView() {
        return expandableListView;
    }

    public void updateView() {
        if (expandableListView.getCount() > 0) {
            expandableListView.setVisibility(VISIBLE);
            recyclerEmptyStateView.setVisibility(GONE);
        } else {
            expandableListView.setVisibility(GONE);
            recyclerEmptyStateView.setVisibility(VISIBLE);
            showEmptyState(emptyState);
        }
    }

    public void showEmptyState() {
        expandableListView.setVisibility(GONE);
        recyclerEmptyStateView.setVisibility(VISIBLE);
        showEmptyState(emptyState);
    }

    public void hideEmptyState() {
        expandableListView.setVisibility(VISIBLE);
        recyclerEmptyStateView.setVisibility(GONE);
    }

    public void showEmptyState(String emptyState) {
        if (emptyState != null) {
            String title = EmptyStateUtil.getTitle(emptyState);
            String message = EmptyStateUtil.getMessage(emptyState);
            int image = EmptyStateUtil.getImage(emptyState);

            emptyTitleTv.setText(title);
            emptyMessageTv.setText(message);
            emptyIv.setImageDrawable(context.getDrawable(image));
        }
    }

    public void showProgressBar() {
        recyclerLoader.setVisibility(VISIBLE);
    }

    public void hideProgressBar() {
        recyclerLoader.setVisibility(GONE);
    }
}
