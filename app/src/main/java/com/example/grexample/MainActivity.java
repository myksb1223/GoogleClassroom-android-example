package com.example.grexample;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends ThirdPartyLoginActivity {

    Button loginBtn, logoutBtn, listBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thirdPartLogin();
            }
        });

        logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thirdPartLogout();
            }
        });

        listBtn = findViewById(R.id.listBtn);
        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void afterLogout() {

    }

    @Override
    protected void updateUI(boolean login) {
        if(login) {
            loginBtn.setVisibility(View.GONE);
            logoutBtn.setVisibility(View.VISIBLE);
            listBtn.setVisibility(View.VISIBLE);
        }
        else {
            loginBtn.setVisibility(View.VISIBLE);
            logoutBtn.setVisibility(View.GONE);
            listBtn.setVisibility(View.GONE);
        }
    }
}
