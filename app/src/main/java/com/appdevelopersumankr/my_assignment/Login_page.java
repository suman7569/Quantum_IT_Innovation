package com.appdevelopersumankr.my_assignment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;


public class Login_page extends AppCompatActivity {
    private AppCompatButton signup_button,loginbutton;
    LoginButton login_with_facebook;
    private EditText emailbox,passwordbox;
    private FirebaseAuth mfirebaseAuth;
    private CallbackManager mcallbackManager;
    private static final String TAG="FacebookAuthentication";
    private FirebaseAuth.AuthStateListener authStateListener;
    private AccessTokenTracker accessTokenTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.page_login );

        findfunction();

        mfirebaseAuth=FirebaseAuth.getInstance ();
        FacebookSdk.sdkInitialize ( getApplicationContext () );



        mcallbackManager=CallbackManager.Factory.create ();
        login_with_facebook.registerCallback ( mcallbackManager, new FacebookCallback<LoginResult> () {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d ( TAG,"onSuccess"+loginResult );
                handlefacebooktoken(loginResult.getAccessToken ());
                Intent intent=new Intent (Login_page.this,Home_Screen.class);
                startActivity (intent);

            }

            @Override
            public void onCancel() {
                Log.d ( TAG,"oncancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d ( TAG,"onError"+error.toString ());
            }
        } );
        authStateListener=new FirebaseAuth.AuthStateListener () {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser ();
                if (user != null){
                    updateUI ( user );
                }else {
                    updateUI ( null );
                }
            }
        };
        accessTokenTracker=new AccessTokenTracker () {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken==null){
                    mfirebaseAuth.signOut ();
                }
            }
        };


        signup_button.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                startActivity ( new Intent (Login_page.this,Signup_Page.class) );
            }
        } );

        loginbutton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                String email=emailbox.getText ().toString ();
                String password=passwordbox.getText ().toString ();

                if (TextUtils.isEmpty ( email )){
                    emailbox.setError ( "Enter E-Mail");
                    return;
                }if (TextUtils.isEmpty ( password)){
                    passwordbox.setError ( "Enter Password" );
                }
                if (password.length ()<6){
                    passwordbox.setError ( "Password Length is Min 6 Digit");
                    return;
                }
                //auth process
                mfirebaseAuth.signInWithEmailAndPassword ( email,password).addOnCompleteListener ( new OnCompleteListener<AuthResult> () {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful ()){
                            Toast.makeText ( Login_page.this, "Logged In Successful", Toast.LENGTH_LONG ).show ();
                            startActivity ( new Intent (Login_page.this,Home_Screen.class) );
                        }
                        else {
                            Toast.makeText ( Login_page.this, "Error !"+task.getException ().getMessage ().toString (), Toast.LENGTH_LONG ).show ();
                        }

                    }
                } );
            }
        } );


//        forgot_password_option.setOnClickListener ( new View.OnClickListener () {
//            @Override
//            public void onClick(View v) {
//                EditText resetmail=new EditText ( v.getContext ());
//                AlertDialog.Builder passwordresetdialog = new AlertDialog.Builder ( v.getContext ());
//                passwordresetdialog.setTitle ( "Reset Password ");
//                passwordresetdialog.setMessage ( "Enter Your E-Mail ID to get Password Reset Link");
//                passwordresetdialog.setView ( resetmail);
//
//                passwordresetdialog.setPositiveButton ( "Yes", new DialogInterface.OnClickListener () {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                            String mail=resetmail.getText ().toString ();
//                            mfirebaseAuth.sendPasswordResetEmail ( mail).addOnSuccessListener ( new OnSuccessListener<Void> () {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    Toast.makeText ( Login_page.this, "Link Sent to Your Mail", Toast.LENGTH_SHORT ).show ();
//                                }
//                            } ).addOnFailureListener ( new OnFailureListener () {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText ( Login_page.this, "Try Again", Toast.LENGTH_SHORT ).show ();
//                                }
//                            } );
//                            passwordresetdialog.create ().show ();
//                    }
//                } );
//                passwordresetdialog.setNegativeButton ( "No", new DialogInterface.OnClickListener () {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                } );
//            }
//        } );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mcallbackManager.onActivityResult ( requestCode,resultCode,data);
        super.onActivityResult ( requestCode, resultCode, data );
    }

    private void handlefacebooktoken(AccessToken token) {
        Log.d ( TAG,"handlefacebooktoken"+token);
        AuthCredential credential= FacebookAuthProvider.getCredential ( token.getToken ());
        mfirebaseAuth.signInWithCredential ( credential ).addOnCompleteListener ( this, new OnCompleteListener<AuthResult> () {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful ()){
                    FirebaseUser user=mfirebaseAuth.getCurrentUser ();
                    updateUI(user);
                }else {
                    Toast.makeText ( Login_page.this, "Login Fail", Toast.LENGTH_LONG ).show ();
                    updateUI ( null );
                }
            }
        } );
    }

    private void updateUI(FirebaseUser user) {
        if (user!=null){
            String username=user.toString ();
            if (user.getPhotoUrl ()!=null){
                String photourl=user.getPhotoUrl ().toString ();
                photourl=photourl+"?type=large";
            }else {

            }
        }
    }

    private void findfunction() {
        signup_button=findViewById (R.id.createbuttonid);
        loginbutton=findViewById (R.id.loginbuttonid);
        emailbox=findViewById (R.id.emailboxid);
        passwordbox=findViewById (R.id.passwordboxid);
        login_with_facebook=findViewById ( R.id.facebookid);
        login_with_facebook.setReadPermissions ( "email","public_profile");
    }

    @Override
    protected void onStart() {
        super.onStart ();
        mfirebaseAuth.addAuthStateListener ( authStateListener );
    }

    @Override
    protected void onStop() {
        super.onStop ();
        if (authStateListener!=null){
            mfirebaseAuth.removeAuthStateListener ( authStateListener );
        }
    }

    public void forgotpasswordresetlink(View view) {
        EditText resetmail=new EditText ( view.getContext ());
        AlertDialog.Builder passwordresetdialog = new AlertDialog.Builder ( view.getContext ());
        passwordresetdialog.setTitle ( "Reset Password ");
        passwordresetdialog.setMessage ( "Enter Your E-Mail ID to get Password Reset Link");
        passwordresetdialog.setView ( resetmail);

        passwordresetdialog.setPositiveButton ( "Yes", new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mail=resetmail.getText ().toString ();
                mfirebaseAuth.sendPasswordResetEmail ( mail).addOnSuccessListener ( new OnSuccessListener<Void> () {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText ( Login_page.this, "Link Sent to Your Mail", Toast.LENGTH_LONG ).show ();
                    }
                } ).addOnFailureListener ( new OnFailureListener () {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText ( Login_page.this, "Try Again", Toast.LENGTH_LONG ).show ();
                    }
                } );
                passwordresetdialog.create ().show ();
            }
        } );
        passwordresetdialog.setNegativeButton ( "No", new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity ( new Intent (Login_page.this,Signup_Page.class) );
            }
        } );
    }
}