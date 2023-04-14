package com.example.familfunctions_team14;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.familfunctions_team14.databinding.ActivityMainBinding;

public class ParentMain extends AppCompatActivity {

    ActivityMainBinding parentBinding;

    public static String linkCode;
    public static String nameOfParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parentBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(parentBinding.getRoot());

        getDataFromActivity();
        loadDataToPost(linkCode);

        replaceFragment(new ParentHomeFragment());

        parentBinding.parentBottomNavigationView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.ParentHome:
                    replaceFragment(new ParentHomeFragment());
                    break;
                case R.id.ParentChoreList:
                    replaceFragment(new ParentChoresListFragment());
                    break;
                case R.id.ParentAdd:
                    startActivity(new Intent(ParentMain.this, PostActivity.class));
//                    replaceFragment(new ParentAddFragment());
                    break;
                case R.id.ParentRewards:
                    replaceFragment(new ParentRewardsFragment());
                    break;
                case R.id.ParentHistory:
                    replaceFragment(new ParentHistoryFragment());
                    break;
            }
            return true;
        });

        Intent toPostAct = new Intent (ParentMain.this, PostActivity.class);
        toPostAct.putExtra("FamilyCodeA2A", linkCode);
    }

    private void replaceFragment(Fragment fragment){

        // === LOAD DATA
        Bundle toFragment = new Bundle();
        toFragment.putString("FamilyCodeM2F", linkCode);
        fragment.setArguments(toFragment);


        FragmentManager parentFragmentManager = getSupportFragmentManager();
        FragmentTransaction parentFragmentTransaction = parentFragmentManager.beginTransaction();
        parentFragmentTransaction.replace(R.id.parentFrameLayout, fragment);
        parentFragmentTransaction.commit();
    }

    private void loadDataToPost(String code){
        Intent toPostAct = new Intent (ParentMain.this, PostActivity.class);
        toPostAct.putExtra("FamilyCodeA2A", linkCode);
    }

    private void getDataFromActivity(){
        linkCode = getIntent().getStringExtra("FamilyCodeA2A");
        nameOfParent = getIntent().getStringExtra("ParentsNameA2A");

    }

}