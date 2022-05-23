package com.thealer.telehealer.common.Feedback;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.appcompat.widget.AppCompatTextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.OpenTok.CallRequest;
import com.thealer.telehealer.apilayer.models.feedback.FeedbackResponseModel;
import com.thealer.telehealer.apilayer.models.feedback.question.FeedbackQuestionModel;
import com.thealer.telehealer.common.CommonObject;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;
import com.thealer.telehealer.common.UserType;

import java.util.ArrayList;
import java.util.List;

public class FeedbackAdapter extends BaseAdapter {

    Activity context;
    LayoutInflater inflter;
    List<FeedbackQuestionModel.Datum> questionsList = new ArrayList<>();
    CallRequest callrequest;

    public FeedbackAdapter(Activity applicationContext, List<FeedbackQuestionModel.Datum> questionsList, CallRequest callrequest) {
        this.context = context;
        this.questionsList = questionsList;
        inflter = (LayoutInflater.from(applicationContext));
        this.callrequest = callrequest;
    }

    @Override
    public int getCount() {
        return questionsList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.question_items, null);
        AppCompatTextView question = (AppCompatTextView) view.findViewById(R.id.question);
        RadioButton yes = (RadioButton) view.findViewById(R.id.yes);
        RadioButton no = (RadioButton) view.findViewById(R.id.no);
        LinearLayout llquestion = (LinearLayout) view.findViewById(R.id.ll_question);
        LinearLayout mainview = (LinearLayout) view.findViewById(R.id.ll_mainview);

        if (questionsList.get(i).getuserSelect() == null || questionsList.get(i).getuserSelect().isEmpty()) {
            yes.setChecked(false);
            no.setChecked(false);
        }

        yes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    questionsList.get(i).setuserSelect(yes.getText().toString());
                    boolean insertdata = false;
                    int insertposition = -1;
                    for (int pos = 0; pos < CommonObject.tempdata.size(); pos++) {
                        if (CommonObject.tempdata.get(pos).id == i) {
                            insertdata = true;
                            insertposition = pos;
                            break;
                        }
                    }

                    if (insertdata) {
                        CommonObject.tempdata.set(insertposition, new FeedbackTemp(i, yes.getText().toString()));
                    }

                    CommonObject.addFeedbackResponse(questionsList.get(i).getFeedbacksQuestionsId(), questionsList.get(i).getQuestion(), yes.getText().toString());
                }
            }
        });

        no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    questionsList.get(i).setuserSelect(yes.getText().toString());
                    boolean insertdata = false;
                    int insertposition = -1;
                    for (int pos = 0; pos < CommonObject.tempdata.size(); pos++) {
                        if (CommonObject.tempdata.get(pos).id == i) {
                            insertdata = true;
                            insertposition = pos;
                            break;
                        }
                    }

                    if (insertdata) {
                        CommonObject.tempdata.set(insertposition, new FeedbackTemp(i, no.getText().toString()));
                    }
                    CommonObject.addFeedbackResponse(questionsList.get(i).getFeedbacksQuestionsId(), questionsList.get(i).getQuestion(), yes.getText().toString());
                }
            }
        });

        if (questionsList.get(i).getIsPhysiciansQuestion() != null) {

            if (!questionsList.get(i).getIsPhysiciansQuestion()) {
                if (UserType.isUserPatient()) {
                    if (callrequest.getCallType().equals(OpenTokConstants.video) || callrequest.getCallType().equals(OpenTokConstants.oneWay)) {
                        llquestion.setVisibility(View.VISIBLE);
                        checkValue(i,"");
                        question.setText("" + questionsList.get(i).getQuestion());
                        yes.setText("" + questionsList.get(i).getOptions().get(0));
                        no.setText("" + questionsList.get(i).getOptions().get(1));
                    } else if (questionsList.get(i).getQuestion().contains(callrequest.getCallType())) {
                        llquestion.setVisibility(View.VISIBLE);
                        checkValue(i,"");
                        question.setText("" + questionsList.get(i).getQuestion());
                        yes.setText("" + questionsList.get(i).getOptions().get(0));
                        no.setText("" + questionsList.get(i).getOptions().get(1));
                    } else {
                        llquestion.setVisibility(View.GONE);
                        mainview.removeView(llquestion);
                    }
                } else {
                    if (callrequest.getCallType().equals(OpenTokConstants.video) || callrequest.getCallType().equals(OpenTokConstants.oneWay)) {
                        llquestion.setVisibility(View.VISIBLE);
                        checkValue(i,"");
                        question.setText("" + questionsList.get(i).getQuestion());
                        yes.setText("" + questionsList.get(i).getOptions().get(0));
                        no.setText("" + questionsList.get(i).getOptions().get(1));
                    } else if (questionsList.get(i).getQuestion().contains(callrequest.getCallType())) {
                        llquestion.setVisibility(View.VISIBLE);
                        checkValue(i,"");
                        question.setText("" + questionsList.get(i).getQuestion());
                        yes.setText("" + questionsList.get(i).getOptions().get(0));
                        no.setText("" + questionsList.get(i).getOptions().get(1));
                    } else {
                        llquestion.setVisibility(View.GONE);
                        mainview.removeView(llquestion);
                    }
                }
            } else {
                llquestion.setVisibility(View.GONE);
                mainview.removeView(llquestion);
            }
        } else {

            if (callrequest.getCallType().equals(OpenTokConstants.video) || callrequest.getCallType().equals(OpenTokConstants.oneWay)) {
                llquestion.setVisibility(View.VISIBLE);
                checkValue(i,"");
                question.setText("" + questionsList.get(i).getQuestion());
                yes.setText("" + questionsList.get(i).getOptions().get(0));
                no.setText("" + questionsList.get(i).getOptions().get(1));
            } else if (questionsList.get(i).getQuestion().contains(callrequest.getCallType())) {
                llquestion.setVisibility(View.VISIBLE);
                checkValue(i,"");
                question.setText("" + questionsList.get(i).getQuestion());
                yes.setText("" + questionsList.get(i).getOptions().get(0));
                no.setText("" + questionsList.get(i).getOptions().get(1));
            } else {
                llquestion.setVisibility(View.GONE);
                mainview.removeView(llquestion);
            }
        }
        return view;
    }

    public void checkValue(int i,String value){
        boolean insertdata = false;
        int insertposition = -1;
        for (int pos = 0; pos < CommonObject.tempdata.size(); pos++) {
            if (CommonObject.tempdata.get(pos).id == i) {
                insertdata = true;
                insertposition = pos;
                break;
            }
        }

        if (insertdata) {
            CommonObject.tempdata.set(insertposition, new FeedbackTemp(i, value));
        } else {
            CommonObject.tempdata.add(new FeedbackTemp(i, ""));
        }
    }

}
