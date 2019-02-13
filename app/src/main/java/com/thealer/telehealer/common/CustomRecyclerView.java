package com.thealer.telehealer.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.emptyState.EmptyStateUtil;

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
    private ProgressBar recyclerLoader;
    private Context context;
    private LinearLayoutManager linearLayoutManager;
    private String emptyState;
    private boolean isScrollable = false;
    private int totalCount = 0;
    private OnPaginateInterface onPaginateInterface;
    private CustomSwipeRefreshLayout swipeLayout;

    public CustomRecyclerView(Context context) {
        super(context);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_custom_recycler_view, this, true);
        initView(view);
    }

    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerEmptyStateView = (ConstraintLayout) view.findViewById(R.id.recycler_empty_state_view);
        emptyIv = (ImageView) view.findViewById(R.id.empty_iv);
        emptyTitleTv = (TextView) view.findViewById(R.id.empty_title_tv);
        emptyMessageTv = (TextView) view.findViewById(R.id.empty_message_tv);
        emptyActionBtn = (CustomButton) view.findViewById(R.id.empty_action_btn);
        recyclerLoader = (ProgressBar) view.findViewById(R.id.recycler_loader);
        swipeLayout = (CustomSwipeRefreshLayout) view.findViewById(R.id.swipe_layout);

        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                updateView();
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                updateView();
            }
        });

        recyclerView.setOnScrollChangeListener(new OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (isScrollable) {
                    if (linearLayoutManager.getItemCount() < totalCount) {
                        if (linearLayoutManager.findLastVisibleItemPosition() == linearLayoutManager.getItemCount() - 1) {
                            onPaginateInterface.onPaginate();
                            showProgressBar();
                        }
                    } else {
                        hideProgressBar();
                    }
                } else {
                    hideProgressBar();
                }

                updateView();
            }
        });
    }

    public CustomSwipeRefreshLayout getSwipeLayout(){
        return swipeLayout;
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

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setOnPaginateInterface(OnPaginateInterface onPaginateInterface) {
        this.onPaginateInterface = onPaginateInterface;
    }

    public void updateView() {
        if (linearLayoutManager.getItemCount() > 0) {
            recyclerView.setVisibility(VISIBLE);
            recyclerEmptyStateView.setVisibility(GONE);
        } else {
            recyclerView.setVisibility(GONE);
            recyclerEmptyStateView.setVisibility(VISIBLE);
            showEmptyState(emptyState);
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
        String title = EmptyStateUtil.getTitle(emptyState);
        String message = EmptyStateUtil.getMessage(emptyState);
        int image = EmptyStateUtil.getImage(emptyState);

        emptyTitleTv.setText(title);
        emptyMessageTv.setText(message);
        emptyIv.setImageDrawable(context.getDrawable(image));
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
}
