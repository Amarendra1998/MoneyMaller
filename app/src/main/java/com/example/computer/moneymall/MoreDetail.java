package com.example.computer.moneymall;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MoreDetail extends AppCompatActivity {
    private Button mcontinue,mback;
    private FirebaseAuth mAuth;
    private DatabaseReference mRoot;
    FirebaseDatabase database;
    private EditText memail,mstatus;
    String UserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_detail);
        mstatus = (EditText)findViewById(R.id.edit4);
        memail = (EditText)findViewById(R.id.editText3);
        mcontinue = (Button)findViewById(R.id.button10);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        UserId= mAuth.getCurrentUser().getUid();
        mRoot = FirebaseDatabase.getInstance().getReference("User").child(UserId);
        mback = (Button)findViewById(R.id.button11);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreDetail.this,CreditScoreActivity.class);
                startActivity(intent);
            }
        });
        mcontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String setuseremail = memail.getText().toString();
                String setstatus = mstatus.getText().toString();
                if (TextUtils.isEmpty(setuseremail)){
                    Toast.makeText(MoreDetail.this,"Please write your email...",Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(setstatus)){
                    Toast.makeText(MoreDetail.this,"Please enter your status...",Toast.LENGTH_SHORT).show();
                }
                else {
                    Map<String,Object> profilemap =new HashMap<>();
                    profilemap.put("email",setuseremail);
                    profilemap.put("status",setstatus);
                    mRoot.child("more").setValue(profilemap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                SendUserToActivity();
                                Toast.makeText(MoreDetail.this, "Profile Updated Successfully..", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(MoreDetail.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void SendUserToActivity() {
        Intent intent = new Intent(MoreDetail.this,LiabilityActivity.class);
        startActivity(intent);
    }
}
