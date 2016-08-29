package com.example.fongxuan.myapplication;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class bkDiscover extends Service {
    private Handler handler = new Handler();
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)  {
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d("background discover", "discover inti Success!");
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.d("background discover", "discover inti Fail!");
            }
        });
        //handler.postDelayed(showTime, 3000);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(showTime);
        super.onDestroy();
    }

    private Runnable showTime = new Runnable() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void run() {

            /*manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    Log.d("discover success", "Success!");
                }

                @Override
                public void onFailure(int reasonCode) {
                    Log.d("discover fail", "onFailure!");
                }
            });*/
            handler.postDelayed(this, 5000);
            /*
            manager.stopPeerDiscovery(channel, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    Log.d("stop_test", "stop_success");
                }

                @Override
                public void onFailure(int reasonCode) {
                    Log.d("stop_test", "stop_fail");
                }
            });*/
            //Log.i("time:", new Date().toString());
            //handler.postDelayed(this, 3000);


        }
    };
}