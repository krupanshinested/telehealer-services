package com.thealer.telehealer.views.home.orders.forms;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.orders.forms.DynamicFormDataBean;
import com.thealer.telehealer.apilayer.models.orders.forms.OrdersUserFormsApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.views.base.BaseBottomSheetDialogFragment;

/**
 * Created by Aswin on 10,June,2019
 */
public class AboutScoreBottomSheet extends BaseBottomSheetDialogFragment {
    private TextView titleTv;
    private LinearLayout container;
    private ImageView closeIv;

    private OrdersUserFormsApiResponseModel formsApiResponseModel;
    private TextView column1Tv;
    private TextView column2Tv;
    private ConstraintLayout parentView;
    private View view;
    private TextView scoreTitleTv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.cloneInContext(contextThemeWrapper).inflate(R.layout.bottomsheet_score, container, false);
        setBottomSheetHeight(view, 75);
        initView(view);
        return view;
    }

    private void initView(View view) {
        titleTv = (TextView) view.findViewById(R.id.title_tv);
        container = (LinearLayout) view.findViewById(R.id.container);
        closeIv = (ImageView) view.findViewById(R.id.close_iv);
        column1Tv = (TextView) view.findViewById(R.id.column1_tv);
        column2Tv = (TextView) view.findViewById(R.id.column2_tv);
        scoreTitleTv = (TextView) view.findViewById(R.id.score_title_tv);

        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if (getArguments() != null) {
            formsApiResponseModel = (OrdersUserFormsApiResponseModel) getArguments().getSerializable(ArgumentKeys.FORM_DETAIL);

            if (formsApiResponseModel != null) {
                titleTv.setText(formsApiResponseModel.getName());

                DynamicFormDataBean.ScoreDetailsBean scoreDetailsBean = formsApiResponseModel.getData().getScore_details();
                column1Tv.setText(scoreDetailsBean.getCol2());
                column2Tv.setText(scoreDetailsBean.getCol1());

                if (scoreDetailsBean.getTitle() != null && !scoreDetailsBean.getTitle().isEmpty()) {
                    scoreTitleTv.setText(scoreDetailsBean.getTitle());
                    scoreTitleTv.setVisibility(View.VISIBLE);
                }

                for (int i = 0; i < scoreDetailsBean.getProperties().getValues().size(); i++) {

                    View scoreItemView = getLayoutInflater().inflate(R.layout.form_score_item, null);

                    TextView labelTv = (TextView) scoreItemView.findViewById(R.id.label_tv);
                    TextView valueTv = (TextView) scoreItemView.findViewById(R.id.value_tv);
                    View bottomView = (View) scoreItemView.findViewById(R.id.bottom_view);

                    if (i == scoreDetailsBean.getProperties().getValues().size() - 1)
                        bottomView.setVisibility(View.VISIBLE);

                    labelTv.setText(scoreDetailsBean.getProperties().getValues().get(i).getValue());
                    valueTv.setText(scoreDetailsBean.getProperties().getValues().get(i).getScore());

                    container.addView(scoreItemView);
                }
            }
        }
    }
}
