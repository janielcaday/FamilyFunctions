package com.example.familfunctions_team14;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ParentVerificationActivity extends AppCompatActivity {

    private TextView presentTitle;
    private TextView presentDescription;
    private TextView presentPointsWorth;
    private ImageView currentImage;

    private Button close;
    private TextView submitDecision;
    private Chip deniedChip;
    private Chip approvedChip;

    String receivedTitle;
    String receivedDescription;
    String receivedPointsWorth;
    String receivedId;
    String receivedAssignedTo;
    String receivedStatus;
    String receivedImageUrl;

    String linkCode;

    boolean decision;

    private DatabaseReference mRootRef;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_verification);

        getDataFromActivity();

        presentTitle = findViewById(R.id.title_of_chore);
        presentDescription = findViewById(R.id.description_of_chore);
        presentPointsWorth = findViewById(R.id.points_of_chore);
        currentImage = findViewById(R.id.image_added);
        close = findViewById(R.id.close);
        submitDecision = findViewById(R.id.post);
        deniedChip = findViewById(R.id.deny);
        approvedChip = findViewById(R.id.approve);

        receivedTitle = getIntent().getStringExtra("choreCurrentTitle");
        receivedDescription = getIntent().getStringExtra("choreCurrentDescription");
        receivedPointsWorth = getIntent().getStringExtra("choreCurrentPointsWorth");
        receivedAssignedTo = getIntent().getStringExtra("choreCurrentAssignedTo");
        receivedId = getIntent().getStringExtra("choreCurrentId");
        receivedStatus = getIntent().getStringExtra("choreCurrentStatus");
        receivedImageUrl = getIntent().getStringExtra("choreCurrentImageUrl");

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

//        getDataFromActivity();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToMainActivity();
            }
        });

        deniedChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    decision = false;
                }
            }
        });

        approvedChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    decision = true;
                }
            }
        });

        submitDecision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(decision){
                    getChildName();
                    updateChoreStatusApproved();
                    Toast.makeText(ParentVerificationActivity.this, "Chore has been approved!", Toast.LENGTH_SHORT).show();
                    backToMainActivity();
                }
                else{
                    updateChoreStatusDenied();
                    Toast.makeText(ParentVerificationActivity.this, "Chore has been denied", Toast.LENGTH_SHORT).show();
                    backToMainActivity();
                }
            }
        });
    }

    private void updateChoreStatusApproved() {
        DatabaseReference reference = mRootRef.child("FamilyFunctions").child(ParentMain.linkCode).child("Chores");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String titleCheck = postSnapshot.child("title").getValue(String.class);
                    if (titleCheck.equals(receivedTitle)) {
                        Chores chore = new Chores();

                        chore.setAssignedTo(receivedAssignedTo);
                        chore.setDescription(receivedDescription);
                        chore.setId(receivedId);
                        chore.setImageUrl(receivedImageUrl);
                        chore.setPointsWorth(receivedPointsWorth);
                        chore.setStatus("3");
                        chore.setTitle(receivedTitle);


                        DatabaseReference referenceValue = mRootRef.child("FamilyFunctions").child(ParentMain.linkCode)
                                .child("Chores").child(receivedId);
                        referenceValue.setValue(chore);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateChoreStatusDenied() {
        DatabaseReference reference = mRootRef.child("FamilyFunctions").child(ParentMain.linkCode).child("Chores");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String titleCheck = postSnapshot.child("title").getValue(String.class);
                    if (titleCheck.equals(receivedTitle)) {
                        Chores chore = new Chores();

                        chore.setAssignedTo(receivedAssignedTo);
                        chore.setDescription(receivedDescription);
                        chore.setId(receivedId);
                        chore.setImageUrl(receivedImageUrl);
                        chore.setPointsWorth(receivedPointsWorth);
                        chore.setStatus("2");
                        chore.setTitle(receivedTitle);


                        DatabaseReference referenceValue = mRootRef.child("FamilyFunctions").child(ParentMain.linkCode)
                                .child("Chores").child(receivedId);
                        referenceValue.setValue(chore);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void backToMainActivity() {
        Intent backToMain = new Intent(ParentVerificationActivity.this, ParentMain.class);
        backToMain.putExtra("FamilyCodeA2A", linkCode);
        startActivity(backToMain);
        finish();
    }




    private void getChildName() {
        DatabaseReference reference = mRootRef.child("FamilyFunctions").child(ParentMain.linkCode).child("Children");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String nameCheck = postSnapshot.child("firstname").getValue(String.class);
                    if (nameCheck.equals(receivedAssignedTo)) {
                        String pointsCheck = postSnapshot.child("points").getValue(String.class);

                        String childsId = postSnapshot.child("id").getValue(String.class);
                        String childsEmail = postSnapshot.child("email").getValue(String.class);
                        String childsFirstname = postSnapshot.child("firstname").getValue(String.class);
                        String childsLastname = postSnapshot.child("lastname").getValue(String.class);
                        String childsPassword = postSnapshot.child("password").getValue(String.class);

                        String modifiedPoints = doMath(pointsCheck, receivedPointsWorth);

                        updatePoints(childsEmail, childsFirstname, childsId, childsLastname, childsPassword, modifiedPoints);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updatePoints(String childsEmail, String childsFirstname,String childsId, String childsLastname, String childsPassword, String modifiedPoints) {
        Children child = new Children();
        child.setEmail(childsEmail);
        child.setFirstname(childsFirstname);
        child.setId(childsId);
        child.setLastname(childsLastname);
        child.setPassword(childsPassword);
        child.setPoints(modifiedPoints);

        DatabaseReference referenceValue = mRootRef.child("FamilyFunctions").child(ParentMain.linkCode)
                .child("Children").child(childsId);
        referenceValue.setValue(child);

    }

    private String doMath(String num1, String num2) {

        int a = Integer.parseInt(num1);
        int b = Integer.parseInt(num2);
        int ans = a+b;

        receivedAssignedTo = "";

        return String.valueOf(ans);
    }

    private void getDataFromActivity(){
        linkCode = ParentMain.linkCode;
    }
}