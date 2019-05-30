package com.example.ravikundu.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllChatViewHolder extends RecyclerView.ViewHolder {

    public View mView;


    public AllChatViewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;
    }

    public void setData(final String userName, final String UID, String thumb, String LastText, String Date){

        TextView mUserName = mView.findViewById(R.id.singleChatName);
        TextView mLastText = mView.findViewById(R.id.singleChatLastText);
        TextView mLastSeen = mView.findViewById(R.id.singleChatDate);
        CircleImageView mProfileImage = mView.findViewById(R.id.singleChatImage);

        mUserName.setText(userName);
        mLastSeen.setText(Date);
        mLastText.setText(LastText);

        Picasso.get().load(thumb).placeholder(R.drawable.default_avatar04).into(mProfileImage);

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(mView.getContext(),ChatActivity.class);
                chatIntent.putExtra("UID",UID);
                chatIntent.putExtra("userName",userName);
                mView.getContext().startActivity(chatIntent);
            }
        });

    }

}
