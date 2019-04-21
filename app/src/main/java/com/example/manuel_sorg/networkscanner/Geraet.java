package com.example.manuel_sorg.networkscanner;

import java.io.Serializable;

public class Geraet implements Serializable {

    private final static String TAG = "Geraet";

    private String hostname;
    private String ipAddress;
    private String bezeichnung;
    private String macAddress;

    public Geraet(String hostname, String ipAddress, String bezeichnung, String macAddress) {
        this.hostname = hostname;
        this.ipAddress = ipAddress.replace("/", "");
        this.bezeichnung = bezeichnung;
        this.macAddress = macAddress;
    }

    public Geraet() { }

    public String getHostname() { return hostname; }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIpAddress() { return ipAddress; }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}
