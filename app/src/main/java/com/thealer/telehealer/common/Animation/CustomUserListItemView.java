package com.thealer.telehealer.common.Animation;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.cardview.widget.CardView;

import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.whoami.PaymentInfo;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.stripe.AppPaymentCardUtils;
import com.thealer.telehealer.views.transaction.AddChargeActivity;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.thealer.telehealer.common.Constants.ACTIVATION_PENDING;
import static com.thealer.telehealer.common.Constants.AVAILABLE;
import static com.thealer.telehealer.common.Constants.BUSY;
import static com.thealer.telehealer.common.Constants.NO_DATA;
import static com.thealer.telehealer.common.Constants.OFFLINE;


/**
 * Created by Aswin on 27,March,2019
 */
public class CustomUserListItemView extends ConstraintLayout {
    private CircleImageView avatarCiv;
    private CircleImageView statusCiv;

    private ConstraintLayout listItemCl;
    private CardView listItemCv;
    private TextView listTitleTv;
    private TextView listSubTitleTv;
    private LinearLayout actionLl;
    private ImageView actionIv, hasCardIV;
    private CheckBox checkbox;

    private Drawable avatarDrawable, actionDrawable;
    private int gravity, actionWidth = 25, actionHeight = 25;
    private boolean showAction, showCheckbox, showStatus, showBottomView;
    private View bottomView;
    private ConstraintLayout abnormalIndicatorCl;
    private CustomButton addChargeBtn;

    public CustomUserListItemView(Context context) {
        super(context);
        initView(null);
    }

    public CustomUserListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_custom_circle_image_view, this, true);
        avatarCiv = (CircleImageView) view.findViewById(R.id.avatar_civ);
        statusCiv = (CircleImageView) view.findViewById(R.id.status_civ);
        listItemCl = (ConstraintLayout) view.findViewById(R.id.list_item_cl);
        listItemCv = (CardView) view.findViewById(R.id.list_item_cv);
        listTitleTv = (TextView) view.findViewById(R.id.list_title_tv);
        listSubTitleTv = (TextView) view.findViewById(R.id.list_sub_title_tv);
        actionLl = (LinearLayout) view.findViewById(R.id.action_ll);
        actionIv = (ImageView) view.findViewById(R.id.action_iv);
        checkbox = (CheckBox) view.findViewById(R.id.checkbox);
        bottomView = (View) view.findViewById(R.id.bottom_view);
        abnormalIndicatorCl = (ConstraintLayout) view.findViewById(R.id.abnormal_indicator_cl);
        hasCardIV = (ImageView) view.findViewById(R.id.card_iv);
        addChargeBtn = (CustomButton) view.findViewById(R.id.accept_btn);


        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomUserListItemView);

            avatarDrawable = typedArray.getDrawable(R.styleable.CustomUserListItemView_image);
            showAction = typedArray.getBoolean(R.styleable.CustomUserListItemView_show_action, false);
            if (showAction) {
                gravity = typedArray.getInteger(R.styleable.CustomUserListItemView_action_gravity, 1);
                actionDrawable = typedArray.getDrawable(R.styleable.CustomUserListItemView_action_src);
                setActionImage(actionDrawable);
                setActionGravity(gravity);
            }
            actionWidth = typedArray.getDimensionPixelSize(R.styleable.CustomUserListItemView_action_width, 12);
            actionHeight = typedArray.getDimensionPixelSize(R.styleable.CustomUserListItemView_action_height, 12);
            showCheckbox = typedArray.getBoolean(R.styleable.CustomUserListItemView_show_checkbox, false);
            showStatus = typedArray.getBoolean(R.styleable.CustomUserListItemView_show_status, true);
            showBottomView = typedArray.getBoolean(R.styleable.CustomUserListItemView_show_bottom_view, false);

            setActionWidthHeight(actionWidth, actionHeight);
            showActionIv(showAction);
            showCheckBox(showCheckbox);
            showStatus(showStatus);
            showBottomView(showBottomView);
            setUserAvatar(avatarDrawable);

            typedArray.recycle();
        }
    }

    private void setActionWidthHeight(int actionWidth, int actionHeight) {

        ViewGroup.LayoutParams layoutParams = actionIv.getLayoutParams();
        layoutParams.width = (int) (actionWidth * getContext().getResources().getDisplayMetrics().scaledDensity);
        layoutParams.height = (int) (actionHeight * getContext().getResources().getDisplayMetrics().scaledDensity);

    }

    private void setActionGravity(int gravity) {
        int g = Gravity.BOTTOM;
        switch (gravity) {
            case 1:
                g = Gravity.TOP;
                break;
            case 2:
                g = Gravity.CENTER;
                break;
            case 3:
                g = Gravity.BOTTOM;
                break;
        }

        actionLl.setGravity(g | Gravity.CENTER);
    }

    private void setActionImage(Drawable actionDrawable) {
        if (actionDrawable != null) {
            actionIv.setImageDrawable(actionDrawable);
        }
    }

    private void setUserAvatar(Drawable avatarDrawable) {
        if (avatarDrawable != null)
            avatarCiv.setImageDrawable(avatarDrawable);
    }

    public CircleImageView getAvatarCiv() {
        return avatarCiv;
    }

    public TextView getListTitleTv() {
        return listTitleTv;
    }

    public TextView getListSubTitleTv() {
        return listSubTitleTv;
    }

    public void setListTitleTv(String title) {
        listTitleTv.setText(title);
    }

    public void setListSubTitleTv(String subTitle) {
        listSubTitleTv.setText(subTitle);
    }

    public ImageView getActionIv() {
        return actionIv;
    }

    public CheckBox getCheckbox() {
        return checkbox;
    }

    public ConstraintLayout getListItemCl() {
        return listItemCl;
    }

    public CardView getListItemCv() {
        return listItemCv;
    }

    public void showActionIv(boolean show) {
        if (show) {
            actionIv.setVisibility(VISIBLE);
        } else {
            actionIv.setVisibility(GONE);
        }
    }

    public void showCheckBox(boolean show) {
        if (show) {
            checkbox.setVisibility(VISIBLE);
        } else {
            checkbox.setVisibility(GONE);
        }
    }

    public void showStatus(boolean show) {
        if (show) {
            statusCiv.setVisibility(VISIBLE);
        } else {
            statusCiv.setVisibility(GONE);
        }
    }

    public void showBottomView(boolean show) {
        if (show) {
            bottomView.setVisibility(VISIBLE);
        } else {
            bottomView.setVisibility(GONE);
        }
    }

    public void hasAbnormalVital(boolean isAbnormal) {
        if (isAbnormal) {
            abnormalIndicatorCl.setVisibility(VISIBLE);
        } else {
            abnormalIndicatorCl.setVisibility(GONE);
        }
    }

    public void setStatus(String status, String last_active) {
        int color = R.color.status_offline;
        switch (status) {
            case AVAILABLE:
                color = R.color.status_available;
                if (Utils.isOneHourBefore(last_active))
                    color = R.color.status_away;
                break;
            case OFFLINE:
                color = R.color.status_offline;
                break;
            case BUSY:
                color = R.color.status_busy;
                break;
            case ACTIVATION_PENDING:
            case NO_DATA:
                color = R.color.colorBlack;
                break;
        }

        statusCiv.setImageDrawable(new ColorDrawable(getContext().getColor(color)));

        if (!status.equals(AVAILABLE)) {
            Utils.greyoutProfile(avatarCiv);
        } else {
            Utils.removeGreyoutProfile(avatarCiv);
        }
    }

    public void showCardStatus(PaymentInfo paymentInfo) {
        AppPaymentCardUtils.setCardStatusImage(hasCardIV, paymentInfo);
        addChargeBtn.setVisibility(VISIBLE);
    }

    public CustomButton getAddChargeBtn() {
        return addChargeBtn;
    }
}
