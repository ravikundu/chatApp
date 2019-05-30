package com.example.ravikundu.chatapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllFriendsViewHolder extends RecyclerView.ViewHolder {

    private View mMainView;

    public AllFriendsViewHolder(@NonNull View itemView) {
        super(itemView);

        mMainView = itemView;
    }

    public void setData(final String UID, String date, final String name, String thumb, String online){

        TextView mDate = mMainView.findViewById(R.id.singleUserStatus);
        TextView mName = mMainView.findViewById(R.id.singleUserName);
        CircleImageView userImageView = mMainView.findViewById(R.id.singleUserImage);
        ImageView onlineImage = mMainView.findViewById(R.id.singleUserOnlineImage);

        mDate.setText(date);
        mName.setText(name);
        Picasso.get().load(thumb).placeholder(R.drawable.default_avatar04).into(userImageView);

        if(online != null && online.equals("true")){
            onlineImage.setVisibility(View.VISIBLE);
        }else {
            onlineImage.setVisibility(View.INVISIBLE);
        }

        mMainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence options[] = new CharSequence[]{"View Profile","Send Message"};

                AlertDialog.Builder builder = new AlertDialog.Builder(mMainView.getContext());
                builder.setTitle("Select an option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                        if(i==0){

                            Intent profileIntent = new Intent(mMainView.getContext(),ProfileActivity.class);
                            profileIntent.putExtra("UID",UID);
                            mMainView.getContext().startActivity(profileIntent);

                        }else{

                            Intent chatIntent = new Intent(mMainView.getContext(),ChatActivity.class);
                            chatIntent.putExtra("UID",UID);
                            chatIntent.putExtra("userName",name);
                            mMainView.getContext().startActivity(chatIntent);

                        }

                    }
                });
                builder.show();

            }
        });

        Toast.makeText(mMainView.getContext(),"AllFriendsViewHolder : Complete",Toast.LENGTH_LONG).show();

    }
}
