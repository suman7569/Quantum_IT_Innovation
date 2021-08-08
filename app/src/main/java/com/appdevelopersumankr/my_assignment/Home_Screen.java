package com.appdevelopersumankr.my_assignment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Home_Screen extends AppCompatActivity {
    private TextView name,email,phone;
    private ImageView profileimage;
    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_home__screen );

        name=findViewById ( R.id.nameid);
        email=findViewById ( R.id.emailid);
        phone=findViewById ( R.id.phoneid);
        profileimage=findViewById ( R.id.imageviewid);

        fauth=FirebaseAuth.getInstance ();
        fstore=FirebaseFirestore.getInstance ();
        userid=fauth.getCurrentUser ().getUid ();

        DocumentReference documentReference=fstore.collection ( "users" ).document (userid);
        documentReference.addSnapshotListener ( this, new EventListener<DocumentSnapshot> () {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                //fname   email phone
                phone.setText ( value.getString ( "phone" ) );
                email.setText ( value.getString ( "email" ) );
                name.setText ( value.getString ( "fname" ) );

            }
        } );
    }

    public void logoutfunction(View view) {
        FirebaseAuth.getInstance ().signOut ();
        startActivity ( new Intent (getApplicationContext (),Login_page.class) );
        finish ();
    }
}