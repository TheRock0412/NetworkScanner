package com.example.manuel_sorg.networkscanner.Network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MacAddress {

    private final static String TAG = "MacAddress";

    private final static String MAC_RE = "^%s\\s+0x1\\s+0x2\\s+([:0-9a-fA-F]+)\\s+\\*\\s+\\w+$";
    private final static int BUF = 8 * 1024;

    private static String ip;
    private String adress;

    public String getAdress() {
        return adress;
    }

    public MacAddress(String ip) {
        this.ip = ip;
        adress = "";
    }

    public void getHardwareAddress() {

        BufferedReader bufferedReader = null;

        try {
            if (ip != null) {
                String ptrn = String.format(MAC_RE, ip.replace(".", "\\."));
                Pattern pattern = Pattern.compile(ptrn);

                bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"), BUF);

                String line;
                Matcher matcher;
                while ((line = bufferedReader.readLine()) != null) {
                    matcher = pattern.matcher(line);
                    if (matcher.matches()) {
                        if (matcher.group(1).matches("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$")) {
                            adress = matcher.group(1);
                        }
                        break;
                    }
                }
            } else {
                Log.e(TAG, "ip is null");
                adress = "";
            }
        } catch (IOException e) {
            Log.e(TAG, "Can't open/read file ARP: " + e.getMessage());
            adress = "";
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }
}
