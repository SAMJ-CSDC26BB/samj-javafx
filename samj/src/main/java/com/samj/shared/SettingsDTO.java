package com.samj.shared;

public class SettingsDTO {

    private String name;
    private String serverURL;
    private String serverPort;
    private String db;

    public SettingsDTO(String name, String serverURL, String serverPort, String db) {
        this.name = name;
        this.serverURL = serverURL;
        this.serverPort = serverPort;
        this.db = db;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }
}