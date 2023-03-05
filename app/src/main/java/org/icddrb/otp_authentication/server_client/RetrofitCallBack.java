package org.icddrb.otp_authentication.server_client;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

abstract public class RetrofitCallBack<T> implements Callback<T> {

    @Override
    public void onResponse(Call call, Response response) {
        Log.e("Response ", response.toString());
        RetrofitClient.dismiss();
        if (response.code() == 200){
            Log.e("Response body ", response.body().toString());
            onSuccess(response);
        }else if (response.code() == 201){
            Log.e("Response body ", response.body().toString());
            onFail(response.body().toString());
        }else{
            try {
                String str = response.errorBody().string();
                Log.e("RetrofitCallBack onResponse error", str);
                onFail(str);
            } catch (IOException e) {
                onFail("Fail to get error message");
            }
        }

    }

    @Override
    public void onFailure(Call call, Throwable t) {
        RetrofitClient.dismiss();
        Log.e("RetrofitCallBack onFailure", t.getLocalizedMessage());
        onFail("Unknown Error");
    }

    public abstract void onSuccess(@NonNull Response<T> response);

    public abstract void onFail(@NonNull String errorMsg);
}