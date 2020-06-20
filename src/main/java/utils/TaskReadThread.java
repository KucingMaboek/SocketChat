package utils;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.net.Socket;

import controllers.ClientController;
import javafx.application.Platform;

public class TaskReadThread implements Runnable {
    //private variables
    private Socket socket;
    private ClientController client;
    private ObjectInputStream input;

    //constructor
    public TaskReadThread(Socket socket, ClientController client) {
        this.socket = socket;
        this.client = client;
    }

    @Override
    public void run() {
        //continuously loop it
        try {
            //Create data input stream
            input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error reading from server: " + e.getMessage());
            e.printStackTrace();
        }
        while (true) {
            try {
                //get input from the client
                String message = null;
                try {
                    message = (String) input.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                //append message of the Text Area of UI (GUI Thread)
                String finalMessage = message;
                Platform.runLater(() -> {
                    //display the message in the textarea
                    client.lv_conversation.getItems().add(finalMessage + "\n");
                });
            } catch (IOException e) {
                System.out.println("Error reading from server: " + e.getMessage());
                e.printStackTrace();
                break;
            }
        }
    }
}
