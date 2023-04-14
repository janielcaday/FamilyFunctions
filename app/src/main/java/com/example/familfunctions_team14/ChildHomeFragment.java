package com.example.familfunctions_team14;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChildHomeFragment#} factory method to
 * create an instance of this fragment.
 */
public class ChildHomeFragment extends Fragment {

    private TextView parentName;
    private Button logout;

    private TextView pointsTxt;

    private TextView dateTime;

    private TextView familyLinkCodeTxt;
    private String homeCodeData;

    private DatabaseReference reference;
    private FirebaseAuth mAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_child_home, container, false);

        mAuth = FirebaseAuth.getInstance();

        parentName = view.findViewById(R.id.childName);
        pointsTxt = view.findViewById(R.id.pointsTxt);
        dateTime = view.findViewById(R.id.dateTime);
        familyLinkCodeTxt = view.findViewById(R.id.familyLinkCode);
        logout = view.findViewById(R.id.logout);

        getDataFromMain();

        greetChild(homeCodeData);
        Calendar calender = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calender.getTime());
        dateTime.setText("Today is " + currentDate);
        showPoints(homeCodeData);
        familyLinkCodeTxt.setText("Family Code: " + homeCodeData);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                LoginActivity.parentStatus = false;
                RegisterActivity.parentStatus = false;
                Toast.makeText(getActivity(), "Logged out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), StartActivity.class));
            }
        });

        return view;
    }

    private void getDataFromMain(){
        Bundle receiver = this.getArguments();
        if(receiver != null){
            homeCodeData = receiver.getString("FamilyCodeM2F");
        }
    }

    private void greetChild(String homeCodeData){
        reference = FirebaseDatabase.getInstance().getReference("FamilyFunctions");
        reference.child(homeCodeData).child("Children").child(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().exists()){
                                DataSnapshot dataSnapshot = task.getResult();
                                String firstName = String.valueOf(dataSnapshot.child("firstname").getValue());
                                parentName.setText("Welcome, " + firstName);
                            }
                            else{
                                Toast.makeText(getActivity(), "Does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(getActivity(), "Failed to read!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showPoints(String homeCodeData) {
        reference = FirebaseDatabase.getInstance().getReference("FamilyFunctions");
        reference.child(homeCodeData).child("Children").child(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().exists()){
                                DataSnapshot dataSnapshot = task.getResult();
                                String points = String.valueOf(dataSnapshot.child("points").getValue());
                                pointsTxt.setText("Points: " + points);
                            }
                            else{
                                Toast.makeText(getActivity(), "Does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(getActivity(), "Failed to read!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}