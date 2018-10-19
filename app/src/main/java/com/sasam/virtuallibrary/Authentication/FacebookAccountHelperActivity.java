package com.sasam.virtuallibrary.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sasam.virtuallibrary.R;

import static com.sasam.virtuallibrary.Util.LoggerTags.AUTHENTIC;

public class FacebookAccountHelperActivity extends AppCompatActivity {

    CallbackManager mCallbackManager;
    com.facebook.login.widget.LoginButton loginButton;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_facebook);
        mAuth = FirebaseAuth.getInstance();

        Log.d(AUTHENTIC,"enterd");

        mCallbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.button_facebook_login);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(AUTHENTIC,"onSuccess");
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                finish();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(FacebookAccountHelperActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        if (getIntent().hasExtra("click")) {
            if (getIntent().getBooleanExtra("click", false)) {
                loginButton.performClick();
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(AUTHENTIC, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(FacebookAccountHelperActivity.this, "Facebook account linked",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(user);
                            finish();
                        } else {
                            Toast.makeText(FacebookAccountHelperActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void unLinkAccount(View view) {
        mAuth.getCurrentUser().unlink(FacebookAuthProvider.PROVIDER_ID)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(FacebookAccountHelperActivity.this, "Facebook account unlinked",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(FacebookAccountHelperActivity.this, "unlink failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                        finish();
                    }
                });
    }

}
//TODO: hash key using password : android : tuxn5t8iQ3M3BlWozrx2r3kpOPg=
                                 //laptop pass : ga0RGNYHvNM5d0SLGQfpQWAPGJ8=
