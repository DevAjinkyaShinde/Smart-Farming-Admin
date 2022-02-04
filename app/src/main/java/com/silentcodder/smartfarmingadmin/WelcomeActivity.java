package com.silentcodder.smartfarmingadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends AppCompatActivity {

    AlertDialog.Builder builder;
    ProgressBar progressBar;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        builder = new AlertDialog.Builder(this);

        progressBar = findViewById(R.id.loader);

        button = findViewById(R.id.btnStart);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        ConnectivityManager ConnectionManager=(ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=ConnectionManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()==true )
        {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user!=null){
                progressBar.setVisibility(View.VISIBLE);
                button.setVisibility(View.GONE);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                        finish();
                    }
                },3000);

            }

        }
        else
        {

            //Uncomment the below code to Set the message and title from the strings.xml file
            builder.setMessage("") .setTitle("No Internet");

            //Setting message manually and performing action on button click
            builder.setMessage("No internet connection !!")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            alert.show();

        }

    }
}