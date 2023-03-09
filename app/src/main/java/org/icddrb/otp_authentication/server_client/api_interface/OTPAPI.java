package org.icddrb.otp_authentication.server_client.api_interface;

import org.icddrb.otp_authentication.model_class.request.OTPRequestModel;
import org.icddrb.otp_authentication.model_class.request.OTPVerifyModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OTPAPI {
    @Headers({"accept: application/json",
            "Content-Type: application/json"})
//    @POST("request_otp")
    @POST("otp")
    Call<String> requestOTP(@Body OTPRequestModel requestObject);

    @Headers({"accept: application/json",
            "Content-Type: application/json"})
//    @POST("submit_otp")
    @POST("otp/verify")
    Call<String> verifyOTP(@Body OTPVerifyModel requestObject);

}
