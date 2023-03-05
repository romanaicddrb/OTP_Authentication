package org.icddrb.otp_authentication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import org.icddrb.otp_authentication.R;
import org.icddrb.otp_authentication.Util.AppUtils;
import org.icddrb.otp_authentication.model_class.request.OTPRequestModel;
import org.icddrb.otp_authentication.server_client.RetrofitCallBack;
import org.icddrb.otp_authentication.server_client.RetrofitClient;
import org.icddrb.otp_authentication.server_client.api_interface.OTPAPI;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText mobileNo;
    TextInputEditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        AppSignatureHelper helper = new AppSignatureHelper(this);
//        ArrayList<String> signs = helper.getAppSignatures();
//
//        for (String sig : signs) {
//            System.out.println("SIGNTURES " + sig);
//        }

//        hash code : sXzcZUledx5



        mobileNo = findViewById(R.id.mobile_no);
        email = findViewById(R.id.email);

        Button otpBtn = findViewById(R.id.get_otp_btn);
        otpBtn.setOnClickListener(view -> {

                    OTPRequestModel requestModel = new OTPRequestModel(
                "","", "");

            openOTPDialog(requestModel);

//            if (isValid()) {
//                sendOTP();
//            }
        });
    }

    boolean isValid() {
        String mobile = mobileNo.getText().toString();
        String mail = email.getText().toString();
        if (mobile.isEmpty()) {
            mobileNo.setError("Mobile number can not be empty");
            return false;
        } else if (mail.isEmpty()) {
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

//        OTPRequestModel requestModel = new OTPRequestModel(
//                AppUtils.getInstance(LoginActivity.this).getDeviceID(),
//                email.getText().toString(), "email");

        OTPRequestModel requestModel = new OTPRequestModel(
                AppUtils.getInstance(LoginActivity.this).getDeviceID(),
                mobileNo.getText().toString(), "mobile");

        RetrofitClient.get(this)
                .create(OTPAPI.class)
                .requestOTP(requestModel)
                .enqueue(new RetrofitCallBack<String>() {
                    @Override
                    public void onSuccess(@NonNull Response<String> response) {
                        openOTPDialog(requestModel);
                    }

                    @Override
                    public void onFail(@NonNull String errorMsg) {

                        Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();

                    }
                });
    }

    private void openOTPDialog(OTPRequestModel requestModel){
//                        OTPDialog otpDialog = new OTPDialog(LoginActivity.this, requestModel);
//                        otpDialog.show();
        OtpDialogFragment dialogFragment = OtpDialogFragment.newInstance(requestModel);
        dialogFragment.show(getSupportFragmentManager(),"My  Fragment");
    }

}