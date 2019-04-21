package com.example.manuel_sorg.networkscanner;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manuel_sorg.networkscanner.Network.Ports;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Device extends AppCompatActivity {

    private static final String TAG = "Device";

    private TextView tv_ip;
    private TextView tv_mac;
    private TextView tv_bez;
    private Button btn_port;
    private ListView ListView_PortScan;

    public static final List<Future<Boolean>> futures = new ArrayList<>();

    ArrayList<String> portlist;
    ArrayAdapter<String> portadapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device);

        tv_ip = (TextView) findViewById(R.id.tv_ip);
        tv_mac = (TextView) findViewById(R.id.tv_mac);
        tv_bez = (TextView) findViewById(R.id.tv_bez);
        btn_port = (Button) findViewById(R.id.btn_portscan);
        ListView_PortScan = (ListView) findViewById(R.id.ListView_Ports);

        portlist = new ArrayList<>();
        portadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, portlist);
        ListView_PortScan.setAdapter(portadapter);

        Geraet g = (Geraet) getIntent().getSerializableExtra("device");
        final String host = g.getHostname();
        final String ip = g.getIpAddress();
        final String mac = g.getMacAddress();
        final String bez = g.getBezeichnung();

        this.setTitle(host);
        tv_ip.setText("IP: " + ip);
        tv_mac.setText("MAC Adresse: " + mac);
        tv_bez.setText(bez);


        btn_port.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new ScanPortTask(ip.replace("/", "")).execute();
            }
        });
    }

    private class ScanPortTask extends AsyncTask<Void, String, Void> {

        private String ip;

        public ScanPortTask(String host) {
            ip = host;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            final ExecutorService es = Executors.newFixedThreadPool(20);
            final int timeout = 200;
            for (int port = 1; port <= 1023; port++) { //65535
                futures.add(portIsOpen(es, ip, port, timeout));
            }
            es.shutdown();

            return null;
        }

        public Future<Boolean> portIsOpen(final ExecutorService es, final String ip, final int port, final int timeout) {
            return es.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    try {
                        Socket socket = new Socket();
                        socket.connect(new InetSocketAddress(ip, port), timeout);

                        publishProgress(Integer.toString(port));
                        socket.close();
                        return true;
                    } catch (Exception ex) {
                        return false;
                    }
                }
            });
        }

        @Override
        protected void onPreExecute() {
            portlist.clear();
            portadapter.notifyDataSetInvalidated();
            Toast.makeText(Device.this, "Scan Ports...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            int port = Integer.parseInt(values[0]);
            Ports.Port p = Ports.Port.getForPort(port);
            if (p != null) {
                portlist.add(values[0] + "   -->   " + p.name());
            } else {
                portlist.add(values[0]);
            }
            portadapter.notifyDataSetInvalidated();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(Device.this, "Done", Toast.LENGTH_LONG).show();
        }
    }
}
