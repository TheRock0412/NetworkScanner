package com.example.manuel_sorg.networkscanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<Geraet> implements View.OnClickListener {

    private final static String TAG = "CustomAdapter";

    private ArrayList<Geraet> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtHost;
        TextView txtIP;
        TextView txtName;
        TextView txtMac;
    }

    public CustomAdapter(ArrayList<Geraet> data, Context context) {
        super(context, R.layout.list_item, data);
        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object = getItem(position);
        Geraet dataModel = (Geraet) object;
    }

    private int lastPosition = -1;

    @SuppressLint("CutPasteId")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Geraet geraet = getItem(position);

        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item, parent, false);

            viewHolder.txtHost = (TextView) convertView.findViewById(R.id.text2);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.text3);
            viewHolder.txtIP = (TextView) convertView.findViewById(R.id.text1);
            viewHolder.txtMac = (TextView) convertView.findViewById(R.id.text4);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        lastPosition = position;

        viewHolder.txtHost.setText(geraet.getHostname());
        viewHolder.txtName.setText(geraet.getBezeichnung());
        viewHolder.txtIP.setText(geraet.getIpAddress());
        viewHolder.txtMac.setText(geraet.getMacAddress());

        return convertView;
    }
}
