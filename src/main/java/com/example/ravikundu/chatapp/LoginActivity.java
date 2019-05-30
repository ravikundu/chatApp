package com.example.ravikundu.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private TextInputEditText mEmail;
    private TextInputEditText mPassword;

    private Button mLoginBtn;

    private ProgressDialog mProgressDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mToolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("LOGIN");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEmail = findViewById(R.id.loginEmailTxt);
        mPassword = findViewById(R.id.loginPasswordTxt);
        mLoginBtn = findViewById(R.id.loginBtn);

        mProgressDialog = new ProgressDialog(this);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

                    mProgressDialog.setTitle("Logging In");
                    mProgressDialog.setMessage("Please wait while we check in your credentials !!");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();

                    loginUser(email,password);
                }else{
                    Toast.makeText(getApplicationContext(),"Fill all the details",Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    mProgressDialog.dismiss();

                    String currentUserID = mAuth.getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    mUserRef.child(currentUserID).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            // Sign in success, update UI with the signed-in user's information
                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();

                        }
                    });

                } else {

                    mProgressDialog.hide();

                    // If sign in fails, display a message to the user.
                    Toast.makeText(getApplicationContext(),"Something Went Wrong.",Toast.LENGTH_LONG).show();
                }

            }
        });

    }
}
