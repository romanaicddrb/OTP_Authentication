package com.example.otp_authentication.server_client.api_interface;

import com.example.otp_authentication.model_class.request.OTPRequestModel;
import com.example.otp_authentication.model_class.request.OTPVerifyModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OTPAPI {
    @Headers({"accept: text/plain",
            "Content-Type: application/json"})
    @POST("request_otp")
    Call<String> requestOTP(@Body OTPRequestModel requestObject);

    @Headers({"accept: text/plain",
            "Content-Type: application/json"})
    @POST("submit_otp")
    Call<String> verifyOTP(@Body OTPVerifyModel requestObject);

}
