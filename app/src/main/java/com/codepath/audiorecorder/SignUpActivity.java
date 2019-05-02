package com.codepath.audiorecorder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignUpActivity extends AppCompatActivity {

    private EditText etSignUpName;
    private EditText etSignUpPassword;
    private EditText etConfirmPassword;
    private Button btnSignUp;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etSignUpName=findViewById(R.id.etSignUpName);
        etSignUpPassword=findViewById(R.id.etSignUpPassword);
        etConfirmPassword=findViewById(R.id.etConfirmPassword);
        btnSignUp=findViewById(R.id.btnSignUp);
        btnCancel=findViewById(R.id.btnCancel);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoLoginActivity();
            }
        });
    }

    private void gotoLoginActivity() {
        Intent i=new Intent(this,LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void signUp() {

    }
}