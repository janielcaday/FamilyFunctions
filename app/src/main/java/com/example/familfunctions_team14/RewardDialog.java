package com.example.familfunctions_team14;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RewardDialog extends DialogFragment {

    private TextView dClose;
    private EditText dTitle, dDescription, dPoints;
    private Button submitReward;

    private DatabaseReference mRootRef;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_reward_dialog, container, false);

        dTitle = view.findViewById(R.id.dTitle);
        dDescription = view.findViewById(R.id.dDescription);
        dPoints = view.findViewById(R.id.dPoints);
        submitReward = view.findViewById(R.id.submitReward);
        dClose = view.findViewById(R.id.dClose);


        mRootRef = FirebaseDatabase.getInstance().getReference();


        dClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dTitle.setText("");
                dDescription.setText("");
                dPoints.setText("");
                getDialog().dismiss();
            }
        });

        submitReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rewardId = mRootRef.child("FamilyFunctions").child(ParentMain.linkCode).child("Chores").push().getKey();

                Rewards reward = new Rewards();
                reward.setId(rewardId);
                reward.setTitle(dTitle.getText().toString());
                reward.setDescription(dDescription.getText().toString());
                reward.setPoints(dPoints.getText().toString());


                mRootRef.child("FamilyFunctions").child(ParentMain.linkCode).child("Rewards").child(rewardId).setValue(reward)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(),"Reward successfully added!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(),"Reward could not be added", Toast.LENGTH_SHORT).show();
                            }
                        });

                getDialog().dismiss();
            }
        });
        return view;
    }

}
