package com.appdevelopersumankr.my_assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class Home_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_home__screen );
    }

    public void logoutfunction(View view) {
        FirebaseAuth.getInstance ().signOut ();
        startActivity ( new Intent (getApplicationContext (),Login_page.class) );
        finish ();
    }
}