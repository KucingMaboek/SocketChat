package utils;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import controllers.ClientController;
import javafx.application.Platform;

public class TaskReadThread implements Runnable {
    //private variables
    private Socket socket;
    private ClientController client;

    //constructor
    public TaskReadThread(Socket socket, ClientController client) {
        this.socket = socket;
        this.client = client;
    }

    @Override 
    public void run() {
        //continuously loop it
        while (true) {
            try {
                //Create data input stream
                DataInputStream input = new DataInputStream(socket.getInputStream());

                //get input from the client
                String message = input.readUTF();

                //append message of the Text Area of UI (GUI Thread)
                Platform.runLater(() -> {
                    //display the message in the textarea
                    client.lv_conversation.getItems().add(message + "\n");
                });
            } catch (IOException ex) {
                System.out.println("Error reading from server: " + ex.getMessage());
                ex.printStackTrace();
                break;
            }
        }
    }
}
