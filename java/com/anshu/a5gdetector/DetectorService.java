package com.anshu.a5gdetector;

import static java.lang.Thread.sleep;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.util.Locale;

public class DetectorService extends Service {
   Thread thread;
   boolean flag=true;
   TextToSpeech textToSpeech;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        textToSpeech=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=TextToSpeech.ERROR)
                    textToSpeech.setLanguage(Locale.ENGLISH);
            }
        });

        thread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (flag) {
                            DetectorService.this.check5G();
                            try {
                                sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            //Toast.makeText(DetectorService.this, "", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        thread.start();
        final String CHANNELID = "Foreground Service ID";
        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_LOW
        );
        Intent launchIntent=new Intent(this,MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,50,launchIntent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                .setContentText("5G to 4G switch alert is active")
                .setContentTitle("5G Detector is running")
                .setSmallIcon(R.drawable.ic_baseline_5g_24)
                .setOngoing(true)
                .setContentIntent(pendingIntent);
        NotificationManager manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(50,notification.build());

        startForeground(1001, notification.build());
        return super.onStartCommand(intent, flags, startId);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy()
    {
        flag=false;
    }
    void check5G() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        int networkType = telephonyManager.getDataNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                //Toast.makeText(this, "2G is connected", Toast.LENGTH_LONG).show();
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                //Toast.makeText(this, "3G is connected", Toast.LENGTH_SHORT).show();
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                //Toast.makeText(this, "4G is connected", Toast.LENGTH_SHORT).show();
                textToSpeech.speak("5G disconnected! You are using 4G",TextToSpeech.QUEUE_FLUSH,null,null);
                Vibrator vibrator=(Vibrator) getSystemService(VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(1000);
                }
                break;
            case TelephonyManager.NETWORK_TYPE_NR:
                //Toast.makeText(this, "5G is connected", Toast.LENGTH_SHORT).show();
                break;
            case TelephonyManager.NETWORK_TYPE_GSM:
            case TelephonyManager.NETWORK_TYPE_IWLAN:
            case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            default:
                //Toast.makeText(this, "Unknown Network", Toast.LENGTH_SHORT).show();
        }
    }

}
