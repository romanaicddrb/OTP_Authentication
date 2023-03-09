package org.icddrb.otp_authentication.activity;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.*;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.*;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.material.button.MaterialButton;

import org.icddrb.otp_authentication.R;
import org.icddrb.otp_authentication.model_class.request.OTPRequestModel;
import org.icddrb.otp_authentication.model_class.request.OTPVerifyModel;
import org.icddrb.otp_authentication.pin_view.PinView;
import org.icddrb.otp_authentication.receiver.SMSReceiver;
import org.icddrb.otp_authentication.server_client.RetrofitCallBack;
import org.icddrb.otp_authentication.server_client.RetrofitClient;
import org.icddrb.otp_authentication.server_client.api_interface.OTPAPI;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Response;

public class OtpDialogFragment extends DialogFragment {

    OTPRequestModel requestModel;

    PinView pinView;

    AppCompatTextView errorText;
    AppCompatTextView timeCounter;

    MaterialButton resendBtn;

    SMSReceiver smsReceiver;

    private static final int REQ_USER_CONSENT = 200;

    ActivityResultLauncher activityResultLauncher;

    public static OtpDialogFragment newInstance(OTPRequestModel requestModel) {
        OtpDialogFragment frag = new OtpDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("object", requestModel);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestModel = getArguments().getParcelable("object");

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if ((result.getResultCode() == RESULT_OK) && (result.getData() != null)) {
                        String message = result.getData().getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                        getOtpFromMessage(message);
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return inflater.inflate(R.layout.dialog_otp, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        pinView = view.findViewById(R.id.pin_view);
        errorText = view.findViewById(R.id.error);
        timeCounter = view.findViewById(R.id.time_count);

        MaterialButton verifyBtn = view.findViewById(R.id.verify_btn);
        verifyBtn.setOnClickListener(v -> {
            if (isValid()) {
                submitOTP();
            }
        });

        resendBtn = view.findViewById(R.id.resend_btn);
        resendBtn.setOnClickListener(v -> resendOTP());

        setCounter();

        startSmartUserConsent();

    }

    private void startSmartUserConsent() {

        SmsRetrieverClient client = SmsRetriever.getClient(getActivity());
        client.startSmsUserConsent(null);

    }

    @SuppressLint("DefaultLocale")
    void setCounter() {
        resendBtn.setEnabled(false);
        resendBtn.setTextColor(getActivity().getResources().getColor(R.color.gray, null));

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
                resendBtn.setTextColor(getActivity().getResources().getColor(R.color.primary_color_dark, null));
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
                requestModel,
                pinView.getText());

        RetrofitClient.get(getActivity())
                .create(OTPAPI.class)
                .verifyOTP(requestObject)
                .enqueue(new RetrofitCallBack<String>() {
                    @Override
                    public void onSuccess(@NonNull Response<String> response) {
                        getActivity().startActivity(new Intent(getActivity(), HomeActivity.class));
                        getActivity().finish();
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

        RetrofitClient.get(getActivity())
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
        if (matcher.find()) {
            pinView.setText(matcher.group(0));
        }
    }

    private void registerBroadcastReceiver() {

        smsReceiver = new SMSReceiver();

        smsReceiver.smsBroadcastReceiverListener = new SMSReceiver.SmsBroadcastReceiverListener() {
            @Override
            public void onSuccess(Intent intent) {

//                startActivityForResult(intent, REQ_USER_CONSENT);

                activityResultLauncher.launch(intent);

            }

            @Override
            public void onFailure() {

            }
        };

        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        getActivity().registerReceiver(smsReceiver, intentFilter);

    }

    @Override
    public void onStart() {
        super.onStart();
        registerBroadcastReceiver();
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(smsReceiver);
    }

}
