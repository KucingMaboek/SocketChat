package models;

import java.net.Socket;

public class ClientModel {
    private Socket socket;
    private String username;

    public ClientModel(Socket socket, String username) {
        this.socket = socket;
        this.username = username;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
