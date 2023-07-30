package com.example.coffeemate2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    //firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;

    //views from xml
    private CircleImageView profileImage;
    private TextView profileName, homeTown, bioDecs, jobDecs, mEditProfile;

    private Intent intent;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        actionbarCustom();

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Users").child(user.getUid());

        //init views
        profileImage = view.findViewById(R.id.profileImage);
        profileName = view.findViewById(R.id.fullname);
        homeTown = view.findViewById(R.id.location);
        jobDecs = view.findViewById(R.id.job);
        bioDecs = view.findViewById(R.id.bio);
        mEditProfile = view.findViewById(R.id.editprofile);

        // Initialize the edit profile button and set a click listener
        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace this fragment with a profile edit fragment
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new ProfileEditFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("fullname").getValue(String.class);
                    String hometown = dataSnapshot.child("hometown").getValue(String.class);
                    String image = dataSnapshot.child("profileimage").getValue(String.class);
                    String bio = dataSnapshot.child("bio").getValue(String.class);
                    String job = dataSnapshot.child("job").getValue(String.class);

                    profileName.setText(name);
                    homeTown.setText(hometown);
                    bioDecs.setText(bio);
                    jobDecs.setText(job);

                    try {
                        Picasso.get().load(image).into(profileImage);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_p).into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void actionbarCustom() {

        // Get the activity's action bar
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        // Set the action bar's custom view
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.z_profile_actionbar);

        // Get the custom view's button and set its click listener
        ImageButton logoutButton = actionBar.getCustomView().findViewById(R.id.action_logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), LoginSignupPage.class));
                getActivity().finish();
            }
        });
    }
}
