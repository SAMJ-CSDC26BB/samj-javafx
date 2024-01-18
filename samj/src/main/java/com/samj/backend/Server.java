package com.samj.backend;

import com.samj.shared.CallForwardingDTO;
import com.samj.shared.DatabaseAPI;

import java.io.IOException;
import java.util.List;
import java.util.Set;

// http://192.168.92.8:82/echo.php?called=123456
public class Server {
    public static List<String> listFeatures = List.of("timeBasedForwarding");

    private final int port;
    private HttpServer webServer;

    private Set<CallForwardingDTO> timeBasedForwardingSet;

    public Server(int port) {
        this.port = port;
        updateTimeBasedForwardingSet();
    }

    public void start() throws IOException {
        webServer = new HttpServer(this.port);
    }

    /**
     * @param calledNumber number incoming from tel
     * @return maybe throws Exception in future?
     */
    static public String timeBasedForwarding(String calledNumber) {
        System.out.println(calledNumber);
        String ForwardedNummer = "";

        //if(checkSyntaxNumber(CalledNumber) &&
        //checkCalledNumberExists(CalledNumber) &&
        //isForwardingActive(CalledNumber)){
        //    ForwardedNummer = getForwardedNumber(CalledNumber);
        //    return ForwardedNummer;
        //}
        return "ERROR in logic!";
    }

    private boolean checkSyntaxNumber(String calledNumber) {
        return false;
    }

    private boolean checkCalledNumberExists(String calledNumber) {
        return false;
    }

    private boolean isForwardingActive(String calledNumber) {
        return false;
    }

    private String getForwardedNumber(String calledNumber) {
        return "";
    }


    public Set<CallForwardingDTO> getTimeBasedForwardingSet() {
        return timeBasedForwardingSet;
    }
    public void updateTimeBasedForwardingSet() {
        timeBasedForwardingSet = DatabaseAPI.loadCallForwardingRecords();
    }
}