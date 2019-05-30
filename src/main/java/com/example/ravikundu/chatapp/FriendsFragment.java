package com.example.ravikundu.chatapp;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsList;

    private DatabaseReference mFriendsDatabaseRef;
    private DatabaseReference mUsersDatabaseRef;
    private FirebaseAuth mFirebaseAuth;

    private FirebaseRecyclerOptions<Friends> mFriendsOption;
    private FirebaseRecyclerAdapter<Friends,AllFriendsViewHolder> mFirebaseRecyclerAdapter;

    private String mCurrentUserID;

    private String mFriendUID;
    private String mFriendName;
    private String mFriendThumb;
    private String mFriendOnline;

    private View mMainView;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_friends,container,false);

        mFriendsList = mMainView.findViewById(R.id.friends_list);
        mFirebaseAuth = FirebaseAuth.getInstance();

        mCurrentUserID = mFirebaseAuth.getCurrentUser().getUid();

        mFriendsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrentUserID);
        mUsersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mFriendsList.setHasFixedSize(true);

        mFriendsOption = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(mFriendsDatabaseRef,Friends.class).build();

        mFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, AllFriendsViewHolder>(mFriendsOption) {
            @Override
            protected void onBindViewHolder(@NonNull AllFriendsViewHolder allFriendsViewHolder, int position, @NonNull Friends friends) {

                String user_id = getRef(position).getKey();

                mUsersDatabaseRef.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        mFriendName = dataSnapshot.child("name").getValue().toString();
                        mFriendThumb = dataSnapshot.child("thumb_image").getValue().toString();
                        if(dataSnapshot.hasChild("online")) {
                            mFriendOnline = dataSnapshot.child("online").getValue().toString();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(mMainView.getContext(),"DataCancelled",Toast.LENGTH_LONG).show();
                    }
                });

                allFriendsViewHolder.setData(user_id,friends.getDate(),mFriendName,mFriendThumb,mFriendOnline);

            }

            @NonNull
            @Override
            public AllFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_layout,parent,false);

                return new AllFriendsViewHolder(view);

            }
        };

        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mFirebaseRecyclerAdapter.startListening();
        mFriendsList.setAdapter(mFirebaseRecyclerAdapter);

        // Inflate the layout for this fragment
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mFirebaseRecyclerAdapter!=null){
            mFirebaseRecyclerAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mFirebaseRecyclerAdapter!=null){
            mFirebaseRecyclerAdapter.startListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mFirebaseRecyclerAdapter!=null){
            mFirebaseRecyclerAdapter.startListening();
        }
    }

}
