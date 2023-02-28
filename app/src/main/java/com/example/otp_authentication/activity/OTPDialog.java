package com.example.otp_authentication.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.otp_authentication.R;
import com.example.otp_authentication.Util.AppUtils;
import com.example.otp_authentication.model_class.request.OTPRequestModel;
import com.example.otp_authentication.model_class.request.OTPVerifyModel;
import com.example.otp_authentication.pin_view.PinView;
import com.example.otp_authentication.server_client.RetrofitCallBack;
import com.example.otp_authentication.server_client.RetrofitClient;
import com.example.otp_authentication.server_client.api_interface.OTPAPI;
import com.google.android.material.button.MaterialButton;

import java.util.concurrent.TimeUnit;

import retrofit2.Response;

public class OTPDialog extends Dialog {

    Activity activity;
    String email;

    PinView pinView;

    AppCompatTextView errorText;
    AppCompatTextView timeCounter;

    MaterialButton resendBtn;

    public OTPDialog(Activity a, String email) {
        super(a);
        this.activity = a;
        this.email = email;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.dialog_otp);

        pinView = findViewById(R.id.pin_view);
        errorText = findViewById(R.id.error);
        timeCounter = findViewById(R.id.time_count);

        MaterialButton verifyBtn = findViewById(R.id.verify_btn);
        verifyBtn.setOnClickListener(view -> {
            if (isValid()) {
                submitOTP();
            }
        });

        resendBtn = findViewById(R.id.resend_btn);
        resendBtn.setOnClickListener(view -> resendOTP());

        setCounter();

    }

    @SuppressLint("DefaultLocale")
    void setCounter(){
        resendBtn.setEnabled(false);
        resendBtn.setTextColor(activity.getResources().getColor(R.color.gray, null));

        new CountDownTimer(180000, 1000) {

            public void onTick(long millisUntilFinished) {
                timeCounter.setText(String.format("%02d:%02d min",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
                ));
            }

            public void onFinish() {
                timeCounter.setText("00:00 min");
                resendBtn.setEnabled(true);
                resendBtn.setTextColor(activity.getResources().getColor(R.color.primary_color_dark, null));
            }
        }.start();

    }

    boolean isValid() {
        String pin = pinView.getText();
        if (pin.isEmpty()) {
            errorText.setVisibility(View.VISIBLE);
            errorText.setText("Pin can not be empty");
            return false;
        } else if (pin.length() != 4) {
            errorText.setVisibility(View.VISIBLE);
            errorText.setText("Incorrect pin");
            return false;
        } else {
            errorText.setVisibility(View.GONE);
            return true;
        }
    }

    public void submitOTP() {

        OTPVerifyModel requestModel = new OTPVerifyModel(
                AppUtils.getInstance(activity.getApplicationContext()).getDeviceID(),
                email, Integer.parseInt(pinView.getText()));

        RetrofitClient.get(activity)
                .create(OTPAPI.class)
                .verifyOTP(requestModel)
                .enqueue(new RetrofitCallBack<String>() {
                    @Override
                    public void onSuccess(@NonNull Response<String> response) {
                        activity.startActivity(new Intent(activity, HomeActivity.class));
                        activity.finish();
                        dismiss();
                    }

                    @Override
                    public void onFail(@NonNull String errorMsg) {
                        errorText.setVisibility(View.VISIBLE);
                        errorText.setText(errorMsg);
                    }
                });
    }

    public void resendOTP() {

        OTPRequestModel requestModel = new OTPRequestModel(
                AppUtils.getInstance(activity.getApplicationContext()).getDeviceID(),
                email, "email");

        RetrofitClient.get(activity)
                .create(OTPAPI.class)
                .requestOTP(requestModel)
                .enqueue(new RetrofitCallBack<String>() {
                    @Override
                    public void onSuccess(@NonNull Response<String> response) {
                        setCounter();
                    }

                    @Override
                    public void onFail(@NonNull String errorMsg) {
                        errorText.setVisibility(View.VISIBLE);
                        errorText.setText(errorMsg);
                    }
                });
    }

}
