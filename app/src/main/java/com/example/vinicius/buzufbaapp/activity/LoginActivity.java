package com.example.vinicius.buzufbaapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vinicius.buzufbaapp.R;
import com.example.vinicius.buzufbaapp.config.ConfiguracaoFireBase;
import com.example.vinicius.buzufbaapp.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button btn_cadastrar;
    private Button btn_logar;
    private Usuario usuario;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        verificaLogado();

        email = (EditText) findViewById(R.id.edit_email);
        password = (EditText) findViewById(R.id.edit_password);
        btn_cadastrar = (Button) findViewById(R.id.btn_cadastrar);
        btn_logar = (Button) findViewById(R.id.btn_logar);

        btn_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
                startActivity(intent);
            }
        });

        btn_logar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                usuario = new Usuario();
                usuario.setEmail(email.getText().toString());
                usuario.setPassword(password.getText().toString());
                validarLogin();
            }
        });

    }

    private void validarLogin(){

        firebaseAuth = ConfiguracaoFireBase.getFirebaseAuth();
        firebaseAuth.signInWithEmailAndPassword(
          usuario.getEmail(),
          usuario.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    Toast.makeText(LoginActivity.this, "Usuário Logado", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(LoginActivity.this, "Erro ao logar no sistema", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void verificaLogado(){

        firebaseAuth = ConfiguracaoFireBase.getFirebaseAuth();

        if (firebaseAuth.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
            startActivity(intent);
        }

    }
}
