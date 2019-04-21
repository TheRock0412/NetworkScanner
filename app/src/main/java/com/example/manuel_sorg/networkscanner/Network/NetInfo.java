package com.example.manuel_sorg.networkscanner.Network;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import static android.content.Context.WIFI_SERVICE;

public class NetInfo {

    private static final String TAG = "NetInfo";

    Context context;
    WifiManager wifimanager;

    String WifiName;

    public String getWifiMac() {
        return WifiMac;
    }

    String WifiMac;

    public NetInfo(Context context) {
        this.context = context;
    }

    public String getWifiinfo() {

        wifimanager = (WifiManager) context.getSystemService(WIFI_SERVICE);

        if (wifimanager.isWifiEnabled()) {

            WifiInfo wifiinfo = wifimanager.getConnectionInfo();

            if (String.valueOf(wifiinfo.getSupplicantState()).equals("COMPLETED")) {

                WifiName = wifiinfo.getSSID();
                WifiMac = wifiinfo.getMacAddress();

            } else {
                System.out.print("Geht nicht");
            }
        } else {
            //wifimanager.setWifiEnabled(true);
        }
        return WifiName;
    }
}
