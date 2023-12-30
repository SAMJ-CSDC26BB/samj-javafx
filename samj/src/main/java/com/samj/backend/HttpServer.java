package com.samj.backend;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class HttpServer {

    private ServerSocket serverSocket;


    public HttpServer(int port) {
        try{
            this.serverSocket = new ServerSocket(port);
        }
        catch (IOException e){
            // nachträglich hinzufügen errorhandling
        }


        System.out.println("HTTP Server started on port " + port);
    }
    private void listener() {
        while (true) {
            try
             {
                 Socket clientSocket = serverSocket.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 String requestLine = in.readLine();

                 // && requestLine.startsWith("GET"))
                 if (requestLine != null){
                     System.out.println(requestLine);
                 }

        } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendResponse (Socket clientSocket){
        while (true) {
            try
            {
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String requestparser (){
        return "";
    }

}