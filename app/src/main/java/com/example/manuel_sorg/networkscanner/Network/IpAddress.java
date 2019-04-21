package com.example.manuel_sorg.networkscanner.Network;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import com.example.manuel_sorg.networkscanner.Database;
import com.example.manuel_sorg.networkscanner.Geraet;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.WIFI_SERVICE;

public class IpAddress {

    private static final String TAG = "IpAddress";

    WifiManager wifiManager;
    Context context;

    Database mDatabaseHelper;

    String MeineIP;
    String hostname;
    private boolean isScanning;

    public boolean getScanning() {
        return isScanning;
    }

    public void setScanning(boolean scanning) {
        isScanning = scanning;
    }

    public IpAddress(Context context) {
        this.context = context;
        mDatabaseHelper = new Database(context);
        setScanning(false);
    }

    public String getMobileIP() {

        wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);

        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            MeineIP = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        } else {
            Log.d(TAG, "IP konnte nicht gelesen werden.");
        }
        return MeineIP;
    }

    public void startScanIP() {
        new ScanIpTask().execute();
    }

    private class ScanIpTask extends AsyncTask<Void, String, Void> {

        String[] splitIP = MeineIP.split("\\.");

        final String subnet = splitIP[0] + "." + splitIP[1] + "." + splitIP[2] + ".";
        static final int lower = 1;
        static final int upper = 255;
        static final int timeout = 800;

        @Override
        protected void onPreExecute() {
            mDatabaseHelper.deleteDB();
            Toast.makeText(context, "Scan IP...", Toast.LENGTH_LONG).show();
            setScanning(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = lower; i <= upper; i++) {
                String host = subnet + i;

                if (host.equals("192.168.178.33"))
                {
                    String test="";
                }

                String responseString = "FAILED";

                try {

                    InetAddress inetAddress = InetAddress.getByName(host);
                    String name = InetAddress.getByName(host).getHostName();

                    if (name.toString().equals("fritz.box")) {
                        hostname = "Fritz Box";
                    }else{
                        String[] splithostname = name.split("[.]");
                        hostname = splithostname[0];
                    }

                    if (inetAddress.isReachable(timeout)) {
                        MacAddress macAddress = new MacAddress(inetAddress.toString().replace("/", ""));
                        macAddress.getHardwareAddress();

                        SystemClock.sleep(2000);

                        if (!macAddress.getAdress().isEmpty()) {
                            URL url = new URL("https://api.macvendors.com/" + macAddress.getAdress());
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {

                                StringBuffer sb = new StringBuffer();
                                InputStream is = null;
                                try {
                                    is = new BufferedInputStream(conn.getInputStream());
                                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                                    String inputLine = "";
                                    while ((inputLine = br.readLine()) != null) {
                                        sb.append(inputLine);
                                    }
                                    responseString = sb.toString();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    if (is != null) {
                                        try {
                                            is.close();
                                        } catch (IOException e) {

                                        }
                                    }
                                }
                            }
                        }
                        publishProgress(hostname.toString(), inetAddress.toString(), responseString, macAddress.getAdress());
                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Geraet g = new Geraet(values[0], values[1], values[2], values[3]);
            if (!g.getIpAddress().equals(MeineIP)) {
                if (!mDatabaseHelper.addData(g)) {
                    //error
                }
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(context, "Done", Toast.LENGTH_LONG).show();
            setScanning(false);
        }
    }
}
