package com.example.coffeemate2;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalDetailsPage extends AppCompatActivity {
    private static  final String TAG = "personalDetailsActivity";

    //view
    CircleImageView mProfileImage;
    ActivityResultLauncher<String> mGetContent;
    DatePickerDialog.OnDateSetListener mDateSetListener;
    StorageReference imagePath;
    StorageReference idPath;
    EditText mBio,mDob,mHometown,mJob;
    TextView noFile,teamsCondition;;
    RadioGroup mGender;
    RadioButton radioButton;
    CheckBox mAgreeCheck;

    Button mDoneBtn,mFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details_page);
        Objects.requireNonNull(getSupportActionBar()).hide(); //This line hide the Action bar

        imagePath = FirebaseStorage.getInstance().getReference().child("ProfileImage");

        //init views
        mProfileImage = findViewById(R.id.profileImage);
        mDoneBtn = findViewById(R.id.buttonDone);
        mBio = findViewById(R.id.bio);
        mDob = findViewById(R.id.dateOfBirth);
        mHometown = findViewById(R.id.hometown);
        mJob = findViewById(R.id.job);
        mGender = findViewById(R.id.radioGroup);
        mAgreeCheck = findViewById(R.id.teamsAgree);
        mFile = findViewById(R.id.file);
        noFile = findViewById(R.id.noFile);

        //birthday picker
        mDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        PersonalDetailsPage.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day
                );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month+1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                mDob.setText(date);
            }
        };

        //profile image picker
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });

        //profile image picker
        mFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noFile.setText("Upload");
            }
        });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                Intent intent = new Intent(PersonalDetailsPage.this,CropperActivity.class);
                intent.putExtra("DATA",result.toString());
                startActivityForResult(intent,101);
            }
        });

        //tearm and condition agreement
        mAgreeCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAgreeCheck.isChecked());
            }
        });

        teamsCondition = findViewById(R.id.teamsCondition);
        teamsCondition.setOnClickListener(view -> teamsCondition());

        mDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bio = mBio.getText().toString().trim();
                String dof = mDob.getText().toString().trim();
                String hometown = mHometown.getText().toString().trim();
                String job = mJob.getText().toString().trim();
                String agree = mAgreeCheck.getText().toString().trim();

                //gender check radio button
                int radioId = mGender.getCheckedRadioButtonId();
                radioButton = findViewById(radioId);
                final String gender = radioButton.getText().toString().trim();

                //check if user fill all the fields before sending data to firebase

                if (dof.isEmpty() || hometown.isEmpty() || gender.isEmpty() || agree.isEmpty()) {
                    Toast.makeText(PersonalDetailsPage.this, "Please fill Date of Birth and Home town", Toast.LENGTH_SHORT).show();
                }
                //store data in firebase
                else {

                    //store data in Firebase
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference bdata = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("bio", bio);
                    hashMap.put("dateofbirth", dof);
                    hashMap.put("hometown", hometown);
                    hashMap.put("job", job);
                    hashMap.put("idcard", "");
                    hashMap.put("gender", gender);
                    hashMap.put("agreewithTearmsnCondition", agree);

                    bdata.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(PersonalDetailsPage.this, "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PersonalDetailsPage.this, LoginPage.class));
                            finish();
                        }
                    });
                }

            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
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
                                    Toast.makeText(PersonalDetailsPage.this,"Image Upload Success",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            });
        }
    }
    //teams and condition button
    public  void teamsCondition() {
        Intent intent = new Intent(this, TearmConditonPage.class);
        startActivity(intent);
    }

    //gender check radio button
    public void checkButton(View view){
        int radioId = mGender.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        Toast.makeText(this,"Selected Gender: " + radioButton.getText(),Toast.LENGTH_SHORT).show();
    }

}