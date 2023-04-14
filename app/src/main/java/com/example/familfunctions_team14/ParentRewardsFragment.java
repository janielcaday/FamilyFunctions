package com.example.familfunctions_team14;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ParentRewardsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParentRewardsFragment extends Fragment {

    private ListView listView;
    private Button addReward;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ParentRewardsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ParentRewardsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ParentRewardsFragment newInstance(String param1, String param2) {
        ParentRewardsFragment fragment = new ParentRewardsFragment();
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
        View view = inflater.inflate(R.layout.fragment_parent_rewards, container, false);

        listView = view.findViewById(R.id.rewards_list);
        addReward = view.findViewById(R.id.add_reward);

        ArrayList<String> points = new ArrayList<>();
        DatabaseReference referenceValue = FirebaseDatabase.getInstance().getReference().child("FamilyFunctions")
                .child(ParentMain.linkCode).child("Rewards");
        referenceValue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                points.clear();
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Rewards rewardItem = postSnapshot.getValue(Rewards.class);
                    String pointsItem = rewardItem.getPoints();
                    points.add(pointsItem);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        addReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        final ArrayList<String> list = new ArrayList<>();
        final ArrayAdapter adapter = new ArrayAdapter<String>(getContext(), R.layout.list_item, list);
        listView.setAdapter(adapter);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("FamilyFunctions")
                .child(ParentMain.linkCode).child("Rewards");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Rewards reward = postSnapshot.getValue(Rewards.class);
                    String txt = "Title: " + reward.getTitle() + "\nDescription: \n" + reward.getDescription() + "\n" + reward.getPoints() + " points";
                    list.add(txt);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pointsToUse = points.get(position);
                if(RegisterActivity.parentStatus || LoginActivity.parentStatus){
                    Intent toGiveRewards = new Intent(getContext(), GiveRewardsActivity.class);
                    toGiveRewards.putExtra("pointsToUse", pointsToUse);
                    startActivity(toGiveRewards);
                }
            }
        });




        return view;
    }

    private void openDialog() {
        RewardDialog dialog = new RewardDialog();
        dialog.show(getParentFragmentManager(), "RewardDialog");

    }
}