package com.example.ravikundu.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText mUsername;
    private TextInputEditText mEmail;
    private TextInputEditText mPassword;

    private Button regBtn;

    private android.support.v7.widget.Toolbar mToolbar;

    private ProgressDialog mProgressDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUsername = findViewById(R.id.regUsernameTxt);
        mEmail = findViewById(R.id.regEmailTxt);
        mPassword = findViewById(R.id.regPasswordTxt);

        regBtn = findViewById(R.id.regBtn);

        mToolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("CREATE ACCOUNT");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = mUsername.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword .getText().toString();

                if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

                    mProgressDialog.setTitle("Registering User");
                    mProgressDialog.setMessage("Please wait while we create your account !!");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();

                    register_user(username,email,password);
                }else{
                    Toast.makeText(getApplicationContext(),"Fill all the details",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void register_user(final String username, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String UID = currentUser.getUid();

                            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();

                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("device_token",deviceToken);
                            userMap.put("name",username);
                            userMap.put("status","Hi there i am using chatApp !!");
                            userMap.put("image","default");
                            userMap.put("thumb_image","default");

                            mDatabaseReference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        mProgressDialog.dismiss();

                                        // Sign in success, update UI with the signed-in user's information
                                        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();
                                    }else {

                                        mProgressDialog.hide();

                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(getApplicationContext(),"Something Went Wrong.",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            /*
                            String error = "";
                            try {
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException  e){
                                error = "Weak Password";
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                error = "Invalid Credentials";
                            }catch (FirebaseAuthUserCollisionException e){
                                error = "Existing User";
                            }catch (FirebaseAuthEmailException e){
                                error = "Invalid Email";
                            }catch (Exception e) {
                                error = "Unknown Error";
                                e.printStackTrace();
                            }
                            Toast.makeText(RegisterActivity.this,error,Toast.LENGTH_LONG).show();
                            */

                        } else {

                            mProgressDialog.hide();

                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(),"Something Went Wrong.",Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
}
