package com.example.firebaseautenticacao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.Toast;

public class AberturaActivity extends AppCompatActivity {

    private ProgressBar progressBar_Abertura;
    private Thread thread;
    private Handler handler;
    private  int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abertura);

        progressBar_Abertura = (ProgressBar)findViewById(R.id.progress_abertura);
        handler = (new Handler());
        thread = new Thread((Runnable) this);
        thread.start();

    }

    private void run()
    {
        i = 1;

        try
        {
            while(i <= 100)
            {
                Thread.sleep(50);
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        i++;
                        progressBar_Abertura.setProgress(i);
                    }
                });
            }

            finish();
            startActivity(new Intent(getBaseContext(), MainActivity.class));
        } catch (InterruptedException e) {
            Toast.makeText(getBaseContext(), "Erro ao carregar Progress Bar. "+ e,
                    Toast.LENGTH_LONG).show();
        }
    }
}
