package br.com.apkdoandroid.sistemadeamarzenamentodelog.utils;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * O método saveLogToFile é responsável por salvar um log em um arquivo de texto.
 * Ele recebe um contexto do aplicativo e uma string contendo o log a ser salvo.
 * É verificado se a permissão de escrita no armazenamento externo está concedida. Se não estiver, é solicitado ao usuário.
 * A data atual é obtida no formato "yyyy-MM-dd".
 * É criado um nome de arquivo combinando a data atual e a extensão ".txt".
 * O diretório do armazenamento interno do aplicativo é obtido usando context.getFilesDir().
 * É criado um diretório chamado "Log" dentro do diretório do armazenamento interno, se ele ainda não existir.
 * Um arquivo de log é criado dentro do diretório "Log" usando o nome do arquivo gerado anteriormente.
 * O arquivo de log é aberto em modo de anexo ou criado como um novo arquivo se não existir.
 * É adicionada uma quebra de linha antes de adicionar o novo log.
 * O log é escrito no arquivo.
 * O arquivo é fechado.
 * Se ocorrer algum erro durante o processo, uma exceção será capturada e retornará false.
 * Caso contrário, true será retornado para indicar que o log foi salvo com sucesso.
 */

public class LogUtils {
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    public static Boolean saveLogWithPermissionCheck(Context context, String log) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // para android 11 e superior
            return saveLogForAndroid11AndAbove(context, log);
        } else { // para versão anterior do android 11
            // Verifica se a permissão de escrita no armazenamento externo está concedida
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Caso a permissão não tenha sido concedida, solicita ao usuário
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
                // Mostra um Toast informando ao usuário que a permissão é necessária
                // Toast.makeText(context, "Permissão de escrita é necessária para salvar o log.", Toast.LENGTH_LONG).show();
                return false;
            } else {
                // Se a permissão foi concedida, chama o método saveLogToFile
                return saveLogToFile(context, log);
            }
        }

    }

    private static boolean saveLogToFile(Context context, String log) {

        try {
            // Obtém a data atual
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // Obtém o diretório raiz do armazenamento externo
            File externalStorageDir = Environment.getExternalStorageDirectory();

            // Cria o diretório do aplicativo, se necessário
            File appDir = new File(externalStorageDir, getAppName(context) + "_Log");
            if (!appDir.exists()) {
                appDir.mkdirs();
            }

            // deletar os log antigo automaticamente 1 vez por mês
            ExclucaoDeLogs.deleteOldLogs(context, appDir.getAbsolutePath());

            // Cria o nome do arquivo de log com a data atual
            String fileName =  currentDate + ".txt";

            // Cria o arquivo de log
            File logFile = new File(appDir, fileName);

            // Cria o log com a data e horário atual
            //  String logWithTimestamp = getCurrentDateTime() + " - " + log;
            String logWithTimestamp = log;

            // Adiciona uma quebra de linha antes de adicionar o novo log
            String lineBreak = System.getProperty("line.separator");
            logWithTimestamp = lineBreak + logWithTimestamp;

            // Abre o arquivo de log em modo de anexo ou cria um novo arquivo se não existir
            FileOutputStream fos = new FileOutputStream(logFile, true);

            // Escreve o log no arquivo
            fos.write(logWithTimestamp.getBytes());
            fos.close();

            return true; // Log salvo com sucesso
        } catch (IOException e) {
            e.printStackTrace();
            return false; // Erro ao salvar o log
        }
    }

    private static Boolean saveLogForAndroid11AndAbove(Context context, String log) {
        try {
            // Obtém a data atual
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // Cria o nome do arquivo de log com a data atual
            String fileName =  currentDate + ".txt";



            // Cria o diretório do aplicativo, se necessário
            File appDir = new File(context.getExternalFilesDir(null), getAppName(context) + "_Log");
            if (!appDir.exists()) {
                appDir.mkdirs();
            }

            // deletar os log antigo automaticamente 1 vez por mês
            ExclucaoDeLogs.deleteOldLogs(context, appDir.getAbsolutePath());

            File file = new File(appDir, fileName);

            String logWithTimestamp = log;

            // Adiciona uma quebra de linha antes de adicionar o novo log
            String lineBreak = System.getProperty("line.separator");
            logWithTimestamp = lineBreak + logWithTimestamp;

            // Cria um objeto FileOutputStream para o arquivo
            FileOutputStream fos = new FileOutputStream(file, true);

            // Escreve o conteúdo do arquivo
            fos.write(logWithTimestamp.getBytes());

            // Fecha o objeto FileOutputStream
            fos.close();

            return true; // Log salvo com sucesso
        } catch (IOException e) {
            e.printStackTrace();
            return false; // Erro ao salvar o log
        }
    }

    private static String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private static String getAppName(Context context) {
        CharSequence appName = context.getPackageManager().getApplicationLabel(context.getApplicationInfo());
        return (appName != null) ? appName.toString() : "LogApp";
    }


}
