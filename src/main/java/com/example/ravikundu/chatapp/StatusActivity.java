package com.example.ravikundu.chatapp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout mTextInputLayout;
    private Button mSaveBtn;

    private DatabaseReference mDatabaseReference;
    private FirebaseUser mCurrentUser;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String UID = mCurrentUser.getUid();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);

        mToolbar = findViewById(R.id.status_app_Bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("STATUS UPDATE");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTextInputLayout = findViewById(R.id.statusTextInput);
        mSaveBtn = findViewById(R.id.statusSaveBtn);

        String status_value = getIntent().getStringExtra("status_value");
        mTextInputLayout.getEditText().setText(status_value);

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog = new ProgressDialog(StatusActivity.this);
                mProgressDialog.setTitle("Saving Changes");
                mProgressDialog.setMessage("Please wait while we save changes");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                String status = mTextInputLayout.getEditText().getText().toString();

                mDatabaseReference.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            mProgressDialog.dismiss();

                        }else{

                            mProgressDialog.cancel();
                            Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_LONG).show();

                        }

                    }
                });

            }
        });

    }
}
