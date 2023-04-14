package com.example.familfunctions_team14;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChildChoresFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChildChoresFragment extends Fragment {

    private TextView screenTitle;
    private String homeCodeData;

    private String childName;

    RecyclerView recChores;
    ChoreAdapter choreAdapter;
    private List<Chores> mChores;

    private DatabaseReference reference;
    private FirebaseAuth mAuth;

    String firstName;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChildChoresFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChildChoresFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChildChoresFragment newInstance(String param1, String param2) {
        ChildChoresFragment fragment = new ChildChoresFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_child_chores, container, false);
        homeCodeData = ChildMain.linkCode;

        screenTitle = view.findViewById(R.id.screenTitle);

        mAuth = FirebaseAuth.getInstance();

        getDataFromMain();
        getNameOfChild(homeCodeData);

        recChores = view.findViewById(R.id.child_recycler_view);
        recChores.setHasFixedSize(true);
        recChores.setLayoutManager(new LinearLayoutManager(getContext()));

        mChores = new ArrayList<Chores>();

        reference = FirebaseDatabase.getInstance().getReference().child("FamilyFunctions").child(ChildMain.linkCode).child("Chores");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Chores chore = postSnapshot.getValue(Chores.class);
                    String nameCheck = chore.getAssignedTo();
                    String statusCheck = chore.getStatus();
                    if((nameCheck.equals(firstName)) && (!statusCheck.equals("2") || !statusCheck.equals("3"))){
                        mChores.add(chore);
                    }
                }
                choreAdapter = new ChoreAdapter(getContext(), mChores);
                recChores.setAdapter(choreAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void getDataFromMain(){
        Bundle receiver = this.getArguments();
        if(receiver != null){
        }
    }

    private String getNameOfChild(String homeCodeData){
        reference = FirebaseDatabase.getInstance().getReference("FamilyFunctions")
                .child(homeCodeData).child("Children").child(mAuth.getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot postSnapshot : snapshot.getChildren()){
                            if(postSnapshot.getKey().equals("firstname")){
                                firstName = postSnapshot.getValue().toString();
                                screenTitle.setText("Hello, " + firstName + ". Here are your chores.");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return firstName;
    }
}