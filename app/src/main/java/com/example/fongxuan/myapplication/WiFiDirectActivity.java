package com.example.fongxuan.myapplication;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class WiFiDirectActivity extends AppCompatActivity implements
                                                     WifiP2pManager.ChannelListener,
                                                     DeviceListFragment.DeviceActionListener{
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;


    public static final String TAG = "wifi direct";
    private WifiP2pManager wifiP2pManager;
    private WifiManager wifiManager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;

    private DeviceDetailFragment detailFragment;
    private DeviceListFragment listFragment;

    private GoogleApiClient client;
    private User user;

    private Button edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);

        wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(this, getMainLooper(), null);
//        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);


        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        receiver = new WiFiDirectBroadcastReceiver(wifiP2pManager, channel, this);
        Intent intent = new Intent(WiFiDirectActivity.this, bkDiscover.class);
        startService(intent);


        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        myToolbar.inflateMenu(R.menu.menu_main);
        // 打開 up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerList = (LinearLayout) findViewById(R.id.drawer_view);

        // 實作 drawer toggle 並放入 toolbar
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        listFragment = (DeviceListFragment) getFragmentManager()
                .findFragmentById(R.id.frag_list);

        Button edit = (Button)findViewById(R.id.drawer_btn_profileEdit);
        edit.setOnClickListener(new DrawerItemClickListener());


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_wifi_switch:
                // User chose the "Settings" item, show the app settings UI...
                if(wifiManager.isWifiEnabled()){
                    wifiManager.setWifiEnabled(false);
                    setIsWifiP2pEnabled(false);
                    item.setIcon(R.drawable.ic_wifi_off);
                }
                else {
                    wifiManager.setWifiEnabled(true);
                    setIsWifiP2pEnabled(true);
                    item.setIcon(R.drawable.ic_wifi_on);
                }
                break;
            case R.id.toolbar_discover:
                peerDiscover();
                break;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean peerDiscover(){
        if (!isWifiP2pEnabled) {
            Toast.makeText(WiFiDirectActivity.this, R.string.p2p_off_warning,
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        listFragment.onInitiateDiscovery();
        Toast.makeText(WiFiDirectActivity.this,"discovering",
                Toast.LENGTH_SHORT).show();
        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(WiFiDirectActivity.this, "Discovery Initiated",
                        Toast.LENGTH_SHORT).show();
                Log.d("discover success", "this is in the onSuccess!");
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(WiFiDirectActivity.this, "Discovery Failed : " + reasonCode,
                        Toast.LENGTH_SHORT).show();
                Log.d("discover fail", "this is in the onFailure!");
            }
        });
        return true;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "WiFiDirect Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.example.ander.apptest/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
//    }
    /**
     * register the BroadcastReceiver with the intent values to be matched
     */
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }


    @Override
    public void showDetails(WifiP2pDevice device) {

        //Detail later
//        DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
//                .findFragmentById(R.id.frag_detail);
//        fragment.showDetails(device);

    } //DeviceListFragment callback

    @Override
    public void connect(WifiP2pConfig config) {
        wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(WiFiDirectActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void disconnect() {
        //Detail later
//        final DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
//                .findFragmentById(R.id.frag_detail);
//        fragment.resetViews();
//        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
//
//            @Override
//            public void onFailure(int reasonCode) {
//                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
//
//            }
//
//            @Override
//            public void onSuccess() {
//                fragment.getView().setVisibility(View.GONE);
//            }
//
//        });
    }

    @Override
    public void onChannelDisconnected() {
        // we will try once more
        if (wifiP2pManager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            resetData();
            retryChannel = true;
            wifiP2pManager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void cancelDisconnect() {

        /*
         * A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
        if (wifiP2pManager != null) {
            if (listFragment.getDevice() == null
                    || listFragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                disconnect();
            } else if (listFragment.getDevice().status == WifiP2pDevice.AVAILABLE
                    || listFragment.getDevice().status == WifiP2pDevice.INVITED) {

                wifiP2pManager.cancelConnect(channel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(WiFiDirectActivity.this, "Aborting connection",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(WiFiDirectActivity.this,
                                "Connect abort request failed. Reason Code: " + reasonCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }



//    @Override
//    public void onStop() {
//        super.onStop();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "WiFiDirect Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.example.ander.apptest/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        client.disconnect();
//    }
    public void resetData() {
        //Detail later
//        DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager()
//                .findFragmentById(R.id.frag_detail);
        if (listFragment != null) {
            listFragment.clearPeers();
        }
//        if (fragmentDetails != null) {
//            fragmentDetails.resetViews();
//        }
    }

    public void updateUser(WifiP2pDevice wifiP2pDevice){
        user.setUserName(wifiP2pDevice.deviceName);
        user.setUserStatus(DeviceListFragment.getDeviceStatus(wifiP2pDevice.status));
        TextView drawer_userName = (TextView)findViewById(R.id.userName);
        TextView drawer_userStatus = (TextView)findViewById(R.id.userStatus);

        drawer_userName.setText(getResources().getString(R.string.drawer_userName)
                +user.getUserName());
        drawer_userStatus.setText(getResources().getString(R.string.drawer_userStatus)
                +user.getUserStatus());
    }
    public void updateUser(int iconNo){
        switch(iconNo){
            case 1:
        }
    }


    private class DrawerItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Log.d("onClick", "in~~~~~~~~~~~~~~");
            if(view.getId() == R.id.drawer_btn_profileEdit){
                Log.d("onClick", "hihi~~~~~");
                new EditProfileDialog();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                // Create and show the dialog.
                EditProfileDialog newFragment = new EditProfileDialog();
                newFragment.show(getFragmentManager(), "dialog");


            }
        }
    }
//    @Override
//    public void onClick(View view) {
//        if(view.getId() == R.id.drawer_btn_profileEdit){
//            new AlertDialog.Builder(this)
//                    .setTitle("User Info")
//                    .setMessage("")
//                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Toast.makeText(getApplicationContext(), "GOGO", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .setNegativeButton("Wait a minute", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Toast.makeText(getApplicationContext(), "Im hungry", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .show();
//        }
//    }
}
