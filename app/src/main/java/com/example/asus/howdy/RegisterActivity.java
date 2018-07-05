package com.example.asus.howdy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateBtn;

    private FirebaseAuth mAuth;

    private Toolbar mToolbar;

    private ProgressDialog mRegProgress;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mDisplayName=(TextInputLayout)findViewById(R.id.reg_display_name);
        mEmail=(TextInputLayout)findViewById(R.id.reg_email);
        mPassword=(TextInputLayout)findViewById(R.id.reg_password);
        mCreateBtn=(Button)findViewById(R.id.reg_create_btn);

        mRegProgress=new ProgressDialog(this);

        mToolbar=(Toolbar)findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String display_name=mDisplayName.getEditText().getText().toString().trim();
                String email=mEmail.getEditText().getText().toString().trim();
                String password=mPassword.getEditText().getText().toString().trim();


                if( !TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {

                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Please wait while your account is created");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();

                    registerUser(display_name, email, password);

                }

            }
        });

    }

    private void registerUser(final String display_name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser current_user=FirebaseAuth.getInstance().getCurrentUser();
                    String uid=current_user.getUid();
                    mDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    HashMap<String,String> userMap=new HashMap<>();
                    userMap.put("name",display_name);
                    userMap.put("status","Yo Yo");
                    userMap.put("image","default");
                    userMap.put("thumb_image","default");
                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                mRegProgress.dismiss();
                                Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();

                            }
                        }
                    });
                    }
                    else
                {
                    mRegProgress.hide();
                    Toast.makeText(RegisterActivity.this,"Eror",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
