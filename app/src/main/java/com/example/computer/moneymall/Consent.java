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

public class Consent extends AppCompatActivity {
    private Button mcontinue;
    private FirebaseAuth mAuth;
    private DatabaseReference mRoot;
    FirebaseDatabase database;
    private EditText msign;
    private String CurrentUserId;

    String UserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent);
        msign = (EditText)findViewById(R.id.edit4);
        mcontinue = (Button)findViewById(R.id.button10);
        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        mRoot = FirebaseDatabase.getInstance().getReference("User").child(CurrentUserId);
        database = FirebaseDatabase.getInstance();
        UserId= mAuth.getCurrentUser().getUid();
        mcontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String setusersign = msign.getText().toString();
                if (TextUtils.isEmpty(setusersign)){
                    Toast.makeText(Consent.this,"Please write your sign...",Toast.LENGTH_SHORT).show();
                }
                else {
                    Map<String,Object> profilemap =new HashMap<>();
                    profilemap.put("signature",setusersign);
                    mRoot.child("Sign").setValue(profilemap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                SendUserToActivity();
                                Toast.makeText(Consent.this, "Profile Updated Successfully..", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(Consent.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
            }

    private void SendUserToActivity() {
        Intent intent = new Intent(Consent.this,ImageActivity.class);
        startActivity(intent);
    }

}
