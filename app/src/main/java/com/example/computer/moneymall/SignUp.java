package com.example.computer.moneymall;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.computer.moneymall.Common.Common;
import com.example.computer.moneymall.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    private EditText edtpass,edtphone,edtname,edtEmail;
    private Button btnsignup;
    FirebaseAuth mauth;
    private DatabaseReference table_user;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        edtpass = (EditText)findViewById(R.id.editpass);
        edtphone = (EditText)findViewById(R.id.editphone);
        edtname = (EditText)findViewById(R.id.editname);
        edtEmail = (EditText)findViewById(R.id.editemail);
        btnsignup = (Button)findViewById(R.id.btnsignup);
        progressDialog = new ProgressDialog(this);
        mauth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
         table_user = database.getReference().child("User");
        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    createaccount();

                   /* final ProgressDialog progressDialog = new ProgressDialog(SignUp.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.show();
                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //check if already user phone is registered
                            if (dataSnapshot.child(edtphone.getText().toString()).exists()) {
                                progressDialog.dismiss();
                                Toast.makeText(SignUp.this, "Phone number already registered", Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                User user = new User(edtname.getText().toString(),edtpass.getText().toString(),edtEmail.getText().toString());
                                table_user.child(edtphone.getText().toString()).setValue(user);
                                Toast.makeText(SignUp.this, "Sign up successfully", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });*/
                }
                else
                {
                    Toast.makeText(SignUp.this,"Please check your internet connection",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
    private void createaccount() {
        String email = edtEmail.getText().toString();
        String password = edtpass.getText().toString();
        String mphone = edtphone.getText().toString();
        String mname = edtname.getText().toString();
        if (TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please provide email...",Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please provide password...",Toast.LENGTH_LONG).show();
        } if (TextUtils.isEmpty(mphone)){
            Toast.makeText(this,"Please provide phone number...",Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(mname)){
            Toast.makeText(this,"Please provide name...",Toast.LENGTH_LONG).show();
        }

        else {
            progressDialog.setTitle("Creating new account");
            progressDialog.setMessage("Please wait, process is under construction...");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();
            mauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        String currentId = mauth.getCurrentUser().getUid();
                        String email = edtEmail.getText().toString();
                        String password = edtpass.getText().toString();
                        String mphone = edtphone.getText().toString();
                        String mname = edtname.getText().toString();
                        Map<String,Object> profilemap =new HashMap<>();
                        profilemap.put("email",email);
                        profilemap.put("phone",password);
                        profilemap.put("name",mphone);
                        profilemap.put("password",mname);
                        table_user.child(currentId).setValue(profilemap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    SendUserToMainActivity();
                                    Toast.makeText(SignUp.this,"Sighed up successfully",Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                } else {
                                    String message = task.getException().toString();
                                    Toast.makeText(SignUp.this,"Error:"+message,Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }else {
                        String message = task.getException().toString();
                        Toast.makeText(SignUp.this,"Error:"+message,Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }
    private void SendUserToMainActivity() {
        Intent loginInt = new Intent(SignUp.this,CreditScore.class);
        loginInt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginInt);
        finish();
    }
}