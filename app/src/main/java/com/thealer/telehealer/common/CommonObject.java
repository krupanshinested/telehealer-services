package com.thealer.telehealer.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.TypeKey;
import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.models.OpenTok.CallRequest;
import com.thealer.telehealer.apilayer.models.feedback.FeedbackResponseModel;
import com.thealer.telehealer.apilayer.models.feedback.question.FeedbackQuestionModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.common.Feedback.FeedbackAdapter;
import com.thealer.telehealer.common.Feedback.FeedbackCallback;
import com.thealer.telehealer.common.Feedback.FeedbackTemp;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;

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
    private static EditText commentbox;
    private static String firstQ = "", secondQ = "", thirdQ = "", forthQ = "", fivthQ = "", sixthQ = "", seventhQ = "";
    static Dialog dialog;
    private static ListView lvquestion;
    private static FeedbackAdapter feedbackAdapter;
    public static List<FeedbackTemp> tempdata = new ArrayList<>();

    public static void dismissdialog(Activity activity) {
        if (!activity.isFinishing() && dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    public static void showDialog(Activity activity, FeedbackQuestionModel questionModel, CallRequest callRequest, String sessionId, String to_guid, String doctorGuid, FeedbackCallback feedbackCallback) {
        dialog = new Dialog(activity);
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

        lvquestion = (ListView) dialog.findViewById(R.id.lv_question);

        commentbox = (EditText) dialog.findViewById(R.id.commentbox);

        dialogButton = (AppCompatTextView) dialog.findViewById(R.id.txtSubmit);
        dialogButton.setOnClickListener(v -> {

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
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("serial", Build.SERIAL);
                jsonObject.put("model", Build.MODEL);
                jsonObject.put("id", Build.ID);
                jsonObject.put("manufacturer", Build.MANUFACTURER);
                jsonObject.put("brand", Build.BRAND);
                jsonObject.put("type", Build.TYPE);
                jsonObject.put("user", Build.USER);
                jsonObject.put("base", Build.VERSION_CODES.BASE);
                jsonObject.put("incremental", Build.VERSION.INCREMENTAL);
                jsonObject.put("sdk", Build.VERSION.SDK);
                jsonObject.put("board", Build.BOARD);
                jsonObject.put("host", Build.HOST);
                jsonObject.put("fingerprint", Build.FINGERPRINT);
                jsonObject.put("versioncode", Build.VERSION.RELEASE);
            } catch (Exception e) {
                Log.d("TAG", "onClick: " + e.getMessage());
            }
            param.put("feedback_message", commentbox.getText().toString().isEmpty() ? "" : commentbox.getText().toString());
            param.put("error_reason", TeleHealerApplication.feedbackreason);
            param.put("error_message", TeleHealerApplication.feedbackreason);
            param.put("device_meta_info", jsonObject.toString());
            param.put("feedback_respone", responsedata);
            param.put("module", getCallType(callRequest.getCallType()));
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

                if (firstQ.trim().equals(questionModel.getData().get(0).getMainQuestionAnswer())) {

                    for (int i = 1; i < tempdata.size(); i++) {
                        if (tempdata.get(i).value == null || tempdata.get(i).value.isEmpty()) {
                            Toast.makeText(activity, "All question's are mandatory", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
                feedbackCallback.onActionSuccess(param);
                responseModels.clear();
                dialog.dismiss();
            } else {
                Toast.makeText(activity, "Select atleast one option.", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();

        setQuestion(questionModel, activity);
        setcheckListner();

    }

    public static void addFeedbackResponse(Integer questionid, String question, String value) {
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
        rbquestionone.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton rb = (RadioButton) radioGroup.findViewById(i);
            firstQ = rb.getText().toString();
            if (rb.getText().toString().contains(questiondata.getData().get(0).getMainQuestionAnswer())) {
                questionption.setVisibility(View.VISIBLE);
                addFeedbackResponse(questiondata.getData().get(0).getFeedbacksQuestionsId(), questiondata.getData().get(0).getQuestion(), rb.getText().toString());
            } else {
                questionption.setVisibility(View.GONE);
                addFeedbackResponse(questiondata.getData().get(0).getFeedbacksQuestionsId(), questiondata.getData().get(0).getQuestion(), rb.getText().toString());
                clearSelection();
            }
        });
    }

    private static void clearSelection() {

        for (int i = 0; i < questiondata.getData().size(); i++) {
            questiondata.getData().get(i).setuserSelect("");
        }
        feedbackAdapter.notifyDataSetChanged();
    }

    private static void setQuestion(FeedbackQuestionModel questionModel, Activity activity) {

        if (questionModel != null) {

            Collections.sort(questionModel.getData(), new Comparator<FeedbackQuestionModel.Datum>() {
                @Override
                public int compare(FeedbackQuestionModel.Datum datum, FeedbackQuestionModel.Datum t1) {
                    return datum.getFeedbacksQuestionsId().compareTo(t1.getFeedbacksQuestionsId());
                }
            });
            for (int i = 0; i < 1; i++) {
                setQuestionAnswer(i);
            }
            feedbackAdapter = new FeedbackAdapter(activity, questionModel.getData(), callrequest);
            lvquestion.setAdapter((ListAdapter) feedbackAdapter);
        } else {
            dialog.dismiss();
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
                if (callrequest.getCallType().equals(OpenTokConstants.video) || callrequest.getCallType().equals(OpenTokConstants.oneWay)) {
                    llquestiontwo.setVisibility(View.VISIBLE);
                    questionstwo.setText("" + questiondata.getData().get(1).getQuestion());
                    rbtngood.setText("" + questiondata.getData().get(1).getOptions().get(0));
                    rbtnbad.setText("" + questiondata.getData().get(1).getOptions().get(1));
                } else if (questiondata.getData().get(1).getQuestion().contains(callrequest.getCallType())) {
                    llquestiontwo.setVisibility(View.VISIBLE);
                    questionstwo.setText("" + questiondata.getData().get(1).getQuestion());
                    rbtngood.setText("" + questiondata.getData().get(1).getOptions().get(0));
                    rbtnbad.setText("" + questiondata.getData().get(1).getOptions().get(1));
                } else {
                    llquestiontwo.setVisibility(View.GONE);
                }
                break;
            case 2:
                if (callrequest.getCallType().equals(OpenTokConstants.video) || callrequest.getCallType().equals(OpenTokConstants.oneWay)) {
                    llquestionthree.setVisibility(View.VISIBLE);
                    questionsthree.setText("" + questiondata.getData().get(2).getQuestion());
                    rbtngoodq3.setText("" + questiondata.getData().get(2).getOptions().get(0));
                    rbtnbadq3.setText("" + questiondata.getData().get(2).getOptions().get(1));
                } else if (questiondata.getData().get(2).getQuestion().contains(callrequest.getCallType())) {
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

                    if (!questiondata.getData().get(3).getIsPhysiciansQuestion()) {
                        if (UserType.isUserPatient()) {
                            if (callrequest.getCallType().equals(OpenTokConstants.video) || callrequest.getCallType().equals(OpenTokConstants.oneWay)) {
                                llquestionfour.setVisibility(View.VISIBLE);
                                questionsfour.setText("" + questiondata.getData().get(3).getQuestion());
                                rbtngoodq4.setText("" + questiondata.getData().get(3).getOptions().get(0));
                                rbtnbadq4.setText("" + questiondata.getData().get(3).getOptions().get(1));
                            } else if (questiondata.getData().get(3).getQuestion().contains(callrequest.getCallType())) {
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
                    } else {
                        llquestionfour.setVisibility(View.GONE);
                    }
                }
                break;
            case 4:
                if (questiondata.getData().get(4).getIsPhysiciansQuestion() != null) {

                    if (!questiondata.getData().get(4).getIsPhysiciansQuestion()) {
                        if (UserType.isUserPatient()) {
                            if (callrequest.getCallType().equals(OpenTokConstants.video) || callrequest.getCallType().equals(OpenTokConstants.oneWay)) {
                                llquestionfive.setVisibility(View.VISIBLE);
                                questionsfive.setText("" + questiondata.getData().get(4).getQuestion());
                                rbtngoodq5.setText("" + questiondata.getData().get(4).getOptions().get(0));
                                rbtnbadq5.setText("" + questiondata.getData().get(4).getOptions().get(1));
                            } else if (questiondata.getData().get(4).getQuestion().contains(callrequest.getCallType())) {
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
                    } else {
                        llquestionfive.setVisibility(View.GONE);
                    }
                }
                break;
            case 5:
                if (questiondata.getData().get(5).getIsPhysiciansQuestion() != null) {

                    if (questiondata.getData().get(5).getIsPhysiciansQuestion()) {
                        if (!UserType.isUserPatient()) {
                            if (callrequest.getCallType().equals(OpenTokConstants.video) || callrequest.getCallType().equals(OpenTokConstants.oneWay)) {
                                llquestionsix.setVisibility(View.VISIBLE);
                                questionssix.setText("" + questiondata.getData().get(5).getQuestion());
                                rbtngoodq6.setText("" + questiondata.getData().get(5).getOptions().get(0));
                                rbtnbadq6.setText("" + questiondata.getData().get(5).getOptions().get(1));
                            } else if (questiondata.getData().get(5).getQuestion().contains(callrequest.getCallType())) {
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
                    } else {
                        llquestionsix.setVisibility(View.GONE);
                    }
                }
                break;
            case 6:
                if (questiondata.getData().get(6).getIsPhysiciansQuestion() != null) {
                    if (questiondata.getData().get(6).getIsPhysiciansQuestion()) {
                        if (!UserType.isUserPatient()) {
                            if (callrequest.getCallType().equals(OpenTokConstants.video) || callrequest.getCallType().equals(OpenTokConstants.oneWay)) {
                                llquestionseven.setVisibility(View.VISIBLE);
                                questionseven.setText("" + questiondata.getData().get(6).getQuestion());
                                rbtngoodq7.setText("" + questiondata.getData().get(6).getOptions().get(0));
                                rbtnbadq7.setText("" + questiondata.getData().get(6).getOptions().get(1));
                            } else if (questiondata.getData().get(6).getQuestion().contains(callrequest.getCallType())) {
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
                    } else {
                        llquestionseven.setVisibility(View.GONE);
                    }
                }
                break;
            default:
                break;
        }
    }

    public static String getCallType(String callType) {

        switch (callType) {

            case OpenTokConstants.audio:
                return OpenTokConstants.audio_call;
            case OpenTokConstants.video:
                return OpenTokConstants.video_call;
            case OpenTokConstants.oneWay:
                return OpenTokConstants.one_way_call;

        }

        return "";

    }

}
