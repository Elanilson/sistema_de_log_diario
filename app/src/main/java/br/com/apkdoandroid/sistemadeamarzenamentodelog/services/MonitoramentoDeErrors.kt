package br.com.apkdoandroid.sistemadeamarzenamentodelog.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import br.com.apkdoandroid.sistemadeamarzenamentodelog.utils.LogUtils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader

class MonitoramentoDeErrors : Service() {
    private val logcatScope = CoroutineScope(Dispatchers.IO)
    private lateinit var context: Context
    private val logcatHandler = Handler(Looper.getMainLooper())
    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "MonitoramentoChannel"
    override fun onCreate() {
        super.onCreate()
        Log.i("my_service", "onCreate: ")
       // createNotificationChannel()
       // startForeground(NOTIFICATION_ID, createNotification())

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("my_service", "onStartCommand: ")
        context = getApplicationContext();
       // LogUtils.saveLogWithPermissionCheck(context, "onStartCommand: ")
        //startLogcatListener()
        startLogcatMonitoring()
        return super.onStartCommand(intent, flags, startId)
       // return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("my_service", "onDestroy")
    }

    private fun startLogcatMonitoring() {
        logcatScope.launch {
            try {
                val process = Runtime.getRuntime().exec("logcat *:S *:V *:E")
                val bufferedReader = process.inputStream.bufferedReader()

                while (true) {
                    val line = bufferedReader.readLine()
                    //if (line != null && line.contains("E/")) {
                    if (line != null) {
                        saveErrorLog(line)
                    }
                    Log.i("my_service", "Estou lendo log ")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun startLogcatListener() {
        val process = Runtime.getRuntime().exec("logcat *:S *:V *:E  ")

        // Configura um BufferedReader para ler as mensagens do Logcat
        val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))

        // Inicia uma thread para ler e exibir mensagens do Logcat no TextView
        Thread {
            var line: String?
            while (true) {
                line = bufferedReader.readLine()
                if (line != null) {
                    // Atualiza o TextView na thread principal
                   // logcatHandler.post { saveErrorLog(line) }

                    Log.i("my_service", "Estou lendo log "+line.length)
                }

            }
        }.start()
    }

    private fun saveErrorLog(log: String) {
        LogUtils.saveLogWithPermissionCheck(context, log)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Monitoramento Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
           // .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Monitoramento de Erros")
            .setContentText("O serviço está em execução.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        return builder.build()
    }


}