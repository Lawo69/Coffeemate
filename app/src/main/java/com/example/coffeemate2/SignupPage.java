package com.example.coffeemate2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class SignupPage extends AppCompatActivity {
    //Views
    EditText mEmail, mPassword, mFullname, mUsername;
    Button mSignupBtn;
    LinearLayout mlogin;

    //progressbar to display while registering user
    ProgressDialog progressDialog;

    //declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);
        Objects.requireNonNull(getSupportActionBar()).hide(); //This line hide the Action bar

        //init
        mEmail = findViewById(R.id.emailEt);
        mPassword = findViewById(R.id.passwordEt);
        mFullname = findViewById(R.id.fullnameEt);
        mUsername = findViewById(R.id.usernameEt);
        mSignupBtn = findViewById(R.id.signupButton);
        mlogin = findViewById(R.id.login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");

        //handle signup btn click
        mSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //input email, password, firstname, username
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String fullname = mFullname.getText().toString().trim();
                String username = mUsername.getText().toString().trim();

                //check if user fill all the fields before sending data to firebase
                if (email.isEmpty() || password.isEmpty() || fullname.isEmpty() || username.isEmpty()) {
                    Toast.makeText(SignupPage.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
                //store data in firebase
                else {
                    //validate
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        //set error and focuses to email edittext
                        mEmail.setError("Invalid Email");
                        mEmail.setFocusable(true);
                    } else if (password.length() < 6) {
                        //set error and focuses to password edittext
                        mPassword.setError("Password length at least 6 characters");
                        mPassword.setFocusable(true);
                    } else {
                        //show progress dialog
                        progressDialog.show();
                        registerUser(email, password, fullname, username); //register the user
                    }
                }

            }
        });
        //hadle login textview click listner
        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupPage.this, LoginPage.class));
                finish();
            }
        });
    }

    private void registerUser(String email, String password, String fullname, String username) {
        //email and password pattern is valid, start registering user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            //get user email and uid from auth
                            String email = user.getEmail();
                            String uid = user.getUid();

                            //when user is registered store user info in firebase realtime database
                            //using HashMap
                            HashMap<Object, String> hashMap = new HashMap<>();

                            //put info in hashmap
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("fullname", fullname);
                            hashMap.put("username", username);
                            hashMap.put("profileimage","");
                            hashMap.put("bio", "");
                            hashMap.put("dateofbirth", "");
                            hashMap.put("hometown", "");
                            hashMap.put("gender", "");
                            hashMap.put("onlineStatus", "online");
                            hashMap.put("idcard", "");
                            hashMap.put("job", "");
                            hashMap.put("agreewithTearmsnCondition", "");

                            //create instance of firebase database and save user info
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            //path to store user data named "Users"
                            DatabaseReference reference = database.getReference("Users");
                            //put data within hashmap in database
                            reference.child(uid).setValue(hashMap);
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(SignupPage.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(SignupPage.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignupPage.this, PersonalDetailsPage.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //error, get and show message
                        progressDialog.dismiss();
                        Toast.makeText(SignupPage.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
