package com.ameron32.chatreborn.helpers;

import android.inputmethodservice.InputMethodService;

import com.google.android.voiceime.VoiceRecognitionTrigger;

import com.ameron32.knbasic.core.chat.R;

import android.app.Service;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.TextView;

public class MyIME extends InputMethodService {
	private static final String TAG = "DemoInputMethodService";

    private ImageButton mButton;

    private View mView;

    private TextView mText;

    private VoiceRecognitionTrigger mVoiceRecognitionTrigger;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "#onCreate");

        // Create the voice recognition trigger, and register the listener.
        // The trigger has to unregistered, when the IME is destroyed.
        mVoiceRecognitionTrigger = new VoiceRecognitionTrigger(this);
        mVoiceRecognitionTrigger.register(new VoiceRecognitionTrigger.Listener() {

            @Override
            public void onVoiceImeEnabledStatusChange() {
                // The call back is done on the main thread.
                updateVoiceImeStatus();
            }
        });
    }

    @Override
    public View onCreateInputView() {
        Log.i(TAG, "#onCreateInputView");
        LayoutInflater inflater = (LayoutInflater) getSystemService(
                Service.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.ime, null);

        mText= (TextView) mView.findViewById(R.id.message);

        mButton = (ImageButton) mView.findViewById(R.id.mic_button);
        if (mVoiceRecognitionTrigger.isInstalled()) {

            // Voice recognition is installed on the phone, and the onClick listener is set.
            // When voice recognition is triggered, the IME should pass its language, so
            // voice recognition will be done in the same language. The language should be
            // specified in Java locale format.
            // If the IME does not have a language, the IME should call
            // mVoiceRecognitionTrigger.startVoiceRecognition() without any parameter.
            mButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mVoiceRecognitionTrigger.startVoiceRecognition(getImeLanguage());
                }
            });

            // The status of the IME (i.e., installed and enabled, installed and displayed)
            // is updated.
            updateVoiceImeStatus();
        } else {

            // No voice recognition is installed, and the microphone icon is not displayed.
            mText.setText("api not available");
            mButton.setVisibility(View.GONE);
        }
        return mView;
    }

    /**
     * Returns the language of the IME. The langauge is used in voice recognition to match the
     * current language of the IME.
     */
    private String getImeLanguage() {
        return "en-US";
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        Log.i(TAG, "#onStartInputView");
        super.onStartInputView(info, restarting);
        if (mVoiceRecognitionTrigger != null) {
            // This method call is required for pasting the recognition results into the TextView
            // when the recognition is done using the Intent API.
            mVoiceRecognitionTrigger.onStartInputView();
        }
    }

    /**
     * Update the microphone icon to reflect the status of the voice recognition.
     */
    private void updateVoiceImeStatus() {
        if (mButton == null) {
            return;
        }

        if (mVoiceRecognitionTrigger.isInstalled()) {
            mButton.setVisibility(View.VISIBLE);
            if (mVoiceRecognitionTrigger.isEnabled()) {
                // Voice recognition is installed and enabled.
                mButton.setEnabled(true);
            } else {
                // Voice recognition is installed, but it is not enabled (no network).
                // The microphone icon is displayed greyed-out.
                mButton.setEnabled(false);
            }
        } else {
            // Voice recognition is not installed, and the microphone icon is not displayed.
            mButton.setVisibility(View.GONE);
        }
        mView.invalidate();
    }

    @Override
    public boolean onEvaluateFullscreenMode() {
        return false;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "#onDestroy");
        if (mVoiceRecognitionTrigger != null) {
            // To avoid service leak, the trigger has to be unregistered when
            // the IME is destroyed.
            mVoiceRecognitionTrigger.unregister(this);
        }
        super.onDestroy();
    }
}
