package com.thealer.telehealer.common.OpenTok;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.OpenTok.openTokInterfaces.OnDSListener;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.R;

import java.util.List;

/**
 * Created by rsekar on 1/30/19.
 */

public class GoogleSpeechRecognizer {
    private final String TAG = "DroidSpeech";

    private Context context;
    private SpeechRecognizer droidSpeechRecognizer;
    private Intent speechIntent;
    private AudioManager audioManager;
    private Handler restartDroidSpeech = new Handler();
    private Handler droidSpeechPartialResult = new Handler();
    private Properties dsProperties = new Properties();
    private OnDSListener droidSpeechListener;

    // MARK: Constructor

    /**
     * Droid Speech Constructor
     *
     * @param context         The application context instance
     */
    public GoogleSpeechRecognizer(Context context) {
        this.context = context;
        dsProperties.listeningMsg = "";
    }

    // MARK: Droid Speech Listener

    /**
     * Sets the droid speech listener
     *
     * @param droidSpeechListener The class instance to initialize droid speech listener
     */
    public void setOnDroidSpeechListener(OnDSListener droidSpeechListener) {
        this.droidSpeechListener = droidSpeechListener;
    }

    // MARK: Droid Speech Private Methods

    /**
     * Initializes the droid speech properties
     */
    private void initDroidSpeechProperties() {
        // Initializing the droid speech recognizer
        droidSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);

        // Initializing the speech intent
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
        speechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        speechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, Constants.MAX_VOICE_RESULTS);
        if (dsProperties.currentSpeechLanguage != null) {
            // Setting the speech language
            speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, dsProperties.currentSpeechLanguage);
            speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, dsProperties.currentSpeechLanguage);
        }

        if (dsProperties.offlineSpeechRecognition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Setting offline speech recognition to true
            speechIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
        }

        // Initializing the audio Manager
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }


    /**
     * Restarts droid speech recognition after a small delay
     */
    private void restartDroidSpeechRecognition() {
        restartDroidSpeech.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (dsProperties.closedByUser) {
                    dsProperties.closedByUser = false;

                    // If audio beep was muted, enabling it again
                    muteAudio(true);
                } else {
                    startDroidSpeechRecognition();
                }
            }

        }, Constants.MAX_PAUSE_TIME);
    }

    /**
     * Mutes (or) un mutes the audio
     *
     * @param mute The mute audio status
     */
    @SuppressWarnings("deprecation")
    private void muteAudio(Boolean mute) {
        try {
            // mute (or) un mute audio based on status
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, mute ? AudioManager.ADJUST_MUTE : AudioManager.ADJUST_UNMUTE, 0);
            } else {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, mute);
            }
        } catch (Exception e) {
            if (audioManager == null) return;

            // un mute the audio if there is an exception
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);
            } else {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            }
        }
    }

    /**
     * Cancels the droid speech operations
     */
    private void cancelDroidSpeechOperations() {
        if (droidSpeechRecognizer != null) {
            droidSpeechRecognizer.cancel();
        }
    }

    /**
     * Closes the droid speech operations
     */
    private void closeDroidSpeech() {
        if (droidSpeechRecognizer != null) {
            droidSpeechRecognizer.destroy();
        }

        // Removing the partial result callback handler if applicable
        droidSpeechPartialResult.removeCallbacksAndMessages(null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // If audio beep was muted, enabling it again
                muteAudio(false);
            }
        },5000);
    }

    // MARK: Droid Speech Public Methods

    /**
     * Sets the preferred language
     *
     * @param language The language code
     */
    public void setPreferredLanguage(String language) {
        if (dsProperties.supportedSpeechLanguages.contains(language)) {
            dsProperties.currentSpeechLanguage = language;

            // Reinitializing the speech properties
            initDroidSpeechProperties();
        }
    }

    /**
     * Sets the listening msg when droid speech is listening to user
     *
     * @param listeningMsg The desired listening msg
     */
    public void setListeningMsg(String listeningMsg) {
        if (dsProperties.listeningMsg != null) {
            dsProperties.listeningMsg = listeningMsg;
        }
    }


    /**
     * Sets the offline speech recognition status
     * <p>
     * NOTE: Default is false, sdk version should be API 23 and above, speech package
     * installed needs to be checked beforehand independently
     *
     * @param offlineSpeechRecognition The offline speech recognition status
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void setOfflineSpeechRecognition(boolean offlineSpeechRecognition) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dsProperties.offlineSpeechRecognition = offlineSpeechRecognition;

            // Reinitializing the speech properties
            initDroidSpeechProperties();
        }
    }

    /**
     * Gets the continuous speech recognition status
     *
     * @return The continuous speech recognition status
     */
    public boolean getContinuousSpeechRecognition() {
        return dsProperties.continuousSpeechRecognition;
    }

    /**
     * Sets the continuous speech recognition status
     * <p>
     * NOTE: Default is true
     *
     * @param continuousSpeechRecognition The continuous speech recognition status
     */
    public void setContinuousSpeechRecognition(boolean continuousSpeechRecognition) {
        dsProperties.continuousSpeechRecognition = continuousSpeechRecognition;
    }

    /**
     * Sets the show recognition progress view status
     * <p>
     * NOTE: Default is false
     *
     * @param showRecognitionProgressView The show recognition progress view status
     */
    public void setShowRecognitionProgressView(boolean showRecognitionProgressView) {
        dsProperties.showRecognitionProgressView = showRecognitionProgressView;
    }

    /**
     * Sets the one step result verify status
     * <p>
     * NOTE: Default is "false", if "true" will be applicable only when recognition progress view is enabled
     *
     * @param oneStepResultVerify True - result will be verified with user, False - if otherwise
     */
    public void setOneStepResultVerify(boolean oneStepResultVerify) {
        dsProperties.oneStepResultVerify = oneStepResultVerify;
    }



    /**
     * Starts the droid speech recognition
     * <p>
     * Trigger Listeners - onDroidSpeechError(int errorType)
     */
    public void startDroidSpeechRecognition() {
        muteAudio(true);
        dsProperties.closedByUser = false;

        if (Utils.isInternetEnabled(context) || dsProperties.offlineSpeechRecognition) {

                dsProperties.startListeningTime = System.currentTimeMillis();
                dsProperties.pauseAndSpeakTime = dsProperties.startListeningTime;
                dsProperties.speechResultFound = false;

                if (droidSpeechRecognizer == null || speechIntent == null || audioManager == null) {
                    // Initializing the droid speech properties if found not initialized
                    initDroidSpeechProperties();
                }

                // Setting the droid speech recognizer listener
                droidSpeechRecognizer.setRecognitionListener(new RecognitionListener() {

                    @Override
                    public void onReadyForSpeech(Bundle bundle) {
                        // If audio beep was muted, enabling it again
                        muteAudio(true);

                        dsProperties.onReadyForSpeech = true;
                    }

                    @Override
                    public void onBeginningOfSpeech() {
                        // NA
                    }

                    @Override
                    public void onRmsChanged(float rmsdB) {

                    }

                    @Override
                    public void onBufferReceived(byte[] bytes) {
                        // NA
                    }

                    @Override
                    public void onEndOfSpeech() {
                        // NA
                    }

                    @Override
                    public void onError(int error) {
                        if (dsProperties.closedByUser) {
                            dsProperties.closedByUser = false;

                            return;
                        }

                        long duration = System.currentTimeMillis() - dsProperties.startListeningTime;

                        // If duration is less than the "error timeout" as the system didn't try listening to the user speech so ignoring
                        if (duration < Constants.ERROR_TIMEOUT && error == SpeechRecognizer.ERROR_NO_MATCH && !dsProperties.onReadyForSpeech)
                            return;

                        if (dsProperties.onReadyForSpeech && duration < Constants.AUDIO_BEEP_DISABLED_TIMEOUT) {
                            // Disabling audio beep if less than "audio beep disabled timeout", as it will be
                            // irritating for the user to hear the beep sound again and again
                            muteAudio(true);
                        } else {
                            // If audio beep was muted, enabling it again
                            muteAudio(true);
                        }

                        if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT || error == SpeechRecognizer.ERROR_AUDIO) {
                            // Restart droid speech recognition
                            restartDroidSpeechRecognition();
                        } else if (droidSpeechListener == null) {
                            Log.e(TAG, "Droid speech error, code = " + error);
                        } else {
                            if (error <= context.getResources().getStringArray(R.array.speech_errors).length) {
                                // Sending an update with the droid speech error
                                droidSpeechListener.onDroidSpeechError(context.getResources().getStringArray(R.array.speech_errors)[error - 1]);
                            } else {
                                // Sending an update that there was an unknown error
                                droidSpeechListener.onDroidSpeechError(context.getResources().getString(R.string.speech_unknown_error));
                            }
                        }
                    }

                    @SuppressWarnings("ConstantConditions")
                    @Override
                    public void onResults(Bundle results) {
                        if (dsProperties.speechResultFound) return;

                        dsProperties.speechResultFound = true;

                        // If audio beep was muted, enabling it again
                        muteAudio(true);

                        Boolean valid = (results != null && results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION) &&
                                results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) != null &&
                                results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).size() > 0 &&
                                !results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0).trim().isEmpty());

                        if (valid) {
                            // Getting the droid speech final result
                            String droidSpeechFinalResult = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
                            if (dsProperties.showRecognitionProgressView && dsProperties.oneStepResultVerify) {
                                // Saving the speech result
                                dsProperties.oneStepVerifySpeechResult = droidSpeechFinalResult;

                                // Closing droid speech operations, will be restarted when user clicks
                                // cancel or confirm if applicable
                                closeDroidSpeech();
                            } else {
                                if (droidSpeechListener == null) {
                                    Log.i(TAG, "Droid speech final result = " + droidSpeechFinalResult);
                                } else {
                                    // Sending an update with the droid speech final result
                                    droidSpeechListener.onDroidSpeechFinalResult(droidSpeechFinalResult);
                                }

                                if (dsProperties.continuousSpeechRecognition) {
                                    // Start droid speech recognition again
                                    startDroidSpeechRecognition();
                                } else {
                                    // Closing the droid speech operations
                                    closeDroidSpeechOperations();
                                }
                            }
                        } else {
                            // No match found, restart droid speech recognition
                            restartDroidSpeechRecognition();
                        }
                    }

                    @SuppressWarnings("ConstantConditions")
                    @Override
                    public void onPartialResults(Bundle partialResults) {
                        if (dsProperties.speechResultFound) return;

                        Boolean valid = (partialResults != null && partialResults.containsKey(SpeechRecognizer.RESULTS_RECOGNITION) &&
                                partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) != null &&
                                partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).size() > 0 &&
                                !partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0).trim().isEmpty());

                        if (valid) {
                            final String droidLiveSpeechResult = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);

                            if (droidSpeechListener == null) {
                                Log.i(TAG, "Droid speech live result = " + droidLiveSpeechResult);
                            } else {
                                // Sending an update with the droid speech live result
                                droidSpeechListener.onDroidSpeechLiveResult(droidLiveSpeechResult);
                            }

                            if ((System.currentTimeMillis() - dsProperties.pauseAndSpeakTime) > Constants.MAX_PAUSE_TIME) {
                                dsProperties.speechResultFound = true;

                                droidSpeechPartialResult.postDelayed(new Runnable() {

                                    @Override
                                    public void run() {

                                        // Closing droid speech operations
                                        closeDroidSpeech();

                                        if (dsProperties.showRecognitionProgressView && dsProperties.oneStepResultVerify) {
                                            // Saving the speech result
                                            dsProperties.oneStepVerifySpeechResult = droidLiveSpeechResult;

                                            // Closing droid speech operations, will be restarted when user clicks
                                            // cancel or confirm if applicable
                                            closeDroidSpeech();
                                        } else {
                                            if (droidSpeechListener == null) {
                                                Log.i(TAG, "Droid speech final result = " + droidLiveSpeechResult);
                                            } else {
                                                // Sending an update with the droid speech final result (Partial live result
                                                // is taken as the final result in this case)
                                                droidSpeechListener.onDroidSpeechFinalResult(droidLiveSpeechResult);

                                                if (dsProperties.continuousSpeechRecognition) {
                                                    // Start droid speech recognition again
                                                    startDroidSpeechRecognition();
                                                } else {
                                                    // Closing the droid speech operations
                                                    closeDroidSpeechOperations();
                                                }
                                            }
                                        }
                                    }

                                }, Constants.PARTIAL_DELAY_TIME);
                            } else {
                                dsProperties.pauseAndSpeakTime = System.currentTimeMillis();
                            }
                        } else {
                            dsProperties.pauseAndSpeakTime = System.currentTimeMillis();
                        }
                    }

                    @Override
                    public void onEvent(int i, Bundle bundle) {
                        // NA
                    }
                });

                // Canceling any running droid speech operations, before listening
                cancelDroidSpeechOperations();

                // Start Listening
                droidSpeechRecognizer.startListening(speechIntent);

        } else {

            if (droidSpeechListener == null) {
                Log.e(TAG, context.getResources().getString(R.string.internet_not_enabled));
            } else {
                // Sending an update that there was a network error
                droidSpeechListener.onDroidSpeechError(context.getResources().getString(R.string.internet_not_enabled));
            }
        }
    }

    /**
     * Closes the entire droid speech operations
     */
    public void closeDroidSpeechOperations() {
        closeDroidSpeech();
    }
}
