package com.example.otp_authentication.server_client;

import android.app.Dialog;
import android.content.Context;

import com.example.otp_authentication.R;
import com.example.otp_authentication.model_class.request.OTPRequestModel;
import com.example.otp_authentication.server_client.api_interface.OTPAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://mchdweb.icddrb.org/otp/api/OtpRequests/";

    public static Retrofit get(Context context) {
        show(context);
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    static private Dialog loaderDialog;

    static public void show(Context context) {

        if (loaderDialog == null) {
            loaderDialog = new Dialog(context);
            loaderDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            loaderDialog.setContentView(R.layout.dialog_loader);
            loaderDialog.setCancelable(false);
        }

        if (!loaderDialog.isShowing()) {
            loaderDialog.show();
        }
    }

    static public void dismiss() {
        if (loaderDialog.isShowing()) {
            loaderDialog.dismiss();
            loaderDialog = null;
        }
    }


}
