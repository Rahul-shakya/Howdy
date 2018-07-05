package com.example.asus.howdy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabse;
    private FirebaseUser mCurrentUser;

    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;

    private Button mStatusBtn;
    private Button mImageBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mDisplayImage=(CircleImageView)findViewById(R.id.settings_image);
        mName=(TextView)findViewById(R.id.settings_username);
        mStatus=(TextView)findViewById(R.id.settings_status);

        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        String current_uid=mCurrentUser.getUid();
        mUserDatabse= FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);




        mStatusBtn=(Button)findViewById(R.id.settings_status_btn);
        mImageBtn=(Button)findViewById(R.id.settings_image_btn);

        //ValueEventListener
        mUserDatabse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {                       //this function works add data or retrieve data

                String name=dataSnapshot.child("name").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String thumb_image=dataSnapshot.child("thumb_image").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);

              //  Picasso.get().load(image).into(mDisplayImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String status_value=mStatus.getText().toString();

                Intent status_intent=new Intent(SettingsActivity.this,StatusActivity.class);
                status_intent.putExtra("status_value",status_value);
                startActivity(status_intent);
            }
        });
    }
}
