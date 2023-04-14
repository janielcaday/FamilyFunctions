package com.example.familfunctions_team14;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

import com.example.familfunctions_team14.databinding.ActivityChildMainBinding;

public class ChildMain extends AppCompatActivity {

    private ActivityChildMainBinding childMainBinding;

    public static String linkCode;
    public static String nameOfChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_main);


        childMainBinding = ActivityChildMainBinding.inflate(getLayoutInflater());
        setContentView(childMainBinding.getRoot());

        getDataFromActivity();

        replaceFragment(new ChildHomeFragment());

        childMainBinding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.ChildHome:
                    replaceFragment(new ChildHomeFragment());
                    break;
                case R.id.ChildChores:
                    replaceFragment(new ChildChoresFragment());
                    break;
                case R.id.ChildPrizes:
                    replaceFragment(new ChildPrizesFragment());
                    break;
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        Bundle toFragment = new Bundle();
        toFragment.putString("FamilyCodeM2F", linkCode);
        fragment.setArguments(toFragment);

        FragmentManager parentFragmentManager = getSupportFragmentManager();
        FragmentTransaction parentFragmentTransaction = parentFragmentManager.beginTransaction();
        parentFragmentTransaction.replace(R.id.childFrameLayout, fragment);
        parentFragmentTransaction.commit();
    }

    private void getDataFromActivity(){
        linkCode = getIntent().getStringExtra("FamilyCodeA2A");
        nameOfChild = getIntent().getStringExtra("ChildsNameA2A");
    }
}