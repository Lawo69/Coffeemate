package com.example.coffeemate2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity {

    //firebase
    FirebaseAuth firebaseAuth;
    DatabaseReference userDbRef;

    //actionbar
    ActionBar actionBar;

    //views
    EditText mPost;
    Button mPublishBtn;

    //user info
    String name,email,uid,dp;

    //progress bar
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Add New Post");
        //enable back button in actionbar
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        pd = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        userDbRef = FirebaseDatabase.getInstance().getReference("Users");
        Query query = userDbRef.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    name = ""+ds.child("fullname").getValue();
                    email = ""+ds.child("email").getValue();
                    dp = ""+ds.child("profileimage").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //init views
        mPost = findViewById(R.id.pPost);
        mPublishBtn = findViewById(R.id.pPublishBtn);

        //publish button click listener
        mPublishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String post = mPost.getText().toString().trim();
                if(TextUtils.isEmpty(post)){
                    Toast.makeText(AddPostActivity.this, "Write a post....", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    uploadData(post);
                }
            }
        });
    }

    private void uploadData(String post) {
        pd.setMessage("Publishing post...");
        pd.show();
        String timeStamp = String.valueOf(System.currentTimeMillis());

        String filePathAndName = "Posts/"+"post_"+timeStamp;

        HashMap<Object,String>hashMap=new HashMap<>();
        //put post info
        hashMap.put("uid",uid);
        hashMap.put("uname",name);
        hashMap.put("udp",dp);
        hashMap.put("pid",timeStamp);
        hashMap.put("ppost",post);
        hashMap.put("ptime",timeStamp);

        //path to store post data
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        //put data in this ref
        ref.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //added in database
                pd.dismiss();
                Toast.makeText(AddPostActivity.this, "Post published", Toast.LENGTH_SHORT).show();
                mPost.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed adding post in database
                pd.dismiss();
                Toast.makeText(AddPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AddPostActivity.this,HomeFragment.class);
                startActivity(intent);
            }
        });


    }

    private void checkUserStatus(){
        // get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            // user is signed in stay here
            email = user.getEmail();
            uid = user.getUid();
        } else {
            // user not signed in, go to signup page
            startActivity(new Intent(this, SignupPage.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();//go to previous activity
        return super.onSupportNavigateUp();
    }
}