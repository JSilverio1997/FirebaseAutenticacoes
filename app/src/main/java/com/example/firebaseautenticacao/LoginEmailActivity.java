package com.example.firebaseautenticacao;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginEmailActivity extends AppCompatActivity implements View.OnClickListener  {

    private EditText edit_text_Email;
    private EditText edit_text_Senha;

    private Button button_Login;
    private Button button_recup_senha;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginemail);

        edit_text_Email = findViewById(R.id.editText_Email_Login);
        edit_text_Senha = findViewById(R.id.editText_Senha_Login);
        button_Login = findViewById(R.id.button_Ok_Login);
        button_recup_senha = findViewById(R.id.button_Recuperar_Senha);

        button_Login.setOnClickListener(this);
        button_recup_senha.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.button_Ok_Login:
                logar();
                break;

            case R.id.button_Recuperar_Senha:
                recuperar_senha();
                break;

        }
    }

    private void logar()
    {
        String email = edit_text_Email.getText().toString().trim();
        String senha = edit_text_Senha.getText().toString().trim();

        if(email.isEmpty())
        {
            Toast.makeText(getBaseContext(),"Por favor digite o seu e-mail."
                    ,Toast.LENGTH_LONG ).show();

            edit_text_Email.requestFocus();
        }
        else if(senha.isEmpty())
        {
            Toast.makeText(getBaseContext(), "Por favor digite a sua senha."
                    ,Toast.LENGTH_LONG).show();

            edit_text_Senha.requestFocus();
        }
        else
        {

            boolean status_rede = Utilidades.verificar_con_internet( this);
            if(status_rede)
            {
                logar_firebase(email, senha);
            }
            else
            {
                Toast.makeText(getBaseContext(), "Erro ao Cadastrar Usuário, " +
                                "por favor cheque o seu Wi-Fi ou a sua Rede Móvel",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
    private void logar_firebase(String email, String senha)
    {
        try
        {
            user = auth.getCurrentUser();

            auth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    boolean resposta = task.isSuccessful();

                    if(resposta)
                    {
                        if(user != null && user.isEmailVerified())
                        {

                            Toast.makeText(getBaseContext(),"Login Efetuado com Sucesso.",
                                    Toast.LENGTH_LONG).show();
                            startActivity( new Intent(getBaseContext(), PrincipalActivity.class));
                            finish();
                        }
                        else
                        {
                            Toast.makeText(getBaseContext(),"O seu E-mail ainda não foi confirmado.",
                                Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        String resposta_erro = task.getException().toString();
                        validar_erro(resposta_erro);

                    }
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(getBaseContext(), "Erro ao tentar realizar ação de Login " +
                    "com o Firebase.", Toast.LENGTH_LONG).show();
        }
    }

    private void validar_erro(String resposta_erro)
    {

        if(resposta_erro.contains("address is badly") ||
           resposta_erro.toLowerCase().contains("invalid_email"))
        {
            Toast.makeText(getBaseContext(),"O E-mail digitado é inválido.",
                    Toast.LENGTH_LONG).show();

            edit_text_Email.requestFocus();
        }
        else if(resposta_erro.contains("There is no user") ||
                resposta_erro.toLowerCase().contains("email_not_found"))
        {
            Toast.makeText(getBaseContext(), " Este E-mail não existe cadastrado.",
                    Toast.LENGTH_LONG).show();

            edit_text_Email.setText(null);
            edit_text_Email.requestFocus();
        }
        else if(resposta_erro.contains("least 6 characters"))
        {
            Toast.makeText(getBaseContext(),"Por favor digite a senha com pelo " +
                    "menos 6 caracteres.", Toast.LENGTH_LONG).show();

            edit_text_Senha.requestFocus();
        }
        else if(resposta_erro.contains("password is invalid"))
        {
            Toast.makeText(getBaseContext(),"A senha digitada é inválida."
                    , Toast.LENGTH_LONG).show();

            edit_text_Senha.requestFocus();
        }
        else if(resposta_erro.contains("interruped connection"))
        {
            Toast.makeText(getBaseContext(), "Erro: Sem conexão com o Firebase.",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getBaseContext(),"Erro ao efetuar o Login.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void recuperar_senha()
    {
       String email = edit_text_Email.getText().toString().trim();

       if(email.isEmpty())
       {
         Toast.makeText(getBaseContext(), "Por favor digite o seu e-mail.",
                Toast.LENGTH_LONG).show();
       }
       else
       {
         redifinir_senha(email);
       }
    }

    private void redifinir_senha(String email)
    {
        try
        {
           auth.sendPasswordResetEmail(email).addOnCompleteListener(this, new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        boolean resposta_firebase = task.isSuccessful();

                        if(resposta_firebase)
                        {
                            Toast.makeText(getBaseContext(), "Foi enviado uma mensagem  para" +
                                            " redifinição de senha para este endereço."
                                    ,Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            String resposta_erro = task.getException().toString();
                            validar_erro(resposta_erro);
                        }

                    }
                });
        }
        catch (Exception e)
        {
            Toast.makeText(getBaseContext(), "Erro ao tentar enviar o E-mail para" +
                    " o usuário.", Toast.LENGTH_LONG).show();
        }
    }
}
