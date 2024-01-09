package com.samj.backend;

import com.samj.backend.Database;
import com.samj.backend.HttpServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// http://192.168.92.8:82/echo.php?called=123456
public class Server
{
    public static List<String> list_features = Arrays.asList("forwardcheck");
    private Database db;
    private HttpServer webServer;

    public Server(){
        db = new Database();
        webServer = new HttpServer(8000);
    }

    /**
     *
     * @param CalledNumber number incoming from tel
     * @return maybe throws Exception in future?
     */
    static public String forwardcheck(String CalledNumber) {
        System.out.println(CalledNumber);
        String ForwardedNummer = "";

        //if(checkSyntaxNumber(CalledNumber) &&
        //checkCalledNumberExists(CalledNumber) &&
        //isForwardingActive(CalledNumber)){
        //    ForwardedNummer = getForwardedNumber(CalledNumber);
        //    return ForwardedNummer;
        //}
        return "ERROR in logic!";
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
