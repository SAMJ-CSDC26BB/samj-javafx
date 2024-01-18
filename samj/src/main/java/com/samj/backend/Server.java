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
    static public String timeBasedForwarding(String args) {
        System.out.println(args);

        return apiCall(args.split("number=")[1]);
    }
    static private String apiCall(String calledNumber){
        Set<CallForwardingDTO> forwardingSet = DatabaseAPI.loadCallForwardingRecordByCalledNumber(calledNumber);
        if(checkCalledNumberExists(forwardingSet) && isForwardingActive(forwardingSet))
            return "number!";
        return "Error in Logic of Application!";
    }

    static private boolean checkCalledNumberExists(Set<CallForwardingDTO> forwardingSet) {
        System.out.print(!forwardingSet.isEmpty());
        return forwardingSet.isEmpty();
    }

    static private boolean isForwardingActive(Set<CallForwardingDTO> forwardingSet) {
        for(CallForwardingDTO DTO : forwardingSet){
            System.out.println(DTO.getDestinationUsername());
        }
        System.out.print("isForwardingActive?!");
        return false;
    }

    private String getForwardedNumber(String calledNumber) {
        return "";
    }
    /**
     * This function will be implemented in the future.
     *
     * https://repo1.maven.org/maven2/com/googlecode/libphonenumber/libphonenumber/8.12.56/
     * com/googlecode/libphonenumber/libphonenumber/8.12.56
     * Features that are possible:
     * 1) find out location code (AT, DE,...)
     * 2) syntax checking
     * @param calledNumber
     * @return true/false
     */
    private boolean checkSyntaxNumber(String calledNumber) {
        return false;
    }




    public Set<CallForwardingDTO> getTimeBasedForwardingSet() {
        return timeBasedForwardingSet;
    }
    public void updateTimeBasedForwardingSet() {
        timeBasedForwardingSet = DatabaseAPI.loadCallForwardingRecords();
    }
}