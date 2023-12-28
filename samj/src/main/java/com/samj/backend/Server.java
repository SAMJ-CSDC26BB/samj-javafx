package com.samj.backend;

import com.samj.backend.Database;
import com.samj.backend.HttpServer;


public class Server
{
    private Database db;
    private HttpServer webServer;

    Server(){
        db = new Database();
        webServer = new HttpServer();
    }

    /**
     *
     * @param CalledNumber number incoming from tel
     * @return maybe throws Exception in future?
     */
    private String checkIncomingNumber(String CalledNumber) {
        String ForwardedNummer = "";

        if(checkSyntaxNumber(CalledNumber) &&
        checkCalledNumberExists(CalledNumber) &&
        isForwardingActive(CalledNumber)){
            ForwardedNummer = getForwardedNumber(CalledNumber);
            return ForwardedNummer;
        }
        return "";
    }

    private boolean checkSyntaxNumber(String CalledNumber) {
        return false;
    }

    private boolean checkCalledNumberExists(String CalledNumber) {
       return false;
    }

    private boolean isForwardingActive(String CalledNumber){
        return false;
    }

    private String getForwardedNumber(String CalledNumber){
        return "";
    }
}
