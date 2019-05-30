package com.example.ravikundu.chatapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mUsersList;

    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerOptions<Users> mUsersOption;
    private FirebaseRecyclerAdapter<Users,AllUsersViewHolder> mFirebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar = findViewById(R.id.users_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All USERS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersList = findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        mUsersOption = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(mDatabaseReference,Users.class).build();

        mFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, AllUsersViewHolder>(mUsersOption) {
            @Override
            protected void onBindViewHolder(@NonNull AllUsersViewHolder allUsersViewHolder, int position, @NonNull Users users) {

                String user_id = getRef(position).getKey();

                allUsersViewHolder.setData(users.getName(),users.getStatus(),users.getThumb_image(), user_id);

            }

            @NonNull
            @Override
            public AllUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_layout,parent,false);

                return new AllUsersViewHolder(view);
            }
        };

        mUsersList.setLayoutManager(new LinearLayoutManager(this));
        mFirebaseRecyclerAdapter.startListening();
        mUsersList.setAdapter(mFirebaseRecyclerAdapter);

        Toast.makeText(getApplicationContext(),"Users added - 1",Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mFirebaseRecyclerAdapter!=null){
            mFirebaseRecyclerAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mFirebaseRecyclerAdapter!=null){
            mFirebaseRecyclerAdapter.startListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mFirebaseRecyclerAdapter!=null){
            mFirebaseRecyclerAdapter.startListening();
        }
    }

}
