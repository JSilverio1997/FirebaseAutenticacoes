package com.example.firebaseautenticacao;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.EditText;
import android.widget.Toast;

import static android.content.Context.CONNECTIVITY_SERVICE;


@SuppressLint("Registered")
 public class Utilidades {

   public static boolean verificar_con_internet(Context context)
    {
        try
        {
            ConnectivityManager conexao = (ConnectivityManager)
                    context.getSystemService(CONNECTIVITY_SERVICE);

            // Checagem de rede para dispositivos novos
            if(conexao != null)
            {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    NetworkCapabilities recursos_rede = conexao.getNetworkCapabilities(conexao.getActiveNetwork());

                    if(recursos_rede != null)
                    {
                        // Verificando se foi recuperado a rede do celular 3G ou 4G
                        if(recursos_rede.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                        {
                            // Retorna verdadeiro se o dispositivo possui 3G ou 4G
                            return true;
                        }
                        else if(recursos_rede.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                        {
                            // Retorna verdadeiro se o dispositivo possui Wi-FI
                            return true;
                        }
                        else
                        {
                            // Retorna falso se o dispositivo possuir uma rede inválida
                            return false;
                        }
                    }
                }
            }
            // Checagem de rede para dispositivos antigos
            else
            {
                NetworkInfo informacao = conexao.getActiveNetworkInfo();

             /* Verifica se existe uma conexão a rede seja ela de qualquer tipo e também
                verifica se o aplicativo realmente possui acesso a internet para fazer
                as suas operações
             */
                if(informacao != null && informacao.isConnected())
                {
                    // Retorna true se houver conexao a uma rede e automia para operar
                    return true;
                }
                else
                {
                 /*Retorna falso caso não esteja conectado a uma rede ou senão não puder
                 realizar as suas operações */
                    return false;
                }
            }
        }
        catch (Exception e)
        {
            Toast.makeText(context, "Erro ao tentar verificar se o aplicativo " +
                    "possuí acesso e conexão a uma rede.", Toast.LENGTH_LONG).show();
        }
        return false;
    }
}
