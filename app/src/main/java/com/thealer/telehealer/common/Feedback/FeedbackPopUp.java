//package com.thealer.telehealer.common.Feedback;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.view.View;
//import android.view.Window;
//import android.widget.RadioGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.widget.AppCompatTextView;
//
//import com.thealer.telehealer.R;
//import com.thealer.telehealer.common.Feedback.AbstractDialog;
//
//public class FeedbackPopUp extends AbstractDialog {
//
//    private View.OnClickListener btYesListener = null;
//    public static RadioGroup radioGroup;
//    Context context;
//    private AppCompatTextView txtSubmit;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.feedback_popup);
//        initView();
//    }
//
//    private void initView() {
//        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
//        txtSubmit = (AppCompatTextView) findViewById(R.id.txtSubmit);
//        txtSubmit.setOnClickListener(btYesListener);
//    }
//
//    public void setPositveButton(String yes, View.OnClickListener onClickListener) {
//        dismiss();
//        this.btYesListener = onClickListener;
//    }
//
//}
