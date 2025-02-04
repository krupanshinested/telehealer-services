package com.thealer.telehealer.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.VisibleForTesting;
import androidx.core.content.ContextCompat;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ListViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.thealer.telehealer.R;


/**
 * Created by Aswin on 13,February,2019
 */
public class CustomSwipeRefreshLayout extends ViewGroup implements NestedScrollingParent, NestedScrollingChild {

    public static final int LARGE = 0;
    public static final int DEFAULT = 1;
    public static final int DEFAULT_SLINGSHOT_DISTANCE = -1;
    @VisibleForTesting
    static final int CIRCLE_DIAMETER = 40;
    @VisibleForTesting
    static final int CIRCLE_DIAMETER_LARGE = 56;
    private static final String LOG_TAG = CustomSwipeRefreshLayout.class.getSimpleName();
    private static final int MAX_ALPHA = 255;
    private static final int STARTING_PROGRESS_ALPHA = 76;
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2.0F;
    private static final int INVALID_POINTER = -1;
    private static final float DRAG_RATE = 0.5F;
    private static final float MAX_PROGRESS_ANGLE = 0.8F;
    private static final int SCALE_DOWN_DURATION = 150;
    private static final int ALPHA_ANIMATION_DURATION = 300;
    private static final int ANIMATE_TO_TRIGGER_DURATION = 200;
    private static final int ANIMATE_TO_START_DURATION = 200;
    private static final int CIRCLE_BG_LIGHT = -328966;
    private static final int DEFAULT_CIRCLE_TARGET = 64;
    private View mTarget;
    CustomSwipeRefreshLayout.OnRefreshListener mListener;
    boolean mRefreshing;
    private int mTouchSlop;
    private float mTotalDragDistance;
    private float mTotalUnconsumed;
    private final NestedScrollingParentHelper mNestedScrollingParentHelper;
    private final NestedScrollingChildHelper mNestedScrollingChildHelper;
    private final int[] mParentScrollConsumed;
    private final int[] mParentOffsetInWindow;
    private boolean mNestedScrollInProgress;
    private int mMediumAnimationDuration;
    int mCurrentTargetOffsetTop;
    private float mInitialMotionY;
    private float mInitialDownY;
    private boolean mIsBeingDragged;
    private int mActivePointerId;
    boolean mScale;
    private boolean mReturningToStart;
    private final DecelerateInterpolator mDecelerateInterpolator;
    private static final int[] LAYOUT_ATTRS = new int[]{16842766};
    CircleImageView mCircleView;
    //    CircleImageView imageView;
    private int mCircleViewIndex;
    protected int mFrom;
    float mStartingScale;
    protected int mOriginalOffsetTop;
    int mSpinnerOffsetEnd;
    int mCustomSlingshotDistance;
    //    CircularProgressDrawable mProgress;
    private Animation mScaleAnimation;
    private Animation mScaleDownAnimation;
    private Animation mAlphaStartAnimation;
    private Animation mAlphaMaxAnimation;
    private Animation mScaleDownToStartAnimation;
    boolean mNotify;
    private int mCircleDiameter;
    boolean mUsingCustomStart;
    private CustomSwipeRefreshLayout.OnChildScrollUpCallback mChildScrollUpCallback;
    private Animation.AnimationListener mRefreshListener;
    private final Animation mAnimateToCorrectPosition;
    private final Animation mAnimateToStartPosition;

    void reset() {
        this.mCircleView.clearAnimation();
//        this.mProgress.stop();
        this.mCircleView.setVisibility(GONE);
        this.setColorViewAlpha(255);
        if (this.mScale) {
            this.setAnimationProgress(0.0F);
        } else {
            this.setTargetOffsetTopAndBottom(this.mOriginalOffsetTop - this.mCurrentTargetOffsetTop);
        }

        this.mCurrentTargetOffsetTop = this.mCircleView.getTop();
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            this.reset();
        }

    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.reset();
    }

    private void setColorViewAlpha(int targetAlpha) {
        this.mCircleView.getBackground().setAlpha(targetAlpha);
//        this.mProgress.setAlpha(targetAlpha);
    }

    public void setProgressViewOffset(boolean scale, int start, int end) {
        this.mScale = scale;
        this.mOriginalOffsetTop = start;
        this.mSpinnerOffsetEnd = end;
        this.mUsingCustomStart = true;
        this.reset();
        this.mRefreshing = false;
    }

    public int getProgressViewStartOffset() {
        return this.mOriginalOffsetTop;
    }

    public int getProgressViewEndOffset() {
        return this.mSpinnerOffsetEnd;
    }

    public void setProgressViewEndTarget(boolean scale, int end) {
        this.mSpinnerOffsetEnd = end;
        this.mScale = scale;
        this.mCircleView.invalidate();
    }

    public void setSlingshotDistance(@Px int slingshotDistance) {
        this.mCustomSlingshotDistance = slingshotDistance;
    }

    public void setSize(int size) {
        if (size == 0 || size == 1) {
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            if (size == 0) {
                this.mCircleDiameter = (int) (56.0F * metrics.density);
            } else {
                this.mCircleDiameter = (int) (40.0F * metrics.density);
            }

            this.mCircleView.setImageDrawable((Drawable) null);
//            this.mProgress.setStyle(size);
//            this.mCircleView.setImageDrawable(this.mProgress);
        }
    }

    public CustomSwipeRefreshLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mRefreshing = false;
        this.mTotalDragDistance = -1.0F;
        this.mParentScrollConsumed = new int[2];
        this.mParentOffsetInWindow = new int[2];
        this.mActivePointerId = -1;
        this.mCircleViewIndex = -1;
        this.mRefreshListener = new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                if (CustomSwipeRefreshLayout.this.mRefreshing) {
//                    CustomSwipeRefreshLayout.this.mProgress.setAlpha(255);
//                    CustomSwipeRefreshLayout.this.mProgress.start();
                    if (CustomSwipeRefreshLayout.this.mNotify && CustomSwipeRefreshLayout.this.mListener != null) {
                        CustomSwipeRefreshLayout.this.mListener.onRefresh();
                    }

                    CustomSwipeRefreshLayout.this.mCurrentTargetOffsetTop = CustomSwipeRefreshLayout.this.mCircleView.getTop();
                } else {
                    CustomSwipeRefreshLayout.this.reset();
                }

            }
        };
        this.mAnimateToCorrectPosition = new Animation() {
            public void applyTransformation(float interpolatedTime, Transformation t) {
                int endTarget;
                if (!CustomSwipeRefreshLayout.this.mUsingCustomStart) {
                    endTarget = CustomSwipeRefreshLayout.this.mSpinnerOffsetEnd - Math.abs(CustomSwipeRefreshLayout.this.mOriginalOffsetTop);
                } else {
                    endTarget = CustomSwipeRefreshLayout.this.mSpinnerOffsetEnd;
                }

                int targetTop = CustomSwipeRefreshLayout.this.mFrom + (int) ((float) (endTarget - CustomSwipeRefreshLayout.this.mFrom) * interpolatedTime);
                int offset = targetTop - CustomSwipeRefreshLayout.this.mCircleView.getTop();
                CustomSwipeRefreshLayout.this.setTargetOffsetTopAndBottom(offset);
//                CustomSwipeRefreshLayout.this.mProgress.setArrowScale(1.0F - interpolatedTime);
            }
        };
        this.mAnimateToStartPosition = new Animation() {
            public void applyTransformation(float interpolatedTime, Transformation t) {
                CustomSwipeRefreshLayout.this.moveToStart(interpolatedTime);
            }
        };
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mMediumAnimationDuration = 2000;
        this.setWillNotDraw(false);
        this.mDecelerateInterpolator = new DecelerateInterpolator(2.0F);
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        this.mCircleDiameter = (int) (40.0F * metrics.density);
        this.createProgressView();
        this.setChildrenDrawingOrderEnabled(true);
        this.mSpinnerOffsetEnd = (int) (64.0F * metrics.density);
        this.mTotalDragDistance = (float) this.mSpinnerOffsetEnd;
        this.mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        this.mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        this.setNestedScrollingEnabled(true);
        this.mOriginalOffsetTop = this.mCurrentTargetOffsetTop = -this.mCircleDiameter;
        this.moveToStart(1.0F);
        TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        this.setEnabled(a.getBoolean(0, true));
        a.recycle();
    }


    protected int getChildDrawingOrder(int childCount, int i) {
        if (this.mCircleViewIndex < 0) {
            return i;
        } else if (i == childCount - 1) {
            return this.mCircleViewIndex;
        } else {
            return i >= this.mCircleViewIndex ? i + 1 : i;
        }
    }

    private void createProgressView() {
        this.mCircleView = new CircleImageView(this.getContext(), getContext().getColor(R.color.colorWhite));
//        this.mCircleView = new CardView(getContext());
        this.mCircleView.setAlpha(0.9f);
//        this.mCircleView.setRadius(20);


        DrawableImageViewTarget imageViewTarget = new DrawableImageViewTarget(this.mCircleView);
        Glide.with(this.getContext()).load(R.raw.throbber).into(imageViewTarget);


//        this.mCircleView.addView(imageView);
        this.mCircleView.setVisibility(GONE);
        this.addView(this.mCircleView);
    }

    public void setOnRefreshListener(@Nullable CustomSwipeRefreshLayout.OnRefreshListener listener) {
        this.mListener = listener;
    }

    public void setRefreshing(boolean refreshing) {
        if (refreshing && this.mRefreshing != refreshing) {
            this.mRefreshing = refreshing;
            int endTarget;
            if (!this.mUsingCustomStart) {
                endTarget = this.mSpinnerOffsetEnd + this.mOriginalOffsetTop;
            } else {
                endTarget = this.mSpinnerOffsetEnd;
            }

            this.setTargetOffsetTopAndBottom(endTarget - this.mCurrentTargetOffsetTop);
            this.mNotify = false;
            this.startScaleUpAnimation(this.mRefreshListener);
        } else {
            this.setRefreshing(refreshing, false);
        }

    }

    private void startScaleUpAnimation(Animation.AnimationListener listener) {
        this.mCircleView.setVisibility(VISIBLE);
//        this.mProgress.setAlpha(255);
        this.mScaleAnimation = new Animation() {
            public void applyTransformation(float interpolatedTime, Transformation t) {
                CustomSwipeRefreshLayout.this.setAnimationProgress(interpolatedTime);
            }
        };
        this.mScaleAnimation.setDuration((long) this.mMediumAnimationDuration);
        if (listener != null) {
            this.mCircleView.setAnimationListener(listener);
        }

        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(this.mScaleAnimation);
    }

    void setAnimationProgress(float progress) {
        this.mCircleView.setScaleX(progress);
        this.mCircleView.setScaleY(progress);
    }

    private void setRefreshing(boolean refreshing, boolean notify) {
        if (this.mRefreshing != refreshing) {
            this.mNotify = notify;
            this.ensureTarget();
            this.mRefreshing = refreshing;
            if (this.mRefreshing) {
                this.animateOffsetToCorrectPosition(this.mCurrentTargetOffsetTop, this.mRefreshListener);
            } else {
                this.startScaleDownAnimation(this.mRefreshListener);
            }
        }

    }

    void startScaleDownAnimation(Animation.AnimationListener listener) {
        this.mScaleDownAnimation = new Animation() {
            public void applyTransformation(float interpolatedTime, Transformation t) {
                CustomSwipeRefreshLayout.this.setAnimationProgress(1.0F - interpolatedTime);
            }
        };
        this.mScaleDownAnimation.setDuration(150L);
        this.mCircleView.setAnimationListener(listener);
        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(this.mScaleDownAnimation);
    }

    private void startProgressAlphaStartAnimation() {
//        this.mAlphaStartAnimation = this.startAlphaAnimation(this.mProgress.getAlpha(), 76);
    }

    private void startProgressAlphaMaxAnimation() {
//        this.mAlphaMaxAnimation = this.startAlphaAnimation(this.mProgress.getAlpha(), 255);
    }

    private Animation startAlphaAnimation(final int startingAlpha, final int endingAlpha) {
        Animation alpha = new Animation() {
            public void applyTransformation(float interpolatedTime, Transformation t) {
//                CustomSwipeRefreshLayout.this.mProgress.setAlpha((int) ((float) startingAlpha + (float) (endingAlpha - startingAlpha) * interpolatedTime));
            }
        };
        alpha.setDuration(300L);
        this.mCircleView.setAnimationListener((Animation.AnimationListener) null);
        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(alpha);
        return alpha;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setProgressBackgroundColor(int colorRes) {
        this.setProgressBackgroundColorSchemeResource(colorRes);
    }

    public void setProgressBackgroundColorSchemeResource(@ColorRes int colorRes) {
        this.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this.getContext(), colorRes));
    }

    public void setProgressBackgroundColorSchemeColor(@ColorInt int color) {
        this.mCircleView.setBackgroundColor(color);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setColorScheme(@ColorRes int... colors) {
        this.setColorSchemeResources(colors);
    }

    public void setColorSchemeResources(@ColorRes int... colorResIds) {
        Context context = this.getContext();
        int[] colorRes = new int[colorResIds.length];

        for (int i = 0; i < colorResIds.length; ++i) {
            colorRes[i] = ContextCompat.getColor(context, colorResIds[i]);
        }

        this.setColorSchemeColors(colorRes);
    }

    public void setColorSchemeColors(@ColorInt int... colors) {
        this.ensureTarget();
//        this.mProgress.setColorSchemeColors(colors);
    }

    public boolean isRefreshing() {
        return this.mRefreshing;
    }

    private void ensureTarget() {
        if (this.mTarget == null) {
            for (int i = 0; i < this.getChildCount(); ++i) {
                View child = this.getChildAt(i);
                if (!child.equals(this.mCircleView)) {
                    this.mTarget = child;
                    break;
                }
            }
        }

    }

    public void setDistanceToTriggerSync(int distance) {
        this.mTotalDragDistance = (float) distance;
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = this.getMeasuredWidth();
        int height = this.getMeasuredHeight();
        if (this.getChildCount() != 0) {
            if (this.mTarget == null) {
                this.ensureTarget();
            }

            if (this.mTarget != null) {
                View child = this.mTarget;
                int childLeft = this.getPaddingLeft();
                int childTop = this.getPaddingTop();
                int childWidth = width - this.getPaddingLeft() - this.getPaddingRight();
                int childHeight = height - this.getPaddingTop() - this.getPaddingBottom();
                child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
                int circleWidth = this.mCircleView.getMeasuredWidth();
                int circleHeight = this.mCircleView.getMeasuredHeight();
                this.mCircleView.layout(width / 2 - circleWidth / 2, this.mCurrentTargetOffsetTop, width / 2 + circleWidth / 2, this.mCurrentTargetOffsetTop + circleHeight);
            }
        }
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.mTarget == null) {
            this.ensureTarget();
        }

        if (this.mTarget != null) {
            this.mTarget.measure(MeasureSpec.makeMeasureSpec(this.getMeasuredWidth() - this.getPaddingLeft() - this.getPaddingRight(), MeasureSpec.getMode(1073741824)), MeasureSpec.makeMeasureSpec(this.getMeasuredHeight() - this.getPaddingTop() - this.getPaddingBottom(), MeasureSpec.getMode(1073741824)));
            this.mCircleView.measure(MeasureSpec.makeMeasureSpec(this.mCircleDiameter, MeasureSpec.getMode(1073741824)), MeasureSpec.makeMeasureSpec(this.mCircleDiameter, MeasureSpec.getMode(1073741824)));
            this.mCircleViewIndex = -1;

            for (int index = 0; index < this.getChildCount(); ++index) {
                if (this.getChildAt(index) == this.mCircleView) {
                    this.mCircleViewIndex = index;
                    break;
                }
            }

        }
    }

    public int getProgressCircleDiameter() {
        return this.mCircleDiameter;
    }

    public boolean canChildScrollUp() {
        if (this.mChildScrollUpCallback != null) {
            return this.mChildScrollUpCallback.canChildScrollUp(this, this.mTarget);
        } else {
            return this.mTarget instanceof ListView ? ListViewCompat.canScrollList((ListView) this.mTarget, -1) : this.mTarget.canScrollVertically(-1);
        }
    }

    public void setOnChildScrollUpCallback(@Nullable CustomSwipeRefreshLayout.OnChildScrollUpCallback callback) {
        this.mChildScrollUpCallback = callback;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        this.ensureTarget();
        int action = ev.getActionMasked();
        if (this.mReturningToStart && action == 0) {
            this.mReturningToStart = false;
        }

        if (this.isEnabled() && !this.mReturningToStart && !this.canChildScrollUp() && !this.mRefreshing && !this.mNestedScrollInProgress) {
            int pointerIndex;
            switch (action) {
                case 0:
                    this.setTargetOffsetTopAndBottom(this.mOriginalOffsetTop - this.mCircleView.getTop());
                    this.mActivePointerId = ev.getPointerId(0);
                    this.mIsBeingDragged = false;
                    pointerIndex = ev.findPointerIndex(this.mActivePointerId);
                    if (pointerIndex < 0) {
                        return false;
                    }

                    this.mInitialDownY = ev.getY(pointerIndex);
                    break;
                case 1:
                case 3:
                    this.mIsBeingDragged = false;
                    this.mActivePointerId = -1;
                    break;
                case 2:
                    if (this.mActivePointerId == -1) {
                        Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                        return false;
                    }

                    pointerIndex = ev.findPointerIndex(this.mActivePointerId);
                    if (pointerIndex < 0) {
                        return false;
                    }

                    float y = ev.getY(pointerIndex);
                    this.startDragging(y);
                case 4:
                case 5:
                default:
                    break;
                case 6:
                    this.onSecondaryPointerUp(ev);
            }

            return this.mIsBeingDragged;
        } else {
            return false;
        }
    }

    public void requestDisallowInterceptTouchEvent(boolean b) {
        if ((Build.VERSION.SDK_INT >= 21 || !(this.mTarget instanceof AbsListView)) && (this.mTarget == null || ViewCompat.isNestedScrollingEnabled(this.mTarget))) {
            super.requestDisallowInterceptTouchEvent(b);
        }

    }

    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return this.isEnabled() && !this.mReturningToStart && !this.mRefreshing && (nestedScrollAxes & 2) != 0;
    }

    public void onNestedScrollAccepted(View child, View target, int axes) {
        this.mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        this.startNestedScroll(axes & 2);
        this.mTotalUnconsumed = 0.0F;
        this.mNestedScrollInProgress = true;
    }

    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (dy > 0 && this.mTotalUnconsumed > 0.0F) {
            if ((float) dy > this.mTotalUnconsumed) {
                consumed[1] = dy - (int) this.mTotalUnconsumed;
                this.mTotalUnconsumed = 0.0F;
            } else {
                this.mTotalUnconsumed -= (float) dy;
                consumed[1] = dy;
            }

            this.moveSpinner(this.mTotalUnconsumed);
        }

        if (this.mUsingCustomStart && dy > 0 && this.mTotalUnconsumed == 0.0F && Math.abs(dy - consumed[1]) > 0) {
            this.mCircleView.setVisibility(GONE);
        }

        int[] parentConsumed = this.mParentScrollConsumed;
        if (this.dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, (int[]) null)) {
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
        }

    }

    public int getNestedScrollAxes() {
        return this.mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    public void onStopNestedScroll(View target) {
        this.mNestedScrollingParentHelper.onStopNestedScroll(target);
        this.mNestedScrollInProgress = false;
        if (this.mTotalUnconsumed > 0.0F) {
            this.finishSpinner(this.mTotalUnconsumed);
            this.mTotalUnconsumed = 0.0F;
        }

        this.stopNestedScroll();
    }

    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        this.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, this.mParentOffsetInWindow);
        int dy = dyUnconsumed + this.mParentOffsetInWindow[1];
        if (dy < 0 && !this.canChildScrollUp()) {
            this.mTotalUnconsumed += (float) Math.abs(dy);
            this.moveSpinner(this.mTotalUnconsumed);
        }

    }

    public void setNestedScrollingEnabled(boolean enabled) {
        this.mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    public boolean isNestedScrollingEnabled() {
        return this.mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    public boolean startNestedScroll(int axes) {
        return this.mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    public void stopNestedScroll() {
        this.mNestedScrollingChildHelper.stopNestedScroll();
    }

    public boolean hasNestedScrollingParent() {
        return this.mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return this.mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return this.mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return this.dispatchNestedPreFling(velocityX, velocityY);
    }

    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return this.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return this.mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return this.mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    private boolean isAnimationRunning(Animation animation) {
        return animation != null && animation.hasStarted() && !animation.hasEnded();
    }

    private void moveSpinner(float overscrollTop) {
//        this.mProgress.setArrowEnabled(true);
        float originalDragPercent = overscrollTop / this.mTotalDragDistance;
        float dragPercent = Math.min(1.0F, Math.abs(originalDragPercent));
        float adjustedPercent = (float) Math.max((double) dragPercent - 0.4D, 0.0D) * 5.0F / 3.0F;
        float extraOS = Math.abs(overscrollTop) - this.mTotalDragDistance;
        float slingshotDist = this.mCustomSlingshotDistance > 0 ? (float) this.mCustomSlingshotDistance : (float) (this.mUsingCustomStart ? this.mSpinnerOffsetEnd - this.mOriginalOffsetTop : this.mSpinnerOffsetEnd);
        float tensionSlingshotPercent = Math.max(0.0F, Math.min(extraOS, slingshotDist * 2.0F) / slingshotDist);
        float tensionPercent = (float) ((double) (tensionSlingshotPercent / 4.0F) - Math.pow((double) (tensionSlingshotPercent / 4.0F), 2.0D)) * 2.0F;
        float extraMove = slingshotDist * tensionPercent * 2.0F;
        int targetY = this.mOriginalOffsetTop + (int) (slingshotDist * dragPercent + extraMove);
        if (this.mCircleView.getVisibility() != VISIBLE) {
            this.mCircleView.setVisibility(VISIBLE);
        }

        if (!this.mScale) {
            this.mCircleView.setScaleX(1.0F);
            this.mCircleView.setScaleY(1.0F);
        }

        if (this.mScale) {
            this.setAnimationProgress(Math.min(1.0F, overscrollTop / this.mTotalDragDistance));
        }

//        if (overscrollTop < this.mTotalDragDistance) {
//            if (this.mProgress.getAlpha() > 76 && !this.isAnimationRunning(this.mAlphaStartAnimation)) {
//                this.startProgressAlphaStartAnimation();
//            }
//        } else if (this.mProgress.getAlpha() < 255 && !this.isAnimationRunning(this.mAlphaMaxAnimation)) {
//            this.startProgressAlphaMaxAnimation();
//        }

        float strokeStart = adjustedPercent * 0.8F;
//        this.mProgress.setStartEndTrim(0.0F, Math.min(0.8F, strokeStart));
//        this.mProgress.setArrowScale(Math.min(1.0F, adjustedPercent));
//        float rotation = (-0.25F + 0.4F * adjustedPercent + tensionPercent * 2.0F) * 0.5F;
//        this.mProgress.setProgressRotation(rotation);
        this.setTargetOffsetTopAndBottom(targetY - this.mCurrentTargetOffsetTop);
    }

    private void finishSpinner(float overscrollTop) {
        if (overscrollTop > this.mTotalDragDistance) {
            this.setRefreshing(true, true);
        } else {
            this.mRefreshing = false;
//            this.mProgress.setStartEndTrim(0.0F, 0.0F);
            Animation.AnimationListener listener = null;
            if (!this.mScale) {
                listener = new Animation.AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                    }

                    public void onAnimationEnd(Animation animation) {
                        if (!CustomSwipeRefreshLayout.this.mScale) {
                            CustomSwipeRefreshLayout.this.startScaleDownAnimation((Animation.AnimationListener) null);
                        }

                    }

                    public void onAnimationRepeat(Animation animation) {
                    }
                };
            }

            this.animateOffsetToStartPosition(this.mCurrentTargetOffsetTop, listener);
//            this.mProgress.setArrowEnabled(false);
        }

    }

    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        if (this.mReturningToStart && action == 0) {
            this.mReturningToStart = false;
        }

        if (this.isEnabled() && !this.mReturningToStart && !this.canChildScrollUp() && !this.mRefreshing && !this.mNestedScrollInProgress) {
            float y;
            float overscrollTop;
            int pointerIndex;
            switch (action) {
                case 0:
                    this.mActivePointerId = ev.getPointerId(0);
                    this.mIsBeingDragged = false;
                    break;
                case 1:
                    pointerIndex = ev.findPointerIndex(this.mActivePointerId);
                    if (pointerIndex < 0) {
                        Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
                        return false;
                    }

                    if (this.mIsBeingDragged) {
                        y = ev.getY(pointerIndex);
                        overscrollTop = (y - this.mInitialMotionY) * 0.5F;
                        this.mIsBeingDragged = false;
                        this.finishSpinner(overscrollTop);
                    }

                    this.mActivePointerId = -1;
                    return false;
                case 2:
                    pointerIndex = ev.findPointerIndex(this.mActivePointerId);
                    if (pointerIndex < 0) {
                        Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                        return false;
                    }

                    y = ev.getY(pointerIndex);
                    this.startDragging(y);
                    if (this.mIsBeingDragged) {
                        overscrollTop = (y - this.mInitialMotionY) * 0.5F;
                        if (overscrollTop <= 0.0F) {
                            return false;
                        }

                        this.moveSpinner(overscrollTop);
                    }
                    break;
                case 3:
                    return false;
                case 4:
                default:
                    break;
                case 5:
                    pointerIndex = ev.getActionIndex();
                    if (pointerIndex < 0) {
                        Log.e(LOG_TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                        return false;
                    }

                    this.mActivePointerId = ev.getPointerId(pointerIndex);
                    break;
                case 6:
                    this.onSecondaryPointerUp(ev);
            }

            return true;
        } else {
            return false;
        }
    }

    private void startDragging(float y) {
        float yDiff = y - this.mInitialDownY;
        if (yDiff > (float) this.mTouchSlop && !this.mIsBeingDragged) {
            this.mInitialMotionY = this.mInitialDownY + (float) this.mTouchSlop;
            this.mIsBeingDragged = true;
//            this.mProgress.setAlpha(76);
        }

    }

    private void animateOffsetToCorrectPosition(int from, Animation.AnimationListener listener) {
        this.mFrom = from;
        this.mAnimateToCorrectPosition.reset();
        this.mAnimateToCorrectPosition.setDuration(200L);
        this.mAnimateToCorrectPosition.setInterpolator(this.mDecelerateInterpolator);
        if (listener != null) {
            this.mCircleView.setAnimationListener(listener);
        }

        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(this.mAnimateToCorrectPosition);
    }

    private void animateOffsetToStartPosition(int from, Animation.AnimationListener listener) {
        if (this.mScale) {
            this.startScaleDownReturnToStartAnimation(from, listener);
        } else {
            this.mFrom = from;
            this.mAnimateToStartPosition.reset();
            this.mAnimateToStartPosition.setDuration(200L);
            this.mAnimateToStartPosition.setInterpolator(this.mDecelerateInterpolator);
            if (listener != null) {
                this.mCircleView.setAnimationListener(listener);
            }

            this.mCircleView.clearAnimation();
            this.mCircleView.startAnimation(this.mAnimateToStartPosition);
        }

    }

    void moveToStart(float interpolatedTime) {
        int targetTop = this.mFrom + (int) ((float) (this.mOriginalOffsetTop - this.mFrom) * interpolatedTime);
        int offset = targetTop - this.mCircleView.getTop();
        this.setTargetOffsetTopAndBottom(offset);
    }

    private void startScaleDownReturnToStartAnimation(int from, Animation.AnimationListener listener) {
        this.mFrom = from;
        this.mStartingScale = this.mCircleView.getScaleX();
        this.mScaleDownToStartAnimation = new Animation() {
            public void applyTransformation(float interpolatedTime, Transformation t) {
                float targetScale = CustomSwipeRefreshLayout.this.mStartingScale + -CustomSwipeRefreshLayout.this.mStartingScale * interpolatedTime;
                CustomSwipeRefreshLayout.this.setAnimationProgress(targetScale);
                CustomSwipeRefreshLayout.this.moveToStart(interpolatedTime);
            }
        };
        this.mScaleDownToStartAnimation.setDuration(150L);
        if (listener != null) {
            this.mCircleView.setAnimationListener(listener);
        }

        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(this.mScaleDownToStartAnimation);
    }

    void setTargetOffsetTopAndBottom(int offset) {
        this.mCircleView.bringToFront();
        ViewCompat.offsetTopAndBottom(this.mCircleView, offset);
        this.mCurrentTargetOffsetTop = this.mCircleView.getTop();
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int pointerIndex = ev.getActionIndex();
        int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == this.mActivePointerId) {
            int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            this.mActivePointerId = ev.getPointerId(newPointerIndex);
        }

    }

    public interface OnChildScrollUpCallback {
        boolean canChildScrollUp(@NonNull CustomSwipeRefreshLayout var1, @Nullable View var2);
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    private class CircleImageView extends androidx.appcompat.widget.AppCompatImageView {
        private static final int KEY_SHADOW_COLOR = 503316480;
        private static final int FILL_SHADOW_COLOR = 1023410176;
        private static final float X_OFFSET = 0.0F;
        private static final float Y_OFFSET = 1.75F;
        private static final float SHADOW_RADIUS = 3.5F;
        private static final int SHADOW_ELEVATION = 4;
        private Animation.AnimationListener mListener;
        int mShadowRadius;

        CircleImageView(Context context, int color) {
            super(context);
            float density = this.getContext().getResources().getDisplayMetrics().density;
            int shadowYOffset = (int) (density * 1.75F);
            int shadowXOffset = (int) (density * 0.0F);
            this.mShadowRadius = (int) (density * 1F);
            ShapeDrawable circle;
            if (this.elevationSupported()) {
                circle = new ShapeDrawable(new RectShape());
                ViewCompat.setElevation(this, 4.0F * density);
            } else {
                RectShape rectShape = new RectShape();
                circle = new ShapeDrawable(rectShape);
                this.setLayerType(1, circle.getPaint());
                circle.getPaint().setShadowLayer((float) this.mShadowRadius, (float) shadowXOffset, (float) shadowYOffset, 503316480);
                int padding = this.mShadowRadius;
                this.setPadding(padding, padding, padding, padding);
            }

            circle.getPaint().setColor(color);
            ViewCompat.setBackground(this, circle);
        }

        private boolean elevationSupported() {
            return Build.VERSION.SDK_INT >= 21;
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (!this.elevationSupported()) {
                this.setMeasuredDimension(this.getMeasuredWidth() + this.mShadowRadius * 2, this.getMeasuredHeight() + this.mShadowRadius * 2);
            }

        }

        public void setAnimationListener(Animation.AnimationListener listener) {
            this.mListener = listener;
        }

        public void onAnimationStart() {
            super.onAnimationStart();
            if (this.mListener != null) {
                this.mListener.onAnimationStart(this.getAnimation());
            }

        }

        public void onAnimationEnd() {
            super.onAnimationEnd();
            if (this.mListener != null) {
                this.mListener.onAnimationEnd(this.getAnimation());
            }

        }

        public void setBackgroundColorRes(int colorRes) {
            this.setBackgroundColor(ContextCompat.getColor(this.getContext(), colorRes));
        }

        public void setBackgroundColor(int color) {
            if (this.getBackground() instanceof ShapeDrawable) {
                ((ShapeDrawable) this.getBackground()).getPaint().setColor(color);
            }

        }

        private class OvalShadow extends OvalShape {
            private RadialGradient mRadialGradient;
            private Paint mShadowPaint = new Paint();

            OvalShadow(int shadowRadius) {
                CircleImageView.this.mShadowRadius = shadowRadius;
                this.updateRadialGradient((int) this.rect().width());
            }

            protected void onResize(float width, float height) {
                super.onResize(width, height);
                this.updateRadialGradient((int) width);
            }

            public void draw(Canvas canvas, Paint paint) {
                int viewWidth = CircleImageView.this.getWidth();
                int viewHeight = CircleImageView.this.getHeight();
                canvas.drawCircle((float) (viewWidth / 2), (float) (viewHeight / 2), (float) (viewWidth / 2), this.mShadowPaint);
                canvas.drawCircle((float) (viewWidth / 2), (float) (viewHeight / 2), (float) (viewWidth / 2 - CircleImageView.this.mShadowRadius), paint);
            }

            private void updateRadialGradient(int diameter) {
                this.mRadialGradient = new RadialGradient((float) (diameter / 2), (float) (diameter / 2), (float) CircleImageView.this.mShadowRadius, new int[]{1023410176, 0}, (float[]) null, Shader.TileMode.CLAMP);
                this.mShadowPaint.setShader(this.mRadialGradient);
            }
        }
    }
}

