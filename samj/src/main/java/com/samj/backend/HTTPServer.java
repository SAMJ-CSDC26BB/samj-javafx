package com.samj.backend;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

public class HTTPServer {

    private ServerSocket serverSocket;
    private URL serverURL;
    private String url;
    private boolean secure = false;

    public HTTPServer(int port) throws IOException {
        this.url = "localhost";
        this.serverURL = new URL("http", url, port, "/");
        this.serverSocket = new ServerSocket(port);
        System.out.println("HTTP Server Socket created with port " + port);
        listener();
    }

    public HTTPServer(String url, int port) throws IOException {
        this.url = url;
        this.serverURL = new URL("http", url, port, "/");
        this.serverSocket = new ServerSocket(port);
        System.out.println("HTTP Server Socket created with port " + port);
        listener();
    }

    public HTTPServer(String url, int port, boolean secure) throws IOException {
        this.url = url;
        if (!secure) {
            this.serverURL = new URL("http", url, port, "/");
        } else {
            this.serverURL = new URL("https", url, port, "/");

        }
        this.serverSocket = new ServerSocket(port);
        System.out.println("HTTP Server Socket created with port " + port);
        listener();
    }

    private void listener() {
        while (true) {
            try (Socket clientSocket = serverSocket.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                String requestLine = in.readLine();
                if (requestLine != null && requestLine.startsWith("GET")) {
                    requestParser(clientSocket, requestLine);
                }

            } catch (IOException e) {
                // Log the IOException, as it's related to network issues.
                e.printStackTrace();
            } catch (ReflectiveOperationException e) {
                // Log exceptions related to reflection (ClassNotFound, NoSuchMethod, InvocationTarget, IllegalAccess)
                e.printStackTrace();
            } catch (Exception e) {
                // Catch any other unexpected exceptions.
                e.printStackTrace();
            }
        }
    }


    private void sendResponse(Socket clientSocket, String response) {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            out.write("HTTP/1.1 200 OK\r\n");

            // Correctly formatted headers
            out.write("Content-Type: text/plain\r\n");
            out.write("Content-Length: " + response.getBytes("UTF-8").length + "\r\n");

            // End of headers
            out.write("\r\n");

            // Writes the response body
            out.write(response);
            out.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void requestParser(Socket clientSocket, String request) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //GET /forwardcheck/?number=0123456789 HTTP/1.1
        String withoutPrefix;
        String feature;
        String ip = clientSocket.getInetAddress().getHostAddress();
        if (request.startsWith("GET")) {
            //logik muss hier noch verbessert werden, leicht zu exploiden!
            //inkludiere check für "/" am ende!!!!
            //curl http://localhost:8000/forwardcheck/\{number\=0123456789\;timestamp\=2023\}/
            //curl http://localhost:8000/forwardcheck/number\=0123456789/
            withoutPrefix = request.split("GET /")[1];
            feature = withoutPrefix.split("/")[0];
            if (Server.listFeatures.contains(feature)) {
                Class<?> c_server = Class.forName("com.samj.backend.Server"); //hohlt sich die Klasse in eine Variable
                Method method = c_server.getMethod(feature, String.class, String.class); //hohlt sich die funktion aus der Klasse in eine Variable
                Object returnValue = method.invoke(null, withoutPrefix.split("/")[1].split("/")[0], ip); //führt funktion die wir gespeichert haben aus
                sendResponse(clientSocket, (String) returnValue);
            } else {
                System.out.println("notSupportedFeature");
                sendResponse(clientSocket, Server.notSupportedFeature);
            }


        } else {
            System.out.println("notSupportedFeature");
            sendResponse(clientSocket, Server.notSupportedFeature);
        }

    }

}