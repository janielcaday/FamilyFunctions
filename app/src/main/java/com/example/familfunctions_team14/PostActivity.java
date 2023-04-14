package com.example.familfunctions_team14;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

//import com.theartofdev.edmodo.cropper.CropImage;

public class PostActivity extends AppCompatActivity {

    private Button close;
    private TextView post;
    private EditText titleOfChore;
    private EditText descriptionOfChore;
    private EditText pointsOfChore;

    private DatabaseReference mRootRef;
    private StorageReference mStorageRef;

    private StorageTask mUploadTask;

    private Spinner spinner;

    String linkCode;
    String assignedTo;

    private Button mButtonChooseImage;
    private ImageView mImageView;

    private Uri mImageUri;


    ArrayList<String> childNames = new ArrayList<String>();
    ArrayAdapter<String> adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        
        loadChildNames();
        close = findViewById(R.id.close);
        mImageView = findViewById(R.id.image_added);
        post = findViewById(R.id.post);
        titleOfChore = findViewById(R.id.title_of_chore);
        descriptionOfChore = findViewById(R.id.description_of_chore);
        pointsOfChore = findViewById(R.id.points_of_chore);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference("chorephotos");

        getDataFromActivity();

        spinner = findViewById(R.id.spinner);

        showDataSpinner(linkCode);

        mButtonChooseImage = findViewById(R.id.choose_file);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToMainActivity();
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUploadTask != null && mUploadTask.isInProgress()){
                    Toast.makeText(PostActivity.this, "Please wait. Upload in progress.", Toast.LENGTH_SHORT).show();
                } else {
                    String titleTxt = titleOfChore.getText().toString();
                    String descrptnTxt = descriptionOfChore.getText().toString();
                    String pointsTxt = pointsOfChore.getText().toString();

                    if(titleTxt.isEmpty() || descrptnTxt.isEmpty() || pointsTxt.isEmpty()){
                        Toast.makeText(PostActivity.this, "Empty fields", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        addToDatabase(linkCode, titleTxt, descrptnTxt, pointsTxt, "0");
                        backToMainActivity();
                    }
                }
            }
        });

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        final ArrayList<String> childNames = new ArrayList<>();
        final ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.list_item, childNames);

    }

    private void showDataSpinner(String linkCode) {
        DatabaseReference reference = mRootRef.child("FamilyFunctions").child(linkCode).child("Children");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                childNames.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    childNames.add(snapshot.child("firstname").getValue(String.class));
                }
                ArrayAdapter adapter = new ArrayAdapter<String>(PostActivity.this, R.layout.list_item, childNames);
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                assignedTo = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadChildNames() {

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

                        Picasso.get().load(mImageUri).into(mImageView);
                    }
                }
            });

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void backToMainActivity() {
        titleOfChore.setText("");
        descriptionOfChore.setText("");
        pointsOfChore.setText("");

        Intent backToMain = new Intent(PostActivity.this, ParentMain.class);
        backToMain.putExtra("FamilyCodeA2A", linkCode);
        startActivity(backToMain);
        finish();
    }

    private void addToDatabase(String linkCode, String titleTxt, String descrptnTxt, String pointsTxt, String status) {
//        final StorageReference ref = mStorageRef
        if(mImageUri != null){
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String choreId = mRootRef.child("FamilyFunctions").child(linkCode).child("Chores").push().getKey();

                            Chores chore = new Chores();
                            chore.setImageUrl(uri.toString());

                            chore.setTitle(titleTxt);
                            chore.setDescription(descrptnTxt);
                            chore.setPointsWorth(pointsTxt);
                            chore.setStatus(status);
                            chore.setAssignedTo(assignedTo);
                            chore.setId(choreId);

                            mRootRef.child("FamilyFunctions").child(linkCode).child("Chores").child(choreId).setValue(chore)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(PostActivity.this, "Chore successfully created", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(PostActivity.this, "Error", Toast.LENGTH_SHORT).show();
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

    private void getDataFromActivity(){
        linkCode = ParentMain.linkCode;
    }
}