package com.example.coffeemate2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class LoginPage extends AppCompatActivity {

    //views
    EditText mEmail, mPassword;
    TextView mForgetPass;
    LinearLayout mSignup;
    Button mLoginBtn;

    //declare an instance of firebaseAuth
    private FirebaseAuth mAuth;

    //progress dialog
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        Objects.requireNonNull(getSupportActionBar()).hide(); //This line hide the Action bar

        //in the onCreate() method, initialize the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        //init
        mEmail = findViewById(R.id.emailEt);
        mPassword = findViewById(R.id.passwordEt);
        mSignup = findViewById(R.id.signup);
        mLoginBtn = findViewById(R.id.loginButton);
        mForgetPass = findViewById(R.id.forgetpass);

        //login button click
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //input data
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                if(!email.isEmpty() || !password.isEmpty()) {
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        //invalid email patterns set error
                        mEmail.setError("Invalid Email");
                        mEmail.setFocusable(true);
                    } else {
                        //valid email pattern
                        loginUser(email, password);
                    }
                }else{
                    Toast.makeText(LoginPage.this,"Enter email and password",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //new to coffeemate textview click
        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginPage.this,SignupPage.class));
                finish();
            }
        });
        //recover pass textview click
        mForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForgetPasswordDialog();
            }
        });

        //init progress dialog
        pd = new ProgressDialog(this);
    }

    private void showForgetPasswordDialog() {
        //Alertdialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");
        builder.setMessage("Please enter your email address. You will receive a link to create a new password via email");

        //set layout linear layout
        LinearLayout linearLayout = new LinearLayout(this);
        //views to set in dialog
        EditText emailEt = new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        emailEt.setMinEms(16);

        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);

        //button forget password
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input email
                String email = emailEt.getText().toString().trim();
                beginRecovery(email);
            }
        });
        //buttons cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dismiss dialog
                dialog.dismiss();
            }
        });
        //show dialog
        builder.create().show();
    }

    private void beginRecovery(String email) {
        //show progress dialog
        pd.setMessage("Sending email...");
        pd.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(LoginPage.this,"Email sent",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(LoginPage.this,"Failed",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                //get and show proper error message
                Toast.makeText(LoginPage.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser(String email, String password) {
        //show progress dialog
        pd.setMessage("Logging In...");
        pd.show();
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //dismiss progress dialog
                            pd.dismiss();
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();

                            //if user is signing in first time then get and show user info from google account
                            if(task.getResult().getAdditionalUserInfo().isNewUser()){
                                //get user email and uid from auth
                                String email = user.getEmail();
                                String uid = user.getUid();
                                //When user is registered store user info in firebase realtime database too
                                //using HashMap
                                HashMap<Object, String> hashMap = new HashMap<>();
                                //put info in hashmap
                                hashMap.put("email",email);
                                hashMap.put("uid",uid);
                                hashMap.put("fullname","");
                                hashMap.put("username","");
                                hashMap.put("profileimage","");
                                hashMap.put("bio","");
                                hashMap.put("dateofbirth","");
                                hashMap.put("hometown","");
                                hashMap.put("gender","");
                                hashMap.put("onlineStatus", "online");
                                hashMap.put("idcard","");
                                hashMap.put("job","");
                                hashMap.put("agreewithTearmsnCondition","");
                                //firebase database istance
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                //path to store user data named "Users"
                                DatabaseReference reference = database.getReference("Users");
                                //put data within hashmap in database
                                reference.child(uid).setValue(hashMap);
                            }

                            //user is logged in, so start login page
                            startActivity(new Intent(LoginPage.this, DashboardPage.class));
                            finish();
                        } else {
                            //dismiss progress dialog
                            pd.dismiss();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginPage.this,"Authentication failed.",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //dismiss progress dialog
                        pd.dismiss();
                        //error, get and show error message
                        Toast.makeText(LoginPage.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
}