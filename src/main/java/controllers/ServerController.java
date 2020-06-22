package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import utils.ConnectionUtil;
import utils.TaskClientConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class ServerController implements Initializable {

    @FXML
    public ListView<String> lv_conversation;

    @FXML
    public ListView<String> lv_client;


    private List<TaskClientConnection> connectionList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new Thread(() -> {
            try {
                // Membuat ServerSocket
                ServerSocket serverSocket = new ServerSocket(ConnectionUtil.port);

                //append message of the Text Area of UI (GUI Thread)
                Platform.runLater(()
                        -> lv_conversation.getItems().add("New server started at " + new Date() + '\n'));

                // looping
                while (true) {
                    // Mengecek permintaan koneksi, dan menambah ke daftar koneksi
                    Socket socket = serverSocket.accept();
                    TaskClientConnection connection = new TaskClientConnection(socket, this);
                    connectionList.add(connection);

                    //Membuat thread baru
                    Thread thread = new Thread(connection);
                    thread.start();

                }
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }).start();
    }

    // mengirim pesan ke seluruh klien yang ada dalam list
    public void broadcast(String message) {
        for (TaskClientConnection clientConnection : this.connectionList) {
            clientConnection.sendMessage(message);
        }
    }

    public void addNewClient(String username) {
        String messages = username + " is joined!!";
        broadcast(messages);
        lv_conversation.getItems().add(messages);
        lv_client.getItems().add(username);
    }
}
