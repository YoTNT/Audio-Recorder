package com.codepath.audiorecorder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

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
                String username=etSignUpName.getText().toString();
                String password1= etSignUpPassword.getText().toString();
                String password2= etConfirmPassword.getText().toString();
                if(!password1.equals(password2)){
                    Toast.makeText(SignUpActivity.this, "The password does not match!", Toast.LENGTH_LONG).show();
                }
                else{
                    signUp(username, password1);
                }
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

    private void signUp(String username, String password) {
        ParseUser user = new ParseUser();

        user.setUsername(username);
        user.setPassword(password);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Toast.makeText(SignUpActivity.this, "Sign Up Success", Toast.LENGTH_LONG).show();
                    gotoLoginActivity();
                }
                else{
                    Log.e(TAG, "Sign up failed!");
                    e.printStackTrace();
                    return;
                }
            }
        });
    }
}
