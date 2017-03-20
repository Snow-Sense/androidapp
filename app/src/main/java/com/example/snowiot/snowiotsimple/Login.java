//Code obtained from TVAC studio video tutorial on youtube on how to create user authentication for firebase

package com.example.snowiot.snowiotsimple;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    //to check if user is already or still logged in
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText mEmailField;
    private EditText mPasswordField;

    private Button mLoginButton;

    //define firebase auth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailField = (EditText) findViewById(R.id.emailField);
        mPasswordField = (EditText) findViewById(R.id.passwordField);

        mLoginButton = (Button) findViewById(R.id.loginButton);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            if(firebaseAuth.getCurrentUser() != null) {             //If getCurrentUser == null, user has not logged in. if != null, user has logged in.

                startActivity(new Intent(Login.this, MainActivity.class)); //if user is logged in, start new intent from Login activity to MainActivity.
                finish();                                                  //end activity
            }

            }
        };

        //as told by firebase docs
        mAuth = FirebaseAuth.getInstance();

        //Event listener for login button
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                startSignin();
            }
        });

    }

    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }





//Custom function for signing
private void startSignin(){
    String email = mEmailField.getText().toString();
    String password = mPasswordField.getText().toString();

    //if email and password fields are empty, then solve without a bug
    if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
    {
        Toast.makeText(Login.this, "Information fields are blank.", Toast.LENGTH_LONG).show();
    }

    else {


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()) {
                    Toast.makeText(Login.this, "Invalid username or password.", Toast.LENGTH_LONG).show();

                }

            }
        });
    }
}

}
