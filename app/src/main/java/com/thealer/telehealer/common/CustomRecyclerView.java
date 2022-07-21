package com.thealer.telehealer.common;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import android.content.Context;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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
public class CustomRecyclerView extends ConstraintLayout {

    private RecyclerView recyclerView;
    private ConstraintLayout recyclerEmptyStateView;
    private ImageView emptyIv;
    private TextView emptyTitleTv;
    private TextView emptyMessageTv;
    private CustomButton emptyActionBtn;
    private ImageView recyclerLoader;
    private Context context;
    private LinearLayoutManager linearLayoutManager;
    private String emptyState;
    private boolean isScrollable = false, isLayoutChanging = false;
    private OnPaginateInterface onPaginateInterface;
    private CustomSwipeRefreshLayout swipeLayout;
    private boolean isNextPageAvailable;

    public CustomRecyclerView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public CustomRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    private void initView() {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_custom_recycler_view, this, true);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerEmptyStateView = (ConstraintLayout) view.findViewById(R.id.recycler_empty_state_view);
        emptyIv = (ImageView) view.findViewById(R.id.empty_iv);
        emptyTitleTv = (TextView) view.findViewById(R.id.empty_title_tv);
        emptyMessageTv = (TextView) view.findViewById(R.id.empty_message_tv);
        emptyActionBtn = (CustomButton) view.findViewById(R.id.empty_action_btn);
        recyclerLoader = (ImageView) view.findViewById(R.id.recycler_loader);
        swipeLayout = (CustomSwipeRefreshLayout) view.findViewById(R.id.swipe_layout);

        Glide.with(context).load(R.raw.throbber).into(recyclerLoader);

        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setOnScrollChangeListener(new OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (isScrollable && !isLayoutChanging) {
                    if (isNextPageAvailable) {
                        if (linearLayoutManager.findLastVisibleItemPosition() == linearLayoutManager.getItemCount() - 1) {
                            onPaginateInterface.onPaginate();
                            showProgressBar();
                        }
                    } else {
                       // hideProgressBar();
                    }
                } else {
                    //hideProgressBar();
                }

                updateView();
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
                            if (!errorModel.geterrorCode().isEmpty() && !errorModel.geterrorCode().equals("SUBSCRIPTION")) {
                                showNetworkEmptyState(errorModel.getCode());
                            }
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

    public CustomSwipeRefreshLayout getSwipeLayout() {
        return swipeLayout;
    }

    public void setEmptyState(String emptyState) {
        this.emptyState = emptyState;
        showEmptyState(emptyState);
    }

    public void setScrollable(boolean scrollable) {
        isScrollable = scrollable;
    }

    public void setNextPage(Object nextPage) {
        isNextPageAvailable = nextPage != null;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public ConstraintLayout getEmptyStateView() {
        return recyclerEmptyStateView;
    }

    public void setOnPaginateInterface(OnPaginateInterface onPaginateInterface) {
        this.onPaginateInterface = onPaginateInterface;
    }

    public void updateView() {
        if (!isLayoutChanging) {
            if (linearLayoutManager.getItemCount() > 0) {
                recyclerView.setVisibility(VISIBLE);
                recyclerEmptyStateView.setVisibility(GONE);
            } else {
                recyclerView.setVisibility(GONE);
                recyclerEmptyStateView.setVisibility(VISIBLE);
                showEmptyState(emptyState);
            }
        } else {
            isLayoutChanging = false;
        }
    }

    public void showOrhideEmptyState(boolean show) {
        if (show) {
            recyclerView.setVisibility(GONE);
            recyclerEmptyStateView.setVisibility(VISIBLE);
        } else {
            recyclerView.setVisibility(VISIBLE);
            recyclerEmptyStateView.setVisibility(GONE);
        }
    }

    public void showEmptyState(String emptyState) {
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

    public void setEmptyStateTitle(String title) {
        emptyTitleTv.setText(title);
    }

    public void hideEmptyState() {
        recyclerEmptyStateView.setVisibility(GONE);
    }

    public void showEmptyState() {
        recyclerEmptyStateView.setVisibility(VISIBLE);
    }

    public LinearLayoutManager getLayoutManager() {
        return linearLayoutManager;
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

    public void setLayoutManager(LinearLayoutManager layoutManager) {
        isLayoutChanging = true;
        this.linearLayoutManager = layoutManager;
        recyclerView.setLayoutManager(layoutManager);
    }
}
