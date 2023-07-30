package com.example.coffeemate2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileEditFragment extends Fragment {

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //views from xml
    CircleImageView mProfileImage;
    ActivityResultLauncher<String> mGetContent;
    StorageReference imagePath;

    EditText mFullName,mUser,mBio,mDob,mHometown,mJob;
    Button mDone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        actionbarCustom();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        imagePath = FirebaseStorage.getInstance().getReference().child("ProfileImage");


        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Users").child(user.getUid());

        //init views
        mProfileImage = view.findViewById(R.id.profileImage);
        mDone = view.findViewById(R.id.buttonDone);
        mFullName = view.findViewById(R.id.fullnameEt);
        mUser = view.findViewById(R.id.usernameEt);
        mBio = view.findViewById(R.id.bio);
        mDob = view.findViewById(R.id.dateOfBirth);
        mHometown = view.findViewById(R.id.hometown);
        mJob = view.findViewById(R.id.job);

        //profile image picker
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                Intent intent = new Intent(getActivity(),CropperActivity.class);
                intent.putExtra("DATA",result.toString());
                startActivityForResult(intent,101);
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("fullname").getValue(String.class);
                    String user = dataSnapshot.child("username").getValue(String.class);
                    String bio = dataSnapshot.child("bio").getValue(String.class);
                    String dof = dataSnapshot.child("dateofbirth").getValue(String.class);
                    String hometown = dataSnapshot.child("hometown").getValue(String.class);
                    String job = dataSnapshot.child("job").getValue(String.class);
                    String image = dataSnapshot.child("profileimage").getValue(String.class);

                    mFullName.setText(name);
                    mUser.setText(user);
                    mBio.setText(bio);
                    mDob.setText(dof);
                    mHometown.setText(hometown);
                    mJob.setText(job);

                    try {
                        Picasso.get().load(image).into(mProfileImage);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_p).into(mProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname = mFullName.getText().toString().trim();
                String user = mUser.getText().toString().trim();
                String bio = mBio.getText().toString().trim();
                String dof = mDob.getText().toString().trim();
                String hometown = mHometown.getText().toString().trim();
                String job = mJob.getText().toString().trim();

                // Replace this fragment with a blank fragment
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new ProfileFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                //store data in Firebase
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference bdata = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("fullname", fullname);
                hashMap.put("username", user);
                hashMap.put("bio", bio);
                hashMap.put("dateofbirth", dof);
                hashMap.put("hometown", hometown);
                hashMap.put("job", job);

                bdata.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),"Uploading fail...",Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });

        return view;
    }

    private void actionbarCustom() {

        // Get the activity's action bar
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        // Set the action bar's custom view
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.z_profileedit_actionbar);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == -1 && requestCode == 101){
            String result = data.getStringExtra("RESULT");
            Uri ImageData = null;
            if(result != null){
                ImageData = Uri.parse(result);
            }
            mProfileImage.setImageURI(ImageData);

            StorageReference Imagename = imagePath.child("image"+ImageData.getLastPathSegment());
            Imagename.putFile(ImageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference imagestore = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("profileimage",String.valueOf(uri));

                            imagestore.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getActivity(),"Image Upload Success",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(),"Uploading fail...",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            });
        }

    }
}