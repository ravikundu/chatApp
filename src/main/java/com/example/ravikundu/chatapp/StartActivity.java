package com.example.ravikundu.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private Button startRegBtn;
    private Button startLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        startRegBtn = findViewById(R.id.startRegBtn);
        startLoginBtn = findViewById(R.id.startLoginBtn);


        startRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerActivity = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(registerActivity);
            }
        });

        startLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginActivity = new Intent(StartActivity.this,LoginActivity.class);
                startActivity(loginActivity);
            }
        });
    }
}
