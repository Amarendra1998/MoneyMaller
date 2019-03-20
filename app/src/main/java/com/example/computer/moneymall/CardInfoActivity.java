package com.example.computer.moneymall;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CardInfoActivity extends AppCompatActivity {
ImageView imageView;
TextView textView;
Button button;
Toolbar toolbar;
    String retriveid;
    private DatabaseReference UserRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_info);
        imageView = (ImageView)findViewById(R.id.imageView3);
        textView = (TextView)findViewById(R.id.textView12);
        button = (Button)findViewById(R.id.button12);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Back");
        UserRef = FirebaseDatabase.getInstance().getReference().child("Product");
        retriveid = getIntent().getExtras().get("visit_user_id").toString();
        retrieveinfo();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardInfoActivity.this,FinalDetail.class);
                startActivity(intent);
            }
        });
    }
    private void retrieveinfo() {
        UserRef.child(retriveid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists())&&(dataSnapshot.hasChild("image")))
                {
                    String userImage = dataSnapshot.child("image").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    Picasso.get().load(userImage).placeholder(R.drawable.ic_person_black_24dp).into(imageView);
                    textView.setText(userName);
                }else
                {
                    String userName = dataSnapshot.child("name").getValue().toString();

                    textView.setText(userName);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
