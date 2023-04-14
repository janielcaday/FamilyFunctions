package com.example.familfunctions_team14;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ChildSubmissionActivity extends AppCompatActivity {

    private TextView presentTitle;
    private TextView presentDescription;
    private TextView presentPointsWorth;
    private ImageView currentImage;
    
    String receivedTitle;
    String receivedDescription;
    String receivedPointsWorth;
    String receivedId;
    String receivedAssignedTo;
    String receivedStatus;

    private Button uploadNewPhoto;
    private TextView submitNewPhoto;
    private Button close;

    private Uri mImageUri;
    private StorageTask mUploadTask;

    String linkCode;

    private DatabaseReference mRootRef;
    private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_submission);

        presentTitle = findViewById(R.id.title_of_chore);
        presentDescription = findViewById(R.id.description_of_chore);
        presentPointsWorth = findViewById(R.id.points_of_chore);
        currentImage = findViewById(R.id.image_to_change);

        close = findViewById(R.id.close);
        uploadNewPhoto = findViewById(R.id.choose_file);
        submitNewPhoto = findViewById(R.id.post);
        
        receivedTitle = getIntent().getStringExtra("choreCurrentTitle");
        receivedDescription = getIntent().getStringExtra("choreCurrentDescription");
        receivedPointsWorth = getIntent().getStringExtra("choreCurrentPointsWorth");
        receivedAssignedTo = getIntent().getStringExtra("choreCurrentAssignedTo");
        receivedId = getIntent().getStringExtra("choreCurrentId");

        presentTitle.setText("Chore: "+ receivedTitle);
        presentDescription.setText("Description: \n" + receivedDescription);
        presentPointsWorth.setText("Points: " + receivedPointsWorth);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference("chorephotos");

        Picasso.get()
                .load(getIntent().getStringExtra("choreCurrentImageUrl"))
                .fit()
                .centerCrop()
                .into(currentImage);

        getDataFromActivity();


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToMainActivity();
            }
        });

        uploadNewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        
        submitNewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(ChildSubmissionActivity.this, "Please wait. Upload in progress.", Toast.LENGTH_SHORT).show();
                } else {
                    modifyImage("1");
//                    addToDatabase(linkCode, titleTxt, descrptnTxt, pointsTxt, "0");
                    backToMainActivity();
                }
            }
        });

        //Glide.with(this).load(getIntent().getStringExtra("choreCurrentImageUrl")).into(currentImage);
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void modifyImage(String status) {
        if(mImageUri != null){
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String choreId = receivedId;

                            Chores chore = new Chores();
//                            chore.setImageUrl(String.valueOf(uri));

                            chore.setImageUrl(uri.toString());

                            chore.setAssignedTo(receivedAssignedTo);
                            chore.setDescription(receivedDescription);
                            chore.setPointsWorth(receivedPointsWorth);
                            chore.setStatus(status);
                            chore.setTitle(receivedTitle);
                            chore.setId(receivedId);

                            mRootRef.child("FamilyFunctions").child(linkCode).child("Chores").child(choreId).setValue(chore)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(ChildSubmissionActivity.this, "Chore successfully created", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ChildSubmissionActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                }
            });

        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        someActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        mImageUri = data.getData();

                        Picasso.get().load(mImageUri).into(currentImage);
                    }
                }
            });

    private void backToMainActivity() {
        Intent backToMain = new Intent(ChildSubmissionActivity.this, ChildMain.class);
        backToMain.putExtra("FamilyCodeA2A", linkCode);
        startActivity(backToMain);
        finish();
    }

    private void getDataFromActivity(){
        linkCode = ChildMain.linkCode;
    }
}