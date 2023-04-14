package com.example.familfunctions_team14;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button login;
    private TextView registerUser;

    private CheckBox parent_toggle;
    public static boolean parentStatus;
    private EditText familyCode;

    private String childsName;
    private String parentsName;

    FirebaseAuth auth;
    DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        parent_toggle = findViewById(R.id.parent_toggle);
        familyCode = findViewById(R.id.familyCode);
        registerUser = findViewById(R.id.registerUser);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        childsName = "";
        parentsName = "";

        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                String txt_code = familyCode.getText().toString();

                loginUser(txt_email, txt_password, txt_code);


            }
        });

        parent_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(parent_toggle.isChecked()){
                    parentStatus = true;
                }
                else{
                    parentStatus = false;
                }
            }
        });
    }

    private void loginUser(String email, String password, String code) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                if(parentStatus == true){
                    Intent toParentMain = new Intent(LoginActivity.this, ParentMain.class);
                    toParentMain.putExtra("FamilyCodeA2A", code);
                    getFirstNameParent(code);
                    toParentMain.putExtra("ParentsNameA2A", parentsName);
                    //toParentMain.putExtra("PersonNameA2A")
                    startActivity(toParentMain);
                    finish();
                }
                else{
                    Intent toChildMain = new Intent(LoginActivity.this, ChildMain.class);
                    //toChildMain.putExtra("ChildsNameA2A", getFirstNameChild(email, code));
                    toChildMain.putExtra("FamilyCodeA2A", code);
                    getFirstNameChild(code);
                    toChildMain.putExtra("ChildsNameA2A", childsName);
                    startActivity(toChildMain);
                    finish();
                }

            }
        });
    }

    private void getFirstNameChild(String code){
        mDatabaseRef.child("FamilyFunctions").child(code).child("Children").child(auth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            DataSnapshot dataSnapshot = task.getResult();
                            String firstNameChild = String.valueOf(dataSnapshot.child("firstname").getValue());
                            childsName = firstNameChild;
                        }
                    }
                });
    }

    private void getFirstNameParent(String code){
        mDatabaseRef.child("FamilyFunctions").child(code).child("Parents").child(auth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            DataSnapshot dataSnapshot = task.getResult();
                            String firstNameParent = String.valueOf(dataSnapshot.child("firstname").getValue());
                            parentsName = firstNameParent;
                        }
                    }
                });
    }

}