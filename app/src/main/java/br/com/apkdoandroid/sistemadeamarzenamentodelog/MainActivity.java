package br.com.apkdoandroid.sistemadeamarzenamentodelog;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import br.com.apkdoandroid.sistemadeamarzenamentodelog.utils.LogUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
    public void salvarLogAcao(View view){
        /**
         * esse sistema de log  você pode adicionar em qualquer parte do código para ele armazena a informação em um text
         * e o fim do dia vai ter um relatiro de tudo que passo por ele.
         * Cada dia é um arquivo de log
         */
        boolean isLogSaved = LogUtils.saveLogWithPermissionCheck(MainActivity.this, "CLick no button");

        if (isLogSaved) {
            // A pasta e o arquivo foram criados com sucesso
            Log.d("LogUtils", "Log salvo com sucesso.");
        } else {
            // Houve um erro ao criar a pasta ou o arquivo
            Log.d("LogUtils", "Erro ao salvar o log.");
        }
    }
}

