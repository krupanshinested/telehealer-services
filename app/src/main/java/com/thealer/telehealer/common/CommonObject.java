package com.thealer.telehealer.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.TypeKey;
import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.OpenTok.CallRequest;
import com.thealer.telehealer.apilayer.models.feedback.FeedbackResponseModel;
import com.thealer.telehealer.apilayer.models.feedback.question.FeedbackQuestionModel;
import com.thealer.telehealer.common.Feedback.FeedbackCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class CommonObject {

    private static AppCompatTextView questionsone, questionstwo, questionsthree, questionsfour, questionsfive, questionssix, questionseven, dialogButton;
    private static LinearLayout questionption, llquestiontwo, llquestionthree, llquestionfour, llquestionfive, llquestionsix, llquestionseven;
    private static RadioGroup rbquestionone, rbquestiontwo, rbquestionthree, rbquestionfour, rbquestionfive, rbquestionsix, rbquestionseven;
    private static RadioButton rbtnyes, rbtngood, rbtngoodq3, rbtngoodq4, rbtngoodq5, rbtngoodq6, rbtngoodq7;
    private static RadioButton rbtnno, rbtnbad, rbtnbadq3, rbtnbadq4, rbtnbadq5, rbtnbadq6, rbtnbadq7;
    private static FeedbackQuestionModel questiondata;
    private static CallRequest callrequest;
    private static List<FeedbackResponseModel> responseModels = new ArrayList<>();
    private static boolean insertdata;
    private static int insertposition;

    public static void showDialog(Activity activity, FeedbackQuestionModel questionModel, CallRequest callRequest, String sessionId, String to_guid, String doctorGuid, FeedbackCallback feedbackCallback) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.feedback_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        questiondata = questionModel;
        callrequest = callRequest;

        questionsone = (AppCompatTextView) dialog.findViewById(R.id.tv_questionone);
        rbquestionone = (RadioGroup) dialog.findViewById(R.id.rb_questionone);
        rbtnyes = (RadioButton) dialog.findViewById(R.id.yes);
        rbtnno = (RadioButton) dialog.findViewById(R.id.no);

        questionption = (LinearLayout) dialog.findViewById(R.id.ll_questionption);

        llquestiontwo = (LinearLayout) dialog.findViewById(R.id.ll_questiontwo);
        questionstwo = (AppCompatTextView) dialog.findViewById(R.id.tv_questiontwo);
        rbquestiontwo = (RadioGroup) dialog.findViewById(R.id.rb_questiontwo);
        rbtngood = (RadioButton) dialog.findViewById(R.id.good);
        rbtnbad = (RadioButton) dialog.findViewById(R.id.bad);

        llquestionthree = (LinearLayout) dialog.findViewById(R.id.ll_questionthree);
        questionsthree = (AppCompatTextView) dialog.findViewById(R.id.tv_questionthree);
        rbquestionthree = (RadioGroup) dialog.findViewById(R.id.rb_questionthree);
        rbtngoodq3 = (RadioButton) dialog.findViewById(R.id.goodq3);
        rbtnbadq3 = (RadioButton) dialog.findViewById(R.id.badq3);

        llquestionfour = (LinearLayout) dialog.findViewById(R.id.ll_questionfour);
        questionsfour = (AppCompatTextView) dialog.findViewById(R.id.tv_questionfour);
        rbquestionfour = (RadioGroup) dialog.findViewById(R.id.rb_questionfour);
        rbtngoodq4 = (RadioButton) dialog.findViewById(R.id.goodq4);
        rbtnbadq4 = (RadioButton) dialog.findViewById(R.id.badq4);

        llquestionfive = (LinearLayout) dialog.findViewById(R.id.ll_questionfive);
        questionsfive = (AppCompatTextView) dialog.findViewById(R.id.tv_questionfive);
        rbquestionfive = (RadioGroup) dialog.findViewById(R.id.rb_questionfive);
        rbtngoodq5 = (RadioButton) dialog.findViewById(R.id.goodq5);
        rbtnbadq5 = (RadioButton) dialog.findViewById(R.id.badq5);

        llquestionsix = (LinearLayout) dialog.findViewById(R.id.ll_questionsix);
        questionssix = (AppCompatTextView) dialog.findViewById(R.id.tv_questionsix);
        rbquestionsix = (RadioGroup) dialog.findViewById(R.id.rb_questionsix);
        rbtngoodq6 = (RadioButton) dialog.findViewById(R.id.goodq6);
        rbtnbadq6 = (RadioButton) dialog.findViewById(R.id.badq6);

        llquestionseven = (LinearLayout) dialog.findViewById(R.id.ll_questionseven);
        questionseven = (AppCompatTextView) dialog.findViewById(R.id.tv_questionseven);
        rbquestionseven = (RadioGroup) dialog.findViewById(R.id.rb_questionseven);
        rbtngoodq7 = (RadioButton) dialog.findViewById(R.id.goodq7);
        rbtnbadq7 = (RadioButton) dialog.findViewById(R.id.badq7);

        dialogButton = (AppCompatTextView) dialog.findViewById(R.id.txtSubmit);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String, Object> param = new HashMap<>();
                String version = BuildConfig.VERSION_NAME;
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                String date = df.format(Calendar.getInstance().getTime());
                param.put("dateTime", date);
                param.put("feedback_type", "call");
                param.put("device", "android");
                param.put("app_version", version);
                if (responseModels.size() != 0) {
                    List<FeedbackResponseModel> temp = new ArrayList<>();
                    temp.addAll(responseModels);
                    if (!responseModels.get(0).getAnswer().trim().equals(questiondata.getData().get(0).getMainQuestionAnswer())) {
                        responseModels.clear();
                        responseModels.add(temp.get(0));
                        temp.clear();
                    }
                }

                ArrayList<FeedbackResponseModel> responsedata = new ArrayList<>();
                try {
                    for (int i = 0; i < responseModels.size(); i++) {
                        responsedata.add(new FeedbackResponseModel(responseModels.get(i).getFeedbacksQuestionsId(), responseModels.get(i).getQuestion(), responseModels.get(i).getAnswer()));
                    }
                } catch (Exception ex) {
                    Log.d("TAG", "onClick: ");
                }
                param.put("feedback_respone", responsedata);
                param.put("rating", 5);
                param.put("session_id", sessionId);
                if (UserDetailPreferenceManager.getRole().equals(Constants.ROLE_PATIENT)) {
                    param.put("taget_user_id", doctorGuid);
                    param.put("user_id", to_guid);
                } else {
                    param.put("taget_user_id", to_guid);
                    param.put("user_id", doctorGuid);
                }
                if (responseModels.size() != 0) {
                    feedbackCallback.onActionSuccess(param);
                    dialog.dismiss();
                } else {
                    Toast.makeText(activity, "Select atleast one option.", Toast.LENGTH_SHORT).show();
                }


            }
        });

        dialog.show();

        setQuestion(questionModel);
        setcheckListner();

    }

    private static void addFeedbackResponse(Integer questionid, String question, String value) {
        insertdata = false;
        if (responseModels.size() == 0) {
            responseModels.add(new FeedbackResponseModel(questionid, question, value));
        } else {
            for (int i = 0; i < responseModels.size(); i++) {
                if (responseModels.get(i).getFeedbacksQuestionsId().equals(questionid)) {
                    insertposition = i;
                    insertdata = true;
                    break;
                }
            }

            if (!insertdata) {
                responseModels.add(new FeedbackResponseModel(questionid, question, value));
            } else {
                responseModels.set(insertposition, new FeedbackResponseModel(questionid, question, value));
            }
        }
    }

    private static void setcheckListner() {
        rbquestionone.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton) radioGroup.findViewById(i);

                if (rb.getText().toString().contains(questiondata.getData().get(0).getMainQuestionAnswer())) {
                    questionption.setVisibility(View.VISIBLE);
                    addFeedbackResponse(questiondata.getData().get(0).getFeedbacksQuestionsId(), questiondata.getData().get(0).getQuestion(), rb.getText().toString());
                } else {
                    questionption.setVisibility(View.GONE);
                    addFeedbackResponse(questiondata.getData().get(0).getFeedbacksQuestionsId(), questiondata.getData().get(0).getQuestion(), rb.getText().toString());
                }
            }
        });

        rbquestiontwo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton) radioGroup.findViewById(i);
                addFeedbackResponse(questiondata.getData().get(1).getFeedbacksQuestionsId(), questiondata.getData().get(1).getQuestion(), rb.getText().toString());
            }
        });

        rbquestionthree.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton) radioGroup.findViewById(i);
                addFeedbackResponse(questiondata.getData().get(2).getFeedbacksQuestionsId(), questiondata.getData().get(2).getQuestion(), rb.getText().toString());
            }
        });

        rbquestionfour.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton) radioGroup.findViewById(i);
                addFeedbackResponse(questiondata.getData().get(3).getFeedbacksQuestionsId(), questiondata.getData().get(3).getQuestion(), rb.getText().toString());
            }
        });

        rbquestionfive.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton) radioGroup.findViewById(i);
                addFeedbackResponse(questiondata.getData().get(4).getFeedbacksQuestionsId(), questiondata.getData().get(4).getQuestion(), rb.getText().toString());
            }
        });

        rbquestionsix.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton) radioGroup.findViewById(i);
                addFeedbackResponse(questiondata.getData().get(5).getFeedbacksQuestionsId(), questiondata.getData().get(5).getQuestion(), rb.getText().toString());
            }
        });

        rbquestionseven.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton) radioGroup.findViewById(i);
                addFeedbackResponse(questiondata.getData().get(6).getFeedbacksQuestionsId(), questiondata.getData().get(6).getQuestion(), rb.getText().toString());
            }
        });
    }

    private static void unCheckButton() {
        if (rbtngood.isChecked()) {
            rbtngood.setChecked(false);
            rbtngood.clearFocus();
        }
        if (rbtnbad.isChecked()) {
            rbtnbad.setChecked(false);
            rbtnbad.clearFocus();
        }
        if (rbtngoodq3.isChecked()) {
            rbtngoodq3.setChecked(false);
            rbtngoodq3.clearFocus();
        }
        if (rbtnbadq3.isChecked()) {
            rbtnbadq3.setChecked(false);
            rbtnbadq3.clearFocus();
        }
        if (rbtngoodq4.isChecked()) {
            rbtngoodq4.setChecked(false);
            rbquestionfour.clearChildFocus(rbtngoodq4);
        }
        if (rbtnbadq4.isChecked()) {
            rbtnbadq4.setChecked(false);
            rbquestionfour.clearChildFocus(rbtnbadq4);
        }
        if (rbtngoodq5.isChecked()) {
            rbtngoodq5.setChecked(false);
            rbquestionfive.clearChildFocus(rbtngoodq5);
        }
        if (rbtnbadq5.isChecked()) {
            rbtnbadq5.setChecked(false);
            rbquestionfive.clearChildFocus(rbtnbadq5);
        }
        if (rbtngoodq6.isChecked()) {
            rbtngoodq6.setChecked(false);
            rbquestionsix.clearChildFocus(rbtngoodq6);
        }
        if (rbtnbadq6.isChecked()) {
            rbtnbadq6.setChecked(false);
            rbquestionsix.clearChildFocus(rbtnbadq6);
        }
        if (rbtngoodq7.isChecked()) {
            rbtngoodq7.setChecked(false);
            rbquestionseven.clearChildFocus(rbtngoodq7);
        }
        if (rbtnbadq7.isChecked()) {
            rbtnbadq7.setChecked(false);
            rbquestionseven.clearChildFocus(rbtnbadq7);
        }
    }

    private static void setQuestion(FeedbackQuestionModel questionModel) {
        Collections.sort(questionModel.getData(), new Comparator<FeedbackQuestionModel.Datum>() {
            @Override
            public int compare(FeedbackQuestionModel.Datum datum, FeedbackQuestionModel.Datum t1) {
                return datum.getFeedbacksQuestionsId().compareTo(t1.getFeedbacksQuestionsId());
            }
        });
        for (int i = 0; i < questionModel.getData().size(); i++) {
            setQuestionAnswer(i);
        }
    }

    private static void setQuestionAnswer(int position) {
        switch (position) {
            case 0:
                questionsone.setText("" + questiondata.getData().get(position).getQuestion());
                rbtnyes.setText("" + questiondata.getData().get(position).getOptions().get(0));
                rbtnno.setText("" + questiondata.getData().get(position).getOptions().get(1));
                break;
            case 1:

                if (questiondata.getData().get(1).getQuestion().contains(callrequest.getCallType())) {
                    llquestiontwo.setVisibility(View.VISIBLE);
                    questionstwo.setText("" + questiondata.getData().get(1).getQuestion());
                    rbtngood.setText("" + questiondata.getData().get(1).getOptions().get(0));
                    rbtnbad.setText("" + questiondata.getData().get(1).getOptions().get(1));
                } else {
                    llquestiontwo.setVisibility(View.GONE);
                }
                break;
            case 2:
                if (questiondata.getData().get(2).getQuestion().contains(callrequest.getCallType())) {
                    llquestionthree.setVisibility(View.VISIBLE);
                    questionsthree.setText("" + questiondata.getData().get(2).getQuestion());
                    rbtngoodq3.setText("" + questiondata.getData().get(2).getOptions().get(0));
                    rbtnbadq3.setText("" + questiondata.getData().get(2).getOptions().get(1));
                } else {
                    llquestionthree.setVisibility(View.GONE);
                }
                break;
            case 3:

                if (questiondata.getData().get(3).getIsPhysiciansQuestion() != null) {

                    if (questiondata.getData().get(3).getIsPhysiciansQuestion()) {
                        if (questiondata.getData().get(3).getQuestion().contains(callrequest.getCallType())) {
                            llquestionfour.setVisibility(View.VISIBLE);
                            questionsfour.setText("" + questiondata.getData().get(3).getQuestion());
                            rbtngoodq4.setText("" + questiondata.getData().get(3).getOptions().get(0));
                            rbtnbadq4.setText("" + questiondata.getData().get(3).getOptions().get(1));
                        } else {
                            llquestionfour.setVisibility(View.GONE);
                        }
                    } else {
                        llquestionfour.setVisibility(View.GONE);
                    }
                }
                break;
            case 4:
                if (questiondata.getData().get(4).getIsPhysiciansQuestion() != null) {

                    if (questiondata.getData().get(4).getIsPhysiciansQuestion()) {
                        if (questiondata.getData().get(4).getQuestion().contains(callrequest.getCallType())) {
                            llquestionfive.setVisibility(View.VISIBLE);
                            questionsfive.setText("" + questiondata.getData().get(4).getQuestion());
                            rbtngoodq5.setText("" + questiondata.getData().get(4).getOptions().get(0));
                            rbtnbadq5.setText("" + questiondata.getData().get(4).getOptions().get(1));
                        } else {
                            llquestionfive.setVisibility(View.GONE);
                        }
                    } else {
                        llquestionfive.setVisibility(View.GONE);
                    }
                }
                break;
            case 5:
                if (questiondata.getData().get(5).getIsPhysiciansQuestion() != null) {

                    if (questiondata.getData().get(5).getIsPhysiciansQuestion()) {
                        if (questiondata.getData().get(5).getQuestion().contains(callrequest.getCallType())) {
                            llquestionsix.setVisibility(View.VISIBLE);
                            questionssix.setText("" + questiondata.getData().get(5).getQuestion());
                            rbtngoodq6.setText("" + questiondata.getData().get(5).getOptions().get(0));
                            rbtnbadq6.setText("" + questiondata.getData().get(5).getOptions().get(1));
                        } else {
                            llquestionsix.setVisibility(View.GONE);
                        }
                    } else {
                        llquestionsix.setVisibility(View.GONE);
                    }
                }
                break;
            case 6:
                if (questiondata.getData().get(6).getIsPhysiciansQuestion() != null) {

                    if (questiondata.getData().get(6).getIsPhysiciansQuestion()) {
                        if (questiondata.getData().get(6).getQuestion().contains(callrequest.getCallType())) {
                            llquestionseven.setVisibility(View.VISIBLE);
                            questionseven.setText("" + questiondata.getData().get(6).getQuestion());
                            rbtngoodq7.setText("" + questiondata.getData().get(6).getOptions().get(0));
                            rbtnbadq7.setText("" + questiondata.getData().get(6).getOptions().get(1));
                        } else {
                            llquestionseven.setVisibility(View.GONE);
                        }
                    } else {
                        llquestionseven.setVisibility(View.GONE);
                    }
                }
                break;
            default:
                break;
        }
    }

}
