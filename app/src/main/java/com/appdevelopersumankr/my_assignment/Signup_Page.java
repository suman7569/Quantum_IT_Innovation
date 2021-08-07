package com.appdevelopersumankr.my_assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup_Page extends AppCompatActivity {
    EditText mfullname,memail,mpassword,mphone;
    AppCompatButton register;
    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_signup__page );
        fstore=FirebaseFirestore.getInstance ();

        mfullname=findViewById ( R.id.nameid);
        memail=findViewById ( R.id.emailid);
        mpassword=findViewById ( R.id.passwordid);
        mphone=findViewById ( R.id.phoneid);
        register=findViewById ( R.id.registerbuttonid);
        fauth=FirebaseAuth.getInstance ();

        if (fauth.getCurrentUser () != null){
            startActivity ( new Intent (Signup_Page.this,Home_Screen.class) );
            finish ();
        }

        register.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                String fullname=mfullname.getText ().toString ();
                String email=memail.getText ().toString ().trim ();
                String password=mpassword.getText ().toString ().trim ();
                String phone=mphone.getText ().toString ().trim ();

                if (TextUtils.isEmpty ( fullname )){
                    mfullname.setError ( "Enter Your Name" );
                    return;
                }
                if (TextUtils.isEmpty ( email )){
                    memail.setError ( "Enter E-Mail");
                    return;
                }if (TextUtils.isEmpty ( password)){
                    mpassword.setError ( "Enter Password" );
                    return;
                }
                if (password.length ()<6){
                    mpassword.setError ( "Password Length is Min 6 Digit");
                    return;
                }
                fauth.createUserWithEmailAndPassword ( email,password).addOnCompleteListener ( new OnCompleteListener<AuthResult> () {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful ()){
                            Toast.makeText ( Signup_Page.this, "User Created ", Toast.LENGTH_LONG ).show ();
                            userID=fauth.getCurrentUser ().getUid ();
                            DocumentReference documentReference=fstore.collection ( "users").document (userID);
                            Map<String,Object> user=new HashMap<> ();
                            user.put ( "fname",fullname);
                            user.put ( "email",email);
                            user.put ( "phone",phone);
                            documentReference.set ( user).addOnSuccessListener ( new OnSuccessListener<Void> () {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            } );
                            startActivity ( new Intent (Signup_Page.this,Home_Screen.class) );
                        }else {
                            Toast.makeText ( Signup_Page.this, "Error !"+task.getException ().getMessage ().toString (), Toast.LENGTH_LONG ).show ();
                        }
                    }
                } );
            }
        } );
    }

    public void alreadyregistertextfield(View view) {
        startActivity ( new Intent (Signup_Page.this,Login_page.class) );
        finish ();
    }
}