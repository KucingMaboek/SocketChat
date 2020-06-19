package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import utils.ConnectionUtil;
import utils.TaskReadThread;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    private ObservableList<String> roleOption =
            FXCollections.observableArrayList(
                    "Server",
                    "Client"
            );
    private String roleValue;

    @FXML
    private TextField tf_username, tf_server, tf_message;

    @FXML
    private Button btn_connect;

    @FXML
    private ChoiceBox<String> cb_role;

    @FXML
    public ListView<String> lv_conversation;

    private String username;
    private DataOutputStream output = null;

    @FXML
    void connectServer(ActionEvent event) {
        String ip = tf_server.getText();
        username = tf_username.getText();
        try {
            // Create a socket to connect to the server
            Socket socket = new Socket(ip, ConnectionUtil.port);

            //Connection successful
            btn_connect.setDisable(true);
            tf_username.setEditable(false);
            tf_server.setEditable(false);

            // Create an output stream to send data to the server
            output = new DataOutputStream(socket.getOutputStream());

            //create a thread in order to read message from server continuously
            TaskReadThread task = new TaskReadThread(socket, this);
            Thread thread = new Thread(task);
            thread.start();

            try {
                output.writeUTF(username + " is joined!!");
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
            //send message to server
            output.writeUTF(username + ": " + message);
            output.flush();

            //clear the textfield
            tf_message.clear();
        } catch (IOException e) {
            System.err.println(e.getMessage());

        }
    }

    public void initialize(URL location, ResourceBundle resources) {
        cb_role.getItems().addAll(roleOption);
    }
}
