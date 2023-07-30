package com.example.coffeemate2;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.coffeemate2.databinding.ActivityDashboardPageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class DashboardPage extends AppCompatActivity {

    // firebase auth
    FirebaseAuth firebaseAuth;

    ActionBar actionBar;
    ActivityDashboardPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_page);

        // init
        firebaseAuth = FirebaseAuth.getInstance();

        binding = ActivityDashboardPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // set the "Home" item as the active item in the navigation bar
        binding.navigationbar.setSelectedItemId(R.id.nav_home);

        // load the HomeFragment
        replaceFragment(new HomeFragment());

        // listen for navigation bar item selection
        binding.navigationbar.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.nav_friend:
                    replaceFragment(new FriendFragment());
                    break;
                case R.id.nav_notfication:
                    replaceFragment(new NotficationFragment());

                    break;
                case R.id.nav_chat:
                    replaceFragment(new ChatListFragment());
                    break;
                case R.id.nav_profile:
                    replaceFragment(new ProfileFragment());
                    break;
            }
            return true;
        });

        checkUserStatus();

    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    private void checkUserStatus(){
        // get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            // user is signed in stay here

        } else {
            // user not signed in, go to signup page
            startActivity(new Intent(DashboardPage.this, LoginSignupPage.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        // check on start of app
        checkUserStatus();
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        // Do nothing, or add code to handle what happens when the back button is pressed on the dashboard
    }
}
