package com.example.otp_authentication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import com.example.otp_authentication.R;
import com.example.otp_authentication.Util.AppUtils;
import com.example.otp_authentication.Util.Loader;
import com.example.otp_authentication.model_class.request.OTPRequestModel;
import com.example.otp_authentication.server_client.RetrofitCallBack;
import com.example.otp_authentication.server_client.RetrofitClient;
import com.example.otp_authentication.server_client.api_interface.OTPAPI;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText mobileNo;
    TextInputEditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mobileNo = findViewById(R.id.mobile_no);
        email = findViewById(R.id.email);

        Button otpBtn = findViewById(R.id.get_otp_btn);
        otpBtn.setOnClickListener(view -> {
            if (isValid()) {
                sendOTP();
            }
        });
    }

    boolean isValid() {
        String mail = email.getText().toString();
        if (mail.isEmpty()) {
            email.setError("Email address can not be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            email.setError("Invalid email address");
            return false;
        } else {
            return true;
        }
    }

    public void sendOTP() {

        OTPRequestModel requestModel = new OTPRequestModel(
                AppUtils.getInstance(LoginActivity.this).getDeviceID(),
                email.getText().toString(), "email");

        RetrofitClient.get(this)
                .create(OTPAPI.class)
                .requestOTP(requestModel)
                .enqueue(new RetrofitCallBack<String>() {
                    @Override
                    public void onSuccess(@NonNull Response<String> response) {
                        OTPDialog otpDialog = new OTPDialog(LoginActivity.this,
                                email.getText().toString());
                        otpDialog.show();
                    }

                    @Override
                    public void onFail(@NonNull String errorMsg) {

                        Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();

                    }
                });
    }

}