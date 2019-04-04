package com.thealer.telehealer.common;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.manager.helper.NetworkUtil;
import com.thealer.telehealer.common.emptyState.EmptyStateUtil;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;

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
    private ImageView recyclerLoader;
    private Context context;
    private String emptyState;
    private boolean isScrollable = false;
    private int totalCount = 0;
    private OnPaginateInterface onPaginateInterface;
    private CustomSwipeRefreshLayout swipeLayout;
    private LinearLayout emptyLl;

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
        recyclerLoader = (ImageView) view.findViewById(R.id.recycler_loader);

        Glide.with(context).load(R.raw.throbber).into(recyclerLoader);
        swipeLayout = (CustomSwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        emptyLl = (LinearLayout) view.findViewById(R.id.empty_ll);

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
                if (totalItemCount < totalCount && isScrollable) {
                    if (firstVisibleItem < totalItemCount - 1) {
                        if ((firstVisibleItem + visibleItemCount == totalItemCount)) {
                            isScrollable = false;
                            onPaginateInterface.onPaginate();
                            showProgressBar();
                        } else {
                            hideProgressBar();
                        }
                    } else {
                        hideProgressBar();
                        isScrollable = false;
                    }
                } else {
                    hideProgressBar();
                }
            }
        });
    }

    public void setActionClickListener(OnClickListener onClickListener) {
        emptyActionBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(v);
                setEmptyState(emptyState);
            }
        });
    }

    public void setErrorModel(LifecycleOwner owner, MutableLiveData<ErrorModel> errorModelMutableLiveData) {
        errorModelMutableLiveData.observe(owner,
                new Observer<ErrorModel>() {
                    @Override
                    public void onChanged(@Nullable ErrorModel errorModel) {
                        swipeLayout.setRefreshing(false);
                        if (errorModel != null) {
                            showNetworkEmptyState(errorModel.getCode());
                        }
                    }
                });
    }

    public void showNetworkEmptyState(int code) {
        String emptyState = this.emptyState;
        boolean actionVisible = false;
        switch (code) {
            case BaseApiViewModel
                    .NETWORK_ERROR_CODE:
                emptyState = EmptyViewConstants.EMPTY_NO_NETWORK;
                actionVisible = true;
                break;
            case 500:
                //handle server down here
                break;
        }
        showEmptyState(emptyState);

        if (actionVisible) {
            showOrhideEmptyState(true);
            emptyActionBtn.setVisibility(VISIBLE);
            emptyActionBtn.setText(context.getString(R.string.retry));
        }
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

    public void showOrhideEmptyState(boolean show) {
        if (show) {
            expandableListView.setVisibility(GONE);
            recyclerEmptyStateView.setVisibility(VISIBLE);
        } else {
            expandableListView.setVisibility(VISIBLE);
            recyclerEmptyStateView.setVisibility(GONE);
        }
    }

    public void showEmptyState() {
        expandableListView.setVisibility(GONE);
        recyclerEmptyStateView.setVisibility(VISIBLE);
        showEmptyState(emptyState);
        emptyLl.setClickable(true);
    }

    public void hideEmptyState() {
        expandableListView.setVisibility(VISIBLE);
        recyclerEmptyStateView.setVisibility(GONE);
        emptyLl.setClickable(false);

    }

    public void showEmptyState(String emptyState) {
        if (emptyState != null) {
            String title = EmptyStateUtil.getTitle(context, emptyState);
            String message = EmptyStateUtil.getMessage(context, emptyState);
            int image = EmptyStateUtil.getImage(emptyState);

            emptyTitleTv.setText(title);
            emptyMessageTv.setText(message);
            emptyIv.setImageDrawable(context.getDrawable(image));

            emptyActionBtn.setVisibility(GONE);

            if (!emptyState.equals(EmptyViewConstants.EMPTY_NO_NETWORK) && !NetworkUtil.isOnline(context)) {
                showNetworkEmptyState(BaseApiViewModel.NETWORK_ERROR_CODE);
            }
        }
    }

    public void showProgressBar() {
        recyclerLoader.setVisibility(VISIBLE);
    }

    public void hideProgressBar() {
        recyclerLoader.setVisibility(GONE);
    }

    public void showOrHideMessage(boolean show) {
        if (show) {
            emptyMessageTv.setVisibility(VISIBLE);
        } else {
            emptyMessageTv.setVisibility(GONE);
        }
    }

    public CustomSwipeRefreshLayout getSwipeLayout() {
        return swipeLayout;
    }

    public void setEmptyStateTitle(String title) {
        emptyTitleTv.setText(title);
    }
}
