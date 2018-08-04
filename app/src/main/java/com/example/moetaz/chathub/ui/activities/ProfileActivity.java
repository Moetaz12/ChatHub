package com.example.moetaz.chathub.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.help.Utilities;
import com.firebase.client.Firebase;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import butterknife.ButterKnife;

import static com.example.moetaz.chathub.help.FirebaseConstants.EMAIL_NODE;
import static com.example.moetaz.chathub.help.FirebaseConstants.FB_ROOT;
import static com.example.moetaz.chathub.help.FirebaseConstants.HASPROFILEPIC;
import static com.example.moetaz.chathub.help.FirebaseConstants.PROFILE_PIC;
import static com.example.moetaz.chathub.help.FirebaseConstants.USERINFO_NODE;
import static com.example.moetaz.chathub.help.FirebaseConstants.USERNAME_NODE;

public class ProfileActivity extends AppCompatActivity {
    private static final int REQUET_CODE = 1;
    ImageView imageView;
    Firebase mCheck;
    TextView textViewEmail, textViewUsername;
    ProgressDialog progressDialog;
    DatabaseReference databaseRef;
    private StorageReference storageReference;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        mCheck = new Firebase(FB_ROOT + Utilities.getUserId());
        storageReference = FirebaseStorage.getInstance().getReference();
        imageView = findViewById(R.id.profile_pic);
        textViewEmail = findViewById(R.id.textView_email);
        textViewUsername = findViewById(R.id.textView_username);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.profile_title);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUET_CODE);
            }
        });

        databaseRef = FirebaseDatabase.getInstance().getReference().child(USERINFO_NODE)
                .child(Utilities.getUserId());
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StorageReference filepath = storageReference.child(getString(R.string.picsFolderFirebase)
                        + Utilities.getUserId() + getString(R.string.jpgExt));
                if (dataSnapshot.hasChild(HASPROFILEPIC)) {
                    Glide.with(getApplicationContext()).using(new FirebaseImageLoader())
                            .load(filepath).into(imageView);
                } else {
                    imageView.setBackgroundResource(R.drawable.avatar);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        setEmailInfo();
        setNameInfo();

    }

    private void setNameInfo() {

        databaseRef.child(USERNAME_NODE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textViewUsername.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setEmailInfo() {
        databaseRef.child(EMAIL_NODE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textViewEmail.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUET_CODE && resultCode == RESULT_OK) {
            progressDialog.setMessage(getString(R.string.upload_meg));
            progressDialog.show();

            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] bData = baos.toByteArray();
            imageView.setImageBitmap(bitmap);

            final StorageReference filepath = storageReference.child(getString(R.string.picsFolderFirebase)
                    + Utilities.getUserId() + getString(R.string.jpgExt));
            filepath.putBytes(bData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Firebase childRef3 = mCheck.child(HASPROFILEPIC);
                    childRef3.setValue("1");
                    progressDialog.dismiss();
                    Uri downloadurl = taskSnapshot.getDownloadUrl();
                    if (downloadurl != null) {
                        Picasso.with(getApplicationContext()).load(downloadurl).into(imageView);
                        databaseRef.child(PROFILE_PIC).setValue(downloadurl.toString());
                        Utilities.saveProfilePicUrl(getApplicationContext());
                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }
}
