package com.samj.backend;

import com.samj.backend.Database;
import com.samj.backend.HttpServer;

public class Server
{
    Server()
    {
        conn_database();
        createHttpServer();
    }

    Database conn_database()
    {

    }

    HttpServer createHttpServer()
    {

    }

    void doing(String NummerVonTelefon)
    {
        data = database.getCurrentData();
        this.doesCalledNumberExist(data, NummerVonTelefon);
            this.isForwardingActive(data, NummerVonTelefon);
                ForwardedNummer = getForwardedNumber(NummerVonTelefon);
    }

}
