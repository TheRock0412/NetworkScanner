package com.example.manuel_sorg.networkscanner;

import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manuel_sorg.networkscanner.Network.IpAddress;
import com.example.manuel_sorg.networkscanner.Network.NetInfo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    Database mDatabaseHelper;
    IpAddress ipAddress;

    private Button btn_Scan;
    private ListView ListView_IP;
    private TextView tv_MeineIP;

    CustomAdapter adapter;
    ArrayList<Geraet> geraetArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getActionBar().show();

        btn_Scan = (Button) findViewById(R.id.btnScan);
        ListView_IP = (ListView) findViewById(R.id.lvNetworkDevice);
        tv_MeineIP = (TextView) findViewById(R.id.tvMeineIP);

        mDatabaseHelper = new Database(this);
        ipAddress = new IpAddress(this);
        NetInfo netinfo = new NetInfo(this);

        adapter = new CustomAdapter(geraetArrayList, getApplicationContext());

        tv_MeineIP.setText("IP:        " + ipAddress.getMobileIP() + "\n" + "WLAN: " + netinfo.getWifiinfo());

        btn_Scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ipAddress.startScanIP();
                checkStatus();
            }
        });

        ListView_IP.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, Device.class);
                intent.putExtra("device", geraetArrayList.get(i));
                startActivity(intent);
            }
        });

        ListView_IP.setAdapter(adapter);
    }

    private void checkStatus() {
        final Handler handler = new Handler();
        final int delay = 2000;
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (ipAddress.getScanning()) {
                    Log.i("SCANNING", "is scanning");
                    toastMessage("scanning ...");

                    Cursor data = mDatabaseHelper.getData();

                    while (data.moveToNext()) {
                        Geraet g = new Geraet(data.getString(4), data.getString(2), data.getString(1), data.getString(3));
                        boolean isAlreadyAdded = false;
                        for (Geraet geraet : geraetArrayList) {
                            if (geraet.getIpAddress().equals(g.getIpAddress())) {
                                isAlreadyAdded = true;
                            }
                        }
                        if (!isAlreadyAdded) {
                            geraetArrayList.add(g);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    handler.postDelayed(this, delay);
                } else {
                    handler.removeCallbacks(this);
                }
            }
        };

        handler.postDelayed(runnable, delay);
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
