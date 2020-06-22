package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import utils.ConnectionUtil;
import utils.TaskReadThread;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    @FXML
    private TextField tf_username, tf_server, tf_message;

    @FXML
    private Button btn_connect, btn_send;

    @FXML
    public ListView<String> lv_conversation;

    private String username;
    private ObjectOutputStream output = null;

    @FXML
    void connectServer(ActionEvent event) {
        String ip;
        if (tf_server.getText().isEmpty()) {
            ip = "localhost";
            tf_server.setText("127.0.0.1");
        } else {
            String[] temp = tf_server.getText().split(".");
            if (temp.length != 4) {
                ip = "localhost";
                tf_server.setText("127.0.0.1");
            } else {
                ip = tf_server.getText();
            }
        }

        if (tf_username.getText().isEmpty()) {
            username = "Guest";
            tf_username.setText("Guest");
        } else {
            username = tf_username.getText();
        }
        try {
            // Membuat socket untuk terkoneksi ke server
            Socket socket = new Socket(ip, ConnectionUtil.port);

            // Kalau Koneksi berhasil
            btn_connect.setDisable(true);
            tf_username.setEditable(false);
            tf_server.setEditable(false);
            tf_message.setEditable(true);
            btn_send.setDisable(false);


            // Membuat outputstream untuk mengirim pesan
            output = new ObjectOutputStream(socket.getOutputStream());

            // Membuat Thread untuk membaca pesan dari server secara berkala
            TaskReadThread task = new TaskReadThread(socket, this);
            Thread thread = new Thread(task);
            thread.start();

            try {
                output.writeObject(username);
                output.flush();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                lv_conversation.getItems().add("Connected");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @FXML
    void sendMessage(ActionEvent event) {

        String message = tf_message.getText();
        try {
            // Mengirim pesan ke server
            output.writeObject(username + ": " + message);
            output.flush();

            // Setelah mengirim pesan, kolom input pesan akan dibersihkan
            tf_message.clear();
        } catch (IOException e) {
            System.err.println(e.getMessage());

        }
    }

    public void initialize(URL location, ResourceBundle resources) {
        tf_message.setEditable(false);
        btn_send.setDisable(true);
    }
}
