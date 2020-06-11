package com.pompip.screen;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketIOHelper {

    private static class Inner{
        static SocketIOHelper socketIOHelper = new SocketIOHelper();
    }

    public static SocketIOHelper getInstance() {
        return Inner.socketIOHelper;
    }
    Socket socket;
    private SocketIOHelper(){
        IO.Options  options= new IO.Options();
        try {
            socket = IO.socket("", options);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    void connect() throws URISyntaxException {
        socket.connect();
    }
}
