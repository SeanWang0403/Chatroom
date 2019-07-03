package com.sean.chatroom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetBroadcastReceive extends BroadcastReceiver {
    private NetConnectedListener netConnectedListener;

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
            //WIFI和行動網路均未連線
            netConnectedListener.netContent(false);

        } else {
            //WIFI連線或者行動網路連線
            netConnectedListener.netContent(true);
        }

    }

    public void setNetConnectedListener(NetConnectedListener netConnectedListener) {
        this.netConnectedListener = netConnectedListener;
    }

    public interface NetConnectedListener {
        void netContent(boolean isConnected);
    }
}
