package com.silentcodder.smartfarmingadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

public class RegisterActivity extends AppCompatActivity {

    CountryCodePicker mCpp;
    EditText mMobileNumber,mName;
    Button mBtnContinue;
    TextView mBtnLogin;

    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseFirestore = FirebaseFirestore.getInstance();

        findIDs();
        mCpp.registerCarrierNumberEditText(mMobileNumber);
        ProgressBar progressBar = findViewById(R.id.loader);
        mBtnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mName.getText().toString();
                if (TextUtils.isEmpty(mCpp.getFullNumberWithPlus())){
                    mMobileNumber.setError("Mobile Number");
                }else if (TextUtils.isEmpty(name)){
                    mName.setError("Name");
                }else {
                    mBtnContinue.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(RegisterActivity.this, RegisterOtpActivity.class);
                    intent.putExtra("MobileNumber",mCpp.getFullNumberWithPlus().replace(" ",""));
                    intent.putExtra("Name",name);
                    startActivity(intent);
                    finish();
                }
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });

    }

    private void findIDs() {
        mCpp = findViewById(R.id.cpp);
        mMobileNumber = findViewById(R.id.mobileNumber);
        mName = findViewById(R.id.name);
        mBtnContinue = findViewById(R.id.btnContinue);
        mBtnLogin = findViewById(R.id.btnLogin);
    }
}