package org.icddrb.otp_authentication.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import org.icddrb.otp_authentication.R;

import org.icddrb.otp_authentication.model_class.request.OTPRequestModel;
import org.icddrb.otp_authentication.model_class.request.OTPVerifyModel;
import org.icddrb.otp_authentication.pin_view.PinView;
import org.icddrb.otp_authentication.receiver.SMSReceiver;
import org.icddrb.otp_authentication.server_client.RetrofitCallBack;
import org.icddrb.otp_authentication.server_client.RetrofitClient;
import org.icddrb.otp_authentication.server_client.api_interface.OTPAPI;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.material.button.MaterialButton;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Response;

public class OTPDialog extends Dialog {

    Activity activity;

    OTPRequestModel requestModel;

    PinView pinView;

    AppCompatTextView errorText;
    AppCompatTextView timeCounter;

    MaterialButton resendBtn;

    SMSReceiver smsReceiver;

    public OTPDialog(Activity a, OTPRequestModel requestModel) {
        super(a);
        this.activity = a;
        this.requestModel = requestModel;
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

        startSmartUserConsent();

    }

    private void startSmartUserConsent() {

        SmsRetrieverClient client = SmsRetriever.getClient(activity);
        client.startSmsUserConsent(null);

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

        OTPVerifyModel requestObject = new OTPVerifyModel(
                requestModel.getDeviceId(),
                requestModel.getRequestUid(),
                Integer.parseInt(pinView.getText()));

        RetrofitClient.get(activity)
                .create(OTPAPI.class)
                .verifyOTP(requestObject)
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




    private void getOtpFromMessage(String message) {

        Pattern otpPattern = Pattern.compile("(|^)\\d{6}");
        Matcher matcher = otpPattern.matcher(message);
        if (matcher.find()){
            pinView.setText(matcher.group(0));
        }
    }
    private void registerBroadcastReceiver(){

        smsReceiver = new SMSReceiver();

        smsReceiver.smsBroadcastReceiverListener = new SMSReceiver.SmsBroadcastReceiverListener() {
            @Override
            public void onSuccess(Intent intent) {

//                startActivityForResult(intent,REQ_USER_CONSENT);

            }

            @Override
            public void onFailure() {

            }
        };

        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        activity.registerReceiver(smsReceiver,intentFilter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcastReceiver();

    }

    @Override
    protected void onStop() {
        super.onStop();
        activity.unregisterReceiver(smsReceiver);
    }
}
