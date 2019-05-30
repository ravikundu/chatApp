package com.example.ravikundu.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersViewHolder extends RecyclerView.ViewHolder {

    View mView;

    public AllUsersViewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;

    }

    public void setData(String name, String status, String image, final String UID) {

        TextView userNameView = mView.findViewById(R.id.singleUserName);
        TextView userStatusView = mView.findViewById(R.id.singleUserStatus);
        CircleImageView userImageView = mView.findViewById(R.id.singleUserImage);
        ImageView onlineImage = mView.findViewById(R.id.singleUserOnlineImage);

        userNameView.setText(name);
        userStatusView.setText(status);
        onlineImage.setVisibility(View.INVISIBLE);
        Picasso.get().load(image).placeholder(R.drawable.default_avatar04).into(userImageView);

        Toast.makeText(mView.getContext(),"Users added - 5  ",Toast.LENGTH_LONG).show();

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent profileActivity = new Intent(mView.getContext(),ProfileActivity.class);
                profileActivity.putExtra("UID",UID);
                mView.getContext().startActivity(profileActivity);

            }
        });

    }

}
