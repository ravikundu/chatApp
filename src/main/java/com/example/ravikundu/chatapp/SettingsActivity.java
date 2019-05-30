package com.example.ravikundu.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    private StorageReference mStorageRefenence;

    private CircleImageView mCircleImage;
    private TextView mName;
    private TextView mStatus;

    private Button mSaveImage;
    private Button mSaveStatus;

    private ProgressDialog mProgressDialog;

    private static int GALLERY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mCircleImage = findViewById(R.id.settingsImage);
        mName = findViewById(R.id.settingsDisplayName);
        mStatus = findViewById(R.id.settingsStatus);

        mSaveImage = findViewById(R.id.settingsChangeImageBtn);
        mSaveStatus = findViewById(R.id.settingsChangeStatusBtn);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String UID = mCurrentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);
        mDatabase.keepSynced(true);
        mStorageRefenence = FirebaseStorage.getInstance().getReference();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String username = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                mName.setText(username);
                mStatus.setText(status);

                if(!image.equals("default")) {
                    //Picasso.get().load(image).placeholder(R.drawable.default_avatar04).into(mCircleImage);

                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_avatar04).into(mCircleImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {

                            Picasso.get().load(image).placeholder(R.drawable.default_avatar04).into(mCircleImage);

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mSaveStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent statusActivity = new Intent(SettingsActivity.this,StatusActivity.class);
                statusActivity.putExtra("status_value",mStatus.getText().toString());
                startActivity(statusActivity);

            }
        });

        mSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_PICK);

                /*
                //by using downloaded api
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);
                        */

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            Uri imageURI = data.getData();

            Toast.makeText(getApplicationContext(),imageURI.toString(),Toast.LENGTH_LONG).show();

            CropImage.activity(imageURI).setAspectRatio(1,1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                mProgressDialog = new ProgressDialog(SettingsActivity.this);
                mProgressDialog.setTitle("Uploading image....");
                mProgressDialog.setMessage("Please wait while we upload image.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                File thumb_filePath = new File(resultUri.getPath());

                final String UID = mCurrentUser.getUid();

                Bitmap thumb_bitmap = null;

                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("SettingActivityBitmap: ",e.getMessage());
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.WEBP,100,baos);
                final byte[] thumb_bytes = baos.toByteArray();

                final StorageReference filePath = mStorageRefenence.child("profile_images").child(UID+".jpg");
                final StorageReference thumbFilepathStorageRef = mStorageRefenence.child("profile_images").child("thumb_images").child(UID+".jpg");

                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                final String image_downloadURL = uri.toString();

                                Toast.makeText(SettingsActivity.this,"Image Uploaded",Toast.LENGTH_LONG).show();

                                UploadTask uploadTask = thumbFilepathStorageRef.putBytes(thumb_bytes);
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        thumbFilepathStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                String thumb_downloadURL = uri.toString();

                                                Toast.makeText(SettingsActivity.this,"Image Uploaded",Toast.LENGTH_LONG).show();

                                                Map updateHashmap = new HashMap();
                                                updateHashmap.put("image",image_downloadURL);
                                                updateHashmap.put("thumb_image",thumb_downloadURL);

                                                mDatabase.updateChildren(updateHashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()){

                                                            Toast.makeText(SettingsActivity.this,"URLs Uploaded",Toast.LENGTH_LONG).show();

                                                        }else{

                                                            Toast.makeText(SettingsActivity.this," Error in URLs Uploading",Toast.LENGTH_LONG).show();

                                                        }
                                                        mProgressDialog.dismiss();
                                                    }
                                                });

                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(SettingsActivity.this,"Error in uploading",Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();

                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    /*
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
    */
}
