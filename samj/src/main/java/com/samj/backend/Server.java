package com.samj.backend;

import com.samj.shared.CallForwardingDTO;
import com.samj.shared.DatabaseAPI;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

// http://192.168.92.8:82/echo.php?called=123456
public class Server {
    static final String noEntryInTimeBasedForwardingTable = "4367763106443";
    static final String notSupportedFeature = "436642864230";
    public static List<String> listFeatures = List.of("timeBasedForwarding");

    private final int port;
    private HTTPServer webServer;

    private Set<CallForwardingDTO> timeBasedForwardingSet;

    public Server(int port) {
        this.port = port;
        updateTimeBasedForwardingSet();
    }

    public void start() throws IOException {
        webServer = new HTTPServer(this.port);
    }

    /**
     * @param args should be incoming number from tel
     *             could be invalid args -> if so send FailureCode
     * @return destinationNumber or failureCode
     */
    public static String timeBasedForwarding(String args, String ip) {
        System.out.println("request ip: " + ip + " with args: " + args);
        String splitArgs = args.split("number=")[1].split(" HTTP")[0];
        System.out.println("splitArgs: " + splitArgs);
        if(checkSyntaxNumber(splitArgs))
            return timeBasedForwardingApiCall(splitArgs);

        System.out.println("noEntryInTimeBasedForwardingTable");
        return noEntryInTimeBasedForwardingTable;
    }

    private static String timeBasedForwardingApiCall(String calledNumber){
        Set<CallForwardingDTO> forwardingSet = DatabaseAPI.loadCallForwardingRecordByCalledNumber(calledNumber);

        if (! checkCalledNumberExists(forwardingSet)){
            return noEntryInTimeBasedForwardingTable;
        }

        String destNumber = getCurrentForwardEntry(forwardingSet);
        return Objects.requireNonNullElse(destNumber, noEntryInTimeBasedForwardingTable);
    }

    private static boolean checkCalledNumberExists(Set<CallForwardingDTO> forwardingSet) {
        return !forwardingSet.isEmpty();
    }

    private static String getCurrentForwardEntry(Set<CallForwardingDTO> forwardingSet) {
        for(CallForwardingDTO DTO : forwardingSet){
            LocalDateTime timeStampNow = LocalDateTime.now();

            //checks if a entry exists with current time interval
            //now >= Startdatum && now <= enddatum
            if(timeStampNow.isAfter(DTO.getBeginTime()) && timeStampNow.isBefore(DTO.getEndTime())){
                String user = "User: " + DTO.getDestinationUsername() + "\nDest: " + DTO.getDestinationNumber();
                String timestamps = "\n" + DTO.getBeginTime() + " -> " + DTO.getEndTime();
                System.out.println(user+timestamps);
                return DTO.getDestinationNumber();
            }
        }
        return null;
    }

    /**
     * At the moment forwards to regex check function!
     * This function will be updated in the future.
     *
     * https://repo1.maven.org/maven2/com/googlecode/libphonenumber/libphonenumber/8.12.56/
     * com/googlecode/libphonenumber/libphonenumber/8.12.56
     * Features that are possible:
     * 1) find out location code (AT, DE,...)
     * 2) syntax checking
     * @param splitArgs
     * @return true/false
     */
    private static boolean checkSyntaxNumber(String splitArgs) {
        return _regexValidNumberCheck(splitArgs);
    }
    private static boolean _regexValidNumberCheck(String telNumber){
        return telNumber.matches("\\+?\\d+");
    }

    public Set<CallForwardingDTO> getTimeBasedForwardingSet() {
        return timeBasedForwardingSet;
    }
    public void updateTimeBasedForwardingSet() {
        timeBasedForwardingSet = DatabaseAPI.loadCallForwardingRecords();
    }
}