package com.example.ravikundu.chatapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView mProfileImage;
    private TextView mProfileName,mProfileStatus,mProfileFriendsCount;
    private Button mFriendRequestBtn;
    private Button mRequestDeclineBtn;

    private DatabaseReference mDatabaseRef;
    private DatabaseReference mFriendReqDatabaseRef;
    private DatabaseReference mFriendsDatabaseRef;
    private DatabaseReference mNotificationRef;
    private DatabaseReference mRootRef;
    private FirebaseUser mCurrentUser;

    private ProgressDialog mProgressDialog;

    private String currentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String UID = getIntent().getStringExtra("UID");

        currentState = "not_friends";

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);
        mFriendReqDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friend_Request");
        mFriendsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationRef = FirebaseDatabase.getInstance().getReference().child("notifications");
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mProfileImage = findViewById(R.id.profileImage);
        mProfileName = findViewById(R.id.profileName);
        mProfileStatus = findViewById(R.id.profileStatus);
        mProfileFriendsCount = findViewById(R.id.profileTotalFriends);
        mFriendRequestBtn = findViewById(R.id.profileFriendRequestBtn);
        mRequestDeclineBtn = findViewById(R.id.profileRequestDeclineBtn);


        mProgressDialog = new ProgressDialog(ProfileActivity.this);
        mProgressDialog.setTitle("Uploading User Data....");
        mProgressDialog.setMessage("Please wait while we upload User Data.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String imageURL = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(name);
                mProfileStatus.setText(status);

                Picasso.get().load(imageURL).placeholder(R.drawable.default_avatar04).into(mProfileImage);

                mRequestDeclineBtn.setVisibility(View.INVISIBLE);
                mRequestDeclineBtn.setEnabled(false);

                mFriendReqDatabaseRef.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(UID)){

                            String req_type = dataSnapshot.child(UID).child("request_type").getValue().toString();

                            if(req_type.equals("received")){

                                currentState = "req_received";
                                mFriendRequestBtn.setText("Accept Request");

                                mRequestDeclineBtn.setVisibility(View.VISIBLE);
                                mRequestDeclineBtn.setEnabled(true);

                            }else{

                                currentState = "req_sent";
                                mFriendRequestBtn.setText("Cancel Request");

                            }

                        }else{

                            mFriendsDatabaseRef.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(UID)){

                                        mFriendRequestBtn.setText("Unfriend");
                                        currentState = "Friends";

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }

                        mProgressDialog.dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mProgressDialog.dismiss();
            }
        });

        mFriendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mFriendRequestBtn.setEnabled(false);

                //-------------Not Firends-----------//

                if(currentState.equals("not_friends")){

                    DatabaseReference mNotificationRef = mRootRef.child("notifications").child(UID).push();
                    String mNotiicationID = mNotificationRef.getKey();

                    HashMap<String,String> notificationData = new HashMap<>();
                    notificationData.put("from",mCurrentUser.getUid());
                    notificationData.put("type","request");

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_Request/" + mCurrentUser.getUid() + "/" + UID + "/" + "request_type","sent");
                    requestMap.put("Friend_Request/"+ UID  + "/" + mCurrentUser.getUid() + "/" + "request_type","received");
                    requestMap.put("notifications/" + UID + "/" + mNotiicationID , notificationData);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if(databaseError != null){

                                Toast.makeText(ProfileActivity.this, "Failed to Send Request",Toast.LENGTH_LONG).show();

                            }
                            mFriendRequestBtn.setEnabled(true);
                            mFriendRequestBtn.setText("Cancel Request");
                            currentState = "req_sent";

                            mRequestDeclineBtn.setVisibility(View.INVISIBLE);
                            mRequestDeclineBtn.setEnabled(false);

                        }
                    });

                }

                //-------------Cancel Request-----------//

                if(currentState.equals("req_sent")){

                    mFriendReqDatabaseRef.child(mCurrentUser.getUid()).child(UID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendReqDatabaseRef.child(UID).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mFriendRequestBtn.setEnabled(true);
                                    mFriendRequestBtn.setText("Send Request");
                                    currentState = "not_friends";

                                    mRequestDeclineBtn.setVisibility(View.INVISIBLE);
                                    mRequestDeclineBtn.setEnabled(false);

                                }
                            });
                        }
                    });

                }

                //-------------Request Received State-----------//

                if(currentState.equals("req_received")){

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    mFriendsDatabaseRef.child(mCurrentUser.getUid()).child(UID).child("date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendsDatabaseRef.child(UID).child(mCurrentUser.getUid()).child("date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mFriendReqDatabaseRef.child(mCurrentUser.getUid()).child(UID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mFriendReqDatabaseRef.child(UID).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    mFriendRequestBtn.setEnabled(true);
                                                    mFriendRequestBtn.setText("Unfriend");
                                                    currentState = "Friends";

                                                    mRequestDeclineBtn.setVisibility(View.INVISIBLE);
                                                    mRequestDeclineBtn.setEnabled(false);

                                                }
                                            });
                                        }
                                    });

                                }
                            });

                        }
                    });

                }

                //----------Unfriend---------//

                if(currentState.equals("Friends")){

                    mFriendsDatabaseRef.child(mCurrentUser.getUid()).child(UID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendsDatabaseRef.child(UID).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mFriendRequestBtn.setEnabled(true);
                                    mFriendRequestBtn.setText("Send Request");
                                    currentState = "not_friends";

                                    mRequestDeclineBtn.setVisibility(View.INVISIBLE);
                                    mRequestDeclineBtn.setEnabled(false);

                                }
                            });
                        }
                    });

                }

            }
        });

    }
}
