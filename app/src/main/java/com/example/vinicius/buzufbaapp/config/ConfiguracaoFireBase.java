package com.example.vinicius.buzufbaapp.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class ConfiguracaoFireBase {

    private static DatabaseReference referenceFireBase ;
    private static FirebaseAuth firebaseAuth;

    public static DatabaseReference getFireBase(){

        if (referenceFireBase == null){

            referenceFireBase = FirebaseDatabase.getInstance().getReference("teste");

        }

        return referenceFireBase;
    }

    public static FirebaseAuth getFirebaseAuth(){

        if (firebaseAuth == null){

            firebaseAuth = FirebaseAuth.getInstance();

        }

        return firebaseAuth;
    }
}
