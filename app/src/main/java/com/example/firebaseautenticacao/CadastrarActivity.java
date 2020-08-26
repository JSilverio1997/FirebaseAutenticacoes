package com.example.firebaseautenticacao;

import android.annotation.SuppressLint;
import android.content.Context;
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

@SuppressLint("Registered")
public class CadastrarActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editText_Email;
    private EditText editText_Senha;
    private EditText editText_Confirmar_Senha;

    private Button button_Cadastrar_Usuario;
    private Button button_Cancelar;

    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        editText_Email = findViewById(R.id.editText_Email);
        editText_Senha = findViewById(R.id.editText_Senha);
        editText_Confirmar_Senha = findViewById(R.id.editText_Confirmar_Senha);

        button_Cadastrar_Usuario = findViewById(R.id.button_Cadastrar_Usuario);
        button_Cancelar = findViewById(R.id.button_Cancelar);

        button_Cadastrar_Usuario.setOnClickListener(this);
        button_Cancelar.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v)
    {
       switch(v.getId())
       {
           case R.id.button_Cadastrar_Usuario:
               cadastrar();
               break;

           case R.id.button_Cancelar:
               break;

       }
    }

    private void cadastrar()
    {
              String email = editText_Email.getText().toString().trim();
              String senha  = editText_Senha.getText().toString().trim();
              String confirmar_senha = editText_Confirmar_Senha.getText().toString().trim();

              if(email.isEmpty())
              {
                  Toast.makeText(getBaseContext(), "Por favor digite o seu e-mail.",
                      Toast.LENGTH_LONG).show();

                  editText_Email.requestFocus();
              }
              else if(senha.isEmpty())
              {
                  Toast.makeText(getBaseContext(),"Por favor digite a sua senha.",
                          Toast.LENGTH_LONG).show();

                  editText_Senha.requestFocus();
              }
              else if(confirmar_senha.isEmpty())
              {
                  Toast.makeText(getBaseContext(),"Por favor preencha o campo repitir senha.",
                          Toast.LENGTH_LONG).show();

                  editText_Confirmar_Senha.requestFocus();
              }
              else
              {
                  if (!senha.contentEquals(confirmar_senha))
                  {
                      Toast.makeText(getBaseContext(), "As senhas não se coincidem," +
                              " por favor digite novamente.", Toast.LENGTH_LONG).show();

                      editText_Senha.requestFocus();
                  }
                  else
                  {
                       boolean conexao_internet = Utilidades.verificar_con_internet( this);

                       if(conexao_internet)
                       {
                        criar_usuario_firebase(email, senha);
                       }
                       else
                       {
                           Toast.makeText(getBaseContext(), "Erro ao Cadastrar Usuário, " +
                                   "por favor cheque o seu Wi-Fi ou a sua Rede Móvel. ",
                                   Toast.LENGTH_LONG).show();
                       }
                  }
              }

    }

    private void criar_usuario_firebase(String email,  String senha)
    {
        try
        {
            auth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        boolean resposta = task.isSuccessful();

                        if(resposta)
                        {
                            user = auth.getCurrentUser();

                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(getBaseContext(), "Foi enviado um e-mail de confirmação para está conta.",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                            else
                                            {
                                                Toast.makeText(getBaseContext(), "Erro ao enviar e-mail para está conta.",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                            /*Toast.makeText(getBaseContext(),"Cadastro Efetuado com Sucesso.",
                                    Toast.LENGTH_LONG).show();*/
                        }
                        else
                        {
                            String resposta_erro = task.getException().toString();
                            validar_erro(resposta_erro);

                        }
                    }
                });

        }
        catch(Exception e)
        {
            Toast.makeText(getBaseContext(),"Erro ao tentar enviar e-mail ou criar o cadastro do usuário. " + e,
                    Toast.LENGTH_LONG).show();
        }
        
        

    }

    private void validar_erro(String resposta_erro)
    {
        if(resposta_erro.contains("address is badly"))
        {
            Toast.makeText(getBaseContext(),"O E-mail digitado é inválido.",
                    Toast.LENGTH_LONG).show();

            editText_Email.requestFocus();
        }
        else if(resposta_erro.contains("address is already in use"))
        {
            Toast.makeText(getBaseContext(), " Este E-mail já existe cadastrado.",
                    Toast.LENGTH_LONG).show();

            editText_Email.setText(null);
            editText_Email.requestFocus();
        }
        else if(resposta_erro.contains("least 6 characters"))
        {
            Toast.makeText(getBaseContext(),"Por favor digite a senha com pelo " +
                    "menos 6 caracteres.", Toast.LENGTH_LONG).show();

            editText_Senha.requestFocus();
        }
        else if(resposta_erro.contains("interruped connection"))
        {
            Toast.makeText(getBaseContext(), "Erro: Sem conexão com o Firebase.",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getBaseContext(), "Erro ao tentar Cadastrar.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
