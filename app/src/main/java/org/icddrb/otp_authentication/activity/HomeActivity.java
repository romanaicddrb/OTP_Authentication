package org.icddrb.otp_authentication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.icddrb.otp_authentication.R;


public class HomeActivity  extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button logoutBtn = findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            HomeActivity.this.finish();
        });
    }
}