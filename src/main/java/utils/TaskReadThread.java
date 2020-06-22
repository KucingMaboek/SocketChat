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
        // Melakukan looping terus menerus
        try {
            // Membuat input stream
            input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error reading from server: " + e.getMessage());
            System.err.println(e.getMessage());
        }
        while (true) {
            try {
                // Mendapatkan inputan dari user
                String message = null;
                try {
                    message = (String) input.readObject();
                } catch (ClassNotFoundException e) {
                    System.err.println(e.getMessage());
                }

                // Menampilkan pesan dari klien ke UI
                String finalMessage = message;
                Platform.runLater(() -> {
                    // Pesan akan ditampilkan di list view
                    client.lv_conversation.getItems().add(finalMessage + "\n");
                });
            } catch (IOException e) {
                System.out.println("Error reading from server: " + e.getMessage());
                System.err.println(e.getMessage());
                break;
            }
        }
    }
}
