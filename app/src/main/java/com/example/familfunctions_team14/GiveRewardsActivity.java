package com.example.familfunctions_team14;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GiveRewardsActivity extends AppCompatActivity {

    private Button close;
    private TextView pointsAwarded, submitAward;
    private Spinner rewardsSpinner;
    String giveTo;
    boolean isAdding;

    String linkCode;

    private DatabaseReference mRootRef;
    ArrayList<String> childNames = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_rewards);

        getDataFromActivity();

        close = findViewById(R.id.close);
        submitAward = findViewById(R.id.submit_award);
        pointsAwarded = findViewById(R.id.points_awarded);
        rewardsSpinner = findViewById(R.id.rewards_spinner);

        String pointsAwardedTxt = getIntent().getStringExtra("pointsToUse");

        pointsAwarded.setText("Points: " + pointsAwardedTxt);

        mRootRef = FirebaseDatabase.getInstance().getReference();

        showDataSpinner(ParentMain.linkCode);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToMainActivity();
            }
        });

        isAdding = true;


        submitAward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                givePoints(pointsAwardedTxt);
                backToMainActivity();
            }
        });
    }

    private void givePoints(String pointsAwardedTxt) {
        DatabaseReference reference = mRootRef.child("FamilyFunctions").child(ParentMain.linkCode).child("Children");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    String nameCheck = postSnapshot.child("firstname").getValue(String.class);
                    if(nameCheck.equals(giveTo)){

                        String pointsCheck = postSnapshot.child("points").getValue(String.class);

                        String childsId = postSnapshot.child("id").getValue(String.class);
                        String childsEmail = postSnapshot.child("email").getValue(String.class);
                        String childsFirstname = postSnapshot.child("firstname").getValue(String.class);
                        String childsLastname = postSnapshot.child("lastname").getValue(String.class);
                        String childsPassword = postSnapshot.child("password").getValue(String.class);

                        String modifiedPoints = doMath(pointsCheck, pointsAwardedTxt);

                        updatePoints(childsEmail, childsFirstname, childsId, childsLastname, childsPassword, modifiedPoints);
                        isAdding = false;

                        Toast.makeText(GiveRewardsActivity.this, nameCheck + " now has " + modifiedPoints + " points", Toast.LENGTH_SHORT).show();
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
        int ans = a-b;

        giveTo = "";

        return String.valueOf(ans);
    }

    private void backToMainActivity() {
        Intent backToMain = new Intent(GiveRewardsActivity.this, ParentMain.class);
        backToMain.putExtra("FamilyCodeA2A", ParentMain.linkCode);
        startActivity(backToMain);
        finish();
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
                ArrayAdapter adapter = new ArrayAdapter<String>(GiveRewardsActivity.this, R.layout.list_item, childNames);
                rewardsSpinner.setAdapter(adapter);

                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        rewardsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                giveTo = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void getDataFromActivity(){
        linkCode = ParentMain.linkCode;
    }
}