package com.samj.backend;

import java.io.*;
import java.net.Socket;
public class RequestSimulator {
    private static final String DEFAULT_CALLED_NUMBER = "123456";
    private static final String SERVER_ADDRESS = "localhost"; // or the actual server IP
    private static final int SERVER_PORT = 8080;

    public static void forwardingcheck(String argcallednumber) throws IOException {
        // Use the default phone number or the one provided as an argument
        String calledNumber = argcallednumber == null ? DEFAULT_CALLED_NUMBER : argcallednumber;

        // Open a socket connection to the server
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send an HTTP GET request to the server
            String request = String.format("GET /forwardingcheck?argcallednumber=%s HTTP/1.1\r\nHost: %s\r\n\r\n",
                    calledNumber, SERVER_ADDRESS);
            out.write(request);
            out.flush();

            // Read and print the response from the server
            String responseLine;
            while ((responseLine = in.readLine()) != null) {
                System.out.println(responseLine);
            }
        } catch (IOException e) {
            e.printStackTrace(); // Log or handle exceptions appropriately
        }
    }
}
