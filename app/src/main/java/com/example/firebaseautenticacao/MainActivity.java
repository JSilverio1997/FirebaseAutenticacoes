package com.example.firebaseautenticacao;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button button_Login_Google,  button_Login, button_Cadastrar, button_Login_Anonimo;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authStateListener;

    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_Login_Google = findViewById(R.id.buttton_Login_Google);
        button_Login = findViewById(R.id.buttton_Login);
        button_Cadastrar = findViewById(R.id.button_Cadastrar);
        button_Login_Anonimo = findViewById(R.id.button_Login_Anonimo);

        button_Login_Google.setOnClickListener(this);
        button_Login.setOnClickListener(this);
        button_Cadastrar.setOnClickListener(this);
        button_Login_Anonimo.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();

        servico_autenticacao();
        servicos_google();

    }

    //  Serviços de Autenticação
    private void servico_autenticacao()
    {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null && user.isEmailVerified())
                {
                    String usuario;
                    if (user.getEmail() == null)
                    {
                        usuario = "Anônimo";
                    }
                    else
                    {
                        usuario = user.getEmail();
                    }

                    Toast.makeText(getBaseContext(), "O Usuário "+ usuario
                            + " está Logado.", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private void servicos_google()
    {
        try
        {
            // Configure Google Sign In
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            googleSignInClient = GoogleSignIn.getClient(this, gso);
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Erro ao tentar utilizar os serviços do Google.",
                    Toast.LENGTH_LONG).show();
        }
    }

    // Método de Tratamento de Clicks
    @Override
    public void onClick(View view)
    {
       switch(view.getId())
          {
               case R.id.buttton_Login_Google:
                   sign_in_google();
                   break;

               case R.id.buttton_Login:
                   sign_in_email();
                   break;

              case R.id.button_Cadastrar:
                  startActivity(new Intent(this, CadastrarActivity.class));
                  break;

              case R.id.button_Login_Anonimo:
                  sign_in_login_anonimo();
                  break;
          }
    }

    // Métodos de Login

    private void sign_in_google()
    {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if(account == null)
        {
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, 555);
        }
        else
        {
            // Tem alguém conectado pelo o Google
            Toast.makeText(getBaseContext(), "O Usuário já está logado com a conta do Google.",
                    Toast.LENGTH_LONG).show();
            finish();
            startActivity(new Intent(getBaseContext(), PrincipalActivity.class));

        }
    }

    private void sign_in_email()
    {
        user =  auth.getCurrentUser();

        if(user != null && user.isEmailVerified())
        {
            finish();
            startActivity(new Intent(this, PrincipalActivity.class));
        }
        else
        {
            startActivity(new Intent(this, LoginEmailActivity.class));
        }
    }

    private void sign_in_login_anonimo()
    {
        try
        {
            adicionar_conta_anonima_firebase();
        }
        catch(Exception e)
        {
            Toast.makeText(getBaseContext(), "Erro ao tentar fazer o login Anônimo.",
                    Toast.LENGTH_LONG).show();
        }
    }

    // Autenticação com do Google com o Firebase
    private void adicionar_conta_google_firebase(GoogleSignInAccount acct)
    {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information
                            finish();
                            startActivity(new Intent(getBaseContext(), PrincipalActivity.class));

                        }
                        else
                        {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getBaseContext(), "Erro ao Logar com a conta do Google.",
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    private void adicionar_conta_anonima_firebase()
    {
        auth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information
                            finish();
                            startActivity(new Intent(getBaseContext(), PrincipalActivity.class));
                        }
                        else
                        {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getBaseContext(), "Erro ao Tentar Criar Conta Anônima.",
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    // Métodos da Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 555)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try
            {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                adicionar_conta_google_firebase(account);
            }
            catch (ApiException e)
            {
                Toast.makeText(getBaseContext(), "Erro ao Logar com a conta do Google.",
                        Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (authStateListener != null)
        {
            auth.removeAuthStateListener(authStateListener);
        }
    }
}
