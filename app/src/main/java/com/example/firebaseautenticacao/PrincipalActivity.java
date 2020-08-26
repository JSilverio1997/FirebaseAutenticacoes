package com.example.firebaseautenticacao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class PrincipalActivity extends AppCompatActivity implements View.OnClickListener{

    private Button button_deslogar;
    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        button_deslogar = (Button) findViewById(R.id.button_Deslogar);
        button_deslogar.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.button_Deslogar:
                deslogar();
                break;
        }
    }

    private void deslogar()
    {
        try
        {
            auth.signOut();

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            googleSignInClient = GoogleSignIn.getClient(this, gso);
            googleSignInClient.signOut();

            finish();

            startActivity(new Intent(getBaseContext(), MainActivity.class));

        }
        catch (Exception e)
        {
            Toast.makeText(getBaseContext(), "Erro ao tentar Deslogar o Usuário.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
