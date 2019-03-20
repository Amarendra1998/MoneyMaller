package com.example.computer.moneymall;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

public class LiabilityActivity extends AppCompatActivity {
CardView credit,personal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liability);

        credit = (CardView)findViewById(R.id.credit);
        personal =  (CardView)findViewById(R.id.prsonal);

        credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LiabilityActivity.this,Document.class);
                startActivity(intent);
            }
        });
        personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LiabilityActivity.this,Document.class);
                startActivity(intent);
            }
        });
    }
}
