package com.anshu.a5gdetector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button check,startAlertService,stopAlertService;
    private static final int READ_PHONE_STATE_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        check = findViewById(R.id.button);
        checkPermission();
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check5G();
            }
        });
        startAlertService=findViewById(R.id.start);
        startAlertService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Alert started",Toast.LENGTH_SHORT).show();
                startApp();
            }
        });
        stopAlertService=findViewById(R.id.stop);
        stopAlertService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Alert stopped",Toast.LENGTH_SHORT).show();
                stopService(new Intent(MainActivity.this,DetectorService.class));
            }
        });


    }
    public boolean foregroundServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if(DetectorService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    private void startApp() {
        if(!foregroundServiceRunning())
            startForegroundService(new Intent(MainActivity.this,DetectorService.class));
    }

    public void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_PHONE_STATE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Phone Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Phone Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void check5G() {
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
                Toast.makeText(this, "2G is connected", Toast.LENGTH_LONG).show();
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
                Toast.makeText(this, "3G is connected", Toast.LENGTH_LONG).show();
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                Toast.makeText(this, "4G is connected", Toast.LENGTH_LONG).show();
                break;
            case TelephonyManager.NETWORK_TYPE_NR:
                Toast.makeText(this, "5G is connected", Toast.LENGTH_LONG).show();
                break;
            case TelephonyManager.NETWORK_TYPE_GSM:
            case TelephonyManager.NETWORK_TYPE_IWLAN:
            case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            default:
                Toast.makeText(this, "Unknown Network", Toast.LENGTH_LONG).show();
        }
    }
}