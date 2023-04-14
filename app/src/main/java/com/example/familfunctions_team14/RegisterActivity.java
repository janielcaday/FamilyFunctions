package com.example.familfunctions_team14;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {


    private EditText firstname;
    private EditText lastname;
    private EditText email;
    private EditText password;
    private CheckBox parent_toggle;
    public static boolean parentStatus;
    private Button register;
    private TextView loginUser;
    public String code;
    private EditText familyCode;

    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        parent_toggle = findViewById(R.id.parent_toggle);
        register = findViewById(R.id.register);
        loginUser = findViewById(R.id.loginUser);
        familyCode = findViewById(R.id.familyCode);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        loginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        parent_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(parent_toggle.isChecked()){
                    parentStatus = true;
                    familyCode.setVisibility(View.INVISIBLE);
                }
                else{
                    parentStatus = false;
                    familyCode.setVisibility(View.VISIBLE);
                }
            }
        });




        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_firstname = firstname.getText().toString();
                String txt_lastname = lastname.getText().toString();
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                if(TextUtils.isEmpty(txt_firstname) || TextUtils.isEmpty(txt_lastname) ||
                        TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                    Toast.makeText(RegisterActivity.this, "Empty credentials", Toast.LENGTH_SHORT).show();
                } else if (txt_password.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Password too short!", Toast.LENGTH_SHORT).show();
                } else{
                    if(parentStatus == true){
                        code = generateRandomCode();
                        registerParentUser(txt_firstname, txt_lastname, txt_email, txt_password, code);
                    }
                    else{
                        code = familyCode.getText().toString();
                        registerChildUser(txt_firstname, txt_lastname, txt_email, txt_password, code);
                    }
                }

            }
        });
    }

    private void registerParentUser(String txt_firstname, String txt_lastname, String txt_email, String txt_password, String code){
        mAuth.createUserWithEmailAndPassword(txt_email, txt_password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                Parents parent = new Parents();

                parent.setEmail(txt_email);
                parent.setFirstname(txt_firstname);
                parent.setId(mAuth.getCurrentUser().getUid());
                parent.setLastname(txt_lastname);
                parent.setPassword(txt_password);

                mRootRef.child("FamilyFunctions").child(code).child("Parents").child(mAuth.getCurrentUser().getUid())
                        .setValue(parent).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Registration success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, ParentMain.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("FamilyCodeA2A", code);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void registerChildUser(String txt_firstname, String txt_lastname, String txt_email, String txt_password, String code){
        mAuth.createUserWithEmailAndPassword(txt_email, txt_password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                Children child = new Children();

                child.setEmail(txt_email);
                child.setFirstname(txt_firstname);
                child.setId(mAuth.getCurrentUser().getUid());
                child.setLastname(txt_lastname);
                child.setPassword(txt_password);
                child.setPoints("0");

                mRootRef.child("FamilyFunctions").child(code).child("Children").child(mAuth.getCurrentUser().getUid())
                        .setValue(child).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(RegisterActivity.this, "Registration success", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, ChildMain.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("FamilyCodeA2A", code);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static String generateRandomCode() {
        String candidateChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        int length = 6;
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(candidateChars.charAt(random.nextInt(candidateChars
                    .length())));
        }

        return sb.toString();
    }
}