package com.bankfinder.chathurangasandun.boatlocator;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterDevice extends AppCompatActivity {


    EditText etOwnerName;
    Button btSearchOwner;








    String ownerID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_device);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        CardView cvOwner = (CardView) findViewById(R.id.cvOwner);
        cvOwner.setRadius(5);

        initComponents();


    }

    private void initComponents() {
        etOwnerName = (EditText) findViewById(R.id.etSearchOwner);
        btSearchOwner = (Button) findViewById(R.id.btSearchOwner);

        btSearchOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ownerName = etOwnerName.getText().toString();
                String isHaveOwner = checkOwnerName(ownerName);

            }
        });




    }

    private String checkOwnerName(String ownerName) {



    }

}
